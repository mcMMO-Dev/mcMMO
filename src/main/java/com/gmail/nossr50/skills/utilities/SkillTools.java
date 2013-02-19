package com.gmail.nossr50.skills.utilities;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Effect;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.PluginManager;
import org.bukkit.potion.Potion;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.potion.PotionType;
import org.getspout.spoutapi.SpoutManager;
import org.getspout.spoutapi.player.SpoutPlayer;

import com.gmail.nossr50.mcMMO;
import com.gmail.nossr50.config.AdvancedConfig;
import com.gmail.nossr50.config.Config;
import com.gmail.nossr50.config.HiddenConfig;
import com.gmail.nossr50.datatypes.PlayerProfile;
import com.gmail.nossr50.events.experience.McMMOPlayerLevelUpEvent;
import com.gmail.nossr50.events.fake.FakeBlockBreakEvent;
import com.gmail.nossr50.events.fake.FakeBlockDamageEvent;
import com.gmail.nossr50.events.fake.FakePlayerAnimationEvent;
import com.gmail.nossr50.locale.LocaleLoader;
import com.gmail.nossr50.mods.ModChecks;
import com.gmail.nossr50.spout.SpoutConfig;
import com.gmail.nossr50.spout.SpoutTools;
import com.gmail.nossr50.util.Misc;
import com.gmail.nossr50.util.Permissions;
import com.gmail.nossr50.util.StringUtils;
import com.gmail.nossr50.util.Users;

public class SkillTools {
    static AdvancedConfig advancedConfig = AdvancedConfig.getInstance();
    public static boolean abilitiesEnabled = Config.getInstance().getAbilitiesEnabled();
    public static int toolDurabilityLoss = Config.getInstance().getAbilityToolDamage();
    public static int abilityLengthIncreaseLevel = AdvancedConfig.getInstance().getAbilityLength();

    public static void handleFoodSkills(Player player, SkillType skill, FoodLevelChangeEvent event, int baseLevel, int maxLevel, int rankChange) {
        int skillLevel = Users.getPlayer(player).getProfile().getSkillLevel(skill);

        int currentFoodLevel = player.getFoodLevel();
        int newFoodLevel = event.getFoodLevel();
        int foodChange = newFoodLevel - currentFoodLevel;

        for (int i = baseLevel; i <= maxLevel; i+= rankChange) {
            if (skillLevel >= i) {
                foodChange++;
            }
        }

        event.setFoodLevel(currentFoodLevel + foodChange);
    }

    /**
     * Checks to see if the cooldown for an item or ability is expired.
     *
     * @param oldTime The time the ability or item was last used
     * @param cooldown The amount of time that must pass between uses
     * @param player The player whose cooldown is being checked
     * @return true if the cooldown is over, false otherwise
     */
    public static boolean cooldownOver(long oldTime, int cooldown, Player player) {
        long currentTime = System.currentTimeMillis();
        int adjustedCooldown = PerksUtils.handleCooldownPerks(player, cooldown);

        if (currentTime - oldTime >= (adjustedCooldown * Misc.TIME_CONVERSION_FACTOR)) {
            return true;
        }

        return false;
    }

    /**
     * Calculate the time remaining until the cooldown expires.
     *
     * @param deactivatedTimeStamp Time of deactivation
     * @param cooldown The length of the cooldown
     * @return the number of seconds remaining before the cooldown expires
     */
    public static int calculateTimeLeft(long deactivatedTimeStamp, int cooldown, Player player) {
        return (int) (((deactivatedTimeStamp + (PerksUtils.handleCooldownPerks(player, cooldown) * Misc.TIME_CONVERSION_FACTOR)) - System.currentTimeMillis()) / Misc.TIME_CONVERSION_FACTOR);
    }

    /**
     * Sends a message to the player when the cooldown expires.
     *
     * @param player The player to send a message to
     * @param profile The profile of the player
     * @param ability The ability to watch cooldowns for
     */
    public static void watchCooldown(Player player, PlayerProfile profile, AbilityType ability) {
        if (player == null || profile == null || ability == null)
            return;

        if (!profile.getAbilityInformed(ability) && cooldownOver(profile.getSkillDATS(ability) * Misc.TIME_CONVERSION_FACTOR, ability.getCooldown(), player)) {
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

        PlayerProfile profile = Users.getPlayer(player).getProfile();
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
                if (!profile.getAbilityMode(ability) && !cooldownOver(profile.getSkillDATS(ability) * Misc.TIME_CONVERSION_FACTOR, ability.getCooldown(), player)) {
                    player.sendMessage(LocaleLoader.getString("Skills.TooTired", calculateTimeLeft(profile.getSkillDATS(ability) * Misc.TIME_CONVERSION_FACTOR, ability.getCooldown(), player)));
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

        if (profile.getToolPreparationMode(tool) && curTime - (profile.getToolPreparationATS(tool) * Misc.TIME_CONVERSION_FACTOR) >= FOUR_SECONDS) {
            profile.setToolPreparationMode(tool, false);

            if (Config.getInstance().getAbilityMessagesEnabled()) {
                player.sendMessage(tool.getLowerTool());
            }
        }

        if (ability.getPermissions(player)) {
            if (profile.getAbilityMode(ability) && (profile.getSkillDATS(ability) * Misc.TIME_CONVERSION_FACTOR) <= curTime) {
                if (ability == AbilityType.BERSERK) {
                    player.setCanPickupItems(true);
                }
                else if (ability == AbilityType.SUPER_BREAKER || ability == AbilityType.GIGA_DRILL_BREAKER) {
                    handleAbilitySpeedDecrease(player);
                }

                profile.setAbilityMode(ability, false);
                profile.setAbilityInformed(ability, false);
                player.playEffect(player.getEyeLocation(), Effect.POTION_BREAK, new Potion(PotionType.STRENGTH));
                player.sendMessage(ability.getAbilityOff());

                sendSkillMessage(player, ability.getAbilityPlayerOff(player));
            }
        }
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
        int xpRemoved = 0;

        if (profile.getSkillXpLevel(skillType) >= profile.getXpToLevel(skillType)) {

            while (profile.getSkillXpLevel(skillType) >= profile.getXpToLevel(skillType)) {
                if ((skillType.getMaxLevel() >= profile.getSkillLevel(skillType) + 1) && (Config.getInstance().getPowerLevelCap() >= Users.getPlayer(player).getPowerLevel() + 1)) {
                    int xp = profile.getXpToLevel(skillType);
                    xpRemoved += xp;

                    profile.removeXp(skillType, xp);
                    skillups++;
                    profile.skillUp(skillType, 1);
                }
                else {
                    profile.addLevels(skillType, 0);
                }
            }

            McMMOPlayerLevelUpEvent eventToFire = new McMMOPlayerLevelUpEvent(player, skillType, skillups);
            mcMMO.p.getServer().getPluginManager().callEvent(eventToFire);

            if (eventToFire.isCancelled()) {
                profile.modifySkill(skillType, profile.getSkillLevel(skillType) - skillups);
                profile.setSkillXpLevel(skillType, profile.getSkillXpLevel(skillType) + xpRemoved);
                return;
            }

            String capitalized = StringUtils.getCapitalized(skillType.toString());

            /* Spout Stuff */
            if (mcMMO.spoutEnabled) {
                SpoutPlayer spoutPlayer = SpoutManager.getPlayer(player);

                if (spoutPlayer != null && spoutPlayer.isSpoutCraftEnabled()) {
                    SpoutTools.levelUpNotification(skillType, spoutPlayer);

                    /* Update custom titles */
                    if (SpoutConfig.getInstance().getShowPowerLevel()) {
                        spoutPlayer.setTitle(LocaleLoader.getString("Spout.Title", spoutPlayer.getName(), Users.getPlayer(player).getPowerLevel()));
                    }
                }
                else {
                    player.sendMessage(LocaleLoader.getString(capitalized + ".Skillup", skillups, profile.getSkillLevel(skillType)));
                }
            }
            else {
                player.sendMessage(LocaleLoader.getString(capitalized + ".Skillup", skillups, profile.getSkillLevel(skillType)));
            }
        }

        if (mcMMO.spoutEnabled) {
            SpoutPlayer spoutPlayer = SpoutManager.getPlayer(player);

            if (spoutPlayer != null && spoutPlayer.isSpoutCraftEnabled()) {
                if (SpoutConfig.getInstance().getXPBarEnabled()) {
                    profile.getSpoutHud().updateXpBar();
                }
            }
        }
    }

    /**
     * Checks if the given string represents a valid skill
     *
     * @param skillName The name of the skill to check
     * @return true if this is a valid skill, false otherwise
     */
    public static boolean isSkill(String skillName) {
        if (!Config.getInstance().getLocale().equalsIgnoreCase("en_US")) {
            return isLocalizedSkill(skillName);
        }

        if (SkillType.getSkill(skillName) != null) {
            return true;
        }

        return false;
    }

    private static boolean isLocalizedSkill(String skillName) {
        for (SkillType skill : SkillType.values()) {
            if (skillName.equalsIgnoreCase(LocaleLoader.getString(StringUtils.getCapitalized(skill.toString()) + ".SkillName"))) {
                return true;
            }
        }

        return false;
    }

    public static String getSkillName(SkillType skill) {
        if (!Config.getInstance().getLocale().equalsIgnoreCase("en_US")) {
            return StringUtils.getCapitalized(LocaleLoader.getString(StringUtils.getCapitalized(skill.toString()) + ".SkillName"));
        }

        return StringUtils.getCapitalized(skill.toString());
    }

    /**
     * Check if the player has any combat skill permissions.
     *
     * @param player The player to check permissions for
     * @return true if the player has combat skills, false otherwise
     */
    public static boolean hasCombatSkills(Player player) {
        if (Permissions.skillEnabled(player, SkillType.AXES)
                || Permissions.skillEnabled(player, SkillType.ARCHERY)
                || Permissions.skillEnabled(player, SkillType.SWORDS)
                || Permissions.skillEnabled(player, SkillType.TAMING)
                || Permissions.skillEnabled(player, SkillType.UNARMED)) {
            return true;
        }

        return false;
    }

    /**
     * Check if the player has any gathering skill permissions.
     *
     * @param player The player to check permissions for
     * @return true if the player has gathering skills, false otherwise
     */
    public static boolean hasGatheringSkills(Player player) {
        if (Permissions.skillEnabled(player, SkillType.EXCAVATION)
                || Permissions.skillEnabled(player, SkillType.FISHING)
                || Permissions.skillEnabled(player, SkillType.HERBALISM)
                || Permissions.skillEnabled(player, SkillType.MINING)
                || Permissions.skillEnabled(player, SkillType.WOODCUTTING)) {
            return true;
        }

        return false;
    }

    /**
     * Check if the player has any misc skill permissions.
     *
     * @param player The player to check permissions for
     * @return true if the player has misc skills, false otherwise
     */
    public static boolean hasMiscSkills(Player player) {
        if (Permissions.skillEnabled(player, SkillType.ACROBATICS)
                || Permissions.skillEnabled(player, SkillType.SMELTING)
                || Permissions.skillEnabled(player, SkillType.REPAIR)) {
            return true;
        }

        return false;
    }

    /**
     * Check to see if an ability can be activated.
     *
     * @param player The player activating the ability
     * @param type The skill the ability is based on
     */
    public static void abilityCheck(Player player, SkillType type) {
        PlayerProfile profile = Users.getPlayer(player).getProfile();
        ToolType tool = type.getTool();
        AbilityType ability = type.getAbility();

        profile.setToolPreparationMode(tool, false);

        /* Axes and Woodcutting are odd because they share the same tool.
         * We show them the too tired message when they take action.
         */
        if (type == SkillType.WOODCUTTING || type == SkillType.AXES) {
            if (!profile.getAbilityMode(ability) && !cooldownOver(profile.getSkillDATS(ability) * Misc.TIME_CONVERSION_FACTOR, ability.getCooldown(), player)) {
                player.sendMessage(LocaleLoader.getString("Skills.TooTired", calculateTimeLeft(profile.getSkillDATS(ability) * Misc.TIME_CONVERSION_FACTOR, ability.getCooldown(), player)));
                return;
            }
        }

        if (!profile.getAbilityMode(ability) && cooldownOver(profile.getSkillDATS(ability), ability.getCooldown(), player)) {
            int ticks = PerksUtils.handleActivationPerks(player, 2 + (profile.getSkillLevel(type) / abilityLengthIncreaseLevel), ability.getMaxTicks());

            player.playEffect(player.getEyeLocation(), Effect.POTION_BREAK, new Potion(PotionType.POISON));
            player.sendMessage(ability.getAbilityOn());
            SkillTools.sendSkillMessage(player, ability.getAbilityPlayer(player));

            profile.setSkillDATS(ability, System.currentTimeMillis() + (ticks * Misc.TIME_CONVERSION_FACTOR));
            profile.setAbilityMode(ability, true);

            if (ability == AbilityType.BERSERK) {
                player.setCanPickupItems(false);
            }
            else if (ability == AbilityType.SUPER_BREAKER || ability == AbilityType.GIGA_DRILL_BREAKER) {
                handleAbilitySpeedIncrease(player);
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

        switch (ability) {
        case BERSERK:
        case GIGA_DRILL_BREAKER:
        case SUPER_BREAKER:
        case LEAF_BLOWER:
            if (!ability.blockCheck(block)) {
                activate = false;
                break;
            }

            if (!blockBreakSimulate(block, player, true)) {
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

    public static void sendSkillMessage(Player player, String message) {
        for (Player otherPlayer : player.getWorld().getPlayers()) {
            if (otherPlayer != player && Misc.isNear(player.getLocation(), otherPlayer.getLocation(), Misc.SKILL_MESSAGE_MAX_SENDING_DISTANCE)) {
                otherPlayer.sendMessage(message);
            }
        }
    }

    /**
     * Check if a skill level is higher than the max bonus level of the ability.
     *
     * @param skillLevel Skill level to check
     * @param maxLevel Max level of the ability
     * @return whichever value is lower
     */
    public static int skillCheck(int skillLevel, int maxLevel) {
        //TODO: Could we just use Math.min(skillLevel, maxLevel) here?
        if (skillLevel > maxLevel) {
            return maxLevel;
        }
    
        return skillLevel;
    }

    public static void handleAbilitySpeedIncrease(Player player) {
        if (HiddenConfig.getInstance().useEnchantmentBuffs()) {
            ItemStack heldItem = player.getItemInHand();

            if (heldItem == null || heldItem.getType() == Material.AIR ) {
                return;
            }

            int efficiencyLevel = heldItem.getEnchantmentLevel(Enchantment.DIG_SPEED);
            ItemMeta itemMeta = heldItem.getItemMeta();
            List<String> itemLore = new ArrayList<String>();

            if (itemMeta.hasLore()) {
                itemLore = itemMeta.getLore();
            }

            itemLore.add("mcMMO Ability Tool");
            itemMeta.addEnchant(Enchantment.DIG_SPEED, efficiencyLevel + 5, true);

            itemMeta.setLore(itemLore);
            heldItem.setItemMeta(itemMeta);
        }
        else {
            int duration = 0;
            int amplifier = 0;

            if (player.hasPotionEffect(PotionEffectType.FAST_DIGGING)) {
                for (PotionEffect effect : player.getActivePotionEffects()) {
                    if (effect.getType() == PotionEffectType.FAST_DIGGING) {
                        duration = effect.getDuration();
                        amplifier = effect.getAmplifier();
                        break;
                    }
                }
            }

            PlayerProfile profile = Users.getPlayer(player).getProfile();
            int ticks = 0;

            if (profile.getAbilityMode(AbilityType.SUPER_BREAKER)) {
                ticks = ((int) (profile.getSkillDATS(AbilityType.SUPER_BREAKER) - System.currentTimeMillis())) / Misc.TIME_CONVERSION_FACTOR;
            }
            else if (profile.getAbilityMode(AbilityType.GIGA_DRILL_BREAKER)) {
                ticks = ((int) (profile.getSkillDATS(AbilityType.GIGA_DRILL_BREAKER) - System.currentTimeMillis())) / Misc.TIME_CONVERSION_FACTOR;
            }

            PotionEffect abilityBuff = new PotionEffect(PotionEffectType.FAST_DIGGING, duration + ticks, amplifier + 10);
            player.addPotionEffect(abilityBuff, true);
        }
    }

    public static void handleAbilitySpeedDecrease(Player player) {
        if (HiddenConfig.getInstance().useEnchantmentBuffs()) {
            PlayerInventory playerInventory = player.getInventory();

            for (int i = 0; i < playerInventory.getContents().length; i++) {
                ItemStack item = playerInventory.getItem(i);
                playerInventory.setItem(i, removeAbilityBuff(item));
            }
        }
        else {
            player.removePotionEffect(PotionEffectType.FAST_DIGGING);
        }
    }

    public static ItemStack removeAbilityBuff(ItemStack item) {
        if (item == null || item.getType() == Material.AIR ) {
            return item;
        }

        if (item.containsEnchantment(Enchantment.DIG_SPEED)) {
            ItemMeta itemMeta = item.getItemMeta();

            if (itemMeta.hasLore()) {
                List<String> itemLore = itemMeta.getLore();

                if (itemLore.remove("mcMMO Ability Tool")) {
                    int efficiencyLevel = item.getEnchantmentLevel(Enchantment.DIG_SPEED);

                    if (efficiencyLevel <= 5) {
                        itemMeta.removeEnchant(Enchantment.DIG_SPEED);
                    }
                    else {
                        itemMeta.addEnchant(Enchantment.DIG_SPEED, efficiencyLevel - 5, true);
                    }

                    itemMeta.setLore(itemLore);
                    item.setItemMeta(itemMeta);
                }
            }
        }

        return item;
    }

    /**
     * Simulate a block break event.
     *
     * @param block The block to break
     * @param player The player breaking the block
     * @param shouldArmSwing true if an armswing event should be fired, false otherwise
     * @return true if the event wasn't cancelled, false otherwise
     */
    public static boolean blockBreakSimulate(Block block, Player player, Boolean shouldArmSwing) {

        //Support for NoCheat
        if (shouldArmSwing) {
            FakePlayerAnimationEvent armswing = new FakePlayerAnimationEvent(player);
            mcMMO.p.getServer().getPluginManager().callEvent(armswing);
        }

        PluginManager pluginManger = mcMMO.p.getServer().getPluginManager();

        FakeBlockDamageEvent damageEvent = new FakeBlockDamageEvent(player, block, player.getItemInHand(), true);
        pluginManger.callEvent(damageEvent);

        FakeBlockBreakEvent breakEvent = new FakeBlockBreakEvent(block, player);
        pluginManger.callEvent(breakEvent);

        if (!damageEvent.isCancelled() && !breakEvent.isCancelled()) {
            return true;
        }

        return false;
    }
}
