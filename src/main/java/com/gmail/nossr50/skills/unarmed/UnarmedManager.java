package com.gmail.nossr50.skills.unarmed;

import org.bukkit.Material;
import org.bukkit.block.BlockState;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.material.MaterialData;
import org.bukkit.material.SmoothBrick;

import com.gmail.nossr50.datatypes.player.McMMOPlayer;
import com.gmail.nossr50.datatypes.skills.AbilityType;
import com.gmail.nossr50.datatypes.skills.SkillType;
import com.gmail.nossr50.datatypes.skills.ToolType;
import com.gmail.nossr50.locale.LocaleLoader;
import com.gmail.nossr50.skills.SkillManager;
import com.gmail.nossr50.util.EventUtils;
import com.gmail.nossr50.util.Misc;
import com.gmail.nossr50.util.Permissions;
import com.gmail.nossr50.util.skills.CombatUtils;
import com.gmail.nossr50.util.skills.SkillUtils;

public class UnarmedManager extends SkillManager {
    public UnarmedManager(McMMOPlayer mcMMOPlayer) {
        super(mcMMOPlayer, SkillType.UNARMED);
    }

    public boolean canActivateAbility() {
        return mcMMOPlayer.getToolPreparationMode(ToolType.FISTS) && Permissions.berserk(getPlayer());
    }

    public boolean canUseIronArm() {
        return Permissions.bonusDamage(getPlayer(), skill);
    }

    public boolean canUseBerserk() {
        return mcMMOPlayer.getAbilityMode(AbilityType.BERSERK) && Permissions.berserk(getPlayer());
    }

    public boolean canDisarm(LivingEntity target) {
        return target instanceof Player && ((Player) target).getItemInHand().getType() != Material.AIR && Permissions.disarm(getPlayer());
    }

    public boolean canDeflect() {
        Player player = getPlayer();

        return player.getItemInHand().getType() == Material.AIR && Permissions.arrowDeflect(player);
    }

    public boolean canUseBlockCracker() {
        return Permissions.blockCracker(getPlayer());
    }

    public boolean blockCrackerCheck(BlockState blockState) {
        MaterialData data = blockState.getData();

        switch (blockState.getType()) {
            case SMOOTH_BRICK:
                if (!Unarmed.blockCrackerSmoothBrick) {
                    return false;
                }

                // Yes, this is awkward, but it's the *right* way to do it.
                SmoothBrick smoothBrick = (SmoothBrick) data;

                if (smoothBrick.getMaterial() != Material.STONE) {
                    return false;
                }

                smoothBrick.setMaterial(Material.COBBLESTONE);
                return true;

            default:
                return false;
        }
    }

    /**
     * Check for disarm.
     *
     * @param defender The defending player
     */
    public void disarmCheck(Player defender) {
        if (SkillUtils.activationSuccessful(getSkillLevel(), getActivationChance(), Unarmed.disarmMaxChance, Unarmed.disarmMaxBonusLevel) && !hasIronGrip(defender)) {
            if (EventUtils.callDisarmEvent(defender).isCancelled()) {
                return;
            }

            Misc.dropItem(defender.getLocation(), defender.getItemInHand());

            defender.setItemInHand(new ItemStack(Material.AIR));
            defender.sendMessage(LocaleLoader.getString("Skills.Disarmed"));
        }
    }

    /**
     * Check for arrow deflection.
     */
    public boolean deflectCheck() {
        if (SkillUtils.activationSuccessful(getSkillLevel(), getActivationChance(), Unarmed.deflectMaxChance, Unarmed.deflectMaxBonusLevel)) {
            getPlayer().sendMessage(LocaleLoader.getString("Combat.ArrowDeflect"));
            return true;
        }

        return false;
    }

    /**
     * Handle the effects of the Berserk ability
     *
     * @param target The {@link LivingEntity} being affected by the ability
     * @param damage The amount of damage initially dealt by the event
     */
    public double berserkDamage(LivingEntity target, double damage) {
        damage = (damage * Unarmed.berserkDamageModifier) - damage;

        return CombatUtils.callFakeDamageEvent(getPlayer(), target, damage);
    }

    /**
     * Handle the effects of the Iron Arm ability
     *
     * @param target The {@link LivingEntity} being affected by the ability
     */
    public double ironArm(LivingEntity target) {
        double unarmedBonus = Math.min(Unarmed.ironArmMinBonusDamage + (getSkillLevel() / Unarmed.ironArmIncreaseLevel), Unarmed.ironArmMaxBonusDamage);

        return CombatUtils.callFakeDamageEvent(getPlayer(), target, unarmedBonus);
    }

    /**
     * Check Iron Grip ability success
     *
     * @param defender The defending player
     * @return true if the defender was not disarmed, false otherwise
     */
    private boolean hasIronGrip(Player defender) {
        if (!Misc.isNPCEntity(defender) && Permissions.ironGrip(defender) && SkillUtils.activationSuccessful(defender, skill, Unarmed.ironGripMaxChance, Unarmed.ironGripMaxBonusLevel)) {
            defender.sendMessage(LocaleLoader.getString("Unarmed.Ability.IronGrip.Defender"));
            getPlayer().sendMessage(LocaleLoader.getString("Unarmed.Ability.IronGrip.Attacker"));

            return true;
        }

        return false;
    }
}
