package spacegame.ai;

import java.util.*;

import spacegame.core.*;
import spacegame.entity.*;
import spacegame.entity.environment.*;
import spacegame.gamestates.*;
import spacegame.inventory.*;
import spacegame.other.*;
import spacegame.other.GameUtilities.*;

public class AIProtectBase extends AIEntity {
	
	private boolean targetAcquired;
	private long baseEntity;
	private boolean shouldAttack;
	private Random rand = new Random();
	private int strength;

	public AIProtectBase(EntityBase e, long baseEntity, boolean attack, int strength) {
		super(e);
		this.baseEntity = baseEntity;
		this.shouldAttack = attack;
		this.strength = strength;
	}
	
	@Override
	public void update(int delta) {
		EntityBase protectingBase = entity.getManager().getEntityByID(baseEntity); //the base the entity is protecting.
		EntityPlayer p = IngameState.getInstance().entityManager.player; //player
		double distanceToBase = getDistanceToPoint(p.asPoint(), protectingBase.asPoint()); //this entities distance from the base
		
		//strength determines how much health and dmg an entity does
		if(strength == 0) {
			strength = (protectingBase instanceof EntityPlanet) ? World.getPlanetInfo((EntityPlanet) protectingBase).difficulty : 1;
		}

		//only some entities can attack, this checks if the entity should be attacking the player in range.
		if(shouldAttack) {
			if(distanceToBase < 500*strength) targetAcquired = true;
			else targetAcquired = false;
		}

		//if there is no target to attack
		if(!targetAcquired && protectingBase != null) {
			double distanceFromBase = getDistanceToPoint(entity.asPoint(), protectingBase.asPoint());
			//entire segment of code makes the entity move around the base
			if(distanceFromBase > protectingBase.model.getWidth()*0.75d) {
				float bearing = (float) (GameUtilities.calculateBearing(protectingBase, entity)-Math.PI/9f);
				if(!withinRange(entity.getVector().rotation, bearing, (float) (Math.PI/90f))) {
					faceEntity(protectingBase, bearing, delta/1000f);
				}
				accelerate(delta/100f);
			} else {
				float bearing = (float) (GameUtilities.calculateBearing(protectingBase, entity)-Math.PI/2);
				if(!withinRange(entity.getVector().rotation, bearing, (float) (Math.PI/90f))) {
					faceEntity(protectingBase, bearing, delta/1000f);
				}
				accelerate(delta/50f);
			}
		} else if(targetAcquired) {
			//code to face the player and attack if the player is in range.
			float bearing = GameUtilities.calculateBearing(p, entity);
			if(!withinRange(entity.getVector().rotation, bearing, (float) (Math.PI/90f))) {
				faceEntity(p, bearing, delta/1000f);
			}
			double distance = getDistanceToPoint(p.asPoint(), entity.asPoint());
			if(distance > 200) {
				accelerate(delta/250f);
			} else if(distance < 100) {
				accelerateBackwards(delta/250f);
			} else if(distance > 100 && distance < 200) {
				((ItemWeapon) entity.inventory.selectedWeapon.itemClass).fire(new Object[] {entity, true, strength > 2});
			}
		}
		//just random movements so the entity does not see perfectly moving
		addRandomVelocityChange(delta/50f);
	}
	
	private void faceEntity(EntityBase p, float bearing, float delta) {
		entity.getVector().rotation = (float) (entity.getVector().rotation + ((getRotationDirection(p, bearing) ? Math.PI : -Math.PI)*delta));
	}
	
	private void accelerate(float delta) {
		entity.getVector().accelerateInDirection(entity.getAcceleration(), (float) (entity.getVector().rotation+Math.PI), delta);
	}
	
	private void accelerateBackwards(float delta) {
		entity.getVector().accelerateInDirection(entity.getAcceleration(), entity.getVector().rotation, delta);
	}
	
	private void addRandomVelocityChange(float delta) {
		if(rand.nextFloat() < 0.3f) {
			float rotationToAdd = (float) (rand.nextFloat()*2*Math.PI*(rand.nextBoolean() ? 1 : -1));
			int speedToAdd = 20;
			entity.getVector().accelerateInDirection(speedToAdd, rotationToAdd, delta);
		}
	}

	//gets the direction the entity needs to move in order to face a specific bearing.
	//returns true when movement is right, and returns false when movement is left
	private boolean getRotationDirection(EntityBase p, float bearing) {
		float entityRotation = (float) (entity.getVector().rotation % (2*Math.PI));
		float diff = (float) ((bearing-entityRotation) % (2*Math.PI));
		float absDiff = Math.abs(diff);
		boolean movementDirection = false;
		if(absDiff >= Math.PI && diff < 0) {
			movementDirection = true;
		} else if(diff >= 0)  {
			movementDirection = true;
		}
		return movementDirection;
	}
	
	private double getDistanceToPoint(Point p1, Point p2) {
		return Math.sqrt(Math.pow(p1.x-p2.x, 2) + Math.pow(p1.y-p2.y, 2));
	}

	//checks if the two numbers are within a specific range.
	private boolean withinRange(float f1, float f2, float range) {
		return Math.abs(f1-f2) <= range;
	}
}
