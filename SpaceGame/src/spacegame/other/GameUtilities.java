package spacegame.other;

import spacegame.*;
import spacegame.entity.*;

public class GameUtilities {
	
	public static float calculateBearing(EntityBase to, EntityBase from) {
		float deltaX = to.getVector().xCoord - from.getVector().xCoord;
		float deltaY = to.getVector().yCoord - from.getVector().yCoord;
		float bearing = (float) Math.atan2(deltaY, deltaX);
		return (float) (bearing - Math.PI/2);
	}
	
	public static Point translateMapToGame(Point p) {
		float xCoordDiff = p.x - CoreGame.getInstance().camera.currentDisplayedMap.getCenterX();
		float yCoordDiff = p.y - CoreGame.getInstance().camera.currentDisplayedMap.getCenterY();
		float xCoord = GameConstants.GAME_WIDTH/2 + xCoordDiff;
		float yCoord = GameConstants.GAME_HEIGHT/2 + yCoordDiff;
		return new Point(xCoord, yCoord);
	}
	
	public static Point translateGameToMap(Point p) {
		float xCoordDiff = (p.x-GameConstants.GAME_WIDTH/2);
		float yCoordDiff = (p.y-GameConstants.GAME_HEIGHT/2);
		float xCoord = xCoordDiff + CoreGame.getInstance().camera.currentDisplayedMap.getCenterX();
		float yCoord = yCoordDiff + CoreGame.getInstance().camera.currentDisplayedMap.getCenterY();
		return new Point(xCoord, yCoord);
	}
	
	public static class Point {
		public float x;
		public float y;
		
		public Point(float x, float y) {
			this.x = x;
			this.y = y;
		}
		
		public void scalePoints(float s) {
			this.x *= s;
			this.y *= s;
		}
		
		public Point copy() {
			return new Point(x, y);
		}
		
		@Override
		public String toString() {
			return String.format("x: %f, y: %f", x, y);
		}
	}
}
