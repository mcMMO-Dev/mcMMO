package com.gmail.nossr50.commands.skills;

import com.gmail.nossr50.datatypes.skills.PrimarySkillType;
import com.gmail.nossr50.datatypes.skills.SubSkillType;
import com.gmail.nossr50.mcMMO;
import com.gmail.nossr50.skills.repair.RepairManager;
import com.gmail.nossr50.skills.repair.repairables.Repairable;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Material;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class RepairCommand extends SkillCommand {
    private String repairMasteryBonus;
    private String superRepairChance;
    private String superRepairChanceLucky;

    private boolean canSuperRepair;
    private boolean canMasterRepair;
    private boolean canArcaneForge;
//    private boolean canRepairStone;
//    private boolean canRepairIron;
//    private boolean canRepairGold;
//    private boolean canRepairDiamond;
//    private boolean canRepairString;
//    private boolean canRepairLeather;
//    private boolean canRepairWood;
    private boolean arcaneBypass;

//    private int diamondLevel;
//    private int goldLevel;
//    private int ironLevel;
//    private int stoneLevel;

    public RepairCommand(mcMMO pluginRef) {
        super(PrimarySkillType.REPAIR, pluginRef);
    }

    @Override
    protected void dataCalculations(Player player, double skillValue) {
        // We're using pickaxes here, not the best but it works
        Repairable diamondRepairable = pluginRef.getRepairableManager().getRepairable(Material.DIAMOND_PICKAXE);
        Repairable goldRepairable = pluginRef.getRepairableManager().getRepairable(Material.GOLDEN_PICKAXE);
        Repairable ironRepairable = pluginRef.getRepairableManager().getRepairable(Material.IRON_PICKAXE);
        Repairable stoneRepairable = pluginRef.getRepairableManager().getRepairable(Material.STONE_PICKAXE);

        // TODO: This isn't really accurate - if they don't have pickaxes loaded it doesn't always mean the repair level is 0
//        diamondLevel = (diamondRepairable == null) ? 0 : diamondRepairable.getMinimumLevel();
//        goldLevel = (goldRepairable == null) ? 0 : goldRepairable.getMinimumLevel();
//        ironLevel = (ironRepairable == null) ? 0 : ironRepairable.getMinimumLevel();
//        stoneLevel = (stoneRepairable == null) ? 0 : stoneRepairable.getMinimumLevel();

        // REPAIR MASTERY
        if (canMasterRepair) {
            double maxBonus = pluginRef.getDynamicSettingsManager().getSkillPropertiesManager().getMaxBonus(SubSkillType.REPAIR_REPAIR_MASTERY);
            int maxBonusLevel = pluginRef.getDynamicSettingsManager().getSkillPropertiesManager().getMaxBonusLevel(SubSkillType.REPAIR_REPAIR_MASTERY);

            repairMasteryBonus = percent.format(Math.min(((maxBonus / maxBonusLevel) * skillValue), maxBonus) / 100D);
        }

        // SUPER REPAIR
        if (canSuperRepair) {
            String[] superRepairStrings = getAbilityDisplayValues(player, SubSkillType.REPAIR_SUPER_REPAIR);
            superRepairChance = superRepairStrings[0];
            superRepairChanceLucky = superRepairStrings[1];
        }
    }

    @Override
    protected void permissionsCheck(Player player) {
        canSuperRepair = canUseSubskill(player, SubSkillType.REPAIR_SUPER_REPAIR);
        canMasterRepair = canUseSubskill(player, SubSkillType.REPAIR_REPAIR_MASTERY);
        canArcaneForge = canUseSubskill(player, SubSkillType.REPAIR_ARCANE_FORGING);
//        canRepairDiamond = Permissions.repairMaterialType(player, ItemMaterialCategory.DIAMOND);
//        canRepairGold = Permissions.repairMaterialType(player, ItemMaterialCategory.GOLD);
//        canRepairIron = Permissions.repairMaterialType(player, ItemMaterialCategory.IRON);
//        canRepairStone = Permissions.repairMaterialType(player, ItemMaterialCategory.STONE);
//        canRepairString = Permissions.repairMaterialType(player, ItemMaterialCategory.STRING);
//        canRepairLeather = Permissions.repairMaterialType(player, ItemMaterialCategory.LEATHER);
//        canRepairWood = Permissions.repairMaterialType(player, ItemMaterialCategory.WOOD);
        arcaneBypass = (pluginRef.getPermissionTools().arcaneBypass(player) || pluginRef.getPermissionTools().hasRepairEnchantBypassPerk(player));
    }

    @Override
    protected List<String> statsDisplay(Player player, double skillValue, boolean hasEndurance, boolean isLucky) {
        List<String> messages = new ArrayList<>();

        if (canArcaneForge) {
            RepairManager repairManager = pluginRef.getUserManager().getPlayer(player).getRepairManager();

            messages.add(getStatMessage(false, true,
                    SubSkillType.REPAIR_ARCANE_FORGING,
                    String.valueOf(pluginRef.getRankTools().getRank(player, SubSkillType.REPAIR_ARCANE_FORGING)),
                    pluginRef.getRankTools().getHighestRankStr(SubSkillType.REPAIR_ARCANE_FORGING)));

            if (pluginRef.getConfigManager().getConfigRepair().getArcaneForging().isDowngradesEnabled() || pluginRef.getConfigManager().getConfigRepair().getArcaneForging().isMayLoseEnchants()) {
                messages.add(getStatMessage(true, true, SubSkillType.REPAIR_ARCANE_FORGING,
                        String.valueOf(arcaneBypass ? 100 : repairManager.getKeepEnchantChance()),
                        String.valueOf(arcaneBypass ? 0 : repairManager.getDowngradeEnchantChance()))); //Jesus those parentheses
            }
        }

        if (canMasterRepair) {
            messages.add(getStatMessage(false, true, SubSkillType.REPAIR_REPAIR_MASTERY, repairMasteryBonus));
        }

        if (canSuperRepair) {
            messages.add(getStatMessage(SubSkillType.REPAIR_SUPER_REPAIR, superRepairChance)
                    + (isLucky ? pluginRef.getLocaleManager().getString("Perks.Lucky.Bonus", superRepairChanceLucky) : ""));
        }

        return messages;
    }

    @Override
    protected List<TextComponent> getTextComponents(Player player) {
        List<TextComponent> textComponents = new ArrayList<>();

        pluginRef.getTextComponentFactory().getSubSkillTextComponents(player, textComponents, PrimarySkillType.REPAIR);

        return textComponents;
    }
}
