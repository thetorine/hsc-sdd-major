package spacegame.other;

import spacegame.*;
import spacegame.core.*;
import spacegame.entity.*;
import spacegame.inventory.*;

public class CommandListener {
	
	public static void performCommand(String command, String[] args) {
		if(command.equals("spawn")) {
			for(int i = 0; i < Integer.parseInt(args[0]); i++) { 
				CoreGame.getInstance().entityManager.spawnEntity(new EntityEnemy());
			}
		} else if(command.equals("clear")) {
			EntityManager manager = CoreGame.getInstance().entityManager;
			for(EntityBase entity : manager.getIngameEntities()) {
				if(entity instanceof EntityEnemy) {
					manager.despawnEntity(entity);
				}
			}
		} else if(command.equals("heal")) {
			CoreGame.getInstance().entityManager.player.setMaxHealth(1000);
		} else if(command.equals("immortal")) {
			CoreGame.getInstance().entityManager.player.isImmortal ^= true;
		} else if(command.equals("give")) {
			CoreGame.getInstance().entityManager.player.inventory.addItemStack(new ItemStack(Item.loadedItems.get(Integer.parseInt(args[0])), Integer.parseInt(args[1])));
		} else if(command.equals("respawn")) {
			CoreGame.getInstance().entityManager.player = new EntityPlayer();
			CoreGame.getInstance().firstLoad = true;
			CoreGame.getInstance().entityManager.spawnEntity(CoreGame.getInstance().entityManager.player);
			CoreGame.getInstance().firstLoad = false;
			CoreGame.getInstance().camera.setEntityFocus(CoreGame.getInstance().entityManager.player);
		}
	}
}
