package com.gmail.nossr50.skills.gathering;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.DyeColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Item;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Sheep;
import org.bukkit.entity.Skeleton;
import org.bukkit.entity.Skeleton.SkeletonType;
import org.bukkit.event.player.PlayerFishEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.material.MaterialData;
import org.bukkit.material.Wool;
import org.bukkit.potion.Potion;
import org.bukkit.potion.PotionType;

import com.gmail.nossr50.config.AdvancedConfig;
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
    static AdvancedConfig advancedConfig = AdvancedConfig.getInstance();

    /**
     * Get the player's current fishing loot tier.
     * 
     * @param profile
     *            The profile of the player
     * @return the player's current fishing rank
     */
    public static int getFishingLootTier(PlayerProfile profile) {
        int level = profile.getSkillLevel(SkillType.FISHING);
        int fishingTier;

        if (level >= advancedConfig.getFishingTierLevelsTier5()) {
            fishingTier = 5;
        } else if (level >= advancedConfig.getFishingTierLevelsTier4()) {
            fishingTier = 4;
        } else if (level >= advancedConfig.getFishingTierLevelsTier3()) {
            fishingTier = 3;
        } else if (level >= advancedConfig.getFishingTierLevelsTier2()) {
            fishingTier = 2;
        } else {
            fishingTier = 1;
        }

        return fishingTier;
    }

    /**
     * Get item results from Fishing.
     * 
     * @param player
     *            The player that was fishing
     * @param event
     *            The event to modify
     */
    private static void getFishingResults(Player player, PlayerFishEvent event) {
        if (player == null)
            return;

        PlayerProfile profile = Users.getProfile(player);
        Item theCatch = (Item) event.getCaught();

        if (Config.getInstance().getFishingDropsEnabled() && Permissions.fishingTreasures(player)) {
            int skillLevel = profile.getSkillLevel(SkillType.FISHING);
            List<FishingTreasure> rewards = new ArrayList<FishingTreasure>();

            for (FishingTreasure treasure : TreasuresConfig.getInstance().fishingRewards) {
                if (treasure.getDropLevel() <= skillLevel && treasure.getMaxLevel() >= skillLevel) {
                    rewards.add(treasure);
                }
            }

            if (rewards.size() <= 0) {
                return;
            }

            FishingTreasure foundTreasure = rewards.get(Misc.getRandom().nextInt(rewards.size()));

            int randomChance = 100;
            if (Permissions.luckyFishing(player)) {
                randomChance = (int) (randomChance * 0.75);
            }

            if (Misc.getRandom().nextDouble() * randomChance <= foundTreasure.getDropChance()) {
                Users.getPlayer(player).addXP(SkillType.FISHING, foundTreasure.getXp());
                theCatch.setItemStack(foundTreasure.getDrop());
            }
        }
        else {
            theCatch.setItemStack(new ItemStack(Material.RAW_FISH));
        }

        short maxDurability = theCatch.getItemStack().getType().getMaxDurability();

        if (maxDurability > 0) {
            theCatch.getItemStack().setDurability((short) (Misc.getRandom().nextInt(maxDurability))); // Change durability to random value
        }

        Skills.xpProcessing(player, profile, SkillType.FISHING, Config.getInstance().getFishingBaseXP());
    }

    /**
     * Process results from Fishing.
     * 
     * @param event
     *            The event to modify
     */
    public static void processResults(PlayerFishEvent event) {
        Player player = event.getPlayer();
        if (player == null)
            return;

        PlayerProfile profile = Users.getProfile(player);

        getFishingResults(player, event);
        Item theCatch = (Item) event.getCaught();

        if (theCatch.getItemStack().getType() != Material.RAW_FISH) {
            int lootTier = Fishing.getFishingLootTier(profile);
            int magicHunterMultiplier = advancedConfig.getFishingMagicMultiplier();
            int specificChance = 1;
            boolean enchanted = false;
            ItemStack fishingResults = theCatch.getItemStack();

            player.sendMessage(LocaleLoader.getString("Fishing.ItemFound"));

            if (ItemChecks.isEnchantable(fishingResults)) {
                int randomChance = 100;

                if (Permissions.luckyFishing(player)) {
                    randomChance = (int) (randomChance * 0.75);
                }

                if (player.getWorld().hasStorm()) {
                    randomChance = (int) (randomChance * 0.909);
                }

                /* CHANCE OF ITEM BEING ENCHANTED
                 * 5% - Tier 1
                 * 10% - Tier 2
                 * 15% - Tier 3
                 * 20% - Tier 4
                 * 25% - Tier 5
                 */
                if (Misc.getRandom().nextInt(randomChance) <= (lootTier * magicHunterMultiplier) && Permissions.fishingMagic(player)) {
                    for (Enchantment newEnchant : Enchantment.values()) {
                        if (newEnchant.canEnchantItem(fishingResults)) {
                            specificChance++;

                            for (Enchantment oldEnchant : fishingResults.getEnchantments().keySet()) {
                                if (oldEnchant.conflictsWith(newEnchant))
                                    specificChance--;
                                    continue;
                            }

                            /* CHANCE OF GETTING EACH ENCHANTMENT
                             * 50% - 1st Enchantment
                             * 33% - 2nd Enchantment
                             * 25% - 3rd Enchantment
                             * 20% - 4th Enchantment
                             * 16.66% - 5th Enchantment
                             * 14.29% - 6th Enchantment
                             * 12.5% - 7th Enchantment
                             * 11.11% - 8th Enchantment
                             */
                            if (Misc.getRandom().nextInt(specificChance) < 1) {
                                enchanted = true;
                                int randomEnchantLevel = Misc.getRandom().nextInt(newEnchant.getMaxLevel()) + 1;

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
     * @param event
     *            The event to modify
     */
    public static void shakeMob(PlayerFishEvent event) {
        int randomChance = 100;

        if (Permissions.luckyFishing(event.getPlayer())) {
            randomChance = (int) (randomChance * 1.25);
        }

        final Player player = event.getPlayer();
        final PlayerProfile profile = Users.getProfile(player);
        int lootTier = getFishingLootTier(profile);

        int dropChance = getShakeChance(lootTier);

        if (Permissions.luckyFishing(player)) {
            // With lucky perk on max level tier, its 100%
            dropChance = (int) (dropChance * 1.25);
        }

        final int DROP_CHANCE = Misc.getRandom().nextInt(100);
        final int DROP_NUMBER = Misc.getRandom().nextInt(randomChance) + 1;

        LivingEntity le = (LivingEntity) event.getCaught();
        EntityType type = le.getType();
        Location location = le.getLocation();

        if (DROP_CHANCE < dropChance) {

            switch (type) {
            case BLAZE:
                Misc.dropItem(location, new ItemStack(Material.BLAZE_ROD));
                break;

            case CAVE_SPIDER:
                if (DROP_NUMBER > 50) {
                    Misc.dropItem(location, new ItemStack(Material.SPIDER_EYE));
                } else {
                    Misc.dropItem(location, new ItemStack(Material.STRING));
                }
                break;

            case CHICKEN:
                if (DROP_NUMBER > 66) {
                    Misc.dropItem(location, new ItemStack(Material.FEATHER));
                } else if (DROP_NUMBER > 33) {
                    Misc.dropItem(location, new ItemStack(Material.RAW_CHICKEN));
                } else {
                    Misc.dropItem(location, new ItemStack(Material.EGG));
                }
                break;

            case COW:
                if (DROP_NUMBER > 95) {
                    Misc.dropItem(location, new ItemStack(Material.MILK_BUCKET));
                } else if (DROP_NUMBER > 50) {
                    Misc.dropItem(location, new ItemStack(Material.LEATHER));
                } else {
                    Misc.dropItem(location, new ItemStack(Material.RAW_BEEF));
                }
                break;

            case CREEPER:
                if (DROP_NUMBER > 97) {
                    Misc.dropItem(location, new ItemStack(Material.SKULL_ITEM, 1, (short) 4));
                } else {
                    Misc.dropItem(location, new ItemStack(Material.SULPHUR));
                }
                break;

            case ENDERMAN:
                Misc.dropItem(location, new ItemStack(Material.ENDER_PEARL));
                break;

            case GHAST:
                if (DROP_NUMBER > 50) {
                    Misc.dropItem(location, new ItemStack(Material.SULPHUR));
                } else {
                    Misc.dropItem(location, new ItemStack(Material.GHAST_TEAR));
                }
                break;

            case IRON_GOLEM:
                if (DROP_NUMBER > 97) {
                    Misc.dropItem(location, new ItemStack(Material.PUMPKIN));
                } else if (DROP_NUMBER > 85) {
                    Misc.dropItem(location, new ItemStack(Material.IRON_INGOT));
                } else {
                    Misc.dropItem(location, new ItemStack(Material.RED_ROSE));
                }
                break;

            case MAGMA_CUBE:
                Misc.dropItem(location, new ItemStack(Material.MAGMA_CREAM));
                break;

            case MUSHROOM_COW:
                if (DROP_NUMBER > 95) {
                    Misc.dropItem(location, new ItemStack(Material.MILK_BUCKET));
                } else if (DROP_NUMBER > 90) {
                    Misc.dropItem(location, new ItemStack(Material.MUSHROOM_SOUP));
                } else if (DROP_NUMBER > 60) {
                    Misc.dropItem(location, new ItemStack(Material.LEATHER));
                } else if (DROP_NUMBER > 30) {
                    Misc.dropItem(location, new ItemStack(Material.RAW_BEEF));
                } else {
                    Misc.dropItem(location, new ItemStack(Material.RED_MUSHROOM));
                    Misc.randomDropItems(location, new ItemStack(Material.RED_MUSHROOM), 50, 2);
                }
                break;

            case PIG:
                Misc.dropItem(location, new ItemStack(Material.PORK));
                break;

            case PIG_ZOMBIE:
                if (DROP_NUMBER > 50) {
                    Misc.dropItem(location, new ItemStack(Material.ROTTEN_FLESH));
                } else {
                    Misc.dropItem(location, new ItemStack(Material.GOLD_NUGGET));
                }
                break;

            case SHEEP:
                final Sheep sheep = (Sheep) le;

                if (!sheep.isSheared()) {
                    final Wool wool = new Wool();
                    wool.setColor(sheep.getColor());

                    final ItemStack theWool = wool.toItemStack();
                    theWool.setAmount(1 + Misc.getRandom().nextInt(6));

                    Misc.dropItem(location, theWool);
                    sheep.setSheared(true);
                }
                break;

            case SKELETON:
                if (((Skeleton) le).getSkeletonType() == SkeletonType.WITHER) {
                    if (DROP_NUMBER > 97) {
                        Misc.dropItem(location, new ItemStack(Material.SKULL_ITEM, 1, (short) 1));
                    } else if (DROP_NUMBER > 50) {
                        Misc.dropItem(location, new ItemStack(Material.BONE));
                    } else {
                        Misc.dropItem(location, new ItemStack(Material.COAL));
                        Misc.randomDropItems(location, new ItemStack(Material.COAL), 50, 2);
                    }
                } else {
                    if (DROP_NUMBER > 97) {
                        Misc.dropItem(location, new ItemStack(Material.SKULL_ITEM));
                    } else if (DROP_NUMBER > 50) {
                        Misc.dropItem(location, new ItemStack(Material.BONE));
                    } else {
                        Misc.dropItem(location, new ItemStack(Material.ARROW));
                        Misc.randomDropItems(location, new ItemStack(Material.ARROW), 50, 2);
                    }
                }
                break;

            case SLIME:
                Misc.dropItem(location, new ItemStack(Material.SLIME_BALL));
                break;

            case SNOWMAN:
                if (DROP_NUMBER > 97) {
                    Misc.dropItem(location, new ItemStack(Material.PUMPKIN));
                } else {
                    Misc.dropItem(location, new ItemStack(Material.SNOW_BALL));
                    Misc.randomDropItems(location, new ItemStack(Material.SNOW_BALL), 50, 4);
                }
                break;

            case SPIDER:
                if (DROP_NUMBER > 50) {
                    Misc.dropItem(location, new ItemStack(Material.SPIDER_EYE));
                } else {
                    Misc.dropItem(location, new ItemStack(Material.STRING));
                }
                break;

            case SQUID:
                ItemStack item;
                try {
                    item = (new MaterialData(Material.INK_SACK, DyeColor.BLACK.getDyeData())).toItemStack(1);
                }
                catch(Exception e) {
                    item = (new MaterialData(Material.INK_SACK, (byte) 0)).toItemStack(1);
                }
                catch(NoSuchMethodError e) {
                    item = (new MaterialData(Material.INK_SACK, (byte) 0)).toItemStack(1);
                }

                Misc.dropItem(location, item);
                break;

            case WITCH:
                final int DROP_NUMBER_2 = Misc.getRandom().nextInt(randomChance) + 1;
                if (DROP_NUMBER > 95) {
                    if (DROP_NUMBER_2 > 66) {
                        Misc.dropItem(location, new Potion(PotionType.INSTANT_HEAL).toItemStack(1));
                    } else if (DROP_NUMBER_2 > 33) {
                        Misc.dropItem(location, new Potion(PotionType.FIRE_RESISTANCE).toItemStack(1));
                    } else {
                        Misc.dropItem(location, new Potion(PotionType.SPEED).toItemStack(1));
                    }
                } else {
                    if (DROP_NUMBER_2 > 88) {
                        Misc.dropItem(location, new ItemStack(Material.GLASS_BOTTLE));
                    } else if (DROP_NUMBER_2 > 75) {
                        Misc.dropItem(location, new ItemStack(Material.GLOWSTONE_DUST));
                    } else if (DROP_NUMBER_2 > 63) {
                        Misc.dropItem(location, new ItemStack(Material.SULPHUR));
                    } else if (DROP_NUMBER_2 > 50) {
                        Misc.dropItem(location, new ItemStack(Material.REDSTONE));
                    } else if (DROP_NUMBER_2 > 38) {
                        Misc.dropItem(location, new ItemStack(Material.SPIDER_EYE));
                    } else if (DROP_NUMBER_2 > 25) {
                        Misc.dropItem(location, new ItemStack(Material.STICK));
                    } else if (DROP_NUMBER_2 > 13) {
                        Misc.dropItem(location, new ItemStack(Material.SUGAR));
                    } else {
                        Misc.dropItem(location, new ItemStack(Material.POTION));
                    }
                }
                break;

            case ZOMBIE:
                if (DROP_NUMBER > 97) {
                    Misc.dropItem(location, new ItemStack(Material.SKULL_ITEM, 1, (short) 2));
                } else {
                    Misc.dropItem(location, new ItemStack(Material.ROTTEN_FLESH));
                }
                break;

            default:
                break;
            }
        }

        Combat.dealDamage(le, 1);
    }

    /**
     * Gets chance of shake success.
     * 
     * @param rank
     *            Treasure hunter rank
     * @return The chance of a successful shake
     */
    public static int getShakeChance(int lootTier) {
        switch (lootTier) {
        case 1:
            return advancedConfig.getShakeChanceRank1();

        case 2:
            return advancedConfig.getShakeChanceRank2();

        case 3:
            return advancedConfig.getShakeChanceRank3();

        case 4:
            return advancedConfig.getShakeChanceRank4();

        case 5:
            return advancedConfig.getShakeChanceRank5();

        default:
            return 10;
        }
    }
}
