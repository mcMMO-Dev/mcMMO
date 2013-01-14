package com.gmail.nossr50.skills.axes;

import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

import com.gmail.nossr50.config.AdvancedConfig;
import com.gmail.nossr50.datatypes.SkillType;
import com.gmail.nossr50.locale.LocaleLoader;
import com.gmail.nossr50.util.Misc;
import com.gmail.nossr50.util.Permissions;
import com.gmail.nossr50.util.Users;

public class Axes {
    public static int bonusDamageMaxBonus = AdvancedConfig.getInstance().getBonusDamageAxesBonusMax();
    public static int bonusDamageMaxBonusLevel = AdvancedConfig.getInstance().getBonusDamageAxesMaxBonusLevel();

    public static int criticalHitMaxBonusLevel = AdvancedConfig.getInstance().getAxesCriticalMaxBonusLevel();
    public static double criticalHitMaxChance = AdvancedConfig.getInstance().getAxesCriticalChance();
    public static double criticalHitPVPModifier = AdvancedConfig.getInstance().getAxesCriticalPVPModifier();
    public static double criticalHitPVEModifier = AdvancedConfig.getInstance().getAxesCriticalPVEModifier();

    static AdvancedConfig advancedConfig = AdvancedConfig.getInstance();

    /**
     * Check for Impact ability.
     *
     * @param attacker The attacking player
     * @param target The defending entity
     * @param event The event to modify
     */
    public static void impact(Player attacker, LivingEntity target, EntityDamageByEntityEvent event) {
        if (attacker == null)
            return;

        /*
         * TODO: Finish this skill. The idea is you will greatly damage an opponents armor.
         * When they are unarmored, you have a proc that will stun them and deal additional damage.
         */
        if (target instanceof Player) {
            Player targetPlayer = (Player) target;
            short durabilityDamage = 1; //Start with 1 durability damage

            /* Every 50 Skill Levels you gain 1 durability damage (default values) */
            int impactIncreaseLevel = advancedConfig.getArmorImpactIncreaseLevel();
            double impactMaxDamage = advancedConfig.getArmorImpactMaxDurabilityDamage() / 100F;
            short maxDurability;
            durabilityDamage += (int) ((double) Users.getProfile(attacker).getSkillLevel(SkillType.AXES) / (double) impactIncreaseLevel);

            if (!hasArmor(targetPlayer)) {
                applyGreaterImpact(attacker, target, event);
            }
            else {
                for (ItemStack armor : targetPlayer.getInventory().getArmorContents()) {
                    if (Math.random() * 100 > 75) {
                    	int lowerdamage = 0;
                    	for (int i = 0; i <= durabilityDamage; i ++) {
                    		if (armor.containsEnchantment(Enchantment.DURABILITY)) {
                        		int level = armor.getEnchantmentLevel(Enchantment.DURABILITY);
                        		if (Misc.getRandom().nextInt(level + 1) > 0) {
                        			lowerdamage++;
                        		}
                        	}	
                    	}
                    	int newDurabilityDamage = durabilityDamage - lowerdamage;
                        maxDurability = (short) (armor.getType().getMaxDurability() * impactMaxDamage);
                    	if (newDurabilityDamage > maxDurability) newDurabilityDamage = maxDurability;
                        armor.setDurability((short) (armor.getDurability() + newDurabilityDamage)); //Damage armor piece
                    }
                }
                targetPlayer.updateInventory();
            }
        }
        else {
            applyGreaterImpact(attacker, target, event); //Since mobs are technically unarmored, this will always trigger
        }
    }

    /**
     * Apply Greater Impact ability.
     *
     * @param attacker The attacking player
     * @param target The defending entity
     * @param event The event to modify
     */
    private static void applyGreaterImpact(Player attacker, LivingEntity target, EntityDamageByEntityEvent event) {
        if (attacker == null)
            return;

        final double GREATER_IMPACT_CHANCE = advancedConfig.getGreaterImpactChance();
        final double GREATER_IMPACT_MULTIPLIER = advancedConfig.getGreaterImpactModifier();
        final int GREATER_IMPACT_DAMAGE = advancedConfig.getGreaterImpactBonusDamage();

        if (!Permissions.greaterImpact(attacker)) {
            return;
        }

        int randomChance = 100;

        if (Permissions.luckyAxes(attacker)) {
            randomChance = (int) (randomChance * 0.75);
        }

        if (Misc.getRandom().nextInt(randomChance) <= GREATER_IMPACT_CHANCE) {
            event.setDamage(event.getDamage() + GREATER_IMPACT_DAMAGE);
            target.setVelocity(attacker.getLocation().getDirection().normalize().multiply(GREATER_IMPACT_MULTIPLIER));
            attacker.sendMessage(LocaleLoader.getString("Axes.Combat.GI.Proc"));
        }
    }

    /**
     * Check if a player has armor.
     *
     * @param player Player whose armor to check
     * @return true if the player has armor, false otherwise
     */
    private static boolean hasArmor(Player player) {
        if (player == null)
            return false;

        PlayerInventory inventory = player.getInventory();

        if (inventory.getBoots() != null || inventory.getChestplate() != null || inventory.getHelmet() != null || inventory.getLeggings() != null) {
            return true;
        }

        return false;
    }
}
