package spacegame.gui.screen;

import org.newdawn.slick.*;

import spacegame.core.*;
import spacegame.entity.*;
import spacegame.gamestates.*;
import spacegame.gui.*;
import spacegame.gui.widgets.*;
import spacegame.inventory.*;
import spacegame.other.*;

public class GuiInventory extends Gui implements EventListener {
	
	public Image inv_slot = AssetManager.uiImages.get("inv_slot.png");
	public HeldItem currentHeldItem;

	public GuiInventory() {
		super(AssetManager.uiImages.get("bg_yellow.png"), 0.33f);
		setBackgroundTint();
		
		inv_slot = scaleImage(inv_slot, 0.1f);
		
		int widthShift = (width-inv_slot.getWidth()*Inventory.COLUMNS)/2;
		int heightShift = (height-inv_slot.getHeight()*Inventory.ROWS)/2;
		
		for(int i = 0; i < Inventory.ROWS; i++) {
			for(int j = 0; j < Inventory.COLUMNS; j++) {
				int xStart = j*inv_slot.getWidth()+widthShift;
				int yStart = i*inv_slot.getHeight()+heightShift;
				GuiInvSlot slot = new GuiInvSlot(xStart, yStart, 0.1f, this, i, j);
				guiElements.add(slot);
			}
		}
		
		for(int i = 0; i < Inventory.WEAPON_COLUMNS; i++) {
			int xStart = (i+1)*inv_slot.getWidth()+widthShift;
			int yStart = (Inventory.COLUMNS-2)*inv_slot.getWidth()+heightShift;
			GuiEquipSlot slot = new GuiEquipSlot(xStart, yStart, 0.1f, this, IngameState.getInstance().entityManager.player, i);
			guiElements.add(slot);
		}
	}
	
	@Override
	public void renderBackground(Graphics g, GameContainer container) {
		super.renderBackground(g, container);
		g.setFont(GameConstants.GAME_FONT[3]);
		g.drawString("Inventory", xStart+(width-g.getFont().getWidth("Inventory"))/2, yStart+0.07f*height);
		g.setFont(GameConstants.GAME_FONT[0]);
	}
	
	@Override
	public void renderForeground(Graphics g, GameContainer container) {
		super.renderForeground(g, container);
		
		if(currentHeldItem != null) {
			g.setFont(GameConstants.GAME_FONT[0]);
			g.setColor(Color.black);
			currentHeldItem.stack.itemClass.getResource().drawCentered(container.getInput().getMouseX(), container.getInput().getMouseY());
			String amount = String.valueOf(currentHeldItem.stack.quantity);
			g.drawString(amount, container.getInput().getMouseX()+10, container.getInput().getMouseY());
		}
		
		String info = "";
		for(Gui element : guiElements) {
			if (element instanceof GuiInvSlot) {
				GuiInvSlot invSlot = (GuiInvSlot) element;
				if(invSlot.highlighted) {
					ItemStack stack = invSlot.getHeldStack();
					if (stack != null) {
						info = stack.itemClass.itemName;
					}
				}
			} else if(element instanceof GuiEquipSlot) {
				GuiEquipSlot invSlot = (GuiEquipSlot) element;
				if(invSlot.highlighted) {
					ItemStack stack = invSlot.getHeldStack();
					if (stack != null) {
						info = stack.itemClass.itemName;
					}
				}
			}
		}
		if(info.length() > 0) {
			drawInfoBoxAtMousePos(g, container, info);
		}
	}
	
	@Override
	public void onStateChange(Gui element) {
		EntityPlayer player = IngameState.getInstance().entityManager.player;
		if(element instanceof GuiInvSlot) {
			GuiInvSlot slot = (GuiInvSlot) element;
			ItemStack slotItem = player.inventory.getItemStackAt(slot.row, slot.col);
			
			if(currentHeldItem != null) {
				//hand not empty and the slot is not empty, tries to combine, returning extra back to hand.
				if(currentHeldItem.stack.equals(slotItem)) {
					ItemStack remainStack = player.inventory.addStackAt(currentHeldItem.stack, slot.row, slot.col);
					if(remainStack != null) {
						currentHeldItem.stack = remainStack;
					} else {
						currentHeldItem = null;
					}
				//simply switch the items.
				} else { 
					ItemStack switchedStack = player.inventory.replaceStackAt(currentHeldItem.stack, slot.row, slot.col);
					if(switchedStack != null) {
						currentHeldItem = new HeldItem(switchedStack, slot.row, slot.col);
					} else {
						currentHeldItem = null;
					}
				}
			} else {
				//nothing in hand, just pick the current slot up.
				if(slotItem != null) {
					player.inventory.removeItemStackAt(slot.row, slot.col);
					currentHeldItem = new HeldItem(slotItem, slot.row, slot.col);
				} 
			}
		} else if(element instanceof GuiEquipSlot) {
			GuiEquipSlot slot = (GuiEquipSlot) element;
			ItemStack slotItem = player.inventory.getWeaponStackAt(slot.col);

			if(currentHeldItem != null) {
				//add item to slot
				if(slotItem == null && currentHeldItem.stack.itemClass instanceof ItemWeapon) {
					player.inventory.weaponStacks[slot.col] = currentHeldItem.stack;
					currentHeldItem = null;
					player.inventory.selectNextWeapon();
				}
				//swap the items
				else if(slotItem != null && currentHeldItem.stack.itemClass instanceof ItemWeapon) {
					player.inventory.weaponStacks[slot.col] = currentHeldItem.stack;
					currentHeldItem = new HeldItem(slotItem, 0, slot.col);
					player.inventory.selectNextWeapon();
				}
			} else if(slotItem != null) {
				player.inventory.weaponStacks[slot.col] = null;
				currentHeldItem = new HeldItem(slotItem, 0, slot.col);
				player.inventory.selectNextWeapon();
			}
		}
	}
	
	public static class HeldItem {
		public ItemStack stack;
		public int row;
		public int column;
		
		public HeldItem(ItemStack is, int r, int c) {
			this.stack = is;
			this.row = r;
			this.column = c;
		}
	}
}
