package com.gmail.nossr50.util.skills;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import com.gmail.nossr50.config.AdvancedConfig;
import com.gmail.nossr50.config.Config;
import com.gmail.nossr50.config.HiddenConfig;
import com.gmail.nossr50.datatypes.player.McMMOPlayer;
import com.gmail.nossr50.datatypes.player.PlayerProfile;
import com.gmail.nossr50.datatypes.skills.AbilityType;
import com.gmail.nossr50.datatypes.skills.SkillType;
import com.gmail.nossr50.locale.LocaleLoader;
import com.gmail.nossr50.util.ItemUtils;
import com.gmail.nossr50.util.Misc;
import com.gmail.nossr50.util.StringUtils;
import com.gmail.nossr50.util.player.UserManager;

public class SkillUtils {
    public static int handleFoodSkills(Player player, SkillType skill, int eventFoodLevel, int baseLevel, int maxLevel, int rankChange) {
        int skillLevel = UserManager.getPlayer(player).getProfile().getSkillLevel(skill);

        int currentFoodLevel = player.getFoodLevel();
        int foodChange = eventFoodLevel - currentFoodLevel;

        for (int i = baseLevel; i <= maxLevel; i += rankChange) {
            if (skillLevel >= i) {
                foodChange++;
            }
        }

        return currentFoodLevel + foodChange;
    }

    /**
     * Calculate the time remaining until the cooldown expires.
     *
     * @param deactivatedTimeStamp Time of deactivation
     * @param cooldown The length of the cooldown
     * @param player The Player to check for cooldown perks
     *
     * @return the number of seconds remaining before the cooldown expires
     */
    public static int calculateTimeLeft(long deactivatedTimeStamp, int cooldown, Player player) {
        return (int) (((deactivatedTimeStamp + (PerksUtils.handleCooldownPerks(player, cooldown) * Misc.TIME_CONVERSION_FACTOR)) - System.currentTimeMillis()) / Misc.TIME_CONVERSION_FACTOR);
    }

    /**
     * Calculate the time remaining until the ability's cooldown expires.
     *
     * @param ability AbilityType whose cooldown to check
     * @param profile The PlayerProfile to get the cooldown from
     * @param player The Player to check for cooldown perks
     *
     * @return the number of seconds remaining before the cooldown expires
     */
    public static int calculateTimeLeft(AbilityType ability, PlayerProfile profile, Player player) {
        return calculateTimeLeft(profile.getSkillDATS(ability) * Misc.TIME_CONVERSION_FACTOR, ability.getCooldown(), player);
    }

    /**
     * Check if the cooldown has expired.
     * This does NOT account for cooldown perks!
     *
     * @param deactivatedTimeStamp Time of deactivation in seconds
     * @param cooldown The length of the cooldown in seconds
     *
     * @return true if the cooldown is expired
     */
    public static boolean cooldownExpired(long deactivatedTimeStamp, int cooldown) {
        return System.currentTimeMillis() >= (deactivatedTimeStamp + cooldown) * Misc.TIME_CONVERSION_FACTOR;
    }

    /**
     * Checks if the given string represents a valid skill
     *
     * @param skillName The name of the skill to check
     * @return true if this is a valid skill, false otherwise
     */
    public static boolean isSkill(String skillName) {
        return Config.getInstance().getLocale().equalsIgnoreCase("en_US") ? SkillType.getSkill(skillName) != null : isLocalizedSkill(skillName);
    }

    public static void sendSkillMessage(Player player, String message) {
        Location location = player.getLocation();

        for (Player otherPlayer : player.getWorld().getPlayers()) {
            if (otherPlayer != player && Misc.isNear(location, otherPlayer.getLocation(), Misc.SKILL_MESSAGE_MAX_SENDING_DISTANCE)) {
                otherPlayer.sendMessage(message);
            }
        }
    }

    public static void handleAbilitySpeedIncrease(Player player) {
        if (HiddenConfig.getInstance().useEnchantmentBuffs()) {
            ItemStack heldItem = player.getItemInHand();

            if (heldItem == null || heldItem.getType() == Material.AIR) {
                return;
            }

            int efficiencyLevel = heldItem.getEnchantmentLevel(Enchantment.DIG_SPEED);
            ItemMeta itemMeta = heldItem.getItemMeta();
            List<String> itemLore = new ArrayList<String>();

            if (itemMeta.hasLore()) {
                itemLore = itemMeta.getLore();
            }

            itemLore.add("mcMMO Ability Tool");
            itemMeta.addEnchant(Enchantment.DIG_SPEED, efficiencyLevel + AdvancedConfig.getInstance().getEnchantBuff(), true);

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

            McMMOPlayer mcMMOPlayer = UserManager.getPlayer(player);
            SkillType skill = mcMMOPlayer.getAbilityMode(AbilityType.SUPER_BREAKER) ? SkillType.MINING : SkillType.EXCAVATION;
            int ticks = PerksUtils.handleActivationPerks(player, 2 + (mcMMOPlayer.getProfile().getSkillLevel(skill) / AdvancedConfig.getInstance().getAbilityLength()), skill.getAbility().getMaxLength()) * Misc.TICK_CONVERSION_FACTOR;

            PotionEffect abilityBuff = new PotionEffect(PotionEffectType.FAST_DIGGING, duration + ticks, amplifier + 10);
            player.addPotionEffect(abilityBuff, true);
        }
    }

    public static void handleAbilitySpeedDecrease(Player player) {
        if (!HiddenConfig.getInstance().useEnchantmentBuffs()) {
            return;
        }

        for (ItemStack item : player.getInventory().getContents()) {
            removeAbilityBuff(item);
        }
    }

    public static void removeAbilityBuff(ItemStack item) {
        if (item == null || item.getType() == Material.AIR || (!ItemUtils.isPickaxe(item) && !ItemUtils.isShovel(item)) || !item.containsEnchantment(Enchantment.DIG_SPEED)) {
            return;
        }

        ItemMeta itemMeta = item.getItemMeta();

        if (itemMeta.hasLore()) {
            List<String> itemLore = itemMeta.getLore();

            if (itemLore.remove("mcMMO Ability Tool")) {
                int efficiencyLevel = item.getEnchantmentLevel(Enchantment.DIG_SPEED);

                if (efficiencyLevel <= AdvancedConfig.getInstance().getEnchantBuff()) {
                    itemMeta.removeEnchant(Enchantment.DIG_SPEED);
                }
                else {
                    itemMeta.addEnchant(Enchantment.DIG_SPEED, efficiencyLevel - AdvancedConfig.getInstance().getEnchantBuff(), true);
                }

                itemMeta.setLore(itemLore);
                item.setItemMeta(itemMeta);
            }
        }
    }

    /**
     * Modify the durability of an ItemStack.
     *
     * @param itemStack The ItemStack which durability should be modified
     * @return the itemStack with modified durability
     */
    public static void handleDurabilityChange(ItemStack itemStack, int durabilityModifier) {
        itemStack.setDurability((short) Math.min(itemStack.getDurability() + durabilityModifier, itemStack.getType().getMaxDurability()));
    }

    public static boolean activationSuccessful(Player player, SkillType skill, double maxChance, int maxLevel) {
        return activationSuccessful(UserManager.getPlayer(player).getProfile().getSkillLevel(skill), PerksUtils.handleLuckyPerks(player, skill), maxChance, maxLevel);
    }

    public static boolean activationSuccessful(int skillLevel, int activationChance, double maxChance, int maxLevel) {
        return (maxChance / maxLevel) * Math.min(skillLevel, maxLevel) > Misc.getRandom().nextInt(activationChance);
    }

    public static boolean treasureDropSuccessful(double dropChance, int activationChance) {
        return dropChance > Misc.getRandom().nextDouble() * activationChance;
    }

    private static boolean isLocalizedSkill(String skillName) {
        for (SkillType skill : SkillType.values()) {
            if (skillName.equalsIgnoreCase(LocaleLoader.getString(StringUtils.getCapitalized(skill.toString()) + ".SkillName"))) {
                return true;
            }
        }

        return false;
    }
}
