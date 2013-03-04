package com.gmail.nossr50.datatypes.player;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import com.gmail.nossr50.mcMMO;
import com.gmail.nossr50.config.Config;
import com.gmail.nossr50.datatypes.mods.CustomTool;
import com.gmail.nossr50.datatypes.party.Party;
import com.gmail.nossr50.datatypes.skills.AbilityType;
import com.gmail.nossr50.datatypes.skills.SkillType;
import com.gmail.nossr50.datatypes.skills.ToolType;
import com.gmail.nossr50.datatypes.spout.huds.McMMOHud;
import com.gmail.nossr50.events.experience.McMMOPlayerXpGainEvent;
import com.gmail.nossr50.party.PartyManager;
import com.gmail.nossr50.party.ShareHandler;
import com.gmail.nossr50.skills.SkillManager;
import com.gmail.nossr50.skills.acrobatics.AcrobaticsManager;
import com.gmail.nossr50.skills.archery.ArcheryManager;
import com.gmail.nossr50.skills.axes.AxesManager;
import com.gmail.nossr50.skills.child.FamilyTree;
import com.gmail.nossr50.skills.excavation.ExcavationManager;
import com.gmail.nossr50.skills.fishing.FishingManager;
import com.gmail.nossr50.skills.herbalism.HerbalismManager;
import com.gmail.nossr50.skills.mining.MiningManager;
import com.gmail.nossr50.skills.smelting.SmeltingManager;
import com.gmail.nossr50.skills.swords.SwordsManager;
import com.gmail.nossr50.skills.taming.TamingManager;
import com.gmail.nossr50.skills.unarmed.UnarmedManager;
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
    private Map<SkillType, SkillManager> skillManagers = new HashMap<SkillType, SkillManager>();

    private Party   party;
    private Party   invite;
    private int     itemShareModifier;

    private Player  ptpRequest;
    private boolean ptpEnabled = true;
    private boolean ptpConfirmRequired = Config.getInstance().getPTPCommandConfirmRequired();
    private long    ptpTimeout;

    private boolean partyChatMode;
    private boolean adminChatMode;
    private boolean displaySkillNotifications = true;

    private boolean abilityUse = true;
    private boolean placedAnvil;
    private boolean placedSalvageAnvil;
    private boolean godMode;

    private Map<AbilityType, Boolean> abilityMode     = new HashMap<AbilityType, Boolean>();
    private Map<AbilityType, Boolean> abilityInformed = new HashMap<AbilityType, Boolean>();

    private Map<ToolType, Boolean> toolMode = new HashMap<ToolType, Boolean>();
    private Map<ToolType, Integer> toolATS  = new HashMap<ToolType, Integer>();

    private int recentlyHurt;
    private int respawnATS;

    public McMMOPlayer(Player player) {
        String playerName = player.getName();

        this.player = player;
        profile = new PlayerProfile(playerName, true);
        party = PartyManager.getPlayerParty(playerName);

        /* 
         * I'm using this method because it makes code shorter and safer (we don't have to add all SkillTypes manually),
         * but I actually have no idea about the performance impact, if there is any.
         * If in the future someone wants to remove this, don't forget to also remove what is in the SkillType enum. - bm01
         */
        try {
            for (SkillType skillType : SkillType.values()) {
                Class<? extends SkillManager> skillManagerClass = skillType.getManagerClass();

                // TODO: The null check is needed only because currently some SkillType doesn't have a valid skillManagerClass 
                if (skillManagerClass != null) {
                    skillManagers.put(skillType, skillManagerClass.getConstructor(McMMOPlayer.class).newInstance(this));
                }
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

    /*
     * Abilities
     */

    /**
     * Reset the mode of all abilities.
     */
    public void resetAbilityMode() {
        for (AbilityType ability : AbilityType.values()) {
            setAbilityMode(ability, false);
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
     * @param bool True if the ability is active, false otherwise
     */
    public void setAbilityMode(AbilityType ability, boolean bool) {
        abilityMode.put(ability, bool);
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
     * @param bool True if the ability is informed, false otherwise
     */
    public void setAbilityInformed(AbilityType ability, boolean bool) {
        abilityInformed.put(ability, bool);
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
     * @param bool true if the tool should be prepped, false otherwise
     */
    public void setToolPreparationMode(ToolType tool, boolean bool) {
        toolMode.put(tool, bool);
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
     * Exploit Prevention
     */

    public int getRespawnATS() {
        return respawnATS;
    }

    public void actualizeRespawnATS() {
        respawnATS = (int) (System.currentTimeMillis() / Misc.TIME_CONVERSION_FACTOR);
    }

    /*
     * Repair Anvil Placement
     */

    public void togglePlacedAnvil() {
        placedAnvil = !placedAnvil;
    }

    public Boolean getPlacedAnvil() {
        return placedAnvil;
    }

    /*
     * Salvage Anvil Placement
     */

    public void togglePlacedSalvageAnvil() {
        placedSalvageAnvil = !placedSalvageAnvil;
    }

    public Boolean getPlacedSalvageAnvil() {
        return placedSalvageAnvil;
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

        for (SkillType type : SkillType.values()) {
            if (type.isChildSkill()) {
                continue;
            }

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
    public void beginXpGain(SkillType skillType, int xp) {
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
    public void beginUnsharedXpGain(SkillType skillType, int xp) {
        xp = modifyXpGain(skillType, xp);

        applyXpGain(skillType, xp);
    }

    /**
     * Applies an experience gain
     *
     * @param skillType Skill being used
     * @param xp Experience amount to add
     */
    public void applyXpGain(SkillType skillType, int xp) {
        McMMOPlayerXpGainEvent event = new McMMOPlayerXpGainEvent(player, skillType, xp);
        mcMMO.p.getServer().getPluginManager().callEvent(event);

        if (event.isCancelled()) {
            return;
        }

        profile.setSkillXpLevel(skillType, profile.getSkillXpLevel(skillType) + event.getXpGained());

        McMMOHud spoutHud = profile.getSpoutHud();

        if (spoutHud != null) {
            spoutHud.setLastGained(skillType);
        }

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
        if (invite != null) {
            return true;
        }

        return false;
    }

    public void setParty(Party party) {
        this.party = party;
    }

    public Party getParty() {
        return party;
    }

    public boolean inParty() {
        if (party != null) {
            return true;
        }

        return false;
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
        if (ptpRequest != null) {
            return true;
        }

        return false;
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
        if (modifier < 10) {
            modifier = 10;
        }

        itemShareModifier = modifier;
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

    /**
     * Modifies an experience gain using skill modifiers, global rate and perks
     *
     * @param skillType Skill being used
     * @param xp Experience amount to process
     * @return Modified experience
     */
    private int modifyXpGain(SkillType skillType, int xp) {
        if (player.getGameMode() == GameMode.CREATIVE) {
            return 0;
        }

        if ((skillType.getMaxLevel() < profile.getSkillLevel(skillType) + 1) || (Config.getInstance().getPowerLevelCap() < getPowerLevel() + 1)) {
            return 0;
        }

        xp = (int) (xp / skillType.getXpModifier() * Config.getInstance().getExperienceGainsGlobalMultiplier());

        if (Config.getInstance().getToolModsEnabled()) {
            ItemStack item = player.getItemInHand();
            CustomTool tool = ModUtils.getToolFromItemStack(item);

            if (tool != null) {
                xp *= tool.getXpMultiplier();
            }
        }

        return PerksUtils.handleXpPerks(player, xp);
    }
}
