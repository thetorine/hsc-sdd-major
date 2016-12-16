package spacegame.gui;

import java.util.*;

import spacegame.gamestates.*;
import spacegame.gui.screen.*;
import spacegame.other.KeyboardListener.*;

//just handles the various screens being opened
public class GuiHierarchy implements IKeyboard {
	
	public Gui currentGui;
	public ArrayList<Gui> currentHeirarchy;
	public HashMap<Integer, Gui> guiKeys;
	public HashMap<Integer, Boolean> registeredKeys;
	
	public GuiHierarchy() {
		currentHeirarchy = new ArrayList<>();
		guiKeys = new HashMap<>();
		registeredKeys = new HashMap<>();
		activateGui(new GuiHUD());
	}

	//opens a gui and performs the particular modifications to the pause system as required by the gui screen
	public void openGui(Gui gui) {
		if(currentGui == null) {
			if(gui.shouldPauseGame()) {
				IngameState.getInstance().gamePaused = true;
			}
			activateGui(gui);
		} else if(currentGui.supportsHierachy()) {
			activateGui(gui);
			if(gui.shouldPauseGame()) {
				IngameState.getInstance().gamePaused = true;
			}
		} else if(gui instanceof GuiPauseMenu) {
			//universal close button (esc)
			gui.onClose();
			loadPreviousGui();
		}
	}

	//internal opening of a gui
	private void activateGui(Gui gui) {
		currentHeirarchy.add(gui);
		currentGui = gui;
		currentGui.onOpen();
	}

	//sets up a gui to open when a particular key is pressed.
	public void openGuiOnKeyPress(Gui gui, Integer key) {
		guiKeys.put(key, gui);
		registeredKeys.put(key, true);
	}

	//removes all the loaded guis and sets it to the hud.
	public void collapseHeirarchy() {
		currentHeirarchy.clear();
		currentGui = null;
		activateGui(new GuiHUD());
		IngameState.getInstance().gamePaused = false;
	}

	//loads the previous gui
	public Gui loadPreviousGui() {
		if(currentHeirarchy.size() > 1) {
			currentHeirarchy.remove(currentHeirarchy.size()-1);
			currentGui = currentHeirarchy.get(currentHeirarchy.size()-1);
			IngameState.getInstance().gamePaused = currentGui.shouldPauseGame();
			return currentGui;
		}
		return null; 
	}

	@Override
	public HashMap<Integer, Boolean> getKeysToListen() {
		return registeredKeys;
	}

	//opens a gui when a particular key is pressed
	@Override
	public void onKeyPress(int key, int delta) {
		Gui gui = guiKeys.get(key);
		if(!currentHeirarchy.contains(gui)) {
			if(gui.shouldOpen()) {
				openGui(gui);
			}
		} else if(currentGui == gui) {
			gui.onClose();
			loadPreviousGui();
		}
	}
}
