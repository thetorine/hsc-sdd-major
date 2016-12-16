package spacegame.gameplay;

import spacegame.gamestates.IngameState;

import java.util.ArrayList;

//manages the upgrades for the player
public class UpgradeManager {

	public ArrayList<Upgrade> availableUpgrades = new ArrayList<>();
	
	public UpgradeManager() {
		//upgrades with max level and the modifier at each level
		addUpgrade("Damage", 3, new int[] { 3, 6, 9, 12 });
		addUpgrade("Armor", 3, new int[] { 10, 20, 30 });
		addUpgrade("Health", 5, new int[] { 120, 240, 360, 480, 600 });
		addUpgrade("Speed", 4, new int[] { 10, 30, 70, 130 });
	}
	
	public void addUpgrade(String name, int maxLevel, int[] modifiers) {
		Upgrade upgrade = new Upgrade(name, maxLevel, modifiers);
		availableUpgrades.add(upgrade);
	}
	
	public Upgrade getUpgrade(String name) {
		for(Upgrade upgrade : availableUpgrades) {
			if(upgrade.name.equals(name)) {
				return upgrade; 
			}
		}
		return null;
	}
	
	public static class Upgrade {
		public String name;
		public int level;
		public int maxLevel;
		public int[] modifers;
		
		public Upgrade(String name, int level, int[] modifiers) {
			this.name = name;
			this.maxLevel = level;
			this.modifers = modifiers;
		}
		
		public int getModifier() {
			return level > 0 ?  modifers[level-1] : 0;
		}

		//upgrades
		public void upgrade() {
			if(canUpgrade()) {
				IngameState.getInstance().entityManager.player.pointsGained -= getPointsForUpgrade(level+1);
				level = Math.min(level+1, maxLevel);
			}
		}

		//checks if an upgrade can be purchased, checks if there are enough points
		public boolean canUpgrade() {
			return IngameState.getInstance().entityManager.player.pointsGained >= getPointsForUpgrade(level+1);
		}

		public int getPointsForUpgrade(int level) {
			if(level > 0) {
				return 100 + 100*(level-1);
			}
			return 0;
		}

		//gets the text to display whether an upgrade is available or not
		public String getUpgradeText() {
			if(level < maxLevel) {
				return String.format("Increase max %s by %d", name, modifers[level]);
			}
			return String.format("Increased %s by %d", name, modifers[maxLevel-1]);
		}
	}
}
