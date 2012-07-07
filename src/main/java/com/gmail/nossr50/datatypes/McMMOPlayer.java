package com.gmail.nossr50.datatypes;

import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import com.gmail.nossr50.mcMMO;
import com.gmail.nossr50.config.Config;
import com.gmail.nossr50.datatypes.mods.CustomTool;
import com.gmail.nossr50.events.experience.McMMOPlayerXpGainEvent;
import com.gmail.nossr50.party.Party;
import com.gmail.nossr50.party.PartyManager;
import com.gmail.nossr50.util.Misc;
import com.gmail.nossr50.util.ModChecks;
import com.gmail.nossr50.util.Users;

public class McMMOPlayer {
    private Player player;
    private PlayerProfile profile;

    private Party party;
    private Party invite;

    public McMMOPlayer (Player player) {
        String playerName = player.getName();
        this.player = player;
        this.profile = new PlayerProfile(playerName, true);
        this.party = PartyManager.getInstance().getPlayerParty(playerName);
    }

    /**
     * Gets the power level of this player.
     *
     * @return the power level of the player
     */
    public int getPowerLevel() {
        int powerLevel = 0;

        for (SkillType type : SkillType.values()) {
            if (type.getPermissions(player)) {
                powerLevel += profile.getSkillLevel(type);
            }
        }

        return powerLevel;
    }

    /**
     * Calculate the party XP modifier.
     *
     * @param skillType Type of skill to check
     * @return the party bonus multiplier
     */
    private double calculatePartyXPModifier(SkillType skillType) {
        double bonusModifier = 0.0;

        for (Player member : party.getOnlineMembers()) {
            if (party.getLeader().equals(member.getName())) {
                if (Misc.isNear(player.getLocation(), member.getLocation(), 25.0)) {
                    PlayerProfile partyLeader = Users.getProfile(member);
                    int leaderSkill = partyLeader.getSkillLevel(skillType);
                    int playerSkill = profile.getSkillLevel(skillType);

                    if (leaderSkill >= playerSkill) {
                        int difference = leaderSkill - playerSkill;
                        bonusModifier = (difference * 0.75) / 100.0;
                    }
                }
            }
        }

        return bonusModifier;
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
                if (type.equals(SkillType.ALL)) {
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
        int modifiedXp = xp * Config.getInstance().xpGainMultiplier;
        addXPOverride(skillType, modifiedXp);
    }

    /**
     * Adds XP to the player, this is affected by skill modifiers and XP Rate and Permissions
     *
     * @param skillType The skill to add XP to
     * @param newvalue The amount of XP to add
     */
    public void addXP(SkillType skillType, int newValue) {
        if (player.getGameMode().equals(GameMode.CREATIVE)) {
            return;
        }

        double bonusModifier = 0;

        if (inParty()) {
            bonusModifier = calculatePartyXPModifier(skillType);
        }

        int xp = (int) (newValue / skillType.getXpModifier()) * Config.getInstance().xpGainMultiplier;

        if (bonusModifier > 0) {
            if (bonusModifier >= 2) {
                bonusModifier = 2;
            }

            double trueBonus = bonusModifier * xp;
            xp += trueBonus;
        }

        if (Config.getInstance().getToolModsEnabled()) {
            ItemStack item = player.getItemInHand();
            CustomTool tool = ModChecks.getToolFromItemStack(item);

            if (tool != null) {
                xp = (int) (xp * tool.getXpMultiplier());
            }
        }

        if (player.hasPermission("mcmmo.perks.xp.quadruple")) {
            xp = xp * 4;
        }
        else if (player.hasPermission("mcmmo.perks.xp.triple")) {
            xp = xp * 3;
        }
        else if (player.hasPermission("mcmmo.perks.xp.150percentboost")) {
            xp = (int) (xp * 2.5);
        }
        else if (player.hasPermission("mcmmo.perks.xp.double")) {
            xp = xp * 2;
        }
        else if (player.hasPermission("mcmmo.perks.xp.50percentboost")) {
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
        else {
            return false;
        }
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
        else {
            return false;
        }
    }

    public void removeParty() {
        party = null;
    }

    public void removeInvite() {
        invite = null;
    }
}
