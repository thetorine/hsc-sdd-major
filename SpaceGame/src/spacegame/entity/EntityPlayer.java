package spacegame.entity;

import java.util.*;

import org.lwjgl.input.*;

import spacegame.*;
import spacegame.core.*;
import spacegame.core.CollisionDetector.*;
import spacegame.inventory.*;
import spacegame.other.*;
import spacegame.other.GameUtilities.*;
import spacegame.other.KeyboardListener.*;

public class EntityPlayer extends EntityBase implements IKeyboard, ICollisionDetection{
	public EntityBase selectedTarget;
	
	public float currentShield;
	public int maxShield;
	public boolean regenShield;
	
	public boolean onPlanet;
	
	@Override
	public void onLoad() {
		if(CoreGame.getInstance().firstLoad) {
			inventory.addItemStack(new ItemStack(Item.goldArmor, 1));
			inventory.addWeaponStack(new ItemStack(Item.blaster, 1));
			inventory.addWeaponStack(new ItemStack(Item.missile, 1));
			inventory.selectNextWeapon();
			setMaxHealth(1000);
		}
		KeyboardListener.registerListener(this);
	}
	
	@Override
	public void update(int delta) {
		super.update(delta);
		int x = (int) (getVector().xCoord - 25*Math.sin(Math.toRadians(getVector().rotation)));
		int y = (int) (getVector().yCoord + 25*Math.cos(Math.toRadians(getVector().rotation)));
		CoreGame.getInstance().world.engineTrailFX.addEmitterAt(new Point(x, y));
		
		if(timeSinceLastDmg > 2000) {
			currentShield = (float) Math.min(maxShield, currentShield+((maxShield-currentShield)*0.1)*delta/1000f); //regen 10% of missing shield every second
		}
		
		this.onPlanet = false;
	}
	
	@Override
	public EntityType getType() {
		return EntityType.Player;
	}
	
	@Override
	public void setSpawnCoords() {
	}
	
	@Override
	public int getMaxVelocity() {
		return 300;
	}
	
	@Override
	public int getAcceleration() {
		return 600;
	}
	
	public void setTarget(Point p) {
		EntityBase b = CoreGame.getInstance().entityManager.getEntityAt(p, true);
		if(b != null) {
			if(selectedTarget != null && selectedTarget.equals(b)) {
				selectedTarget = null;
			} else {
				selectedTarget = b;
			}
		}
	}
	
	@Override
	public void addSavableData(HashMap<String, Object> savableMap) {
		savableMap.put("maxShield", maxShield);
		savableMap.put("currentShield", currentShield);
	}
	
	@Override
	public void loadSavableData(HashMap<String, String> rawData) {
		maxShield = Integer.parseInt(rawData.get("maxShield"));
		currentShield = Float.parseFloat(rawData.get("currentShield"));
	}

	@Override
	public HashMap<Integer, Boolean> getKeysToListen() {
		HashMap<Integer, Boolean> map = new HashMap<>();
		map.put(GameConstants.UP, false);
		map.put(GameConstants.LEFT, false);
		map.put(GameConstants.RIGHT, false);
		map.put(GameConstants.DOWN, false);
		map.put(GameConstants.CUT_VELOCITY, false);
		map.put(GameConstants.FIRE_WEAPON, false);
		map.put(GameConstants.CYCLE_WEAPON, true);
		return map;
	}

	@Override
	public void onKeyPress(int key, int delta) {
		if(CoreGame.getInstance().gamePaused) {
			return;
		}
		if(key == GameConstants.UP) {
			getVector().accelerate(getAcceleration(), delta/1000f);
		} else if(key == GameConstants.LEFT) {
			getVector().steerInDirection(false, 200, delta/100f);
		} else if(key == GameConstants.RIGHT) {
			getVector().steerInDirection(true, 200, delta/100f);
		} else if(key == GameConstants.DOWN) {
			getVector().decelerate(getAcceleration(), delta/1000f);
		} else if(key == GameConstants.CUT_VELOCITY) {
			getVector().removeVelocity();
		} else if(key == GameConstants.FIRE_WEAPON) {
			if(inventory.selectedWeapon != null) {
				((ItemWeapon) inventory.selectedWeapon.itemClass).fire(new Object[] {this, false, true});
			}
		} else if(key == GameConstants.CYCLE_WEAPON) {
			inventory.selectNextWeapon();
		}
	}
	
	public boolean isKeyDown(int key) {
		return Keyboard.isKeyDown(key);
	}

	@Override
	public void setModel() {
		this.model = TextureHandler.getImageByName(TextureHandler.baseSheet, getModelName(), 0.5f);
	}

	@Override
	public String getModelName() {
		return "playerShip2_orange.png";
	}
	
	@Override
	public void setMaxHealth(int health) {
		super.setMaxHealth(health);
		maxShield = health/2;
		currentShield = maxShield;
	}
	
	@Override
	public void damageEntity(int dmg) {
		if(currentShield == 0) {
			super.damageEntity(dmg);
		} else {
			currentShield = (int) Math.max(0, currentShield-0.6*dmg); //reduce 40% dmg
		}
		this.timeSinceLastDmg = 0;
	}

	@Override
	public void onCollision(EntityBase collisionWith) {
		if(collisionWith instanceof EntityPlanet) {
			this.onPlanet = World.getPlanetInfo((EntityPlanet) collisionWith).liberated;
		} 
	}

	@Override
	public int getRenderWeight() {
		return 0;
	}
	
	@Override
	public void onDespawn() {
		super.onDespawn();
		KeyboardListener.deregisterListener(this);
	}
}