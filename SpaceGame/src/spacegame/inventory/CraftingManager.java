package spacegame.inventory;

import java.util.*;

public class CraftingManager {
	public HashMap<ItemStack, CraftingRecipe> craftingRecipes = new HashMap<>();
	
	public CraftingManager() {
		addRecipe(new ItemStack(Item.steelArmor, 1), new ItemStack(Item.metal, 10), new ItemStack(Item.rare_metal, 4), new ItemStack(Item.bronzeArmor, 1));
		addRecipe(new ItemStack(Item.goldArmor, 1), new ItemStack(Item.rare_metal, 12), new ItemStack(Item.metal, 2));
		addRecipe(new ItemStack(Item.bronzeArmor, 1), new ItemStack(Item.metal, 3));
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
	}
}