package com.gmail.nossr50.skills.gathering;

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

import com.gmail.nossr50.config.Config;
import com.gmail.nossr50.config.TreasuresConfig;
import com.gmail.nossr50.datatypes.PlayerProfile;
import com.gmail.nossr50.datatypes.SkillType;
import com.gmail.nossr50.datatypes.treasure.FishingTreasure;
import com.gmail.nossr50.locale.LocaleLoader;
import com.gmail.nossr50.util.Combat;
import com.gmail.nossr50.util.ItemChecks;
import com.gmail.nossr50.util.Misc;
import com.gmail.nossr50.util.Permissions;
import com.gmail.nossr50.util.Skills;
import com.gmail.nossr50.util.Users;

public class Fishing {

    private static Random random = new Random();

    /**
     * Get the player's current fishing loot tier.
     *
     * @param profile The profile of the player
     * @return the player's current fishing rank
     */
    public static int getFishingLootTier(PlayerProfile profile) {
        int level = profile.getSkillLevel(SkillType.FISHING);
        int fishingTier;

        if (level >= Config.getInstance().getFishingTierLevelsTier5()) {
            fishingTier = 5;
        }
        else if (level >= Config.getInstance().getFishingTierLevelsTier4()) {
            fishingTier = 4;
        }
        else if (level >= Config.getInstance().getFishingTierLevelsTier3()) {
            fishingTier =  3;
        }
        else if (level >= Config.getInstance().getFishingTierLevelsTier2()) {
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
        PlayerProfile profile = Users.getProfile(player);
        List<FishingTreasure> rewards = new ArrayList<FishingTreasure>();
        Item theCatch = (Item) event.getCaught();

        switch (getFishingLootTier(profile)) {
        case 1:
            rewards = TreasuresConfig.getInstance().fishingRewardsTier1;
            break;

        case 2:
            rewards = TreasuresConfig.getInstance().fishingRewardsTier2;
            break;

        case 3:
            rewards = TreasuresConfig.getInstance().fishingRewardsTier3;
            break;

        case 4:
            rewards = TreasuresConfig.getInstance().fishingRewardsTier4;
            break;

        case 5:
            rewards = TreasuresConfig.getInstance().fishingRewardsTier5;
            break;

        default:
            break;
        }

        if (Config.getInstance().getFishingDropsEnabled() && rewards.size() > 0 && Permissions.getInstance().fishingTreasures(player)) {
            FishingTreasure treasure = rewards.get(random.nextInt(rewards.size()));

            int randomChance = 100;

            if (player.hasPermission("mcmmo.perks.lucky.fishing")) {
                randomChance = (int) (randomChance * 0.75);
            }

            if (random.nextDouble() * randomChance <= treasure.getDropChance()) {
                Users.getPlayer(player).addXP(SkillType.FISHING, treasure.getXp());
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

        Skills.xpProcessing(player, profile, SkillType.FISHING, Config.getInstance().getFishingBaseXP());
    }

    /**
     * Process results from Fishing.
     *
     * @param event The event to modify
     */
    public static void processResults(PlayerFishEvent event) {
        Player player = event.getPlayer();
        PlayerProfile profile = Users.getProfile(player);

        getFishingResults(player, event);
        Item theCatch = (Item) event.getCaught();

        if (theCatch.getItemStack().getType() != Material.RAW_FISH) {
            final int ENCHANTMENT_CHANCE = 10;
            boolean enchanted = false;
            ItemStack fishingResults = theCatch.getItemStack();

            player.sendMessage(LocaleLoader.getString("Fishing.ItemFound"));

            if (ItemChecks.isArmor(fishingResults) || ItemChecks.isTool(fishingResults)) {
                int randomChance = 100;

                if (player.hasPermission("mcmmo.perks.lucky.fishing")) {
                    randomChance = (int) (randomChance * 0.75);
                }

                if (random.nextInt(randomChance) <= ENCHANTMENT_CHANCE && Permissions.getInstance().fishingMagic(player)) {
                    for (Enchantment newEnchant : Enchantment.values()) {
                        if (newEnchant.canEnchantItem(fishingResults)) {
                            Map<Enchantment, Integer> resultEnchantments = fishingResults.getEnchantments();

                            for (Enchantment oldEnchant : resultEnchantments.keySet()) {
                                if (oldEnchant.conflictsWith(newEnchant)) {
                                    return;
                                }
                            }

                            /* Actual chance to have an enchantment is related to your fishing skill */
                            if (random.nextInt(15) < Fishing.getFishingLootTier(profile)) {
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
                player.sendMessage(LocaleLoader.getString("Fishing.MagicFound"));
            }
        }
    }

    /**
     * Shake a mob, have them drop an item.
     *
     * @param event The event to modify
     */
    public static void shakeMob(PlayerFishEvent event) {
        int randomChance = 100;

        if (event.getPlayer().hasPermission("mcmmo.perks.lucky.fishing")) {
            randomChance = (int) (randomChance * 0.75);
        }

        final int DROP_NUMBER = random.nextInt(randomChance);

        LivingEntity le = (LivingEntity) event.getCaught();
        EntityType type = le.getType();
        Location location = le.getLocation();

        switch (type) {
        case BLAZE:
            Misc.dropItem(location, new ItemStack(Material.BLAZE_ROD));
            break;

        case CAVE_SPIDER:
            if (DROP_NUMBER > 50) {
                Misc.dropItem(location, new ItemStack(Material.SPIDER_EYE));
            }
            else {
                Misc.dropItem(location, new ItemStack(Material.STRING));
            }
            break;

        case CHICKEN:
            if (DROP_NUMBER > 66) {
                Misc.dropItem(location, new ItemStack(Material.FEATHER));
            }
            else if (DROP_NUMBER > 33) {
                Misc.dropItem(location, new ItemStack(Material.RAW_CHICKEN));
                }
            else {
                Misc.dropItem(location, new ItemStack(Material.EGG));
            }
            break;

        case COW:
            if (DROP_NUMBER > 99) {
                Misc.dropItem(location, new ItemStack(Material.MILK_BUCKET));
            }
            else if (DROP_NUMBER > 50) {
                Misc.dropItem(location, new ItemStack(Material.LEATHER));
            }
            else {
                Misc.dropItem(location, new ItemStack(Material.RAW_BEEF));
            }
            break;

        case CREEPER:
            Misc.dropItem(location, new ItemStack(Material.SULPHUR));
            break;

        case ENDERMAN:
            Misc.dropItem(location, new ItemStack(Material.ENDER_PEARL));
            break;

        case GHAST:
            if (DROP_NUMBER > 50) {
                Misc.dropItem(location, new ItemStack(Material.SULPHUR));
            }
            else {
                Misc.dropItem(location, new ItemStack(Material.GHAST_TEAR));
            }
            break;

        case MAGMA_CUBE:
            Misc.dropItem(location, new ItemStack(Material.MAGMA_CREAM));
            break;

        case MUSHROOM_COW:
            if (DROP_NUMBER > 99) {
                Misc.dropItem(location, new ItemStack(Material.MILK_BUCKET));
            }
            else if (DROP_NUMBER > 98) {
                Misc.dropItem(location, new ItemStack(Material.MUSHROOM_SOUP));
            }
            else if (DROP_NUMBER > 66) {
                Misc.dropItem(location, new ItemStack(Material.LEATHER));
            }
            else if (DROP_NUMBER > 33) {
                Misc.dropItem(location, new ItemStack(Material.RAW_BEEF));
            }
            else {
                Misc.dropItems(location, new ItemStack(Material.RED_MUSHROOM), 3);
            }
            break;

        case PIG:
            Misc.dropItem(location, new ItemStack(Material.PORK));
            break;

        case PIG_ZOMBIE:
            if (DROP_NUMBER > 50) {
                Misc.dropItem(location, new ItemStack(Material.ROTTEN_FLESH));
            }
            else {
                Misc.dropItem(location, new ItemStack(Material.GOLD_NUGGET));
            }
            break;

        case SHEEP:
            Sheep sheep = (Sheep) le;

            if (!sheep.isSheared()) {
                Wool wool = new Wool();
                wool.setColor(sheep.getColor());

                ItemStack theWool = wool.toItemStack();
                theWool.setAmount(1 + random.nextInt(6));

                Misc.dropItem(location, theWool);
                sheep.setSheared(true);
            }
            break;

        case SKELETON:
            if (DROP_NUMBER > 50) {
                Misc.dropItem(location, new ItemStack(Material.BONE));
            }
            else {
                Misc.dropItems(location, new ItemStack(Material.ARROW), 3);
            }
            break;

        case SLIME:
            Misc.dropItem(location, new ItemStack(Material.SLIME_BALL));
            break;

        case SNOWMAN:
            if (DROP_NUMBER > 99) {
                Misc.dropItem(location, new ItemStack(Material.PUMPKIN));
            }
            else {
                Misc.dropItems(location, new ItemStack(Material.SNOW_BALL), 5);
            }
            break;

        case SPIDER:
            if (DROP_NUMBER > 50) {
                Misc.dropItem(location, new ItemStack(Material.SPIDER_EYE));
            }
            else {
                Misc.dropItem(location, new ItemStack(Material.STRING));
            }
            break;

        case SQUID:
            Misc.dropItem(location, new ItemStack(Material.INK_SACK, 1, (short) 0, (byte) 0x0));
            break;

        case ZOMBIE:
            Misc.dropItem(location, new ItemStack(Material.ROTTEN_FLESH));
            break;

        default:
            break;
        }

        Combat.dealDamage(le, 1);
    }
}
