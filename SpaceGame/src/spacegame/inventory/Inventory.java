package spacegame.inventory;

import spacegame.entity.EntityBase;
import spacegame.entity.EntityPlayer;

public class Inventory {
	public static int WEAPON_COLUMNS = 4;
	public static int ROWS = 4;
	public static int COLUMNS = 6;
	
	public ItemStack[] weaponStacks = new ItemStack[WEAPON_COLUMNS];
	public ItemStack[][] main = new ItemStack[ROWS][COLUMNS];
	
	public ItemStack selectedWeapon;
	public int weaponCD;
	public int weaponid = -1;

	public EntityBase entityInv;

	public Inventory(EntityBase entity) {
		this.entityInv = entity;
	}
	
	public void update() {
		for(int row = 0; row < ROWS; row++) {
			for(int col = 0; col < COLUMNS; col++) {
				if(main[row][col] != null) {
					if(main[row][col].quantity == 0) {
						main[row][col] = null;
					}
				}
			}
		}
	}
	
	//this entire class does not account for a completely full inventory. 
	public void addItemStack(ItemStack stack) {
		//find existing stack
		//if exists then join stacks and then add the remainder stack to somewhere else, 
		//however here have to account for the remainder > max quantity
		ItemStack potentialStack = findExistingStack(stack, false);
		if(potentialStack != null) {
			ItemStack remainder = potentialStack.joinStacks(stack);
			if(remainder != null) {
				if(remainder.quantity > remainder.itemClass.maxQuantity) {
					//have to divide the stacks so that each one does not exceed the max quantity.
					divideAndAddStacks(remainder);
				} else {
					addStackToAvailablePosition(remainder);
				}
			}
		} else {
			divideAndAddStacks(stack);
		}
	}
	
	public void divideAndAddStacks(ItemStack stack) {
		int noOfFullStacks = stack.quantity/stack.itemClass.maxQuantity;
		int lastStackSize = stack.quantity%stack.itemClass.maxQuantity;
		for(int i = 0; i < noOfFullStacks; i++) {
			addStackToAvailablePosition(new ItemStack(stack.itemClass, stack.itemClass.maxQuantity));
		}
		if(lastStackSize > 0) {
			addStackToAvailablePosition(new ItemStack(stack.itemClass, lastStackSize));
		}
	}
	
	public void addWeaponStack(ItemStack stack) {
		boolean alreadyContained = false;
		for(ItemStack is : weaponStacks) {
			if(is != null && is.itemClass == stack.itemClass) {
				alreadyContained = true;
				break;
			}
		}
		if(!alreadyContained) {
			addWeaponToAvailablePosition(stack);
		}
	}
	
	//one day account for full inventory
	public void addStackToAvailablePosition(ItemStack stack) {
		for(int row = 0; row < ROWS; row++) {
			for(int col = 0; col < COLUMNS; col++) {
				if(main[row][col] == null) {
					stack.setStackLocation(row, col);
					main[row][col] = stack;
					return;
				}
			}
		}
	}
	
	public ItemStack replaceStackAt(ItemStack stack, int row, int col) {
		ItemStack currentStack = getItemStackAt(row, col);
		main[row][col] = stack;
		if(stack != null) {
			stack.setStackLocation(row, col);
		}
		return currentStack;
	}
	
	public void addWeaponToAvailablePosition(ItemStack stack) {
		for(int col = 0; col < WEAPON_COLUMNS; col++) {
			if(weaponStacks[col] == null) {
				weaponStacks[col] = stack;
				return;
			}
		}
	}
	
	public ItemStack addStackAt(ItemStack stack, int row, int column) {
		ItemStack existingStack = getItemStackAt(row, column);
		if(existingStack != null) {
			ItemStack remainingStack = existingStack.joinStacks(stack);
			return remainingStack;
		} else {
			stack.setStackLocation(row, column);
			main[row][column] = stack;
			return null;
		}
	}
	
	public void removeItemStackAt(int row, int column) {
		main[row][column] = null;
	}
	
	public ItemStack getItemStackAt(int row, int no) {
		return main[row][no];
	}
	
	public ItemStack getWeaponStackAt(int column) {
		return weaponStacks[column];
	}
	
	public ItemStack findExistingStack(ItemStack stack, boolean canBeFull) {
		for(int row = 0; row < ROWS; row++) {
			for(int col = 0; col < COLUMNS; col++) {
				ItemStack stackAtLocation = getItemStackAt(row, col);
				if(stackAtLocation != null) {
					if(stackAtLocation.equals(stack)) {
						if(!canBeFull && !stackAtLocation.isFull()) {
							return stackAtLocation;
						} else if(canBeFull) {
							return stackAtLocation;
						}
					}
				}
			}
		} 
		return null;
	}
	
	public boolean contains(ItemStack stack) {
		ItemStack is = findExistingStack(stack, true);
		if(is != null) {
			return is.quantity >= stack.quantity;
		}
		return false;
	}
	
	public void selectNextWeapon() {
		if(weaponid == -1) {
			weaponid = 0;
		} else {
			if(weaponid == weaponStacks.length-1) {
				weaponid = 0;
			} else {
				weaponid++;
				while(weaponStacks[weaponid] == null) {
					selectNextWeapon();
				}
			}
		}
		selectedWeapon = weaponStacks[weaponid];
	}
	
	public void selectWeaponByID(int id) {
		if(weaponStacks[id] != null) {
			selectedWeapon = weaponStacks[id];
			weaponid = id;
		}
	}
	
	public int calculateTotalArmor() {
		int armor = 0;
		for(int row = 0; row < ROWS; row++) {
			for(int col = 0; col < COLUMNS; col++) {
				ItemStack stack = getItemStackAt(row, col);
				if(stack != null && stack.itemClass instanceof ItemArmor) { 
					armor += ((ItemArmor)stack.itemClass).armor;
				}
			}
		}
		if(entityInv instanceof EntityPlayer) {
			EntityPlayer player = (EntityPlayer) entityInv;
			armor += player.upgradeManager.getUpgrade("Armor").getModifier();
		}
		return armor;
	}
}
