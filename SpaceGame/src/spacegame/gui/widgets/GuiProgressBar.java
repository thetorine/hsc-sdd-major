package spacegame.gui.widgets;

import org.newdawn.slick.*;

import spacegame.core.*;
import spacegame.gui.*;
import spacegame.other.*;

public class GuiProgressBar extends Gui {
	
	public Image percentBar = TextureHandler.uiImages.get("element_select.png");
	public float percentageFinished;
	public Color colorFilter;
	public String text = "";

	public GuiProgressBar(int x, int y, float scale, Gui parent) {
		super(TextureHandler.uiImages.get("bar_base.png"), x, y, scale, parent);
		percentBar = scaleImage(percentBar, scale);
	}
	
	@Override
	public void renderForeground(Graphics g, GameContainer container) {
		super.renderForeground(g, container);
		if(colorFilter != null) {
			g.drawImage(percentBar, xStart, yStart, xStart+width*percentageFinished, yStart+height, 0, 0, width*percentageFinished, height, colorFilter);
		} else {
			g.drawImage(percentBar, xStart, yStart, xStart+width*percentageFinished, yStart+height, 0, 0, width*percentageFinished, height);
		}
		
		g.setFont(GameConstants.GAME_FONT[0]);
		g.drawString(text, xStart+(width-g.getFont().getWidth(text))/2, yStart+(height-g.getFont().getHeight(text))/2);
	}

	public void setPercentage(float f) {
		this.percentageFinished = f;
	}
	
	public void setColorFilter(float r, float g, float b) {
		colorFilter = new Color(r, g, b);
	}
	
	public void setText(String s) {
		text = s;
	}
	
}
