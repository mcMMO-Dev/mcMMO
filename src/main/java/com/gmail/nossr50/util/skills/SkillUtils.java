package com.gmail.nossr50.util.skills;

import com.gmail.nossr50.config.AdvancedConfig;
import com.gmail.nossr50.config.Config;
import com.gmail.nossr50.config.HiddenConfig;
import com.gmail.nossr50.datatypes.player.McMMOPlayer;
import com.gmail.nossr50.datatypes.skills.SuperAbility;
import com.gmail.nossr50.datatypes.skills.PrimarySkill;
import com.gmail.nossr50.datatypes.skills.SubSkill;
import com.gmail.nossr50.events.skills.secondaryabilities.SubSkillEvent;
import com.gmail.nossr50.events.skills.secondaryabilities.SubSkillWeightedActivationCheckEvent;
import com.gmail.nossr50.locale.LocaleLoader;
import com.gmail.nossr50.mcMMO;
import com.gmail.nossr50.util.EventUtils;
import com.gmail.nossr50.util.ItemUtils;
import com.gmail.nossr50.util.Misc;
import com.gmail.nossr50.util.StringUtils;
import com.gmail.nossr50.util.player.UserManager;
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

import java.util.ArrayList;
import java.util.List;

public class SkillUtils {
    public static int handleFoodSkills(Player player, PrimarySkill skill, int eventFoodLevel, int baseLevel, int maxLevel, int rankChange) {
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
        return Config.getInstance().getLocale().equalsIgnoreCase("en_US") ? PrimarySkill.getSkill(skillName) != null : isLocalizedSkill(skillName);
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
            ItemStack heldItem = player.getInventory().getItemInMainHand();

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
            PrimarySkill skill = mcMMOPlayer.getAbilityMode(SuperAbility.SUPER_BREAKER) ? PrimarySkill.MINING : PrimarySkill.EXCAVATION;
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
        if (itemStack.hasItemMeta() && itemStack.getItemMeta().isUnbreakable()) {
            return;
        }

        Material type = itemStack.getType();
        short maxDurability = mcMMO.getRepairableManager().isRepairable(type) ? mcMMO.getRepairableManager().getRepairable(type).getMaximumDurability() : type.getMaxDurability();
        durabilityModifier = (int) Math.min(durabilityModifier / (itemStack.getEnchantmentLevel(Enchantment.DURABILITY) + 1), maxDurability * maxDamageModifier);

        itemStack.setDurability((short) Math.min(itemStack.getDurability() + durabilityModifier, maxDurability));
    }

    /**
     * Checks whether or not the given skill succeeds
     * @param subSkill The ability corresponding to this check
     * @param player The player whose skill levels we are checking against
     * @param skillLevel The skill level of the corresponding skill
     * @param activationChance used to determine activation chance
     * @param maxChance maximum chance
     * @param maxLevel maximum skill level bonus
     * @return true if random chance succeeds and the event isn't cancelled
     */
    private static boolean performRandomSkillCheck(SubSkill subSkill, Player player, int skillLevel, int activationChance, double maxChance, int maxLevel) {
        double chance = (maxChance / maxLevel) * Math.min(skillLevel, maxLevel) / activationChance;
        return performRandomSkillCheckStatic(subSkill, player, activationChance, chance);
    }

    /**
     * This method is the final step in determining if a Sub-Skill / Secondary Skill in mcMMO successfully activates either from chance or otherwise
     *
     * There are 4 types of Sub-Skill / Secondary Skill activations in mcMMO
     * 1) Random Chance with a linear increase to 100% (At 100 Skill Level)
     * 2) Random Chance with a linear increase to 100% at 100 Skill Level but caps out earlier in the curve (At x/100 Skill Level)
     * 3) Random Chance with a pre-determined activation roll and threshold roll
     * 4) Skills that are not chance based
     *
     * Random skills check for success based on numbers and then fire a cancellable event, if that event is not cancelled they succeed
     * All other skills just fire the cancellable event and succeed if it is not cancelled
     *
     * @param subSkill The identifier for this specific sub-skill
     * @param player The owner of this sub-skill
     * @param skill The identifier for the parent of our sub-skill
     * @param activationChance This is the value that we roll against, 100 is normal, and 75 is for lucky perk
     * @param subskillActivationType this value represents what kind of activation procedures this sub-skill uses
     * @return returns true if all conditions are met and they event is not cancelled
     */
    public static boolean isActivationSuccessful(SubSkillActivationType subskillActivationType, SubSkill subSkill, Player player,
                                                 PrimarySkill skill, int skillLevel, int activationChance)
    {
        //Maximum chance to succeed
        double maxChance = AdvancedConfig.getInstance().getMaxChance(subSkill);
        //Maximum roll we can make
        int maxBonusLevel = AdvancedConfig.getInstance().getMaxBonusLevel(subSkill);

        switch(subskillActivationType)
        {
            //100 Skill = Guaranteed
            case RANDOM_LINEAR_100_SCALE_NO_CAP:
                return performRandomSkillCheck(subSkill, player, skillLevel, PerksUtils.handleLuckyPerks(player, skill), 100.0D, 100);
            case RANDOM_LINEAR_100_SCALE_WITH_CAP:
                return performRandomSkillCheck(subSkill, player, skillLevel, PerksUtils.handleLuckyPerks(player, skill), AdvancedConfig.getInstance().getMaxChance(subSkill), AdvancedConfig.getInstance().getMaxBonusLevel(subSkill));
            case RANDOM_STATIC_CHANCE:
                //Grab the static activation chance of this skill
                double staticRoll = getSecondaryAbilityStaticChance(subSkill) / activationChance;
                return performRandomSkillCheckStatic(subSkill, player, activationChance, staticRoll);
            case ALWAYS_FIRES:
                SubSkillEvent event = EventUtils.callSubSkillEvent(player, subSkill);
                return !event.isCancelled();
                default:
                    return false;
        }
    }

    /**
     * Grabs static activation rolls for Secondary Abilities
     * @param subSkill The secondary ability to grab properties of
     * @return The static activation roll involved in the RNG calculation
     */
    public static double getSecondaryAbilityStaticChance(SubSkill subSkill)
    {
        switch(subSkill)
        {
            case AXES_ARMOR_IMPACT:
                return AdvancedConfig.getInstance().getImpactChance();
            case AXES_GREATER_IMPACT:
                return AdvancedConfig.getInstance().getGreaterImpactChance();
            case TAMING_FAST_FOOD:
                return AdvancedConfig.getInstance().getFastFoodChance();
                default:
                    return 100.0D;
        }
    }

    /**
     * Used to determine whether or not a sub-skill activates from random chance (using static values)
     * @param subSkill The identifier for this specific sub-skill
     * @param player The owner of this sub-skill
     * @param activationChance This is the value that we roll against, 100 is normal, and 75 is for lucky perk
     * @param chance This is the static modifier for our random calculations
     * @return true if random chance was successful and the event wasn't cancelled
     */
    private static boolean performRandomSkillCheckStatic(SubSkill subSkill, Player player, int activationChance, double chance) {
        SubSkillWeightedActivationCheckEvent event = new SubSkillWeightedActivationCheckEvent(player, subSkill, chance);
        mcMMO.p.getServer().getPluginManager().callEvent(event);
        return (event.getChance() * activationChance) > Misc.getRandom().nextInt(activationChance) && !event.isCancelled();
    }

    public static boolean treasureDropSuccessful(Player player, double dropChance, int activationChance) {
        SubSkillWeightedActivationCheckEvent event = new SubSkillWeightedActivationCheckEvent(player, SubSkill.EXCAVATION_TREASURE_HUNTER, dropChance / activationChance);
        mcMMO.p.getServer().getPluginManager().callEvent(event);
        return (event.getChance() * activationChance) > (Misc.getRandom().nextDouble() * activationChance) && !event.isCancelled();
    }

    private static boolean isLocalizedSkill(String skillName) {
        for (PrimarySkill skill : PrimarySkill.values()) {
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
        return getRepairAndSalvageQuantities(item, getRepairAndSalvageItem(item), (byte) -1);
    }

    public static int getRepairAndSalvageQuantities(ItemStack item, Material repairMaterial, byte repairMetadata) {
        // Workaround for Bukkit bug where damaged items would not return any recipes
        item = item.clone();
        item.setDurability((short) 0);

        int quantity = 0;
        List<Recipe> recipes = mcMMO.p.getServer().getRecipesFor(item);

        if (recipes.isEmpty()) {
            return quantity;
        }

        Recipe recipe = recipes.get(0);

        if (recipe instanceof ShapelessRecipe) {
            for (ItemStack ingredient : ((ShapelessRecipe) recipe).getIngredientList()) {
                if (ingredient != null && (repairMaterial == null || ingredient.getType() == repairMaterial) && (repairMetadata == -1 || ingredient.getType().equals(repairMaterial))) {
                    quantity += ingredient.getAmount();
                }
            }
        }
        else if (recipe instanceof ShapedRecipe) {
            for (ItemStack ingredient : ((ShapedRecipe) recipe).getIngredientMap().values()) {
                if (ingredient != null && (repairMaterial == null || ingredient.getType() == repairMaterial) && (repairMetadata == -1 || ingredient.getType().equals(repairMaterial))) {
                    quantity += ingredient.getAmount();
                }
            }
        }

        return quantity;
    }
}
