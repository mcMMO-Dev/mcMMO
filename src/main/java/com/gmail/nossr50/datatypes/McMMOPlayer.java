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
import com.gmail.nossr50.skills.SkillType;
import com.gmail.nossr50.spout.huds.SpoutHud;
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
            if (type.isChildSkill()) continue;
            if (type.getPermissions(player)) {
                powerLevel += profile.getSkillLevel(type);
            }
        }

        return powerLevel;
    }

    /**
     * Adds XP to the player, doesn't calculate for XP Rate
     *
     * @param skillType The skill to add XP to
     * @param newValue The amount of XP to add
     */
    public void addXPOverride(SkillType skillType, int xp) {
        if (skillType.equals(SkillType.ALL)) {
            for (SkillType type : SkillType.values()) {
                if (type.equals(SkillType.ALL) || type.isChildSkill()) {
                    continue;
                }

                mcMMO.p.getServer().getPluginManager().callEvent(new McMMOPlayerXpGainEvent(player, type, xp));
                profile.setSkillXPLevel(type, profile.getSkillXpLevel(type) + xp);
            }
        }
        else {
            mcMMO.p.getServer().getPluginManager().callEvent(new McMMOPlayerXpGainEvent(player, skillType, xp));
            profile.setSkillXPLevel(skillType, profile.getSkillXpLevel(skillType) + xp);

            SpoutHud spoutHud = profile.getSpoutHud();

            if (spoutHud != null) {
                spoutHud.setLastGained(skillType);
            }
        }
    }

    /**
     * Adds XP to the player, this ignores skill modifiers.
     *
     * @param skillType The skill to add XP to
     * @param newValue The amount of XP to add
     */
    public void addXPOverrideBonus(SkillType skillType, int xp) {
        int modifiedXp = (int)Math.floor(xp * Config.getInstance().getExperienceGainsGlobalMultiplier());
        addXPOverride(skillType, modifiedXp);
    }

    /**
     * Adds XP to the player, this is affected by skill modifiers and XP Rate and Permissions
     *
     * @param skillType The skill to add XP to
     * @param newvalue The amount of XP to add
     */
    public void addXP(SkillType skillType, int newValue) {
        if (player == null)
            return;
        else if (player.getGameMode() == null)
            return;

        if (player.getGameMode().equals(GameMode.CREATIVE)) {
            return;
        }

        int xp = (int)Math.floor((newValue / skillType.getXpModifier()) * Config.getInstance().getExperienceGainsGlobalMultiplier());

        if (Config.getInstance().getToolModsEnabled()) {
            ItemStack item = player.getItemInHand();
            CustomTool tool = ModChecks.getToolFromItemStack(item);

            if (tool != null) {
                xp = (int) (xp * tool.getXpMultiplier());
            }
        }

        if (Permissions.xpQuadruple(player)) {
            xp = xp * 4;
        }
        else if (Permissions.xpTriple(player)) {
            xp = xp * 3;
        }
        else if (Permissions.xpDoubleAndOneHalf(player)) {
            xp = (int) (xp * 2.5);
        }
        else if (Permissions.xpDouble(player)) {
            xp = xp * 2;
        }
        else if (Permissions.xpOneAndOneHalf(player)) {
            xp = (int) (xp * 1.5);
        }

        mcMMO.p.getServer().getPluginManager().callEvent(new McMMOPlayerXpGainEvent(player, skillType, xp));
        profile.setSkillXPLevel(skillType, profile.getSkillXpLevel(skillType) + xp);

        SpoutHud spoutHud = profile.getSpoutHud();

        if (spoutHud != null) {
            spoutHud.setLastGained(skillType);
        }
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
