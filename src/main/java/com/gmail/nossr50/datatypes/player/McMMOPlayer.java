package com.gmail.nossr50.datatypes.player;

import com.gmail.nossr50.config.AdvancedConfig;
import com.gmail.nossr50.config.Config;
import com.gmail.nossr50.config.WorldBlacklist;
import com.gmail.nossr50.config.experience.ExperienceConfig;
import com.gmail.nossr50.datatypes.chat.ChatMode;
import com.gmail.nossr50.datatypes.experience.XPGainReason;
import com.gmail.nossr50.datatypes.experience.XPGainSource;
import com.gmail.nossr50.datatypes.interactions.NotificationType;
import com.gmail.nossr50.datatypes.mods.CustomTool;
import com.gmail.nossr50.datatypes.party.Party;
import com.gmail.nossr50.datatypes.party.PartyTeleportRecord;
import com.gmail.nossr50.datatypes.skills.PrimarySkillType;
import com.gmail.nossr50.datatypes.skills.SuperAbilityType;
import com.gmail.nossr50.datatypes.skills.ToolType;
import com.gmail.nossr50.locale.LocaleLoader;
import com.gmail.nossr50.mcMMO;
import com.gmail.nossr50.party.PartyManager;
import com.gmail.nossr50.party.ShareHandler;
import com.gmail.nossr50.runnables.skills.AbilityDisableTask;
import com.gmail.nossr50.runnables.skills.BleedTimerTask;
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
import com.gmail.nossr50.skills.salvage.SalvageManager;
import com.gmail.nossr50.skills.smelting.SmeltingManager;
import com.gmail.nossr50.skills.swords.SwordsManager;
import com.gmail.nossr50.skills.taming.TamingManager;
import com.gmail.nossr50.skills.unarmed.UnarmedManager;
import com.gmail.nossr50.skills.woodcutting.WoodcuttingManager;
import com.gmail.nossr50.util.EventUtils;
import com.gmail.nossr50.util.Misc;
import com.gmail.nossr50.util.Permissions;
import com.gmail.nossr50.util.experience.ExperienceBarManager;
import com.gmail.nossr50.util.player.NotificationManager;
import com.gmail.nossr50.util.player.UserManager;
import com.gmail.nossr50.util.scoreboards.ScoreboardManager;
import com.gmail.nossr50.util.skills.ParticleEffectUtils;
import com.gmail.nossr50.util.skills.PerksUtils;
import com.gmail.nossr50.util.skills.RankUtils;
import com.gmail.nossr50.util.skills.SkillUtils;
import com.gmail.nossr50.util.sounds.SoundManager;
import com.gmail.nossr50.util.sounds.SoundType;
import org.apache.commons.lang.Validate;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.plugin.Plugin;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

public class McMMOPlayer {
    private Player        player;
    private PlayerProfile profile;

    private final Map<PrimarySkillType, SkillManager> skillManagers = new HashMap<PrimarySkillType, SkillManager>();
    private ExperienceBarManager experienceBarManager;

    private Party   party;
    private Party   invite;
    private Party   allianceInvite;
    private int     itemShareModifier;

    private PartyTeleportRecord ptpRecord;

    private boolean partyChatMode;
    private boolean adminChatMode;
    private boolean displaySkillNotifications = true;
    private boolean debugMode;

    private boolean abilityUse = true;
    private boolean godMode;
    private boolean chatSpy = false; //Off by default

    private final Map<SuperAbilityType, Boolean> abilityMode     = new HashMap<SuperAbilityType, Boolean>();
    private final Map<SuperAbilityType, Boolean> abilityInformed = new HashMap<SuperAbilityType, Boolean>();

    private final Map<ToolType, Boolean> toolMode = new HashMap<ToolType, Boolean>();

    private int recentlyHurt;
    private int respawnATS;
    private int teleportATS;
    private long databaseATS;
    //private int chimeraWingLastUse;
    private Location teleportCommence;

    private boolean isUsingUnarmed;
    private final FixedMetadataValue playerMetadata;
    private String playerName;

    public McMMOPlayer(Player player, PlayerProfile profile) {
        this.playerName = player.getName();
        UUID uuid = player.getUniqueId();

        this.player = player;
        playerMetadata = new FixedMetadataValue(mcMMO.p, playerName);
        this.profile = profile;

        if (profile.getUniqueId() == null) {
            profile.setUniqueId(uuid);
        }

        /*
         * I'm using this method because it makes code shorter and safer (we don't have to add all SkillTypes manually),
         * but I actually have no idea about the performance impact, if there is any.
         * If in the future someone wants to remove this, don't forget to also remove what is in the PrimarySkillType enum. - bm01
         */
        try {
            for (PrimarySkillType primarySkillType : PrimarySkillType.values()) {
                skillManagers.put(primarySkillType, primarySkillType.getManagerClass().getConstructor(McMMOPlayer.class).newInstance(this));
            }
        }
        catch (Exception e) {
            e.printStackTrace();
            mcMMO.p.getPluginLoader().disablePlugin(mcMMO.p);
        }

        for (SuperAbilityType superAbilityType : SuperAbilityType.values()) {
            abilityMode.put(superAbilityType, false);
            abilityInformed.put(superAbilityType, true); // This is intended
        }

        for (ToolType toolType : ToolType.values()) {
            toolMode.put(toolType, false);
        }

        experienceBarManager = new ExperienceBarManager(this);

        debugMode = false; //Debug mode helps solve support issues, players can toggle it on or off
    }

    public String getPlayerName() {
        return playerName;
    }

    /*public void hideXpBar(PrimarySkillType primarySkillType)
    {
        experienceBarManager.hideExperienceBar(primarySkillType);
    }*/

    public void processPostXpEvent(PrimarySkillType primarySkillType, Plugin plugin, XPGainSource xpGainSource)
    {
        //Check if they've reached the power level cap just now
        if(hasReachedPowerLevelCap()) {
            NotificationManager.sendPlayerInformationChatOnly(player, "LevelCap.PowerLevel", String.valueOf(Config.getInstance().getPowerLevelCap()));
        } else if(hasReachedLevelCap(primarySkillType)) {
            NotificationManager.sendPlayerInformationChatOnly(player, "LevelCap.Skill", String.valueOf(Config.getInstance().getLevelCap(primarySkillType)), primarySkillType.getName());
        }

        //Updates from Party sources
        if(xpGainSource == XPGainSource.PARTY_MEMBERS && !ExperienceConfig.getInstance().isPartyExperienceBarsEnabled())
            return;

        //Updates from passive sources (Alchemy, Smelting, etc...)
        if(xpGainSource == XPGainSource.PASSIVE && !ExperienceConfig.getInstance().isPassiveGainsExperienceBarsEnabled())
            return;

        updateXPBar(primarySkillType, plugin);
    }

    public void processUnlockNotifications(mcMMO plugin, PrimarySkillType primarySkillType, int skillLevel)
    {
        RankUtils.executeSkillUnlockNotifications(plugin, this, primarySkillType, skillLevel);
    }

    public void updateXPBar(PrimarySkillType primarySkillType, Plugin plugin)
    {
        //Skill Unlock Notifications

        if(primarySkillType.isChildSkill())
            return;

        //XP BAR UPDATES
        experienceBarManager.updateExperienceBar(primarySkillType, plugin);
    }

    public double getProgressInCurrentSkillLevel(PrimarySkillType primarySkillType)
    {
        double currentXP = profile.getSkillXpLevel(primarySkillType);
        double maxXP = profile.getXpToLevel(primarySkillType);

        return (currentXP / maxXP);
    }

    public AcrobaticsManager getAcrobaticsManager() {
        return (AcrobaticsManager) skillManagers.get(PrimarySkillType.ACROBATICS);
    }

    public AlchemyManager getAlchemyManager() {
        return (AlchemyManager) skillManagers.get(PrimarySkillType.ALCHEMY);
    }

    public ArcheryManager getArcheryManager() {
        return (ArcheryManager) skillManagers.get(PrimarySkillType.ARCHERY);
    }

    public AxesManager getAxesManager() {
        return (AxesManager) skillManagers.get(PrimarySkillType.AXES);
    }

    public ExcavationManager getExcavationManager() {
        return (ExcavationManager) skillManagers.get(PrimarySkillType.EXCAVATION);
    }

    public FishingManager getFishingManager() {
        return (FishingManager) skillManagers.get(PrimarySkillType.FISHING);
    }

    public HerbalismManager getHerbalismManager() {
        return (HerbalismManager) skillManagers.get(PrimarySkillType.HERBALISM);
    }

    public MiningManager getMiningManager() {
        return (MiningManager) skillManagers.get(PrimarySkillType.MINING);
    }

    public RepairManager getRepairManager() {
        return (RepairManager) skillManagers.get(PrimarySkillType.REPAIR);
    }

    public SalvageManager getSalvageManager() {
        return (SalvageManager) skillManagers.get(PrimarySkillType.SALVAGE);
    }

    public SmeltingManager getSmeltingManager() {
        return (SmeltingManager) skillManagers.get(PrimarySkillType.SMELTING);
    }

    public SwordsManager getSwordsManager() {
        return (SwordsManager) skillManagers.get(PrimarySkillType.SWORDS);
    }

    public TamingManager getTamingManager() {
        return (TamingManager) skillManagers.get(PrimarySkillType.TAMING);
    }

    public UnarmedManager getUnarmedManager() {
        return (UnarmedManager) skillManagers.get(PrimarySkillType.UNARMED);
    }

    public WoodcuttingManager getWoodcuttingManager() {
        return (WoodcuttingManager) skillManagers.get(PrimarySkillType.WOODCUTTING);
    }

    /*
     * Abilities
     */

    /**
     * Reset the mode of all abilities.
     */
    public void resetAbilityMode() {
        for (SuperAbilityType ability : SuperAbilityType.values()) {
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
    public boolean getAbilityMode(SuperAbilityType ability) {
        return abilityMode.get(ability);
    }

    /**
     * Set the mode of an ability.
     *
     * @param ability The ability to check
     * @param isActive True if the ability is active, false otherwise
     */
    public void setAbilityMode(SuperAbilityType ability, boolean isActive) {
        abilityMode.put(ability, isActive);
    }

    /**
     * Get the informed state of an ability
     *
     * @param ability The ability to check
     * @return true if the ability is informed, false otherwise
     */
    public boolean getAbilityInformed(SuperAbilityType ability) {
        return abilityInformed.get(ability);
    }

    /**
     * Set the informed state of an ability.
     *
     * @param ability The ability to check
     * @param isInformed True if the ability is informed, false otherwise
     */
    public void setAbilityInformed(SuperAbilityType ability, boolean isInformed) {
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
        return profile.getChimaerWingDATS();
    }

    public void actualizeChimeraWingLastUse() {
        profile.setChimaeraWingDATS((int) (System.currentTimeMillis() / Misc.TIME_CONVERSION_FACTOR));
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
     * Party Chat Spy
     */

    public boolean isPartyChatSpying() { return chatSpy; }

    public void togglePartyChatSpying() { chatSpy = !chatSpy;}

    /*
     * Debug Mode Flags
     */

    public boolean isDebugMode() {
        return debugMode;
    }

    public void toggleDebugMode() {
        debugMode = !debugMode;
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

        for (PrimarySkillType type : PrimarySkillType.NON_CHILD_SKILLS) {
            if (type.getPermissions(player)) {
                powerLevel += getSkillLevel(type);
            }
        }

        return powerLevel;
    }

    /**
     * Whether or not a player is level capped
     * If they are at the power level cap, this will return true, otherwise it checks their skill level
     * @param primarySkillType
     * @return
     */
    public boolean hasReachedLevelCap(PrimarySkillType primarySkillType) {
        if(hasReachedPowerLevelCap())
            return true;

        return getSkillLevel(primarySkillType) >= Config.getInstance().getLevelCap(primarySkillType);
    }

    /**
     * Whether or not a player is power level capped
     * Compares their power level total to the current set limit
     * @return true if they have reached the power level cap
     */
    public boolean hasReachedPowerLevelCap() {
        return this.getPowerLevel() >= Config.getInstance().getPowerLevelCap();
    }

    /**
     * Begins an experience gain. The amount will be affected by skill modifiers, global rate, perks, and may be shared with the party
     *
     * @param skill Skill being used
     * @param xp Experience amount to process
     */
    public void beginXpGain(PrimarySkillType skill, float xp, XPGainReason xpGainReason, XPGainSource xpGainSource) {
        Validate.isTrue(xp >= 0.0, "XP gained should be greater than or equal to zero.");

        if (xp <= 0.0) {
            return;
        }

        if (skill.isChildSkill()) {
            Set<PrimarySkillType> parentSkills = FamilyTree.getParents(skill);
            float splitXp = xp / parentSkills.size();

            for (PrimarySkillType parentSkill : parentSkills) {
                if (parentSkill.getPermissions(player)) {
                    beginXpGain(parentSkill, splitXp, xpGainReason, xpGainSource);
                }
            }

            return;
        }

        // Return if the experience has been shared
        if (party != null && ShareHandler.handleXpShare(xp, this, skill, ShareHandler.getSharedXpGainReason(xpGainReason))) {
            return;
        }

        beginUnsharedXpGain(skill, xp, xpGainReason, xpGainSource);
    }

    /**
     * Begins an experience gain. The amount will be affected by skill modifiers, global rate and perks
     *
     * @param skill Skill being used
     * @param xp Experience amount to process
     */
    public void beginUnsharedXpGain(PrimarySkillType skill, float xp, XPGainReason xpGainReason, XPGainSource xpGainSource) {
        if(player.getGameMode() == GameMode.CREATIVE)
            return;

        applyXpGain(skill, modifyXpGain(skill, xp), xpGainReason, xpGainSource);

        if (party == null) {
            return;
        }

        if (!Config.getInstance().getPartyXpNearMembersNeeded() || !PartyManager.getNearMembers(this).isEmpty()) {
            party.applyXpGain(modifyXpGain(skill, xp));
        }
    }

    /**
     * Applies an experience gain
     *
     * @param primarySkillType Skill being used
     * @param xp Experience amount to add
     */
    public void applyXpGain(PrimarySkillType primarySkillType, float xp, XPGainReason xpGainReason, XPGainSource xpGainSource) {
        if (!primarySkillType.getPermissions(player)) {
            return;
        }

        if (primarySkillType.isChildSkill()) {
            Set<PrimarySkillType> parentSkills = FamilyTree.getParents(primarySkillType);

            for (PrimarySkillType parentSkill : parentSkills) {
                applyXpGain(parentSkill, xp / parentSkills.size(), xpGainReason, xpGainSource);
            }

            return;
        }

        if (!EventUtils.handleXpGainEvent(player, primarySkillType, xp, xpGainReason)) {
            return;
        }

        isUsingUnarmed = (primarySkillType == PrimarySkillType.UNARMED);
        checkXp(primarySkillType, xpGainReason, xpGainSource);
    }

    /**
     * Check the XP of a skill.
     *
     * @param primarySkillType The skill to check
     */
    private void checkXp(PrimarySkillType primarySkillType, XPGainReason xpGainReason, XPGainSource xpGainSource) {
        if(hasReachedLevelCap(primarySkillType))
            return;

        if (getSkillXpLevelRaw(primarySkillType) < getXpToLevel(primarySkillType)) {
            processPostXpEvent(primarySkillType, mcMMO.p, xpGainSource);
            return;
        }

        int levelsGained = 0;
        float xpRemoved = 0;

        while (getSkillXpLevelRaw(primarySkillType) >= getXpToLevel(primarySkillType)) {
            if (hasReachedLevelCap(primarySkillType)) {
                setSkillXpLevel(primarySkillType, 0);
                break;
            }

            xpRemoved += profile.levelUp(primarySkillType);
            levelsGained++;
        }

        if (EventUtils.tryLevelChangeEvent(player, primarySkillType, levelsGained, xpRemoved, true, xpGainReason)) {
            return;
        }

        if (Config.getInstance().getLevelUpSoundsEnabled()) {
            SoundManager.sendSound(player, player.getLocation(), SoundType.LEVEL_UP);
        }

        /*
         * Check to see if the player unlocked any new skills
         */

        NotificationManager.sendPlayerLevelUpNotification(this, primarySkillType, levelsGained, profile.getSkillLevel(primarySkillType));

        //UPDATE XP BARS
        processPostXpEvent(primarySkillType, mcMMO.p, xpGainSource);
    }

    /*
     * Players & Profiles
     */

    public Player getPlayer() {
        return player;
    }

    public PlayerProfile getProfile() {
        return profile;
    }

    /*
     * Party Stuff
     */

    public void setupPartyData() {
        party = PartyManager.getPlayerParty(player.getName(), player.getUniqueId());
        ptpRecord = new PartyTeleportRecord();

        if (inParty()) {
            loginParty();
        }
    }

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

    public void setPartyAllianceInvite(Party allianceInvite) {
        this.allianceInvite = allianceInvite;
    }

    public Party getPartyAllianceInvite() {
        return allianceInvite;
    }

    public boolean hasPartyAllianceInvite() {
        return (allianceInvite != null);
    }

    public void removePartyAllianceInvite() {
        allianceInvite = null;
    }

    public void loginParty() {
        party.addOnlineMember(this.getPlayer());
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
                partyChatMode = !adminChatMode && partyChatMode;
                return;

            case PARTY:
                partyChatMode = !partyChatMode;
                adminChatMode = !partyChatMode && adminChatMode;
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
     * @param primarySkillType Skill being used
     * @param xp Experience amount to process
     * @return Modified experience
     */
    private float modifyXpGain(PrimarySkillType primarySkillType, float xp) {
        if ((primarySkillType.getMaxLevel() <= getSkillLevel(primarySkillType)) || (Config.getInstance().getPowerLevelCap() <= getPowerLevel())) {
            return 0;
        }

        xp = (float) (xp / primarySkillType.getXpModifier() * ExperienceConfig.getInstance().getExperienceGainsGlobalMultiplier());

        if (Config.getInstance().getToolModsEnabled()) {
            CustomTool tool = mcMMO.getModManager().getTool(player.getInventory().getItemInMainHand());

            if (tool != null) {
                xp *= tool.getXpMultiplier();
            }
        }

        return PerksUtils.handleXpPerks(player, xp, primarySkillType);
    }

    public void checkGodMode() {
        if (godMode && !Permissions.mcgod(player)
            || godMode && WorldBlacklist.isWorldBlacklisted(player.getWorld())) {
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
    public void checkAbilityActivation(PrimarySkillType skill) {
        ToolType tool = skill.getTool();
        SuperAbilityType ability = skill.getAbility();

        if (getAbilityMode(ability) || !ability.getPermissions(player)) {
            return;
        }


        //TODO: This is hacky and temporary solution until skills are move to the new system
        //Potential problems with this include skills with two super abilities (ie mining)
        if(!skill.isSuperAbilityUnlocked(getPlayer()))
        {
            int diff = RankUtils.getSuperAbilityUnlockRequirement(skill.getAbility()) - getSkillLevel(skill);

            //Inform the player they are not yet skilled enough
            NotificationManager.sendPlayerInformation(player, NotificationType.ABILITY_COOLDOWN, "Skills.AbilityGateRequirementFail", String.valueOf(diff), skill.getName());
            return;
        }

        int timeRemaining = calculateTimeRemaining(ability);

        if (timeRemaining > 0) {
            /*
             * Axes and Woodcutting are odd because they share the same tool.
             * We show them the too tired message when they take action.
             */
            if (skill == PrimarySkillType.WOODCUTTING || skill == PrimarySkillType.AXES) {
                NotificationManager.sendPlayerInformation(player, NotificationType.ABILITY_COOLDOWN, "Skills.TooTired", String.valueOf(timeRemaining));
                //SoundManager.sendSound(player, player.getLocation(), SoundType.TIRED);
            }

            return;
        }

        if (EventUtils.callPlayerAbilityActivateEvent(player, skill).isCancelled()) {
            return;
        }

        //These values change depending on whether or not the server is in retro mode
        int abilityLengthVar = AdvancedConfig.getInstance().getAbilityLength();
        int abilityLengthCap = AdvancedConfig.getInstance().getAbilityLengthCap();

        int ticks;

        //Ability cap of 0 or below means no cap
        if(abilityLengthCap > 0)
        {
            ticks = PerksUtils.handleActivationPerks(player, 2 + (Math.min(abilityLengthCap, getSkillLevel(skill)) / abilityLengthVar), ability.getMaxLength());
        } else {
            ticks = PerksUtils.handleActivationPerks(player, 2 + (getSkillLevel(skill) / abilityLengthVar), ability.getMaxLength());
        }

        // Notify people that ability has been activated
        ParticleEffectUtils.playAbilityEnabledEffect(player);

        if (useChatNotifications()) {
            NotificationManager.sendPlayerInformation(player, NotificationType.SUPER_ABILITY, ability.getAbilityOn());
            //player.sendMessage(ability.getAbilityOn());
        }

        SkillUtils.sendSkillMessage(player, NotificationType.SUPER_ABILITY_ALERT_OTHERS, ability.getAbilityPlayer());

        //Sounds
        SoundManager.worldSendSound(player.getWorld(), player.getLocation(), SoundType.ABILITY_ACTIVATED_GENERIC);


        // Enable the ability
        profile.setAbilityDATS(ability, System.currentTimeMillis() + (ticks * Misc.TIME_CONVERSION_FACTOR));
        setAbilityMode(ability, true);

        if (ability == SuperAbilityType.SUPER_BREAKER || ability == SuperAbilityType.GIGA_DRILL_BREAKER) {
            SkillUtils.handleAbilitySpeedIncrease(player);
        }

        setToolPreparationMode(tool, false);
        new AbilityDisableTask(this, ability).runTaskLater(mcMMO.p, ticks * Misc.TICK_CONVERSION_FACTOR);
    }

    public void processAbilityActivation(PrimarySkillType skill) {
        if (Config.getInstance().getAbilitiesOnlyActivateWhenSneaking() && !player.isSneaking()) {
            return;
        }

        ItemStack inHand = player.getInventory().getItemInMainHand();

        if (mcMMO.getModManager().isCustomTool(inHand) && !mcMMO.getModManager().getTool(inHand).isAbilityEnabled()) {
            return;
        }

        if (!getAbilityUse()) {
            return;
        }

        for (SuperAbilityType superAbilityType : SuperAbilityType.values()) {
            if (getAbilityMode(superAbilityType)) {
                return;
            }
        }

        SuperAbilityType ability = skill.getAbility();
        ToolType tool = skill.getTool();

        /*
         * Woodcutting & Axes need to be treated differently.
         * Basically the tool always needs to ready and we check to see if the cooldown is over when the user takes action
         */
        if (tool.inHand(inHand) && !getToolPreparationMode(tool)) {
            if (skill != PrimarySkillType.WOODCUTTING && skill != PrimarySkillType.AXES) {
                int timeRemaining = calculateTimeRemaining(ability);

                if (!getAbilityMode(ability) && timeRemaining > 0) {
                    NotificationManager.sendPlayerInformation(player, NotificationType.ABILITY_COOLDOWN, "Skills.TooTired", String.valueOf(timeRemaining));
                    return;
                }
            }

            if (Config.getInstance().getAbilityMessagesEnabled()) {
                NotificationManager.sendPlayerInformation(player, NotificationType.TOOL, tool.getRaiseTool());
                SoundManager.sendSound(player, player.getLocation(), SoundType.TOOL_READY);
            }

            setToolPreparationMode(tool, true);
            new ToolLowerTask(this, tool).runTaskLater(mcMMO.p, 4 * Misc.TICK_CONVERSION_FACTOR);
        }
    }

    /**
     * Calculate the time remaining until the ability's cooldown expires.
     *
     * @param ability SuperAbilityType whose cooldown to check
     *
     * @return the number of seconds remaining before the cooldown expires
     */
    public int calculateTimeRemaining(SuperAbilityType ability) {
        long deactivatedTimestamp = profile.getAbilityDATS(ability) * Misc.TIME_CONVERSION_FACTOR;
        return (int) (((deactivatedTimestamp + (PerksUtils.handleCooldownPerks(player, ability.getCooldown()) * Misc.TIME_CONVERSION_FACTOR)) - System.currentTimeMillis()) / Misc.TIME_CONVERSION_FACTOR);
    }

    /*
     * These functions are wrapped from PlayerProfile so that we don't always have to store it alongside the McMMOPlayer object.
     */
    public int getSkillLevel(PrimarySkillType skill) {
        return profile.getSkillLevel(skill);
    }

    public float getSkillXpLevelRaw(PrimarySkillType skill) {
        return profile.getSkillXpLevelRaw(skill);
    }

    public int getSkillXpLevel(PrimarySkillType skill) {
        return profile.getSkillXpLevel(skill);
    }

    public void setSkillXpLevel(PrimarySkillType skill, float xpLevel) {
        profile.setSkillXpLevel(skill, xpLevel);
    }

    public int getXpToLevel(PrimarySkillType skill) {
        return profile.getXpToLevel(skill);
    }

    public void removeXp(PrimarySkillType skill, int xp) {
        profile.removeXp(skill, xp);
    }

    public void modifySkill(PrimarySkillType skill, int level) {
        profile.modifySkill(skill, level);
    }

    public void addLevels(PrimarySkillType skill, int levels) {
        profile.addLevels(skill, levels);
    }

    public void addXp(PrimarySkillType skill, float xp) {
        profile.addXp(skill, xp);
    }

    public void setAbilityDATS(SuperAbilityType ability, long DATS) {
        profile.setAbilityDATS(ability, DATS);
    }

    public void resetCooldowns() {
        profile.resetCooldowns();
    }

    public FixedMetadataValue getPlayerMetadata() {
        return playerMetadata;
    }

    /**
     * This method is called by PlayerQuitEvent to tear down the mcMMOPlayer.
     *
     * @param syncSave if true, data is saved synchronously
     */
    public void logout(boolean syncSave) {
        Player thisPlayer = getPlayer();
        BleedTimerTask.bleedOut(getPlayer());
        cleanup();

        if (syncSave) {
            getProfile().save(true);
        } else {
            getProfile().scheduleAsyncSave();
        }

        UserManager.remove(thisPlayer);

        if(Config.getInstance().getScoreboardsEnabled())
            ScoreboardManager.teardownPlayer(thisPlayer);

        if (inParty()) {
            party.removeOnlineMember(thisPlayer);
        }

        //Remove user from cache
        mcMMO.getDatabaseManager().cleanupUser(thisPlayer.getUniqueId());
    }

    /**
     * Cleanup various things related to this player
     * Such as temporary summons..
     * Turning off abilities...
     * Etc...
     */
    public void cleanup() {
        resetAbilityMode();
        getTamingManager().cleanupAllSummons();
    }
}
