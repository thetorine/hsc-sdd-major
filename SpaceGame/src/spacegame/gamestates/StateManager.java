package spacegame.gamestates;

import org.newdawn.slick.AppGameContainer;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.state.StateBasedGame;
import spacegame.core.SoundManager;
import spacegame.other.GameConstants;

import java.io.File;
import java.io.IOException;
import java.net.URI;

public class StateManager extends StateBasedGame {
	
	public static StateManager instance;
	public SoundManager soundManager;
	
	public static void main(String[] args) throws SlickException {
		AppGameContainer container = new AppGameContainer(new StateManager("give me a name"));
		float scale = GameConstants.WINDOW_SCALE;
		GameConstants.GAME_WIDTH = (int) (container.getScreenWidth()/scale);
		GameConstants.GAME_HEIGHT = (int) (container.getScreenHeight()/scale);
		container.setDisplayMode(GameConstants.GAME_WIDTH, GameConstants.GAME_HEIGHT, GameConstants.WINDOW_SCALE == 1f);
		container.setTargetFrameRate(120); //screen tearing occurs with this but minimizes CPU usage
		container.setShowFPS(true);
		container.start();
	}
	
	public StateManager(String name) {
		super(name);
		StateManager.instance = this;
		soundManager = new SoundManager();
	}
	
	@Override
	public boolean closeRequested() {
		try {
			IngameState.getInstance().dataHandler.saveInterfaceData();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return super.closeRequested();
	}
	
	@Override
	public void initStatesList(GameContainer container) throws SlickException {
		addState(new StartGameState());
		addState(new IngameState());
	}

	public static File getHomeDirectory() {
		try {
			URI jarLocation = StateManager.class.getProtectionDomain().getCodeSource().getLocation().toURI();
			File jar = new File(jarLocation);
			return jar;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

}
