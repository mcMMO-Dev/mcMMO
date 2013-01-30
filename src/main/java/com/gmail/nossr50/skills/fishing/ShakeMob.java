package com.gmail.nossr50.skills.fishing;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.bukkit.Material;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Sheep;
import org.bukkit.entity.Skeleton;
import org.bukkit.entity.Skeleton.SkeletonType;
import org.bukkit.inventory.ItemStack;
import org.bukkit.material.Wool;
import org.bukkit.potion.Potion;
import org.bukkit.potion.PotionType;

import com.gmail.nossr50.skills.fishing.Fishing.Tier;
import com.gmail.nossr50.skills.utilities.CombatTools;
import com.gmail.nossr50.util.Misc;
import com.gmail.nossr50.util.Permissions;

public final class ShakeMob {
    private ShakeMob() {}

    /**
     * Begins Tree Feller
     *
     * @param player Player using Shake Mob
     * @param mob Targeted entity
     * @param skillLevel Fishing level of the player
     */
    public static void process(Player player, LivingEntity mob, int skillLevel) {
        int activationChance = Misc.calculateActivationChance(Permissions.luckyFishing(player));

        if (getShakeProbability(skillLevel) <= Misc.getRandom().nextInt(activationChance)) {
            return;
        }

        Map<ItemStack, Integer> possibleDrops = new HashMap<ItemStack, Integer>();

        findPossibleDrops(mob, possibleDrops);

        if (possibleDrops.isEmpty()) {
            return;
        }

        ItemStack drop = chooseDrop(possibleDrops);

        // It's possible that chooseDrop returns null if the sum of probability in possibleDrops is inferior than 100
        if (drop == null) {
            return;
        }

        // Extra processing depending on the mob and drop type
        switch (mob.getType()) {
        case SHEEP:
            Sheep sheep = (Sheep) mob;

            if (drop.getType() == Material.WOOL) {
                if (sheep.isSheared()) {
                    return;
                }

                // TODO: Find a cleaner way to do this, maybe by using Sheep.getColor().getWoolData() (available since 1.4.7-R0.1)
                Wool wool = (Wool) drop.getData();

                wool.setColor(sheep.getColor());
                drop.setDurability(wool.getData());
                sheep.setSheared(true);
            }
            break;

        case SKELETON:
            Skeleton skeleton = (Skeleton) mob;

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

        default:
            break;
        }

        Misc.dropItem(mob.getLocation(), drop);
        CombatTools.dealDamage(mob, 1); // We may want to base the damage on the entity max health
    }

    /**
     * Finds the possible drops of an entity
     *
     * @param mob Targeted entity
     * @param possibleDrops List of ItemStack that can be dropped
     */
    private static void findPossibleDrops(LivingEntity mob, Map<ItemStack, Integer> possibleDrops) {
        switch (mob.getType()) {
        case BLAZE:
            possibleDrops.put(new ItemStack(Material.BLAZE_ROD), 100);
            break;
        case CAVE_SPIDER:
        case SPIDER:
            possibleDrops.put(new ItemStack(Material.SPIDER_EYE), 50);
            possibleDrops.put(new ItemStack(Material.STRING), 50);
            break;
        case CHICKEN:
            possibleDrops.put(new ItemStack(Material.FEATHER), 34);
            possibleDrops.put(new ItemStack(Material.RAW_CHICKEN), 33);
            possibleDrops.put(new ItemStack(Material.EGG), 33);
            break;
        case COW:
            possibleDrops.put(new ItemStack(Material.MILK_BUCKET), 2);
            possibleDrops.put(new ItemStack(Material.LEATHER), 49);
            possibleDrops.put(new ItemStack(Material.RAW_BEEF), 49);
            break;
        case CREEPER:
            possibleDrops.put(new ItemStack(Material.SKULL_ITEM, 1, (short) 4), 1);
            possibleDrops.put(new ItemStack(Material.SULPHUR), 99);
            break;
        case ENDERMAN:
            possibleDrops.put(new ItemStack(Material.ENDER_PEARL), 100);
            break;
        case GHAST:
            possibleDrops.put(new ItemStack(Material.SULPHUR), 50);
            possibleDrops.put(new ItemStack(Material.GHAST_TEAR), 50);
            break;
        case IRON_GOLEM:
            possibleDrops.put(new ItemStack(Material.PUMPKIN), 3);
            possibleDrops.put(new ItemStack(Material.IRON_INGOT), 12);
            possibleDrops.put(new ItemStack(Material.RED_ROSE), 85);
            break;
        case MAGMA_CUBE:
            possibleDrops.put(new ItemStack(Material.MAGMA_CREAM), 3);
            break;
        case MUSHROOM_COW:
            possibleDrops.put(new ItemStack(Material.MILK_BUCKET), 5);
            possibleDrops.put(new ItemStack(Material.MUSHROOM_SOUP), 5);
            possibleDrops.put(new ItemStack(Material.LEATHER), 30);
            possibleDrops.put(new ItemStack(Material.RAW_BEEF), 30);
            possibleDrops.put(new ItemStack(Material.RED_MUSHROOM, Misc.getRandom().nextInt(3) + 1), 30);
            break;
        case PIG:
            possibleDrops.put(new ItemStack(Material.PORK), 3);
            break;
        case PIG_ZOMBIE:
            possibleDrops.put(new ItemStack(Material.ROTTEN_FLESH), 50);
            possibleDrops.put(new ItemStack(Material.GOLD_NUGGET), 50);
            break;
        case SHEEP:
            possibleDrops.put(new ItemStack(Material.WOOL, Misc.getRandom().nextInt(6) + 1), 100);
            break;
        case SKELETON:
            possibleDrops.put(new ItemStack(Material.SKULL_ITEM, 1, (short) 0), 2);
            possibleDrops.put(new ItemStack(Material.BONE), 49);
            possibleDrops.put(new ItemStack(Material.ARROW, Misc.getRandom().nextInt(3) + 1), 49);
            break;
        case SLIME:
            possibleDrops.put(new ItemStack(Material.SLIME_BALL), 100);
            break;
        case SNOWMAN:
            possibleDrops.put(new ItemStack(Material.PUMPKIN), 3);
            possibleDrops.put(new ItemStack(Material.SNOW_BALL, Misc.getRandom().nextInt(4) + 1), 97);
            break;
        case SQUID:
            possibleDrops.put(new ItemStack(Material.INK_SACK), 100); // TODO: Add DyeColor.BLACK.getDyeData() to make it more explicit (available since 1.4.7-R0.1)
            break;
        case WITCH:
            possibleDrops.put(new Potion(PotionType.INSTANT_HEAL).toItemStack(1), 1);
            possibleDrops.put(new Potion(PotionType.FIRE_RESISTANCE).toItemStack(1), 1);
            possibleDrops.put(new Potion(PotionType.SPEED).toItemStack(1), 1);
            possibleDrops.put(new ItemStack(Material.GLASS_BOTTLE), 9);
            possibleDrops.put(new ItemStack(Material.GLOWSTONE_DUST), 13);
            possibleDrops.put(new ItemStack(Material.SULPHUR), 12);
            possibleDrops.put(new ItemStack(Material.REDSTONE), 13);
            possibleDrops.put(new ItemStack(Material.SPIDER_EYE), 12);
            possibleDrops.put(new ItemStack(Material.STICK), 13);
            possibleDrops.put(new ItemStack(Material.SUGAR), 12);
            possibleDrops.put(new ItemStack(Material.POTION), 13);
            break;
        case ZOMBIE:
            possibleDrops.put(new ItemStack(Material.SKULL_ITEM, 1, (short) 2), 2);
            possibleDrops.put(new ItemStack(Material.ROTTEN_FLESH), 98);
            break;
        default:
            return;
        }
    }

    /**
     * Randomly chooses a drop among the list
     *
     * @param possibleDrops List of ItemStack that can be dropped
     * @return Chosen ItemStack
     */
    private static ItemStack chooseDrop(Map<ItemStack, Integer> possibleDrops) {
        int dropProbability = Misc.getRandom().nextInt(100);
        int cumulatedProbability = 0;

        for (Entry<ItemStack, Integer> entry : possibleDrops.entrySet()) {
            cumulatedProbability += entry.getValue();

            if (dropProbability < cumulatedProbability) {
                return entry.getKey();
            }
        }

        return null;
    }

    /**
     * Gets the Shake Mob probability for a given skill level
     *
     * @param skillLevel Fishing skill level
     * @return Shake Mob probability
     */
    public static int getShakeProbability(int skillLevel) {
        for (Tier tier : Tier.values()) {
            if (skillLevel >= tier.getLevel()) {
                return tier.getShakeChance();
            }
        }

        return 0;
    }
}
