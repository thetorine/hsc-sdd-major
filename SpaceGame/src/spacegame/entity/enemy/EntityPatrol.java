package spacegame.entity.enemy;

import java.util.*;

import spacegame.ai.*;
import spacegame.core.*;
import spacegame.core.CollisionDetector.*;
import spacegame.entity.*;

public class EntityPatrol extends EntityEnemy implements ICollisionDetection {
	
	@Override
	public EntityType getType() {
		return EntityType.Patrol;
	}

	@Override
	public void setSpawnCoords() {
		setMaxHealth(100);
	}

	@Override
	public int getMaxVelocity() {
		return 350;
	}

	@Override
	public int getAcceleration() {
		return 150;
	}

	@Override
	public void setModel() {
		this.model = TextureHandler.getImageByName(TextureHandler.baseSheet, getModelName(), 0.25f);
		attackModifier = 2;
	}
	
	public void addSavableData(HashMap<String, Object> savableMap) {
		super.addSavableData(savableMap);
	}
	
	@Override
	public void loadSavableData(HashMap<String, String> rawData) {
		super.loadSavableData(rawData);
	}

	@Override
	public String getModelName() {
		return "enemyBlue1.png";
	}

	@Override
	public void onLoad() {
		super.onLoad();
	}

	@Override
	public int getRenderWeight() {
		return 1;
	}

	@Override
	public void onCollision(EntityBase collisionWith) {
		for(AIEntity ai : aiControllers) {
			if(ai instanceof AICollisionWithObject) {
				AICollisionWithObject collisionAi = (AICollisionWithObject) ai;
				collisionAi.setCurrentCollider(collisionWith);
			}
		}
	}
}
