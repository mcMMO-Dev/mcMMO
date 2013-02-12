package com.gmail.nossr50.skills.axes;

import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.inventory.EntityEquipment;
import org.bukkit.inventory.ItemStack;

import com.gmail.nossr50.locale.LocaleLoader;
import com.gmail.nossr50.util.ItemChecks;
import com.gmail.nossr50.util.Misc;
import com.gmail.nossr50.util.Permissions;

public class ImpactEventHandler {
    private AxeManager manager;
    private Player player;
    private EntityDamageByEntityEvent event;
    private short durabilityDamage = 1;
    private EntityEquipment entityEquipment;
    protected LivingEntity defender;
    boolean impactApplied;

    public ImpactEventHandler(AxeManager manager, EntityDamageByEntityEvent event, LivingEntity defender) {
        this.manager = manager;
        this.player = manager.getMcMMOPlayer().getPlayer();
        this.event = event;
        this.defender = defender;
        this.entityEquipment = defender.getEquipment();
    }

    protected boolean applyImpact() {
        // Every 50 Skill Levels you gain 1 durability damage (default values)
        durabilityDamage += (short) (manager.getSkillLevel() / Axes.impactIncreaseLevel);
        // getArmorContents.length can't be used because it's always equal to 4 (no armor = air block)
        boolean hasArmor = false;

        for (ItemStack itemStack : entityEquipment.getArmorContents()) {
            if (ItemChecks.isArmor(itemStack)) {
                damageArmor(itemStack);
                hasArmor = true;
            }
        }

        return hasArmor;
    }

    private void damageArmor(ItemStack armor) {
        if (Misc.getRandom().nextInt(100) >= 25) {
            return;
        }

        float modifier = 1;

        if (armor.containsEnchantment(Enchantment.DURABILITY)) {
            modifier /= armor.getEnchantmentLevel(Enchantment.DURABILITY) + 1;
        }

        armor.setDurability((short) (durabilityDamage * modifier + armor.getDurability()));
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
}
