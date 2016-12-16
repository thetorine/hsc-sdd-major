package spacegame.gui.screen;

import org.newdawn.slick.Color;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import spacegame.core.AssetManager;
import spacegame.core.World;
import spacegame.entity.EntityPlayer;
import spacegame.gamestates.IngameState;
import spacegame.gui.Gui;
import spacegame.gui.widgets.EventListener;
import spacegame.gui.widgets.GuiButton;
import spacegame.other.GameConstants;
import spacegame.other.KeyboardListener;

public class GuiFinishedGame extends Gui implements EventListener {

    public GuiFinishedGame() {
        super(AssetManager.uiImages.get("bg_green.png"), 0.33f);
        setBackgroundTint();
        GuiButton respawnButton = new GuiButton("Play Again", 0.1f, this);
        respawnButton.yStart = (int) (yStart + height - 0.15f * GameConstants.GAME_HEIGHT);
        addGuiElement(respawnButton);
    }

    @Override
    public void renderBackground(Graphics g, GameContainer container) {
        super.renderBackground(g, container);
        g.setFont(GameConstants.GAME_FONT[3]);
        g.drawString("Game Ended", xStart+(width-g.getFont().getWidth("Game Ended!"))/2, yStart+0.07f*height);
        g.setFont(GameConstants.GAME_FONT[0]);
    }

    @Override
    public void renderForeground(Graphics g, GameContainer container) {
        super.renderForeground(g, container);
        g.setColor(Color.black);
        int lastHeight = (int) (yStart+0.25f*height);
        String str = "Congratulations you finished the game. You ended with " + IngameState.getInstance().entityManager.player.pointsGained + " points!";
        wrapText(str, xStart + 0.05f*GameConstants.GAME_WIDTH, lastHeight, width - 0.1f*GameConstants.GAME_WIDTH, g);
    }

    @Override
    public void onStateChange(Gui element) {
        if (element instanceof GuiButton) {
            GuiButton button = (GuiButton) element;
            if(button.buttonName.equals("Play Again")) {
                IngameState.getInstance().entityManager.ingameEntities.clear();
                World.planetsList.clear();
                KeyboardListener.deregisterListener(IngameState.getInstance().entityManager.player);
                IngameState.getInstance().firstLoad = true;
                IngameState.getInstance().entityManager.loadPlanetSystems(true);
                IngameState.getInstance().entityManager.player = new EntityPlayer();
                IngameState.getInstance().entityManager.spawnEntity(IngameState.getInstance().entityManager.player);
                IngameState.getInstance().camera.setEntityFocus(IngameState.getInstance().entityManager.player);
                IngameState.getInstance().guiHierarchy.collapseHeirarchy();
            }
        }
    }

}
