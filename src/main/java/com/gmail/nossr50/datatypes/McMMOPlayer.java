package com.gmail.nossr50.datatypes;

import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import com.gmail.nossr50.mcMMO;
import com.gmail.nossr50.config.Config;
import com.gmail.nossr50.events.experience.McMMOPlayerXpGainEvent;
import com.gmail.nossr50.mods.ModChecks;
import com.gmail.nossr50.mods.datatypes.CustomTool;
import com.gmail.nossr50.party.Party;
import com.gmail.nossr50.party.PartyManager;
import com.gmail.nossr50.party.ShareHandler;
import com.gmail.nossr50.skills.utilities.SkillTools;
import com.gmail.nossr50.skills.utilities.SkillType;
import com.gmail.nossr50.spout.huds.SpoutHud;
import com.gmail.nossr50.util.Misc;

public class McMMOPlayer {
    private Player player;
    private PlayerProfile profile;
    private Party party;
    private Party invite;
    private Player ptpRequest;
    private boolean ptpEnabled = true;
    private boolean ptpConfirmRequired = Config.getInstance().getPTPCommandConfirmRequired();
    private long ptpTimeout;
    private boolean partyChatMode;
    private boolean adminChatMode;

    public McMMOPlayer (Player player) {
        String playerName = player.getName();
        this.player = player;
        this.profile = new PlayerProfile(playerName, true);
        this.party = PartyManager.getPlayerParty(playerName);
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

            if (player.hasPermission("mcmmo.skills." + type.toString().toLowerCase())) {
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

        if (xp == 0) {
            return;
        }

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

        SpoutHud spoutHud = profile.getSpoutHud();

        if (spoutHud != null) {
            spoutHud.setLastGained(skillType);
        }

        SkillTools.xpCheckSkill(skillType, player, profile);
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
            CustomTool tool = ModChecks.getToolFromItemStack(item);

            if (tool != null) {
                xp *= tool.getXpMultiplier();
            }
        }

        // TODO: Too many permission checks here, is there no way to avoid that? 
        if (player.hasPermission("mcmmo.perks.xp.quadruple")) {
            xp *= 4;
        }
        else if (player.hasPermission("mcmmo.perks.xp.triple")) {
            xp *= 3;
        }
        else if (player.hasPermission("mcmmo.perks.xp.150percentboost")) {
            xp *= 2.5;
        }
        else if (player.hasPermission("mcmmo.perks.xp.150percentboost")) {
            xp *= 2;
        }
        else if (player.hasPermission("mcmmo.perks.xp.50percentboost")) {
            xp *= 1.5;
        }

        return xp;
    }

    // Players & Profiles

    public Player getPlayer() {
        return player;
    }

    public void setPlayer(Player player) {
        this.player = player;
    }

    public PlayerProfile getProfile() {
        return profile;
    }

    // Party Stuff

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
        return (ptpRequest != null) ? true : false;
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
}
