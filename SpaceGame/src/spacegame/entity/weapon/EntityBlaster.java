package spacegame.entity.weapon;

import java.util.*;

import spacegame.*;
import spacegame.core.*;
import spacegame.core.CollisionDetector.*;
import spacegame.entity.*;
import spacegame.entity.enemy.*;
import spacegame.inventory.*;

public class EntityBlaster extends EntityBase implements ICollisionDetection {
	public long shooter;
	public int timeActive;
	public ItemWeapon weapon;
	public int velocity;
	public boolean invertShot;
	public boolean coordsSet;
	
	public EntityBlaster(long shotFrom, boolean invertShot) {
		this.shooter = shotFrom;
		this.weapon = Item.blaster;
		this.invertShot = invertShot;
	}
	
	@Override
	public void update(int delta) {
		super.update(delta);
		getVector().setVelocityWithDirection(getMaxVelocity(), (float) (getVector().rotation + (invertShot ? Math.PI : 0)));
		timeActive += delta;
		if(timeActive > 500) {
			CoreGame.getInstance().entityManager.despawnEntity(this);
		}
	}

	@Override
	public EntityType getType() {
		return EntityType.Blaster;
	}

	@Override
	public void setSpawnCoords() {
		EntityBase shootingEntity = getManager().getEntityByID(shooter);
		if(!coordsSet) {
			getVector().xCoord = (float) (shootingEntity.getVector().xCoord + 25*Math.sin(shootingEntity.getVector().rotation)*(invertShot ? -1 : 1));
			getVector().yCoord = (float) (shootingEntity.getVector().yCoord - 25*Math.cos(shootingEntity.getVector().rotation)*(invertShot ? -1 : 1));
			
		}
		getVector().rotation = shootingEntity.getVector().rotation;
		this.model.setRotation(getVector().rotation);
		velocity = (int) (shootingEntity.getVector().velocityLength+500);
	}

	@Override
	public void onCollision(EntityBase collisionWith) {
		EntityBase shootingEntity = getManager().getEntityByID(shooter);
		if(shootingEntity != null && collisionWith.maxHealth > 0 && collisionWith.getType() != shootingEntity.getType()) {
			if(shootingEntity instanceof EntityEnemy && collisionWith instanceof EntityEnemy) {
				//dont do anything
			} else {
				CoreGame.getInstance().entityManager.despawnEntity(this);
				CoreGame.getInstance().world.createImpactAt(asPoint());
				weapon.onImpactWith(collisionWith);
			}
		}
	}
	
	@Override
	public void addSavableData(HashMap<String, Object> savableMap) {
		savableMap.put("shooter", shooter);
		savableMap.put("timeActive", timeActive);
		savableMap.put("velocity", velocity);
		savableMap.put("invertShot", invertShot);
	}
	
	@Override
	public void loadSavableData(HashMap<String, String> rawData) {
		shooter = Long.parseLong(rawData.get("shooter"));
		timeActive = Integer.parseInt(rawData.get("timeActive"));
		velocity = Integer.parseInt(rawData.get("velocity"));
		invertShot = Boolean.parseBoolean(rawData.get("invertShot"));
	}

	@Override
	public int getMaxVelocity() {
		return velocity;
	}

	@Override
	public int getAcceleration() {
		return 250;
	}

	@Override
	public void setModel() {
		this.model = TextureHandler.getImageByName(TextureHandler.baseSheet, getModelName(), 0.4f);
	}

	@Override
	public String getModelName() {
		return "laserBlue05.png";
	}

	@Override
	public void onLoad() {
	}

	@Override
	public int getRenderWeight() {
		return 2;
	}
	
	public EntityBlaster setCoords(boolean b) {
		coordsSet = !b;
		EntityBase shootingEntity = getManager().getEntityByID(shooter);
		getVector().xCoord = (float) (shootingEntity.getVector().xCoord + 25*Math.sin(shootingEntity.getVector().rotation)*(invertShot ? -1 : 1));
		getVector().yCoord = (float) (shootingEntity.getVector().yCoord - 25*Math.cos(shootingEntity.getVector().rotation)*(invertShot ? -1 : 1));
		return this;
	}
}
