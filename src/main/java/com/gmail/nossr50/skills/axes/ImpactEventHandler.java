package com.gmail.nossr50.skills.axes;

import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.inventory.EntityEquipment;
import org.bukkit.inventory.ItemStack;

import com.gmail.nossr50.locale.LocaleLoader;
import com.gmail.nossr50.util.Misc;
import com.gmail.nossr50.util.Permissions;

public class ImpactEventHandler {
    private AxeManager manager;
    private Player player;
    private EntityDamageByEntityEvent event;
    private short durabilityDamage = 1;
    private EntityEquipment equipment;
    private ItemStack[] armorContents;

    protected LivingEntity defender;

    public ImpactEventHandler(AxeManager manager, EntityDamageByEntityEvent event, LivingEntity defender) {
        this.manager = manager;
        this.player = manager.getPlayer();
        this.event = event;
        this.defender = defender;
        this.equipment = defender.getEquipment();
        this.armorContents = equipment.getArmorContents();
    }

    protected void damageArmor() {
        /* Every 50 Skill Levels you gain 1 durability damage (default values) */
        durabilityDamage += (short) (manager.getSkillLevel() / Axes.impactIncreaseLevel);

        for (ItemStack armor : armorContents) {
            if (Misc.getRandom().nextInt(100) > 75) {

                for (int i = 0; i <= durabilityDamage; i++) {
                    if (armor.containsEnchantment(Enchantment.DURABILITY)) {
                        handleDurabilityEnchantment(armor);
                    }
                }

                damageValidation(armor);
                armor.setDurability((short) (armor.getDurability() + durabilityDamage));
            }
        }

        equipment.setArmorContents(armorContents);
    }

    protected void applyGreaterImpact() {
        if (!Permissions.greaterImpact(player)) {
            return;
        }

        if (Misc.getRandom().nextInt(manager.getActivationChance()) <= Axes.greaterImpactChance) {
            handleGreaterImpactEffect();
            sendAbilityMessge();
        }
    }

    private void handleGreaterImpactEffect() {
        event.setDamage(event.getDamage() + Axes.greaterImpactBonusDamage);
        defender.setVelocity(player.getLocation().getDirection().normalize().multiply(Axes.greaterImpactKnockbackMultiplier));
    }

    private void sendAbilityMessge() {
        player.sendMessage(LocaleLoader.getString("Axes.Combat.GI.Proc"));

        if (defender instanceof Player) {
            ((Player) defender).sendMessage(LocaleLoader.getString("Axes.Combat.GI.Struck"));
        }
    }

    private void handleDurabilityEnchantment(ItemStack armor) {
        int enchantmentLevel = armor.getEnchantmentLevel(Enchantment.DURABILITY);

        if (Misc.getRandom().nextInt(enchantmentLevel + 1) > 0) {
            durabilityDamage--;
        }
    }

    private void damageValidation(ItemStack armor) {
        short maxDurability = (short) (armor.getType().getMaxDurability() * Axes.impactMaxDurabilityDamageModifier);

        if (durabilityDamage > maxDurability) {
            durabilityDamage = maxDurability;
        }
    }
}
