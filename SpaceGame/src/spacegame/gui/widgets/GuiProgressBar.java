package spacegame.gui.widgets;

import org.newdawn.slick.*;

import spacegame.core.*;
import spacegame.gui.*;

public class GuiProgressBar extends Gui {
	
	public Image percentBar = TextureHandler.uiImages.get("element_select.png");
	public float percentageFinished;

	public GuiProgressBar(int x, int y, float scale, Gui parent) {
		super(TextureHandler.uiImages.get("bar_base.png"), x, y, scale, parent);
		percentBar = scaleImage(percentBar, scale);
	}
	
	@Override
	public void renderForeground(Graphics g, GameContainer container) {
		super.renderForeground(g, container);
		g.drawImage(percentBar, xStart, yStart, xStart+width*percentageFinished, yStart+height, 0, 0, width*percentageFinished, height);
	}

	public void setPercentage(float f) {
		this.percentageFinished = f;
	}
	
}
