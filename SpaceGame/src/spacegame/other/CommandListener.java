package spacegame.other;

import spacegame.entity.*;
import spacegame.entity.enemy.*;
import spacegame.gamestates.*;
import spacegame.inventory.*;

public class CommandListener {
	
	public static void performCommand(String command, String[] args) {
		if(command.equals("spawn")) {
			for(int i = 0; i < Integer.parseInt(args[0]); i++) { 
				IngameState.getInstance().entityManager.spawnEntity(new EntityEnemy());
			}
		} else if(command.equals("clear")) {
			EntityManager manager = IngameState.getInstance().entityManager;
			for(EntityBase entity : manager.getIngameEntities()) {
				if(entity instanceof EntityEnemy) {
					manager.despawnEntity(entity);
				}
			}
		} else if(command.equals("heal")) {
			IngameState.getInstance().entityManager.player.setMaxHealth(IngameState.getInstance().entityManager.player.maxHealth);
		} else if(command.equals("immortal")) {
			IngameState.getInstance().entityManager.player.isImmortal ^= true;
		} else if(command.equals("give")) {
			IngameState.getInstance().entityManager.player.inventory.addItemStack(new ItemStack(Item.loadedItems.get(Integer.parseInt(args[0])), Integer.parseInt(args[1])));
		} else if(command.equals("respawn")) {
			IngameState.getInstance().entityManager.player = new EntityPlayer();
			IngameState.getInstance().firstLoad = true;
			IngameState.getInstance().entityManager.spawnEntity(IngameState.getInstance().entityManager.player);
			IngameState.getInstance().firstLoad = false;
			IngameState.getInstance().camera.setEntityFocus(IngameState.getInstance().entityManager.player);
		}
	}
}
