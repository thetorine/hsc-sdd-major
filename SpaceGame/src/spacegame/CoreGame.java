package spacegame;

import java.io.*;

import org.newdawn.slick.*;
import org.newdawn.slick.font.effects.*;

import spacegame.core.*;
import spacegame.core.ExplorablePlanet.*;
import spacegame.entity.*;
import spacegame.gui.*;
import spacegame.gui.screen.*;
import spacegame.inventory.*;
import spacegame.other.*;

public class CoreGame extends BasicGame {
	private static CoreGame instance;
	
	public static CoreGame getInstance() {
		return instance;
	}
	
	public static void main(String[] args) {
		instance = new CoreGame("[insert name here]");
		try {
			float scale = GameConstants.WINDOW_SCALE;
			AppGameContainer container = new AppGameContainer(instance);
			GameConstants.GAME_WIDTH = (int) (container.getScreenWidth()/scale);
			GameConstants.GAME_HEIGHT = (int) (container.getScreenHeight()/scale);
			container.setDisplayMode(GameConstants.GAME_WIDTH, GameConstants.GAME_HEIGHT, scale == 1f);
			container.setTargetFrameRate(60); //screen tearing occurs with this but minimizes CPU usage
			container.setShowFPS(true);
			container.start();
		} catch (SlickException e) {
			e.printStackTrace();
		}
	}
	
	public EntityManager entityManager;
	public Camera camera;
	public DataHandler dataHandler;
	public GameContainer gContainer;
	public GuiIngame ingameGUI;
	public KeyboardListener keyboardListener;
	public GameRenderer renderer;
	public TextureHandler textureManager;
	public World world;
	public GuiHierarchy guiHierarchy;
	public CraftingManager craftingManager;
	
	public boolean firstLoad;
	public String fontLocation = GameConstants.RESOURCE + "font/kenvector_future_thin.ttf";
	
	public boolean gamePaused;
	
	public CoreGame(String title) {
		super(title);
	}

	@Override
	public boolean closeRequested() {
		try {
			dataHandler.saveInterfaceData();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return super.closeRequested();
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public void init(GameContainer container) throws SlickException {
		textureManager = new TextureHandler();
		dataHandler = new DataHandler();
		world = new World();
		entityManager = new EntityManager();
		dataHandler.loadInterfaceData();
		camera = new Camera(entityManager.player);
		entityManager.loadPlanetSystems(firstLoad);
		keyboardListener = new KeyboardListener();
		guiHierarchy = new GuiHierarchy();
		renderer = new GameRenderer();
		ingameGUI = new GuiIngame();
		craftingManager = new CraftingManager();
		world.onGameCreation();
		
		KeyboardListener.registerListener(guiHierarchy);
		try {
			int arrayLength = GameConstants.GAME_FONT.length;
			for(int i = 0; i < arrayLength; i++) {
				UnicodeFont font = new UnicodeFont(fontLocation, (int) ((15+10*i)/GameConstants.WINDOW_SCALE*1.1f), false, false);
				font.addAsciiGlyphs();
				font.getEffects().add(new ColorEffect());
				font.loadGlyphs();
				GameConstants.GAME_FONT[i] = font;
			}
			for(EntityBase b : entityManager.initialSpawn) {
				entityManager.spawnEntity(b);
			}
		} catch(Exception e) {
			e.printStackTrace();
		}
		
		guiHierarchy.openGuiOnKeyPress(new GuiPauseMenu(), GameConstants.PAUSE_MENU);
		guiHierarchy.openGuiOnKeyPress(new GuiCmdOverlay(), GameConstants.COMMAND);
		guiHierarchy.openGuiOnKeyPress(new GuiDiagnostics(), GameConstants.DIAGNOSTICS);
		guiHierarchy.openGuiOnKeyPress(new GuiInventory(), GameConstants.INVENTORY);
		guiHierarchy.openGuiOnKeyPress(new GuiMap(), GameConstants.MAP);
		guiHierarchy.openGuiOnKeyPress(new GuiPlanetInfo(), GameConstants.PLANET);
		guiHierarchy.openGuiOnKeyPress(new GuiShopMenu(), GameConstants.SHOP);
	}
	
	@Override
	public void mouseMoved(int oldx, int oldy, int newx, int newy) {
		if(ingameGUI != null) {
			ingameGUI.mouseMoved(oldx, oldy, newx, newy);
		}
		if(guiHierarchy.currentGui != null) {
			guiHierarchy.currentGui.mouseMoved(oldx, oldy, newx, newy);
		}
	}
	
	@Override
	public void mousePressed(int button, int x, int y) {
		if(ingameGUI != null) {
			ingameGUI.mousePressed(button, x, y);
		}
		if(guiHierarchy.currentGui != null) {
			guiHierarchy.currentGui.mousedPressed(button, x, y);
		}
	}
	
	@Override
	public void mouseDragged(int oldx, int oldy, int newx, int newy) {
		super.mouseDragged(oldx, oldy, newx, newy);
		if(guiHierarchy.currentGui != null) {
			guiHierarchy.currentGui.mouseDragged(oldx, oldy, newx, newy);
		}
	}
	
	public void mouseReleased(int button, int x, int y) {
		if(guiHierarchy.currentGui != null) {
			guiHierarchy.currentGui.mouseReleased(button, x, y);
		}
	}
	
	@Override
	public void mouseWheelMoved(int change) {
		if(guiHierarchy.currentGui != null) {
			guiHierarchy.currentGui.mouseWheelMoved(change);
		}
	}

	@Override
	public void render(GameContainer container, Graphics g) throws SlickException {
		g.setAntiAlias(true);
		g.setFont(GameConstants.GAME_FONT[0]);
		renderer.render(container, g);
		if(guiHierarchy.currentGui != null) {
			guiHierarchy.currentGui.render(g, container);
		}
	}
	
	@Override
	public void update(GameContainer container, int delta) throws SlickException {
		gContainer = container;
		if(!gamePaused) {
			entityManager.onUpdate(delta);
			camera.onUpdate(delta);
			world.onUpdate(delta);
			CollisionDetector.update(delta);
		} 
		
		for(Long id : World.planetsList.keySet()) {
			ExplorablePlanet ePlanet = World.planetsList.get(id);
			for(PlanetRegion region : ePlanet.planetRegions) {
				region.update(delta);
			}
		}
		
		if(guiHierarchy.currentGui != null) {
			guiHierarchy.currentGui.onUpdate(delta);
		}
		
		keyboardListener.onUpdate(delta);
	}
}