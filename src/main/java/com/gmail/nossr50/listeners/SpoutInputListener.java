package com.gmail.nossr50.listeners;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.getspout.spoutapi.event.input.KeyPressedEvent;
import org.getspout.spoutapi.gui.ScreenType;
import org.getspout.spoutapi.player.SpoutPlayer;

import com.gmail.nossr50.mcMMO;
import com.gmail.nossr50.datatypes.popups.PopupMMO;
import com.gmail.nossr50.spout.SpoutStuff;
import com.gmail.nossr50.util.Users;

public class SpoutInputListener implements Listener {
    private mcMMO plugin;

    public SpoutInputListener(mcMMO plugin) {
        this.plugin = plugin;
    }

    /**
     * Monitor Spout KeyPressed events.
     *
     * @param event The event to watch
     */
    @EventHandler
    public void onKeyPressedEvent(KeyPressedEvent event) {
        SpoutPlayer sPlayer = event.getPlayer();

        if (!sPlayer.isSpoutCraftEnabled() || sPlayer.getMainScreen().getActivePopup() != null || event.getScreenType() != ScreenType.GAME_SCREEN) {
            return;
        }

        if (event.getKey() == SpoutStuff.keypress) {
            if (!SpoutStuff.playerScreens.containsKey(sPlayer)) {
                PopupMMO mmoPop = new PopupMMO(sPlayer, Users.getProfile(sPlayer), plugin);

                SpoutStuff.playerScreens.put(sPlayer, mmoPop);
                sPlayer.getMainScreen().attachPopupScreen(SpoutStuff.playerScreens.get(sPlayer));
                sPlayer.getMainScreen().setDirty(true);
            }
            else {
                sPlayer.getMainScreen().attachPopupScreen(SpoutStuff.playerScreens.get(sPlayer));
                sPlayer.getMainScreen().setDirty(true);
            }
        }
    }
}
