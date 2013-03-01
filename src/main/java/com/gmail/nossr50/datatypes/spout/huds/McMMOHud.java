package com.gmail.nossr50.datatypes.spout.huds;

import org.bukkit.entity.Player;
import org.getspout.spoutapi.SpoutManager;

import com.gmail.nossr50.mcMMO;
import com.gmail.nossr50.config.spout.SpoutConfig;
import com.gmail.nossr50.datatypes.player.McMMOPlayer;
import com.gmail.nossr50.datatypes.player.PlayerProfile;
import com.gmail.nossr50.datatypes.skills.SkillType;
import com.gmail.nossr50.datatypes.spout.popups.McMMOMenu;
import com.gmail.nossr50.datatypes.spout.popups.McMMOXpBar;

public class McMMOHud {
    private Player        player;
    private PlayerProfile profile;

    private SkillType lastGained;
    private SkillType skillLock;
    private boolean   xpBarLocked;

    private McMMOMenu  menu;
    private McMMOXpBar xpBar;

    public McMMOHud(McMMOPlayer mcMMOPlayer) {
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

            xpBar = new McMMOXpBar(SpoutManager.getPlayer(player), profile.getHudType());
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
        if (menu != null) {
            return true;
        }

        return false;
    }

    public void openMenu() {
        menu = new McMMOMenu(SpoutManager.getPlayer(player), profile);
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
