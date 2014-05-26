package com.gmail.nossr50.commands.skills;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Material;
import org.bukkit.entity.Player;

import com.gmail.nossr50.mcMMO;
import com.gmail.nossr50.datatypes.skills.MaterialType;
import com.gmail.nossr50.datatypes.skills.SecondaryAbility;
import com.gmail.nossr50.datatypes.skills.SkillType;
import com.gmail.nossr50.locale.LocaleLoader;
import com.gmail.nossr50.skills.repair.ArcaneForging;
import com.gmail.nossr50.skills.repair.ArcaneForging.Tier;
import com.gmail.nossr50.skills.repair.Repair;
import com.gmail.nossr50.skills.repair.RepairManager;
import com.gmail.nossr50.skills.repair.repairables.Repairable;
import com.gmail.nossr50.util.Permissions;
import com.gmail.nossr50.util.player.UserManager;

public class RepairCommand extends SkillCommand {
    private String repairMasteryBonus;
    private String superRepairChance;
    private String superRepairChanceLucky;

    private boolean canSuperRepair;
    private boolean canMasterRepair;
    private boolean canArcaneForge;
    private boolean canRepairStone;
    private boolean canRepairIron;
    private boolean canRepairGold;
    private boolean canRepairDiamond;
    private boolean canRepairString;
    private boolean canRepairLeather;
    private boolean canRepairWood;
    private boolean arcaneBypass;

    private int diamondLevel;
    private int goldLevel;
    private int ironLevel;
    private int stoneLevel;

    public RepairCommand() {
        super(SkillType.REPAIR);
    }

    @Override
    protected void dataCalculations(Player player, float skillValue, boolean isLucky) {
        // We're using pickaxes here, not the best but it works
        Repairable diamondRepairable = mcMMO.getRepairableManager().getRepairable(Material.DIAMOND_PICKAXE);
        Repairable goldRepairable = mcMMO.getRepairableManager().getRepairable(Material.GOLD_PICKAXE);
        Repairable ironRepairable = mcMMO.getRepairableManager().getRepairable(Material.IRON_PICKAXE);
        Repairable stoneRepairable = mcMMO.getRepairableManager().getRepairable(Material.STONE_PICKAXE);

        // TODO: This isn't really accurate - if they don't have pickaxes loaded it doesn't always mean the repair level is 0
        diamondLevel = (diamondRepairable == null) ? 0 : diamondRepairable.getMinimumLevel();
        goldLevel = (goldRepairable == null) ? 0 : goldRepairable.getMinimumLevel();
        ironLevel = (ironRepairable == null) ? 0 : ironRepairable.getMinimumLevel();
        stoneLevel = (stoneRepairable == null) ? 0 : stoneRepairable.getMinimumLevel();

        // REPAIR MASTERY
        if (canMasterRepair) {
            repairMasteryBonus = percent.format(Math.min(((Repair.repairMasteryMaxBonus / Repair.repairMasteryMaxBonusLevel) * skillValue), Repair.repairMasteryMaxBonus) / 100D);
        }

        // SUPER REPAIR
        if (canSuperRepair) {
            String[] superRepairStrings = calculateAbilityDisplayValues(skillValue, SecondaryAbility.SUPER_REPAIR, isLucky);
            superRepairChance = superRepairStrings[0];
            superRepairChanceLucky = superRepairStrings[1];
        }
    }

    @Override
    protected void permissionsCheck(Player player) {
        canSuperRepair = Permissions.secondaryAbilityEnabled(player, SecondaryAbility.SUPER_REPAIR);
        canMasterRepair = Permissions.secondaryAbilityEnabled(player, SecondaryAbility.REPAIR_MASTERY);
        canArcaneForge = Permissions.secondaryAbilityEnabled(player, SecondaryAbility.ARCANE_FORGING);
        canRepairDiamond = Permissions.repairMaterialType(player, MaterialType.DIAMOND);
        canRepairGold = Permissions.repairMaterialType(player, MaterialType.GOLD);
        canRepairIron = Permissions.repairMaterialType(player, MaterialType.IRON);
        canRepairStone = Permissions.repairMaterialType(player, MaterialType.STONE);
        canRepairString = Permissions.repairMaterialType(player, MaterialType.STRING);
        canRepairLeather = Permissions.repairMaterialType(player, MaterialType.LEATHER);
        canRepairWood = Permissions.repairMaterialType(player, MaterialType.WOOD);
        arcaneBypass = Permissions.arcaneBypass(player);
    }

    @Override
    protected List<String> effectsDisplay() {
        List<String> messages = new ArrayList<String>();

        if (canRepairLeather || canRepairString || canRepairWood || canRepairStone || canRepairIron || canRepairGold || canRepairDiamond) {
            messages.add(LocaleLoader.getString("Effects.Template", LocaleLoader.getString("Repair.Effect.0"), LocaleLoader.getString("Repair.Effect.1")));
        }

        if (canMasterRepair) {
            messages.add(LocaleLoader.getString("Effects.Template", LocaleLoader.getString("Repair.Effect.2"), LocaleLoader.getString("Repair.Effect.3")));
        }

        if (canSuperRepair) {
            messages.add(LocaleLoader.getString("Effects.Template", LocaleLoader.getString("Repair.Effect.4"), LocaleLoader.getString("Repair.Effect.5")));
        }

        /* Repair Level Requirements */

        if (canRepairStone && stoneLevel > 0) {
            messages.add(LocaleLoader.getString("Effects.Template", LocaleLoader.getString("Repair.Effect.14", stoneLevel), LocaleLoader.getString("Repair.Effect.15")));
        }

        if (canRepairIron && ironLevel > 0) {
            messages.add(LocaleLoader.getString("Effects.Template", LocaleLoader.getString("Repair.Effect.12", ironLevel), LocaleLoader.getString("Repair.Effect.13")));
        }

        if (canRepairGold && goldLevel > 0) {
            messages.add(LocaleLoader.getString("Effects.Template", LocaleLoader.getString("Repair.Effect.10", goldLevel), LocaleLoader.getString("Repair.Effect.11")));
        }

        if (canRepairDiamond && diamondLevel > 0) {
            messages.add(LocaleLoader.getString("Effects.Template", LocaleLoader.getString("Repair.Effect.6", diamondLevel), LocaleLoader.getString("Repair.Effect.7")));
        }

        if (canArcaneForge) {
            messages.add(LocaleLoader.getString("Effects.Template", LocaleLoader.getString("Repair.Effect.8"), LocaleLoader.getString("Repair.Effect.9")));
        }

        return messages;
    }

    @Override
    protected List<String> statsDisplay(Player player, float skillValue, boolean hasEndurance, boolean isLucky) {
        List<String> messages = new ArrayList<String>();

        if (canMasterRepair) {
            messages.add(LocaleLoader.getString("Repair.Skills.Mastery", repairMasteryBonus));
        }

        if (canSuperRepair) {
            messages.add(LocaleLoader.getString("Repair.Skills.Super.Chance", superRepairChance) + (isLucky ? LocaleLoader.getString("Perks.Lucky.Bonus", superRepairChanceLucky) : ""));
        }

        if (canArcaneForge) {
            RepairManager repairManager = UserManager.getPlayer(player).getRepairManager();

            messages.add(LocaleLoader.getString("Repair.Arcane.Rank", repairManager.getArcaneForgingRank(), Tier.values().length));

            if (ArcaneForging.arcaneForgingEnchantLoss) {
                messages.add(LocaleLoader.getString("Repair.Arcane.Chance.Success", (arcaneBypass ? 100 : repairManager.getKeepEnchantChance())));
            }

            if (ArcaneForging.arcaneForgingDowngrades) {
                messages.add(LocaleLoader.getString("Repair.Arcane.Chance.Downgrade", (arcaneBypass ? 0 : repairManager.getDowngradeEnchantChance())));
            }
        }

        return messages;
    }
}
