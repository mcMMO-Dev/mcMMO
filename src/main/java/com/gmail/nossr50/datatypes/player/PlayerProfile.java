package com.gmail.nossr50.datatypes.player;

import com.gmail.nossr50.config.experience.ExperienceConfig;
import com.gmail.nossr50.datatypes.experience.FormulaType;
import com.gmail.nossr50.datatypes.experience.SkillXpGain;
import com.gmail.nossr50.datatypes.skills.PrimarySkillType;
import com.gmail.nossr50.datatypes.skills.SuperAbilityType;
import com.gmail.nossr50.mcMMO;
import com.gmail.nossr50.runnables.player.PlayerProfileSaveTask;
import com.gmail.nossr50.util.player.UserManager;
import com.gmail.nossr50.util.skills.SkillTools;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import java.util.EnumMap;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.DelayQueue;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class PlayerProfile {
    private final String playerName;
    private @Nullable UUID uuid;
    private boolean loaded;
    private volatile boolean changed;

    /* HUDs */
    private int scoreboardTipsShown;
    private int saveAttempts = 0;

    private @Nullable Long lastLogin;

    /* Skill Data */
    private final Map<PrimarySkillType, Integer> skills = new EnumMap<>(
            PrimarySkillType.class);   // Skill & Level
    private final Map<PrimarySkillType, Float> skillsXp = new EnumMap<>(
            PrimarySkillType.class);     // Skill & XP
    private final Map<SuperAbilityType, Integer> abilityDATS = new EnumMap<>(
            SuperAbilityType.class); // Ability & Cooldown
    private final Map<UniqueDataType, Integer> uniquePlayerData = new EnumMap<>(
            UniqueDataType.class); //Misc data that doesn't fit into other categories (chimaera wing, etc..)

    // Store previous XP gains for diminished returns
    private final DelayQueue<SkillXpGain> gainedSkillsXp = new DelayQueue<>();
    private final Map<PrimarySkillType, Float> rollingSkillsXp = new EnumMap<>(
            PrimarySkillType.class);

    @Deprecated
    public PlayerProfile(String playerName) {
        this(playerName, null, 0);
    }

    @Deprecated
    public PlayerProfile(String playerName, UUID uuid) {
        this(playerName, uuid, 0);
    }

    @Deprecated
    public PlayerProfile(String playerName, int startingLevel) {
        this(playerName, null, startingLevel);
    }

    public PlayerProfile(String playerName, @Nullable UUID uuid, int startingLevel) {
        this.uuid = uuid;
        this.playerName = playerName;

        scoreboardTipsShown = 0;

        for (SuperAbilityType superAbilityType : SuperAbilityType.values()) {
            abilityDATS.put(superAbilityType, 0);
        }

        for (PrimarySkillType primarySkillType : SkillTools.NON_CHILD_SKILLS) {
            skills.put(primarySkillType, startingLevel);
            skillsXp.put(primarySkillType, 0F);
        }

        //Misc Cooldowns
        uniquePlayerData.put(UniqueDataType.CHIMAERA_WING_DATS, 0); //Chimaera wing
        lastLogin = System.currentTimeMillis();
    }

    @Deprecated
    public PlayerProfile(@NotNull String playerName, boolean isLoaded, int startingLvl) {
        this(playerName, startingLvl);
        this.loaded = isLoaded;
    }

    public PlayerProfile(@NotNull String playerName, @Nullable UUID uuid, boolean isLoaded, int startingLvl) {
        this(playerName, uuid, startingLvl);
        this.loaded = isLoaded;
    }

    public PlayerProfile(@NotNull String playerName, @Nullable UUID uuid,
            Map<PrimarySkillType, Integer> levelData, Map<PrimarySkillType, Float> xpData,
            Map<SuperAbilityType, Integer> cooldownData, int scoreboardTipsShown,
            Map<UniqueDataType, Integer> uniqueProfileData, @Nullable Long lastLogin) {
        this.playerName = playerName;
        this.uuid = uuid;
        this.scoreboardTipsShown = scoreboardTipsShown;

        skills.putAll(levelData);
        skillsXp.putAll(xpData);
        abilityDATS.putAll(cooldownData);
        uniquePlayerData.putAll(uniqueProfileData);

        loaded = true;

        if (lastLogin != null) {
            this.lastLogin = lastLogin;
        }
    }

    public void scheduleAsyncSave() {
        mcMMO.p.getFoliaLib().getScheduler().runAsync(new PlayerProfileSaveTask(this, false));
    }

    public void scheduleAsyncSaveDelay() {
        mcMMO.p.getFoliaLib().getScheduler()
                .runLaterAsync(new PlayerProfileSaveTask(this, false), 20);
    }

    @Deprecated
    public void scheduleSyncSaveDelay() {
        mcMMO.p.getFoliaLib().getScheduler().runLater(new PlayerProfileSaveTask(this, true), 20);
    }

    public void save(boolean useSync) {
        if (!changed || !loaded) {
            saveAttempts = 0;
            return;
        }

        // TODO should this part be synchronized?
        PlayerProfile profileCopy = new PlayerProfile(playerName, uuid, ImmutableMap.copyOf(skills),
                ImmutableMap.copyOf(skillsXp), ImmutableMap.copyOf(abilityDATS),
                scoreboardTipsShown, ImmutableMap.copyOf(uniquePlayerData), lastLogin);
        changed = !mcMMO.getDatabaseManager().saveUser(profileCopy);

        if (changed) {
            mcMMO.p.getLogger()
                    .severe("PlayerProfile saving failed for player: " + playerName + " " + uuid);

            if (saveAttempts > 0) {
                mcMMO.p.getLogger().severe("Attempted to save profile for player " + getPlayerName()
                        + " resulted in failure. " + saveAttempts + " have been made so far.");
            }

            if (saveAttempts < 10) {
                saveAttempts++;

                //Back out of async saving if we detect a server shutdown, this is not always going to be caught
                if (mcMMO.isServerShutdownExecuted() || useSync) {
                    mcMMO.p.getFoliaLib().getScheduler()
                            .runNextTick(new PlayerProfileSaveTask(this, true));
                } else {
                    scheduleAsyncSave();
                }

            } else {
                mcMMO.p.getLogger().severe("mcMMO has failed to save the profile for "
                        + getPlayerName() + " numerous times." +
                        " mcMMO will now stop attempting to save this profile." +
                        " Check your console for errors and inspect your DB for issues.");
            }

        } else {
            saveAttempts = 0;
        }
    }

    /**
     * Get this users last login, will return current java.lang.System#currentTimeMillis() if it
     * doesn't exist
     *
     * @return the last login
     * @deprecated This is only function for FlatFileDB atm, and it's only here for unit testing
     * right now
     */
    public @NotNull Long getLastLogin() {
        return Objects.requireNonNullElse(lastLogin, -1L);
    }

    public void updateLastLogin() {
        this.lastLogin = System.currentTimeMillis();
    }

    public String getPlayerName() {
        return playerName;
    }

    public UUID getUniqueId() {
        return uuid;
    }

    public void setUniqueId(UUID uuid) {
        markProfileDirty();

        this.uuid = uuid;
    }

    public boolean isLoaded() {
        return loaded;
    }

    /**
     * Marks the profile as "dirty" which flags a profile to be saved in the next save operation
     */
    public void markProfileDirty() {
        changed = true;
    }

    public int getScoreboardTipsShown() {
        return scoreboardTipsShown;
    }

    public void setScoreboardTipsShown(int scoreboardTipsShown) {
        markProfileDirty();

        this.scoreboardTipsShown = scoreboardTipsShown;
    }

    public void increaseTipsShown() {
        setScoreboardTipsShown(getScoreboardTipsShown() + 1);
    }

    /*
     * Cooldowns
     */

    public int getChimaerWingDATS() {
        return uniquePlayerData.get(UniqueDataType.CHIMAERA_WING_DATS);
    }

    protected void setChimaeraWingDATS(int DATS) {
        markProfileDirty();
        uniquePlayerData.put(UniqueDataType.CHIMAERA_WING_DATS, DATS);
    }

    public void setUniqueData(UniqueDataType uniqueDataType, int newData) {
        markProfileDirty();
        uniquePlayerData.put(uniqueDataType, newData);
    }

    public long getUniqueData(UniqueDataType uniqueDataType) {
        return uniquePlayerData.get(uniqueDataType);
    }

    /**
     * Get the current deactivation timestamp of an ability.
     *
     * @param ability The {@link SuperAbilityType} to get the DATS for
     * @return the deactivation timestamp for the ability
     */
    public long getAbilityDATS(SuperAbilityType ability) {
        return abilityDATS.get(ability);
    }

    /**
     * Set the current deactivation timestamp of an ability.
     *
     * @param ability The {@link SuperAbilityType} to set the DATS for
     * @param DATS the DATS of the ability
     */
    protected void setAbilityDATS(SuperAbilityType ability, long DATS) {
        markProfileDirty();

        abilityDATS.put(ability, (int) (DATS * .001D));
    }

    /**
     * Reset all ability cooldowns.
     */
    protected void resetCooldowns() {
        markProfileDirty();

        abilityDATS.replaceAll((a, v) -> 0);
    }

    /*
     * Xp Functions
     */

    public int getSkillLevel(PrimarySkillType skill) {
        return SkillTools.isChildSkill(skill) ? getChildSkillLevel(skill) : skills.get(skill);
    }

    public float getSkillXpLevelRaw(PrimarySkillType skill) {
        return skillsXp.get(skill);
    }

    public int getSkillXpLevel(PrimarySkillType skill) {
        if (SkillTools.isChildSkill(skill)) {
            return 0;
        }

        return (int) Math.floor(getSkillXpLevelRaw(skill));
    }

    public void setSkillXpLevel(PrimarySkillType skill, float xpLevel) {
        if (SkillTools.isChildSkill(skill)) {
            return;
        }

        markProfileDirty();

        skillsXp.put(skill, xpLevel);
    }

    protected float levelUp(PrimarySkillType skill) {
        float xpRemoved = getXpToLevel(skill);

        markProfileDirty();

        skills.put(skill, skills.get(skill) + 1);
        skillsXp.put(skill, skillsXp.get(skill) - xpRemoved);

        return xpRemoved;
    }

    /**
     * Remove Xp from a skill.
     *
     * @param skill Type of skill to modify
     * @param xp Amount of xp to remove
     */
    public void removeXp(PrimarySkillType skill, int xp) {
        if (SkillTools.isChildSkill(skill)) {
            return;
        }

        markProfileDirty();

        skillsXp.put(skill, skillsXp.get(skill) - xp);
    }

    public void removeXp(PrimarySkillType skill, float xp) {
        if (SkillTools.isChildSkill(skill)) {
            return;
        }

        markProfileDirty();

        skillsXp.put(skill, skillsXp.get(skill) - xp);
    }

    /**
     * Modify a skill level.
     *
     * @param skill Type of skill to modify
     * @param level New level value for the skill
     */
    public void modifySkill(PrimarySkillType skill, int level) {
        if (SkillTools.isChildSkill(skill)) {
            return;
        }

        markProfileDirty();

        //Don't allow levels to be negative
        if (level < 0) {
            level = 0;
        }

        skills.put(skill, level);
        skillsXp.put(skill, 0F);
    }

    /**
     * Add levels to a skill.
     *
     * @param skill Type of skill to add levels to
     * @param levels Number of levels to add
     */
    public void addLevels(PrimarySkillType skill, int levels) {
        modifySkill(skill, skills.get(skill) + levels);
    }

    /**
     * Add Experience to a skill.
     *
     * @param skill Type of skill to add experience to
     * @param xp Number of experience to add
     */
    public void addXp(PrimarySkillType skill, float xp) {
        markProfileDirty();

        if (SkillTools.isChildSkill(skill)) {
            var parentSkills = mcMMO.p.getSkillTools().getChildSkillParents(skill);
            float dividedXP = (xp / parentSkills.size());

            for (PrimarySkillType parentSkill : parentSkills) {
                skillsXp.put(parentSkill, skillsXp.get(parentSkill) + dividedXP);
            }
        } else {
            skillsXp.put(skill, skillsXp.get(skill) + xp);
        }
    }

    /**
     * Get the registered amount of experience gained This is used for diminished XP returns
     *
     * @return xp Experience amount registered
     */
    public float getRegisteredXpGain(PrimarySkillType primarySkillType) {
        float xp = 0F;

        if (rollingSkillsXp.get(primarySkillType) != null) {
            xp = rollingSkillsXp.get(primarySkillType);
        }

        return xp;
    }

    /**
     * Register an experience gain This is used for diminished XP returns
     *
     * @param primarySkillType Skill being used
     * @param xp Experience amount to add
     */
    public void registerXpGain(PrimarySkillType primarySkillType, float xp) {
        gainedSkillsXp.add(new SkillXpGain(primarySkillType, xp));
        rollingSkillsXp.put(primarySkillType, getRegisteredXpGain(primarySkillType) + xp);
    }

    /**
     * Remove experience gains older than a given time This is used for diminished XP returns
     */
    public void purgeExpiredXpGains() {
        SkillXpGain gain;
        while ((gain = gainedSkillsXp.poll()) != null) {
            rollingSkillsXp.put(gain.getSkill(),
                    getRegisteredXpGain(gain.getSkill()) - gain.getXp());
        }
    }

    /**
     * Get the amount of Xp remaining before the next level.
     *
     * @param primarySkillType Type of skill to check
     * @return the total amount of Xp until next level
     */
    public int getXpToLevel(PrimarySkillType primarySkillType) {
        if (SkillTools.isChildSkill(primarySkillType)) {
            return 0;
        }

        int level = (ExperienceConfig.getInstance().getCumulativeCurveEnabled())
                ? UserManager.getPlayer(playerName).getPowerLevel() : skills.get(primarySkillType);
        FormulaType formulaType = ExperienceConfig.getInstance().getFormulaType();

        return mcMMO.getFormulaManager().getXPtoNextLevel(level, formulaType);
    }

    private int getChildSkillLevel(@NotNull PrimarySkillType primarySkillType)
            throws IllegalArgumentException {
        if (!SkillTools.isChildSkill(primarySkillType)) {
            throw new IllegalArgumentException(primarySkillType + " is not a child skill!");
        }

        ImmutableList<PrimarySkillType> parents = mcMMO.p.getSkillTools()
                .getChildSkillParents(primarySkillType);
        int sum = 0;

        for (PrimarySkillType parent : parents) {
            sum += Math.min(getSkillLevel(parent), mcMMO.p.getSkillTools().getLevelCap(parent));
        }

        return sum / parents.size();
    }
}
