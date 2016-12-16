package spacegame.inventory;

public class ItemStack {
	public Item itemClass;
	public int quantity;
	public int row;
	public int column;
	
	public ItemStack(Item i, int initialQuanity) {
		this.itemClass = i;
		this.quantity = initialQuanity;
	}
	
	//returns leftovers as a new stack
	public ItemStack joinStacks(ItemStack stack) {
		if(stack.itemClass.equals(itemClass)) {
			int remainder = quantity+stack.quantity-itemClass.maxQuantity;
			this.add(stack.quantity);
			if(remainder > 0) {
				return new ItemStack(itemClass, remainder);
			} 
			return null;
		}
		return stack;
	}
	
	public boolean containsItem(ItemStack stack) {
		return stack.itemClass.equals(itemClass);
	}
	
	public void add(int amount) {
		quantity = Math.min(itemClass.maxQuantity, quantity+amount);
	}
	
	public void consume(int amount) {
		quantity = Math.max(0, quantity-amount);
	}
	
	public void setStackLocation(int row, int column) {
		this.row = row;
		this.column = column;
	}
	
	public boolean isFull() {
		return quantity == itemClass.maxQuantity;
	}
	
	@Override
	public boolean equals(Object obj) {
		if(obj instanceof ItemStack) {
			return ((ItemStack) obj).itemClass.equals(itemClass);
		}
		return false;
	}
}
