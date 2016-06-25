package spacegame.core;

import org.newdawn.slick.Graphics;
import org.newdawn.slick.Image;
import spacegame.core.DataHandler.ISavable;
import spacegame.entity.EntityBase;
import spacegame.entity.EntityItemDrop;
import spacegame.entity.EntityPlayer;
import spacegame.entity.environment.EntityPlanet;
import spacegame.gameplay.ExplorablePlanet;
import spacegame.gameplay.ExplorablePlanet.PlanetRegion;
import spacegame.gamestates.IngameState;
import spacegame.gamestates.StateManager;
import spacegame.inventory.Item;
import spacegame.inventory.ItemStack;
import spacegame.other.GameConstants;
import spacegame.other.GameUtilities.Point;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;

public class World implements ISavable {
	public ArrayList<ParticleFX> particleList = new ArrayList<>();
	public ParticleFX explosionFX = new ParticleFX(GameConstants.RESOURCE + "emitters/explosion.xml");
	public ParticleFX engineTrailFX = new ParticleFX(GameConstants.RESOURCE + "emitters/enginetrail.xml");
	public ParticleFX impactFX = new ParticleFX(GameConstants.RESOURCE + "emitters/impact.xml");
	public ParticleFX starFX = new ParticleFX(GameConstants.RESOURCE + "emitters/star.xml");
	
	private Image starImage = AssetManager.getImageByName(AssetManager.baseSheet, "star2.png", 0.4f);
	private ArrayList<Point> staticStarPoints = new ArrayList<>();
	private ArrayList<Point> slowStarPoints = new ArrayList<>();
	private ArrayList<Point> mediumStarPoints = new ArrayList<>();
	private ArrayList<Point> fastStarPoints = new ArrayList<>();
	private Random random = new Random();
	
	public static HashMap<Long, ExplorablePlanet> planetsList = new HashMap<>();
	
	public World() {
		IngameState.getInstance().dataHandler.registerInterface(this);
	}
	
	public void onGameCreation() {
		particleList.add(explosionFX);
		particleList.add(engineTrailFX);
		particleList.add(impactFX);
		particleList.add(starFX);
		
		for(int i = 0; i < 100; i++) {
			int randX = random.nextInt(GameConstants.GAME_WIDTH);
			int randY = random.nextInt(GameConstants.GAME_HEIGHT);
			staticStarPoints.add(new Point(randX, randY));
		}
		for(int i = 0; i < 50; i++) {
			int randX = random.nextInt(GameConstants.GAME_WIDTH);
			int randY = random.nextInt(GameConstants.GAME_HEIGHT);
			slowStarPoints.add(new Point(randX, randY));
		}
		for(int i = 0; i < 7; i++) {
			int randX = random.nextInt(GameConstants.GAME_WIDTH);
			int randY = random.nextInt(GameConstants.GAME_HEIGHT);
			mediumStarPoints.add(new Point(randX, randY));
		}
		for(int i = 0; i < 5; i++) {
			int randX = random.nextInt(GameConstants.GAME_WIDTH);
			int randY = random.nextInt(GameConstants.GAME_HEIGHT);
			fastStarPoints.add(new Point(randX, randY));
		}
		starImage.setAlpha(0.8f);
	}
	
	public void render(Graphics g) {
		//draw static star background
		for(Point p : staticStarPoints) {
			g.fillRect(p.x, p.y, 1, 1);
		}
		
		for(Point p : slowStarPoints) {
			g.fillRect(p.x, p.y, 2, 2);
		}
		
		for(Point p : mediumStarPoints) {
			g.drawImage(starImage, p.x, p.y);
		}
		
		for(Point p : fastStarPoints) {
			g.drawImage(starImage, p.x, p.y);
		}
	}
	
	public void onUpdate(int delta) {
		for(ParticleFX fx : particleList) {
			fx.effectSystem.update(delta);
			fx.updateCoordianates();
		}
		
		EntityPlayer player = IngameState.getInstance().entityManager.player;
		if(player.getVector().velocityLength > 0) {
			for(Point p : slowStarPoints) {
				modifyStarPosition(p, 100f, delta);
			}
			for(Point p : mediumStarPoints) {
				modifyStarPosition(p, 10f, delta);
			}
			for(Point p : fastStarPoints) {
				modifyStarPosition(p, 5f, delta);
			}
		}
	}
	
	public static ExplorablePlanet getPlanetInfo(EntityPlanet planet) {
		return planetsList.get(planet.id);
	}
	
	public void createStarAt(Point pt) {
		starFX.addEmitterAt(pt);
	}
	
	public void createImpactAt(Point pt) {
		impactFX.addEmitterAt(pt);
	}
	
	public void createExplosionAt(Point pt, int radius) {
		explosionFX.addEmitterAt(pt);
		StateManager.instance.soundManager.explosion.play(1f, 0.7f);
	}
	
	public void dropItemIntoWorld(EntityBase entity, ItemStack stack) {
		EntityItemDrop dropEntity = new EntityItemDrop(stack);
		dropEntity.getVector().setCoords(entity.getVector().xCoord, entity.getVector().yCoord);
		IngameState.getInstance().entityManager.spawnEntity(dropEntity);
	}

	@Override
	public void addSavableData(String savable, HashMap<String, Object> savableMap) {
		long id = Long.parseLong(savable.split("-")[1]);
		ExplorablePlanet ep = planetsList.get(id);
		savableMap.put("planet", ep.planet);
		savableMap.put("planetName", ep.planetName);
		savableMap.put("numberOfRegions", ep.planetRegions.size());
		savableMap.put("liberated", ep.liberated);
		savableMap.put("difficulty", ep.difficulty);
		for(int i = 0; i < ep.planetRegions.size(); i++) {
			PlanetRegion region = ep.planetRegions.get(i);
			savableMap.put(String.format("%d,locationName", i), region.locationName);
			savableMap.put(String.format("%d,desc", i), region.desc);
			savableMap.put(String.format("%d,exploreTime", i), region.exploreTime);
			savableMap.put(String.format("%d,currentExplore", i), region.currentExplore);
			savableMap.put(String.format("%d,completed", i), region.completed);
			savableMap.put(String.format("%d,givenItems", i), region.givenItems);
			String yield = "";
			for(ItemStack stack : region.exploreYield) {
				yield = yield + "," + stack.itemClass.itemName + "," + stack.quantity;
			}
			savableMap.put(String.format("%d,exploreYield", i), yield.replaceFirst(",", ""));
		}
	}

	@Override
	public void loadData(String savable, HashMap<String, String> rawData) {
		ExplorablePlanet ep = new ExplorablePlanet(Long.parseLong(rawData.get("planet")), rawData.get("planetName"), Integer.parseInt(rawData.get("difficulty")));
		ep.liberated = Boolean.parseBoolean(rawData.get("liberated"));
		int regionNo = Integer.parseInt(rawData.get("numberOfRegions"));
		for(int i = 0; i < regionNo; i++) {
			String locationName = rawData.get(String.format("%d,locationName", i));
			String desc = rawData.get(String.format("%d,desc", i));
			int exploreTime = Integer.parseInt(rawData.get(String.format("%d,exploreTime", i)));
			int currentExplore = Integer.parseInt(rawData.get(String.format("%d,currentExplore", i)));
			boolean completed = Boolean.parseBoolean(rawData.get(String.format("%d,completed", i)));
			boolean givenItems = Boolean.parseBoolean(rawData.get(String.format("%d,givenItems", i)));
			
			ArrayList<ItemStack> yieldStacksList = new ArrayList<>();
			String[] yield = rawData.get(String.format("%d,exploreYield", i)).split(",");
			for(int j = 0; j < yield.length-1; j+=2) {
				String itemName = yield[j];
				int amount = Integer.parseInt(yield[j+1]);
				yieldStacksList.add(new ItemStack(Item.getItemByName(itemName), amount));
			}
			ItemStack[] yieldStacks = new ItemStack[yieldStacksList.size()];
			PlanetRegion region = new PlanetRegion(locationName, desc, exploreTime, yieldStacksList.toArray(yieldStacks));
			region.currentExplore = currentExplore;
			region.completed = completed; 
			region.givenItems = givenItems;
			ep.planetRegions.add(region);
		}
		World.planetsList.put(ep.planet, ep);
	}

	@Override
	public ArrayList<String> getSavableList() {
		ArrayList<String> array = new ArrayList<>();
		for(long id: planetsList.keySet()) {
			array.add(String.format("planet-%d", id));
		}
		return array;
	}

	@Override
	public String getSaveDir() {
		return "world";
	}
	
	public void modifyStarPosition(Point p, float speedModifier, int delta) {
		EntityPlayer player = IngameState.getInstance().entityManager.player;
		float xVelocity = player.getVector().xVelocity;
		float yVelocity = player.getVector().yVelocity;
		
		p.x -= xVelocity*delta/1000f/speedModifier;
		p.y -= yVelocity*delta/1000f/speedModifier;
		
		if(p.x < 0) {
			p.x += GameConstants.GAME_WIDTH;
		} else if(p.x > GameConstants.GAME_WIDTH) {
			p.x -= GameConstants.GAME_WIDTH;
		}
		
		if(p.y < 0) {
			p.y += GameConstants.GAME_HEIGHT;
		} else if(p.y > GameConstants.GAME_HEIGHT) {
			p.y -= GameConstants.GAME_HEIGHT;
		}
	}
}