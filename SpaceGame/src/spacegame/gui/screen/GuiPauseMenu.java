package spacegame.gui.screen;

import spacegame.*;
import spacegame.core.*;
import spacegame.gui.*;
import spacegame.gui.widgets.*;
import spacegame.other.*;

public class GuiPauseMenu extends Gui implements EventListener {
	
	public GuiPauseMenu() {
		super(TextureHandler.uiImages.get("bg_white.png"), 0.3f);
		setBackgroundTint();
		GuiButton resumeGame = new GuiButton("Resume Game", 0.1f, this);
		resumeGame.yStart -= 0.12f*GameConstants.GAME_HEIGHT;
		guiElements.add(resumeGame);
		guiElements.add(new GuiButton("Options", 0.1f, this));
		GuiButton closeGame = new GuiButton("Exit Game", 0.1f, this);
		closeGame.yStart += 0.12f*GameConstants.GAME_HEIGHT;
		guiElements.add(closeGame);
	}

	@Override
	public void onStateChange(Gui element) {
		if(element instanceof GuiButton) {
			GuiButton gb = (GuiButton) element;
			if(gb.buttonName.equals("Resume Game")) {
				CoreGame.getInstance().guiHierarchy.collapseHeirarchy();
			} else if(gb.buttonName.equals("Exit Game")) {
				System.exit(0);
			}
		}
	}
}
