package spacegame.gui.screen;

import java.util.*;

import org.newdawn.slick.*;
import org.newdawn.slick.geom.*;

import spacegame.core.*;
import spacegame.entity.environment.*;
import spacegame.gameplay.*;
import spacegame.gameplay.ExplorablePlanet.*;
import spacegame.gamestates.*;
import spacegame.gui.*;
import spacegame.gui.widgets.*;
import spacegame.gui.widgets.EventListener;
import spacegame.gui.widgets.GuiList.*;
import spacegame.other.*;

public class GuiPlanetInfo extends Gui implements EventListener {
	public ExplorablePlanet planetInfo;
	public ArrayList<GuiInvSlot> slotsDrawn = new ArrayList<>();
	public int slotWidth;

	public GuiPlanetInfo() {
		super(AssetManager.uiImages.get("bg_green.png"), 0.4f);
		setBackgroundTint();
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
		lastHeight = (int) wrapText("Explore Yield:", (float) (infoRect.getMinX()+0.05*width), lastHeight, maxWidth, g);
		int totalWidth = (int) (slotWidth*slotsDrawn.size() + 0.1f*infoRect.getWidth()*(slotsDrawn.size()-1));
		int x = (int) (infoRect.getMinX() + (infoRect.getWidth()-totalWidth)/2);
		for(int i = 0; i < slotsDrawn.size(); i++) {
			GuiInvSlot slot = slotsDrawn.get(i);
			slot.xStart = (int) (x+slotWidth*i+0.1f*infoRect.getWidth()*i);
			slot.yStart = lastHeight;
			if(i == slotsDrawn.size()-1) {
				lastHeight = (int) (slot.yStart + slot.height + 0.02f*GameConstants.GAME_HEIGHT);
			}
		}
		
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
		} else if (element instanceof GuiList) {
			GuiList guiList = (GuiList) element;
			for(Gui gui : slotsDrawn) {
				removeGuiElement(gui);
			}
			slotsDrawn.clear();
			PlanetRegion region = planetInfo.planetRegions.get(guiList.selectedIndex);
			for(int i = 0; i < region.exploreYield.size(); i++) {
				GuiInvSlot slot = new GuiInvSlot(i*100, 0, 0.08f, this, 0, 0);
				slot.setPlaceHolder(region.exploreYield.get(i));
				slotsDrawn.add(slot);
				addGuiElement(slot);
				slotWidth = slot.width;
			}
		}
	}
	
	@Override
	public void onOpen() {
		super.onOpen();
		EntityPlanet planet = (EntityPlanet) IngameState.getInstance().entityManager.getEntityAt(IngameState.getInstance().entityManager.player.asPoint(), true);
		planetInfo = World.getPlanetInfo(planet);
		GuiList list = new GuiList((int)(0.05f*width), (int)(0.15f*height), 0.32f, this);
		ArrayList<ListData> data = new ArrayList<>();
		for(PlanetRegion region : planetInfo.planetRegions) {
			data.add(new ListData(region.locationName));
		}
		list.addRows(data);
		guiElements.add(list);
		GuiProgressBar bar = new GuiProgressBar(0, 0, 0.08f, this);
		guiElements.add(bar);
		bar.setText("Mission Progress");
		GuiButton exploreButton = new GuiButton("Explore", 0, 0, 0.08f, this);
		guiElements.add(exploreButton);
		onStateChange(list); 
	}
	
	@Override
	public boolean shouldOpen() {
		EntityPlanet planet = (EntityPlanet) IngameState.getInstance().entityManager.getEntityAt(IngameState.getInstance().entityManager.player.asPoint(), true);
		return planet != null;
	}
	
	@Override
	public void onClose() {
		super.onClose();
		guiElements.clear();
		planetInfo = null;
	}
}
