package com.gmail.nossr50.skills;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Random;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Item;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Sheep;
import org.bukkit.event.player.PlayerFishEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.material.Wool;

import com.gmail.nossr50.Combat;
import com.gmail.nossr50.ItemChecks;
import com.gmail.nossr50.Users;
import com.gmail.nossr50.m;
import com.gmail.nossr50.config.LoadProperties;
import com.gmail.nossr50.config.LoadTreasures;
import com.gmail.nossr50.datatypes.PlayerProfile;
import com.gmail.nossr50.datatypes.SkillType;
import com.gmail.nossr50.datatypes.treasure.FishingTreasure;
import com.gmail.nossr50.locale.mcLocale;

public class Fishing {

    private static Random random = new Random();

    /**
     * Get the player's current fishing loot tier.
     *
     * @param PP The profile of the player
     * @return the player's current fishing rank
     */
    public static int getFishingLootTier(PlayerProfile PP) {
        int level = PP.getSkillLevel(SkillType.FISHING);
        int fishingTier;

        if (level >= LoadProperties.fishingTier5) {
            fishingTier = 5;
        }
        else if (level >= LoadProperties.fishingTier4) {
            fishingTier = 4;
        }
        else if (level >= LoadProperties.fishingTier3) {
            fishingTier =  3;
        }
        else if (level >= LoadProperties.fishingTier2) {
            fishingTier =  2;
        }
        else {
            fishingTier =  1;
        }

        return fishingTier;
    }

    /**
     * Get item results from Fishing.
     *
     * @param player The player that was fishing
     * @param event The event to modify
     */
    private static void getFishingResults(Player player, PlayerFishEvent event) {
        PlayerProfile PP = Users.getProfile(player);
        List<FishingTreasure> rewards = new ArrayList<FishingTreasure>();
        Item theCatch = (Item) event.getCaught();

        switch (getFishingLootTier(PP)) {
        case 1:
            rewards = LoadTreasures.fishingRewardsTier1;
            break;

        case 2:
            rewards = LoadTreasures.fishingRewardsTier2;
            break;

        case 3:
            rewards = LoadTreasures.fishingRewardsTier3;
            break;

        case 4:
            rewards = LoadTreasures.fishingRewardsTier4;
            break;

        case 5:
            rewards = LoadTreasures.fishingRewardsTier5;
            break;

        default:
            break;
        }

        if (LoadProperties.fishingDrops) {
            FishingTreasure treasure = rewards.get(random.nextInt(rewards.size()));

            if (random.nextDouble() * 100 <= treasure.getDropChance()) {
                Users.getProfile(player).addXP(SkillType.FISHING, treasure.getXp());
                theCatch.setItemStack(treasure.getDrop());
            }
        }
        else {
            theCatch.setItemStack(new ItemStack(Material.RAW_FISH));
        }

        short maxDurability = theCatch.getItemStack().getType().getMaxDurability();

        if (maxDurability > 0) {
            theCatch.getItemStack().setDurability((short) (random.nextInt(maxDurability))); //Change durability to random value
        }

        m.mcDropItem(player.getLocation(), new ItemStack(Material.RAW_FISH)); //Always drop a fish
        PP.addXP(SkillType.FISHING, LoadProperties.mfishing);
        Skills.XpCheckSkill(SkillType.FISHING, player);
    }

    /**
     * Process results from Fishing.
     *
     * @param event The event to modify
     */
    public static void processResults(PlayerFishEvent event) {
        Player player = event.getPlayer();
        PlayerProfile PP = Users.getProfile(player);

        getFishingResults(player, event);
        Item theCatch = (Item)event.getCaught();

        if (theCatch.getItemStack().getType() != Material.RAW_FISH) {
            final int ENCHANTMENT_CHANCE = 10;
            boolean enchanted = false;
            ItemStack fishingResults = theCatch.getItemStack();

            player.sendMessage(mcLocale.getString("Fishing.ItemFound"));
            if (ItemChecks.isArmor(fishingResults) || ItemChecks.isTool(fishingResults)) {
                if (random.nextInt(100) <= ENCHANTMENT_CHANCE) {
                    for (Enchantment newEnchant : Enchantment.values()) {
                        if (newEnchant.canEnchantItem(fishingResults)) {
                            Map<Enchantment, Integer> resultEnchantments = fishingResults.getEnchantments();

                            for (Enchantment oldEnchant : resultEnchantments.keySet()) {
                                if (oldEnchant.conflictsWith(newEnchant)) {
                                    return;
                                }
                            }

                            /* Actual chance to have an enchantment is related to your fishing skill */
                            if (random.nextInt(15) < Fishing.getFishingLootTier(PP)) {
                                enchanted = true;
                                int randomEnchantLevel = random.nextInt(newEnchant.getMaxLevel()) + 1;

                                if (randomEnchantLevel < newEnchant.getStartLevel()) {
                                    randomEnchantLevel = newEnchant.getStartLevel();
                                }

                                fishingResults.addEnchantment(newEnchant, randomEnchantLevel);
                            }
                        }
                    }
                }
            }

            if (enchanted) {
                player.sendMessage(mcLocale.getString("Fishing.MagicFound"));
            }
        }
    }

    /**
     * Shake a mob, have them drop an item.
     *
     * @param event The event to modify
     */
    public static void shakeMob(PlayerFishEvent event) {
        final int DROP_NUMBER = random.nextInt(100);

        LivingEntity le = (LivingEntity) event.getCaught();
        EntityType type = le.getType();
        Location loc = le.getLocation();

        switch (type) {
        case BLAZE:
            m.mcDropItem(loc, new ItemStack(Material.BLAZE_ROD));
            break;

        case CAVE_SPIDER:
            if (DROP_NUMBER > 50) {
                m.mcDropItem(loc, new ItemStack(Material.SPIDER_EYE));
            }
            else {
                m.mcDropItem(loc, new ItemStack(Material.STRING));
            }
            break;

        case CHICKEN:
            if (DROP_NUMBER > 66) {
                m.mcDropItem(loc, new ItemStack(Material.FEATHER));
            }
            else if (DROP_NUMBER > 33) {
                m.mcDropItem(loc, new ItemStack(Material.RAW_CHICKEN));
                }
            else {
                m.mcDropItem(loc, new ItemStack(Material.EGG));
            }
            break;

        case COW:
            if (DROP_NUMBER > 99) {
                m.mcDropItem(loc, new ItemStack(Material.MILK_BUCKET));
            }
            else if (DROP_NUMBER > 50) {
                m.mcDropItem(loc, new ItemStack(Material.LEATHER));
            }
            else {
                m.mcDropItem(loc, new ItemStack(Material.RAW_BEEF));
            }
            break;

        case CREEPER:
            m.mcDropItem(loc, new ItemStack(Material.SULPHUR));
            break;

        case ENDERMAN:
            m.mcDropItem(loc, new ItemStack(Material.ENDER_PEARL));
            break;

        case GHAST:
            if (DROP_NUMBER > 50) {
                m.mcDropItem(loc, new ItemStack(Material.SULPHUR));
            }
            else {
                m.mcDropItem(loc, new ItemStack(Material.GHAST_TEAR));
            }
            break;

        case MAGMA_CUBE:
            m.mcDropItem(loc, new ItemStack(Material.MAGMA_CREAM));
            break;

        case MUSHROOM_COW:
            if (DROP_NUMBER > 99) {
                m.mcDropItem(loc, new ItemStack(Material.MILK_BUCKET));
            }
            else if (DROP_NUMBER > 98) {
                m.mcDropItem(loc, new ItemStack(Material.MUSHROOM_SOUP));
            }
            else if (DROP_NUMBER > 66) {
                m.mcDropItem(loc, new ItemStack(Material.LEATHER));
            }
            else if (DROP_NUMBER > 33) {
                m.mcDropItem(loc, new ItemStack(Material.RAW_BEEF));
            }
            else {
                m.mcDropItems(loc, new ItemStack(Material.RED_MUSHROOM), 3);
            }
            break;

        case PIG:
            m.mcDropItem(loc, new ItemStack(Material.PORK));
            break;

        case PIG_ZOMBIE:
            if (DROP_NUMBER > 50) {
                m.mcDropItem(loc, new ItemStack(Material.ROTTEN_FLESH));
            }
            else {
                m.mcDropItem(loc, new ItemStack(Material.GOLD_NUGGET));
            }
            break;

        case SHEEP:
            Sheep sheep = (Sheep) le;
            
            if (!sheep.isSheared()) {
                Wool wool = new Wool();
                wool.setColor(sheep.getColor());

                ItemStack theWool = wool.toItemStack();
                theWool.setAmount(1 + random.nextInt(6));

                m.mcDropItem(loc, theWool);
                sheep.setSheared(true);
            }
            break;

        case SKELETON:
            if (DROP_NUMBER > 50) {
                m.mcDropItem(loc, new ItemStack(Material.BONE));
            }
            else {
                m.mcDropItems(loc, new ItemStack(Material.ARROW), 3);
            }
            break;

        case SLIME:
            m.mcDropItem(loc, new ItemStack(Material.SLIME_BALL));
            break;

        case SNOWMAN:
            if (DROP_NUMBER > 99) {
                m.mcDropItem(loc, new ItemStack(Material.PUMPKIN));
            }
            else {
                m.mcDropItems(loc, new ItemStack(Material.SNOW_BALL), 5);
            }
            break;

        case SPIDER:
            if (DROP_NUMBER > 50) {
                m.mcDropItem(loc, new ItemStack(Material.SPIDER_EYE));
            }
            else {
                m.mcDropItem(loc, new ItemStack(Material.STRING));
            }
            break;

        case SQUID:
            m.mcDropItem(loc, new ItemStack(Material.INK_SACK, 1, (short) 0, (byte) 0x0));
            break;

        case ZOMBIE:
            m.mcDropItem(loc, new ItemStack(Material.ROTTEN_FLESH));
            break;

        default:
            break;
        }

        Combat.dealDamage(le, 1);
    }
}
