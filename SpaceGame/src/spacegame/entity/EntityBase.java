package spacegame.entity;

import java.util.*;

import org.newdawn.slick.*;
import org.newdawn.slick.geom.*;

import spacegame.*;
import spacegame.ai.*;
import spacegame.core.*;
import spacegame.inventory.*;
import spacegame.other.*;
import spacegame.other.GameUtilities.Point;

public abstract class EntityBase implements Comparable<EntityBase> {
	public long id;
	public Image model;
	public Polygon collisionShape;
	public Inventory inventory = new Inventory();
	
	public VelocityVector velocity = new VelocityVector(0, 0);
	public int maxHealth = -1;
	public boolean isImmortal;
	public int currentHealth = 0;
	public int attackModifier = 1;
	
	public long orbittingEntity;
	public float entityRotation;
	public boolean inOrbit;
	public float orbitRadius;
	public int timeSinceLastDmg = 150;
	
	public ArrayList<AIEntity> aiControllers = new ArrayList<>();
	
	public abstract EntityType getType();
	public abstract void setSpawnCoords();
	public abstract int getMaxVelocity();
	public abstract int getAcceleration();
	public abstract void setModel();
	public abstract String getModelName();
	public abstract void onLoad();
	public abstract int getRenderWeight();
	
	public void update(int delta) {
		setCollisionShape();
		updateRotation();
		getVector().onUpdate(delta);
		if(getManager().getEntityByID(orbittingEntity) != null) {
			performOrbit(delta);
		} else {
			move(delta);
		}
		
		if(currentHealth == 0 && maxHealth > 0) {
			CoreGame.getInstance().entityManager.despawnEntity(this);
			CoreGame.getInstance().world.createExplosionAt(asPoint(), (int) collisionShape.getBoundingCircleRadius());
			onDespawn();
		}
		
		for(AIEntity ai : aiControllers) {
			ai.update(delta);
		}
		
		timeSinceLastDmg += delta;
		if(timeSinceLastDmg > 0 && timeSinceLastDmg < 150) {
			if(currentHealth > 0) {
				model.bind();
				model.setImageColor(1f, 0.5f, 0.5f);
			}
		} else {
			setModel();
			updateRotation();
		}
		
		inventory.weaponCD = Math.max(0, inventory.weaponCD-delta/attackModifier);
	}
	
	public void move(int delta) {
		if(velocity.velocityLength > getMaxVelocity()) { 
			velocity.setVelocity(getMaxVelocity());
		} else if(velocity.velocityLength < 0) {
			velocity.setVelocity(0);
		}
	}
	
	public void spawn() {
		onLoad();
		setModel();
		setCollisionShape();
		setID();
	}
	
	public void setID() {
		if(id == 0) {
			id = Math.abs(new Random().nextLong());
			setSpawnCoords();
		} 
	}
	
	public void setOrbit(float xStart, float yStart, float initialRotation, EntityBase entityToOrbit) {
		getVector().setCoords(xStart, yStart);
		entityRotation = initialRotation;
		orbittingEntity = entityToOrbit.id;
		orbitRadius = (float) Math.sqrt(Math.pow(xStart-entityToOrbit.getVector().xCoord, 2) + Math.pow(yStart-entityToOrbit.getVector().yCoord, 2));
		inOrbit = true;
	}
	
	public void performOrbit(int delta) {
		EntityBase entity = getManager().getEntityByID(orbittingEntity);
		orbitRadius = orbitRadius > 0 ? orbitRadius : entity.collisionShape.getBoundingCircleRadius()*4f;
		if(!inOrbit) {
			entityRotation = getClosestAngleForRotation(entity, orbitRadius);
			float x1 = getVector().xCoord;
			float y1 = getVector().yCoord;
			float x2 = (float) (entity.getVector().xCoord + orbitRadius*Math.cos(Math.toRadians(entityRotation)));
			float y2 = (float) (entity.getVector().yCoord + orbitRadius*Math.sin(Math.toRadians(entityRotation)));
			float distance = (float) Math.sqrt(Math.pow(x2-x1, 2) + Math.pow(y2-y1, 2));
			if(distance <= 5) {
				inOrbit = true;
			} else {
				float xDistance = x2-x1;
				float yDistance = y2-y1;
				getVector().xCoord += xDistance*delta/1000f;
				getVector().yCoord += yDistance*delta/1000f;
				
				float rotation = getVector().rotation;
				float newRotation = GameUtilities.calculateBearing(this, entity);
				float rotationDifference = rotation - newRotation;
				rotation += rotationDifference*delta/1000f;
				if(Math.abs((newRotation - rotation) % 360) > 10) {
					rotation = rotation % 360;
				}
			}
		} else {
			velocity.removeVelocity();
			float centerX = entity.getVector().xCoord;
			float centerY = entity.getVector().yCoord;
			entityRotation += 250/orbitRadius*delta/1000f;
			getVector().xCoord = (float) (centerX + orbitRadius*Math.cos(Math.toRadians(entityRotation)));
			getVector().yCoord = (float) (centerY + orbitRadius*Math.sin(Math.toRadians(entityRotation)));
		}
	}
	
	public int getClosestAngleForRotation(EntityBase entity, float r) {
		float distance = Float.MAX_VALUE;
		int angle = 0;
		for(int a = 0; a <= 360; a+=4) {
			float x1 = getVector().xCoord;
			float y1 = getVector().yCoord;
			float x2 = (float) (entity.getVector().xCoord + r*Math.cos(Math.toRadians(a)));
			float y2 = (float) (entity.getVector().yCoord + r*Math.sin(Math.toRadians(a)));
			float d = (float) (Math.pow(x2-x1, 2) + Math.pow(y2-y1, 2));
			if(d < distance) {
				angle = a;
				distance = d;
			}
		}
		return angle;
	}
	
	public void setCollisionShape() { 
		collisionShape = TextureHandler.getPolygonForImage(getModelName());
		Transform t = Transform.createRotateTransform((float) Math.toRadians(getVector().rotation), getVector().xCoord, getVector().yCoord);
		collisionShape = (Polygon) collisionShape.transform(t);
		collisionShape.setCenterX(getVector().xCoord);
		collisionShape.setCenterY(getVector().yCoord);
	}
	
	public void setMaxHealth(int health) {
		this.maxHealth = health;
		this.currentHealth = health;
	}
	
	public void damageEntity(int dmg) {
		if(this.maxHealth > 0 && !isImmortal) {
			float maxEffectiveHealth = maxHealth * (1f+(inventory.calculateTotalArmor()/100f));
			float effectiveHealth = currentHealth * (1f+(inventory.calculateTotalArmor()/100f));
			effectiveHealth = Math.max(0, effectiveHealth-dmg);
			currentHealth = (int) (effectiveHealth/maxEffectiveHealth*maxHealth);
			this.timeSinceLastDmg = 0;
		}
	}
	
	public void updateRotation() {
		model.setRotation(getVector().rotation);
	}
	
	public void addAIController(AIEntity ai) {
		this.aiControllers.add(ai);
	}
	
	public void addSavableData(HashMap<String, Object> savableMap) {}
	
	public void loadSavableData(HashMap<String, String> rawData) {}
	
	public void onDespawn() {}
	
	@Override
	public boolean equals(Object obj) {
		if(obj instanceof EntityBase) {
			EntityBase e = (EntityBase) obj;
			if(e.id == this.id) return true;
		}
		return false;
	}
	
	public Point asPoint() {
		return new Point(velocity.xCoord, velocity.yCoord);
	}
	
	public VelocityVector getVector() {
		return velocity;
	}
	
	public EntityManager getManager() {
		return CoreGame.getInstance().entityManager;
	}
	
	@Override
	public int compareTo(EntityBase o) {
		return o.getRenderWeight() - getRenderWeight();
	}
	
	public enum EntityType {
		Blaster, Enemy, Meteor, Missile, Planet, Player, Star, Item, Spawner
	}
}
