package spacegame.gui.widgets;

import org.newdawn.slick.*;

import spacegame.core.*;
import spacegame.gui.*;

public class GuiButton extends Gui {
	
	public Image baseImage = TextureHandler.uiImages.get("button_base.png");
	public Image highlightImage = TextureHandler.uiImages.get("button_highlight.png");
	public Image selectImage = TextureHandler.uiImages.get("button_pressed.png");
	public boolean highlighted;
	public boolean selected;
	public String buttonName;
	
	//draw button based on coordinates supplied.
	public GuiButton(String name, int x, int y, float scale, Gui parent) {
		super(TextureHandler.uiImages.get("button_base.png"), x, y, scale, parent);
		baseImage = scaleImage(TextureHandler.uiImages.get("button_base.png"), scale);
		highlightImage = scaleImage(TextureHandler.uiImages.get("button_highlight.png"), scale);
		selectImage = scaleImage(TextureHandler.uiImages.get("button_pressed.png"), scale);
		
		buttonName = name;
	}
	
	//draw button based on parent gui center
	public GuiButton(String name, float scale, Gui parent) {
		super(TextureHandler.uiImages.get("button_base.png"), scale, parent);
		baseImage = scaleImage(TextureHandler.uiImages.get("button_base.png"), scale);
		highlightImage = scaleImage(TextureHandler.uiImages.get("button_highlight.png"), scale);
		selectImage = scaleImage(TextureHandler.uiImages.get("button_pressed.png"), scale);
		
		buttonName = name;
	}
	
	@Override
	public void render(Graphics g, GameContainer container) {
		if(highlighted) {
			if(selected) {
				g.drawImage(selectImage, xStart, yStart+4);
			} else {
				g.drawImage(highlightImage, xStart, yStart);
			}
		} else {
			g.drawImage(baseImage, xStart, yStart);
		}
		renderForeground(g, container);
	}

	@Override
	public void renderForeground(Graphics g, GameContainer container) {
		g.setColor(highlighted ? Color.white : Color.darkGray);
		g.drawString(buttonName, xStart+(width-g.getFont().getWidth(buttonName))/2, yStart+(height-g.getFont().getHeight(buttonName))/2+(selected ? 4 : 0));
	}
	
	@Override
	public void mousedPressed(int button, int x, int y) {
		super.mousedPressed(button, x, y);
		selected = highlighted;
	}
	
	@Override
	public void mouseReleased(int button, int x, int y) {
		super.mouseReleased(button, x, y);
		if(parentClass instanceof EventListener && selected) {
			EventListener el = (EventListener) parentClass;
			el.onStateChange(this);
		}
		selected = false;
	}
	
	@Override
	public void mouseMoved(int oldX, int oldY, int newX, int newY) {
		super.mouseMoved(oldX, oldY, newX, newY);
		highlighted = newX >= xStart && newX <= xStart+width && newY >= yStart && newY <= yStart+height;
	}
}
