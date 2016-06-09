package spacegame.gui.screen;

import org.newdawn.slick.*;

import spacegame.core.*;
import spacegame.gamestates.*;
import spacegame.gui.*;
import spacegame.gui.widgets.*;
import spacegame.other.*;

public class GuiControls extends Gui implements EventListener {
	
	public GuiButton backButton;

	public GuiControls() {
		super(AssetManager.uiImages.get("bg_white.png"), 0.3f);
		setBackgroundTint();
		backButton = new GuiButton("Back", 0.07f, this);
		addGuiElement(backButton);
	}
	
	@Override
	public void renderForeground(Graphics g, GameContainer container) {
		super.renderForeground(g, container);
		float lastHeight = yStart+0.04f*GameConstants.GAME_HEIGHT;
		float strX = this.xStart + 0.02f*GameConstants.GAME_WIDTH;
		float maxWidth = this.width - 0.04f*GameConstants.GAME_WIDTH;
		
		g.setColor(Color.black);
		g.setFont(GameConstants.GAME_FONT[1]);
		lastHeight = wrapText("Keyboard Controls", strX, lastHeight, maxWidth, g);
		g.setFont(GameConstants.GAME_FONT[0]);
		strX += 0.01f*GameConstants.GAME_WIDTH;
		lastHeight = wrapText("Movement: W A S D", strX, lastHeight, maxWidth, g);
		lastHeight = wrapText("Fire Weapon: Space", strX, lastHeight, maxWidth, g);
		lastHeight = wrapText("Cycle Weapons: Q", strX, lastHeight, maxWidth, g);
		lastHeight = wrapText("Orbital Map: M", strX, lastHeight, maxWidth, g);
		lastHeight = wrapText("Inventory: I", strX, lastHeight, maxWidth, g);
		lastHeight = wrapText("Shop: P", strX, lastHeight, maxWidth, g);
		lastHeight = wrapText("Upgrade Menu: U", strX, lastHeight, maxWidth, g);
		lastHeight = wrapText("Map Controls: Z Zoom In, X Zoom Out", strX, lastHeight, maxWidth, g);
		lastHeight = wrapText("Memory Usage: Tab", strX, lastHeight, maxWidth, g);
		
		backButton.xStart = (int) strX;
		backButton.yStart = (int) lastHeight;
	}

	@Override
	public void onStateChange(Gui element) {
		if (element instanceof GuiButton) {
			GuiButton backB	= (GuiButton) element;
			if(backB.buttonName.equals("Back")) {
				IngameState.getInstance().guiHierarchy.loadPreviousGui();
			}
		}
	}

}
