package spacegame.gui.screen;

import java.util.*;

import org.newdawn.slick.*;
import org.newdawn.slick.geom.*;

import spacegame.*;
import spacegame.core.*;
import spacegame.entity.*;
import spacegame.gameplay.*;
import spacegame.gameplay.UpgradeManager.*;
import spacegame.gui.*;
import spacegame.gui.widgets.*;
import spacegame.gui.widgets.EventListener;
import spacegame.gui.widgets.GuiList.*;
import spacegame.other.*;

public class GuiUpgrade extends Gui implements EventListener {
	
	private GuiList guiList;
	private GuiButton upgradeAbility;

	public GuiUpgrade() {
		super(TextureHandler.uiImages.get("bg_green.png"), 0.4f);
		setBackgroundTint();
		guiList = new GuiList((int)(0.05f*width), (int)(0.15f*height), 0.32f, this);
		ArrayList<ListData> data = new ArrayList<>();
		EntityPlayer player = CoreGame.getInstance().entityManager.player;
		for(Upgrade upgrade : player.manager.availableUpgrades) {
			data.add(new ListData(upgrade.name));
		}
		guiList.addRows(data);
		guiElements.add(guiList);
		
		upgradeAbility = new GuiButton("Upgrade", 0.07f, this);
		addGuiElement(upgradeAbility);
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
		
		g.setFont(GameConstants.GAME_FONT[3]);
		g.drawString("Upgrade Menu", xStart+(width-g.getFont().getWidth("Upgrade Menu"))/2, yStart+0.07f*height);
		g.setFont(GameConstants.GAME_FONT[0]);
		
		Color c = g.getColor();
		Color c1 = new Color(Color.darkGray);
		g.setColor(new Color(c1.r, c1.g, c1.b, 0.5f));
		Rectangle infoRect = new Rectangle(listXEnd+0.05f*width, listYStart+0.04f*height, width-(listXEnd-xStart)-0.1f*width, height-(listYStart-yStart)-0.13f*height);
		g.fill(infoRect);
		
		g.setColor(Color.black);
		
		int maxWidth = (int) (infoRect.getWidth() - 2*0.05*width);
		int lastHeight = (int) (infoRect.getMinY()+0.05*height);
		
		EntityPlayer player = CoreGame.getInstance().entityManager.player;
		UpgradeManager manager = player.manager;
		Upgrade upgrade = manager.getUpgrade(guiList.pluggedData.get(guiList.selectedIndex).displayName);
		
		lastHeight = (int) wrapText(upgrade.name, (float) (infoRect.getMinX()+0.05*width), lastHeight, maxWidth, g);
		lastHeight = (int) wrapText("Current Level: " + upgrade.level, (float) (infoRect.getMinX()+0.05*width), lastHeight, maxWidth, g);
		lastHeight = (int) wrapText("Max Level: " + upgrade.maxLevel, (float) (infoRect.getMinX()+0.05*width), lastHeight, maxWidth, g);
		if(upgrade.level < upgrade.maxLevel) {
			lastHeight = (int) wrapText("Next Level: " + (upgrade.level+1), (float) (infoRect.getMinX()+0.1*width), lastHeight, maxWidth, g);
		}
		lastHeight = (int) wrapText(upgrade.getUpgradeText(), (float) (infoRect.getMinX()+0.1*width), lastHeight, maxWidth, g);
		
		upgradeAbility.xStart = (int) (infoRect.getMinX() + (infoRect.getWidth()-upgradeAbility.width)/2);
		upgradeAbility.yStart = lastHeight;
		lastHeight += 0.02f*GameConstants.GAME_HEIGHT + upgradeAbility.height;
		
		g.setColor(c);
	}

	@Override
	public void onStateChange(Gui element) {
		if (element instanceof GuiButton) {
			GuiButton button = (GuiButton) element;
			if(button.buttonName.equals("Upgrade")) {
				EntityPlayer player = CoreGame.getInstance().entityManager.player;
				UpgradeManager manager = player.manager;
				Upgrade upgrade = manager.getUpgrade(guiList.pluggedData.get(guiList.selectedIndex).displayName);
				upgrade.upgrade();
				if(upgrade.level == upgrade.maxLevel) {
					button.buttonName = "Upgraded!";
				}
			}
		}
	}
}
