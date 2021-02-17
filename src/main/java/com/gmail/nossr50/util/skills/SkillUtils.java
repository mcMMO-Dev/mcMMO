package com.gmail.nossr50.util.skills;

import com.gmail.nossr50.config.AdvancedConfig;
import com.gmail.nossr50.config.Config;
import com.gmail.nossr50.config.HiddenConfig;
import com.gmail.nossr50.datatypes.experience.XPGainReason;
import com.gmail.nossr50.datatypes.experience.XPGainSource;
import com.gmail.nossr50.datatypes.interactions.NotificationType;
import com.gmail.nossr50.datatypes.player.McMMOPlayer;
import com.gmail.nossr50.datatypes.skills.PrimarySkillType;
import com.gmail.nossr50.datatypes.skills.SubSkillType;
import com.gmail.nossr50.datatypes.skills.SuperAbilityType;
import com.gmail.nossr50.locale.LocaleLoader;
import com.gmail.nossr50.mcMMO;
import com.gmail.nossr50.util.ItemUtils;
import com.gmail.nossr50.util.Misc;
import com.gmail.nossr50.util.Permissions;
import com.gmail.nossr50.util.compat.layers.persistentdata.AbstractPersistentDataLayer;
import com.gmail.nossr50.util.player.NotificationManager;
import com.gmail.nossr50.util.player.UserManager;
import com.gmail.nossr50.util.random.*;
import com.gmail.nossr50.util.text.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.Recipe;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.inventory.ShapelessRecipe;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Iterator;

public final class SkillUtils {
    /**
     * This is a static utility class, therefore we don't want any instances of
     * this class. Making the constructor private prevents accidents like that.
     */
    private SkillUtils() {}

    public static void applyXpGain(McMMOPlayer mcMMOPlayer, PrimarySkillType skill, float xp, XPGainReason xpGainReason) {
        mcMMOPlayer.beginXpGain(skill, xp, xpGainReason, XPGainSource.SELF);
    }

    public static void applyXpGain(McMMOPlayer mcMMOPlayer, PrimarySkillType skill, float xp, XPGainReason xpGainReason, XPGainSource xpGainSource) {
        mcMMOPlayer.beginXpGain(skill, xp, xpGainReason, xpGainSource);
    }

    /*
     * Skill Stat Calculations
     */

    public static String[] calculateLengthDisplayValues(Player player, float skillValue, PrimarySkillType skill) {
        int maxLength = skill.getAbility().getMaxLength();
        int abilityLengthVar = AdvancedConfig.getInstance().getAbilityLength();
        int abilityLengthCap = AdvancedConfig.getInstance().getAbilityLengthCap();

        int length;

        if(abilityLengthCap > 0)
        {
            length =     (int) Math.min(abilityLengthCap, 2 + (skillValue / abilityLengthVar));
        } else {
            length = 2 + (int) (skillValue / abilityLengthVar);
        }

        int enduranceLength = PerksUtils.handleActivationPerks(player, length, maxLength);

        if (maxLength != 0) {
            length = Math.min(length, maxLength);
        }

        return new String[] { String.valueOf(length), String.valueOf(enduranceLength) };
    }

    /*
     * Others
     */

    public static int handleFoodSkills(Player player, int eventFoodLevel, SubSkillType subSkillType) {
        int curRank = RankUtils.getRank(player, subSkillType);

        int currentFoodLevel = player.getFoodLevel();
        int foodChange = eventFoodLevel - currentFoodLevel;

        foodChange+=curRank;

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
        return Config.getInstance().getLocale().equalsIgnoreCase("en_US") ? PrimarySkillType.getSkill(skillName) != null : isLocalizedSkill(skillName);
    }

    public static void sendSkillMessage(Player player, NotificationType notificationType, String key) {
        Location location = player.getLocation();

        for (Player otherPlayer : player.getWorld().getPlayers()) {
            if (otherPlayer != player && Misc.isNear(location, otherPlayer.getLocation(), Misc.SKILL_MESSAGE_MAX_SENDING_DISTANCE)) {
                NotificationManager.sendNearbyPlayersInformation(otherPlayer, notificationType, key, player.getName());
            }
        }
    }

    public static void handleAbilitySpeedIncrease(Player player) {
        if (HiddenConfig.getInstance().useEnchantmentBuffs()) {
            ItemStack heldItem = player.getInventory().getItemInMainHand();

            if(heldItem == null)
                return;

            if (!ItemUtils.canBeSuperAbilityDigBoosted(heldItem)) {
                return;
            }

            int originalDigSpeed = heldItem.getEnchantmentLevel(Enchantment.DIG_SPEED);

            //Add dig speed

            //Lore no longer gets added, no point to it afaik
            //ItemUtils.addAbilityLore(heldItem); //lore can be a secondary failsafe for 1.13 and below
            ItemUtils.addDigSpeedToItem(heldItem, heldItem.getEnchantmentLevel(Enchantment.DIG_SPEED));

            //1.13.2+ will have persistent metadata for this item
            AbstractPersistentDataLayer compatLayer = mcMMO.getCompatibilityManager().getPersistentDataLayer();
            compatLayer.setSuperAbilityBoostedItem(heldItem, originalDigSpeed);
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

            //Not Loaded
            if(mcMMOPlayer == null)
                return;

            PrimarySkillType skill = mcMMOPlayer.getAbilityMode(SuperAbilityType.SUPER_BREAKER) ? PrimarySkillType.MINING : PrimarySkillType.EXCAVATION;

            int abilityLengthVar = AdvancedConfig.getInstance().getAbilityLength();
            int abilityLengthCap = AdvancedConfig.getInstance().getAbilityLengthCap();

            int ticks;

            if(abilityLengthCap > 0)
            {
                ticks = PerksUtils.handleActivationPerks(player,  Math.min(abilityLengthCap, 2 + (mcMMOPlayer.getSkillLevel(skill) / abilityLengthVar)),
                        skill.getAbility().getMaxLength()) * Misc.TICK_CONVERSION_FACTOR;
            } else {
                ticks = PerksUtils.handleActivationPerks(player, 2 + ((mcMMOPlayer.getSkillLevel(skill)) / abilityLengthVar),
                        skill.getAbility().getMaxLength()) * Misc.TICK_CONVERSION_FACTOR;
            }

            PotionEffect abilityBuff = new PotionEffect(PotionEffectType.FAST_DIGGING, duration + ticks, amplifier + 10);
            player.addPotionEffect(abilityBuff, true);
        }
    }

    public static void removeAbilityBoostsFromInventory(@NotNull Player player) {
        for (ItemStack itemStack : player.getInventory().getContents()) {
            removeAbilityBuff(itemStack);
        }
    }

    public static void removeAbilityBuff(@Nullable ItemStack itemStack) {
        if(itemStack == null)
            return;

        if(!ItemUtils.canBeSuperAbilityDigBoosted(itemStack))
            return;


        //1.13.2+ will have persistent metadata for this itemStack
        AbstractPersistentDataLayer compatLayer = mcMMO.getCompatibilityManager().getPersistentDataLayer();

        if(compatLayer.isLegacyAbilityTool(itemStack)) {
            ItemMeta itemMeta = itemStack.getItemMeta();

            // This is safe to call without prior checks.
            itemMeta.removeEnchant(Enchantment.DIG_SPEED);

            itemStack.setItemMeta(itemMeta);
            ItemUtils.removeAbilityLore(itemStack);
        }

        if(compatLayer.isSuperAbilityBoosted(itemStack)) {
            compatLayer.removeBonusDigSpeedOnSuperAbilityTool(itemStack);
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
    public static void handleDurabilityChange(ItemStack itemStack, double durabilityModifier, double maxDamageModifier) {
        if(itemStack.getItemMeta() != null && itemStack.getItemMeta().isUnbreakable()) {
            return;
        }

        Material type = itemStack.getType();
        short maxDurability = mcMMO.getRepairableManager().isRepairable(type) ? mcMMO.getRepairableManager().getRepairable(type).getMaximumDurability() : type.getMaxDurability();
        durabilityModifier = (int) Math.min(durabilityModifier / (itemStack.getEnchantmentLevel(Enchantment.DURABILITY) + 1), maxDurability * maxDamageModifier);

        itemStack.setDurability((short) Math.min(itemStack.getDurability() + durabilityModifier, maxDurability));
    }

    private static boolean isLocalizedSkill(String skillName) {
        for (PrimarySkillType skill : PrimarySkillType.values()) {
            if (skillName.equalsIgnoreCase(LocaleLoader.getString(StringUtils.getCapitalized(skill.toString()) + ".SkillName"))) {
                return true;
            }
        }

        return false;
    }

    @Nullable
    public static Material getRepairAndSalvageItem(@NotNull ItemStack inHand) {
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
            return Material.OAK_WOOD;
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
        return getRepairAndSalvageQuantities(item.getType(), getRepairAndSalvageItem(item));
    }

    public static int getRepairAndSalvageQuantities(Material itemMaterial, Material recipeMaterial) {
        int quantity = 0;

        if(mcMMO.getMaterialMapStore().isNetheriteTool(itemMaterial) || mcMMO.getMaterialMapStore().isNetheriteArmor(itemMaterial)) {
            //One netherite bar requires 4 netherite scraps
            return 4;
        }

        for(Iterator<? extends Recipe> recipeIterator = Bukkit.getServer().recipeIterator(); recipeIterator.hasNext();) {
            Recipe bukkitRecipe = recipeIterator.next();

            if(bukkitRecipe.getResult().getType() != itemMaterial)
                continue;

            if(bukkitRecipe instanceof ShapelessRecipe) {
                for (ItemStack ingredient : ((ShapelessRecipe) bukkitRecipe).getIngredientList()) {
                    if (ingredient != null
                            && (recipeMaterial == null || ingredient.getType() == recipeMaterial)
                            && (ingredient.getType() == recipeMaterial)) {
                        quantity += ingredient.getAmount();
                    }
                }
            } else if(bukkitRecipe instanceof ShapedRecipe) {
                for (ItemStack ingredient : ((ShapedRecipe) bukkitRecipe).getIngredientMap().values()) {
                    if (ingredient != null
                            && (recipeMaterial == null || ingredient.getType() == recipeMaterial)
                            && (ingredient.getType() == recipeMaterial)) {
                        quantity += ingredient.getAmount();
                    }
                }
            }
        }

        return quantity;
    }

    /**
     * This is one of several Skill RNG check methods
     * This helper method is for specific {@link SubSkillType}, which help mcMMO understand where the RNG values used in our calculations come from from this {@link SubSkillType}
     *
     * 1) Determine where the RNG values come from for the passed {@link SubSkillType}
     *  NOTE: In the config file, there are values which are static and which are more dynamic, this is currently a bit hardcoded and will need to be updated manually
     *
     * 2) Determine whether or not to use Lucky multiplier and influence the outcome
     *
     * 3) Creates a {@link Probability} and pipes it to {@link RandomChanceUtil} which processes the result and returns it
     *
     * @param subSkillType target subskill
     * @param player target player, can be null (null players are given odds equivalent to a player with no levels or luck)
     * @return true if the Skill RNG succeeds, false if it fails
     */
    public static boolean isSkillRNGSuccessful(@NotNull SubSkillType subSkillType, @Nullable Player player) {
        try {
            //Process probability
            Probability probability = getSubSkillProbability(subSkillType, player);
            //Player can be null
            boolean isLucky = player != null && Permissions.lucky(player, subSkillType.getParentSkill());

            if(isLucky) {
                return RandomChanceUtil.processProbability(probability, RandomChanceUtil.LUCKY_MODIFIER);
            } else {
                return RandomChanceUtil.processProbability(probability);
            }

        } catch (RuntimeException | InvalidStaticChance e) {
            e.printStackTrace();
        }

        return false;
    }

    /**
     * This is one of several Skill RNG check methods
     * This helper method is specific to static value RNG, which can be influenced by a player's Luck
     *
     * @param primarySkillType the related primary skill
     * @param player the target player, can be null (null players have the worst odds)
     * @param probabilityPercentage the probability of this player succeeding in "percentage" format (0-100 inclusive)
     * @return true if the RNG succeeds, false if it fails
     */
    public static boolean isStaticSkillRNGSuccessful(@NotNull PrimarySkillType primarySkillType, @Nullable Player player, double probabilityPercentage) {
        //Grab a probability converted from a "percentage" value
        Probability probability = ProbabilityFactory.ofPercentageValue(probabilityPercentage);

        return isStaticSkillRNGSuccessful(primarySkillType, player, probability);
    }

    /**
     * This is one of several Skill RNG check methods
     * This helper method is specific to static value RNG, which can be influenced by a player's Luck
     *
     * @param primarySkillType the related primary skill
     * @param player the target player, can be null (null players have the worst odds)
     * @param probability the probability of this player succeeding
     * @return true if the RNG succeeds, false if it fails
     */
    public static boolean isStaticSkillRNGSuccessful(@NotNull PrimarySkillType primarySkillType, @Nullable Player player, @NotNull Probability probability) {
        boolean isLucky = player != null && Permissions.lucky(player, primarySkillType);

        if(isLucky) {
            return RandomChanceUtil.processProbability(probability, RandomChanceUtil.LUCKY_MODIFIER);
        } else {
            return RandomChanceUtil.processProbability(probability);
        }
    }

    /**
     * Grab the {@link Probability} for a specific {@link SubSkillType} for a specific {@link Player}
     *
     * @param subSkillType target subskill
     * @param player target player
     * @return the Probability of this skill succeeding
     * @throws InvalidStaticChance when a skill that does not have a hard coded static chance and it is asked for
     * @throws RuntimeException
     */
    public static @NotNull Probability getSubSkillProbability(@NotNull SubSkillType subSkillType, @Nullable Player player) throws InvalidStaticChance, RuntimeException {
        SkillProbabilityType skillProbabilityType = SkillProbabilityType.DYNAMIC_CONFIGURABLE;

        if(subSkillType == SubSkillType.TAMING_FAST_FOOD_SERVICE || subSkillType == SubSkillType.AXES_ARMOR_IMPACT || subSkillType == SubSkillType.AXES_GREATER_IMPACT)
            skillProbabilityType = SkillProbabilityType.STATIC_CONFIGURABLE;

        return ProbabilityFactory.ofSubSkill(player, subSkillType, skillProbabilityType);
    }
}
