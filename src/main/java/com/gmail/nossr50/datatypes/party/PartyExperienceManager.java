package com.gmail.nossr50.datatypes.party;

import com.gmail.nossr50.config.Config;
import com.gmail.nossr50.config.experience.ExperienceConfig;
import com.gmail.nossr50.datatypes.experience.FormulaType;
import com.gmail.nossr50.locale.LocaleLoader;
import com.gmail.nossr50.mcMMO;
import com.gmail.nossr50.util.EventUtils;
import com.gmail.nossr50.util.sounds.SoundManager;
import com.gmail.nossr50.util.sounds.SoundType;
import org.bukkit.entity.Player;

import java.text.DecimalFormat;

public class PartyExperienceManager {

    private int partyLevel;
    private float partyExperience;
    private ShareMode xpShareMode   = ShareMode.NONE;

    public void setXpShareMode(ShareMode xpShareMode) {
        this.xpShareMode = xpShareMode;
    }

    public ShareMode getXpShareMode() {
        return xpShareMode;
    }

    /**
     * Applies an experience gain
     *
     * @param xp Experience amount to add
     */
    public void applyXpGain(float xp) {
        if (!EventUtils.handlePartyXpGainEvent(this, xp)) {
            return;
        }

        if (getXp() < getXpToLevel()) {
            return;
        }

        int levelsGained = 0;
        float xpRemoved = 0;

        while (getXp() >= getXpToLevel()) {
            if (hasReachedLevelCap()) {
                setXp(0);
                return;
            }

            xpRemoved += levelUp();
            levelsGained++;
        }

        if (!EventUtils.handlePartyLevelChangeEvent(this, levelsGained, xpRemoved)) {
            return;
        }

        if (!Config.getInstance().getPartyInformAllMembers()) {
            Player leader = mcMMO.p.getServer().getPlayer(this.leader.getUniqueId());

            if (leader != null) {
                leader.sendMessage(LocaleLoader.getString("Party.LevelUp", levelsGained, getLevel()));

                if (Config.getInstance().getLevelUpSoundsEnabled()) {
                    SoundManager.sendSound(leader, leader.getLocation(), SoundType.LEVEL_UP);
                }
            }
            return;
        }

        mcMMO.getPartyManager().informPartyMembersLevelUp(this, levelsGained, getLevel());
    }

    public boolean hasReachedLevelCap() {
        return Config.getInstance().getPartyLevelCap() < getLevel() + 1;
    }

    public int getLevel() {
        return level;
    }

    public void setLevel(int level) {
        this.level = level;
    }

    public float getXp() {
        return xp;
    }

    public void setXp(float xp) {
        this.xp = xp;
    }

    public void addXp(float xp) {
        setXp(getXp() + xp);
    }

    protected float levelUp() {
        float xpRemoved = getXpToLevel();

        setLevel(getLevel() + 1);
        setXp(getXp() - xpRemoved);

        return xpRemoved;
    }

    public int getXpToLevel() {
        FormulaType formulaType = ExperienceConfig.getInstance().getFormulaType();
        return (mcMMO.getFormulaManager().getXPtoNextLevel(level, formulaType)) * (getPartyMembers().size() + Config.getInstance().getPartyXpCurveMultiplier());
    }

    public String getXpToLevelPercentage() {
        DecimalFormat percent = new DecimalFormat("##0.00%");
        return percent.format(this.getXp() / getXpToLevel());
    }
}
