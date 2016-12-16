package spacegame.gui.screen;

import java.io.*;

import org.newdawn.slick.Color;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import spacegame.core.*;
import spacegame.gamestates.*;
import spacegame.gui.*;
import spacegame.gui.widgets.*;
import spacegame.other.*;

public class GuiMainMenu extends Gui implements EventListener {

	public GuiMainMenu() {
		super(AssetManager.uiImages.get("bg_white.png"), 1f);
		background.setAlpha(0);
		
		GuiButton resumeGame = new GuiButton("Start", 0.1f, this);
		resumeGame.xStart = (GameConstants.GAME_WIDTH-resumeGame.width)/2;
		resumeGame.yStart = (int) (GameConstants.GAME_HEIGHT/2 - 0.04f*GameConstants.GAME_WIDTH);
		guiElements.add(resumeGame);
		GuiButton closeGame = new GuiButton("Exit Game", 0.1f, this);
		closeGame.xStart = (GameConstants.GAME_WIDTH-resumeGame.width)/2;
		closeGame.yStart = (int) (GameConstants.GAME_HEIGHT/2 + 0.04f* GameConstants.GAME_HEIGHT);
		guiElements.add(closeGame);
	}

	@Override
	public void renderForeground(Graphics g, GameContainer container) {
		super.renderForeground(g, container);
		g.setColor(Color.white);
		g.setFont(GameConstants.GAME_FONT[3]);
		String str = "Made by Tarunveer Singh";
		g.drawString(str, 0.98f*GameConstants.GAME_WIDTH-g.getFont().getWidth(str), 0.98f*GameConstants.GAME_HEIGHT-g.getFont().getHeight(str));
		str = "Star Conqueror";
		g.drawString(str, (GameConstants.GAME_WIDTH-g.getFont().getWidth(str))/2, 0.35f*GameConstants.GAME_HEIGHT);
		g.setFont(GameConstants.GAME_FONT[0]);
	}

	@Override
	public void onStateChange(Gui element) {
		if (element instanceof GuiButton) {
			GuiButton button = (GuiButton) element;
			if(button.buttonName.equals("Start")) {
				StateManager.instance.enterState(1);
			} else if(button.buttonName.equals("Exit Game")) {
				try {
					IngameState.getInstance().dataHandler.saveInterfaceData();
				} catch (IOException e) {
					e.printStackTrace();
				}
				System.exit(0);
			}
		}
	}
	
}
