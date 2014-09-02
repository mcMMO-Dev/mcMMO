package com.gmail.nossr50.datatypes.player;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import org.apache.commons.lang.Validate;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.metadata.FixedMetadataValue;

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
import com.gmail.nossr50.datatypes.skills.XPGainReason;
import com.gmail.nossr50.locale.LocaleLoader;
import com.gmail.nossr50.party.PartyManager;
import com.gmail.nossr50.party.ShareHandler;
import com.gmail.nossr50.runnables.skills.AbilityDisableTask;
import com.gmail.nossr50.runnables.skills.ToolLowerTask;
import com.gmail.nossr50.skills.SkillAbilityManager;
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
import com.gmail.nossr50.util.StringUtils;
import com.gmail.nossr50.util.skills.ParticleEffectUtils;
import com.gmail.nossr50.util.skills.PerksUtils;
import com.gmail.nossr50.util.skills.SkillUtils;

public class McMMOPlayer {
    private Player        player;
    private PlayerProfile profile;

    private final Map<SkillType, SkillManager> skillManagers = new HashMap<SkillType, SkillManager>();

    private Party   party;
    private Party   invite;
    private Party   allianceInvite;
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

    private int recentlyHurt;
    private int respawnATS;
    private int teleportATS;
    private long databaseATS;
    private int chimeraWingLastUse;
    private Location teleportCommence;

    private boolean isUsingUnarmed;
    private final FixedMetadataValue playerMetadata;

    public McMMOPlayer(Player player, PlayerProfile profile) {
        String playerName = player.getName();
        UUID uuid = player.getUniqueId();

        this.player = player;
        playerMetadata = new FixedMetadataValue(mcMMO.p, playerName);
        this.profile = profile;
        party = PartyManager.getPlayerParty(playerName);
        ptpRecord = new PartyTeleportRecord();

        if (profile.getUniqueId() == null) {
            profile.setUniqueId(uuid);
        }

        try {
            for (SkillType skillType : SkillType.getSkillList()) {
                skillManagers.put(skillType, skillType.getManagerClass().getConstructor(McMMOPlayer.class).newInstance(this));
            }
        }
        catch (Exception e) {
            e.printStackTrace();
            mcMMO.p.getPluginLoader().disablePlugin(mcMMO.p);
        }

        for (AbilityType abilityType : AbilityType.getAbilities()) {
            abilityMode.put(abilityType, false);
            abilityInformed.put(abilityType, true); // This is intended
        }

        for (ToolType toolType : ToolType.getToolList()) {
            toolMode.put(toolType, false);
        }
    }

    public AcrobaticsManager getAcrobaticsManager() {
        return (AcrobaticsManager) skillManagers.get(SkillType.acrobatics);
    }

    public AlchemyManager getAlchemyManager() {
        return (AlchemyManager) skillManagers.get(SkillType.alchemy);
    }

    public ArcheryManager getArcheryManager() {
        return (ArcheryManager) skillManagers.get(SkillType.archery);
    }

    public AxesManager getAxesManager() {
        return (AxesManager) skillManagers.get(SkillType.axes);
    }

    public ExcavationManager getExcavationManager() {
        return (ExcavationManager) skillManagers.get(SkillType.excavation);
    }

    public FishingManager getFishingManager() {
        return (FishingManager) skillManagers.get(SkillType.fishing);
    }

    public HerbalismManager getHerbalismManager() {
        return (HerbalismManager) skillManagers.get(SkillType.herbalism);
    }

    public MiningManager getMiningManager() {
        return (MiningManager) skillManagers.get(SkillType.mining);
    }

    public RepairManager getRepairManager() {
        return (RepairManager) skillManagers.get(SkillType.repair);
    }

    public SalvageManager getSalvageManager() {
        return (SalvageManager) skillManagers.get(SkillType.salvage);
    }

    public SmeltingManager getSmeltingManager() {
        return (SmeltingManager) skillManagers.get(SkillType.smelting);
    }

    public SwordsManager getSwordsManager() {
        return (SwordsManager) skillManagers.get(SkillType.swords);
    }

    public TamingManager getTamingManager() {
        return (TamingManager) skillManagers.get(SkillType.taming);
    }

    public UnarmedManager getUnarmedManager() {
        return (UnarmedManager) skillManagers.get(SkillType.unarmed);
    }

    public WoodcuttingManager getWoodcuttingManager() {
        return (WoodcuttingManager) skillManagers.get(SkillType.woodcutting);
    }
    
    public SkillManager getSkillManager(SkillType skillType) {
    	return skillManagers.get(skillType);
    }

    /*
     * Abilities
     */

    /**
     * Reset the mode of all abilities.
     */
    public void resetAbilityMode() {
        for (AbilityType ability : AbilityType.getAbilities()) {
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
        for (ToolType tool : ToolType.getToolList()) {
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

        for (SkillType type : SkillType.getNonChildSkills()) {
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
    public void beginXpGain(SkillType skill, float xp, XPGainReason xpGainReason) {
        Validate.isTrue(xp >= 0.0, "XP gained should be greater than or equal to zero.");

        if (xp <= 0.0) {
            return;
        }

        if (skill.isChildSkill()) {
            Set<SkillType> parentSkills = FamilyTree.getParents(skill);
            float splitXp = xp / parentSkills.size();

            for (SkillType parentSkill : parentSkills) {
                if (parentSkill.getPermissions(player)) {
                    beginXpGain(parentSkill, splitXp, xpGainReason);
                }
            }

            return;
        }

        // Return if the experience has been shared
        if (party != null && ShareHandler.handleXpShare(xp, this, skill, ShareHandler.getSharedXpGainReason(xpGainReason))) {
            return;
        }

        beginUnsharedXpGain(skill, xp, xpGainReason);
    }

    /**
     * Begins an experience gain. The amount will be affected by skill modifiers, global rate and perks
     *
     * @param skill Skill being used
     * @param xp Experience amount to process
     */
    public void beginUnsharedXpGain(SkillType skill, float xp, XPGainReason xpGainReason) {
        applyXpGain(skill, modifyXpGain(skill, xp), xpGainReason);

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
     * @param skillType Skill being used
     * @param xp Experience amount to add
     */
    public void applyXpGain(SkillType skillType, float xp, XPGainReason xpGainReason) {
        if (!skillType.getPermissions(player)) {
            return;
        }

        if (skillType.isChildSkill()) {
            Set<SkillType> parentSkills = FamilyTree.getParents(skillType);

            for (SkillType parentSkill : parentSkills) {
                applyXpGain(parentSkill, xp / parentSkills.size(), xpGainReason);
            }

            return;
        }

        if (!EventUtils.handleXpGainEvent(player, skillType, xp, xpGainReason)) {
            return;
        }

        isUsingUnarmed = (skillType == SkillType.unarmed);
        checkXp(skillType, xpGainReason);
    }

    /**
     * Check the XP of a skill.
     *
     * @param skillType The skill to check
     */
    private void checkXp(SkillType skillType, XPGainReason xpGainReason) {
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

        if (!EventUtils.handleLevelChangeEvent(player, skillType, levelsGained, xpRemoved, true, xpGainReason)) {
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
            CustomTool tool = mcMMO.getModManager().getTool(player.getItemInHand());

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
     * @return whether the ability was successfully activated
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
            if (skill == SkillType.woodcutting || skill == SkillType.axes) {
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

        if (ability == AbilityType.superBreaker || ability == AbilityType.gigaDrillBreaker) {
            SkillUtils.handleAbilitySpeedIncrease(player);
        }
        SkillManager manager = this.getSkillManager(skill);
        if(manager instanceof SkillAbilityManager) {
        	((SkillAbilityManager) manager).onAbilityActivated();
        }
        new AbilityDisableTask(this, ability).runTaskLater(mcMMO.p, ticks * Misc.TICK_CONVERSION_FACTOR);
    }

    public void processAbilityActivation(SkillType skill) {
        if (Config.getInstance().getAbilitiesOnlyActivateWhenSneaking() && !player.isSneaking()) {
            return;
        }

        ItemStack inHand = player.getItemInHand();

        if (mcMMO.getModManager().isCustomTool(inHand) && !mcMMO.getModManager().getTool(inHand).isAbilityEnabled()) {
            return;
        }

        if (!getAbilityUse()) {
            return;
        }

        for (AbilityType abilityType : AbilityType.getAbilities()) {
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
            if (skill != SkillType.woodcutting && skill != SkillType.axes) {
                int timeRemaining = calculateTimeRemaining(ability);

                if (!getAbilityMode(ability) && timeRemaining > 0) {
                    player.sendMessage(LocaleLoader.getString("Skills.TooTired", timeRemaining));
                    return;
                }
            }

            if (Config.getInstance().getAbilityMessagesEnabled()) {
                player.sendMessage(tool.getRaiseTool());
            }

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

    public FixedMetadataValue getPlayerMetadata() {
        return playerMetadata;
    }
}
