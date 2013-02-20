package com.gmail.nossr50.skills.axes;

import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.inventory.EntityEquipment;
import org.bukkit.inventory.ItemStack;

import com.gmail.nossr50.locale.LocaleLoader;
import com.gmail.nossr50.mods.ModChecks;
import com.gmail.nossr50.util.ItemChecks;
import com.gmail.nossr50.util.Misc;
import com.gmail.nossr50.util.ParticleEffectUtils;
import com.gmail.nossr50.util.Permissions;
import com.gmail.nossr50.util.Users;

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
                hasArmor = true;

                if (Misc.getRandom().nextInt(100) < 25) {
                    damageArmor(itemStack);
                }
            }
        }

        return hasArmor;
    }

    private void damageArmor(ItemStack armor) {
        // Modifier simulate the durability enchantment behavior
        float modifier = 1;

        if (armor.containsEnchantment(Enchantment.DURABILITY)) {
            modifier /= armor.getEnchantmentLevel(Enchantment.DURABILITY) + 1;
        }

        float modifiedDurabilityDamage = durabilityDamage * modifier;
        short maxDurabilityDamage = ModChecks.isCustomArmor(armor) ? ModChecks.getArmorFromItemStack(armor).getDurability() : armor.getType().getMaxDurability();
        maxDurabilityDamage *= Axes.impactMaxDurabilityDamageModifier;

        if (modifiedDurabilityDamage > maxDurabilityDamage) {
            modifiedDurabilityDamage = maxDurabilityDamage;
        }

        armor.setDurability((short) (modifiedDurabilityDamage + armor.getDurability()));
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

        ParticleEffectUtils.playGreaterImpactEffect(defender);
        defender.setVelocity(player.getLocation().getDirection().normalize().multiply(Axes.greaterImpactKnockbackMultiplier));
    }

    private void sendAbilityMessge() {
        if (manager.getMcMMOPlayer().getProfile().useChatNotifications()) {
            player.sendMessage(LocaleLoader.getString("Axes.Combat.GI.Proc"));
        }

        if (defender instanceof Player) {
            Player defendingPlayer = (Player) defender;

            if (Users.getPlayer(defendingPlayer).getProfile().useChatNotifications()) {
                defendingPlayer.sendMessage(LocaleLoader.getString("Axes.Combat.GI.Struck"));
            }
        }
    }
}
