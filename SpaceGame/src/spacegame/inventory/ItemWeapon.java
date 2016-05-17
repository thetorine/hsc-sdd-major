package spacegame.inventory;

import spacegame.*;
import spacegame.entity.*;

public class ItemWeapon extends Item {
	public int baseDamage;
	public int maxTimer;
	
	public ItemWeapon(String name, int dmg, int cd) {
		super(name);
		this.baseDamage = dmg;
		this.maxTimer = cd;
	}

	public void fire(Object[] data) {
		EntityBase entity = (EntityBase) data[0];
		if(entity.inventory.weaponCD == 0) {
			switch(itemName) {
				case "Photon Blaster": {
					if((boolean) data[2]) {
						doubleBlaster(entity, (boolean) data[1]);
					} else {
						CoreGame.getInstance().entityManager.spawnEntity(new EntityBlaster(((EntityBase) data[0]).id, (boolean) data[1]));
					}
					entity.inventory.weaponCD = this.maxTimer;
					break;
				}
				case "Missile Launcher": {
					EntityMissile m = new EntityMissile(((EntityBase) data[0]).id);
					CoreGame.getInstance().entityManager.spawnEntity(m);
					entity.inventory.weaponCD = this.maxTimer;
					break;
				}
			}
		}
	}
	
	
	public void doubleBlaster(EntityBase shooter, boolean invertShot) {
		EntityBlaster blaster1 = new EntityBlaster(shooter.id, invertShot).setCoords(false);
		blaster1.getVector().xCoord = (float) (shooter.getVector().xCoord + 10*Math.sin(Math.toRadians(shooter.getVector().rotation-90))*(invertShot ? -1 : 1));
		blaster1.getVector().yCoord = (float) (shooter.getVector().yCoord - 10*Math.cos(Math.toRadians(shooter.getVector().rotation-90))*(invertShot ? -1 : 1));
		EntityBlaster blaster2 = new EntityBlaster(shooter.id, invertShot).setCoords(false);
		blaster2.getVector().xCoord = (float) (shooter.getVector().xCoord + 10*Math.sin(Math.toRadians(shooter.getVector().rotation+90))*(invertShot ? -1 : 1));
		blaster2.getVector().yCoord = (float) (shooter.getVector().yCoord - 10*Math.cos(Math.toRadians(shooter.getVector().rotation+90))*(invertShot ? -1 : 1));
		shooter.getManager().spawnEntity(blaster1);
		shooter.getManager().spawnEntity(blaster2);
	}
	
	public void onImpactWith(EntityBase e) {
		e.damageEntity(baseDamage);
	}
}
