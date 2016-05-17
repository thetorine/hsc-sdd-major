package spacegame.entity;

import java.util.*;

import spacegame.core.CollisionDetector.*;
import spacegame.inventory.*;

public class EntityItemDrop extends EntityBase implements ICollisionDetection {
	
	public String itemResource;
	public ItemStack stackDrop;
	public float currentScale;
	
	@Override
	public void update(int delta) {
		int timer = (int) (System.currentTimeMillis()%1000);
		double coefficient = 2*Math.PI/1000;
		double sin = 0.15d*Math.sin(coefficient*timer);
		currentScale = (float) (0.7f + sin);
		model = stackDrop.itemClass.getResource().getScaledCopy(currentScale);
	}
	
	public EntityItemDrop(ItemStack stack) {
		this.stackDrop = stack;
		this.itemResource = stack.itemClass.resourceName;
	}

	@Override
	public EntityType getType() {
		return EntityType.Item;
	}

	@Override
	public void setSpawnCoords() {
	}

	@Override
	public int getMaxVelocity() {
		return 0;
	}

	@Override
	public int getAcceleration() {
		return 0;
	}

	@Override
	public void setModel() {
		model = stackDrop.itemClass.getResource();
	}

	@Override
	public String getModelName() {
		return itemResource;
	}

	@Override
	public void onLoad() {
	}

	@Override
	public int getRenderWeight() {
		return 2;
	}
	
	@Override
	public void addSavableData(HashMap<String, Object> savableMap) {
		savableMap.put("stackDrop", String.format("%s,%d", stackDrop.itemClass.itemName, stackDrop.quantity));
	}

	@Override
	public void onCollision(EntityBase collisionWith) {
		if(collisionWith instanceof EntityPlayer) {
			collisionWith.inventory.addItemStack(stackDrop);
			getManager().despawnEntity(this);
		}
	}
}
