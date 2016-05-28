package spacegame.entity.enemy;

import java.util.*;

import spacegame.*;
import spacegame.core.*;
import spacegame.entity.*;
import spacegame.entity.environment.*;
import spacegame.inventory.*;

public class EntitySpawner extends EntityEnemy {
	
	public int spawnDelay; 
	public int strength;
	public Random random = new Random();
	
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
				if(e instanceof EntityEnemy && ((EntityEnemy) e).protectingEntity == protectingEntity || e instanceof EntityPatrol && ((EntityPatrol) e).protectingEntity == protectingEntity) {
					noOfProtectors += 1;
				}
			}
			if(noOfProtectors < 3*(strength+1)) {
				EntityBase entity = random.nextBoolean() ? new EntityPatrol() : new EntityEnemy();
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
		attackModifier = 2;
	}

	@Override
	public String getModelName() {
		return "enemyBlue5.png";
	}

	@Override
	public void onLoad() { 
		super.onLoad();
		inventory.addWeaponStack(new ItemStack(Item.blaster, 1));
		inventory.selectNextWeapon();
	}

	@Override
	public int getRenderWeight() {
		return 1;
	}
	
	@Override
	public void addSavableData(HashMap<String, Object> savableMap) {
		super.addSavableData(savableMap);
		savableMap.put("spawnDelay", spawnDelay);
	}
	
	@Override
	public void loadSavableData(HashMap<String, String> rawData) {
		super.loadSavableData(rawData);
		spawnDelay = Integer.parseInt(rawData.get("spawnDelay"));
	}
}
