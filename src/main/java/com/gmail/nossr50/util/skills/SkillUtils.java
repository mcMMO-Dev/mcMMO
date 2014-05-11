package com.gmail.nossr50.util.skills;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.Recipe;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.inventory.ShapelessRecipe;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.material.MaterialData;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import com.gmail.nossr50.mcMMO;
import com.gmail.nossr50.config.AdvancedConfig;
import com.gmail.nossr50.config.Config;
import com.gmail.nossr50.config.HiddenConfig;
import com.gmail.nossr50.datatypes.player.McMMOPlayer;
import com.gmail.nossr50.datatypes.skills.AbilityType;
import com.gmail.nossr50.datatypes.skills.SecondaryAbility;
import com.gmail.nossr50.datatypes.skills.SkillType;
import com.gmail.nossr50.events.skills.secondaryabilities.SecondaryAbilityEvent;
import com.gmail.nossr50.events.skills.secondaryabilities.SecondaryAbilityWeightedActivationCheckEvent;
import com.gmail.nossr50.locale.LocaleLoader;
import com.gmail.nossr50.util.EventUtils;
import com.gmail.nossr50.util.ItemUtils;
import com.gmail.nossr50.util.Misc;
import com.gmail.nossr50.util.StringUtils;
import com.gmail.nossr50.util.player.UserManager;

public class SkillUtils {
    public static int handleFoodSkills(Player player, SkillType skill, int eventFoodLevel, int baseLevel, int maxLevel, int rankChange) {
        int skillLevel = UserManager.getPlayer(player).getSkillLevel(skill);

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
            int ticks = PerksUtils.handleActivationPerks(player, 2 + (mcMMOPlayer.getSkillLevel(skill) / AdvancedConfig.getInstance().getAbilityLength()), skill.getAbility().getMaxLength()) * Misc.TICK_CONVERSION_FACTOR;

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

    public static void handleDurabilityChange(ItemStack itemStack, int durabilityModifier) {
        handleDurabilityChange(itemStack, durabilityModifier, 1.0);
    }

    /**
     * Modify the durability of an ItemStack.
     *
     * @param itemStack The ItemStack which durability should be modified
     * @param durabilityModifier the amount to modify the durability by
     * @param maxDamageModifier the amount to adjust the max damage by
     */
    public static void handleDurabilityChange(ItemStack itemStack, int durabilityModifier, double maxDamageModifier) {
        Material type = itemStack.getType();
        short maxDurability = mcMMO.getRepairableManager().isRepairable(type) ? mcMMO.getRepairableManager().getRepairable(type).getMaximumDurability() : type.getMaxDurability();
        durabilityModifier = (int) Math.min(durabilityModifier / (itemStack.getEnchantmentLevel(Enchantment.DURABILITY) + 1), maxDurability * maxDamageModifier);

        itemStack.setDurability((short) Math.min(itemStack.getDurability() + durabilityModifier, maxDurability));
    }

    public static boolean activationSuccessful(SecondaryAbility skillAbility, Player player, SkillType skill) {
        return activationSuccessful(skillAbility, player, UserManager.getPlayer(player).getSkillLevel(skill), PerksUtils.handleLuckyPerks(player, skill));
    }

    public static boolean activationSuccessful(SecondaryAbility skillAbility, Player player, int skillLevel, int activationChance) {
        return activationSuccessful(skillAbility, player, skillLevel, activationChance, AdvancedConfig.getInstance().getMaxChance(skillAbility), AdvancedConfig.getInstance().getMaxBonusLevel(skillAbility));
    }

    public static boolean activationSuccessful(SecondaryAbility skillAbility, Player player, int skillLevel, int activationChance, double maxChance, int maxLevel) {
        double chance = (maxChance / maxLevel) * Math.min(skillLevel, maxLevel) / activationChance;
        SecondaryAbilityWeightedActivationCheckEvent event = new SecondaryAbilityWeightedActivationCheckEvent(player, skillAbility, chance);
        mcMMO.p.getServer().getPluginManager().callEvent(event);
        return (event.getChance() * activationChance) > Misc.getRandom().nextInt(activationChance) && !event.isCancelled();
    }

    public static boolean activationSuccessful(SecondaryAbility skillAbility, Player player, double staticChance, int activationChance) {
        double chance = staticChance / activationChance;
        SecondaryAbilityWeightedActivationCheckEvent event = new SecondaryAbilityWeightedActivationCheckEvent(player, skillAbility, chance);
        mcMMO.p.getServer().getPluginManager().callEvent(event);
        return (event.getChance() * activationChance) > Misc.getRandom().nextInt(activationChance) && !event.isCancelled();
    }

    public static boolean activationSuccessful(SecondaryAbility skillAbility, Player player) {
        SecondaryAbilityEvent event = EventUtils.callSecondaryAbilityEvent(player, skillAbility);
        return !event.isCancelled();
    }

    public static boolean treasureDropSuccessful(Player player, double dropChance, int activationChance) {
        SecondaryAbilityWeightedActivationCheckEvent event = new SecondaryAbilityWeightedActivationCheckEvent(player, SecondaryAbility.EXCAVATION_TREASURE_HUNTER, dropChance / activationChance);
        mcMMO.p.getServer().getPluginManager().callEvent(event);
        return (event.getChance() * activationChance) > (Misc.getRandom().nextDouble() * activationChance) && !event.isCancelled();
    }

    private static boolean isLocalizedSkill(String skillName) {
        for (SkillType skill : SkillType.values()) {
            if (skillName.equalsIgnoreCase(LocaleLoader.getString(StringUtils.getCapitalized(skill.toString()) + ".SkillName"))) {
                return true;
            }
        }

        return false;
    }

    protected static Material getRepairAndSalvageItem(ItemStack inHand) {
        if (ItemUtils.isDiamondTool(inHand) || ItemUtils.isDiamondArmor(inHand)) {
            return Material.DIAMOND;
        }
        else if (ItemUtils.isGoldTool(inHand) || ItemUtils.isGoldArmor(inHand)) {
            return Material.GOLD_INGOT;
        }
        else if (ItemUtils.isIronTool(inHand) || ItemUtils.isIronArmor(inHand)) {
            return Material.IRON_INGOT;
        }
        else if (ItemUtils.isStoneTool(inHand)) {
            return Material.COBBLESTONE;
        }
        else if (ItemUtils.isWoodTool(inHand)) {
            return Material.WOOD;
        }
        else if (ItemUtils.isLeatherArmor(inHand)) {
            return Material.LEATHER;
        }
        else if (ItemUtils.isStringTool(inHand)) {
            return Material.STRING;
        }
        else {
            return null;
        }
    }

    public static int getRepairAndSalvageQuantities(ItemStack item) {
        return getRepairAndSalvageQuantities(item, getRepairAndSalvageItem(item), (byte) -1);
    }

    public static int getRepairAndSalvageQuantities(ItemStack item, Material repairMaterial, byte repairMetadata) {
        // Workaround for Bukkit bug where damaged items would not return any recipes
        item = item.clone();
        item.setDurability((short) 0);

        int quantity = 0;
        MaterialData repairData = repairMaterial != null ? new MaterialData(repairMaterial, repairMetadata) : null;
        List<Recipe> recipes = mcMMO.p.getServer().getRecipesFor(item);

        if (recipes.isEmpty()) {
            return quantity;
        }

        Recipe recipe = recipes.get(0);

        if (recipe instanceof ShapelessRecipe) {
            for (ItemStack ingredient : ((ShapelessRecipe) recipe).getIngredientList()) {
                if (ingredient != null && (repairMaterial == null || ingredient.getType() == repairMaterial) && (repairMetadata == -1 || ingredient.getData().equals(repairData))) {
                    quantity += ingredient.getAmount();
                }
            }
        }
        else if (recipe instanceof ShapedRecipe) {
            for (ItemStack ingredient : ((ShapedRecipe) recipe).getIngredientMap().values()) {
                if (ingredient != null && (repairMaterial == null || ingredient.getType() == repairMaterial) && (repairMetadata == -1 || ingredient.getData().equals(repairData))) {
                    quantity += ingredient.getAmount();
                }
            }
        }

        return quantity;
    }
}
