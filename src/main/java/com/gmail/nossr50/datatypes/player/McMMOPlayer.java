package com.gmail.nossr50.datatypes.player;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Server;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

import com.gmail.nossr50.mcMMO;
import com.gmail.nossr50.config.AdvancedConfig;
import com.gmail.nossr50.config.Config;
import com.gmail.nossr50.config.experience.ExperienceConfig;
import com.gmail.nossr50.datatypes.chat.ChatMode;
import com.gmail.nossr50.datatypes.mods.CustomTool;
import com.gmail.nossr50.datatypes.party.Party;
import com.gmail.nossr50.datatypes.party.PartyTeleportRecord;
import com.gmail.nossr50.datatypes.skills.AbilityType;
import com.gmail.nossr50.datatypes.skills.SkillType;
import com.gmail.nossr50.datatypes.skills.ToolType;
import com.gmail.nossr50.locale.LocaleLoader;
import com.gmail.nossr50.party.PartyManager;
import com.gmail.nossr50.party.ShareHandler;
import com.gmail.nossr50.runnables.skills.AbilityDisableTask;
import com.gmail.nossr50.runnables.skills.ToolLowerTask;
import com.gmail.nossr50.skills.SkillManager;
import com.gmail.nossr50.skills.acrobatics.AcrobaticsManager;
import com.gmail.nossr50.skills.alchemy.AlchemyManager;
import com.gmail.nossr50.skills.archery.ArcheryManager;
import com.gmail.nossr50.skills.axes.AxesManager;
import com.gmail.nossr50.skills.child.FamilyTree;
import com.gmail.nossr50.skills.excavation.ExcavationManager;
import com.gmail.nossr50.skills.fishing.FishingManager;
import com.gmail.nossr50.skills.herbalism.HerbalismManager;
import com.gmail.nossr50.skills.mining.MiningManager;
import com.gmail.nossr50.skills.repair.RepairManager;
import com.gmail.nossr50.skills.smelting.SmeltingManager;
import com.gmail.nossr50.skills.swords.SwordsManager;
import com.gmail.nossr50.skills.taming.TamingManager;
import com.gmail.nossr50.skills.unarmed.UnarmedManager;
import com.gmail.nossr50.skills.woodcutting.WoodcuttingManager;
import com.gmail.nossr50.util.EventUtils;
import com.gmail.nossr50.util.Misc;
import com.gmail.nossr50.util.ModUtils;
import com.gmail.nossr50.util.Permissions;
import com.gmail.nossr50.util.StringUtils;
import com.gmail.nossr50.util.skills.ParticleEffectUtils;
import com.gmail.nossr50.util.skills.PerksUtils;
import com.gmail.nossr50.util.skills.SkillUtils;

import org.apache.commons.lang.Validate;

public class McMMOPlayer {
    private Player        player;
    private PlayerProfile profile;

    private final Map<SkillType, SkillManager> skillManagers = new HashMap<SkillType, SkillManager>();

    private Party   party;
    private Party   invite;
    private int     itemShareModifier;

    private PartyTeleportRecord ptpRecord;

    private boolean partyChatMode;
    private boolean adminChatMode;
    private boolean displaySkillNotifications = true;

    private boolean abilityUse = true;
    private boolean godMode;

    private final Map<AbilityType, Boolean> abilityMode     = new HashMap<AbilityType, Boolean>();
    private final Map<AbilityType, Boolean> abilityInformed = new HashMap<AbilityType, Boolean>();

    private final Map<ToolType, Boolean> toolMode = new HashMap<ToolType, Boolean>();
    private final Map<ToolType, Integer> toolATS  = new HashMap<ToolType, Integer>();

    private int recentlyHurt;
    private int respawnATS;
    private int teleportATS;
    private long databaseATS;
    private int chimeraWingLastUse;
    private Location teleportCommence;

    private boolean isUsingUnarmed;

    public McMMOPlayer(Player player) {
        String playerName = player.getName();

        this.player = player;
        profile = mcMMO.getDatabaseManager().loadPlayerProfile(playerName, true);
        party = PartyManager.getPlayerParty(playerName);
        ptpRecord = new PartyTeleportRecord();

        /*
         * I'm using this method because it makes code shorter and safer (we don't have to add all SkillTypes manually),
         * but I actually have no idea about the performance impact, if there is any.
         * If in the future someone wants to remove this, don't forget to also remove what is in the SkillType enum. - bm01
         */
        try {
            for (SkillType skillType : SkillType.values()) {
                skillManagers.put(skillType, skillType.getManagerClass().getConstructor(McMMOPlayer.class).newInstance(this));
            }
        }
        catch (Exception e) {
            e.printStackTrace();
            mcMMO.p.getPluginLoader().disablePlugin(mcMMO.p);
        }

        for (AbilityType abilityType : AbilityType.values()) {
            abilityMode.put(abilityType, false);
            abilityInformed.put(abilityType, true); // This is intended
        }

        for (ToolType toolType : ToolType.values()) {
            toolMode.put(toolType, false);
            toolATS.put(toolType, 0);
        }

        if (!profile.isLoaded()) {
            mcMMO.p.getLogger().warning("Unable to load the PlayerProfile for " + playerName + ". Will retry over the next several seconds.");
            new RetryProfileLoadingTask().runTaskTimerAsynchronously(mcMMO.p, 11L, 31L);
        }
    }

    private class RetryProfileLoadingTask extends BukkitRunnable {
        private static final int MAX_TRIES = 5;
        private final String playerName = McMMOPlayer.this.player.getName();
        private int attempt = 0;

        // WARNING: ASYNC TASK
        // DO NOT MODIFY THE McMMOPLAYER FROM THIS CODE
        @Override
        public void run() {
            // Quit if they logged out
            if (!player.isOnline()) {
                mcMMO.p.getLogger().info("Aborting profile loading recovery for " + playerName + " - player logged out");
                this.cancel();
                return;
            }

            // Send the message that we're doing the recovery
            if (attempt == 0) {
                player.sendMessage(LocaleLoader.getString("Recovery.Notice"));
            }

            // Increment attempt counter and try
            attempt++;
            PlayerProfile profile = mcMMO.getDatabaseManager().loadPlayerProfile(playerName, true);
            // If successful, schedule the apply
            if (profile.isLoaded()) {
                new ApplySuccessfulProfile(profile).runTask(mcMMO.p);
                player.sendMessage(LocaleLoader.getString("Recovery.Success"));
                this.cancel();
                return;
            }

            // If we've failed five times, give up
            if (attempt >= MAX_TRIES) {
                mcMMO.p.getLogger().severe("Giving up on attempting to load the PlayerProfile for " + playerName);
                mcMMO.p.getServer().broadcast(LocaleLoader.getString("Recovery.AdminFailureNotice", playerName), Server.BROADCAST_CHANNEL_ADMINISTRATIVE);
                player.sendMessage(LocaleLoader.getString("Recovery.Failure").split("\n"));
                this.cancel();
                return;
            }
        }
    }

    private class ApplySuccessfulProfile extends BukkitRunnable {
        private final PlayerProfile profile;

        private ApplySuccessfulProfile(PlayerProfile profile) {
            this.profile = profile;
        }

        // Synchronized task
        // No database access permitted
        @Override
        public void run() {
            McMMOPlayer.this.profile = profile;
        }
    }

    public AcrobaticsManager getAcrobaticsManager() {
        return (AcrobaticsManager) skillManagers.get(SkillType.ACROBATICS);
    }

    public AlchemyManager getAlchemyManager() {
        return (AlchemyManager) skillManagers.get(SkillType.ALCHEMY);
    }

    public ArcheryManager getArcheryManager() {
        return (ArcheryManager) skillManagers.get(SkillType.ARCHERY);
    }

    public AxesManager getAxesManager() {
        return (AxesManager) skillManagers.get(SkillType.AXES);
    }

    public ExcavationManager getExcavationManager() {
        return (ExcavationManager) skillManagers.get(SkillType.EXCAVATION);
    }

    public FishingManager getFishingManager() {
        return (FishingManager) skillManagers.get(SkillType.FISHING);
    }

    public HerbalismManager getHerbalismManager() {
        return (HerbalismManager) skillManagers.get(SkillType.HERBALISM);
    }

    public MiningManager getMiningManager() {
        return (MiningManager) skillManagers.get(SkillType.MINING);
    }

    public RepairManager getRepairManager() {
        return (RepairManager) skillManagers.get(SkillType.REPAIR);
    }

    public SmeltingManager getSmeltingManager() {
        return (SmeltingManager) skillManagers.get(SkillType.SMELTING);
    }

    public SwordsManager getSwordsManager() {
        return (SwordsManager) skillManagers.get(SkillType.SWORDS);
    }

    public TamingManager getTamingManager() {
        return (TamingManager) skillManagers.get(SkillType.TAMING);
    }

    public UnarmedManager getUnarmedManager() {
        return (UnarmedManager) skillManagers.get(SkillType.UNARMED);
    }

    public WoodcuttingManager getWoodcuttingManager() {
        return (WoodcuttingManager) skillManagers.get(SkillType.WOODCUTTING);
    }

    /*
     * Abilities
     */

    /**
     * Reset the mode of all abilities.
     */
    public void resetAbilityMode() {
        for (AbilityType ability : AbilityType.values()) {
            // Correctly disable and handle any special deactivate code
            new AbilityDisableTask(this, ability).run();
        }
    }

    /**
     * Get the mode of an ability.
     *
     * @param ability The ability to check
     * @return true if the ability is enabled, false otherwise
     */
    public boolean getAbilityMode(AbilityType ability) {
        return abilityMode.get(ability);
    }

    /**
     * Set the mode of an ability.
     *
     * @param ability The ability to check
     * @param isActive True if the ability is active, false otherwise
     */
    public void setAbilityMode(AbilityType ability, boolean isActive) {
        abilityMode.put(ability, isActive);
    }

    /**
     * Get the informed state of an ability
     *
     * @param ability The ability to check
     * @return true if the ability is informed, false otherwise
     */
    public boolean getAbilityInformed(AbilityType ability) {
        return abilityInformed.get(ability);
    }

    /**
     * Set the informed state of an ability.
     *
     * @param ability The ability to check
     * @param isInformed True if the ability is informed, false otherwise
     */
    public void setAbilityInformed(AbilityType ability, boolean isInformed) {
        abilityInformed.put(ability, isInformed);
    }

    /**
     * Get the current prep mode of a tool.
     *
     * @param tool Tool to get the mode for
     * @return true if the tool is prepped, false otherwise
     */
    public boolean getToolPreparationMode(ToolType tool) {
        return toolMode.get(tool);
    }

    public boolean getAbilityUse() {
        return abilityUse;
    }

    public void toggleAbilityUse() {
        abilityUse = !abilityUse;
    }

    /*
     * Tools
     */

    /**
     * Reset the prep modes of all tools.
     */
    public void resetToolPrepMode() {
        for (ToolType tool : ToolType.values()) {
            setToolPreparationMode(tool, false);
        }
    }

    /**
     * Set the current prep mode of a tool.
     *
     * @param tool Tool to set the mode for
     * @param isPrepared true if the tool should be prepped, false otherwise
     */
    public void setToolPreparationMode(ToolType tool, boolean isPrepared) {
        toolMode.put(tool, isPrepared);
    }

    /**
     * Get the current prep ATS of a tool.
     *
     * @param tool Tool to get the ATS for
     * @return the ATS for the tool
     */
    public long getToolPreparationATS(ToolType tool) {
        return toolATS.get(tool);
    }

    /**
     * Set the current prep ATS of a tool.
     *
     * @param tool Tool to set the ATS for
     * @param ATS the ATS of the tool
     */
    public void setToolPreparationATS(ToolType tool, long ATS) {
        int startTime = (int) (ATS / Misc.TIME_CONVERSION_FACTOR);

        toolATS.put(tool, startTime);
    }

    /*
     * Recently Hurt
     */

    public int getRecentlyHurt() {
        return recentlyHurt;
    }

    public void setRecentlyHurt(int value) {
        recentlyHurt = value;
    }

    public void actualizeRecentlyHurt() {
        recentlyHurt = (int) (System.currentTimeMillis() / Misc.TIME_CONVERSION_FACTOR);
    }

    /*
     * Teleportation cooldown & warmup
     */

    public int getChimeraWingLastUse() {
        return chimeraWingLastUse;
    }

    public void actualizeChimeraWingLastUse() {
        chimeraWingLastUse = (int) (System.currentTimeMillis() / Misc.TIME_CONVERSION_FACTOR);
    }

    public Location getTeleportCommenceLocation() {
        return teleportCommence;
    }

    public void setTeleportCommenceLocation(Location location) {
        teleportCommence = location;
    }

    public void actualizeTeleportCommenceLocation(Player player) {
        teleportCommence = player.getLocation();
    }

    /*
     * Exploit Prevention
     */

    public int getRespawnATS() {
        return respawnATS;
    }

    public void actualizeRespawnATS() {
        respawnATS = (int) (System.currentTimeMillis() / Misc.TIME_CONVERSION_FACTOR);
    }

    public int getTeleportATS() {
        return teleportATS;
    }

    public void actualizeTeleportATS() {
        teleportATS = (int) (System.currentTimeMillis() / Misc.TIME_CONVERSION_FACTOR);
    }

    public long getDatabaseATS() {
        return databaseATS;
    }

    public void actualizeDatabaseATS() {
        databaseATS = System.currentTimeMillis();
    }

    /*
     * God Mode
     */

    public boolean getGodMode() {
        return godMode;
    }

    public void toggleGodMode() {
        godMode = !godMode;
    }

    /*
     * Skill notifications
     */

    public boolean useChatNotifications() {
        return displaySkillNotifications;
    }

    public void toggleChatNotifications() {
        displaySkillNotifications = !displaySkillNotifications;
    }

    /**
     * Gets the power level of this player.
     *
     * @return the power level of the player
     */
    public int getPowerLevel() {
        int powerLevel = 0;

        for (SkillType type : SkillType.NON_CHILD_SKILLS) {
            if (type.getPermissions(player)) {
                powerLevel += getSkillLevel(type);
            }
        }

        return powerLevel;
    }

    /**
     * Begins an experience gain. The amount will be affected by skill modifiers, global rate, perks, and may be shared with the party
     *
     * @param skill Skill being used
     * @param xp Experience amount to process
     */
    public void beginXpGain(SkillType skill, float xp) {
        Validate.isTrue(xp >= 0.0, "XP gained should be greater than or equal to zero.");

        if (xp <= 0.0) {
            return;
        }

        if (skill.isChildSkill()) {
            Set<SkillType> parentSkills = FamilyTree.getParents(skill);
            float splitXp = xp / parentSkills.size();

            for (SkillType parentSkill : parentSkills) {
                if (parentSkill.getPermissions(player)) {
                    beginXpGain(parentSkill, splitXp);
                }
            }

            return;
        }

        // Return if the experience has been shared
        if (party != null && ShareHandler.handleXpShare(xp, this, skill)) {
            return;
        }

        beginUnsharedXpGain(skill, xp);
    }

    /**
     * Begins an experience gain. The amount will be affected by skill modifiers, global rate and perks
     *
     * @param skill Skill being used
     * @param xp Experience amount to process
     */
    public void beginUnsharedXpGain(SkillType skill, float xp) {
        applyXpGain(skill, modifyXpGain(skill, xp));
    }

    /**
     * Applies an experience gain
     *
     * @param skillType Skill being used
     * @param xp Experience amount to add
     */
    public void applyXpGain(SkillType skillType, float xp) {
        if (!skillType.getPermissions(player)) {
            return;
        }

        if (skillType.isChildSkill()) {
            Set<SkillType> parentSkills = FamilyTree.getParents(skillType);

            for (SkillType parentSkill : parentSkills) {
                applyXpGain(parentSkill, xp / parentSkills.size());
            }

            return;
        }

        if (!EventUtils.handleXpGainEvent(player, skillType, xp)) {
            return;
        }

        isUsingUnarmed = (skillType == SkillType.UNARMED);
        checkXp(skillType);
    }

    /**
     * Check the XP of a skill.
     *
     * @param skillType The skill to check
     */
    private void checkXp(SkillType skillType) {
        if (getSkillXpLevelRaw(skillType) < getXpToLevel(skillType)) {
            return;
        }

        int levelsGained = 0;
        float xpRemoved = 0;

        while (getSkillXpLevelRaw(skillType) >= getXpToLevel(skillType)) {
            if (hasReachedLevelCap(skillType)) {
                setSkillXpLevel(skillType, 0);
                break;
            }

            xpRemoved += profile.levelUp(skillType);
            levelsGained++;
        }

        if (!EventUtils.handleLevelChangeEvent(player, skillType, levelsGained, xpRemoved, true)) {
            return;
        }

        if (Config.getInstance().getLevelUpSoundsEnabled()) {
            player.playSound(player.getLocation(), Sound.LEVEL_UP, Misc.LEVELUP_VOLUME, Misc.LEVELUP_PITCH);
        }

        player.sendMessage(LocaleLoader.getString(StringUtils.getCapitalized(skillType.toString()) + ".Skillup", levelsGained, getSkillLevel(skillType)));
    }

    /*
     * Players & Profiles
     */

    public Player getPlayer() {
        return player;
    }

    public void setPlayer(Player player) {
        this.player = player;
    }

    public PlayerProfile getProfile() {
        return profile;
    }

    /*
     * Party Stuff
     */

    public void setPartyInvite(Party invite) {
        this.invite = invite;
    }

    public Party getPartyInvite() {
        return invite;
    }

    public boolean hasPartyInvite() {
        return (invite != null);
    }

    public void setParty(Party party) {
        this.party = party;
    }

    public Party getParty() {
        return party;
    }

    public boolean inParty() {
        return (party != null);
    }

    public void removeParty() {
        party = null;
    }

    public void removePartyInvite() {
        invite = null;
    }

    public PartyTeleportRecord getPartyTeleportRecord() {
        return ptpRecord;
    }

    public int getItemShareModifier() {
        if (itemShareModifier < 10) {
            setItemShareModifier(10);
        }

        return itemShareModifier;
    }

    public void setItemShareModifier(int modifier) {
        itemShareModifier = Math.max(10, modifier);
    }

    /*
     * Chat modes
     */

    public boolean isChatEnabled(ChatMode mode) {
        switch (mode) {
            case ADMIN:
                return adminChatMode;

            case PARTY:
                return partyChatMode;

            default:
                return false;
        }
    }

    public void disableChat(ChatMode mode) {
        switch (mode) {
            case ADMIN:
                adminChatMode = false;
                return;

            case PARTY:
                partyChatMode = false;
                return;

            default:
                return;
        }
    }

    public void enableChat(ChatMode mode) {
        switch (mode) {
            case ADMIN:
                adminChatMode = true;
                partyChatMode = false;
                return;

            case PARTY:
                partyChatMode = true;
                adminChatMode = false;
                return;

            default:
                return;
        }

    }

    public void toggleChat(ChatMode mode) {
        switch (mode) {
            case ADMIN:
                adminChatMode = !adminChatMode;
                partyChatMode = adminChatMode ? false : partyChatMode;
                return;

            case PARTY:
                partyChatMode = !partyChatMode;
                adminChatMode = partyChatMode ? false : adminChatMode;
                return;

            default:
                return;
        }
    }

    public boolean isUsingUnarmed() {
        return isUsingUnarmed;
    }

    /**
     * Modifies an experience gain using skill modifiers, global rate and perks
     *
     * @param skillType Skill being used
     * @param xp Experience amount to process
     * @return Modified experience
     */
    private float modifyXpGain(SkillType skillType, float xp) {
        if (player.getGameMode() == GameMode.CREATIVE || (skillType.getMaxLevel() <= getSkillLevel(skillType)) || (Config.getInstance().getPowerLevelCap() <= getPowerLevel())) {
            return 0;
        }

        xp = (float) (xp / skillType.getXpModifier() * ExperienceConfig.getInstance().getExperienceGainsGlobalMultiplier());

        if (Config.getInstance().getToolModsEnabled()) {
            CustomTool tool = ModUtils.getToolFromItemStack(player.getItemInHand());

            if (tool != null) {
                xp *= tool.getXpMultiplier();
            }
        }

        return PerksUtils.handleXpPerks(player, xp, skillType);
    }

    public void checkGodMode() {
        if (godMode && !Permissions.mcgod(player)) {
            toggleGodMode();
            player.sendMessage(LocaleLoader.getString("Commands.GodMode.Forbidden"));
        }
    }

    public void checkParty() {
        if (inParty() && !Permissions.party(player)) {
            removeParty();
            player.sendMessage(LocaleLoader.getString("Party.Forbidden"));
        }
    }

    /**
     * Check to see if an ability can be activated.
     *
     * @param skill The skill the ability is based on
     */
    public void checkAbilityActivation(SkillType skill) {
        ToolType tool = skill.getTool();
        AbilityType ability = skill.getAbility();

        setToolPreparationMode(tool, false);

        if (getAbilityMode(ability)) {
            return;
        }

        int timeRemaining = calculateTimeRemaining(ability);

        if (timeRemaining > 0) {
            /*
             * Axes and Woodcutting are odd because they share the same tool.
             * We show them the too tired message when they take action.
             */
            if (skill == SkillType.WOODCUTTING || skill == SkillType.AXES) {
                player.sendMessage(LocaleLoader.getString("Skills.TooTired", timeRemaining));
            }

            return;
        }

        if (EventUtils.callPlayerAbilityActivateEvent(player, skill).isCancelled()) {
            return;
        }

        int ticks = PerksUtils.handleActivationPerks(player, 2 + (getSkillLevel(skill) / AdvancedConfig.getInstance().getAbilityLength()), ability.getMaxLength());

        // Notify people that ability has been activated
        ParticleEffectUtils.playAbilityEnabledEffect(player);

        if (useChatNotifications()) {
            player.sendMessage(ability.getAbilityOn());
        }

        SkillUtils.sendSkillMessage(player, ability.getAbilityPlayer(player));

        // Enable the ability
        profile.setAbilityDATS(ability, System.currentTimeMillis() + (ticks * Misc.TIME_CONVERSION_FACTOR));
        setAbilityMode(ability, true);

        if (ability == AbilityType.SUPER_BREAKER || ability == AbilityType.GIGA_DRILL_BREAKER) {
            SkillUtils.handleAbilitySpeedIncrease(player);
        }

        new AbilityDisableTask(this, ability).runTaskLater(mcMMO.p, ticks * Misc.TICK_CONVERSION_FACTOR);
    }

    public void processAbilityActivation(SkillType skill) {
        if (Config.getInstance().getAbilitiesOnlyActivateWhenSneaking() && !player.isSneaking()) {
            return;
        }

        ItemStack inHand = player.getItemInHand();

        if (ModUtils.isCustomTool(inHand) && !ModUtils.getToolFromItemStack(inHand).isAbilityEnabled()) {
            return;
        }

        if (!getAbilityUse()) {
            return;
        }

        for (AbilityType abilityType : AbilityType.values()) {
            if (getAbilityMode(abilityType)) {
                return;
            }
        }

        AbilityType ability = skill.getAbility();
        ToolType tool = skill.getTool();

        /*
         * Woodcutting & Axes need to be treated differently.
         * Basically the tool always needs to ready and we check to see if the cooldown is over when the user takes action
         */
        if (ability.getPermissions(player) && tool.inHand(inHand) && !getToolPreparationMode(tool)) {
            if (skill != SkillType.WOODCUTTING && skill != SkillType.AXES) {
                int timeRemaining = calculateTimeRemaining(ability);

                if (!getAbilityMode(ability) && timeRemaining > 0) {
                    player.sendMessage(LocaleLoader.getString("Skills.TooTired", timeRemaining));
                    return;
                }
            }

            if (Config.getInstance().getAbilityMessagesEnabled()) {
                player.sendMessage(tool.getRaiseTool());
            }

            setToolPreparationATS(tool, System.currentTimeMillis());
            setToolPreparationMode(tool, true);
            new ToolLowerTask(this, tool).runTaskLaterAsynchronously(mcMMO.p, 4 * Misc.TICK_CONVERSION_FACTOR);
        }
    }

    /**
     * Calculate the time remaining until the ability's cooldown expires.
     *
     * @param ability AbilityType whose cooldown to check
     *
     * @return the number of seconds remaining before the cooldown expires
     */
    public int calculateTimeRemaining(AbilityType ability) {
        long deactivatedTimestamp = profile.getAbilityDATS(ability) * Misc.TIME_CONVERSION_FACTOR;
        return (int) (((deactivatedTimestamp + (PerksUtils.handleCooldownPerks(player, ability.getCooldown()) * Misc.TIME_CONVERSION_FACTOR)) - System.currentTimeMillis()) / Misc.TIME_CONVERSION_FACTOR);
    }

    private boolean hasReachedLevelCap(SkillType skill) {
        return (skill.getMaxLevel() < getSkillLevel(skill) + 1) || (Config.getInstance().getPowerLevelCap() < getPowerLevel() + 1);
    }

    /*
     * These functions are wrapped from PlayerProfile so that we don't always have to store it alongside the McMMOPlayer object.
     */
    public int getSkillLevel(SkillType skill) {
        return profile.getSkillLevel(skill);
    }

    public float getSkillXpLevelRaw(SkillType skill) {
        return profile.getSkillXpLevelRaw(skill);
    }

    public int getSkillXpLevel(SkillType skill) {
        return profile.getSkillXpLevel(skill);
    }

    public void setSkillXpLevel(SkillType skill, float xpLevel) {
        profile.setSkillXpLevel(skill, xpLevel);
    }

    public int getXpToLevel(SkillType skill) {
        return profile.getXpToLevel(skill);
    }

    public void removeXp(SkillType skill, int xp) {
        profile.removeXp(skill, xp);
    }

    public void modifySkill(SkillType skill, int level) {
        profile.modifySkill(skill, level);
    }

    public void addLevels(SkillType skill, int levels) {
        profile.addLevels(skill, levels);
    }

    public void addXp(SkillType skill, float xp) {
        profile.addXp(skill, xp);
    }

    public void setAbilityDATS(AbilityType ability, long DATS) {
        profile.setAbilityDATS(ability, DATS);
    }

    public void resetCooldowns() {
        profile.resetCooldowns();
    }
}
