package spacegame.gui.screen;

import org.lwjgl.input.*;
import org.newdawn.slick.*;
import org.newdawn.slick.geom.*;

import spacegame.*;
import spacegame.core.*;
import spacegame.gui.*;
import spacegame.other.*;

public class GuiCmdOverlay extends Gui {

	public static String ALPHANUMERICAL = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
	public String typedString = "";
	public int initialDelay;
	
	public GuiCmdOverlay() {
		super(TextureHandler.uiImages.get("bar_base.png"), 0);
		setBackgroundTint();
		background.setAlpha(0);
	}
	
	@Override
	public void onUpdate(int delta) {
		super.onUpdate(delta);
		initialDelay++;
		
		Input input = CoreGame.getInstance().gContainer.getInput();
		input.enableKeyRepeat();
		
		int keyCode = Keyboard.getEventKey();
		if(input.isKeyPressed(keyCode) && initialDelay > 10) {
			char c = Keyboard.getEventCharacter();
			if(Character.isLetterOrDigit(c)) {
				typedString += c;
			} else if(Character.isSpaceChar(c)) {
				if(!typedString.endsWith(" ")) {
					typedString += " ";
				}
			} else if(typedString.length() > 0 && keyCode == Keyboard.KEY_BACK) {
				typedString = typedString.substring(0, typedString.length()-1);
			} else if(keyCode == Keyboard.KEY_RETURN) {
				if(typedString.length() > 0) {
					String command = typedString.split(" ")[0];
					String[] args = typedString.replace(command + " ", "").split(" ");
					CommandListener.performCommand(command, args);
					CoreGame.getInstance().currentGui = null;
					CoreGame.getInstance().gamePaused = false;
				}
			}
		}
	}
	
	@Override
	public void drawBackgroundTint(Graphics g) {
		super.drawBackgroundTint(g);
		g.setColor(Color.white);
		Rectangle r = new Rectangle(0, 0.95f*GameConstants.GAME_HEIGHT, GameConstants.GAME_WIDTH, 0.05f*GameConstants.GAME_HEIGHT);
		g.fill(r);

		g.setColor(Color.black);
		g.drawString(typedString, 0.01f*GameConstants.GAME_WIDTH, r.getMinY() + (r.getHeight()-g.getFont().getHeight(ALPHANUMERICAL))/2);
		
		int timeModifier = (int) (System.currentTimeMillis()%1000);
		if(timeModifier < 500) {
			Rectangle typeInd = new Rectangle(0.01f*GameConstants.GAME_HEIGHT+g.getFont().getWidth(typedString)+0.005f*GameConstants.GAME_WIDTH, r.getMinY()+(r.getHeight()-g.getFont().getHeight(ALPHANUMERICAL))/2, 0.005f*GameConstants.GAME_WIDTH, g.getFont().getHeight(ALPHANUMERICAL));
			g.fill(typeInd);
		}
	}

}
