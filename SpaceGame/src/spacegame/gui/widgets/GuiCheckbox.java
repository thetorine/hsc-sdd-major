package spacegame.gui.widgets;

import org.newdawn.slick.*;

import spacegame.core.*;
import spacegame.gui.*;

public class GuiCheckbox extends Gui {

	public Image baseImage = AssetManager.uiImages.get("checkbox.png");
	public Image selectImage = AssetManager.uiImages.get("checkbox_activate.png");
	public boolean selected;
	
	public GuiCheckbox(int x, int y, float scale, Gui parent, boolean b) {
		super(AssetManager.uiImages.get("checkbox.png"), x, y, scale, parent);
		baseImage = scaleImage(AssetManager.uiImages.get("checkbox.png"), scale);
		selectImage = scaleImage(AssetManager.uiImages.get("checkbox_activate.png"), scale);
		selected = b;
	}
	
	@Override
	public void render(Graphics g, GameContainer container) {
		g.drawImage(selected ? selectImage : baseImage, xStart, yStart);
	}

	@Override
	public void mousedPressed(int button, int x, int y) {
		super.mousedPressed(button, x, y);
		if(x > xStart && x < xStart+width && y > yStart && y < yStart+height) {
			selected ^= true;
			if(parentClass instanceof EventListener) {
				((EventListener) parentClass).onStateChange(this);
			}
		}
	}
	

}
