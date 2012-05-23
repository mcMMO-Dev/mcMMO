package com.gmail.nossr50.listeners;

import org.bukkit.ChatColor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.getspout.spoutapi.event.input.KeyPressedEvent;
import org.getspout.spoutapi.event.screen.ButtonClickEvent;
import org.getspout.spoutapi.event.screen.ScreenCloseEvent;
import org.getspout.spoutapi.event.spout.SpoutCraftEnableEvent;
import org.getspout.spoutapi.gui.ScreenType;
import org.getspout.spoutapi.player.SpoutPlayer;

import com.gmail.nossr50.mcMMO;
import com.gmail.nossr50.config.SpoutConfig;
import com.gmail.nossr50.datatypes.HUDType;
import com.gmail.nossr50.datatypes.HUDmmo;
import com.gmail.nossr50.datatypes.PlayerProfile;
import com.gmail.nossr50.datatypes.buttons.ButtonEscape;
import com.gmail.nossr50.datatypes.buttons.ButtonHUDStyle;
import com.gmail.nossr50.datatypes.buttons.ButtonPartyToggle;
import com.gmail.nossr50.datatypes.popups.PopupMMO;
import com.gmail.nossr50.spout.SpoutStuff;
import com.gmail.nossr50.util.Users;

public class SpoutListener implements Listener {
    private final mcMMO plugin;

    public SpoutListener(mcMMO plugin) {
        this.plugin = plugin;
    }

    /**
     * Monitor SpoutCraftEnable events.
     *
     * @param event The event to watch
     */
    @EventHandler
    public void onSpoutCraftEnable(SpoutCraftEnableEvent event) {
        SpoutPlayer sPlayer = event.getPlayer();
        PlayerProfile PPs = Users.getProfile(sPlayer);

        //TODO: Add custom titles based on skills
        if (SpoutConfig.getInstance().getShowPowerLevel()) {
            sPlayer.setTitle(sPlayer.getName()+ "\n" + ChatColor.YELLOW + "P" + ChatColor.GOLD + "lvl" + ChatColor.WHITE+"." + ChatColor.GREEN + String.valueOf(PPs.getPowerLevel()));
        }

        if (sPlayer.isSpoutCraftEnabled()) {
            SpoutStuff.playerHUDs.put(sPlayer, new HUDmmo(sPlayer, plugin)); //Setup Party HUD stuff
            
            PPs.toggleSpoutEnabled();
        }
    }

    /**
     * Monitor Spout ButtonClick events.
     *
     * @param event The event to watch
     */
    @EventHandler
    public void onButtonClick(ButtonClickEvent event) {
        SpoutPlayer sPlayer = event.getPlayer();
        PlayerProfile PP = Users.getProfile(sPlayer);
        
        if (event.getButton() instanceof ButtonHUDStyle) {
            if (SpoutStuff.playerHUDs.containsKey(sPlayer)) {
                SpoutStuff.playerHUDs.get(sPlayer).resetHUD();
                SpoutStuff.playerHUDs.remove(sPlayer);
                
                switch (PP.getHUDType()) {
                case RETRO:
                    PP.setHUDType(HUDType.STANDARD);
                    break;

                case STANDARD:
                    PP.setHUDType(HUDType.SMALL);
                    break;

                case SMALL:
                    PP.setHUDType(HUDType.DISABLED);
                    break;

                case DISABLED:
                    PP.setHUDType(HUDType.RETRO);
                    break;

                default:
                    break;
                }

                SpoutStuff.playerHUDs.put(sPlayer, new HUDmmo(sPlayer, plugin));
                SpoutStuff.playerScreens.get(sPlayer).updateButtons(PP);
            }
        }
        else if (event.getButton() instanceof ButtonEscape) {
            sPlayer.getMainScreen().closePopup();
        }
        else if (event.getButton() instanceof ButtonPartyToggle) {
            PP.togglePartyHUD();
            ButtonPartyToggle bpt = (ButtonPartyToggle) event.getButton();
            bpt.updateText(PP);
            SpoutStuff.playerHUDs.get(sPlayer).resetHUD();
            SpoutStuff.playerHUDs.get(sPlayer).initializeHUD(sPlayer);
        }
    }

    /**
     * Monitor Spout ScreenClose events.
     *
     * @param event The event to watch
     */
    @EventHandler
    public void onScreenClose(ScreenCloseEvent event) {
        if (event.getScreen() instanceof PopupMMO) {
            SpoutStuff.playerScreens.remove(event.getPlayer());
        }
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
