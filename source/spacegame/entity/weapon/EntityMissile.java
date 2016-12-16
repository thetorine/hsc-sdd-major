package spacegame.entity.weapon;

import spacegame.core.AssetManager;
import spacegame.core.CollisionDetector.ICollisionDetection;
import spacegame.entity.EntityBase;
import spacegame.entity.EntityPlayer;
import spacegame.entity.enemy.EntityEnemy;
import spacegame.gamestates.IngameState;
import spacegame.gamestates.StateManager;
import spacegame.inventory.Item;
import spacegame.inventory.ItemWeapon;
import spacegame.other.GameUtilities;
import spacegame.other.GameUtilities.Point;

import java.util.HashMap;

public class EntityMissile extends EntityBase implements ICollisionDetection {
	public long initialEntity;
	public long targetEntity;
	public ItemWeapon weapon;
	public int aliveTime;
	
	public EntityMissile(long launchEntity) {
		this.initialEntity = launchEntity;
		this.weapon = Item.missile;
		StateManager.instance.soundManager.missile.play(1f, 0.2f);
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
			getVector().rotation = (float) (getVector().rotation + ((getRotationDirection(entity) ? Math.PI : -Math.PI)*delta));
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
		float entityRotation = (float) (getVector().rotation % (2*Math.PI));
		float diff = (float) ((GameUtilities.calculateBearing(this, entity)-entityRotation) % (2*Math.PI));
		float absDiff = Math.abs(diff);
		boolean movementDirection = false;
		if(absDiff >= Math.PI && diff < 0) {
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
			IngameState.getInstance().entityManager.despawnEntity(this);
			IngameState.getInstance().world.createImpactAt(asPoint());
			weapon.onImpactWith(IngameState.getInstance().entityManager.getEntityByID(initialEntity), collisionWith);
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
		getVector().xCoord = (float) (firingEntity.getVector().xCoord + 25*Math.sin(firingEntity.getVector().rotation));
		getVector().yCoord = (float) (firingEntity.getVector().yCoord - 25*Math.cos(firingEntity.getVector().rotation));
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
		this.model = AssetManager.getCustomImageByName(getModelName());
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