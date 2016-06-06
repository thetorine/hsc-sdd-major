package spacegame.inventory;

import java.util.*;

public class CraftingManager {
	public HashMap<ItemStack, CraftingRecipe> craftingRecipes = new HashMap<>();
	
	public CraftingManager() {
		addRecipe(new ItemStack(Item.rare_metal, 1), new ItemStack(Item.metal, 9));
		addRecipe(new ItemStack(Item.steelArmor, 1), new ItemStack(Item.metal, 6), new ItemStack(Item.rare_metal, 2));
		addRecipe(new ItemStack(Item.goldArmor, 1), new ItemStack(Item.rare_metal, 6), new ItemStack(Item.metal, 2));
		addRecipe(new ItemStack(Item.bronzeArmor, 1), new ItemStack(Item.metal, 3));
		addRecipe(new ItemStack(Item.flux_capacitor, 1), new ItemStack(Item.ionic_diode, 1), new ItemStack(Item.metal, 3), new ItemStack(Item.rare_metal, 1));
		addRecipe(new ItemStack(Item.ionic_diode, 1), new ItemStack(Item.metal, 3));
		addRecipe(new ItemStack(Item.gamma_burst, 1), new ItemStack(Item.flux_capacitor, 1), new ItemStack(Item.energy_casket, 1));
		addRecipe(new ItemStack(Item.energy_casket, 1), new ItemStack(Item.metal, 4)); 
	}
	
	public void addRecipe(ItemStack output, ItemStack...input) {
		craftingRecipes.put(output, new CraftingRecipe(input));
	}
	
	public boolean hasRecipeForItem(Item i) {
		for(ItemStack stack : craftingRecipes.keySet()) {
			if(stack.itemClass.equals(i)) {
				return true;
			}
		}
		return false;
	}
	
	public CraftingRecipe getRecipeFor(Item item) {
		for(ItemStack stack : craftingRecipes.keySet()) {
			if(stack.itemClass.equals(item)) {
				return craftingRecipes.get(stack);
			}
		}
		return null;
	}
	
	public static class CraftingRecipe {
		public ArrayList<ItemStack> recipeItems = new ArrayList<>();
		
		public CraftingRecipe(ItemStack...stack) {
			recipeItems.addAll(Arrays.asList(stack));
		}
		
		public boolean doesInvContainItems(Inventory inv) {
			for(ItemStack stack : recipeItems) {
				if(!inv.contains(stack)) {
					return false;
				}
			}
			return true;
		}
		
		//dont have to check if the inv has the items because the guishopmenu already does
		public void consumeItems(Inventory inv) {
			for(ItemStack stack : recipeItems) {
				ItemStack recipe = inv.findExistingStack(stack, true);
				recipe.consume(stack.quantity);
			}
		}
	}
}