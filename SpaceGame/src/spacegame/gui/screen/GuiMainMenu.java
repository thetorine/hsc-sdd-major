package spacegame.gui.screen;

import java.io.*;

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
		resumeGame.yStart = (int) (GameConstants.GAME_HEIGHT/2 - 0.08f*GameConstants.GAME_WIDTH);
		guiElements.add(resumeGame);
		GuiButton closeGame = new GuiButton("Exit Game", 0.1f, this);
		closeGame.xStart = (GameConstants.GAME_WIDTH-resumeGame.width)/2;
		closeGame.yStart = (int) (GameConstants.GAME_HEIGHT/2 + 0.08f* GameConstants.GAME_HEIGHT);
		guiElements.add(closeGame);
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
