package com.gmail.nossr50.skills.unarmed;

import static com.gmail.nossr50.util.random.ProbabilityUtil.isSkillRNGSuccessful;
import static org.bukkit.Material.INFESTED_STONE_BRICKS;
import static org.bukkit.Material.STONE_BRICKS;

import com.gmail.nossr50.api.ItemSpawnReason;
import com.gmail.nossr50.datatypes.interactions.NotificationType;
import com.gmail.nossr50.datatypes.player.McMMOPlayer;
import com.gmail.nossr50.datatypes.skills.PrimarySkillType;
import com.gmail.nossr50.datatypes.skills.SubSkillType;
import com.gmail.nossr50.datatypes.skills.SuperAbilityType;
import com.gmail.nossr50.datatypes.skills.ToolType;
import com.gmail.nossr50.mcMMO;
import com.gmail.nossr50.skills.SkillManager;
import com.gmail.nossr50.util.EventUtils;
import com.gmail.nossr50.util.ItemUtils;
import com.gmail.nossr50.util.MetadataConstants;
import com.gmail.nossr50.util.Misc;
import com.gmail.nossr50.util.Permissions;
import com.gmail.nossr50.util.player.NotificationManager;
import com.gmail.nossr50.util.player.UserManager;
import com.gmail.nossr50.util.random.ProbabilityUtil;
import com.gmail.nossr50.util.skills.RankUtils;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.entity.Item;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

public class UnarmedManager extends SkillManager {

    public UnarmedManager(McMMOPlayer mmoPlayer) {
        super(mmoPlayer, PrimarySkillType.UNARMED);
    }

    public boolean canActivateAbility() {
        return mmoPlayer.getToolPreparationMode(ToolType.FISTS) && Permissions.berserk(getPlayer());
    }

    public boolean canUseSteelArm() {
        if (!RankUtils.hasUnlockedSubskill(getPlayer(), SubSkillType.UNARMED_STEEL_ARM_STYLE)) {
            return false;
        }

        return Permissions.isSubSkillEnabled(getPlayer(), SubSkillType.UNARMED_STEEL_ARM_STYLE);
    }

    public boolean canUseBerserk() {
        return mmoPlayer.getAbilityMode(SuperAbilityType.BERSERK);
    }

    public boolean canDisarm(LivingEntity target) {
        if (!RankUtils.hasUnlockedSubskill(getPlayer(), SubSkillType.UNARMED_DISARM)) {
            return false;
        }

        return target instanceof Player
                && ((Player) target).getInventory().getItemInMainHand().getType() != Material.AIR
                && Permissions.isSubSkillEnabled(getPlayer(), SubSkillType.UNARMED_DISARM);
    }

    public boolean canDeflect() {
        if (!RankUtils.hasUnlockedSubskill(getPlayer(), SubSkillType.UNARMED_ARROW_DEFLECT)) {
            return false;
        }

        Player player = getPlayer();

        return ItemUtils.isUnarmed(player.getInventory().getItemInMainHand())
                && Permissions.isSubSkillEnabled(getPlayer(), SubSkillType.UNARMED_ARROW_DEFLECT);
    }

    public boolean canUseBlockCracker() {
        if (!RankUtils.hasUnlockedSubskill(getPlayer(), SubSkillType.UNARMED_BLOCK_CRACKER)) {
            return false;
        }

        return Permissions.isSubSkillEnabled(getPlayer(), SubSkillType.UNARMED_BLOCK_CRACKER);
    }

    public void blockCrackerCheck(@NotNull Block block) {
        if (!mcMMO.p.getGeneralConfig().isBlockCrackerAllowed()) {
            return;
        }
        if (!ProbabilityUtil.isNonRNGSkillActivationSuccessful(SubSkillType.UNARMED_BLOCK_CRACKER,
                mmoPlayer)) {
            return;
        }

        switch (block.getType()) {
            case STONE_BRICKS:
                block.setType(Material.CRACKED_STONE_BRICKS);
                return;
            case INFESTED_STONE_BRICKS:
                block.setType(Material.INFESTED_CRACKED_STONE_BRICKS);
                return;
            case DEEPSLATE_BRICKS:
                block.setType(Material.CRACKED_DEEPSLATE_BRICKS);
                return;
            case DEEPSLATE_TILES:
                block.setType(Material.CRACKED_DEEPSLATE_TILES);
                return;
            case POLISHED_BLACKSTONE_BRICKS:
                block.setType(Material.CRACKED_POLISHED_BLACKSTONE_BRICKS);
                return;
            case NETHER_BRICKS:
                block.setType(Material.CRACKED_NETHER_BRICKS);
                return;
            default:
        }
    }

    /**
     * Check for disarm.
     *
     * @param defender The defending player
     */
    public void disarmCheck(@NotNull Player defender) {
        if (isSkillRNGSuccessful(SubSkillType.UNARMED_DISARM, mmoPlayer,
                mmoPlayer.getAttackStrength())
                && !hasIronGrip(defender)) {
            if (EventUtils.callDisarmEvent(defender).isCancelled()) {
                return;
            }

            if (UserManager.getPlayer(defender) == null) {
                return;
            }

            final Item item = ItemUtils.spawnItem(getPlayer(), defender.getLocation(),
                    defender.getInventory().getItemInMainHand(),
                    ItemSpawnReason.UNARMED_DISARMED_ITEM);

            if (item != null && mcMMO.p.getAdvancedConfig().getDisarmProtected()) {
                item.setMetadata(MetadataConstants.METADATA_KEY_DISARMED_ITEM,
                        UserManager.getPlayer(defender).getPlayerMetadata());
            }

            defender.getInventory().setItemInMainHand(new ItemStack(Material.AIR));
            NotificationManager.sendPlayerInformation(defender, NotificationType.SUBSKILL_MESSAGE,
                    "Skills.Disarmed");
        }
    }

    /**
     * Check for arrow deflection.
     */
    public boolean deflectCheck() {
        if (isSkillRNGSuccessful(SubSkillType.UNARMED_ARROW_DEFLECT, mmoPlayer)) {
            NotificationManager.sendPlayerInformation(getPlayer(),
                    NotificationType.SUBSKILL_MESSAGE, "Combat.ArrowDeflect");
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
        damage =
                ((damage * Unarmed.berserkDamageModifier) * mmoPlayer.getAttackStrength()) - damage;

        return damage;
    }

    /**
     * Handle the effects of the Iron Arm ability
     */
    public double calculateSteelArmStyleDamage() {
        if (ProbabilityUtil.isNonRNGSkillActivationSuccessful(SubSkillType.UNARMED_STEEL_ARM_STYLE,
                mmoPlayer)) {
            return getSteelArmStyleDamage();
        }

        return 0;
    }

    public double getSteelArmStyleDamage() {
        double rank = RankUtils.getRank(getPlayer(), SubSkillType.UNARMED_STEEL_ARM_STYLE);

        double bonus = 0;

        if (rank >= 18) {
            bonus = 1 + rank - 18;
        }

        double finalBonus = bonus + 0.5 + (rank / 2);

        if (mcMMO.p.getAdvancedConfig().isSteelArmDamageCustom()) {
            return mcMMO.p.getAdvancedConfig().getSteelArmOverride(
                    RankUtils.getRank(getPlayer(), SubSkillType.UNARMED_STEEL_ARM_STYLE),
                    finalBonus);
        } else {
            return finalBonus;
        }
    }

    /**
     * Check Iron Grip ability success
     *
     * @param defender The defending player
     * @return true if the defender was not disarmed, false otherwise
     */
    private boolean hasIronGrip(@NotNull Player defender) {
        if (!Misc.isNPCEntityExcludingVillagers(defender)
                && Permissions.isSubSkillEnabled(defender, SubSkillType.UNARMED_IRON_GRIP)
                && isSkillRNGSuccessful(SubSkillType.UNARMED_IRON_GRIP,
                UserManager.getPlayer(defender))) {
            NotificationManager.sendPlayerInformation(defender, NotificationType.SUBSKILL_MESSAGE,
                    "Unarmed.Ability.IronGrip.Defender");
            NotificationManager.sendPlayerInformation(getPlayer(),
                    NotificationType.SUBSKILL_MESSAGE, "Unarmed.Ability.IronGrip.Attacker");

            return true;
        }

        return false;
    }
}
