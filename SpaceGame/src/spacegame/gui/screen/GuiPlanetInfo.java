package spacegame.gui.screen;

import java.util.*;

import org.newdawn.slick.*;
import org.newdawn.slick.geom.*;

import spacegame.core.*;
import spacegame.core.ExplorablePlanet.*;
import spacegame.entity.*;
import spacegame.gui.*;
import spacegame.gui.widgets.*;
import spacegame.gui.widgets.EventListener;
import spacegame.gui.widgets.GuiList.*;
import spacegame.other.*;

public class GuiPlanetInfo extends Gui implements EventListener {
	public ExplorablePlanet planetInfo;

	public GuiPlanetInfo(EntityPlanet planet) {
		super(TextureHandler.uiImages.get("bg_green.png"), 0.4f);
		planetInfo = World.getPlanetInfo(planet);
		setBackgroundTint();
		GuiList list = new GuiList((int)(0.05f*width), (int)(0.15f*height), 0.32f, this);
		ArrayList<ListData> data = new ArrayList<>();
		for(PlanetRegion region : planetInfo.planetRegions) {
			data.add(new ListData(region.locationName));
		}
		list.addRows(data);
		guiElements.add(list);
		GuiProgressBar bar = new GuiProgressBar(0, 0, 0.08f, this);
		guiElements.add(bar);
		GuiButton exploreButton = new GuiButton("Explore", 0, 0, 0.08f, this);
		guiElements.add(exploreButton);
	}
	
	@Override
	public void renderBackground(Graphics g, GameContainer container) {
		super.renderBackground(g, container);
		int listYStart = 0, listXEnd = 0;
		for(Gui elements : guiElements) {
			if(elements instanceof GuiList) {
				listYStart = elements.yStart;
				listXEnd = elements.xStart+elements.width;
			}
		}
		String s = planetInfo.planetName;
		g.setFont(GameConstants.GAME_FONT[3]);
		g.drawString(s, xStart+(width-g.getFont().getWidth(s))/2, yStart+0.07f*height);
		g.setFont(GameConstants.GAME_FONT[0]);
		Color c = g.getColor();
		Color c1 = new Color(Color.darkGray);
		g.setColor(new Color(c1.r, c1.g, c1.b, 0.5f));
		Rectangle infoRect = new Rectangle(listXEnd+0.05f*width, listYStart+0.04f*height, width-(listXEnd-xStart)-0.1f*width, height-(listYStart-yStart)-0.13f*height);
		g.fill(infoRect);
		
		GuiList currentList = null;
		GuiProgressBar progressBar = null;
		GuiButton exploreButton = null;
		for(Gui element : guiElements) {
			if(element instanceof GuiList) {
				currentList = (GuiList) element;
			} else if(element instanceof GuiProgressBar) {
				progressBar = (GuiProgressBar) element;
			} else if(element instanceof GuiButton) {
				exploreButton = (GuiButton) element;
			}
		}
		g.setColor(Color.black);
		PlanetRegion region = planetInfo.planetRegions.get(currentList.selectedIndex);
		int lastHeight = (int) (infoRect.getMinY() + 0.05f*height);
		int maxWidth = (int) (infoRect.getWidth() - 2*0.05*width);
		lastHeight = (int) wrapText(region.desc, (float) (infoRect.getMinX()+0.05*width), lastHeight, maxWidth, g);
		lastHeight = (int) wrapText("Completion Time: " + (region.exploreTime/1000f) + " sec", (float) (infoRect.getMinX()+0.05*width), lastHeight, maxWidth, g);
		String exploreYield = "Explore Yield: ";
		for(int i = 0; i < region.exploreYield.size(); i++) {
			String s1 = region.exploreYield.get(i).itemClass.itemName;
			exploreYield = exploreYield + s1 + ", ";
		}
		lastHeight = (int) wrapText(exploreYield, (float) (infoRect.getMinX()+0.05*width), lastHeight, maxWidth, g);
		
		progressBar.xStart = (int) (infoRect.getMinX() + (infoRect.getWidth()-progressBar.width)/2);
		progressBar.yStart = lastHeight;
		progressBar.setPercentage(1f-(float)region.currentExplore/(float)region.exploreTime);
		lastHeight = (int) (progressBar.yStart+progressBar.height+0.03f*height);
		exploreButton.xStart = (int) (infoRect.getMinX() + (infoRect.getWidth()-exploreButton.width)/2);
		exploreButton.yStart = lastHeight;
		g.setColor(c);
		
		if(region.completed) {
			exploreButton.buttonName = "Completed";
		} else if(region.currentExplore < region.exploreTime) {
			exploreButton.buttonName = "In Progress";
		} else {
			exploreButton.buttonName = "Explore";
		}
	}
	
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
				g.drawString(cLine, x, y + g.getFont().getHeight(cLine)*currentLine);
				currentLine++;
				cLine = " " + words[currentWord];
			}
			currentWord++;
		}
		g.drawString(cLine, x, y + g.getFont().getHeight(cLine)*currentLine);
		return y+g.getFont().getHeight(cLine)*(currentLine+1)+0.02f*GameConstants.GAME_HEIGHT;
	}

	@Override
	public void onStateChange(Gui element) {
		if(element instanceof GuiButton) {
			GuiList currentList = null;
			for(Gui gui : guiElements) {
				if(gui instanceof GuiList) {
					currentList = (GuiList) gui;
				} 
			}
			 planetInfo.planetRegions.get(currentList.selectedIndex).beginExplore();
		}
	}
	
	
}