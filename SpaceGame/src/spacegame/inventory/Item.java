package spacegame.inventory;

import java.util.*;

import org.newdawn.slick.*;

import spacegame.core.*;

public class Item {
	public String itemName;
	public int maxQuantity;
	public int rotation;
	public long id;
	public String resourceName;
	public float scale;
	private Image resource;
	
	public static ArrayList<Item> loadedItems = new ArrayList<>();
	
	public static ItemWeapon blaster = (ItemWeapon) new ItemWeapon("Photon Blaster", 50, 200).setMaxQuantity(1).setResource("gun09.png", 0.8f, 315);
	public static ItemWeapon missile = (ItemWeapon) new ItemWeapon("Missile Launcher", 100, 1).setMaxQuantity(1).setResource("gun06.png", 0.8f, 315);
	
	public static Item metal = new Item("Scrap Metal").setResource("things_silver.png", 1f, 0).setMaxQuantity(100);
	public static Item rare_metal = new Item("Rare Metal").setResource("things_gold.png", 1f, 0).setMaxQuantity(25);
	
	public static ItemArmor goldArmor = (ItemArmor) new ItemArmor("Gold Hull", 90).setResource("shield_gold.png", 1f, 0);
	public static ItemArmor steelArmor = (ItemArmor) new ItemArmor("Steel Hull", 70).setResource("shield_silver.png", 1f, 0);
	public static ItemArmor bronzeArmor = (ItemArmor) new ItemArmor("Bronze Hull", 50).setResource("shield_bronze.png", 1f, 0);
	
	public Item(String name) {
		this.itemName = name;
		this.setid();
		loadedItems.add(this);
	}
	
	public Item setMaxQuantity(int q) {
		this.maxQuantity = q;
		return this;
	}
	
	public void setid() {
		this.id = Math.abs(new Random().nextLong());
	}
	
	public Item setResource(String fileName, float scale, int rotation) {
		this.resource = TextureHandler.getImageByName(TextureHandler.baseSheet, fileName, scale);
		this.resource.setRotation(rotation);
		this.resourceName = fileName;
		this.scale = scale;
		this.rotation = rotation;
		return this;
	}
	
	public Image getResource() {
		if(resource == null) {
			this.resource = TextureHandler.getImageByName(TextureHandler.baseSheet, resourceName, scale);
			this.resource.setRotation(rotation);
		}
		return resource;
	}
	
	public static Item getItemByName(String name) {
		for(Item item : loadedItems) {
			if(item.itemName.equals(name)) {
				return item;
			}
		}
		return null;
	}
	
	@Override
	public boolean equals(Object obj) {
		return ((Item)obj).id == id;
	}
}
