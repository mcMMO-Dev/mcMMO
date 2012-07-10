package com.gmail.nossr50.util;

import java.util.Random;

import org.bukkit.ChatColor;
import org.bukkit.block.Block;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.getspout.spoutapi.SpoutManager;
import org.getspout.spoutapi.player.SpoutPlayer;

import com.gmail.nossr50.mcMMO;
import com.gmail.nossr50.config.Config;
import com.gmail.nossr50.config.SpoutConfig;
import com.gmail.nossr50.datatypes.AbilityType;
import com.gmail.nossr50.datatypes.McMMOPlayer;
import com.gmail.nossr50.datatypes.PlayerProfile;
import com.gmail.nossr50.datatypes.PlayerStat;
import com.gmail.nossr50.datatypes.SkillType;
import com.gmail.nossr50.datatypes.ToolType;
import com.gmail.nossr50.events.experience.McMMOPlayerLevelUpEvent;
import com.gmail.nossr50.locale.LocaleLoader;
import com.gmail.nossr50.spout.SpoutStuff;

public class Skills {

    private final static int TIME_CONVERSION_FACTOR = 1000;
    private final static double MAX_DISTANCE_AWAY = 10.0;

    private final static Random random = new Random();

    /**
     * Checks to see if the cooldown for an item or ability is expired.
     *
     * @param oldTime The time the ability or item was last used
     * @param cooldown The amount of time that must pass between uses
     * @param player The player whose cooldown is being checked
     * @return true if the cooldown is over, false otherwise
     */
    public static boolean cooldownOver(long oldTime, int cooldown, Player player){
        long currentTime = System.currentTimeMillis();
        int adjustedCooldown = cooldown;

        //Reduced Cooldown Donor Perks
        if (player.hasPermission("mcmmo.perks.cooldowns.halved")) {
            adjustedCooldown = (int) (adjustedCooldown * 0.5);
        }
        else if (player.hasPermission("mcmmo.perks.cooldowns.thirded")) {
            adjustedCooldown = (int) (adjustedCooldown * 0.66);
        }
        else if (player.hasPermission("mcmmo.perks.cooldowns.quartered")) {
            adjustedCooldown = (int) (adjustedCooldown * 0.75);
        }

        if (currentTime - oldTime >= (adjustedCooldown * TIME_CONVERSION_FACTOR)) {
            return true;
        }
        else {
            return false;
        }
    }

    /**
     * Calculate the time remaining until the cooldown expires.
     *
     * @param deactivatedTimeStamp Time of deactivation
     * @param cooldown The length of the cooldown
     * @return the number of seconds remaining before the cooldown expires
     */
    public static int calculateTimeLeft(long deactivatedTimeStamp, int cooldown) {
        return (int) (((deactivatedTimeStamp + (cooldown * TIME_CONVERSION_FACTOR)) - System.currentTimeMillis()) / TIME_CONVERSION_FACTOR);
    }

    /**
     * Sends a message to the player when the cooldown expires.
     *
     * @param player The player to send a message to
     * @param profile The profile of the player
     * @param ability The ability to watch cooldowns for
     */
    public static void watchCooldown(Player player, PlayerProfile profile, AbilityType ability) {
        if (!profile.getAbilityInformed(ability) && cooldownOver(profile.getSkillDATS(ability) * TIME_CONVERSION_FACTOR, ability.getCooldown(), player)) {
            profile.setAbilityInformed(ability, true);
            player.sendMessage(ability.getAbilityRefresh());
        }
    }

    /**
     * Process activating abilities & readying the tool.
     *
     * @param player The player using the ability
     * @param skill The skill the ability is tied to
     */
    public static void activationCheck(Player player, SkillType skill) {
        if (Config.getInstance().getAbilitiesOnlyActivateWhenSneaking() && !player.isSneaking()) {
            return;
        }

        PlayerProfile profile = Users.getProfile(player);
        AbilityType ability = skill.getAbility();
        ToolType tool = skill.getTool();
        ItemStack inHand = player.getItemInHand();

        if (ModChecks.isCustomTool(inHand) && !ModChecks.getToolFromItemStack(inHand).isAbilityEnabled()) {
            return;
        }

        /* Check if any abilities are active */
        if (profile == null) {
            return;
        }

        if (!profile.getAbilityUse()) {
            return;
        }

        for (AbilityType x : AbilityType.values()) {
            if (profile.getAbilityMode(x)) {
                return;
            }
        }

        /* Woodcutting & Axes need to be treated differently.
         * Basically the tool always needs to ready and we check to see if the cooldown is over when the user takes action
         */
        if (ability.getPermissions(player) && tool.inHand(inHand) && !profile.getToolPreparationMode(tool)) {
            if (skill != SkillType.WOODCUTTING && skill != SkillType.AXES) {
                if (!profile.getAbilityMode(ability) && !cooldownOver(profile.getSkillDATS(ability) * TIME_CONVERSION_FACTOR, ability.getCooldown(), player)) {
                    player.sendMessage(LocaleLoader.getString("Skills.TooTired") + ChatColor.YELLOW + " (" + calculateTimeLeft(profile.getSkillDATS(ability) * TIME_CONVERSION_FACTOR, ability.getCooldown()) + "s)");
                    return;
                }
            }

            if (Config.getInstance().getAbilityMessagesEnabled()) {
                player.sendMessage(tool.getRaiseTool());
            }

            profile.setToolPreparationATS(tool, System.currentTimeMillis());
            profile.setToolPreparationMode(tool, true);
        }
    }

    /**
     * Monitors various things relating to skill abilities.
     *
     * @param player The player using the skill
     * @param profile The profile of the player
     * @param curTime The current system time
     * @param skill The skill being monitored
     */
    public static void monitorSkill(Player player, PlayerProfile profile, long curTime, SkillType skill) {
        final int FOUR_SECONDS = 4000;

        ToolType tool = skill.getTool();
        AbilityType ability = skill.getAbility();

        if (profile == null) {
            return;
        }

        if (profile.getToolPreparationMode(tool) && curTime - (profile.getToolPreparationATS(tool) * TIME_CONVERSION_FACTOR) >= FOUR_SECONDS) {
            profile.setToolPreparationMode(tool, false);

            if (Config.getInstance().getAbilityMessagesEnabled()) {
                player.sendMessage(tool.getLowerTool());
            }
        }

        if (ability.getPermissions(player)) {
            if (profile.getAbilityMode(ability) && (profile.getSkillDATS(ability) * TIME_CONVERSION_FACTOR) <= curTime) {
                profile.setAbilityMode(ability, false);
                profile.setAbilityInformed(ability, false);
                player.sendMessage(ability.getAbilityOff());

                for (Player nearbyPlayer : player.getWorld().getPlayers()) {
                    if (nearbyPlayer != player && Misc.isNear(player.getLocation(), nearbyPlayer.getLocation(), MAX_DISTANCE_AWAY)) {
                        nearbyPlayer.sendMessage(ability.getAbilityPlayerOff(player));
                    }
                }
            }
        }
    }

    /**
     * Update the leaderboards.
     *
     * @param skillType The skill to update the leaderboards for
     * @param player The player whose skill to update
     */
    public static void processLeaderboardUpdate(SkillType skillType, Player player) {
        McMMOPlayer mcMMOPlayer = Users.getPlayer(player);
        PlayerProfile profile = mcMMOPlayer.getProfile();
        PlayerStat ps = new PlayerStat();

        if (skillType != SkillType.ALL) {
            ps.statVal = profile.getSkillLevel(skillType);
        }
        else {
            ps.statVal = mcMMOPlayer.getPowerLevel();
        }

        ps.name = player.getName();
        Leaderboard.updateLeaderboard(ps, skillType);
    }

    /**
     * Check the XP of a skill.
     *
     * @param skillType The skill to check
     * @param player The player whose skill to check
     * @param profile The profile of the player whose skill to check
     */
    public static void xpCheckSkill(SkillType skillType, Player player, PlayerProfile profile) {
        int skillups = 0;

        if (profile.getSkillXpLevel(skillType) >= profile.getXpToLevel(skillType)) {

            while (profile.getSkillXpLevel(skillType) >= profile.getXpToLevel(skillType)) {
                if ((skillType.getMaxLevel() >= profile.getSkillLevel(skillType) + 1) && (Misc.getPowerLevelCap() >= Users.getPlayer(player).getPowerLevel() + 1)) {
                    profile.removeXP(skillType, profile.getXpToLevel(skillType));
                    skillups++;
                    profile.skillUp(skillType, 1);

                    McMMOPlayerLevelUpEvent eventToFire = new McMMOPlayerLevelUpEvent(player, skillType);
                    mcMMO.p.getServer().getPluginManager().callEvent(eventToFire);
                }
                else {
                    profile.addLevels(skillType, 0);
                }
            }

            if (!Config.getInstance().getUseMySQL()) {
                processLeaderboardUpdate(skillType, player);
                processLeaderboardUpdate(SkillType.ALL, player);
            }

            String capitalized = Misc.getCapitalized(skillType.toString());

            /* Spout Stuff */
            if (mcMMO.spoutEnabled) {
                SpoutPlayer spoutPlayer = SpoutManager.getPlayer(player);

                if (spoutPlayer != null && spoutPlayer.isSpoutCraftEnabled()) {
                    SpoutStuff.levelUpNotification(skillType, spoutPlayer);

                    /* Update custom titles */
                    if (SpoutConfig.getInstance().getShowPowerLevel()) {
                        spoutPlayer.setTitle(spoutPlayer.getName()+ "\n" + ChatColor.YELLOW + "P" + ChatColor.GOLD + "lvl" + ChatColor.WHITE + "." + ChatColor.GREEN + String.valueOf(Users.getPlayer(player).getPowerLevel()));
                    }
                }
                else {
                    player.sendMessage(LocaleLoader.getString(capitalized + ".Skillup", new Object[] {String.valueOf(skillups), profile.getSkillLevel(skillType)}));
                }
            }
            else {
                player.sendMessage(LocaleLoader.getString(capitalized + ".Skillup", new Object[] {String.valueOf(skillups), profile.getSkillLevel(skillType)}));
            }
        }

        if (mcMMO.spoutEnabled) {
            SpoutPlayer spoutPlayer = (SpoutPlayer) player;

            if (spoutPlayer != null && spoutPlayer.isSpoutCraftEnabled()) {
                if (SpoutConfig.getInstance().getXPBarEnabled()) {
                    profile.getSpoutHud().updateXpBar();
                }
            }
        }
    }

    /**
     * Check XP of all skills.
     *
     * @param player The player to check XP for.
     * @param profile The profile of the player whose skill to check
     */
    public static void xpCheckAll(Player player, PlayerProfile profile) {
        for (SkillType skillType : SkillType.values()) {
            //Don't want to do anything with this one
            if (skillType == SkillType.ALL) {
                continue;
            }

            xpCheckSkill(skillType, player, profile);
        }
    }

    /**
     * Get the skill represented by the given string
     *
     * @param skillName The name of the skill
     * @return the SkillType if it exists, null otherwise
     */
    public static SkillType getSkillType(String skillName) {
        for (SkillType x : SkillType.values()) {
            if (x.toString().equals(skillName.toUpperCase())) {
                return x;
            }
        }

        return null;
    }

    /**
     * Checks if the given string represents a valid skill
     *
     * @param skillname The name of the skill to check
     * @return true if this is a valid skill, false otherwise
     */
    public static boolean isSkill(String skillName) {
        if (getSkillType(skillName) != null) {
            return true;
        }
        else {
            return false;
        }
    }

    /**
     * Check if the player has any combat skill permissions.
     *
     * @param player The player to check permissions for
     * @return true if the player has combat skills, false otherwise
     */
    public static boolean hasCombatSkills(Player player) {
        if (Permissions.getInstance().axes(player)
                || Permissions.getInstance().archery(player)
                || Permissions.getInstance().swords(player)
                || Permissions.getInstance().taming(player)
                || Permissions.getInstance().unarmed(player)) {
            return true;
        }
        else {
            return false;
        }
    }

    /**
     * Check if the player has any gathering skill permissions.
     *
     * @param player The player to check permissions for
     * @return true if the player has gathering skills, false otherwise
     */
    public static boolean hasGatheringSkills(Player player) {
        if (Permissions.getInstance().excavation(player)
                || Permissions.getInstance().fishing(player)
                || Permissions.getInstance().herbalism(player)
                || Permissions.getInstance().mining(player)
                || Permissions.getInstance().woodcutting(player)) {
            return true;
        }
        else {
            return false;
        }
    }

    /**
     * Check if the player has any misc skill permissions.
     *
     * @param player The player to check permissions for
     * @return true if the player has misc skills, false otherwise
     */
    public static boolean hasMiscSkills(Player player) {
        if (Permissions.getInstance().acrobatics(player) || Permissions.getInstance().repair(player)) {
            return true;
        }
        else {
            return false;
        }
    }

    /**
     * Handle tool durability loss from abilities.
     *
     * @param inHand The item to damage
     * @param durabilityLoss The durability to remove from the item
     */
    public static void abilityDurabilityLoss(ItemStack inHand, int durabilityLoss) {
        if (Config.getInstance().getAbilitiesDamageTools()) {
            if (inHand.containsEnchantment(Enchantment.DURABILITY)) {
                int level = inHand.getEnchantmentLevel(Enchantment.DURABILITY);
                if (random.nextInt(level + 1) > 0) {
                    return;
                }
            }
            inHand.setDurability((short) (inHand.getDurability() + durabilityLoss));
        }
    }

    /**
     * Check to see if an ability can be activated.
     *
     * @param player The player activating the ability
     * @param type The skill the ability is based on
     */
    public static void abilityCheck(Player player, SkillType type) {
        PlayerProfile profile = Users.getProfile(player);
        ToolType tool = type.getTool();

        if (!profile.getToolPreparationMode(tool)) {
            return;
        }

        profile.setToolPreparationMode(tool, false);

        AbilityType ability = type.getAbility();

        /* Axes and Woodcutting are odd because they share the same tool.
         * We show them the too tired message when they take action.
         */
        if (type == SkillType.WOODCUTTING || type == SkillType.AXES) {
            if (!profile.getAbilityMode(ability) && !cooldownOver(profile.getSkillDATS(ability) * TIME_CONVERSION_FACTOR, ability.getCooldown(), player)) {
                player.sendMessage(LocaleLoader.getString("Skills.TooTired") + ChatColor.YELLOW + " (" + calculateTimeLeft(profile.getSkillDATS(ability) * TIME_CONVERSION_FACTOR, ability.getCooldown()) + "s)");
                return;
            }
        }

        int ticks = 2 + (profile.getSkillLevel(type) / 50);

        if (player.hasPermission("mcmmo.perks.activationtime.twelveseconds")) {
            ticks = ticks + 12;
        }
        else if (player.hasPermission("mcmmo.perks.activationtime.eightseconds")) {
            ticks = ticks + 8;
        }
        else if (player.hasPermission("mcmmo.perks.activationtime.fourseconds")) {
            ticks = ticks + 4;
        }

        int maxTicks = ability.getMaxTicks();

        if (maxTicks != 0 && ticks > maxTicks) {
            ticks = maxTicks;
        }

        if (!profile.getAbilityMode(ability) && cooldownOver(profile.getSkillDATS(ability), ability.getCooldown(), player)) {
            player.sendMessage(ability.getAbilityOn());

            for (Player y : player.getWorld().getPlayers()) {
                if (y != player && Misc.isNear(player.getLocation(), y.getLocation(), MAX_DISTANCE_AWAY)) {
                    y.sendMessage(ability.getAbilityPlayer(player));
                }
            }

            profile.setSkillDATS(ability, System.currentTimeMillis() + (ticks * TIME_CONVERSION_FACTOR));
            profile.setAbilityMode(ability, true);
        }
    }

    /**
     * Check to see if ability should be triggered.
     *
     * @param player The player using the ability
     * @param block The block modified by the ability
     * @param ability The ability to check
     * @return true if the ability should activate, false otherwise
     */
    public static boolean triggerCheck(Player player, Block block, AbilityType ability) {
        boolean activate = true;

        if (!ability.getPermissions(player)) {
            activate = false;
            return activate;
        }

        switch (ability) {
        case BERSERK:
        case GIGA_DRILL_BREAKER:
        case SUPER_BREAKER:
        case LEAF_BLOWER:
            if (!ability.blockCheck(block)) {
                activate = false;
                break;
            }

            if (!Misc.blockBreakSimulate(block, player, true)) {
                activate = false;
                break;
            }
            break;

        case GREEN_TERRA:
            if (!ability.blockCheck(block)) {
                activate = false;
                break;
            }
            break;

        default:
            activate = false;
            break;
        }

        return activate;
    }

    /**
     * Handle the processing of XP gain from individual skills.
     *
     * @param player The player that gained XP
     * @param profile The profile of the player gaining XP
     * @param type The type of skill to gain XP from
     * @param xp the amount of XP to gain
     */
    public static void xpProcessing(Player player, PlayerProfile profile, SkillType type, int xp) {
        if (type.getPermissions(player)) {
            Users.getPlayer(player).addXP(type, xp);
            xpCheckSkill(type, player, profile);
        }
    }
}
