package spacegame.gameplay;

import org.lwjgl.input.Keyboard;
import org.newdawn.slick.Input;
import spacegame.entity.EntityPlayer;
import spacegame.gamestates.IngameState;
import spacegame.gui.Gui;
import spacegame.gui.screen.GuiHUD;
import spacegame.inventory.Item;
import spacegame.other.GameConstants;

import java.util.ArrayList;

//manages the tutorials at the start of the game.
public class Tutorial {

    public int currentTutorial = 0;
    public ArrayList<TutorialMessage> tutorials = new ArrayList<>();

    public Tutorial(EntityPlayer player) {
        Input input = IngameState.getInstance().gContainer.getInput();

        tutorials.add(new TutorialMessage("Your galaxy has been invaded by an unknown race. Liberate the planets from under their control to win the game!" +
                " Press enter to proceed or spacebar to skip the tutorial.") {
            @Override
            public boolean isCompleted() {
                return input.isKeyPressed(Keyboard.KEY_RETURN);
            }
        });
        tutorials.add(new TutorialMessage("Use W to accelerate, A and D to rotate left and right and S to slow down.") {
            @Override
            public boolean isCompleted() {
                return input.isKeyPressed(GameConstants.UP) ||  input.isKeyPressed(GameConstants.DOWN) || input.isKeyPressed(GameConstants.LEFT) || input.isKeyPressed(GameConstants.RIGHT);
            }
        });
        tutorials.add(new TutorialMessage("Buy a Photon Blaster from the Shop. Press P to open the shop.") {
            @Override
            public boolean isCompleted() {
                return player.inventory.hasItem(Item.blaster);
            }
        });
        tutorials.add(new TutorialMessage("To open the inventory, press I. They by clicking on the photon blaster, move it to one of the four slots at the bottom.") {
            @Override
            public boolean isCompleted() {
                return player.inventory.isWeaponEquipped(Item.blaster);
            }
        });
        tutorials.add(new TutorialMessage("You have now equipped the Photon Blaster. Weapons can be cycled quickly using Q. Press spacebar to fire your weapon.") {
            @Override
            public boolean isCompleted() {
                return input.isKeyPressed(GameConstants.FIRE_WEAPON);
            }
        });
        tutorials.add(new TutorialMessage("To view the map press M. The map automatically zooms in when near planets. By deselecting the checkbox, the map can be moved around using the mouse and the zoom level can be changed using Z or X.") {
            @Override
            public boolean isCompleted() {
                return input.isKeyPressed(GameConstants.MAP);
            }
        });
        tutorials.add(new TutorialMessage("By destroying enemies, you gain points. Points can be used to upgrade parts of the ship. Press U to open the upgrade menu.") {
            @Override
            public boolean isCompleted() {
                return input.isKeyPressed(GameConstants.UPGRADE_MENU);
            }
        });
        tutorials.add(new TutorialMessage("When a planet has been liberated, it can be explored by pressing L which yields potential rewards. Press enter to continue.") {
            @Override
            public boolean isCompleted() {
                return input.isKeyPressed(Keyboard.KEY_RETURN);
            }
        });
        tutorials.add(new TutorialMessage("Once this planet sector has been completed, head south-east to find another planet system. Press enter to continue.") {
            @Override
            public boolean isCompleted() {
                return input.isKeyPressed(Keyboard.KEY_RETURN);
            }
        });
        tutorials.add(new TutorialMessage("The tutorial is now over. Good luck! Press enter to continue.") {
            @Override
            public boolean isCompleted() {
                return input.isKeyPressed(Keyboard.KEY_RETURN);
            }
        });
    }

    public void onUpdate() {
        TutorialMessage tutorial = tutorials.get(currentTutorial);
        Gui baseGui = IngameState.getInstance().guiHierarchy.currentHeirarchy.get(0);
        if(tutorial.isCompleted()) {
            if(currentTutorial == tutorials.size()-1) {
                IngameState.getInstance().entityManager.player.tutorialCompleted = true;
                if (baseGui instanceof GuiHUD) {
                    GuiHUD guiHUD = (GuiHUD) baseGui;
                    guiHUD.messageOnDisplay = false;
                }
            } else {
                currentTutorial++;
            }
        }
        if(!IngameState.getInstance().entityManager.player.tutorialCompleted) {
            tutorial = tutorials.get(currentTutorial);
            if (baseGui instanceof GuiHUD) {
                GuiHUD guiHUD = (GuiHUD) baseGui;
                guiHUD.showMessage(tutorial.msg, Keyboard.KEY_ESCAPE);
            }
        }
        if(currentTutorial == 0 && !IngameState.getInstance().entityManager.player.tutorialCompleted) {
            if(IngameState.getInstance().gContainer.getInput().isKeyPressed(Keyboard.KEY_SPACE)) {
                IngameState.getInstance().entityManager.player.tutorialCompleted = true;
                if (baseGui instanceof GuiHUD) {
                    GuiHUD guiHUD = (GuiHUD) baseGui;
                    guiHUD.messageOnDisplay = false;
                }
            }
        }
    }

    public static abstract class TutorialMessage {
        public String msg;

        public TutorialMessage(String message) {
            msg = message;
        }

        public abstract boolean isCompleted();
    }
}
