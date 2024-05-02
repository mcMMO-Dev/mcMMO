package com.gmail.nossr50.util.skills;

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
import com.gmail.nossr50.metadata.ItemMetadataService;
import com.gmail.nossr50.util.ItemUtils;
import com.gmail.nossr50.util.Misc;
import com.gmail.nossr50.util.Permissions;
import com.gmail.nossr50.util.player.NotificationManager;
import com.gmail.nossr50.util.player.UserManager;
import com.gmail.nossr50.util.text.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.Recipe;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.inventory.ShapelessRecipe;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.potion.PotionEffect;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Iterator;

import static com.gmail.nossr50.util.PotionEffectMapper.getHaste;

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
        int maxLength = mcMMO.p.getSkillTools().getSuperAbilityMaxLength(mcMMO.p.getSkillTools().getSuperAbility(skill));
        int abilityLengthVar = mcMMO.p.getAdvancedConfig().getAbilityLength();
        int abilityLengthCap = mcMMO.p.getAdvancedConfig().getAbilityLengthCap();

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
        return mcMMO.p.getGeneralConfig().getLocale().equalsIgnoreCase("en_US") ? mcMMO.p.getSkillTools().matchSkill(skillName) != null : isLocalizedSkill(skillName);
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

            int originalDigSpeed = heldItem.getEnchantmentLevel(mcMMO.p.getEnchantmentMapper().getEfficiency());
            ItemUtils.addDigSpeedToItem(heldItem, heldItem.getEnchantmentLevel(mcMMO.p.getEnchantmentMapper().getEfficiency()));

            //1.13.2+ will have persistent metadata for this item
            mcMMO.getMetadataService().getItemMetadataService().setSuperAbilityBoostedItem(heldItem, originalDigSpeed);
        } else {
            int duration = 0;
            int amplifier = 0;

            if (player.hasPotionEffect(getHaste())) {
                for (PotionEffect effect : player.getActivePotionEffects()) {
                    if (effect.getType() == getHaste()) {
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

            int abilityLengthVar = mcMMO.p.getAdvancedConfig().getAbilityLength();
            int abilityLengthCap = mcMMO.p.getAdvancedConfig().getAbilityLengthCap();

            int ticks;

            if(abilityLengthCap > 0)
            {
                ticks = PerksUtils.handleActivationPerks(player,  Math.min(abilityLengthCap, 2 + (mcMMOPlayer.getSkillLevel(skill) / abilityLengthVar)),
                        mcMMO.p.getSkillTools().getSuperAbilityMaxLength(mcMMO.p.getSkillTools().getSuperAbility(skill))) * Misc.TICK_CONVERSION_FACTOR;
            } else {
                ticks = PerksUtils.handleActivationPerks(player, 2 + ((mcMMOPlayer.getSkillLevel(skill)) / abilityLengthVar),
                        mcMMO.p.getSkillTools().getSuperAbilityMaxLength(mcMMO.p.getSkillTools().getSuperAbility(skill))) * Misc.TICK_CONVERSION_FACTOR;
            }

            PotionEffect abilityBuff = new PotionEffect(getHaste(), duration + ticks, amplifier + 10);
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
        ItemMetadataService itemMetadataService = mcMMO.getMetadataService().getItemMetadataService();

        if(itemMetadataService.isLegacyAbilityTool(itemStack)) {
            ItemMeta itemMeta = itemStack.getItemMeta();

            if(itemMeta != null) {
                // This is safe to call without prior checks.
                itemMeta.removeEnchant(mcMMO.p.getEnchantmentMapper().getEfficiency());

                itemStack.setItemMeta(itemMeta);
                ItemUtils.removeAbilityLore(itemStack);
            }
        }

        if(itemMetadataService.isSuperAbilityBoosted(itemStack)) {
            itemMetadataService.removeBonusDigSpeedOnSuperAbilityTool(itemStack);
        }
    }

    public static void handleDurabilityChange(ItemStack itemStack, int durabilityModifier) {
        handleDurabilityChange(itemStack, durabilityModifier, 1.0);
    }

    /**
     * Modify the durability of an ItemStack, using Tools specific formula for unbreaking enchant damage reduction
     *
     * @param itemStack The ItemStack which durability should be modified
     * @param durabilityModifier the amount to modify the durability by
     * @param maxDamageModifier the amount to adjust the max damage by
     */
    public static void handleDurabilityChange(ItemStack itemStack, double durabilityModifier, double maxDamageModifier) {
        if(itemStack.hasItemMeta() && itemStack.getItemMeta().isUnbreakable()) {
            return;
        }

        Material type = itemStack.getType();
        short maxDurability = mcMMO.getRepairableManager().isRepairable(type) ? mcMMO.getRepairableManager().getRepairable(type).getMaximumDurability() : type.getMaxDurability();
        durabilityModifier = (int) Math.min(durabilityModifier / (itemStack.getEnchantmentLevel(mcMMO.p.getEnchantmentMapper().getUnbreaking()) + 1), maxDurability * maxDamageModifier);

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


    /**
     * Modify the durability of an ItemStack, using Armor specific formula for unbreaking enchant damage reduction
     *
     * @param itemStack The ItemStack which durability should be modified
     * @param durabilityModifier the amount to modify the durability by
     * @param maxDamageModifier the amount to adjust the max damage by
     */
    public static void handleArmorDurabilityChange(ItemStack itemStack, double durabilityModifier, double maxDamageModifier) {
        if(itemStack.hasItemMeta() && itemStack.getItemMeta().isUnbreakable()) {
            return;
        }

        Material type = itemStack.getType();
        short maxDurability = mcMMO.getRepairableManager().isRepairable(type) ? mcMMO.getRepairableManager().getRepairable(type).getMaximumDurability() : type.getMaxDurability();
        durabilityModifier = (int) Math.min(durabilityModifier * (0.6 + 0.4/ (itemStack.getEnchantmentLevel(mcMMO.p.getEnchantmentMapper().getUnbreaking()) + 1)), maxDurability * maxDamageModifier);

        itemStack.setDurability((short) Math.min(itemStack.getDurability() + durabilityModifier, maxDurability));
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
     * Checks if a player can use a skill
     * @param player target player
     * @param subSkillType target subskill
     * @return true if the player has permission and has the skill unlocked
     */
    public static boolean canUseSubskill(Player player, @NotNull SubSkillType subSkillType) {
        return Permissions.isSubSkillEnabled(player, subSkillType) && RankUtils.hasUnlockedSubskill(player, subSkillType);
    }
}
