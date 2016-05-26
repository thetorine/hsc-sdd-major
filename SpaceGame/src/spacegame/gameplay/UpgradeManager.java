package spacegame.gameplay;

import java.util.*;

public class UpgradeManager {

	public ArrayList<Upgrade> availableUpgrades = new ArrayList<>();
	
	public UpgradeManager() {
		addUpgrade("Damage", 3, new int[] { 1, 2, 3 });
		addUpgrade("Armor", 3, new int[] { 1, 2, 3 });
		addUpgrade("Health", 5, new int[] { 1, 2, 3, 4, 5 });
		addUpgrade("Speed", 4, new int[] { 1, 2, 3, 4 });
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
	
	public boolean isMaxLevelUnlocked(String name) {
		for(Upgrade upgrade : availableUpgrades) {
			if(upgrade.name.equals(name)) {
				return upgrade.level == upgrade.maxLevel;
			}
		}
		return false;
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
			return modifers[level];
		}
		
		public void upgrade() {
			//TODO add some currency to enable upgrading
			level = Math.min(level+1, maxLevel);
		}
		
		public String getUpgradeText() {
			if(level < maxLevel) {
				return String.format("Increase max %s by %d", name, modifers[level]);
			}
			return String.format("Increased %s by %d", name, modifers[maxLevel-1]);
		}
	}
}
