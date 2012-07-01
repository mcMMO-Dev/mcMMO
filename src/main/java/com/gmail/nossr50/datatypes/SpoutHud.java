package com.gmail.nossr50.datatypes;

import org.getspout.spoutapi.SpoutManager;

import com.gmail.nossr50.mcMMO;
import com.gmail.nossr50.config.SpoutConfig;
import com.gmail.nossr50.datatypes.popups.Menu;
import com.gmail.nossr50.datatypes.popups.XpBar;

public class SpoutHud {
    private PlayerProfile playerProfile;

    private Menu menu;
    private XpBar xpBar;

    public SpoutHud(PlayerProfile playerProfile) {
        this.playerProfile = playerProfile;

        initializeXpBar();
    }

    /**
     * Initialize the HUD.
     */
    public void initializeXpBar() {
        if (SpoutConfig.getInstance().getXPBarEnabled()) {
            if (xpBar != null) {
                xpBar.removeWidgets();
            }

            xpBar = new XpBar(SpoutManager.getPlayer(playerProfile.getPlayer()), playerProfile.getHudType());
        }
    }

    /**
     * Update the XP bar.
     */
    public void updateXpBar() {
        SkillType skillType = playerProfile.getXpBarLocked() ? playerProfile.getSkillLock() : playerProfile.getLastGained();

        if (skillType == null) {
            return;
        }

        xpBar.update(skillType, playerProfile);
    }

    public boolean isMenuOpened() {
        return (menu != null) ? true : false;
    }

    public void openMenu() {
        menu = new Menu(SpoutManager.getPlayer(playerProfile.getPlayer()), playerProfile);
    }

    public void onMenuClose() {
        menu = null;
    }

    public void removeWidgets() {
        if (menu != null) {
            menu.close();
        }

        SpoutManager.getPlayer(playerProfile.getPlayer()).getMainScreen().removeWidgets(mcMMO.p);
    }
}