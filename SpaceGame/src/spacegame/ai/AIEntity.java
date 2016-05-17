package spacegame.ai;

import spacegame.entity.*;

public class AIEntity {
	public EntityBase entity;
	
	public AIEntity(EntityBase e) {
		this.entity = e;
	}
	
	public void update(int delta) {}
}
