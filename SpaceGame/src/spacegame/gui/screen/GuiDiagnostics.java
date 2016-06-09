package spacegame.gui.screen;

import org.newdawn.slick.*;

import spacegame.core.*;
import spacegame.gui.*;
import spacegame.other.*;

public class GuiDiagnostics extends Gui {
	
	public int maxMemUsed;

	public GuiDiagnostics() {
		super(AssetManager.uiImages.get("bg_white.png"), 0.52f);
		background.setAlpha(0);
	}
	
	@Override
	public void renderBackground(Graphics g, GameContainer container) {
		super.renderBackground(g, container);
		g.setColor(Color.white);
		int lastHeight = yStart;
		int x = (int) (0.008f*GameConstants.GAME_WIDTH);
		int maxWidth = (int) (0.7f*GameConstants.GAME_WIDTH);
		
		Runtime runtime = Runtime.getRuntime();
		
		int freeMem = (int) (runtime.freeMemory()/1000000);
		int maxMem = (int) (runtime.totalMemory()/1000000);
		int mem = (int) (runtime.totalMemory()-runtime.freeMemory())/1000000;
		int percentage = (int) ((float)mem/(float)maxMem*100f);
		maxMemUsed = mem > maxMemUsed ? mem : maxMemUsed;
		
		lastHeight = (int) wrapText(String.format("Memory Used: %d mb (%d", mem, percentage) + "%)", x, lastHeight, maxWidth, g);
		lastHeight = (int) wrapText("Max Memory Used: " + maxMemUsed + " mb", x, lastHeight, maxWidth, g);
		lastHeight = (int) wrapText("Memory: " + maxMem + " mb", x, lastHeight, maxWidth, g);
		lastHeight = (int) wrapText("Free Memory: " + freeMem + " mb", x, lastHeight, maxWidth, g);
	}
	
	@Override
	public boolean shouldPauseGame() {
		return false;
	}
	
	@Override
	public boolean supportsHierachy() {
		return true;
	}
}
