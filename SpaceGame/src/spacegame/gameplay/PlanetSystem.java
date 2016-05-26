package spacegame.gameplay;

import java.util.*;

import org.newdawn.slick.*;

import spacegame.*;
import spacegame.core.*;
import spacegame.entity.*;
import spacegame.entity.enemy.*;
import spacegame.entity.environment.*;
import spacegame.inventory.*;
import spacegame.other.GameUtilities.*;

public class PlanetSystem {
	public Point systemCenter;
	public ArrayList<EntityBase> systemEntities;
	
	public PlanetSystem(float centerX, float centerY, Color starTint) {
		this.systemEntities = new ArrayList<>();
		this.systemCenter = new Point(centerX, centerY);
		
		EntityStar systemStar = new EntityStar();
		systemStar.setID();
		systemStar.getVector().setCoords(centerX, centerY);
		this.systemEntities.add(systemStar);
		CoreGame.getInstance().world.createStarAt(systemStar.asPoint());
	}
	
	public void addEntity(EntityBase b, float x, float y, boolean orbit) {
		b.getVector().setCoords(x, y);
		systemEntities.add(b);
		b.setID();
		if(orbit) {
			b.setOrbit(x, y, getInitialRotation(new Point(x, y), systemCenter), getSystemStar());
		}
	}
	
	public void addPlanet(EntityPlanet planet, String name, float distance, float angle) {
		float xCoord = (float) (systemCenter.x + distance*Math.sin(Math.toRadians(angle)));
		float yCoord = (float) (systemCenter.y - distance*Math.cos(Math.toRadians(angle)));
		this.addEntity(planet, xCoord, yCoord, true);
		
		//TODO just a test for entities orbiting entities that are orbiting. 
		if(CoreGame.getInstance().firstLoad) {
			EntityMeteor meteor = new EntityMeteor();
			float mxCoord = (float) (xCoord + distance/5*Math.sin(Math.toRadians(angle)));
			float myCoord = (float) (yCoord - distance/5*Math.cos(Math.toRadians(angle)));
			meteor.getVector().setCoords(mxCoord, myCoord);
			meteor.setOrbit(mxCoord, myCoord, getInitialRotation(new Point(mxCoord, myCoord), new Point(xCoord, yCoord)), planet);
			
			CoreGame.getInstance().entityManager.spawnEntity(meteor);
		}
	}
	
	private float getInitialRotation(Point from, Point to) {
		float deltaX = to.x - from.x;
		float deltaY = to.y - from.y;
		float bearing = (float) Math.atan2(deltaY, deltaX);
		return (float) (Math.toDegrees(bearing)+180);
	}
	
	public void spawnSystem() {
		for(EntityBase b : systemEntities) {
			CoreGame.getInstance().entityManager.spawnEntity(b);
			if(b instanceof EntityPlanet) {
				EntitySpawner spawner = new EntitySpawner(b.id);
				spawner.inventory.addItemStack(new ItemStack(Item.goldArmor, 1));
				spawner.getVector().setCoords(b.getVector().xCoord, b.getVector().yCoord);
				spawner.strength = World.getPlanetInfo((EntityPlanet) b).difficulty;
				CoreGame.getInstance().entityManager.spawnEntity(spawner);
			}
		}
	}
	
	private EntityStar getSystemStar() {
		for(EntityBase b : systemEntities) {
			if(b instanceof EntityStar) {
				return (EntityStar) b;
			}
		}
		return null;
	}
}
