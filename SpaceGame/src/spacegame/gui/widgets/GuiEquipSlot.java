package spacegame.gui.widgets;

import org.newdawn.slick.*;

import spacegame.core.*;
import spacegame.entity.*;
import spacegame.gui.*;
import spacegame.inventory.*;

public class GuiEquipSlot extends Gui {
	public int col;
	public boolean highlighted;
	public boolean pressed;
	public EntityBase entityInv;
	
	public Image inv_slot = TextureHandler.uiImages.get("inv_slot.png");
	public Image inv_slot_highlight = TextureHandler.uiImages.get("inv_slot_highlight.png");
	public Image inv_slot_click = TextureHandler.uiImages.get("inv_slot_click.png");

	public GuiEquipSlot(int x, int y, float scale, Gui parent, EntityBase entity, int col) {
		super(TextureHandler.uiImages.get("inv_slot.png"), x, y, scale, parent);
		inv_slot = scaleImage(inv_slot, scale);
		inv_slot_highlight = scaleImage(inv_slot_highlight, scale);
		inv_slot_click = scaleImage(inv_slot_click, scale);
		background.setAlpha(0);
		
		this.col = col;
		this.entityInv = entity;
	}
	
	@Override
	public void renderBackground(Graphics g, GameContainer container) {
		super.renderBackground(g, container);
		if(pressed) {
			g.drawImage(inv_slot_click, xStart, yStart+4);
		} else if(highlighted) {
			g.drawImage(inv_slot_highlight, xStart, yStart);
		} else {
			g.drawImage(inv_slot, xStart, yStart);
		}
		
		if(entityInv != null) {
			ItemStack slotItem = entityInv.inventory.getWeaponStackAt(col);
			if(slotItem != null) {
				Image item = slotItem.itemClass.getResource();
				item.drawCentered(xStart+width/2, yStart+width/2 + (pressed ? 4 : 0));
				String s = ""+slotItem.quantity;
				g.setColor(Color.black);
				g.drawString(s, xStart+width/2, yStart+height/2 + (pressed ? 4 : 0));
			}
		}
	}
	
	@Override
	public void mouseMoved(int oldX, int oldY, int newX, int newY) {
		super.mouseMoved(oldX, oldY, newX, newY);
		highlighted = newX >= xStart && newX <= xStart+width && newY >= yStart && newY <= yStart+height;
	}
	
	@Override
	public void mousedPressed(int button, int x, int y) {
		super.mousedPressed(button, x, y);
		pressed = x >= xStart && x <= xStart+width && y >= yStart && y <= yStart+height;
	}
	
	@Override
	public void mouseReleased(int button, int x, int y) {
		super.mouseReleased(button, x, y);
		pressed = false;
		if(parentClass != null && parentClass instanceof EventListener && button == Input.MOUSE_LEFT_BUTTON) {
			if(x >= xStart && x <= xStart+width && y >= yStart && y <= yStart+height) {
				((EventListener)parentClass).onStateChange(this);
			}
		}
	}
	
	public ItemStack getHeldStack() {
		return entityInv.inventory.getWeaponStackAt(col);
	}
}
