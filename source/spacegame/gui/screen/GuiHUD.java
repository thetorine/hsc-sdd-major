package spacegame.gui.screen;

import org.newdawn.slick.Color;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.geom.Rectangle;
import spacegame.core.AssetManager;
import spacegame.entity.EntityBase;
import spacegame.entity.EntityPlayer;
import spacegame.gameplay.Minimap;
import spacegame.gamestates.IngameState;
import spacegame.gui.Gui;
import spacegame.gui.widgets.EventListener;
import spacegame.gui.widgets.GuiEquipSlot;
import spacegame.gui.widgets.GuiProgressBar;
import spacegame.inventory.Inventory;
import spacegame.inventory.ItemStack;
import spacegame.other.GameConstants;
import spacegame.other.GameUtilities;
import spacegame.other.GameUtilities.Point;

public class GuiHUD extends Gui implements EventListener {
	
	public EntityPlayer player;
	public GuiProgressBar healthBar;
	public GuiProgressBar shieldBar;
	public EntityBase lastEntityPressed;
	public Rectangle infoRect;
	public Rectangle pointsRect;
	public Rectangle msgBox;
	public GuiEquipSlot[] entitySlots = new GuiEquipSlot[4];
	public Minimap gameMinimap = new Minimap();

	public int slotXStart, slotYStart, slotXEnd, slotYEnd;

	public String currentMessage;
	public boolean messageOnDisplay;
	public int exitKeycode;

	public GuiHUD() {
		super(AssetManager.uiImages.get("bg_white.png"), 1f);
		background.setAlpha(0);
		player = IngameState.getInstance().entityManager.player;
		healthBar = new GuiProgressBar(0, 0, 0.09f, this);
		healthBar.xStart = (GameConstants.GAME_WIDTH-healthBar.width)/2;
		healthBar.yStart = (int) (0.83f*GameConstants.GAME_HEIGHT);
		healthBar.setColorFilter(0, 1, 0);
		healthBar.setText("Health Points");
		shieldBar = new GuiProgressBar(0, 0, 0.09f, this);
		shieldBar.xStart = (GameConstants.GAME_WIDTH-healthBar.width)/2;
		shieldBar.yStart = (int) (healthBar.yStart+healthBar.height+0.005f*GameConstants.GAME_HEIGHT);
		shieldBar.setText("Shield Energy");
		for(int i = 0; i < Inventory.WEAPON_COLUMNS; i++) {
			GuiEquipSlot slot = new GuiEquipSlot(0, 0, 0.08f, this, player, i);
			switch(i) {
				case 0: {
					slot.xStart = (int) (healthBar.xStart-0.005f*GameConstants.GAME_HEIGHT-slot.width);
					slot.yStart = healthBar.yStart + (healthBar.height-slot.height)/2;
					slotXStart = slot.xStart;
					slotYStart = slot.yStart;
					break;
				}
				case 1: {
					slot.xStart = (int) (healthBar.xStart-0.005f*GameConstants.GAME_HEIGHT-slot.width);
					slot.yStart = shieldBar.yStart + (shieldBar.height-slot.height)/2;
					break;
				}
				case 2: {
					slot.xStart = (int) (shieldBar.xStart+shieldBar.width+0.005f*GameConstants.GAME_HEIGHT);
					slot.yStart = healthBar.yStart + (healthBar.height-slot.height)/2;
					break;
				}
				case 3: {
					slot.xStart = (int) (shieldBar.xStart+shieldBar.width+0.005f*GameConstants.GAME_HEIGHT);
					slot.yStart = shieldBar.yStart + (shieldBar.height-slot.height)/2;
					slotXEnd = slot.xStart + slot.width;;
					slotYEnd = slot.yStart + slot.height;
					break;
				}
			}
			addGuiElement(slot);
		}
		
		for(int i = 0; i < entitySlots.length; i++) {
			entitySlots[i] = new GuiEquipSlot(0, 0, 0.08f, this, lastEntityPressed, i);
			addGuiElement(entitySlots[i]);
		}
		
		addGuiElement(healthBar);
		addGuiElement(shieldBar);
	}
	
	@Override
	public void onUpdate(int delta) {
		super.onUpdate(delta);
		for (Gui gui : guiElements) {
			if (gui instanceof GuiEquipSlot) {
				GuiEquipSlot slot = (GuiEquipSlot) gui;
				if(slot.entityInv == player) {
					if(slot.col == player.inventory.weaponid) {
						slot.pressed = true;
					} else if(!slot.highlighted) {
						slot.pressed = false;
					}
				}
			}
		}
		
		if(lastEntityPressed == null) {
			for(GuiEquipSlot slot: entitySlots) {
				slot.hidden = true;
			}
		} else {
			for(GuiEquipSlot slot: entitySlots) {
				slot.hidden = false;
			}
		}

		if(IngameState.getInstance().gContainer.getInput().isKeyPressed(exitKeycode)) {
			messageOnDisplay = false;
		}
		
		player = IngameState.getInstance().entityManager.player;
	}
	
	@Override
	public void renderBackground(Graphics g, GameContainer container) {
		super.renderBackground(g, container);
		healthBar.setPercentage((float)player.currentHealth/(float)player.maxHealth);
		shieldBar.setPercentage(player.currentShield/(float)player.maxShield);
		gameMinimap.render(g, GameConstants.GAME_WIDTH, GameConstants.GAME_HEIGHT, player);
		
		g.setColor(new Color(0, 0, 0, 0.5f));
		g.fillRoundRect(slotXStart-0.01f*GameConstants.GAME_WIDTH, slotYStart-0.01f*GameConstants.GAME_WIDTH, slotXEnd-slotXStart+0.02f*GameConstants.GAME_WIDTH, slotYEnd-slotYStart+0.02f*GameConstants.GAME_WIDTH, 10);

		if(infoRect == null) {
			infoRect = new Rectangle(0.01f*GameConstants.GAME_WIDTH, 0, 0.25f*GameConstants.GAME_WIDTH, 0);
		}

		pointsRect = null;
		if(pointsRect == null) {
			pointsRect = new Rectangle(0.01f*GameConstants.GAME_WIDTH, slotYStart-0.01f*GameConstants.GAME_WIDTH, 0.25f*GameConstants.GAME_WIDTH, slotYEnd-slotYStart+0.02f*GameConstants.GAME_WIDTH);
			pointsRect.setY(0.88f*GameConstants.GAME_HEIGHT);
			pointsRect.setHeight(0.98f*GameConstants.GAME_HEIGHT - pointsRect.getMinY());
		}

		g.setColor(Color.white);
		g.fill(pointsRect);

		//current game info/stats
		g.setColor(Color.black);
		EntityPlayer player = IngameState.getInstance().entityManager.player;
		int xStart = (int) (pointsRect.getMinX() + 0.01f*GameConstants.GAME_WIDTH);
		int currentHeight = (int) (pointsRect.getMinY()+0.02f*GameConstants.GAME_HEIGHT);
		currentHeight = (int) wrapText("Game Stats", xStart, currentHeight, 0.22f*GameConstants.GAME_WIDTH, g);
		currentHeight = (int) wrapText("Current Points: " + player.pointsGained, xStart+0.01f*GameConstants.GAME_WIDTH, currentHeight, 0.22f*GameConstants.GAME_WIDTH, g);

		if(lastEntityPressed != null) {
			g.setColor(Color.white);
			g.fill(infoRect);
			g.setColor(Color.black);
			int initialHeight = (int) (0.01f*GameConstants.GAME_WIDTH);
			int lastHeight = (int) (initialHeight+0.02f*GameConstants.GAME_HEIGHT);
			String position = String.format("Coordinates: %d, %d", (int)lastEntityPressed.getVector().xCoord, (int)lastEntityPressed.getVector().yCoord);
			lastHeight = (int) wrapText(String.format("Entity Type: %s", lastEntityPressed.getType().name()), infoRect.getMinX()+0.01f*GameConstants.GAME_WIDTH, lastHeight, 0.22f*GameConstants.GAME_WIDTH, g);
			lastHeight = (int) wrapText(position, infoRect.getMinX()+0.01f*GameConstants.GAME_WIDTH, lastHeight, 0.22f*GameConstants.GAME_WIDTH, g);
			if(lastEntityPressed.maxHealth > 0) {
				lastHeight = (int) wrapText(String.format("Health: %d/%d", lastEntityPressed.currentHealth, lastEntityPressed.maxHealth), infoRect.getMinX()+0.01f*GameConstants.GAME_WIDTH, lastHeight, 0.22f*GameConstants.GAME_WIDTH, g);
			}
			
			lastHeight = (int) wrapText("Velocity: " + (int)lastEntityPressed.getVector().velocityLength, infoRect.getMinX()+0.01f*GameConstants.GAME_WIDTH, lastHeight, 0.22f*GameConstants.GAME_WIDTH, g);
			
			int slotHeight = 0;
			for(int i = 0; i < entitySlots.length; i++) {
				GuiEquipSlot slot = entitySlots[i];
				slot.entityInv = lastEntityPressed;
				slot.xStart = (int) (infoRect.getMinX()+0.01f*GameConstants.GAME_WIDTH + slot.width*i + 0.01f*GameConstants.GAME_WIDTH*(i));
				slot.yStart = lastHeight;
				slotHeight = slot.height;
			}
			lastHeight += slotHeight + 0.02f*GameConstants.GAME_HEIGHT;
			infoRect.setY(initialHeight);
			infoRect.setHeight(lastHeight-initialHeight);
		}
	}
	
	@Override
	public void renderForeground(Graphics g, GameContainer container) {
		super.renderForeground(g, container);
		String info = "";
		for(Gui element : guiElements) {
			if(element instanceof GuiEquipSlot) {
				GuiEquipSlot invSlot = (GuiEquipSlot) element;
				if(invSlot.highlighted) {
					ItemStack stack = invSlot.getHeldStack();
					if (stack != null) {
						info = stack.itemClass.itemName;
					}
				}
			}
		}
		if(info.length() > 0) {
			drawInfoBoxAtMousePos(g, container, info);
		}

		if(messageOnDisplay) {
			int width = (int) (0.4f*GameConstants.GAME_WIDTH);
			int height = (int) (0.2f*GameConstants.GAME_HEIGHT);
			if(msgBox == null) {
				msgBox = new Rectangle((GameConstants.GAME_WIDTH-width)/2, 0.6f*GameConstants.GAME_HEIGHT, width, 0);
			} else {
				g.setColor(Color.white);
				g.fill(msgBox);
			}
			g.setColor(Color.black);
			int lastHeight = (int) (0.62f*GameConstants.GAME_HEIGHT);
			lastHeight = (int) wrapText(currentMessage, msgBox.getMinX() + 0.025f*GameConstants.GAME_WIDTH, lastHeight, 0.35f*GameConstants.GAME_WIDTH ,g);
			msgBox.setHeight(lastHeight-msgBox.getMinY());
		}
	}
	
	@Override
	public void mousedPressed(int button, int x, int y) {
		super.mousedPressed(button, x, y);
		Point pt = GameUtilities.translateGameToMap(new Point(x, y));
		EntityBase b = IngameState.getInstance().entityManager.getEntityAt(pt, false);
		if(b == null || b != lastEntityPressed) {
			lastEntityPressed = b;
		} else {
			lastEntityPressed = null;
		}
	}

	public void showMessage(String message, int exitKey) {
		currentMessage = message;
		exitKeycode = exitKey;
		messageOnDisplay = true;
	}
	
	@Override
	public boolean shouldPauseGame() {
		return false;
	}
	
	@Override
	public boolean supportsHierachy() {
		return true;
	}

	@Override
	public void onStateChange(Gui element) {
		if (element instanceof GuiEquipSlot) {
			GuiEquipSlot slot = (GuiEquipSlot) element;
			if(slot.entityInv == player) {
				player.inventory.selectWeaponByID(slot.col);
			}
		}
	}
}
