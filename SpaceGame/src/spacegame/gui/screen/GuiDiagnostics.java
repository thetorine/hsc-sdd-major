package spacegame.gui.screen;

import org.newdawn.slick.*;

import spacegame.core.*;
import spacegame.gui.*;
import spacegame.other.*;

public class GuiDiagnostics extends Gui {
	
	public int maxMemUsed;

	public GuiDiagnostics() {
		super(TextureHandler.uiImages.get("bg_white.png"), 0.52f);
		background.setAlpha(0);
	}
	
	@Override
	public void renderBackground(Graphics g, GameContainer container) {
		super.renderBackground(g, container);
		g.setColor(Color.white);
		int lastHeight = yStart;
		int x = (int) (0.01f*GameConstants.GAME_WIDTH);
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

}
