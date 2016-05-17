//package spacegame.ui.screens;
//
//import java.util.*;
//
//import org.newdawn.slick.*;
//import org.newdawn.slick.fills.*;
//import org.newdawn.slick.geom.*;
//
//import spacegame.*;
//import spacegame.entity.*;
//import spacegame.other.*;
//import spacegame.other.GameUtilities.Point;
//import spacegame.ui.element.*;
//
//public class ContextMenu {
//	public boolean drawContextMenu;
//	public float xStart;
//	public float yStart;
//	public ArrayList<Element> elementsToDraw = new ArrayList<>();
//	public boolean displayed;
//	public IClickableListener listener;
//	public Point mapCoord;
//	
//	public ContextMenu(float xStart, float yStart, IClickableListener l) {
//		this.xStart = xStart;
//		this.yStart = yStart;
//		this.mapCoord = GameUtilities.translateGameToMap(new Point(xStart, yStart));
//		this.listener = l;
//	}
//	
//	public void render(GameContainer container, Graphics g) {
//		if(displayed) {
//			g.setFont(GameConstants.GAME_FONT[0]);
//			resetPoints();
//			for(int i = 0; i < elementsToDraw.size(); i++) {
//				ElementMenuItem item = (ElementMenuItem) elementsToDraw.get(i);
//				Point p = new Point(item.elementShape.getCenterX(), item.elementShape.getCenterY());
//				Rectangle r = new Rectangle(p.x-(item.width/2), p.y-(item.height/2), item.width, item.height);
//				GradientFill fill = null;
//				if(item.disabled) {
//					fill = new GradientFill(r.getMinX(), r.getMinY(), Color.lightGray, r.getMaxX(), r.getMaxY(), Color.lightGray);
//				} else {
//					if(item.highlighted) {
//						fill = new GradientFill(r.getMinX(), r.getMinY(), Color.blue, r.getMaxX(), r.getMaxY(), Color.blue);
//					} else {
//						fill = new GradientFill(r.getMinX(), r.getMinY(), Color.white, r.getMaxX(), r.getMaxY(), Color.white);
//					}
//				}
//				g.fill(r, fill);
//				
//				g.setColor(item.highlighted ? Color.white : Color.black);
//				String s = item.itemName;
//				Font f = g.getFont();
//				g.drawString(s, r.getMinX()+(item.width-f.getWidth(s))/2, r.getMinY()+(item.height-f.getHeight(s))/2);
//				
//				if(item.highlighted) {
//					listener.onHighlight(item);
//				}
//			}
//		}
//	}
//	
//	public void mousePressed(int button, int x, int y) {
//		switch(button) {
//			case 0: {
//				for(int i = 0; i < elementsToDraw.size(); i++) {
//					ElementMenuItem item = (ElementMenuItem) elementsToDraw.get(i);
//					if(!item.disabled) {
//						if(item.elementShape.contains(x, y)) {
//							item.elementPressed();
//						} 
//					} 
//				}
//				hide();
//				break;
//			}
//		}
//	}
//	
//	public void mouseMoved(int oldx, int oldy, int newx, int newy) {
//		for(int i = 0; i < elementsToDraw.size(); i++) {
//			ElementMenuItem item = (ElementMenuItem) elementsToDraw.get(i);
//			if(!item.disabled) {
//				if(item.elementShape.contains(newx, newy)) {
//					item.elementHighlighted();
//					item.highlighted = true;
//				} else {
//					item.highlighted = false;
//				}
//			}
//		}
//	}
//	
//	private void resetPoints() {
//		Point gameCoord = GameUtilities.translateMapToGame(mapCoord);
//		float x = gameCoord.x;
//		float y = gameCoord.y;
//		
//		ArrayList<Element> newList = new ArrayList<>();
//		for(int i = 0; i < elementsToDraw.size(); i++) {
//			ElementMenuItem pItem = (ElementMenuItem) elementsToDraw.get(i);
//			ElementMenuItem item = new ElementMenuItem(pItem.itemName, x, y+(i*(pItem.height)), pItem.width, pItem.height, pItem.backgroundColor, listener, this);
//			item.highlighted = pItem.highlighted;
//			item.disabled = pItem.disabled;
//			newList.add(item);
//		}
//		elementsToDraw = newList;
//	}
//	
//	public void show() {
//		displayed = true;
//	}
//	
//	public void hide() {
//		displayed = false;
//		CoreGame.getInstance().currentMenu = null;
//	}
//	
//	public void addMenuItem(String name) {
//		ElementMenuItem item = new ElementMenuItem(name, xStart, yStart+elementsToDraw.size()*25, 100, 25, Color.white, listener, this);
//		elementsToDraw.add(item);
//		this.listener.onActivation(item);
//
//	}
//	
//	public static class GameContextMenu implements IClickableListener {
//		@Override
//		public void onClick(Element menu) {
//			ElementMenuItem item = (ElementMenuItem) menu;
//			Point mapCoord = null;
//			if(item.parent != null) {
//				mapCoord = item.parent.mapCoord;
//			}
//			if(item.itemName.equals("Info")) {
////				//ScreenInfo screen = new ScreenInfo(10, 10, GameConstants.GAME_WIDTH/2, GameConstants.GAME_HEIGHT/2, null);
////				//CoreGame.getInstance().currentScreen = screen;
////				EntityBase b = CoreGame.getInstance().entityManager.getEntityAt(mapCoord, false);
////				if(b != null) {
////					screen.setEntityFocus(b);
////				} else {
////					screen.addInfo("Coordinates", String.format("[%d, %d]", (int)mapCoord.x, (int)mapCoord.y));
////				}
//			}
//			if(item.itemName.equals("Focus")) {
//				EntityBase b = CoreGame.getInstance().entityManager.getEntityAt(mapCoord, false);
//				CoreGame.getInstance().camera.setEntityFocus(b);
//			}
//			if(item.itemName.equals("Orbit")) {
//				EntityPlayer player = CoreGame.getInstance().entityManager.player;
//				if(!player.inOrbit) {
//					EntityBase b = CoreGame.getInstance().entityManager.getEntityAt(mapCoord, false);
//					player.setOrbit(player.getVector().xCoord, player.getVector().yCoord, getInitialRotation(player.asPoint(), b.asPoint()), b);
//				} else {
//					player.orbittingEntity = 0;
//					player.inOrbit = false;
//				}
//			}
//		}
//
//		@Override
//		public void onHighlight(Element item) {}
//
//		@Override
//		public void onActivation(Element menu) {
//		}
//		
//		private float getInitialRotation(Point from, Point to) {
//			float deltaX = to.x - from.x;
//			float deltaY = to.y - from.y;
//			float bearing = (float) Math.atan2(deltaY, deltaX);
//			return (float) (Math.toDegrees(bearing)+180);
//		}
//	}
//}
