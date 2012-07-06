package com.gmail.nossr50.listeners;

import org.bukkit.ChatColor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.getspout.spoutapi.event.input.KeyPressedEvent;
import org.getspout.spoutapi.event.screen.ButtonClickEvent;
import org.getspout.spoutapi.event.screen.ScreenCloseEvent;
import org.getspout.spoutapi.event.spout.SpoutCraftEnableEvent;
import org.getspout.spoutapi.gui.Button;
import org.getspout.spoutapi.gui.ScreenType;
import org.getspout.spoutapi.player.SpoutPlayer;

import com.gmail.nossr50.config.SpoutConfig;
import com.gmail.nossr50.datatypes.McMMOPlayer;
import com.gmail.nossr50.datatypes.SpoutHud;
import com.gmail.nossr50.datatypes.PlayerProfile;
import com.gmail.nossr50.datatypes.buttons.McmmoButton;
import com.gmail.nossr50.datatypes.popups.Menu;
import com.gmail.nossr50.spout.SpoutStuff;
import com.gmail.nossr50.util.Users;

public class SpoutListener implements Listener {
    /**
     * Monitor SpoutCraftEnable events.
     *
     * @param event The event to watch
     */
    @EventHandler
    public void onSpoutCraftEnable(SpoutCraftEnableEvent event) {
        SpoutPlayer spoutPlayer = event.getPlayer();
        McMMOPlayer mcMMOPlayer = Users.getPlayer(spoutPlayer);
        PlayerProfile profile = mcMMOPlayer.getProfile();

        //TODO: Add custom titles based on skills
        if (SpoutConfig.getInstance().getShowPowerLevel()) {
            spoutPlayer.setTitle(spoutPlayer.getName() + "\n" + ChatColor.YELLOW + "P" + ChatColor.GOLD + "lvl" + ChatColor.WHITE+"." + ChatColor.GREEN + String.valueOf(mcMMOPlayer.getPowerLevel()));
        }

        profile.setSpoutHud(new SpoutHud(mcMMOPlayer)); //Setup Party HUD stuff
    }

    /**
     * Monitor Spout ButtonClick events.
     *
     * @param event The event to watch
     */
    @EventHandler
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
    @EventHandler
    public void onScreenClose(ScreenCloseEvent event) {
        if (event.getScreen() instanceof Menu) {
            SpoutPlayer spoutPlayer = event.getPlayer();

            Users.getProfile(spoutPlayer).getSpoutHud().onMenuClose();
            spoutPlayer.getMainScreen().setDirty(true);
        }
    }

    /**
     * Monitor Spout KeyPressed events.
     *
     * @param event The event to watch
     */
    @EventHandler
    public void onKeyPressedEvent(KeyPressedEvent event) {
        SpoutPlayer spoutPlayer = event.getPlayer();

        if (spoutPlayer.getMainScreen().getActivePopup() != null || event.getScreenType() != ScreenType.GAME_SCREEN) {
            return;
        }

        if (event.getKey() == SpoutStuff.keypress) {
            SpoutHud spoutHud = Users.getProfile(spoutPlayer).getSpoutHud();

            if (!spoutHud.isMenuOpened()) {
                spoutHud.openMenu();
            }
        }
    }
}
