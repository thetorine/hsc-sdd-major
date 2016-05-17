package spacegame.gui;

import org.newdawn.slick.*;
import org.newdawn.slick.geom.*;

import spacegame.*;
import spacegame.core.*;
import spacegame.entity.*;
import spacegame.gui.screen.*;
import spacegame.other.*;

public class GuiIngame {
	
	public Minimap gameMinimap = new Minimap();
	
	public GuiIngame() {
		float sideLength = 0.05f*GameConstants.GAME_WIDTH;
		for(int i = 0; i < 4; i++) {
			//TODO IMPLEMENT WEAPON SLOTS AGAIN
		}
	}

	public void render(Graphics g, GameContainer container, float width, float height) {
		EntityPlayer player = CoreGame.getInstance().entityManager.player;
		
		if(CoreGame.getInstance().currentGui == null || CoreGame.getInstance().currentGui instanceof GuiDiagnostics) {
			//health bar
			g.setColor(Color.white);
			float healthPercentage = ((float)player.currentHealth/player.maxHealth);
			int bottomBarWidth = (int) (0.35*width);
			int bottonBarHeight = (int) (0.06*height);
			Rectangle baseHealthBar = new Rectangle((width-bottomBarWidth)/2, 0.83f*height, bottomBarWidth, bottonBarHeight);
			Rectangle healthBar = new Rectangle((width-bottomBarWidth)/2, 0.83f*height, bottomBarWidth*healthPercentage, bottonBarHeight);
			g.fill(baseHealthBar);
			g.setColor(Color.green.darker(0.8f-healthPercentage));
			g.fill(healthBar);
			g.setColor(Color.black);
			
			String health = "Health: " + player.currentHealth + "/" + player.maxHealth;
			g.drawString(health, healthBar.getX() + 20, healthBar.getY() + (healthBar.getHeight()-g.getFont().getHeight(health))/2);
			
			//shield bar
			g.setColor(Color.white);
			float shieldPercentage = ((float)player.currentShield/player.maxShield);
			Rectangle baseShieldBar = new Rectangle(baseHealthBar.getMinX(), baseHealthBar.getMaxY(), bottomBarWidth, bottonBarHeight);
			Rectangle shieldBar = new Rectangle(baseHealthBar.getMinX(), baseHealthBar.getMaxY(), bottomBarWidth*shieldPercentage, bottonBarHeight);
			g.fill(baseShieldBar);
			g.setColor(Color.cyan.darker(0.8f-shieldPercentage));
			g.fill(shieldBar);
			g.setColor(Color.black);
			
			String shield = "Shield: " + (int)Math.ceil(player.currentShield) + "/" + player.maxShield;
			g.drawString(shield, shieldBar.getX() + 20, shieldBar.getY() + (shieldBar.getHeight()-g.getFont().getHeight(shield))/2);
			
			//map
			gameMinimap.render(g, width, height, player);
		}			
	}
	
	public void mousePressed(int button, int x, int y) {
	}
	
	public void mouseMoved(int oldx, int oldy, int newx, int newy) {
	}
}	
