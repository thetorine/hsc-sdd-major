package spacegame.inventory;

import spacegame.entity.EntityBase;
import spacegame.entity.EntityPlayer;
import spacegame.entity.enemy.EntityPatrol;
import spacegame.entity.enemy.EntitySpawner;
import spacegame.entity.weapon.EntityBlaster;
import spacegame.entity.weapon.EntityMissile;
import spacegame.gamestates.IngameState;

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
		if(entity.inventory.weaponCD == 0 ) {
			switch(itemName) {
				case "Photon Blaster": {
					if((boolean) data[2]) {
						doubleBlaster(entity, (boolean) data[1]);
					} else {
						IngameState.getInstance().entityManager.spawnEntity(new EntityBlaster(((EntityBase) data[0]).id, (boolean) data[1]));
					}
					entity.inventory.weaponCD = this.maxTimer;
					break;
				}
				case "Missile Launcher": {
					EntityMissile m = new EntityMissile(((EntityBase) data[0]).id);
					IngameState.getInstance().entityManager.spawnEntity(m);
					entity.inventory.weaponCD = this.maxTimer;
					break;
				}
			}
		}
	}
	
	
	public void doubleBlaster(EntityBase shooter, boolean invertShot) {
		EntityBlaster blaster1 = new EntityBlaster(shooter.id, invertShot).setCoords(false);
		blaster1.getVector().xCoord = (float) (shooter.getVector().xCoord + 10*Math.sin(shooter.getVector().rotation-Math.PI/2)*(invertShot ? -1 : 1));
		blaster1.getVector().yCoord = (float) (shooter.getVector().yCoord - 10*Math.cos(shooter.getVector().rotation-Math.PI/2)*(invertShot ? -1 : 1));
		EntityBlaster blaster2 = new EntityBlaster(shooter.id, invertShot).setCoords(false);
		blaster2.getVector().xCoord = (float) (shooter.getVector().xCoord + 10*Math.sin(shooter.getVector().rotation+Math.PI/2)*(invertShot ? -1 : 1));
		blaster2.getVector().yCoord = (float) (shooter.getVector().yCoord - 10*Math.cos(shooter.getVector().rotation+Math.PI/2)*(invertShot ? -1 : 1));
		shooter.getManager().spawnEntity(blaster1);
		shooter.getManager().spawnEntity(blaster2);
	}
	
	public void onImpactWith(EntityBase shootingEntity, EntityBase e) {
		if(shootingEntity instanceof EntityPlayer) {
			e.damageEntity(baseDamage + ((EntityPlayer) shootingEntity).upgradeManager.getUpgrade("Damage").getModifier());
			if(e.currentHealth <= 0) {
				((EntityPlayer) shootingEntity).pointsGained += (e instanceof EntitySpawner ? 30 : (e instanceof EntityPatrol ? 10 : 15 ));
			}
		} else {
			e.damageEntity(baseDamage);
		}
	}
}
