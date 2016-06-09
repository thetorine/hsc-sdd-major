package spacegame.gui.screen;

import java.util.*;

import org.newdawn.slick.*;
import org.newdawn.slick.geom.*;

import spacegame.core.*;
import spacegame.gamestates.*;
import spacegame.gui.*;
import spacegame.gui.widgets.*;
import spacegame.gui.widgets.EventListener;
import spacegame.gui.widgets.GuiList.*;
import spacegame.inventory.*;
import spacegame.inventory.CraftingManager.*;
import spacegame.other.*;

public class GuiShopMenu extends Gui implements EventListener {
	
	private Rectangle mainRect;
	private GuiButton craftButton;
	private boolean drawnPurchaseButton;
	private HashMap<GuiInvSlot, ArrayList<GuiInvSlot>> invSlotsDrawn = new HashMap<>();

	public GuiShopMenu() {
		super(AssetManager.uiImages.get("bg_green.png"), 0.4f);
		setBackgroundTint();
		GuiList list = new GuiList((int)(0.05f*width), (int)(0.15f*height), 0.32f, this);
		ArrayList<ListData> data = new ArrayList<>();
		for(Item item : Item.loadedItems) {
			if(IngameState.getInstance().craftingManager.hasRecipeForItem(item)) {
				data.add(new ListData(item.itemName));
			}
		}
		list.addRows(data);
		guiElements.add(list);
	}
	
	@Override
	public void renderBackground(Graphics g, GameContainer container) {
		super.renderBackground(g, container);
		int listYStart = 0, listXEnd = 0;
		GuiList menuList = null;
		for(Gui elements : guiElements) {
			if(elements instanceof GuiList) {
				listYStart = elements.yStart;
				listXEnd = elements.xStart+elements.width;
				menuList = (GuiList) elements;
			}
		}
		
		g.setFont(GameConstants.GAME_FONT[3]);
		g.drawString("Shop", xStart+(width-g.getFont().getWidth("Shop"))/2, yStart+0.07f*height);
		g.setFont(GameConstants.GAME_FONT[0]);
		mainRect = new Rectangle(listXEnd+0.05f*width, listYStart+0.04f*height, width-(listXEnd-xStart)-0.1f*width, height-(listYStart-yStart)-0.13f*height);
		g.fill(mainRect);
		 
		if(!drawnPurchaseButton) {
			craftButton = new GuiButton("Purchase", 0, 0, 0.07f, this);
			craftButton.xStart = (int) (mainRect.getMinX() + (mainRect.getWidth()-craftButton.width)/2);
			craftButton.yStart = (int) (mainRect.getMinY() + 0.8f*mainRect.getHeight());
			addGuiElement(craftButton);
			drawnPurchaseButton = true;
			
			onStateChange(menuList);
		}
		
		if(invSlotsDrawn.keySet().iterator().hasNext()) {
			GuiInvSlot outputSlot = invSlotsDrawn.keySet().iterator().next();
			if(outputSlot != null) {
				ArrayList<GuiInvSlot> inputSlots = invSlotsDrawn.get(outputSlot);
				int totalWidth = (int) (outputSlot.width*inputSlots.size() + 0.1f*mainRect.getWidth()*(inputSlots.size()-1));
				int x = (int) (mainRect.getMinX() +  (mainRect.getWidth()-totalWidth)/2);
				g.setColor(Color.black);
				g.setLineWidth(2);
				g.drawLine(outputSlot.xStart + outputSlot.width*0.5f, outputSlot.yStart + outputSlot.height, outputSlot.xStart + outputSlot.width*0.5f, outputSlot.yStart + outputSlot.height + 0.2f*mainRect.getWidth());
				g.drawLine(x+0.5f*outputSlot.width, outputSlot.yStart + outputSlot.height + 0.2f*mainRect.getWidth(), x+totalWidth-0.5f*outputSlot.width, outputSlot.yStart + outputSlot.height + 0.2f*mainRect.getWidth());
				
				for(GuiInvSlot slot : inputSlots) {
					if(!IngameState.getInstance().entityManager.player.inventory.contains(slot.getHeldStack())) g.setColor(Color.red);
					g.drawLine(slot.xStart+0.5f*outputSlot.width, outputSlot.yStart + outputSlot.height + 0.2f*mainRect.getWidth(), slot.xStart+0.5f*outputSlot.width, slot.yStart);
				}
			}
		}
		g.setColor(Color.black);
		
		ListData data = menuList.pluggedData.get(menuList.selectedIndex);
		ItemStack stack = new ItemStack(Item.getItemByName(data.displayName), 1);	
		CraftingRecipe recipe = IngameState.getInstance().craftingManager.getRecipeFor(stack.itemClass);
		if(recipe.doesInvContainItems(IngameState.getInstance().entityManager.player.inventory)) {
			craftButton.buttonName = "Purchase";
		} else {
			craftButton.buttonName = "Not Enough Items";
		}
	}
	
	@Override
	public void renderForeground(Graphics g, GameContainer container) {
		super.renderForeground(g, container);
		for(Gui gui : guiElements) {
			if (gui instanceof GuiInvSlot) {
				GuiInvSlot slot = (GuiInvSlot) gui;
				if(slot.highlighted) {
					drawInfoBoxAtMousePos(g, container, slot.getHeldStack().itemClass.itemName);
				}
			}
		}
	}

	@Override
	public void onStateChange(Gui element) {
		if(element instanceof GuiList) {
			for(Gui gui : guiElements) {
				if(gui instanceof GuiInvSlot) {
					removeGuiElement(gui);
				}
			}
			
			if(mainRect != null) {
				invSlotsDrawn.clear();
				GuiList list = (GuiList) element;
				ListData data = list.pluggedData.get(list.selectedIndex);
				ItemStack stack = new ItemStack(Item.getItemByName(data.displayName), 1);
				GuiInvSlot output = new GuiInvSlot(0, 0, 0.08f, this, 0, 0);
				output.setPlaceHolder(stack);
				output.xStart = (int) (mainRect.getMinX() + (mainRect.getWidth()-output.width)/2);
				output.yStart = (int) (mainRect.getMinY() + 0.15f*mainRect.getHeight());
				addGuiElement(output);
				
				CraftingRecipe recipe = IngameState.getInstance().craftingManager.getRecipeFor(stack.itemClass);
				int totalWidth = (int) (output.width*recipe.recipeItems.size() + 0.1f*mainRect.getWidth()*(recipe.recipeItems.size()-1));
				int x = (int) (mainRect.getMinX() - xStart + (mainRect.getWidth()-totalWidth)/2);
				ArrayList<GuiInvSlot> inputSlots = new ArrayList<>();
				for(int i = 0; i < recipe.recipeItems.size(); i++) {
					ItemStack rStack = recipe.recipeItems.get(i);
					GuiInvSlot slot = new GuiInvSlot((int) (x+output.width*i+0.1f*mainRect.getWidth()*i), (int) (output.yStart - yStart + 0.4f*mainRect.getHeight()), 0.08f, this, 0, 0);
					slot.setPlaceHolder(rStack);
					addGuiElement(slot);
					inputSlots.add(slot);
				}
				
				invSlotsDrawn.put(output, inputSlots);
			}
		} else if(element instanceof GuiButton) {
			GuiButton craftButton = (GuiButton) element;
			if(craftButton.buttonName.equals("Purchase")) {
				GuiList itemList = null;
				for(Gui gui : guiElements) {
					if(gui instanceof GuiList) {
						itemList = (GuiList) gui;
					}
				}
				if(itemList != null) {
					ListData data = itemList.pluggedData.get(itemList.selectedIndex);
 					ItemStack stack = new ItemStack(Item.getItemByName(data.displayName), 1);
					CraftingRecipe recipe = IngameState.getInstance().craftingManager.getRecipeFor(stack.itemClass);
					Inventory inv = IngameState.getInstance().entityManager.player.inventory;
					if(recipe.doesInvContainItems(inv)) {
						inv.addItemStack(stack);
						recipe.consumeItems(inv);
					}
				}
			}
		}
	}
}