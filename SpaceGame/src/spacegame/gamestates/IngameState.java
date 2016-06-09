package spacegame.gamestates;

import org.newdawn.slick.*;
import org.newdawn.slick.font.effects.*;
import org.newdawn.slick.state.*;

import spacegame.core.*;
import spacegame.entity.*;
import spacegame.gameplay.*;
import spacegame.gameplay.ExplorablePlanet.*;
import spacegame.gui.*;
import spacegame.gui.screen.*;
import spacegame.inventory.*;
import spacegame.other.*;

public class IngameState extends BasicGameState {
	private static IngameState instance;
	
	public static IngameState getInstance() {
		return instance;
	}
	
	public EntityManager entityManager;
	public Camera camera;
	public DataHandler dataHandler;
	public GameContainer gContainer;
	public KeyboardListener keyboardListener;
	public GameRenderer renderer;
	public AssetManager textureManager;
	public World world;
	public GuiHierarchy guiHierarchy;
	public CraftingManager craftingManager;
	
	public boolean firstLoad;
	public static String FONT_LOCATION = GameConstants.RESOURCE + "font/kenvector_future_thin.ttf";
	
	public boolean gamePaused;
	
	public IngameState() {
		IngameState.instance = this;
		loadTextures();
		loadFonts();
	}
	
	public void loadTextures() {
		textureManager = new AssetManager();
	}
	
	@SuppressWarnings("unchecked")
	public void loadFonts() {
		int arrayLength = GameConstants.GAME_FONT.length;
		try {
			for(int i = 0; i < arrayLength; i++) {
				UnicodeFont font = new UnicodeFont(FONT_LOCATION, (int) ((15+10*i)/GameConstants.WINDOW_SCALE), false, false);
				font.addAsciiGlyphs();
				font.getEffects().add(new ColorEffect());
				font.loadGlyphs();
				GameConstants.GAME_FONT[i] = font;
			}
		} catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	@Override
	public void init(GameContainer container, StateBasedGame game) throws SlickException {
		dataHandler = new DataHandler();
		world = new World();
		entityManager = new EntityManager();
		dataHandler.loadInterfaceData();
		camera = new Camera(entityManager.player);
		entityManager.loadPlanetSystems(firstLoad);
		keyboardListener = new KeyboardListener();
		guiHierarchy = new GuiHierarchy();
		renderer = new GameRenderer();
		craftingManager = new CraftingManager();
		world.onGameCreation();
		
		KeyboardListener.registerListener(guiHierarchy);
		for(EntityBase b : entityManager.initialSpawn) {
			entityManager.spawnEntity(b);
		}
		
		guiHierarchy.openGuiOnKeyPress(new GuiPauseMenu(), GameConstants.PAUSE_MENU);
		guiHierarchy.openGuiOnKeyPress(new GuiCmdOverlay(), GameConstants.COMMAND);
		guiHierarchy.openGuiOnKeyPress(new GuiDiagnostics(), GameConstants.DIAGNOSTICS);
		guiHierarchy.openGuiOnKeyPress(new GuiInventory(), GameConstants.INVENTORY);
		guiHierarchy.openGuiOnKeyPress(new GuiMap(), GameConstants.MAP);
		guiHierarchy.openGuiOnKeyPress(new GuiPlanetInfo(), GameConstants.PLANET);
		guiHierarchy.openGuiOnKeyPress(new GuiShopMenu(), GameConstants.SHOP);
		guiHierarchy.openGuiOnKeyPress(new GuiUpgrade(), GameConstants.UPGRADE_MENU);
	}
	
	@Override
	public void mouseMoved(int oldx, int oldy, int newx, int newy) {
		if(guiHierarchy.currentGui != null) {
			guiHierarchy.currentGui.mouseMoved(oldx, oldy, newx, newy);
		}
	}
	
	@Override
	public void mousePressed(int button, int x, int y) {
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
	public void render(GameContainer container, StateBasedGame game, Graphics g) throws SlickException {
		g.setAntiAlias(true);
		g.setFont(GameConstants.GAME_FONT[0]);
		renderer.render(container, g);
		if(guiHierarchy.currentGui != null) {
			guiHierarchy.currentGui.render(g, container);
		}
	}
	
	@Override
	public void update(GameContainer container, StateBasedGame game, int delta) throws SlickException {
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

	@Override
	public int getID() {
		return 1;
	}
}