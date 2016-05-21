package spacegame.core;

import java.io.*;
import java.util.*;

import org.newdawn.slick.*;

import spacegame.*;
import spacegame.core.DataHandler.*;
import spacegame.core.ExplorablePlanet.*;
import spacegame.entity.*;
import spacegame.inventory.*;
import spacegame.other.*;
import spacegame.other.GameUtilities.*;

public class EntityManager implements ISavable {
	public ArrayList<EntityBase> ingameEntities = new ArrayList<>();
	public ArrayList<EntityBase> spawnableEntities = new ArrayList<>();
	public ArrayList<EntityBase> removableEntities = new ArrayList<>();
	
	public ArrayList<EntityBase> initialSpawn = new ArrayList<>();
	
	public EntityPlayer player = new EntityPlayer();
	
	public EntityManager() {
		CoreGame.getInstance().dataHandler.registerInterface(this);
		try {
			initialise();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public void initialise() throws Exception {
		if(CoreGame.getInstance().firstLoad) {
			spawnEntity(player);
		}
		updateEntityList();
	}
	
	public void onUpdate(int delta) {
		for(EntityBase entity : ingameEntities) {
			//TODO change to update only if the entity is currently near the player
			entity.update(delta);
		}
		
		updateEntityList();
	}

	@Override
	public void addSavableData(String savable, HashMap<String, Object> savableMap) {
		String id = savable.split("-")[1];
		EntityBase entity = getEntityByID(Long.parseLong(id));
		savableMap.put("id", entity.id);
		savableMap.put("xCoord", entity.getVector().xCoord);
		savableMap.put("yCoord", entity.getVector().yCoord);
		savableMap.put("xVelocity", entity.getVector().xVelocity);
		savableMap.put("yVelocity", entity.getVector().yVelocity);
		savableMap.put("rotation", entity.getVector().rotation);
		savableMap.put("angularVelocity", entity.getVector().angularVelocity);
		savableMap.put("maxHealth", entity.maxHealth);
		savableMap.put("currentHealth", entity.currentHealth);
		savableMap.put("timeSinceLastDmg", entity.timeSinceLastDmg);
		savableMap.put("orbittingEntity", entity.orbittingEntity);
		savableMap.put("entityRotation", entity.entityRotation);
		savableMap.put("inOrbit", entity.inOrbit);
		savableMap.put("orbitRadius", entity.orbitRadius);
		savableMap.put("isImmortal", entity.isImmortal);
		
		for(int row = 0; row < Inventory.ROWS; row++) {
			String invInRow = "";
			for(int col = 0; col < Inventory.COLUMNS; col++) {
				ItemStack stack = entity.inventory.getItemStackAt(row, col);
				if(stack != null) {
					invInRow = invInRow + String.format(",%s,%d,%d,%d", stack.itemClass.itemName, stack.quantity, stack.row, stack.column);
				}
			}
			savableMap.put(String.format("%d,mainInv", row), invInRow.replaceFirst(",", ""));
		}
		
		String weaponInv = "";
		for(ItemStack stack : entity.inventory.weaponStacks) {
			if(stack != null) {
				weaponInv = weaponInv + "," + stack.itemClass.itemName;
			}
		}
		savableMap.put("weaponInv", weaponInv.replaceFirst(",", ""));
		
		entity.addSavableData(savableMap);
 	}

	@Override
	public void loadData(String savable, HashMap<String, String> rawData) {
		EntityBase loadingEntity = getEntityForSavable(savable, rawData);
		loadingEntity.id = Long.parseLong(rawData.get("id"));
		loadingEntity.getVector().xCoord = Float.parseFloat(rawData.get("xCoord"));
		loadingEntity.getVector().yCoord = Float.parseFloat(rawData.get("yCoord"));
		loadingEntity.getVector().xVelocity = Float.parseFloat(rawData.get("xVelocity"));
		loadingEntity.getVector().yVelocity = Float.parseFloat(rawData.get("yVelocity"));
		loadingEntity.getVector().rotation = Float.parseFloat(rawData.get("rotation"));
		loadingEntity.getVector().angularVelocity = Float.parseFloat(rawData.get("angularVelocity"));
		loadingEntity.maxHealth = Integer.parseInt(rawData.get("maxHealth"));
		loadingEntity.currentHealth = Integer.parseInt(rawData.get("currentHealth"));
		loadingEntity.timeSinceLastDmg = Integer.parseInt(rawData.get("timeSinceLastDmg"));
		loadingEntity.orbittingEntity = Long.parseLong(rawData.get("orbittingEntity"));
		loadingEntity.entityRotation = Float.parseFloat(rawData.get("entityRotation"));
		loadingEntity.inOrbit = Boolean.parseBoolean(rawData.get("inOrbit"));
		loadingEntity.orbitRadius = Float.parseFloat(rawData.get("orbitRadius"));
		loadingEntity.isImmortal = Boolean.parseBoolean(rawData.get("isImmortal"));
		
		for(int i = 0; i < 4; i++) {
			String[] invInRow = rawData.get(String.format("%d,mainInv", i)).split(",");
			for(int item = 0; item < invInRow.length - 3; item += 4) {
				Item stackItem = Item.getItemByName(invInRow[item]);
				int quantity = Integer.parseInt(invInRow[item+1]);
				int row = Integer.parseInt(invInRow[item+2]);
				int col = Integer.parseInt(invInRow[item+3]);
				loadingEntity.inventory.addStackAt(new ItemStack(stackItem, quantity), row, col);
			}
		}
		
		String[] weaponInv = rawData.get("weaponInv").split(",");
		for(String name : weaponInv) {
			if(name.length() > 0) {
				loadingEntity.inventory.addWeaponStack(new ItemStack(Item.getItemByName(name), 1));
			}
		}
		if(loadingEntity.inventory.weaponStacks.length > 0) {
			loadingEntity.inventory.selectNextWeapon();
		}
		
		loadingEntity.loadSavableData(rawData);
		
		initialSpawn.add(loadingEntity);
		if(loadingEntity instanceof EntityPlayer) { 
			player = (EntityPlayer) loadingEntity;
		}
	}
	
	public EntityBase getEntityForSavable(String savable, HashMap<String, String> rawData) {
		String name = savable.split("-")[0];
		switch(name) {
			case "Player": return new EntityPlayer();
			case "Meteor": return new EntityMeteor();
			case "Enemy": return new EntityEnemy();
			case "Planet": return new EntityPlanet(rawData.get("resourceName"));
			case "Missile": return new EntityMissile(Long.parseLong(rawData.get("initialEntity")));
			case "Blaster": return new EntityBlaster(Long.parseLong(rawData.get("shooter")), Boolean.parseBoolean(rawData.get("invertShot")));
			case "Star": return new EntityStar();
			case "Item": {
				String[] stackDetail = rawData.get("stackDrop").split(",");
				ItemStack stack = new ItemStack(Item.getItemByName(stackDetail[0]), Integer.parseInt(stackDetail[1]));
				return new EntityItemDrop(stack);
			}
			case "Spawner": return new EntitySpawner(Long.parseLong(rawData.get("protectingEntity")));
		}
		return null;
	}

	@Override
	public ArrayList<String> getSavableList() {
		ArrayList<String> array = new ArrayList<>();
		for(EntityBase b : getIngameEntities()) {
			array.add(b.getType() + "-" + b.id);
		}
		return array;
	}

	@Override
	public String getSaveDir() {
		return "entity";
	}
	
	public void spawnEntity(EntityBase e) {
		spawnableEntities.add(e);
		e.spawn();
	}
	
	public void despawnEntity(EntityBase e) {
		removableEntities.add(e);
	}
	
	public EntityBase getEntityAt(Point pt, boolean removePlayer) {
		for(EntityBase e : ingameEntities) {
			if(e instanceof EntityPlayer && removePlayer) {
				continue;
			}
			if(e.collisionShape.contains(pt.x, pt.y) || e.collisionShape.includes(pt.x, pt.y)) {
				return e;
			}
		}
		return null;
	}
	
	public EntityBase getEntityByID(long id) {
		for(EntityBase b : getIngameEntities()) {
			if(b.id == id) {
				return b;
			}
		}
		return null;
	}
	
	public ArrayList<EntityBase> getIngameEntities() {
		return ingameEntities;
	}
	
	public void updateEntityList() {
		if(spawnableEntities.size() > 0) {
			ingameEntities.addAll(spawnableEntities);
			spawnableEntities.clear();
		}
		if(removableEntities.size() > 0) {
			ingameEntities.removeAll(removableEntities);
			removableEntities.clear();
		}
		Collections.sort(ingameEntities);
	}
	
	public void loadPlanetSystems(boolean spawn) {
		try {
			String dir = GameConstants.RESOURCE + "system";
			File systemFile = new File(dir);
			for(File child : systemFile.listFiles()) {
				if(child.isDirectory()) {
					BufferedReader reader = new BufferedReader(new FileReader(new File(child, "info.txt")));
					//String systemName = "";
					float x = 0, y = 0;
					int color = 0;
					while(reader.ready()) {
						String[] l = reader.readLine().split(":");
						switch(l[0]) {
							case "name": /*systemName = l[1];*/ break;
							case "xCenter": x = Float.parseFloat(l[1]); break;
							case "yCenter": y = Float.parseFloat(l[1]); break;
							case "starTint": color = Integer.parseInt(l[1], 16); break;
						}
					}
					reader.close();
					PlanetSystem system = new PlanetSystem(x, y, new Color(color));
					
					FilenameFilter filter = new FilenameFilter() {
						@Override
						public boolean accept(File dir, String name) {
							return name.endsWith(".txt") && !name.equals("info.txt");
						}
					};
					for(File planets : child.listFiles(filter)) {
						reader = new BufferedReader(new FileReader(planets));
						String planetName = null, textureName = null, textureLocation = null;
						float textureScale = -1, distanceFromCenter = 0, initialAngle = 0;
						int difficulty = 0;
						EntityPlanet planet = null;
						ExplorablePlanet explorable = null;
						while(reader.ready()) {
							String s = reader.readLine();
							if(!s.equals("region")) {
								String[] l = s.split(":");
								switch(l[0]) {
									case "name": planetName = l[1]; break;
									case "distance": distanceFromCenter = Float.parseFloat(l[1]); break;
									case "angle": initialAngle = Float.parseFloat(l[1]); break;
									case "textureName": textureName = l[1]; break;
									case "textureLocation": textureLocation = l[1]; break;
									case "textureScale": textureScale = Float.parseFloat(l[1]); break;
									case "difficulty": difficulty = Integer.parseInt(l[1]); break;
								}
								
								if(textureScale > 0) {
									TextureHandler.registerCustomImage(textureName, textureLocation, textureScale, true);
									planet = new EntityPlanet(textureName);
									planet.setID();
									explorable = new ExplorablePlanet(planet.id, planetName, difficulty);
								}
							} else {
								String regionName = reader.readLine().split(":")[1];
								String desc = reader.readLine().split(":")[1];
								int exploreTime = Integer.parseInt(reader.readLine().split(":")[1]);
								String[] yield = reader.readLine().split(":")[1].split(",");
								ArrayList<ItemStack> yieldStacksList = new ArrayList<>();
								for(int i = 0; i < yield.length-1; i+=2) {
									String itemName = yield[i];
									int amount = Integer.parseInt(yield[i+1]);
									yieldStacksList.add(new ItemStack(Item.getItemByName(itemName), amount));
								}
								ItemStack[] yieldStacks = new ItemStack[yieldStacksList.size()];
								PlanetRegion region = new PlanetRegion(regionName, desc, exploreTime, yieldStacksList.toArray(yieldStacks));
								explorable.planetRegions.add(region);
							}
						}
						system.addPlanet(planet, planetName, distanceFromCenter, initialAngle);
						if(spawn) { 
							World.planetsList.put(planet.id, explorable);
						}
					}
					if(spawn) {
						system.spawnSystem();
					}
	 			}
			}
		} catch(Exception e) {
			e.printStackTrace();
		}
	}
}
