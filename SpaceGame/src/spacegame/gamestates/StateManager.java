package spacegame.gamestates;

import java.io.*;

import org.newdawn.slick.*;
import org.newdawn.slick.state.*;

import spacegame.other.*;

public class StateManager extends StateBasedGame {
	
	public static StateManager instance;
	
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

}
