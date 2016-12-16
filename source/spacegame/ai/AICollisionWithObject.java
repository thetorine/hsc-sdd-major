package spacegame.ai;

import spacegame.entity.*;
import spacegame.other.*;

public class AICollisionWithObject extends AIEntity {
	private EntityBase collisionWith;
	
	public AICollisionWithObject(EntityBase e) {
		super(e);
	}
	
	@Override
	public void update(int delta) {
		super.update(delta);
		//simply causes a colliding object to suddenly move in the opposite direction to the collision. 
		if(collisionWith != null && collisionWith.maxHealth > 0) {
			float bearing = GameUtilities.calculateBearing(collisionWith, entity);
			entity.getVector().accelerateInDirection(500, bearing, delta/1000f);
			collisionWith = null;
		}
	}

	//sets the entity that this class's entity is colliding with
	public void setCurrentCollider(EntityBase entity) {
		this.collisionWith = entity;
	}
}
