package spacegame.ai;

import spacegame.entity.*;

//just a base class for all things ai
public class AIEntity {
	public EntityBase entity;
	
	public AIEntity(EntityBase e) {
		this.entity = e;
	}
	
	public void update(int delta) {}
}
