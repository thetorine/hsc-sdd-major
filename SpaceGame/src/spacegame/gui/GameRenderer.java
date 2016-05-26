package spacegame.gui;

import org.newdawn.slick.*;
import org.newdawn.slick.geom.*;

import spacegame.*;
import spacegame.core.*;
import spacegame.entity.*;
import spacegame.entity.environment.*;
import spacegame.gui.screen.*;
import spacegame.other.*;
import spacegame.other.GameUtilities.Point;

public class GameRenderer {
	
	private CoreGame game = CoreGame.getInstance();
	private int gameWidth = GameConstants.GAME_WIDTH;
	private int gameHeight = GameConstants.GAME_HEIGHT;
	
	public void render(GameContainer container, Graphics g) {
		g.setColor(Color.white);
		
		TextureHandler.getCustomImageByName("bg").draw(0, 0, gameWidth, gameHeight);
		game.world.render(g);
		renderEntities(container, g);
		renderTargetIcon(container, g);
	}
	
	public void renderEntities(GameContainer container, Graphics g) {
		for(EntityBase e : game.entityManager.getIngameEntities()) {
			if(e.collisionShape != null) {
				Point p = e.asPoint();
				if(game.camera.currentDisplayedMap.intersects(e.collisionShape) || game.camera.currentDisplayedMap.contains(e.collisionShape)) {
					Point screenPos = GameUtilities.translateMapToGame(p);
					
					if(e instanceof EntityPlanet && !World.getPlanetInfo((EntityPlanet) e).liberated) {
						g.setColor(new Color(1, 1, 1, 0.1f));
						g.fill(new Circle(screenPos.x, screenPos.y, e.model.getWidth()*0.75f));
						g.setColor(new Color(0, 0, 0, 0.1f));
						g.fill(new Circle(screenPos.x, screenPos.y, e.model.getWidth()*0.6f));
						g.setColor(Color.white);
					}
					e.model.drawCentered(screenPos.x, screenPos.y);
					
					if(e instanceof EntityPlayer) {
						EntityPlayer player = (EntityPlayer) e;
						if(player.currentShield > 0) {
							Image shield = TextureHandler.getImageByName(TextureHandler.baseSheet, "shield3.png", 0.5f);
							shield.setRotation(player.getVector().rotation);
							shield.drawCentered(screenPos.x, screenPos.y);
						}
						
						if(player.onPlanet) {
							String s = "Press L To Land";
							g.drawString(s, (gameWidth-g.getFont().getWidth(s))/2, (gameHeight-g.getFont().getHeight(s))*0.6f);
						}
					} else if(e.maxHealth > 0) {
						float width = 0.03f*gameWidth;
						float height = 0.01f*gameHeight;
						float healthPercentage = ((float)e.currentHealth/e.maxHealth);
						Rectangle healthBox = new Rectangle(screenPos.x-width/2, screenPos.y+e.collisionShape.getBoundingCircleRadius()+10, width*healthPercentage, height);
						Rectangle baseBox = new Rectangle(screenPos.x-width/2, screenPos.y+e.collisionShape.getBoundingCircleRadius()+10, width, height);
						g.setColor(Color.red);
						g.fill(healthBox);
						g.setColor(Color.white);
						g.draw(baseBox);
					}
				}
			}
		}
		for(ParticleFX fx : game.world.particleList) {
			fx.effectSystem.render();
		}
	}
	
	public void renderTargetIcon(GameContainer container, Graphics g) {
		EntityPlayer player = game.entityManager.player;
		EntityBase target = player.selectedTarget;
		if(target != null) { 
			if(!game.camera.currentDisplayedMap.contains(target.collisionShape) && !(game.guiHierarchy.currentGui instanceof GuiMap)) {
				float bearing = GameUtilities.calculateBearing(player, target);
				float xCoord = (float) (gameWidth/2 + 200*Math.sin(Math.toRadians(bearing)));
				float yCoord = (float) (gameHeight/2 - 200*Math.cos(Math.toRadians(bearing)));
				Polygon poly = new Polygon(new float[] {0, 0, 10, 15, 20, 0});
				poly = (Polygon) poly.transform(Transform.createRotateTransform((float) Math.toRadians(bearing+180), xCoord, yCoord));
				poly.setCenterX(xCoord);
				poly.setCenterY(yCoord);
				g.setColor(Color.red);
				g.fill(poly);
			}
		}
	}
}
