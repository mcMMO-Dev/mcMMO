package com.gmail.nossr50.spout;

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

import com.gmail.nossr50.datatypes.McMMOPlayer;
import com.gmail.nossr50.datatypes.PlayerProfile;
import com.gmail.nossr50.locale.LocaleLoader;
import com.gmail.nossr50.spout.buttons.McmmoButton;
import com.gmail.nossr50.spout.huds.SpoutHud;
import com.gmail.nossr50.spout.popups.Menu;
import com.gmail.nossr50.util.Users;

public class SpoutListener implements Listener {

    /**
     * Monitor SpoutCraftEnable events.
     *
     * @param event The event to watch
     */
    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onSpoutCraftEnable(SpoutCraftEnableEvent event) {
        SpoutPlayer spoutPlayer = event.getPlayer();
        McMMOPlayer mcMMOPlayer = Users.getPlayer(event.getPlayer().getPlayer());
        PlayerProfile profile = mcMMOPlayer.getProfile();

        //TODO: Add custom titles based on skills
        if (SpoutTools.showPowerLevel) {
            spoutPlayer.setTitle(LocaleLoader.getString("Spout.Title", spoutPlayer.getName(), mcMMOPlayer.getPowerLevel()));
        }

        profile.setSpoutHud(new SpoutHud(mcMMOPlayer)); //Setup Party HUD stuff
    }

    /**
     * Monitor Spout ButtonClick events.
     *
     * @param event The event to watch
     */
    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onButtonClick(ButtonClickEvent event) {
        Button button = event.getButton();

        if (button instanceof McmmoButton) {
            ((McmmoButton) button).activate();
        }
    }

    /**
     * Monitor Spout ScreenClose events.
     *
     * @param event The event to watch
     */
    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onScreenClose(ScreenCloseEvent event) {
        if (event.getScreen() instanceof Menu) {
            SpoutPlayer spoutPlayer = event.getPlayer();

            Users.getPlayer(spoutPlayer).getProfile().getSpoutHud().onMenuClose();
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

        if (event.getKey() == SpoutTools.menuKey) {
            SpoutHud spoutHud = Users.getPlayer(spoutPlayer).getProfile().getSpoutHud();

            if (!spoutHud.isMenuOpened()) {
                spoutHud.openMenu();
            }
        }
    }
}
