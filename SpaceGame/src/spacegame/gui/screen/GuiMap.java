package spacegame.gui.screen;

import org.lwjgl.input.*;
import org.newdawn.slick.*;
import org.newdawn.slick.geom.*;

import spacegame.*;
import spacegame.core.*;
import spacegame.entity.*;
import spacegame.entity.environment.*;
import spacegame.gui.*;
import spacegame.gui.widgets.*;
import spacegame.other.*;
import spacegame.other.GameUtilities.Point;

public class GuiMap extends Gui {
	
	public float mapRatio = 0.05f;
	public int centerX;
	public int centerY;
	
	public GuiCheckbox centerScreen;

	public GuiMap() {
		super(TextureHandler.uiImages.get("bg_white.png"), 0.5f);
		setBackgroundTint();
		centerScreen = new GuiCheckbox(0, 0, 0.1f, this, true);
		guiElements.add(centerScreen);
	}
	
	@Override
	public void renderBackground(Graphics g, GameContainer container) {
		super.renderBackground(g, container);
		String s = "Orbital Map";
		g.setFont(GameConstants.GAME_FONT[3]);
		g.drawString(s, xStart+(width-g.getFont().getWidth(s))/2, yStart+0.07f*height);
		
		Rectangle mapBoundary = new Rectangle(xStart+(width-0.8f*width)/2, yStart+0.16f*height, 0.8f*width, 0.7f*height);
		
		centerScreen.xStart = (int) (mapBoundary.getX());
		centerScreen.yStart = (int) (mapBoundary.getMaxY()+0.025f*mapBoundary.getHeight());
		
		s = "Player Center";
		g.setFont(GameConstants.GAME_FONT[1]);
		g.drawString(s, centerScreen.xStart+centerScreen.width+0.025f*width, centerScreen.yStart+(centerScreen.height-g.getFont().getHeight(s))/2);
		
		g.setColor(Color.decode("0x1e0033"));
		g.fill(mapBoundary);
		g.setClip(mapBoundary);
		
		for(EntityBase e : CoreGame.getInstance().entityManager.getIngameEntities()) {
			if(e.inOrbit && CoreGame.getInstance().entityManager.getEntityByID(e.orbittingEntity) instanceof EntityStar) {
				float radius = e.orbitRadius*mapRatio;
				Point cP = translatePointToGame(e.getManager().getEntityByID(e.orbittingEntity).asPoint(), mapRatio);
				Circle c = new Circle(cP.x, cP.y, radius);
				g.setLineWidth(2);
				g.setColor(Color.white);
				g.draw(c);
			}
			
			Point pt = translatePointToGame(e.asPoint(), mapRatio);
			Image img = e instanceof EntityStar ? TextureHandler.getCustomImageByName("star").getScaledCopy(mapRatio) : e.model.getScaledCopy(mapRatio);
			img.setRotation(e.velocity.rotation);
			img.drawCentered(pt.x, pt.y);
		}
		
		Input input = container.getInput();
		Point p = translatePointToMap(new Point(input.getMouseX(), input.getMouseY()), mapRatio);
		EntityBase entity = CoreGame.getInstance().entityManager.getEntityAt(p, true);
		if(entity != null && entity instanceof EntityPlanet) {
			EntityPlanet planet = (EntityPlanet) entity;
			drawInfoBoxAtMousePos(g, container, World.getPlanetInfo(planet).planetName);
		}
		
		g.clearClip();
	}
	
	@Override
	public void onUpdate(int delta) {
		super.onUpdate(delta);
		Input input = CoreGame.getInstance().gContainer.getInput();
		if(input.isKeyDown(GameConstants.MAP_ZOOMIN)) {
			mapRatio = (float) Math.min(0.8f, mapRatio+0.1*delta/100f);
		} else if(input.isKeyDown(GameConstants.MAP_ZOOMOUT)) {
			mapRatio = (float) Math.max(0.1f, mapRatio-0.1*delta/100f);
		}
		
		if(centerScreen.selected) {
			centerX = (int) CoreGame.getInstance().entityManager.player.getVector().xCoord;
			centerY = (int) CoreGame.getInstance().entityManager.player.getVector().yCoord;
			mapRatio = Math.max(0.1f, 0.8f-0.0006f*getPlayerProximityToPlanet());
		}
		
		if(input.isKeyPressed(Keyboard.KEY_C)) {
			EntityPlayer player = CoreGame.getInstance().entityManager.player;
			Point pt = translatePointToMap(new Point(input.getAbsoluteMouseX(), input.getMouseY()), mapRatio);
			player.getVector().setCoords(pt.x, pt.y);
		}
	}
	
	@Override
	public boolean shouldPauseGame() {
		return false;
	}
	
	public int getPlayerProximityToPlanet() {
		int distance = Integer.MAX_VALUE;
		EntityPlayer player = CoreGame.getInstance().entityManager.player;
		for(EntityBase e : CoreGame.getInstance().entityManager.getIngameEntities()) {
			if(e instanceof EntityPlanet || e instanceof EntityStar) {
				int d1 = getDistance((int)player.getVector().xCoord, (int)player.getVector().yCoord, (int)e.getVector().xCoord, (int)e.getVector().yCoord);
				if(d1 < distance) {
					distance = d1;
				}
			}
		}
		return distance;
	}
	
	@Override
	public void mouseDragged(int oldx, int oldy, int newx, int newy) {
		super.mouseDragged(oldx, oldy, newx, newy);
		if(!centerScreen.selected) {
			centerX -= (newx-oldx)/mapRatio;
			centerY -= (newy-oldy)/mapRatio;
		}
	}
	
	@Override
	public void mouseWheelMoved(int change) {
		if(change > 0) {
			mapRatio = (float) Math.min(0.8f, mapRatio+0.1f*Math.abs(change/200f));
		} else if(change < 0) {
			mapRatio = (float) Math.max(0.1f, mapRatio-0.1f*Math.abs(change/200f));
		}
	}
	
	public int getDistance(int x1, int y1, int x2, int y2) {
		return (int) Math.sqrt(Math.pow(x2-x1, 2)+Math.pow(y2-y1, 2));
	}
	
	private Point translatePointToMap(Point p, float scale) {
		float xCoord = (p.x-xStart-width/2)/scale;
		float yCoord = (p.y-yStart-height/2)/scale;
		return new Point(xCoord + centerX, yCoord + centerY);
	}
	
	private Point translatePointToGame(Point p, float scale) {
		float xCoordDiff = p.x - centerX;
		float yCoordDiff = p.y - centerY;
		float xCoord = this.width/2 + xCoordDiff*scale;
		float yCoord = this.height/2 + yCoordDiff*scale;
		return new Point(this.xStart+xCoord, this.yStart+yCoord);
	}
}
