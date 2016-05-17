package spacegame.gui;

import java.util.*;

import org.newdawn.slick.*;

import spacegame.other.*;

public class Gui {
	public Image background;
	public int xStart, yStart, width, height;
	public ArrayList<Gui> guiElements = new ArrayList<>();
	public ArrayList<Gui> addList = new ArrayList<>();
	public ArrayList<Gui> removalList = new ArrayList<>();
	public Gui parentClass;
	public boolean bgTint;
	
	//draw centered
	public Gui(Image bg, float scale) {
		background = scaleImage(bg, scale);
		width = background.getWidth();
		height = background.getHeight();
		
		xStart = (GameConstants.GAME_WIDTH-width)/2;
		yStart = (GameConstants.GAME_HEIGHT-height)/2;
	}
	
	//draw centered in parent
	public Gui(Image bg, float scale, Gui parent) {
		background = scaleImage(bg, scale);
		width = background.getWidth();
		height = background.getHeight();
		
		xStart = parent.xStart + (parent.width-width)/2;
		yStart = parent.yStart + (parent.height-height)/2;
		this.parentClass = parent;
	}
	
	//draw shifted from parent start
	public Gui(Image bg, int x, int y, float scale, Gui parent) {
		background = scaleImage(bg, scale);
		width = background.getWidth();
		height = background.getHeight();
		
		xStart = parent.xStart + x;
		yStart = parent.yStart + y;
		this.parentClass = parent;
	}
	
	public void render(Graphics g, GameContainer container) {
		renderBackground(g, container);
		renderForeground(g, container);
	}
	
	public void onUpdate(int delta) {
		for(Gui gui : guiElements) {
			gui.onUpdate(delta);;
		}
	}
	
	public void renderBackground(Graphics g, GameContainer container) {
		drawBackgroundTint(g);
		g.drawImage(background, xStart, yStart);
	}
	
	public void renderForeground(Graphics g, GameContainer container) {
		for(Gui gui : guiElements) {
			gui.render(g, container);
		}
		
		if(removalList.size() > 0) {
			guiElements.removeAll(removalList);
			removalList.clear();
		}
		if(addList.size() > 0) {
			guiElements.addAll(addList);
			addList.clear();
		}
	}
	
	public void mouseMoved(int oldX, int oldY, int newX, int newY) {
		for(Gui gui : guiElements) {
			gui.mouseMoved(oldX, oldY, newX, newY);
		}
	}
	
	public void mousedPressed(int button, int x, int y) {
		for(Gui gui : guiElements) {
			gui.mousedPressed(button, x, y);
		}
	}
	
	public void mouseDragged(int oldx, int oldy, int newx, int newy) {
		for(Gui gui : guiElements) {
			gui.mouseDragged(oldx, oldy, newx, newy);
		}
	}
	
	public void mouseReleased(int button, int x, int y) {
		for(Gui gui : guiElements) {
			gui.mouseReleased(button, x, y);
		}
	}
	
	public Image scaleImage(Image img, float scale) {
		scale /= GameConstants.WINDOW_SCALE;
		return img.getScaledCopy(scale);
	}
	
	public void setBackgroundTint() {
		bgTint = true;
	}
	
	public void drawBackgroundTint(Graphics g) {
		if(bgTint) {
			Color c = Color.black;
			g.setColor(new Color(c.r, c.g, c.b, 0.5f));
			g.fillRect(0, 0, GameConstants.GAME_WIDTH, GameConstants.GAME_HEIGHT);
		}
	}
	
	public void addGuiElement(Gui gui) {
		addList.add(gui);
	}
	
	public void removeGuiElement(Gui gui) {
		removalList.add(gui);
	}
	
	public boolean supportsHierachy() {
		return false;
	}
	
	public void onOpen() {}
	
	public void onClose() {}
	
	public boolean shouldPauseGame() {
		return true;
	}
}
