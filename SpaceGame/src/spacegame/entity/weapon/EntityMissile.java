package spacegame.entity.weapon;

import java.util.*;

import spacegame.*;
import spacegame.core.*;
import spacegame.core.CollisionDetector.*;
import spacegame.entity.*;
import spacegame.entity.EntityBase.*;
import spacegame.entity.enemy.*;
import spacegame.inventory.*;
import spacegame.other.*;
import spacegame.other.GameUtilities.*;

public class EntityMissile extends EntityBase implements ICollisionDetection {
	public long initialEntity;
	public long targetEntity;
	public ItemWeapon weapon;
	public int aliveTime;
	
	public EntityMissile(long launchEntity) {
		this.initialEntity = launchEntity;
		this.weapon = Item.missile;
	}
	
	@Override
	public void update(int d) {
		super.update(d);
		float delta = d/1000f;
		
		if(aliveTime > 10000) {
			getManager().despawnEntity(this);
		}
		
		EntityBase entity = getManager().getEntityByID(targetEntity);
		if(entity != null) {
			getVector().rotation = getVector().rotation + ((getRotationDirection(entity) ? 180 : -180)*delta);
			getVector().setVelocityWithDirection(getMaxVelocity(), getVector().rotation);
		} else {
			setClosestTarget();
		}
		
		aliveTime += d;
	}
	
	private void setClosestTarget() {
		double distance = Double.MAX_VALUE;
		long entityId = 0;
		for(EntityBase base : getManager().getIngameEntities()) {
			if(base instanceof EntityEnemy || base instanceof EntityPlayer) {
				if(base.id != initialEntity) {
					double d1 = getDistanceToPoint(asPoint(), base.asPoint());
					if(d1 < distance) {
						entityId = base.id;
						distance = d1;
					}
				}
			}
		}
		targetEntity = entityId;
	}
	
	private double getDistanceToPoint(Point p1, Point p2) {
		return Math.sqrt(Math.pow(p1.x-p2.x, 2) + Math.pow(p1.y-p2.y, 2));
	}
	
	private boolean getRotationDirection(EntityBase entity) {
		float entityRotation = getVector().rotation % 360;
		float diff = (GameUtilities.calculateBearing(this, entity)-entityRotation) % 360;
		float absDiff = Math.abs(diff);
		boolean movementDirection = false;
		if(absDiff >= 180 && diff < 0) {
			movementDirection = true;
		} else if(diff >= 0)  {
			movementDirection = true;
		}
		return movementDirection;
	}
	
	@Override
	public EntityType getType() {
		return EntityType.Missile;
	}

	@Override
	public void onCollision(EntityBase collisionWith) {
		if(collisionWith.id == targetEntity) {
			CoreGame.getInstance().entityManager.despawnEntity(this);
			CoreGame.getInstance().world.createImpactAt(asPoint());
			weapon.onImpactWith(collisionWith);
		}
	}

	@Override
	public void addSavableData(HashMap<String, Object> savableMap) {
		savableMap.put("initialEntity", initialEntity);
		savableMap.put("targetEntity", targetEntity);	
	}
	
	@Override
	public void loadSavableData(HashMap<String, String> rawData) {
		initialEntity = Long.parseLong(rawData.get("initialEntity"));
		targetEntity = Long.parseLong(rawData.get("targetEntity"));
	}
	
	@Override
	public void setSpawnCoords() {
		EntityBase firingEntity = getManager().getEntityByID(initialEntity);
		getVector().rotation = firingEntity.getVector().rotation;
		getVector().xCoord = (float) (firingEntity.getVector().xCoord + 50*Math.sin(Math.toRadians(firingEntity.getVector().rotation)));
		getVector().yCoord = (float) (firingEntity.getVector().yCoord - 50*Math.cos(Math.toRadians(firingEntity.getVector().rotation)));
		this.model.setRotation(getVector().rotation);
	}
	
	@Override
	public int getMaxVelocity() {
		return 350;
	}

	@Override
	public int getAcceleration() {
		return 100;
	}

	@Override
	public void setModel() {
		this.model = TextureHandler.getCustomImageByName(getModelName());
	}

	@Override
	public String getModelName() {
		return "missile";
	}

	@Override
	public void onLoad() {
		
	}

	@Override
	public int getRenderWeight() {
		return 2;
	}
}