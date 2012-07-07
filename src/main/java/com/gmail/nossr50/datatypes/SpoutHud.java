package com.gmail.nossr50.datatypes;

import org.bukkit.entity.Player;
import org.getspout.spoutapi.SpoutManager;

import com.gmail.nossr50.mcMMO;
import com.gmail.nossr50.config.SpoutConfig;
import com.gmail.nossr50.datatypes.popups.Menu;
import com.gmail.nossr50.datatypes.popups.XpBar;

public class SpoutHud {
    private Player player;
    private PlayerProfile profile;

    private SkillType lastGained;
    private SkillType skillLock;
    private boolean xpBarLocked;

    private Menu menu;
    private XpBar xpBar;

    public SpoutHud(McMMOPlayer mcMMOPlayer) {
        this.player = mcMMOPlayer.getPlayer();
        this.profile = mcMMOPlayer.getProfile();

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

            xpBar = new XpBar(SpoutManager.getPlayer(player), profile.getHudType());
        }
    }

    /**
     * Update the XP bar.
     */
    public void updateXpBar() {
        SkillType skillType = xpBarLocked ? skillLock : lastGained;

        if (skillType == null) {
            return;
        }

        xpBar.update(skillType, profile);
    }

    public boolean isMenuOpened() {
        return (menu != null) ? true : false;
    }

    public void openMenu() {
        menu = new Menu(SpoutManager.getPlayer(player), profile);
    }

    public void onMenuClose() {
        menu = null;
    }

    public void removeWidgets() {
        if (menu != null) {
            menu.close();
        }

        SpoutManager.getPlayer(player).getMainScreen().removeWidgets(mcMMO.p);
    }

    public SkillType getLastGained() {
        return lastGained;
    }

    public void setLastGained(SkillType type) {
        this.lastGained = type;
    }

    public boolean getXpBarLocked() {
        return xpBarLocked;
    }

    public void setXpBarLocked(boolean locked) {
        this.xpBarLocked = locked;
    }

    public void toggleXpBarLocked() {
        xpBarLocked = !xpBarLocked;
    }

    public SkillType getSkillLock() {
        return skillLock;
    }

    public void setSkillLock(SkillType type) {
        this.skillLock = type;
    }
}