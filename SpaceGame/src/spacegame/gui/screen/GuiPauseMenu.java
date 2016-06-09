package spacegame.gui.screen;

import spacegame.core.*;
import spacegame.gamestates.*;
import spacegame.gui.*;
import spacegame.gui.widgets.*;
import spacegame.other.*;

public class GuiPauseMenu extends Gui implements EventListener {
	
	public GuiPauseMenu() {
		super(AssetManager.uiImages.get("bg_white.png"), 0.3f);
		setBackgroundTint();
		GuiButton resumeGame = new GuiButton("Resume Game", 0.1f, this);
		resumeGame.yStart -= 0.12f*GameConstants.GAME_HEIGHT;
		guiElements.add(resumeGame);
		guiElements.add(new GuiButton("Controls", 0.1f, this));
		GuiButton closeGame = new GuiButton("Main Menu", 0.1f, this);
		closeGame.yStart += 0.12f*GameConstants.GAME_HEIGHT;
		guiElements.add(closeGame);
	}

	@Override
	public void onStateChange(Gui element) {
		if(element instanceof GuiButton) {
			GuiButton gb = (GuiButton) element;
			if(gb.buttonName.equals("Resume Game")) {
				IngameState.getInstance().guiHierarchy.collapseHeirarchy();
			} else if(gb.buttonName.equals("Main Menu")) {
				StateManager.instance.enterState(0);
				IngameState.getInstance().guiHierarchy.collapseHeirarchy();
			} else if(gb.buttonName.equals("Controls")) {
				IngameState.getInstance().guiHierarchy.openGui(new GuiControls());
			}
		}
	}
	
	@Override
	public boolean supportsHierachy() {
		return true;
	}
}
