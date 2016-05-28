package spacegame.gameplay;

import java.util.*;

import org.newdawn.slick.*;
import org.newdawn.slick.geom.*;

import spacegame.entity.*;
import spacegame.entity.EntityBase.*;
import spacegame.other.*;
import spacegame.other.GameUtilities.Point;

public class Minimap {
	public float edgeDistance;
	public float mapRadius;
	public float visionRadius = 750f;
	public float ratio;
	
	public HashMap<EntityType, MapDesignation> designations = new HashMap<>();
	
	public Minimap() {
		edgeDistance = GameConstants.GAME_HEIGHT*0.05f;
		mapRadius = GameConstants.GAME_HEIGHT*0.15f;
		ratio = visionRadius/mapRadius;
		designations.put(EntityType.Enemy, new MapDesignation(mapRadius/30, Color.red));
		designations.put(EntityType.Planet, new MapDesignation(mapRadius/25, Color.pink));
		designations.put(EntityType.Player, new MapDesignation(mapRadius/20, Color.cyan));
		designations.put(EntityType.Spawner, new MapDesignation(mapRadius/25, Color.gray));
		designations.put(EntityType.Star, new MapDesignation(mapRadius/25, Color.orange));
		designations.put(EntityType.Meteor, new MapDesignation(mapRadius/25, Color.white));
	}
	
	public void render(Graphics g, float width, float height, EntityPlayer player) {
		float cX = width-edgeDistance-mapRadius;
		float cY = edgeDistance+mapRadius;
		g.setColor(new Color(0f, 0, 0, 15f));
		g.fill(new Circle(cX, cY, mapRadius*1.2f));
		g.setColor(new Color(0f, 0.35f, 0.06f));
		g.fill(new Circle(cX, cY, mapRadius));
		
		double playerRotation = player.getVector().rotation;
		
		g.setColor(Color.green);
		for(int i = 0; i <= 8; i++) {
			double angle = 2*Math.PI/8*i - playerRotation;
			g.drawLine(cX, cY, cX + mapRadius*(float)Math.sin(angle), cY - mapRadius*(float)Math.cos(angle));
			
			if(i > 0 && i <= 3) {
				float radius = mapRadius/3*i;
				g.draw(new Circle(cX, cY, radius));
			}
		}
		
		g.setColor(Color.white);
		String str = "N";
		g.drawString(str, (float) (cX + (mapRadius*1.1)*Math.sin(-playerRotation)-0.5*g.getFont().getWidth(str)), (float) (cY - (mapRadius*1.1)*Math.cos(-playerRotation)-0.5*g.getFont().getHeight(str)));
		str = "E";
		g.drawString(str, (float) (cX + (mapRadius*1.1)*Math.sin(-playerRotation+Math.PI/2)-0.5*g.getFont().getWidth(str)), (float) (cY - (mapRadius*1.1)*Math.cos(-playerRotation+Math.PI/2)-0.5*g.getFont().getHeight(str)));
		str = "W";
		g.drawString(str, (float) (cX + (mapRadius*1.1)*Math.sin(-playerRotation-Math.PI/2)-0.5*g.getFont().getWidth(str)), (float) (cY - (mapRadius*1.1)*Math.cos(-playerRotation-Math.PI/2)-0.5*g.getFont().getHeight(str)));
		str = "S";
		g.drawString(str, (float) (cX + (mapRadius*1.1)*Math.sin(-playerRotation+Math.PI)-0.5*g.getFont().getWidth(str)), (float) (cY - (mapRadius*1.1)*Math.cos(-playerRotation+Math.PI)-0.5*g.getFont().getHeight(str)));
		
		str = "P";
		g.setColor(Color.cyan);
		g.drawString(str, (float) (cX - 0.5*g.getFont().getWidth(str)), (float) (cY - (mapRadius*1.1) - 0.5*g.getFont().getHeight(str)));
		
		for(EntityBase b : player.getManager().getIngameEntities()) {
			MapDesignation md = designations.get(b.getType());
			if(md != null) {
				g.setColor(md.color);
				if(!(b instanceof EntityPlayer)) {
					double distance = getDistanceToPoint(player.asPoint(), b.asPoint());
					if(distance <= visionRadius) {
						distance /= ratio;
						double angle = Math.atan2(b.asPoint().y-player.asPoint().y, b.asPoint().x-player.asPoint().x) - Math.PI/2 - playerRotation;

						g.fill(new Circle((float) (cX - distance*Math.sin(angle)), (float) (cY + distance*Math.cos(angle)), md.size));
					}
				} else {
					g.fill(new Circle(cX, cY, md.size));
				}
 			}
		}
	}
	
	private double getDistanceToPoint(Point p1, Point p2) {
		return Math.sqrt(Math.pow(p1.x-p2.x, 2) + Math.pow(p1.y-p2.y, 2));
	}
	
	public static class MapDesignation {
		public float size;
		public Color color;
		
		public MapDesignation(float f, Color c) {
			this.size = f;
			this.color = c;
		}
	}
}
