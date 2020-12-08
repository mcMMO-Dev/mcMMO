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
import org.jetbrains.annotations.NotNull;

import java.text.DecimalFormat;

public class PartyExperienceManager {

    private int partyLevel;
    private float partyExperience;
    private final @NotNull Party partyRef;
    private final @NotNull PartyMemberManager partyMemberManagerRef;
    private @NotNull ShareMode xpShareMode = ShareMode.NONE;

    public PartyExperienceManager(@NotNull PartyMemberManager partyMemberManager, @NotNull Party party) {
        this.partyRef = party;
        this.partyMemberManagerRef = partyMemberManager;
    }

    public void setXpShareMode(@NotNull ShareMode xpShareMode) {
        this.xpShareMode = xpShareMode;
    }

    public @NotNull ShareMode getXpShareMode() {
        return xpShareMode;
    }

    /**
     * Applies an experience gain
     *
     * @param xp Experience amount to add
     */
    public void applyXpGain(float xp) {
        if (!EventUtils.handlePartyXpGainEvent(partyRef, xp)) {
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

        if (!EventUtils.handlePartyLevelChangeEvent(partyRef, levelsGained, xpRemoved)) {
            return;
        }

        if (!Config.getInstance().getPartyInformAllMembers()) {
            Player leader = mcMMO.p.getServer().getPlayer(partyMemberManagerRef.getPartyLeader().getUniqueId());

            if (leader != null) {
                leader.sendMessage(LocaleLoader.getString("Party.LevelUp", levelsGained, getLevel()));

                if (Config.getInstance().getLevelUpSoundsEnabled()) {
                    SoundManager.sendSound(leader, leader.getLocation(), SoundType.LEVEL_UP);
                }
            }

        } else {
            mcMMO.getPartyManager().informPartyMembersLevelUp(partyRef, levelsGained, getLevel());
        }
    }

    public boolean hasReachedLevelCap() {
        return Config.getInstance().getPartyLevelCap() < getLevel() + 1;
    }

    public int getLevel() {
        return partyLevel;
    }

    public void setLevel(int level) {
        partyLevel = level;
    }

    public float getXp() {
        return partyExperience;
    }

    public void setXp(float xp) {
        this.partyExperience = xp;
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

    //TODO: Why is it based on the number of party members? seems dumb
    public int getXpToLevel() {
        FormulaType formulaType = ExperienceConfig.getInstance().getFormulaType();
        return (mcMMO.getFormulaManager().getXPtoNextLevel(partyLevel, formulaType)) * (partyMemberManagerRef.getPartyMembers().size() + Config.getInstance().getPartyXpCurveMultiplier());
    }

    public @NotNull String getXpToLevelPercentage() {
        DecimalFormat percent = new DecimalFormat("##0.00%");
        return percent.format(this.getXp() / getXpToLevel());
    }

    public @NotNull Party getParty() {
        return partyRef;
    }
}
