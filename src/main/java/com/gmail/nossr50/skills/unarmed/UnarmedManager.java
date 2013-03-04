package com.gmail.nossr50.skills.unarmed;

import org.bukkit.Material;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import com.gmail.nossr50.mcMMO;
import com.gmail.nossr50.datatypes.player.McMMOPlayer;
import com.gmail.nossr50.datatypes.skills.AbilityType;
import com.gmail.nossr50.datatypes.skills.SkillType;
import com.gmail.nossr50.datatypes.skills.ToolType;
import com.gmail.nossr50.events.skills.unarmed.McMMOPlayerDisarmEvent;
import com.gmail.nossr50.locale.LocaleLoader;
import com.gmail.nossr50.skills.SkillManager;
import com.gmail.nossr50.util.Misc;
import com.gmail.nossr50.util.Permissions;
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

    /**
     * Check for disarm.
     *
     * @param defender The defending player
     */
    public void disarmCheck(Player defender) {
        if (SkillUtils.activationSuccessful(getSkillLevel(), getActivationChance(), Unarmed.disarmMaxChance, Unarmed.disarmMaxBonusLevel) && !hasIronGrip(defender)) {
            McMMOPlayerDisarmEvent disarmEvent = new McMMOPlayerDisarmEvent(defender);
            mcMMO.p.getServer().getPluginManager().callEvent(disarmEvent);

            if (!disarmEvent.isCancelled()) {
                Misc.dropItem(defender.getLocation(), defender.getItemInHand());

                defender.setItemInHand(new ItemStack(Material.AIR));
                defender.sendMessage(LocaleLoader.getString("Skills.Disarmed"));
            }
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

    public int berserkDamage(int damage) {
        return (int) (damage * Unarmed.berserkDamageModifier);
    }

    /**
     * Handle the effects of the Iron Arm ability
     *
     * @param damage The amount of damage initially dealt by the event
     * @return the modified event damage
     */
    public int ironArmCheck(int damage) {
        int unarmedBonus = Math.min(3 + (getSkillLevel() / Unarmed.ironArmIncreaseLevel), Unarmed.ironArmMaxBonusDamage);

        return damage + unarmedBonus;
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
