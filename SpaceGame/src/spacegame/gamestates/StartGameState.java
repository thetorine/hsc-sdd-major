package spacegame.gamestates;

import org.newdawn.slick.*;
import org.newdawn.slick.state.*;

import spacegame.core.*;
import spacegame.gui.*;
import spacegame.gui.screen.*;
import spacegame.other.*;

public class StartGameState extends BasicGameState {
	
	public static String FONT_LOCATION = GameConstants.RESOURCE + "font/kenvector_future_thin.ttf";
	
	public Gui currentGui;

	@Override
	public void render(GameContainer container, StateBasedGame game, Graphics g) throws SlickException {
		g.setAntiAlias(true);
		AssetManager.getCustomImageByName("bg").draw(0, 0, GameConstants.GAME_WIDTH,  GameConstants.GAME_HEIGHT);
		if(currentGui != null) {
			currentGui.render(g, container);
		}
	}

	@Override
	public void init(GameContainer container, StateBasedGame game) throws SlickException {
		currentGui = new GuiMainMenu();
	}

	@Override
	public void update(GameContainer container, StateBasedGame game, int delta) throws SlickException {
		if(currentGui != null) {
			currentGui.onUpdate(delta);
		}
	}
	
	@Override
	public void mouseMoved(int oldx, int oldy, int newx, int newy) {
		if(currentGui != null) {
			currentGui.mouseMoved(oldx, oldy, newx, newy);
		}
	}
	
	@Override
	public void mousePressed(int button, int x, int y) {
		if(currentGui != null) {
			currentGui.mousedPressed(button, x, y);
		}
	}
	
	@Override
	public void mouseDragged(int oldx, int oldy, int newx, int newy) {
		super.mouseDragged(oldx, oldy, newx, newy);
		if(currentGui != null) {
			currentGui.mouseDragged(oldx, oldy, newx, newy);
		}
	}
	
	public void mouseReleased(int button, int x, int y) {
		if(currentGui != null) {
			currentGui.mouseReleased(button, x, y);
		}
	}
	
	@Override
	public void mouseWheelMoved(int change) {
		if(currentGui != null) {
			currentGui.mouseWheelMoved(change);
		}
	}

	@Override
	public int getID() {
		return 0;
	}
	
}
