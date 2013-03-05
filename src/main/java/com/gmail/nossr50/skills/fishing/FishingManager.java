package com.gmail.nossr50.skills.fishing;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Item;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Sheep;
import org.bukkit.entity.Skeleton;
import org.bukkit.entity.Skeleton.SkeletonType;
import org.bukkit.inventory.ItemStack;

import com.gmail.nossr50.config.AdvancedConfig;
import com.gmail.nossr50.config.Config;
import com.gmail.nossr50.config.treasure.TreasureConfig;
import com.gmail.nossr50.datatypes.player.McMMOPlayer;
import com.gmail.nossr50.datatypes.skills.SkillType;
import com.gmail.nossr50.datatypes.treasure.FishingTreasure;
import com.gmail.nossr50.locale.LocaleLoader;
import com.gmail.nossr50.skills.SkillManager;
import com.gmail.nossr50.skills.fishing.Fishing.Tier;
import com.gmail.nossr50.util.ItemUtils;
import com.gmail.nossr50.util.Misc;
import com.gmail.nossr50.util.Permissions;
import com.gmail.nossr50.util.skills.CombatUtils;
import com.gmail.nossr50.util.skills.SkillUtils;

public class FishingManager extends SkillManager {
    public FishingManager(McMMOPlayer mcMMOPlayer) {
        super(mcMMOPlayer, SkillType.FISHING);
    }

    public boolean canShake(Entity target) {
        return target instanceof LivingEntity && getSkillLevel() >= AdvancedConfig.getInstance().getShakeUnlockLevel() && Permissions.shake(getPlayer());
    }

    /**
     * Gets the loot tier
     *
     * @return the loot tier
     */
    public int getLootTier() {
        int skillLevel = getSkillLevel();

        for (Tier tier : Tier.values()) {
            if (skillLevel >= tier.getLevel()) {
                return tier.toNumerical();
            }
        }

        return 0;
    }

    /**
     * Gets the Shake Mob probability
     *
     * @return Shake Mob probability
     */
    public int getShakeProbability() {
        int skillLevel = getSkillLevel();

        for (Tier tier : Tier.values()) {
            if (skillLevel >= tier.getLevel()) {
                return tier.getShakeChance();
            }
        }

        return 0;
    }

    /**
     * Handle the Fisherman's Diet ability
     *
     * @param rankChange The # of levels to change rank for the food
     * @param eventFoodLevel The initial change in hunger from the event
     * @return the modified change in hunger for the event
     */
    public int handleFishermanDiet(int rankChange, int eventFoodLevel) {
        return SkillUtils.handleFoodSkills(getPlayer(), skill, eventFoodLevel, Fishing.fishermansDietRankLevel1, Fishing.fishermansDietMaxLevel, rankChange);
    }

    /**
     * Process the results from a successful fishing trip
     *
     * @param fishingCatch The {@link Item} initially caught
     */
    public void handleFishing(Item fishingCatch) {
        int treasureXp = 0;
        Player player = getPlayer();
        FishingTreasure treasure = null;

        if (Config.getInstance().getFishingDropsEnabled() && Permissions.fishingTreasureHunter(player)) {
            treasure = getFishingTreasure();
        }

        if (treasure != null) {
            player.sendMessage(LocaleLoader.getString("Fishing.ItemFound"));

            treasureXp = treasure.getXp();
            ItemStack treasureDrop = treasure.getDrop();

            if (Permissions.magicHunter(player) && ItemUtils.isEnchantable(treasureDrop) && handleMagicHunter(treasureDrop)) {
                player.sendMessage(LocaleLoader.getString("Fishing.MagicFound"));
            }

            // Drop the original catch at the feet of the player and set the treasure as the real catch
            Misc.dropItem(player.getEyeLocation(), fishingCatch.getItemStack());
            fishingCatch.setItemStack(treasureDrop);
        }

        applyXpGain(Config.getInstance().getFishingBaseXP() + treasureXp);
    }

    /**
     * Handle the vanilla XP boost for Fishing
     *
     * @param experience The amount of experience initially awarded by the event
     * @return the modified event damage
     */
    public int handleVanillaXpBoost(int experience) {
        return experience * getVanillaXpMultiplier();
    }

    /**
     * Handle the Shake ability
     *
     * @param mob The {@link LivingEntity} affected by the ability
     */
    public void shakeCheck(LivingEntity target) {
        if (getShakeProbability() > Misc.getRandom().nextInt(getActivationChance())) {
            Map<ItemStack, Integer> possibleDrops = new HashMap<ItemStack, Integer>();

            Fishing.findPossibleDrops(target, possibleDrops);

            if (possibleDrops.isEmpty()) {
                return;
            }

            ItemStack drop = Fishing.chooseDrop(possibleDrops);

            // It's possible that chooseDrop returns null if the sum of probability in possibleDrops is inferior than 100
            if (drop == null) {
                return;
            }

            // Extra processing depending on the mob and drop type
            switch (target.getType()) {
                case SHEEP:
                    Sheep sheep = (Sheep) target;

                    if (drop.getType() == Material.WOOL) {
                        if (sheep.isSheared()) {
                            return;
                        }

                        drop.setDurability(sheep.getColor().getWoolData());
                        sheep.setSheared(true);
                    }
                    break;

                case SKELETON:
                    Skeleton skeleton = (Skeleton) target;

                    if (skeleton.getSkeletonType() == SkeletonType.WITHER) {
                        switch (drop.getType()) {
                            case SKULL_ITEM:
                                drop.setDurability((short) 1);
                                break;

                            case ARROW:
                                drop.setType(Material.COAL);
                                break;

                            default:
                                break;
                        }
                    }
                    break;

                default:
                    break;
            }

            Misc.dropItem(target.getLocation(), drop);
            CombatUtils.dealDamage(target, target.getMaxHealth() / 4); // Make it so you can shake a mob no more than 4 times.
        }
    }

    /**
     * Process the Treasure Hunter ability for Fishing
     *
     * @return The {@link FishingTreasure} found, or null if no treasure was found.
     */
    private FishingTreasure getFishingTreasure() {
        List<FishingTreasure> rewards = new ArrayList<FishingTreasure>();
        int skillLevel = getSkillLevel();

        for (FishingTreasure treasure : TreasureConfig.getInstance().fishingRewards) {
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

        if (!SkillUtils.treasureDropSuccessful(treasure.getDropChance(), skillLevel)) {
            return null;
        }

        short maxDurability = treasureDrop.getType().getMaxDurability();

        if (maxDurability > 0) {
            treasureDrop.setDurability((short) (Misc.getRandom().nextInt(maxDurability)));
        }

        return treasure;
    }

    /**
     * Process the Magic Hunter ability
     *
     * @param treasureDrop The {@link ItemStack} to enchant
     * @return true if the item has been enchanted
     */
    private boolean handleMagicHunter(ItemStack treasureDrop) {
        Player player = getPlayer();
        int activationChance = this.activationChance;

        if (player.getWorld().hasStorm()) {
            activationChance *= Fishing.STORM_MODIFIER;
        }

        if (Misc.getRandom().nextInt(activationChance) > getLootTier() * AdvancedConfig.getInstance().getFishingMagicMultiplier()) {
            return false;
        }

        List<Enchantment> possibleEnchantments = new ArrayList<Enchantment>();

        for (Enchantment enchantment : Enchantment.values()) {
            if (enchantment.canEnchantItem(treasureDrop)) {
                possibleEnchantments.add(enchantment);
            }
        }

        // This make sure that the order isn't always the same, for example previously Unbreaking had a lot more chance to be used than any other enchant
        Collections.shuffle(possibleEnchantments, Misc.getRandom());

        boolean enchanted = false;
        int specificChance = 1;

        for (Enchantment possibleEnchantment : possibleEnchantments) {
            boolean conflicts = false;

            for (Enchantment currentEnchantment : treasureDrop.getEnchantments().keySet()) {
                conflicts = currentEnchantment.conflictsWith(possibleEnchantment);

                if (conflicts) {
                    break;
                }
            }

            if (!conflicts && Misc.getRandom().nextInt(specificChance) == 0) {
                treasureDrop.addEnchantment(possibleEnchantment, Misc.getRandom().nextInt(possibleEnchantment.getMaxLevel()) + 1);

                specificChance++;
                enchanted = true;
            }
        }

        return enchanted;
    }

    /**
     * Gets the vanilla XP multiplier
     *
     * @return the vanilla XP multiplier
     */
    private int getVanillaXpMultiplier() {
        int skillLevel = getSkillLevel();

        for (Tier tier : Tier.values()) {
            if (skillLevel >= tier.getLevel()) {
                return tier.getVanillaXPBoostModifier();
            }
        }

        return 0;
    }
}
