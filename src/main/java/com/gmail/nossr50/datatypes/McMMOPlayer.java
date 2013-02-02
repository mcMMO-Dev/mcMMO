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
import com.gmail.nossr50.util.Permissions;

public class McMMOPlayer {
    private Player player;
    private PlayerProfile profile;
    private Party party;
    private Party invite;

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

            if (type.getPermissions(player)) {
                powerLevel += profile.getSkillLevel(type);
            }
        }

        return powerLevel;
    }

    /**
     * Adds Xp to the player, doesn't calculate for Xp Rate
     *
     * @param skillType The skill to add Xp to
     * @param xp The amount of Xp to add
     */
    public void addXpOverride(SkillType skillType, int xp) {
        if (skillType.equals(SkillType.ALL)) {
            for (SkillType type : SkillType.values()) {
                if (type.equals(SkillType.ALL) || type.isChildSkill()) {
                    continue;
                }

                mcMMO.p.getServer().getPluginManager().callEvent(new McMMOPlayerXpGainEvent(player, type, xp));
                profile.setSkillXpLevel(type, profile.getSkillXpLevel(type) + xp);
            }
        }
        else {
            mcMMO.p.getServer().getPluginManager().callEvent(new McMMOPlayerXpGainEvent(player, skillType, xp));
            profile.setSkillXpLevel(skillType, profile.getSkillXpLevel(skillType) + xp);

            SpoutHud spoutHud = profile.getSpoutHud();

            if (spoutHud != null) {
                spoutHud.setLastGained(skillType);
            }
        }
    }

    /**
     * Adds Xp to the player, this ignores skill modifiers.
     *
     * @param skillType The skill to add Xp to
     * @param xp The amount of Xp to add
     */
    public void addXpOverrideBonus(SkillType skillType, int xp) {
        int modifiedXp = (int) Math.floor(xp * Config.getInstance().getExperienceGainsGlobalMultiplier());
        addXpOverride(skillType, modifiedXp);
    }

    /**
     * Adds experience to the player, this is affected by skill modifiers, rate and permissions
     *
     * @param skillType The skill to add Xp to
     * @param xp The amount of Xp to add
     */
    public void addXp(SkillType skillType, int xp) {
        if (player.getGameMode() == GameMode.CREATIVE) {
            return;
        }

        if (party != null && !ShareHandler.isRunning()) {
            // Return if the Xp has been shared
            if (ShareHandler.handleEqualXpShare(xp, this, skillType)) {
                return;
            }
        }

        if ((skillType.getMaxLevel() < profile.getSkillLevel(skillType) + 1) || (Misc.getPowerLevelCap() < getPowerLevel() + 1)) {
            return;
        }

        xp = (int) (xp / skillType.getXpModifier() * Config.getInstance().getExperienceGainsGlobalMultiplier());

        if (Config.getInstance().getToolModsEnabled()) {
            ItemStack item = player.getItemInHand();
            CustomTool tool = ModChecks.getToolFromItemStack(item);

            if (tool != null) {
                xp = (int) (xp * tool.getXpMultiplier());
            }
        }

        // TODO: find a better way to do this, if possible
        if (Permissions.xpQuadruple(player)) {
            xp *= 4;
        }
        else if (Permissions.xpTriple(player)) {
            xp *= 3;
        }
        else if (Permissions.xpDoubleAndOneHalf(player)) {
            xp *= 2.5;
        }
        else if (Permissions.xpDouble(player)) {
            xp *= 2;
        }
        else if (Permissions.xpOneAndOneHalf(player)) {
            xp *= 1.5;
        }

        mcMMO.p.getServer().getPluginManager().callEvent(new McMMOPlayerXpGainEvent(player, skillType, xp));
        profile.setSkillXpLevel(skillType, profile.getSkillXpLevel(skillType) + xp);

        SpoutHud spoutHud = profile.getSpoutHud();

        if (spoutHud != null) {
            spoutHud.setLastGained(skillType);
        }

        SkillTools.xpCheckSkill(skillType, player, profile);
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

    public void setInvite(Party invite) {
        this.invite = invite;
    }

    public Party getInvite() {
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

    public void removeInvite() {
        invite = null;
    }
}
