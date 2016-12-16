package spacegame.gui;

import java.util.*;

import org.newdawn.slick.*;
import org.newdawn.slick.geom.*;

import spacegame.other.*;

public class Gui {
	public Image background;
	public int xStart, yStart, width, height;
	public ArrayList<Gui> guiElements = new ArrayList<>();
	public ArrayList<Gui> addList = new ArrayList<>();
	public ArrayList<Gui> removalList = new ArrayList<>();
	public Gui parentClass;
	public boolean bgTint;
	public boolean hidden;
	
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
		if(!hidden) {
			renderBackground(g, container);
			renderForeground(g, container);
		}
	}
	
	public void onUpdate(int delta) {
		for(Gui gui : guiElements) {
			if(!gui.hidden) {
				gui.onUpdate(delta);
			}
		}
	}
	
	public void renderBackground(Graphics g, GameContainer container) {
		drawBackgroundTint(g);
		g.drawImage(background, xStart, yStart);
	}

	//renders the elements loaded in this gui
	public void renderForeground(Graphics g, GameContainer container) {
		for(Gui gui : guiElements) {
			gui.render(g, container);
			postElementRender(gui, g, container);
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
	
	public void mouseWheelMoved(int change) {
		for(Gui gui : guiElements) {
			gui.mouseWheelMoved(change);
		}
	}

	//scales the image with regards to the window scale
	public Image scaleImage(Image img, float scale) {
		scale /= GameConstants.WINDOW_SCALE;
		return img.getScaledCopy(scale);
	}

	//makes the background dark
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

	//draws a info box at the mouse's position
	public void drawInfoBoxAtMousePos(Graphics g, GameContainer container, String info) {
		drawInfoBox(g, container, info, container.getInput().getMouseX(), container.getInput().getMouseY());
	}

	//draws an info box with the given info at a particular coordinates
	public void drawInfoBox(Graphics g, GameContainer container, String info, int x, int y) {
		g.setFont(GameConstants.GAME_FONT[0]);
		int textWidth = g.getFont().getWidth(info);
		int boxWidth = (int) (textWidth+0.025f*GameConstants.GAME_WIDTH);
		int textHeight = (int) g.getFont().getHeight(info);
		int boxHeight = (int) (textHeight+0.025f*GameConstants.GAME_HEIGHT);
		g.setColor(Color.white);
		Polygon triangle = new Polygon(new float[] {0,0, 2, -3, 4,0 });
		triangle = (Polygon) triangle.transform(Transform.createScaleTransform(5f, 5f));
		triangle.setCenterX(x);
		triangle.setY(y+triangle.getHeight()/2);
		Rectangle rect = new Rectangle(triangle.getCenterX()-boxWidth/2, triangle.getY(), boxWidth, boxHeight);
		g.fill(rect);
		g.fill(triangle);
		g.setColor(Color.black);
		g.drawString(info, rect.getMinX() + (rect.getWidth()-textWidth)/2, rect.getMinY()+(rect.getHeight()-textHeight)/2);
	}

	//wraps the text so that long sentences do not go on forever, adds an extra padding at the end to make it nice for the next text
	public float wrapText(String s, float x, float y, float maxWidth, Graphics g) {
		String[] words = s.split(" ");
		int currentWord = 0;
		int currentLine = 0;
		String cLine = "";
		while(currentWord < words.length) {
			String newLine = cLine + " " + words[currentWord];
			float w = g.getFont().getWidth(newLine);
			if(w <= maxWidth) {
				cLine = newLine;
			} else {
				g.drawString(cLine, x, y + g.getFont().getHeight(cLine)*currentLine
						+ 0.01f*GameConstants.GAME_HEIGHT*currentLine);
				currentLine++;
				cLine = " " + words[currentWord];
			}
			currentWord++;
		}
		g.drawString(cLine, x, y + g.getFont().getHeight(cLine)*currentLine
				+ 0.01f*GameConstants.GAME_HEIGHT*currentLine);
		return y+g.getFont().getHeight(cLine)*(currentLine+1)
				+0.02f*GameConstants.GAME_HEIGHT + 0.01f*GameConstants.GAME_HEIGHT*(currentLine+1);
	}
	
	public void addGuiElement(Gui gui) {
		addList.add(gui);
	}
	
	public void removeGuiElement(Gui gui) {
		removalList.add(gui);
	}
	
	public void postElementRender(Gui gui, Graphics g, GameContainer container) {}
	
	public boolean supportsHierachy() {
		return false;
	}
	
	public void onOpen() {}
	
	public void onClose() {}
	
	public boolean shouldOpen() {
		return true;
	}
	
	public boolean shouldPauseGame() {
		return true;
	}
}
