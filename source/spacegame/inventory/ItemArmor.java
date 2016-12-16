package spacegame.inventory;

public class ItemArmor extends Item {
	public int armor;

	public ItemArmor(String name, int armor) {
		super(name);
		this.armor = armor;
		setMaxQuantity(1);
	}
}
