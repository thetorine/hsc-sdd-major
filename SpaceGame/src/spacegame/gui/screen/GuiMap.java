package spacegame.gui.screen;

import org.newdawn.slick.*;
import org.newdawn.slick.geom.*;

import spacegame.*;
import spacegame.core.*;
import spacegame.entity.*;
import spacegame.gui.*;
import spacegame.other.*;
import spacegame.other.GameUtilities.Point;

public class GuiMap extends Gui {
	
	public float mapRatio = 0.25f;

	public GuiMap() {
		super(TextureHandler.uiImages.get("bg_white.png"), 0.5f);
		setBackgroundTint();
	}
	
	@Override
	public void renderBackground(Graphics g, GameContainer container) {
		super.renderBackground(g, container);
		String s = "Orbital Map";
		g.setFont(GameConstants.GAME_FONT[3]);
		g.drawString(s, xStart+(width-g.getFont().getWidth(s))/2, yStart+0.07f*height);
		
		Rectangle mapBoundary = new Rectangle(xStart+(width-0.8f*width)/2, yStart+0.16f*height, 0.8f*width, 0.75f*height);
		g.fill(mapBoundary);
		g.setClip(mapBoundary);
		
		for(EntityBase e : CoreGame.getInstance().entityManager.getIngameEntities()) {
			if(e.inOrbit) {
				float radius = e.orbitRadius*mapRatio;
				Point cP = translatePointToGame(e.getManager().getEntityByID(e.orbittingEntity).asPoint(), mapRatio);
				Circle c = new Circle(cP.x, cP.y, radius);
				g.setLineWidth(1);
				g.setColor(Color.orange);
				g.draw(c);
			}
			
			Point pt = translatePointToGame(e.asPoint(), mapRatio);
			Image img = e instanceof EntityStar ? TextureHandler.getCustomImageByName("star").getScaledCopy(mapRatio) : e.model.getScaledCopy(mapRatio);
			img.setRotation(e.velocity.rotation);
			img.drawCentered(pt.x, pt.y);
		}
		
		Input input = container.getInput();
		Point p = translatePointToMap(new Point(input.getAbsoluteMouseX(), input.getAbsoluteMouseY()), mapRatio);
		EntityBase entity = CoreGame.getInstance().entityManager.getEntityAt(p, true);
		if(entity != null && entity instanceof EntityPlanet) {
			String displayStr = World.getPlanetInfo((EntityPlanet) entity).planetName;
			g.setColor(Color.white);
			g.setFont(GameConstants.GAME_FONT[0]);
			float strWidth = g.getFont().getWidth(displayStr);
			float strHeight = g.getFont().getHeight(displayStr);
			Rectangle bgRect = new Rectangle(input.getAbsoluteMouseX(), input.getAbsoluteMouseY(), strWidth+25, strHeight+10);
			g.fill(bgRect);
			g.setColor(Color.black);
			g.drawString(displayStr, bgRect.getMinX()+(bgRect.getWidth()-strWidth)/2, bgRect.getMinY()+(bgRect.getHeight()-strHeight)/2);
		}
		
		g.clearClip();
	}
	
	@Override
	public void onUpdate(int delta) {
		super.onUpdate(delta);
		Input input = CoreGame.getInstance().gContainer.getInput();
		if(input.isKeyDown(GameConstants.MAP_ZOOMIN)) {
			mapRatio = (float) Math.min(0.3f, mapRatio+0.1*delta/1000f);
		} else if(input.isKeyDown(GameConstants.MAP_ZOOMOUT)) {
			mapRatio = (float) Math.max(0.05f, mapRatio-0.1*delta/1000f);
		}
	}
	
	@Override
	public boolean shouldPauseGame() {
		return false;
	}
	
	private Point translatePointToMap(Point p, float scale) {
		float xCoord = (p.x-xStart-width/2)/scale;
		float yCoord = (p.y-yStart-height/2)/scale;
		return new Point(xCoord + CoreGame.getInstance().camera.currentDisplayedMap.getCenterX(), yCoord + CoreGame.getInstance().camera.currentDisplayedMap.getCenterY());
	}
	
	private Point translatePointToGame(Point p, float scale) {
		float xCoordDiff = p.x - CoreGame.getInstance().camera.currentDisplayedMap.getCenterX();
		float yCoordDiff = p.y - CoreGame.getInstance().camera.currentDisplayedMap.getCenterY();
		float xCoord = this.width/2 + xCoordDiff*scale;
		float yCoord = this.height/2 + yCoordDiff*scale;
		return new Point(this.xStart+xCoord, this.yStart+yCoord);
	}
}
