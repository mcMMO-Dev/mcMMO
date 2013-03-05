package com.gmail.nossr50.listeners;

import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.getspout.spoutapi.event.input.KeyPressedEvent;
import org.getspout.spoutapi.event.screen.ButtonClickEvent;
import org.getspout.spoutapi.event.screen.ScreenCloseEvent;
import org.getspout.spoutapi.event.spout.SpoutCraftEnableEvent;
import org.getspout.spoutapi.gui.Button;
import org.getspout.spoutapi.gui.ScreenType;
import org.getspout.spoutapi.player.SpoutPlayer;

import com.gmail.nossr50.datatypes.player.McMMOPlayer;
import com.gmail.nossr50.datatypes.spout.buttons.McMMOButton;
import com.gmail.nossr50.datatypes.spout.huds.McMMOHud;
import com.gmail.nossr50.datatypes.spout.popups.McMMOMenu;
import com.gmail.nossr50.locale.LocaleLoader;
import com.gmail.nossr50.util.player.UserManager;
import com.gmail.nossr50.util.spout.SpoutUtils;

public class SpoutListener implements Listener {

    /**
     * Monitor SpoutCraftEnable events.
     *
     * @param event The event to watch
     */
    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onSpoutCraftEnable(SpoutCraftEnableEvent event) {
        SpoutPlayer spoutPlayer = event.getPlayer();
        McMMOPlayer mcMMOPlayer = UserManager.getPlayer(spoutPlayer);

        // TODO: Add custom titles based on skills
        if (SpoutUtils.showPowerLevel) {
            spoutPlayer.setTitle(LocaleLoader.getString("Spout.Title", spoutPlayer.getName(), mcMMOPlayer.getPowerLevel()));
        }

        mcMMOPlayer.getProfile().setSpoutHud(new McMMOHud(mcMMOPlayer)); // Setup Party HUD stuff
    }

    /**
     * Monitor Spout ButtonClick events.
     *
     * @param event The event to watch
     */
    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onButtonClick(ButtonClickEvent event) {
        Button button = event.getButton();

        if (button instanceof McMMOButton) {
            ((McMMOButton) button).activate();
        }
    }

    /**
     * Monitor Spout ScreenClose events.
     *
     * @param event The event to watch
     */
    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onScreenClose(ScreenCloseEvent event) {
        if (event.getScreen() instanceof McMMOMenu) {
            SpoutPlayer spoutPlayer = event.getPlayer();

            UserManager.getPlayer(spoutPlayer).getProfile().getSpoutHud().onMenuClose();
            spoutPlayer.getMainScreen().setDirty(true);
        }
    }

    /**
     * Monitor Spout KeyPressed events.
     *
     * @param event The event to watch
     */
    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onKeyPressedEvent(KeyPressedEvent event) {
        SpoutPlayer spoutPlayer = event.getPlayer();

        if (spoutPlayer.getMainScreen().getActivePopup() != null || event.getScreenType() != ScreenType.GAME_SCREEN) {
            return;
        }

        if (event.getKey() == SpoutUtils.menuKey) {
            McMMOHud spoutHud = UserManager.getPlayer(spoutPlayer).getProfile().getSpoutHud();

            if (!spoutHud.isMenuOpened()) {
                spoutHud.openMenu();
            }
        }
    }
}
