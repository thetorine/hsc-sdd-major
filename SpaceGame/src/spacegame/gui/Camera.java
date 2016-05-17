package spacegame.gui;

import org.newdawn.slick.geom.*;

import spacegame.entity.*;
import spacegame.other.*;

public class Camera {
	public float xCoord = 0f;
	public float yCoord = 0f;
	public CameraType currentCameraType;
	public Rectangle currentDisplayedMap;
	public EntityBase lockedEntity;
	
	public Camera(EntityBase b) { 
		currentCameraType = CameraType.LOCKED;
		lockedEntity = b;
		onUpdate(0);
	}
	
	public void onUpdate(int delta) {
		if(currentCameraType == CameraType.LOCKED && lockedEntity != null) {
			int xStart = (int) (lockedEntity.getVector().xCoord-GameConstants.GAME_WIDTH/2);
			int yStart = (int) (lockedEntity.getVector().yCoord-GameConstants.GAME_HEIGHT/2);
			int width = GameConstants.GAME_WIDTH;
			int height = GameConstants.GAME_HEIGHT;
			setRectangleCoords(xStart, yStart, width, height);
			currentDisplayedMap.setCenterX(lockedEntity.getVector().xCoord);
			currentDisplayedMap.setCenterY(lockedEntity.getVector().yCoord);
		}
		
		this.xCoord = currentDisplayedMap.getCenterX();
		this.yCoord = currentDisplayedMap.getCenterY();
	}
	
	public void setRectangleCoords(int xStart, int yStart, int width, int height) {
		if(currentDisplayedMap != null) {
			currentDisplayedMap.setBounds(xStart, yStart, width, height);
		} else {
			currentDisplayedMap = new Rectangle(xStart, yStart, width, height);
		}
	}
	
	public void setEntityFocus(EntityBase b) {
		lockedEntity = b;
		currentCameraType = CameraType.LOCKED;
	}
	
	public enum CameraType {
		LOCKED
	}
}
