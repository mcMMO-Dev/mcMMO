package com.gmail.nossr50.skills;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.block.Block;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.getspout.spoutapi.SpoutManager;
import org.getspout.spoutapi.player.SpoutPlayer;

import com.gmail.nossr50.Leaderboard;
import com.gmail.nossr50.Users;
import com.gmail.nossr50.m;
import com.gmail.nossr50.mcPermissions;
import com.gmail.nossr50.config.LoadProperties;
import com.gmail.nossr50.spout.SpoutStuff;
import com.gmail.nossr50.datatypes.AbilityType;
import com.gmail.nossr50.datatypes.PlayerProfile;
import com.gmail.nossr50.datatypes.PlayerStat;
import com.gmail.nossr50.datatypes.SkillType;
import com.gmail.nossr50.datatypes.ToolType;
import com.gmail.nossr50.events.McMMOPlayerLevelUpEvent;
import com.gmail.nossr50.locale.mcLocale;

public class Skills {

    private final static int TIME_CONVERSION_FACTOR = 1000;
    private final static int MAX_DISTANCE_AWAY = 10;

    /**
     * Checks to see if the cooldown for an item or ability is expired.
     *
     * @param oldTime The time the ability or item was last used
     * @param cooldown The amount of time that must pass between uses
     * @return true if the cooldown is over, false otherwise
     */
    public static boolean cooldownOver(long oldTime, int cooldown){
        long currentTime = System.currentTimeMillis();

        if (currentTime - oldTime >= (cooldown * TIME_CONVERSION_FACTOR)) {
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
     * @param PP The profile of the player
     * @param curTime The current system time
     * @param ability The ability to watch cooldowns for
     */
    public static void watchCooldown(Player player, PlayerProfile PP, long curTime, AbilityType ability) {
        if (!ability.getInformed(PP) && curTime - (PP.getSkillDATS(ability) * TIME_CONVERSION_FACTOR) >= (ability.getCooldown() * TIME_CONVERSION_FACTOR)) {
            ability.setInformed(PP, true);
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
        if (LoadProperties.enableOnlyActivateWhenSneaking && !player.isSneaking()) {
            return;
        }

        PlayerProfile PP = Users.getProfile(player);
        AbilityType ability = skill.getAbility();
        ToolType tool = skill.getTool();
        ItemStack inHand = player.getItemInHand();

        /* Check if any abilities are active */
        if (!PP.getAbilityUse() || PP.getSuperBreakerMode() || PP.getSerratedStrikesMode() || PP.getTreeFellerMode() || PP.getGreenTerraMode() || PP.getBerserkMode() || PP.getGigaDrillBreakerMode()) {
            return;
        }

        /* Woodcutting & Axes need to be treated differently.
         * Basically the tool always needs to ready and we check to see if the cooldown is over when the user takes action
         */
        if (skill == SkillType.WOODCUTTING || skill == SkillType.AXES) {
            if (tool.inHand(inHand) && !tool.getToolMode(PP)) {
                if (LoadProperties.enableAbilityMessages) {
                    player.sendMessage(tool.getRaiseTool());
                }

                tool.setToolATS(PP, System.currentTimeMillis());
                tool.setToolMode(PP, true);
            }
        }
        else if (ability.getPermissions(player) && tool.inHand(inHand) && !tool.getToolMode(PP)) {
            if (!ability.getMode(PP) && !cooldownOver(PP.getSkillDATS(ability) * TIME_CONVERSION_FACTOR, ability.getCooldown())) {
                player.sendMessage(mcLocale.getString("Skills.TooTired") + ChatColor.YELLOW + " (" + calculateTimeLeft(PP.getSkillDATS(ability) * TIME_CONVERSION_FACTOR, ability.getCooldown()) + "s)");
                return;
            }

            if (LoadProperties.enableAbilityMessages) {
                player.sendMessage(tool.getRaiseTool());
            }

            tool.setToolATS(PP, System.currentTimeMillis());
            tool.setToolMode(PP, true);
        }
    }

    /**
     * Monitors various things relating to skill abilities.
     *
     * @param player The player using the skill
     * @param PP The profile of the player
     * @param curTime The current system time
     * @param skill The skill being monitored
     */
    public static void monitorSkill(Player player, PlayerProfile PP, long curTime, SkillType skill) {
        final int FOUR_SECONDS = 4000;

        ToolType tool = skill.getTool();
        AbilityType ability = skill.getAbility();

        if (tool.getToolMode(PP) && curTime - (tool.getToolATS(PP) * TIME_CONVERSION_FACTOR) >= FOUR_SECONDS) {
            tool.setToolMode(PP, false);
            player.sendMessage(tool.getLowerTool());
        }

        if (ability.getPermissions(player)) {
            if (ability.getMode(PP) && (PP.getSkillDATS(ability) * TIME_CONVERSION_FACTOR) <= curTime) {
                ability.setMode(PP, false);
                ability.setInformed(PP, false);
                player.sendMessage(ability.getAbilityOff());

                for (Player y : player.getWorld().getPlayers()) {
                    if (y != player && m.isNear(player.getLocation(), y.getLocation(), MAX_DISTANCE_AWAY)) {
                        y.sendMessage(ability.getAbilityPlayerOff(player));
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
    public static void ProcessLeaderboardUpdate(SkillType skillType, Player player) {
        PlayerProfile PP = Users.getProfile(player);
        PlayerStat ps = new PlayerStat();

        if (skillType != SkillType.ALL) {
            ps.statVal = PP.getSkillLevel(skillType);
        }
        else {
            ps.statVal = m.getPowerLevel(player, PP);
        }

        ps.name = player.getName();
        Leaderboard.updateLeaderboard(ps, skillType);
    }

    /**
     * Check the XP of a skill.
     *
     * @param skillType The skill to check
     * @param player The player whose skill to check
     */
    public static void XpCheckSkill(SkillType skillType, Player player) {
        PlayerProfile PP = Users.getProfile(player);

        if (PP.getSkillXpLevel(skillType) >= PP.getXpToLevel(skillType)) {
            int skillups = 0;
            
            while (PP.getSkillXpLevel(skillType) >= PP.getXpToLevel(skillType)) {
                if (skillType.getMaxLevel() >= PP.getSkillLevel(skillType) + 1) {
                    skillups++;
                    PP.removeXP(skillType, PP.getXpToLevel(skillType));
                    PP.skillUp(skillType, 1);

                    McMMOPlayerLevelUpEvent eventToFire = new McMMOPlayerLevelUpEvent(player, skillType);
                    Bukkit.getPluginManager().callEvent(eventToFire);
                }
                else {
                    PP.removeXP(skillType, PP.getXpToLevel(skillType));
                }
            }

            if (!LoadProperties.useMySQL) {
                ProcessLeaderboardUpdate(skillType, player);
                ProcessLeaderboardUpdate(SkillType.ALL, player);
            }

            String capitalized = m.getCapitalized(skillType.toString());

            /* Spout Stuff */
            if (LoadProperties.spoutEnabled && player instanceof SpoutPlayer) {
                SpoutPlayer sPlayer = SpoutManager.getPlayer(player);

                if (sPlayer.isSpoutCraftEnabled()) {
                    if (LoadProperties.xpbar) {
                        SpoutStuff.updateXpBar(sPlayer);
                    }

                    SpoutStuff.levelUpNotification(skillType, sPlayer);
                }
                else {
                    player.sendMessage(mcLocale.getString("Skills."+capitalized+"Up", new Object[] {String.valueOf(skillups), PP.getSkillLevel(skillType)}));
                }
            }
            else {
                player.sendMessage(mcLocale.getString("Skills."+capitalized+"Up", new Object[] {String.valueOf(skillups), PP.getSkillLevel(skillType)}));
            }
        }
    }

    /**
     * Check XP of all skills.
     *
     * @param player The player to check XP for.
     */
    public static void XpCheckAll(Player player) {
        for (SkillType x : SkillType.values()) {
            //Don't want to do anything with this one
            if (x == SkillType.ALL) {
                continue;
            }

            XpCheckSkill(x, player);
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
            if (x.toString().equals(skillName.toUpperCase()))
                return x;
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
     * Get the format string for 
     * @param skillname
     * @param level
     * @param XP
     * @param XPToLevel
     * @return
     */
    public static String getSkillStats(String skillname, Integer level, Integer XP, Integer XPToLevel) {
        //TODO: Ditch this function in favor of better locale setup.

        ChatColor parColor = ChatColor.DARK_AQUA;
        ChatColor xpColor = ChatColor.GRAY;
        ChatColor LvlColor = ChatColor.GREEN;
        ChatColor skillColor = ChatColor.YELLOW;

        return skillColor + skillname + LvlColor + level + parColor +" XP" + "(" + xpColor + XP + parColor + "/" + xpColor + XPToLevel + parColor + ")";
    }

    /**
     * Check if the player has any combat skill permissions.
     *
     * @param player The player to check permissions for
     * @return true if the player has combat skills, false otherwise
     */
    public static boolean hasCombatSkills(Player player) {
        if (mcPermissions.getInstance().axes(player)
                || mcPermissions.getInstance().archery(player)
                || mcPermissions.getInstance().swords(player)
                || mcPermissions.getInstance().taming(player)
                || mcPermissions.getInstance().unarmed(player)) {
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
        if (mcPermissions.getInstance().excavation(player)
                || mcPermissions.getInstance().fishing(player)
                || mcPermissions.getInstance().herbalism(player)
                || mcPermissions.getInstance().mining(player)
                || mcPermissions.getInstance().woodcutting(player)) {
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
        if (mcPermissions.getInstance().acrobatics(player) || mcPermissions.getInstance().repair(player)) {
            return true;
        }
        else {
            return false;
        }
    }

    /**
     * Handle tool durability loss from abilities.
     *
     * @param inhand The item to damage
     * @param durabilityLoss The durability to remove from the item
     */
    public static void abilityDurabilityLoss(ItemStack inhand, int durabilityLoss) {
        if (LoadProperties.toolsLoseDurabilityFromAbilities) {
            if (!inhand.containsEnchantment(Enchantment.DURABILITY)) {
                inhand.setDurability((short) (inhand.getDurability() + durabilityLoss));
            }
        }
    }

    /**
     * Check to see if an ability can be activated.
     *
     * @param player The player activating the ability
     * @param type The skill the ability is based on
     */
    public static void abilityCheck(Player player, SkillType type) {
        PlayerProfile PP = Users.getProfile(player);
        AbilityType ability = type.getAbility();

        if (type.getTool().inHand(player.getItemInHand())) {
            if (type.getTool().getToolMode(PP)) {
                type.getTool().setToolMode(PP, false);
            }

            /* Axes and Woodcutting are odd because they share the same tool.
             * We show them the too tired message when they take action.
             */
            if (type == SkillType.WOODCUTTING || type == SkillType.AXES) {
                if (!ability.getMode(PP) && !cooldownOver(PP.getSkillDATS(ability) * TIME_CONVERSION_FACTOR, ability.getCooldown())) {
                    player.sendMessage(mcLocale.getString("Skills.TooTired") + ChatColor.YELLOW + " (" + calculateTimeLeft(PP.getSkillDATS(ability) * TIME_CONVERSION_FACTOR, ability.getCooldown()) + "s)");
                    return;
                }
            }

            int ticks = 2 + (PP.getSkillLevel(type) / 50);

            if (!ability.getMode(PP) && cooldownOver(PP.getSkillDATS(ability), ability.getCooldown())) {
                player.sendMessage(ability.getAbilityOn());

                for (Player y : player.getWorld().getPlayers()) {
                    if (y != player && m.isNear(player.getLocation(), y.getLocation(), MAX_DISTANCE_AWAY)) {
                        y.sendMessage(ability.getAbilityPlayer(player));
                    }
                }

                PP.setSkillDATS(ability, System.currentTimeMillis()+(ticks * TIME_CONVERSION_FACTOR));
                ability.setMode(PP, true);
            }
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
            if (!m.blockBreakSimulate(block, player, true)) {
                activate = false;
                break;
            }
            /* FALLS THROUGH */

        case GREEN_TERRA:
            if (!ability.blockCheck(block.getType())) {
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
}
