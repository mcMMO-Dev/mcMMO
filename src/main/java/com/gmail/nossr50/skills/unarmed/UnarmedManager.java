package com.gmail.nossr50.skills.unarmed;

import com.gmail.nossr50.config.AdvancedConfig;
import com.gmail.nossr50.datatypes.player.McMMOPlayer;
import com.gmail.nossr50.datatypes.skills.AbilityType;
import com.gmail.nossr50.datatypes.skills.SecondaryAbility;
import com.gmail.nossr50.datatypes.skills.SkillType;
import com.gmail.nossr50.datatypes.skills.ToolType;
import com.gmail.nossr50.locale.LocaleLoader;
import com.gmail.nossr50.mcMMO;
import com.gmail.nossr50.skills.SkillManager;
import com.gmail.nossr50.util.EventUtils;
import com.gmail.nossr50.util.ItemUtils;
import com.gmail.nossr50.util.Misc;
import com.gmail.nossr50.util.Permissions;
import com.gmail.nossr50.util.player.UserManager;
import com.gmail.nossr50.util.skills.SkillUtils;
import org.bukkit.Material;
import org.bukkit.block.BlockState;
import org.bukkit.entity.Item;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.block.data.BlockData;

public class UnarmedManager extends SkillManager {
    public UnarmedManager(McMMOPlayer mcMMOPlayer) {
        super(mcMMOPlayer, SkillType.UNARMED);
    }

    public boolean canActivateAbility() {
        return mcMMOPlayer.getToolPreparationMode(ToolType.FISTS) && Permissions.berserk(getPlayer());
    }

    public boolean canUseIronArm() {
        return Permissions.secondaryAbilityEnabled(getPlayer(), SecondaryAbility.IRON_ARM);
    }

    public boolean canUseBerserk() {
        return mcMMOPlayer.getAbilityMode(AbilityType.BERSERK) && Permissions.berserk(getPlayer());
    }

    public boolean canDisarm(LivingEntity target) {
        return target instanceof Player && ((Player) target).getInventory().getItemInMainHand().getType() != Material.AIR && Permissions.secondaryAbilityEnabled(getPlayer(), SecondaryAbility.DISARM);
    }

    public boolean canDeflect() {
        Player player = getPlayer();

        return ItemUtils.isUnarmed(player.getInventory().getItemInMainHand()) && Permissions.secondaryAbilityEnabled(getPlayer(), SecondaryAbility.DEFLECT);
    }

    public boolean canUseBlockCracker() {
        return Permissions.secondaryAbilityEnabled(getPlayer(), SecondaryAbility.BLOCK_CRACKER);
    }

    public boolean blockCrackerCheck(BlockState blockState) {
        if (!SkillUtils.activationSuccessful(SecondaryAbility.BLOCK_CRACKER, getPlayer())) {
            return false;
        }

        BlockData data = blockState.getBlockData();

        switch (blockState.getType()) {
            case STONE_BRICKS:
                if (!Unarmed.blockCrackerSmoothBrick) {
                    return false;
                }

                blockState.setType(Material.CRACKED_STONE_BRICKS);
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
        if (SkillUtils.activationSuccessful(SecondaryAbility.DISARM, getPlayer(), getSkillLevel(), activationChance) && !hasIronGrip(defender)) {
            if (EventUtils.callDisarmEvent(defender).isCancelled()) {
                return;
            }

            Item item = Misc.dropItem(defender.getLocation(), defender.getInventory().getItemInMainHand());

            if (item != null && AdvancedConfig.getInstance().getDisarmProtected()) {
                item.setMetadata(mcMMO.disarmedItemKey, UserManager.getPlayer(defender).getPlayerMetadata());
            }

            defender.getInventory().setItemInMainHand(new ItemStack(Material.AIR));
            defender.sendMessage(LocaleLoader.getString("Skills.Disarmed"));
        }
    }

    /**
     * Check for arrow deflection.
     */
    public boolean deflectCheck() {
        if (SkillUtils.activationSuccessful(SecondaryAbility.DEFLECT, getPlayer(), getSkillLevel(), activationChance)) {
            getPlayer().sendMessage(LocaleLoader.getString("Combat.ArrowDeflect"));
            return true;
        }

        return false;
    }

    /**
     * Handle the effects of the Berserk ability
     *
     * @param damage The amount of damage initially dealt by the event
     */
    public double berserkDamage(double damage) {
        damage = (damage * Unarmed.berserkDamageModifier) - damage;

        return damage;
    }

    /**
     * Handle the effects of the Iron Arm ability
     */
    public double ironArm() {
        if (!SkillUtils.activationSuccessful(SecondaryAbility.IRON_ARM, getPlayer())) {
            return 0;
        }

        return Math.min(Unarmed.ironArmMinBonusDamage + (getSkillLevel() / Unarmed.ironArmIncreaseLevel), Unarmed.ironArmMaxBonusDamage);
    }

    /**
     * Check Iron Grip ability success
     *
     * @param defender The defending player
     * @return true if the defender was not disarmed, false otherwise
     */
    private boolean hasIronGrip(Player defender) {
        if (!Misc.isNPCEntity(defender) && Permissions.secondaryAbilityEnabled(defender, SecondaryAbility.IRON_GRIP) && SkillUtils.activationSuccessful(SecondaryAbility.IRON_GRIP, defender, skill)) {
            defender.sendMessage(LocaleLoader.getString("Unarmed.Ability.IronGrip.Defender"));
            getPlayer().sendMessage(LocaleLoader.getString("Unarmed.Ability.IronGrip.Attacker"));

            return true;
        }

        return false;
    }
}
