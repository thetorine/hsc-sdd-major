package spacegame.entity;

import java.util.*;

import spacegame.*;
import spacegame.ai.*;
import spacegame.core.*;

public class EntitySpawner extends EntityBase {
	
	public long protectingEntity; 
	public int spawnDelay; 
	public int strength;
	
	public EntitySpawner(long entity) {
		this.protectingEntity = entity;
	}
	
	@Override
	public void update(int delta) {
		super.update(delta);
		if(strength == 0) {
			EntityBase base = CoreGame.getInstance().entityManager.getEntityByID(protectingEntity);
			strength = (base instanceof EntityPlanet) ? World.getPlanetInfo((EntityPlanet) base).difficulty : 1;
		}
		spawnDelay += delta;
		if(spawnDelay > 2000*(strength)) {
			int noOfProtectors = 0;
			for(EntityBase e : getManager().getIngameEntities()) {
				if(e instanceof EntityEnemy && ((EntityEnemy) e).protectingEntity == protectingEntity) {
					noOfProtectors += 1;
				}
			}
			if(noOfProtectors < 3*(strength+1)) {
				EntityEnemy entity = new EntityEnemy();
				entity.getVector().setCoords(getVector().xCoord, getVector().yCoord);
				getManager().spawnEntity(entity);
			}
			spawnDelay = 0;
		}
	}

	@Override
	public EntityType getType() {
		return EntityType.Spawner;
	}
	
	@Override
	public void onDespawn() {
		EntityBase base = CoreGame.getInstance().entityManager.getEntityByID(protectingEntity);
		if(base instanceof EntityPlanet) {
			World.getPlanetInfo((EntityPlanet) base).liberated = true;
		}
	}

	@Override
	public void setSpawnCoords() {
		setMaxHealth(2500);
	}

	@Override
	public int getMaxVelocity() {
		return 100;
	}

	@Override
	public int getAcceleration() {
		return 75;
	}

	@Override
	public void setModel() {
		this.model = TextureHandler.getImageByName(TextureHandler.baseSheet, getModelName(), 0.8f);
	}

	@Override
	public String getModelName() {
		return "enemyBlack4.png";
	}

	@Override
	public void onLoad() {
		addAIController(new AIProtectBase(this, protectingEntity, false, strength));
	}

	@Override
	public int getRenderWeight() {
		return 1;
	}
	
	@Override
	public void addSavableData(HashMap<String, Object> savableMap) {
		savableMap.put("protectingEntity", protectingEntity);
		savableMap.put("spawnDelay", spawnDelay);
	}
	
	@Override
	public void loadSavableData(HashMap<String, String> rawData) {
		spawnDelay = Integer.parseInt(rawData.get("spawnDelay"));
	}

}
