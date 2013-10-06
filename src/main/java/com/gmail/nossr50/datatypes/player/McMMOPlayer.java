package com.gmail.nossr50.datatypes.player;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Server;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import com.gmail.nossr50.mcMMO;
import com.gmail.nossr50.config.Config;
import com.gmail.nossr50.config.experience.ExperienceConfig;
import com.gmail.nossr50.datatypes.mods.CustomTool;
import com.gmail.nossr50.datatypes.party.Party;
import com.gmail.nossr50.datatypes.skills.AbilityType;
import com.gmail.nossr50.datatypes.skills.SkillType;
import com.gmail.nossr50.datatypes.skills.ToolType;
import com.gmail.nossr50.events.experience.McMMOPlayerXpGainEvent;
import com.gmail.nossr50.locale.LocaleLoader;
import com.gmail.nossr50.party.PartyManager;
import com.gmail.nossr50.party.ShareHandler;
import com.gmail.nossr50.runnables.skills.AbilityDisableTask;
import com.gmail.nossr50.skills.SkillManager;
import com.gmail.nossr50.skills.acrobatics.AcrobaticsManager;
import com.gmail.nossr50.skills.archery.ArcheryManager;
import com.gmail.nossr50.skills.axes.AxesManager;
import com.gmail.nossr50.skills.child.FamilyTree;
import com.gmail.nossr50.skills.excavation.ExcavationManager;
import com.gmail.nossr50.skills.fishing.FishingManager;
import com.gmail.nossr50.skills.herbalism.HerbalismManager;
import com.gmail.nossr50.skills.mining.MiningManager;
import com.gmail.nossr50.skills.repair.Repair;
import com.gmail.nossr50.skills.repair.RepairManager;
import com.gmail.nossr50.skills.smelting.SmeltingManager;
import com.gmail.nossr50.skills.swords.SwordsManager;
import com.gmail.nossr50.skills.taming.TamingManager;
import com.gmail.nossr50.skills.unarmed.UnarmedManager;
import com.gmail.nossr50.skills.woodcutting.WoodcuttingManager;
import com.gmail.nossr50.util.Misc;
import com.gmail.nossr50.util.ModUtils;
import com.gmail.nossr50.util.Permissions;
import com.gmail.nossr50.util.skills.PerksUtils;
import com.gmail.nossr50.util.skills.SkillUtils;

public class McMMOPlayer {
    private Player        player;
    private PlayerProfile profile;

    /*
     * Since SkillManager isn't a "polymorphic type" we may prefer to have one field for each of our
     * class inheriting from SkillManager. This would also prevent the need for casting.
     * However, by using the map and the loop in the constructor
     * we make sure that all class inheriting from SkillManager are instanced.
     * Which solution is better, I let you decide. - bm01
     */
    private final Map<SkillType, SkillManager> skillManagers = new HashMap<SkillType, SkillManager>();

    private Party   party;
    private Party   invite;
    private int     itemShareModifier;

    private Player  ptpRequest;
    private boolean ptpEnabled = true;
    private boolean ptpConfirmRequired = Config.getInstance().getPTPCommandConfirmRequired();
    private long    ptpTimeout;
    private int     ptpLastUse;

    private boolean partyChatMode;
    private boolean adminChatMode;
    private boolean displaySkillNotifications = true;

    private boolean abilityUse = true;
    private boolean placedRepairAnvil;
    private int     lastRepairClick;
    private boolean placedSalvageAnvil;
    private int     lastSalvageClick;
    private boolean godMode;

    private final Map<AbilityType, Boolean> abilityMode     = new HashMap<AbilityType, Boolean>();
    private final Map<AbilityType, Boolean> abilityInformed = new HashMap<AbilityType, Boolean>();

    private final Map<ToolType, Boolean> toolMode = new HashMap<ToolType, Boolean>();
    private final Map<ToolType, Integer> toolATS  = new HashMap<ToolType, Integer>();

    private int recentlyHurt;
    private int respawnATS;
    private int teleportATS;
    private int chimeraWingLastUse;
    private Location teleportCommence;

    private boolean isUsingUnarmed;

    public McMMOPlayer(Player player) {
        String playerName = player.getName();

        this.player = player;
        profile = mcMMO.getDatabaseManager().loadPlayerProfile(playerName, true);
        party = PartyManager.getPlayerParty(playerName);

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
                Bukkit.broadcast(LocaleLoader.getString("Recovery.AdminFailureNotice", playerName), Server.BROADCAST_CHANNEL_ADMINISTRATIVE);
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

    /*
     * Repair Anvil Placement
     */

    public boolean getPlacedAnvil(Material anvilType) {
        if (anvilType == Repair.repairAnvilMaterial) {
            return placedRepairAnvil;
        }

        if (anvilType == Repair.salvageAnvilMaterial) {
            return placedSalvageAnvil;
        }

        return true;
    }

    public void togglePlacedAnvil(Material anvilType) {
        if (anvilType == Repair.repairAnvilMaterial) {
            placedRepairAnvil = !placedRepairAnvil;
        }

        if (anvilType == Repair.salvageAnvilMaterial) {
            placedSalvageAnvil = !placedSalvageAnvil;
        }
    }

    /*
     * Repair Anvil Usage
     */

    public int getLastAnvilUse(Material anvilType) {
        if (anvilType == Repair.repairAnvilMaterial) {
            return lastRepairClick;
        }

        if (anvilType == Repair.salvageAnvilMaterial) {
            return lastSalvageClick;
        }

        return 0;
    }

    public void setLastAnvilUse(Material anvilType, int value) {
        if (anvilType == Repair.repairAnvilMaterial) {
            lastRepairClick = value;
        }

        if (anvilType == Repair.salvageAnvilMaterial) {
            lastSalvageClick = value;
        }
    }

    public void actualizeLastAnvilUse(Material anvilType) {
        if (anvilType == Repair.repairAnvilMaterial) {
            lastRepairClick = (int) (System.currentTimeMillis() / Misc.TIME_CONVERSION_FACTOR);
        }

        if (anvilType == Repair.salvageAnvilMaterial) {
            lastSalvageClick = (int) (System.currentTimeMillis() / Misc.TIME_CONVERSION_FACTOR);
        }
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

        for (SkillType type : SkillType.nonChildSkills()) {
            if (Permissions.skillEnabled(player, type)) {
                powerLevel += profile.getSkillLevel(type);
            }
        }

        return powerLevel;
    }

    /**
     * Begins an experience gain. The amount will be affected by skill modifiers, global rate, perks, and may be shared with the party
     *
     * @param skillType Skill being used
     * @param xp Experience amount to process
     */
    public void beginXpGain(SkillType skillType, float xp) {
        if (xp == 0) {
            return;
        }

        if (skillType.isChildSkill()) {
            Set<SkillType> parentSkills = FamilyTree.getParents(skillType);

            for (SkillType parentSkill : parentSkills) {
                if (Permissions.skillEnabled(player, parentSkill)) {
                    beginXpGain(parentSkill, xp / parentSkills.size());
                }
            }

            return;
        }

        // Return if the experience has been shared
        if (party != null && ShareHandler.handleXpShare(xp, this, skillType)) {
            return;
        }

        beginUnsharedXpGain(skillType, xp);
    }

    /**
     * Begins an experience gain. The amount will be affected by skill modifiers, global rate and perks
     *
     * @param skillType Skill being used
     * @param xp Experience amount to process
     */
    public void beginUnsharedXpGain(SkillType skillType, float xp) {
        xp = modifyXpGain(skillType, xp);

        applyXpGain(skillType, xp);
    }

    /**
     * Applies an experience gain
     *
     * @param skillType Skill being used
     * @param xp Experience amount to add
     */
    public void applyXpGain(SkillType skillType, float xp) {
        if (!Permissions.skillEnabled(player, skillType)) {
            return;
        }

        if (skillType.isChildSkill()) {
            Set<SkillType> parentSkills = FamilyTree.getParents(skillType);

            for (SkillType parentSkill : parentSkills) {
                applyXpGain(parentSkill, xp / parentSkills.size());
            }

            return;
        }

        McMMOPlayerXpGainEvent event = new McMMOPlayerXpGainEvent(player, skillType, xp);
        mcMMO.p.getServer().getPluginManager().callEvent(event);

        if (event.isCancelled()) {
            return;
        }

        profile.setSkillXpLevel(skillType, profile.getSkillXpLevelRaw(skillType) + event.getRawXpGained());

        isUsingUnarmed = (skillType == SkillType.UNARMED);
        SkillUtils.xpCheckSkill(skillType, player, profile);
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

    public boolean getPtpEnabled() {
        return ptpEnabled;
    }

    public void togglePtpUse() {
        ptpEnabled = !ptpEnabled;
    }

    public Player getPtpRequest() {
        return ptpRequest;
    }

    public void setPtpRequest(Player ptpRequest) {
        this.ptpRequest = ptpRequest;
    }

    public boolean hasPtpRequest() {
        return (ptpRequest != null);
    }

    public void removePtpRequest() {
        ptpRequest = null;
    }

    public boolean getPtpConfirmRequired() {
        return ptpConfirmRequired;
    }

    public void togglePtpConfirmRequired() {
        ptpConfirmRequired = !ptpConfirmRequired;
    }

    public int getPtpLastUse() {
        return ptpLastUse;
    }

    public void actualizePtpLastUse() {
        ptpLastUse = (int) (System.currentTimeMillis() / Misc.TIME_CONVERSION_FACTOR);
    }

    public long getPtpTimeout() {
        return ptpTimeout;
    }

    public void actualizePtpTimeout() {
        ptpTimeout = (int) (System.currentTimeMillis() / Misc.TIME_CONVERSION_FACTOR);
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

    public boolean getAdminChatMode() {
        return adminChatMode;
    }

    public void setAdminChat(boolean enabled) {
        adminChatMode = enabled;
    }

    public void toggleAdminChat() {
        adminChatMode = !adminChatMode;
    }

    public boolean getPartyChatMode() {
        return partyChatMode;
    }

    public void setPartyChat(boolean enabled) {
        partyChatMode = enabled;
    }

    public void togglePartyChat() {
        partyChatMode = !partyChatMode;
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
        if (player.getGameMode() == GameMode.CREATIVE || (skillType.getMaxLevel() <= profile.getSkillLevel(skillType)) || (Config.getInstance().getPowerLevelCap() <= getPowerLevel())) {
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
}
