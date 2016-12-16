package spacegame.gameplay;

import org.newdawn.slick.Color;
import spacegame.core.World;
import spacegame.entity.EntityBase;
import spacegame.entity.enemy.EntitySpawner;
import spacegame.entity.environment.EntityMeteor;
import spacegame.entity.environment.EntityPlanet;
import spacegame.entity.environment.EntityStar;
import spacegame.gamestates.IngameState;
import spacegame.inventory.Item;
import spacegame.inventory.ItemStack;
import spacegame.other.GameUtilities.Point;

import java.util.ArrayList;

//a class used to spawn the planets and the system at the start of a game.
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
		IngameState.getInstance().world.createStarAt(systemStar.asPoint());
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
		
		if(IngameState.getInstance().firstLoad) {
			EntityMeteor meteor = new EntityMeteor();
			float mxCoord = (float) (xCoord + distance/5*Math.sin(Math.toRadians(angle)));
			float myCoord = (float) (yCoord - distance/5*Math.cos(Math.toRadians(angle)));
			meteor.getVector().setCoords(mxCoord, myCoord);
			meteor.setOrbit(mxCoord, myCoord, getInitialRotation(new Point(mxCoord, myCoord), new Point(xCoord, yCoord)), planet);
			
			IngameState.getInstance().entityManager.spawnEntity(meteor);
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
			IngameState.getInstance().entityManager.spawnEntity(b);
			if(b instanceof EntityPlanet) {
				EntitySpawner spawner = new EntitySpawner(b.id);
				spawner.inventory.addItemStack(new ItemStack(Item.goldArmor, 1));
				spawner.getVector().setCoords(b.getVector().xCoord, b.getVector().yCoord);
				spawner.strength = World.getPlanetInfo((EntityPlanet) b).difficulty;
				IngameState.getInstance().entityManager.spawnEntity(spawner);
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
