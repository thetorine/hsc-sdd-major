package spacegame.gui.widgets;

import org.newdawn.slick.*;

import spacegame.*;
import spacegame.core.*;
import spacegame.entity.*;
import spacegame.gui.*;
import spacegame.inventory.*;

public class GuiInvSlot extends Gui {
	public int row;
	public int col;
	public boolean highlighted;
	public boolean pressed;
	public boolean pickable = true;
	public ItemStack placeHolder;
	
	public Image inv_slot = TextureHandler.uiImages.get("inv_slot.png");
	public Image inv_slot_highlight = TextureHandler.uiImages.get("inv_slot_highlight.png");
	public Image inv_slot_click = TextureHandler.uiImages.get("inv_slot_click.png");

	public GuiInvSlot(int x, int y, float scale, Gui parent, int row, int col) {
		super(TextureHandler.uiImages.get("inv_slot.png"), x, y, scale, parent);
		inv_slot = scaleImage(inv_slot, scale);
		inv_slot_highlight = scaleImage(inv_slot_highlight, scale);
		inv_slot_click = scaleImage(inv_slot_click, scale);
		background.setAlpha(0);
		
		this.row = row;
		this.col = col;
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
		
		EntityPlayer player = CoreGame.getInstance().entityManager.player;
		ItemStack slotItem = placeHolder == null ? player.inventory.getItemStackAt(row, col) : placeHolder;
		if(slotItem != null) {
			Image item = slotItem.itemClass.getResource();
			item.drawCentered(xStart+width/2, yStart+width/2 + (pressed ? 4 : 0));
			String s = ""+slotItem.quantity;
			g.setColor(Color.black);
			g.drawString(s, xStart+width/2, yStart+height/2 + (pressed ? 4 : 0));
		}
	}
	
	@Override
	public void renderForeground(Graphics g, GameContainer container) {
		super.renderForeground(g, container);
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
			if(x >= xStart && x <= xStart+width && y >= yStart && y <= yStart+height && pickable) {
				((EventListener)parentClass).onStateChange(this);
			}
		}
	}
	
	public void setSelectable(boolean b) {
		pickable = b;
	}

	public void setPlaceHolder(ItemStack s) {
		this.placeHolder = s;
	}
	
	public ItemStack getHeldStack() {
		EntityPlayer player = CoreGame.getInstance().entityManager.player;
		return placeHolder == null ? player.inventory.getItemStackAt(row, col) : placeHolder;
	}
}
