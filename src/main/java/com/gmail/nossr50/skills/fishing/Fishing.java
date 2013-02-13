package com.gmail.nossr50.skills.fishing;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Item;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.event.player.PlayerFishEvent;
import org.bukkit.inventory.ItemStack;

import com.gmail.nossr50.config.AdvancedConfig;
import com.gmail.nossr50.config.Config;
import com.gmail.nossr50.config.TreasuresConfig;
import com.gmail.nossr50.datatypes.McMMOPlayer;
import com.gmail.nossr50.datatypes.treasure.FishingTreasure;
import com.gmail.nossr50.locale.LocaleLoader;
import com.gmail.nossr50.skills.utilities.SkillTools;
import com.gmail.nossr50.skills.utilities.SkillType;
import com.gmail.nossr50.util.ItemChecks;
import com.gmail.nossr50.util.Misc;
import com.gmail.nossr50.util.Permissions;

public final class Fishing {
    static final AdvancedConfig ADVANCED_CONFIG = AdvancedConfig.getInstance();

    // The order of the values is extremely important, a few methods depend on it to work properly
    protected enum Tier {
        FIVE(5) {
            @Override public int getLevel() {return ADVANCED_CONFIG.getFishingTierLevelsTier5();}
            @Override public int getShakeChance() {return ADVANCED_CONFIG.getShakeChanceRank5();}
            @Override public int getVanillaXPBoostModifier() {return ADVANCED_CONFIG.getFishingVanillaXPModifierRank5();}},
        FOUR(4) {
            @Override public int getLevel() {return ADVANCED_CONFIG.getFishingTierLevelsTier4();}
            @Override public int getShakeChance() {return ADVANCED_CONFIG.getShakeChanceRank4();}
            @Override public int getVanillaXPBoostModifier() {return ADVANCED_CONFIG.getFishingVanillaXPModifierRank4();}},
        THREE(3) {
            @Override public int getLevel() {return ADVANCED_CONFIG.getFishingTierLevelsTier3();}
            @Override public int getShakeChance() {return ADVANCED_CONFIG.getShakeChanceRank3();}
            @Override public int getVanillaXPBoostModifier() {return ADVANCED_CONFIG.getFishingVanillaXPModifierRank3();}},
        TWO(2) {
            @Override public int getLevel() {return ADVANCED_CONFIG.getFishingTierLevelsTier2();}
            @Override public int getShakeChance() {return ADVANCED_CONFIG.getShakeChanceRank2();}
            @Override public int getVanillaXPBoostModifier() {return ADVANCED_CONFIG.getFishingVanillaXPModifierRank2();}},
        ONE(1) {
            @Override public int getLevel() {return ADVANCED_CONFIG.getFishingTierLevelsTier1();}
            @Override public int getShakeChance() {return ADVANCED_CONFIG.getShakeChanceRank1();}
            @Override public int getVanillaXPBoostModifier() {return ADVANCED_CONFIG.getFishingVanillaXPModifierRank1();}};

        int numerical;

        private Tier(int numerical) {
            this.numerical = numerical;
        }

        public int toNumerical() {
            return numerical;
        }

        abstract protected int getLevel();
        abstract protected int getShakeChance();
        abstract protected int getVanillaXPBoostModifier();
    }

    // TODO: Get rid of that
    public static int fishermansDietRankLevel1 = ADVANCED_CONFIG.getFishermanDietRankChange();
    public static int fishermansDietRankLevel2 = fishermansDietRankLevel1 * 2;
    public static int fishermansDietMaxLevel = fishermansDietRankLevel1 * 5;

    private Fishing() {}

    /**
     * Begins Fisherman's Diet ability
     *
     * @param player Player using the ability
     * @param rankChange ???
     * @param event Event to process
     */
    public static void beginFishermansDiet(Player player, int rankChange, FoodLevelChangeEvent event) {
        // TODO: The permission should probably not be checked here
        // TODO: Also I don't like the idea of moving event around
        if (!Permissions.fishermansDiet(player)) {
            return;
        }

        SkillTools.handleFoodSkills(player, SkillType.FISHING, event, fishermansDietRankLevel1, fishermansDietMaxLevel, rankChange);
    }

    /**
     * Begins Shake Mob ability
     *
     * @param player Player using the ability
     * @param mob Targeted mob
     * @param skillLevel Fishing level of the player
     */
    public static void beginShakeMob(Player player, LivingEntity mob, int skillLevel) {
        ShakeMob.process(player, mob, skillLevel);
    }

    /**
     * Begins Fishing
     *
     * @param mcMMOPlayer Player fishing
     * @param skillLevel Fishing level of the player
     * @param event Event to process
     */
    public static void beginFishing(McMMOPlayer mcMMOPlayer, int skillLevel, PlayerFishEvent event) {
        int treasureXp = 0;
        Player player = mcMMOPlayer.getPlayer();
        FishingTreasure treasure = checkForTreasure(player, skillLevel);

        if (treasure != null) {
            player.sendMessage(LocaleLoader.getString("Fishing.ItemFound"));

            treasureXp = treasure.getXp();
            ItemStack treasureDrop = treasure.getDrop();

            if (Permissions.fishingMagic(player) && beginMagicHunter(player, skillLevel, treasureDrop, player.getWorld().hasStorm())) {
                player.sendMessage(LocaleLoader.getString("Fishing.MagicFound"));
            }

            // Drop the original catch at the feet of the player and set the treasure as the real catch
            Item caught = (Item) event.getCaught();
            Misc.dropItem(player.getEyeLocation(), caught.getItemStack());
            caught.setItemStack(treasureDrop);
        }

        mcMMOPlayer.beginXpGain(SkillType.FISHING, Config.getInstance().getFishingBaseXP() + treasureXp);
        event.setExpToDrop(event.getExpToDrop() * getVanillaXpMultiplier(skillLevel));
    }

    /**
     * Checks for treasure
     *
     * @param player Player fishing
     * @param skillLevel Fishing level of the player
     * @return Chosen treasure
     */
    private static FishingTreasure checkForTreasure(Player player, int skillLevel) {
        if (!Config.getInstance().getFishingDropsEnabled() || !Permissions.fishingTreasures(player)) {
            return null;
        }

        List<FishingTreasure> rewards = new ArrayList<FishingTreasure>();

        for (FishingTreasure treasure : TreasuresConfig.getInstance().fishingRewards) {
            int maxLevel = treasure.getMaxLevel();

            if (treasure.getDropLevel() <= skillLevel && (maxLevel >= skillLevel || maxLevel <= 0)) {
                rewards.add(treasure);
            }
        }

        if (rewards.isEmpty()) {
            return null;
        }

        FishingTreasure treasure = rewards.get(Misc.getRandom().nextInt(rewards.size()));
        ItemStack treasureDrop = treasure.getDrop();
        int activationChance = SkillTools.calculateActivationChance(Permissions.luckyFishing(player));

        if (Misc.getRandom().nextDouble() * activationChance > treasure.getDropChance()) {
            return null;
        }

        short maxDurability = treasureDrop.getType().getMaxDurability();

        if (maxDurability > 0) {
            treasureDrop.setDurability((short) (Misc.getRandom().nextInt(maxDurability)));
        }

        return treasure;
    }

    /**
     * Processes for treasure
     *
     * @param player Player fishing
     * @param skillLevel Fishing level of the player
     * @param itemStack ItemStack to enchant
     * @param storm World's weather
     * @return True if the ItemStack has been enchanted
     */
    private static boolean beginMagicHunter(Player player, int skillLevel, ItemStack itemStack, boolean storm) {
        if (!ItemChecks.isEnchantable(itemStack)) {
            return false;
        }

        int activationChance = SkillTools.calculateActivationChance(Permissions.luckyFishing(player));

        if (storm) {
            activationChance = (int) (activationChance * 0.909);
        }

        if (Misc.getRandom().nextInt(activationChance) > getLootTier(skillLevel) * ADVANCED_CONFIG.getFishingMagicMultiplier()) {
            return false;
        }

        List<Enchantment> possibleEnchantments = new ArrayList<Enchantment>();

        for (Enchantment enchantment : Enchantment.values()) {
            if (enchantment.canEnchantItem(itemStack)) {
                possibleEnchantments.add(enchantment);
            }
        }

        // This make sure that the order isn't always the same, for example previously Unbreaking had a lot more chance to be used than any other enchant
        Collections.shuffle(possibleEnchantments, Misc.getRandom());

        boolean enchanted = false;
        int specificChance = 1;

        for (Enchantment possibleEnchantment : possibleEnchantments) {
            boolean conflicts = false;

            for (Enchantment currentEnchantment : itemStack.getEnchantments().keySet()) {
                conflicts = currentEnchantment.conflictsWith(possibleEnchantment);

                if (conflicts) {
                    break;
                }
            }

            if (!conflicts && Misc.getRandom().nextInt(specificChance) == 0) {
                itemStack.addEnchantment(possibleEnchantment, Misc.getRandom().nextInt(possibleEnchantment.getMaxLevel()) + 1);

                specificChance++;
                enchanted = true;
            }
        }

        return enchanted;
    }

    /**
     * Gets the loot tier for a given skill level
     *
     * @param skillLevel Fishing skill level
     * @return Loot tier
     */
   public static int getLootTier(int skillLevel) {
        for (Tier tier : Tier.values()) {
            if (skillLevel >= tier.getLevel()) {
                return tier.toNumerical();
            }
        }

        return 0;
    }

   /**
    * Gets the vanilla xp multiplier for a given skill level
    *
    * @param skillLevel Fishing skill level
    * @return Shake Mob probability
    */
   public static int getVanillaXpMultiplier(int skillLevel) {
       for (Tier tier : Tier.values()) {
           if (skillLevel >= tier.getLevel()) {
               return tier.getVanillaXPBoostModifier();
           }
       }

       return 0;
   }
}
