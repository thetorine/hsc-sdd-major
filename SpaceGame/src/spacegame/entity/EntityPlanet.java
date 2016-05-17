package spacegame.entity;

import java.util.*;

import org.newdawn.slick.*;

import spacegame.core.*;

public class EntityPlanet extends EntityBase {
	
	private String resourceName;
	private Image planetResource;
	
	public EntityPlanet(String resourceName) {
		this.resourceName = resourceName;
	}
	
	@Override
	public void update(int delta) {
		super.update(delta);
		float rotationalVelocity = 2f;
		getVector().rotation += rotationalVelocity*delta/1000f;
	}

	@Override
	public EntityType getType() {
		return EntityType.Planet;
	}

	@Override
	public void setSpawnCoords() {
		
	}
	
	@Override
	public int getMaxVelocity() {
		return 0;
	}

	@Override
	public int getAcceleration() {
		return 0;
	}

	@Override
	public void setModel() {
		this.model = planetResource;
	}

	@Override
	public String getModelName() {
		return resourceName;
	}

	@Override
	public void onLoad() {
		this.planetResource = TextureHandler.getCustomImageByName(resourceName);		
	}
	
	@Override
	public void addSavableData(HashMap<String, Object> savableMap) {
		savableMap.put("resourceName", resourceName);
	}

	@Override
	public int getRenderWeight() {
		return 3;
	}
}
