package spacegame.gameplay;

import java.util.*;

import spacegame.inventory.*;

public class ExplorablePlanet {
	
	public long planet;
	public String planetName;
	public boolean liberated;
	public int difficulty;
	public ArrayList<PlanetRegion> planetRegions = new ArrayList<>();
	
	public ExplorablePlanet(long planet, String name, int difficulty) {
		this.planet = planet;
		this.planetName = name;
		this.difficulty = difficulty == 0 ? 1 : difficulty;
	}
	
	public void addRegion(String name, String desc, int exploreTime, ItemStack... stack) {
		planetRegions.add(new PlanetRegion(name, desc, exploreTime, stack));
	}
	
	public static class PlanetRegion {
		public String locationName;
		public String desc;
		public int exploreTime;
		public int currentExplore;
		public ArrayList<ItemStack> exploreYield = new ArrayList<>();
		
		public boolean completed;
		
		public PlanetRegion(String name, String desc, int exploreTime, ItemStack... stack) {
			this.locationName = name;
			this.desc = desc;
			this.exploreTime = exploreTime;
			this.currentExplore = exploreTime;
			
			if(stack != null) {
				for(ItemStack is : stack) {
					this.exploreYield.add(is);
				}
			}
		}
		
		public void update(int delta) {
			if(currentExplore > 0 && currentExplore < exploreTime) {
				currentExplore = Math.max(currentExplore-delta, 0);
			} else if (currentExplore == 0) {
				completed = true;
			}
		}
		
		public void beginExplore() {
			if(!completed) {
				currentExplore -= 1;
			}
		}
		
	}
}
