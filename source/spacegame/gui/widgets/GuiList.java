package spacegame.gui.widgets;

import org.lwjgl.input.Keyboard;
import org.newdawn.slick.*;
import spacegame.core.AssetManager;
import spacegame.gamestates.IngameState;
import spacegame.gamestates.StateManager;
import spacegame.gui.Gui;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

public class GuiList extends Gui {
	
	public ArrayList<ListData> pluggedData = new ArrayList<>();
	public float currentDrawStart;
	public int selectedIndex;
	
	public Image scrollBar = AssetManager.uiImages.get("scroll_bar.png");
	public Image scrollBar_clicked = AssetManager.uiImages.get("scroll_bar_clicked.png");
	public Image element = AssetManager.uiImages.get("element.png");
	public Image element_highlight = AssetManager.uiImages.get("element_highlight.png");
	public Image element_select = AssetManager.uiImages.get("element_select.png");
	
	//drawing start/end for elements
	public int drawStart = (int) (yStart+0.05f*height);
	public int drawEnd = (int) (yStart+height-0.05f*height);
	
	//scroll bar
	public int sbX;
	public int sbYMax;
	public int sbCurrentY;
	public boolean sbMousePress;

	public GuiList(int x, int y, float scale, Gui parent) {
		super(AssetManager.uiImages.get("panel.png"), x, y, scale, parent);
		scrollBar = scaleImage(scrollBar, scale*0.2f);
		scrollBar_clicked = scaleImage(scrollBar_clicked, scale*0.2f);
		element = scaleImage(element, scale*0.25f);
		element_highlight = scaleImage(element_highlight, scale*0.25f);
		element_select = scaleImage(element_select, scale*0.25f);
		
		sbX = (int) (xStart+element.getWidth()+0.5f*scrollBar.getWidth());
		sbYMax = drawEnd-scrollBar.getHeight();
		sbCurrentY = drawStart;
		
		background.setAlpha(0);
	}
	
	@Override
	public void onUpdate(int delta) {
		super.onUpdate(delta);
		GameContainer container = IngameState.getInstance().gContainer;
		if(container.getInput().isKeyDown(Keyboard.KEY_UP)) {
			currentDrawStart -= 50*delta/1000f;
		} else if(container.getInput().isKeyDown(Keyboard.KEY_DOWN)) {
			currentDrawStart += 50*delta/1000f;
		}
		
		int finalElementY = drawStart + pluggedData.get(pluggedData.size()-1).y + element.getHeight();
		int distance = Math.abs(finalElementY-drawEnd);
		
		if(finalElementY > drawEnd) {
			currentDrawStart = (float)(sbCurrentY-drawStart)/(float)(sbYMax-drawStart)*distance;
		}
	}
	
	@Override
	public void renderBackground(Graphics g, GameContainer container) {
		super.renderBackground(g, container);
		int xPos = xStart+(width-element.getWidth())/2;
		g.setClip(xPos, drawStart, element.getWidth(), drawEnd-drawStart);
		for(int i = 0; i < pluggedData.size(); i++) {
			ListData plData = pluggedData.get(i);
			int yPos = (int) (yStart + 0.05f*height + plData.y - currentDrawStart);
			Image renImage = getElementImageForRender(i, xPos, yPos);
			g.drawImage(renImage, xPos, renImage == element_select ? yPos+4 : yPos);
			g.drawString(plData.displayName, xPos+(element.getWidth()-g.getFont().getWidth(plData.displayName))/2, yPos+(element.getHeight()-g.getFont().getHeight(plData.displayName))/2 + (renImage == element_select ? 4 : 0));
		}
		g.clearClip();
		g.setColor(Color.darkGray);
		g.fillRect(sbX+(scrollBar.getWidth()-0.5f*scrollBar.getWidth())/2, drawStart, 0.5f*scrollBar.getWidth(), drawEnd-drawStart);
		g.drawImage(sbMousePress ? scrollBar_clicked : scrollBar, sbX, sbCurrentY);
	}
	
	@Override
	public void mousedPressed(int button, int x, int y) {
		super.mousedPressed(button, x, y);
		if(button == Input.MOUSE_LEFT_BUTTON) {
			sbMousePress = x >= sbX && x <= sbX + scrollBar.getWidth() && y >= sbCurrentY && y <= sbCurrentY + scrollBar.getHeight();
			if(!sbMousePress) {
				int xPos = xStart+(width-element.getWidth())/2;
				for(int i = 0; i < pluggedData.size(); i++) {
					ListData plData = pluggedData.get(i);
					int yPos = (int) (yStart + 0.05f*height + plData.y - currentDrawStart);
					if(x >= xPos && x <= xPos+element.getWidth() && y >= yPos && y <= yPos+element.getHeight()) {
						selectedIndex = i;
						if(parentClass instanceof EventListener) {
							EventListener el = (EventListener) parentClass;
							el.onStateChange(this);
							StateManager.instance.soundManager.clickPress.play();
						}
					}
				}
			}
		} 
	}
	
	@Override
	public void mouseReleased(int button, int x, int y) {
		super.mouseReleased(button, x, y);
		if(button == Input.MOUSE_LEFT_BUTTON) {
			sbMousePress = false;
		} 
	}
	
	@Override
	public void mouseDragged(int oldx, int oldy, int newx, int newy) {
		super.mouseDragged(oldx, oldy, newx, newy);
		if(sbMousePress) {
			int deltaY = newy - oldy;
			sbCurrentY = deltaY > 0 ? Math.min(sbCurrentY+deltaY, sbYMax) : Math.max(sbCurrentY+deltaY, drawStart);
		}
	}
	
	@Override
	public void renderForeground(Graphics g, GameContainer container) {
		super.renderForeground(g, container);
	}
	
	public void addRows(ArrayList<ListData> data) {
		pluggedData.addAll(data);
		for(int i = 0; i < pluggedData.size(); i++) {
			pluggedData.get(i).y = (int) (i*element.getHeight() + i*0.05*element.getHeight());
		}
		Comparator<ListData> comparator = new Comparator<GuiList.ListData>() {
			@Override
			public int compare(ListData o1, ListData o2) {
				return o1.y-o2.y;
			}
		};
		Collections.sort(pluggedData, comparator);
	}
	
	public Image getElementImageForRender(int dataIndex, int xStart, int yStart) {
		if(dataIndex == selectedIndex) return element_select;
		GameContainer con = IngameState.getInstance().gContainer;
		int xEnd = xStart + element.getWidth();
		int yEnd = yStart + element.getHeight();
		int mouseX = con.getInput().getAbsoluteMouseX();
		int mouseY = con.getInput().getAbsoluteMouseY();
		if(mouseX >= xStart && mouseX <= xEnd && mouseY >= yStart && mouseY <= yEnd) {
			return element_highlight;
		}
		return element;
	}

	public static class ListData { 
		public String displayName;
		public int y;
		
		public ListData(String s) {
			displayName = s;
		}
		
		public void setListCoords(int y) {
			this.y = y;
		}
	}
}
