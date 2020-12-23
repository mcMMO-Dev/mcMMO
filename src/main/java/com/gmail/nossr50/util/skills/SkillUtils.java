package com.gmail.nossr50.util.skills;

import com.gmail.nossr50.config.AdvancedConfig;
import com.gmail.nossr50.config.Config;
import com.gmail.nossr50.config.HiddenConfig;
import com.gmail.nossr50.datatypes.interactions.NotificationType;
import com.neetgames.mcmmo.player.OnlineMMOPlayer;
import com.gmail.nossr50.datatypes.skills.PrimarySkillType;
import com.gmail.nossr50.datatypes.skills.SubSkillType;
import com.gmail.nossr50.datatypes.skills.SuperAbilityType;
import com.gmail.nossr50.locale.LocaleLoader;
import com.gmail.nossr50.mcMMO;
import com.gmail.nossr50.util.ItemUtils;
import com.gmail.nossr50.util.Misc;
import com.gmail.nossr50.util.compat.layers.persistentdata.AbstractPersistentDataLayer;
import com.gmail.nossr50.util.player.NotificationManager;
import com.gmail.nossr50.util.text.StringUtils;
import com.neetgames.mcmmo.exceptions.UnexpectedValueException;
import com.neetgames.mcmmo.skill.SkillBossBarState;
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

    public static void applyXpGain(@NotNull OnlineMMOPlayer mmoPlayer, @NotNull PrimarySkillType primarySkillType, float xp, @NotNull XPGainReason xpGainReason) {
        mmoPlayer.getExperienceHandler().beginXpGain(Misc.adaptPlayer(mmoPlayer), primarySkillType, xp, xpGainReason, XPGainSource.SELF);
    }

    public static void applyXpGain(@NotNull OnlineMMOPlayer mmoPlayer, @NotNull PrimarySkillType primarySkillType, float xp, @NotNull XPGainReason xpGainReason, @NotNull XPGainSource xpGainSource) {
        mmoPlayer.getExperienceHandler().beginXpGain(Misc.adaptPlayer(mmoPlayer), primarySkillType, xp, xpGainReason, xpGainSource);
    }

    public static @NotNull SkillBossBarState asBarState(String str) {
        for(SkillBossBarState barState : SkillBossBarState.values()) {
            if(barState.toString().equalsIgnoreCase(str)) {
                return barState;
            }
        }

        mcMMO.p.getLogger().severe("Unable to read bar state for value " + str + " setting to default instead.");
        return SkillBossBarState.NORMAL;
    }

    /*
     * Skill Stat Calculations
     */

    public static @NotNull String[] calculateLengthDisplayValues(@NotNull OnlineMMOPlayer mmoPlayer, float skillValue, @NotNull PrimarySkillType skill) {
        int maxLength = skill.getSuperAbilityType().getMaxLength();
        int abilityLengthVar = AdvancedConfig.getInstance().getAbilityLength();
        int abilityLengthCap = AdvancedConfig.getInstance().getAbilityLengthCap();

        int length;

        if(abilityLengthCap > 0)
        {
            length =     (int) Math.min(abilityLengthCap, 2 + (skillValue / abilityLengthVar));
        } else {
            length = 2 + (int) (skillValue / abilityLengthVar);
        }

        int enduranceLength = PerksUtils.handleActivationPerks(Misc.adaptPlayer(mmoPlayer), length, maxLength);

        if (maxLength != 0) {
            length = Math.min(length, maxLength);
        }

        return new String[] { String.valueOf(length), String.valueOf(enduranceLength) };
    }

    /*
     * Others
     */

    public static int handleFoodSkills(@NotNull OnlineMMOPlayer mmoPlayer, int eventFoodLevel, @NotNull SubSkillType subSkillType) {
        int curRank = RankUtils.getRank(mmoPlayer, subSkillType);

        int currentFoodLevel = Misc.adaptPlayer(mmoPlayer).getFoodLevel();
        int foodChange = eventFoodLevel - currentFoodLevel;

        foodChange+=curRank;

        return currentFoodLevel + foodChange;
    }

    /**
     * Calculate the time remaining until the cooldown expires.
     *
     * @param deactivatedTimeStamp Time of deactivation
     * @param cooldown The length of the cooldown
     * @param mmoPlayer The Player to check for cooldown perks
     *
     * @return the number of seconds remaining before the cooldown expires
     */
    public static int calculateTimeLeft(long deactivatedTimeStamp, int cooldown, @NotNull OnlineMMOPlayer mmoPlayer) {
        return (int) (((deactivatedTimeStamp + (PerksUtils.handleCooldownPerks(Misc.adaptPlayer(mmoPlayer), cooldown) * Misc.TIME_CONVERSION_FACTOR)) - System.currentTimeMillis()) / Misc.TIME_CONVERSION_FACTOR);
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
    public static boolean isSkill(@NotNull String skillName) {
        return Config.getInstance().getLocale().equalsIgnoreCase("en_US") ? PrimarySkillType.getSkill(skillName) != null : isLocalizedSkill(skillName);
    }

    public static void sendSkillMessage(@NotNull Player player, @NotNull NotificationType notificationType, @NotNull String key) {
        Location location = player.getLocation();

        for (Player otherPlayer : player.getWorld().getPlayers()) {
            if (otherPlayer != player && Misc.isNear(location, otherPlayer.getLocation(), Misc.SKILL_MESSAGE_MAX_SENDING_DISTANCE)) {
                NotificationManager.sendNearbyPlayersInformation(otherPlayer, notificationType, key, player.getName());
            }
        }
    }

    public static void handleAbilitySpeedIncrease(@NotNull Player player) {
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

            OnlineMMOPlayer mmoPlayer = mcMMO.getUserManager().queryPlayer(player);

            //Not Loaded
            if(mmoPlayer == null)
                return;

            PrimarySkillType skill = mmoPlayer.getSuperAbilityManager().getAbilityMode(SuperAbilityType.SUPER_BREAKER) ? PrimarySkillType.MINING : PrimarySkillType.EXCAVATION;

            int abilityLengthVar = AdvancedConfig.getInstance().getAbilityLength();
            int abilityLengthCap = AdvancedConfig.getInstance().getAbilityLengthCap();

            int ticks;

            if(abilityLengthCap > 0)
            {
                ticks = PerksUtils.handleActivationPerks(player,  Math.min(abilityLengthCap, 2 + (mmoPlayer.getExperienceHandler().getSkillLevel(skill) / abilityLengthVar)),
                        skill.getSuperAbilityType().getMaxLength()) * Misc.TICK_CONVERSION_FACTOR;
            } else {
                ticks = PerksUtils.handleActivationPerks(player, 2 + ((mmoPlayer.getExperienceHandler().getSkillLevel(skill)) / abilityLengthVar),
                        skill.getSuperAbilityType().getMaxLength()) * Misc.TICK_CONVERSION_FACTOR;
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

    public static void handleDurabilityChange(@NotNull ItemStack itemStack, int durabilityModifier) {
        handleDurabilityChange(itemStack, durabilityModifier, 1.0);
    }

    /**
     * Modify the durability of an ItemStack.
     *
     * @param itemStack The ItemStack which durability should be modified
     * @param durabilityModifier the amount to modify the durability by
     * @param maxDamageModifier the amount to adjust the max damage by
     */
    public static void handleDurabilityChange(@NotNull ItemStack itemStack, double durabilityModifier, double maxDamageModifier) {
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

    public static @Nullable Material getRepairAndSalvageItem(@NotNull ItemStack inHand) {
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

    public static int getRepairAndSalvageQuantities(@NotNull ItemStack item) {
        if(getRepairAndSalvageItem(item) == null) {
            mcMMO.p.getLogger().severe("No value defined for item "+item.toString()+" in getRepairAndSalvageItem()");
            throw new UnexpectedValueException();
        }

        return getRepairAndSalvageQuantities(item.getType(), getRepairAndSalvageItem(item));
    }

    public static int getRepairAndSalvageQuantities(@NotNull Material itemMaterial, @NotNull Material recipeMaterial) {
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
}
