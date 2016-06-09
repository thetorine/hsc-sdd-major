package spacegame.entity.enemy;

import java.util.*;

import spacegame.ai.*;
import spacegame.core.*;
import spacegame.core.CollisionDetector.*;
import spacegame.entity.*;
import spacegame.entity.environment.*;
import spacegame.gamestates.*;
import spacegame.inventory.*;
import spacegame.other.GameUtilities.*;

public class EntityEnemy extends EntityBase implements ICollisionDetection {
	public long protectingEntity;
	
	@Override
	public void update(int delta) {
		super.update(delta);
	}

	@Override
	public EntityType getType() {
		return EntityType.Enemy;
	}
	
	@Override
	public void setSpawnCoords() {
		setMaxHealth(200);
	}
	
	@Override
	public int getMaxVelocity() {
		return 300;
	}

	@Override
	public int getAcceleration() {
		return 100;
	}
	
	@Override
	public void addSavableData(HashMap<String, Object> savableMap) {
		savableMap.put("protectingEntity", protectingEntity);
	}
	
	@Override
	public void loadSavableData(HashMap<String, String> rawData) {
		protectingEntity = Long.parseLong(rawData.get("protectingEntity"));
	}

	@Override
	public void setModel() {
		this.model = AssetManager.getImageByName(AssetManager.baseSheet, getModelName(), 0.3f);
	}

	@Override
	public String getModelName() {
		return "enemyGreen3.png";
	}

	@Override
	public void onCollision(EntityBase collisionWith) {
		for(AIEntity ai : aiControllers) {
			if(ai instanceof AICollisionWithObject) {
				AICollisionWithObject collisionAi = (AICollisionWithObject) ai;
				collisionAi.setCurrentCollider(collisionWith);
			}
		}
	}

	@Override
	public void onLoad() {
		if(protectingEntity == 0) {
			double distance = Double.MAX_VALUE;
			EntityPlanet closestPlanet = null;
			for(EntityBase e : getManager().getIngameEntities()) {
				if(e instanceof EntityPlanet) {
					double d1 = getDistanceToPoint(e.asPoint());
					if(d1 < distance) {
						closestPlanet = (EntityPlanet) e;
						distance = d1;
					}
				}
			}
			protectingEntity = closestPlanet.id;
			inventory.addWeaponStack(new ItemStack(Item.blaster, 1));
			inventory.selectNextWeapon();
		}
		addAIController(new AIProtectBase(this, protectingEntity, true, 0));
		addAIController(new AICollisionWithObject(this));
		attackModifier = 4;
	}
	
	@Override
	public void onDespawn() {
		Random random = new Random();
		if(random.nextFloat() < 0.3f) {
			IngameState.getInstance().world.dropItemIntoWorld(this, new ItemStack(Item.metal, random.nextInt(3)+1));
		} else if(random.nextFloat() < 0.1f) {
			IngameState.getInstance().world.dropItemIntoWorld(this, new ItemStack(Item.rare_metal, random.nextInt(1)+1));
		}
	}
	
	private double getDistanceToPoint(Point p) {
		return Math.sqrt(Math.pow(getVector().xCoord-p.x, 2) + Math.pow(getVector().yCoord-p.y, 2));
	}

	@Override
	public int getRenderWeight() {
		return 1;
	}
}
