package spacegame;

import java.io.*;
import java.util.*;

import org.newdawn.slick.*;
import org.newdawn.slick.font.effects.*;

import spacegame.core.*;
import spacegame.core.ExplorablePlanet.*;
import spacegame.entity.*;
import spacegame.gui.*;
import spacegame.gui.screen.*;
import spacegame.inventory.*;
import spacegame.other.*;
import spacegame.other.KeyboardListener.*;

public class CoreGame extends BasicGame implements IKeyboard {
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
			container.setVSync(true);
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
	public Gui currentGui;
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
	
	@Override
	public HashMap<Integer, Boolean> getKeysToListen() {
		HashMap<Integer, Boolean> map = new HashMap<>();
		map.put(GameConstants.PAUSE_MENU, true);
		map.put(GameConstants.OPEN_MAP, true);
		map.put(GameConstants.OPEN_INV, true);
		map.put(GameConstants.OPEN_PLANET_REGION, true);
		map.put(GameConstants.OPEN_COMMAND, true);
		map.put(GameConstants.DIAGNOSTICS, true);
		return map;
	}
	
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
		renderer = new GameRenderer();
		ingameGUI = new GuiIngame();
		world.onGameCreation();
		craftingManager = new CraftingManager();
		
		KeyboardListener.registerListener(instance);
		try {
			int arrayLength = GameConstants.GAME_FONT.length;
			for(int i = 0; i < arrayLength; i++) {
				UnicodeFont font = new UnicodeFont(fontLocation, (int) ((15+10*i)/GameConstants.WINDOW_SCALE), false, false);
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
	}
	
	@Override
	public void mouseMoved(int oldx, int oldy, int newx, int newy) {
		if(ingameGUI != null) {
			ingameGUI.mouseMoved(oldx, oldy, newx, newy);
		}
		if(currentGui != null) {
			currentGui.mouseMoved(oldx, oldy, newx, newy);
		}
	}
	
	@Override
	public void mousePressed(int button, int x, int y) {
		if(ingameGUI != null) {
			ingameGUI.mousePressed(button, x, y);
		}
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
	public void onKeyPress(int key, int delta) {
		if(!(currentGui instanceof GuiCmdOverlay)) {
			if(key == GameConstants.PAUSE_MENU) {
				gamePaused ^= true;
				if(gamePaused == false) {
					currentGui = null;
				} else {
					currentGui = new GuiPauseMenu();
				}
			}
			if(key == GameConstants.OPEN_MAP) {
				if(currentGui instanceof GuiMap) {
					currentGui = null;
				} else {
					currentGui = new GuiMap();
				}
			}
			if(key == GameConstants.OPEN_INV) {
				gamePaused ^= true;
				if(currentGui instanceof GuiInventory) {
					currentGui = null;
				} else {
					currentGui = new GuiInventory();
				}
			}
			if(key == GameConstants.OPEN_PLANET_REGION) {
				gamePaused ^= true;
				if(currentGui instanceof GuiPlanetInfo) {
					currentGui = null;
				} else {
					EntityPlanet planet = (EntityPlanet) entityManager.getEntityAt(entityManager.player.asPoint(), true);
					currentGui = new GuiPlanetInfo(planet);
				}
			}
			if(key == GameConstants.OPEN_COMMAND) {
				gamePaused ^= true;
				if(currentGui instanceof GuiCmdOverlay) {
					currentGui = null;
				} else {
					currentGui = new GuiShopMenu();
				}
			}
		} else if(key == GameConstants.PAUSE_MENU) {
			gamePaused = false;
			currentGui = null;
		}
		
		if(key == GameConstants.DIAGNOSTICS) {
			if(currentGui == null) {
				currentGui = new GuiDiagnostics();
			} else {
				currentGui = null;
			}
		}
	}
	
	@Override
	public void render(GameContainer container, Graphics g) throws SlickException {
		g.setAntiAlias(true);
		g.setFont(GameConstants.GAME_FONT[0]);
		renderer.render(container, g);
		if(currentGui != null) {
			currentGui.render(g, container);
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
		
		if(currentGui != null) {
			currentGui.onUpdate(delta);
		}
		
		keyboardListener.onUpdate(delta);
	}
}