package spacegame.entity;

import org.lwjgl.input.Keyboard;
import spacegame.core.AssetManager;
import spacegame.core.CollisionDetector.ICollisionDetection;
import spacegame.core.World;
import spacegame.entity.environment.EntityPlanet;
import spacegame.gameplay.Tutorial;
import spacegame.gameplay.UpgradeManager;
import spacegame.gamestates.IngameState;
import spacegame.gui.screen.GuiGameOver;
import spacegame.inventory.Item;
import spacegame.inventory.ItemStack;
import spacegame.inventory.ItemWeapon;
import spacegame.other.GameConstants;
import spacegame.other.GameUtilities.Point;
import spacegame.other.KeyboardListener;
import spacegame.other.KeyboardListener.IKeyboard;

import java.util.HashMap;
import java.util.Random;

public class EntityPlayer extends EntityBase implements IKeyboard, ICollisionDetection{
	public EntityBase selectedTarget;
	public UpgradeManager upgradeManager;
	
	public float currentShield;
	public int maxShield;
	public boolean regenShield;
	public int pointsGained;
	
	public boolean onPlanet;
	
	private Random random = new Random();
	private int textureId = random.nextInt(3)+1;

	public Tutorial tutorialSetup;
	public boolean tutorialCompleted;
	
	public EntityPlayer() {
		upgradeManager = new UpgradeManager();
	}
	
	@Override
	public void onLoad() {
		if(IngameState.getInstance().firstLoad) {
			inventory.addItemStack(new ItemStack(Item.metal, 3));
			setMaxHealth(1000);
		}
		KeyboardListener.registerListener(this);
		tutorialSetup = new Tutorial(this);
	}
	
	@Override
	public void update(int delta) {
		super.update(delta);
		int x = (int) (getVector().xCoord - 25*Math.sin(getVector().rotation));
		int y = (int) (getVector().yCoord + 25*Math.cos(getVector().rotation));
		IngameState.getInstance().world.engineTrailFX.addEmitterAt(new Point(x, y));
		
		if(timeSinceLastDmg > 2000) {
			currentShield = (float) Math.min(maxShield, currentShield+((maxShield-currentShield)*0.1)*delta/1000f); //regen 10% of missing shield every second
		}

		maxHealth = 1000 + upgradeManager.getUpgrade("Health").getModifier();

		tutorialSetup.onUpdate();
		this.onPlanet = false;
	}
	
	@Override
	public EntityType getType() {
		return EntityType.Player;
	}
	
	@Override
	public void setSpawnCoords() {
		getVector().xCoord = -18000;
		getVector().yCoord = -11000;
	}
	
	@Override
	public int getMaxVelocity() {
		return 300 + upgradeManager.getUpgrade("Speed").getModifier();
	}
	
	@Override
	public int getAcceleration() {
		return 600;
	}
	
	public void setTarget(Point p) {
		EntityBase b = IngameState.getInstance().entityManager.getEntityAt(p, true);
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
		savableMap.put("pointsGained", pointsGained);
		savableMap.put("tutorialCompleted", tutorialCompleted);

		String upgradeSavable = "";
		for(UpgradeManager.Upgrade upgrade : upgradeManager.availableUpgrades) {
			if(upgradeSavable.length() == 0) {
				upgradeSavable = upgrade.name + "," + upgrade.level;
			} else {
				upgradeSavable = upgradeSavable + "," + upgrade.name +  "," + upgrade.level;
			}
		}
		savableMap.put("upgradeManager", upgradeSavable);
	}
	
	@Override
	public void loadSavableData(HashMap<String, String> rawData) {
		maxShield = Integer.parseInt(rawData.get("maxShield"));
		currentShield = Float.parseFloat(rawData.get("currentShield"));
		pointsGained = Integer.parseInt(rawData.get("pointsGained"));
		tutorialCompleted = Boolean.parseBoolean(rawData.get("tutorialCompleted"));

		String upgradeSavable[] = rawData.get("upgradeManager").split(",");
		for(int i = 0; i < upgradeSavable.length; i+=2) {
			String upgradeName = upgradeSavable[i];
			int level = Integer.parseInt(upgradeSavable[i+1]);
			upgradeManager.getUpgrade(upgradeName).level = level;
		}
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
		if(IngameState.getInstance().gamePaused) {
			return;
		}
		if(key == GameConstants.UP) {
			getVector().accelerate(getAcceleration(), delta/1000f);
		} else if(key == GameConstants.LEFT) {
			getVector().steerInDirection(false, Math.PI, delta/100f);
		} else if(key == GameConstants.RIGHT) {
			getVector().steerInDirection(true, Math.PI, delta/100f);
		} else if(key == GameConstants.DOWN) {
			getVector().decelerate(getAcceleration()/2, delta/5000f);
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
	
	@Override
	public void setModel() {
		this.model = AssetManager.getImageByName(AssetManager.baseSheet, getModelName(), 0.5f);
	}

	@Override
	public String getModelName() {
		return String.format("playerShip%d_blue.png", textureId);
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
		IngameState.getInstance().guiHierarchy.openGui(new GuiGameOver());
	}
}