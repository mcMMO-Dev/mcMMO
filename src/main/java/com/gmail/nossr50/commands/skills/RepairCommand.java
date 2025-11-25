package com.gmail.nossr50.commands.skills;

import com.gmail.nossr50.datatypes.skills.MaterialType;
import com.gmail.nossr50.datatypes.skills.PrimarySkillType;
import com.gmail.nossr50.datatypes.skills.SubSkillType;
import com.gmail.nossr50.locale.LocaleLoader;
import com.gmail.nossr50.mcMMO;
import com.gmail.nossr50.skills.repair.ArcaneForging;
import com.gmail.nossr50.skills.repair.Repair;
import com.gmail.nossr50.skills.repair.RepairManager;
import com.gmail.nossr50.skills.repair.repairables.Repairable;
import com.gmail.nossr50.util.Permissions;
import com.gmail.nossr50.util.random.ProbabilityUtil;
import com.gmail.nossr50.util.skills.RankUtils;
import com.gmail.nossr50.util.text.TextComponentFactory;
import java.util.ArrayList;
import java.util.List;
import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.entity.Player;

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
        super(PrimarySkillType.REPAIR);
    }

    @Override
    protected void dataCalculations(Player player, float skillValue) {
        // We're using pickaxes here, not the best but it works
        Repairable diamondRepairable = mcMMO.getRepairableManager()
                .getRepairable(Material.DIAMOND_PICKAXE);
        Repairable goldRepairable = mcMMO.getRepairableManager()
                .getRepairable(Material.GOLDEN_PICKAXE);
        Repairable ironRepairable = mcMMO.getRepairableManager()
                .getRepairable(Material.IRON_PICKAXE);
        Repairable stoneRepairable = mcMMO.getRepairableManager()
                .getRepairable(Material.STONE_PICKAXE);

        // TODO: This isn't really accurate - if they don't have pickaxes loaded it doesn't always mean the repair level is 0
        diamondLevel = (diamondRepairable == null) ? 0 : diamondRepairable.getMinimumLevel();
        goldLevel = (goldRepairable == null) ? 0 : goldRepairable.getMinimumLevel();
        ironLevel = (ironRepairable == null) ? 0 : ironRepairable.getMinimumLevel();
        stoneLevel = (stoneRepairable == null) ? 0 : stoneRepairable.getMinimumLevel();

        // REPAIR MASTERY
        if (canMasterRepair) {
            repairMasteryBonus = percent.format(Math.min(
                    ((Repair.repairMasteryMaxBonus / Repair.repairMasteryMaxBonusLevel)
                            * skillValue), Repair.repairMasteryMaxBonus) / 100D);
        }

        // SUPER REPAIR
        if (canSuperRepair) {
            String[] superRepairStrings = ProbabilityUtil.getRNGDisplayValues(mmoPlayer,
                    SubSkillType.REPAIR_SUPER_REPAIR);
            superRepairChance = superRepairStrings[0];
            superRepairChanceLucky = superRepairStrings[1];
        }
    }

    @Override
    protected void permissionsCheck(Player player) {
        canSuperRepair = Permissions.canUseSubSkill(player, SubSkillType.REPAIR_SUPER_REPAIR);
        canMasterRepair = Permissions.canUseSubSkill(player, SubSkillType.REPAIR_REPAIR_MASTERY);
        canArcaneForge = Permissions.canUseSubSkill(player, SubSkillType.REPAIR_ARCANE_FORGING);
        canRepairDiamond = Permissions.repairMaterialType(player, MaterialType.DIAMOND);
        canRepairGold = Permissions.repairMaterialType(player, MaterialType.GOLD);
        canRepairIron = Permissions.repairMaterialType(player, MaterialType.IRON);
        canRepairStone = Permissions.repairMaterialType(player, MaterialType.STONE);
        canRepairString = Permissions.repairMaterialType(player, MaterialType.STRING);
        canRepairLeather = Permissions.repairMaterialType(player, MaterialType.LEATHER);
        canRepairWood = Permissions.repairMaterialType(player, MaterialType.WOOD);
        arcaneBypass = (Permissions.arcaneBypass(player) || Permissions.hasRepairEnchantBypassPerk(
                player));
    }

    @Override
    protected List<String> statsDisplay(Player player, float skillValue, boolean hasEndurance,
            boolean isLucky) {
        List<String> messages = new ArrayList<>();

        if (canArcaneForge) {
            RepairManager repairManager = mmoPlayer.getRepairManager();

            messages.add(getStatMessage(false, true,
                    SubSkillType.REPAIR_ARCANE_FORGING,
                    String.valueOf(RankUtils.getRank(player, SubSkillType.REPAIR_ARCANE_FORGING)),
                    RankUtils.getHighestRankStr(SubSkillType.REPAIR_ARCANE_FORGING)));

            if (ArcaneForging.arcaneForgingEnchantLoss || ArcaneForging.arcaneForgingDowngrades) {
                messages.add(getStatMessage(true, true, SubSkillType.REPAIR_ARCANE_FORGING,
                        String.valueOf(arcaneBypass ? 100 : repairManager.getKeepEnchantChance()),
                        String.valueOf(arcaneBypass ? 0
                                : repairManager.getDowngradeEnchantChance()))); //Jesus those parentheses
            }
        }

        if (canMasterRepair) {
            messages.add(getStatMessage(false, true, SubSkillType.REPAIR_REPAIR_MASTERY,
                    repairMasteryBonus));
        }

        if (canSuperRepair) {
            messages.add(getStatMessage(SubSkillType.REPAIR_SUPER_REPAIR, superRepairChance)
                    + (isLucky ? LocaleLoader.getString("Perks.Lucky.Bonus", superRepairChanceLucky)
                    : ""));
        }

        return messages;
    }

    @Override
    protected List<Component> getTextComponents(Player player) {
        List<Component> textComponents = new ArrayList<>();

        TextComponentFactory.appendSubSkillTextComponents(player, textComponents,
                PrimarySkillType.REPAIR);

        return textComponents;
    }
}
