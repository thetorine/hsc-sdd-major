package spacegame.entity.environment;

import spacegame.core.*;
import spacegame.entity.*;

public class EntityStar extends EntityBase {

	@Override
	public EntityType getType() {
		return EntityType.Star;
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
		this.model = TextureHandler.getCustomImageByName(getModelName()).getScaledCopy(0);
	}

	@Override
	public String getModelName() {
		return "star";
	}

	@Override
	public void onLoad() {
		
	}

	@Override
	public int getRenderWeight() {
		return 3;
	}

}
