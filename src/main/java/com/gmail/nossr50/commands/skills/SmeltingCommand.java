package com.gmail.nossr50.commands.skills;

import com.gmail.nossr50.datatypes.skills.PrimarySkillType;
import com.gmail.nossr50.datatypes.skills.SubSkillType;
import com.gmail.nossr50.locale.LocaleLoader;
import com.gmail.nossr50.util.Permissions;
import com.gmail.nossr50.util.random.ProbabilityUtil;
import com.gmail.nossr50.util.skills.RankUtils;
import com.gmail.nossr50.util.text.TextComponentFactory;
import java.util.ArrayList;
import java.util.List;
import net.kyori.adventure.text.Component;
import org.bukkit.entity.Player;

public class SmeltingCommand extends SkillCommand {
    private String burnTimeModifier;
    private String str_secondSmeltChance;
    private String str_secondSmeltChanceLucky;
    private String str_fluxMiningChance;
    private String str_fluxMiningChanceLucky;

    private boolean canFuelEfficiency;
    private boolean canSecondSmelt;
    private boolean canFluxMine;
    private boolean canUnderstandTheArt;

    public SmeltingCommand() {
        super(PrimarySkillType.SMELTING);
    }

    @Override
    protected void dataCalculations(Player player, float skillValue) {
        // FUEL EFFICIENCY
        if (canFuelEfficiency) {
            burnTimeModifier = String.valueOf(
                    mmoPlayer.getSmeltingManager().getFuelEfficiencyMultiplier());
        }

        // FLUX MINING
        /*if (canFluxMine) {
            String[] fluxMiningStrings = getRNGDisplayValues(player, SubSkillType.SMELTING_FLUX_MINING);
            str_fluxMiningChance = fluxMiningStrings[0];
            str_fluxMiningChanceLucky = fluxMiningStrings[1];
        }*/

        // SECOND SMELT
        if (canSecondSmelt) {
            String[] secondSmeltStrings = ProbabilityUtil.getRNGDisplayValues(mmoPlayer,
                    SubSkillType.SMELTING_SECOND_SMELT);
            str_secondSmeltChance = secondSmeltStrings[0];
            str_secondSmeltChanceLucky = secondSmeltStrings[1];
        }
    }

    @Override
    protected void permissionsCheck(Player player) {
        canFuelEfficiency = Permissions.canUseSubSkill(player,
                SubSkillType.SMELTING_FUEL_EFFICIENCY);
        canSecondSmelt = Permissions.canUseSubSkill(player, SubSkillType.SMELTING_SECOND_SMELT);
        //canFluxMine = canUseSubskill(player, SubSkillType.SMELTING_FLUX_MINING);
        canUnderstandTheArt =
                Permissions.vanillaXpBoost(player, skill) && RankUtils.hasUnlockedSubskill(player,
                        SubSkillType.SMELTING_UNDERSTANDING_THE_ART);
    }

    @Override
    protected List<String> statsDisplay(Player player, float skillValue, boolean hasEndurance,
            boolean isLucky) {
        List<String> messages = new ArrayList<>();

        /*if (canFluxMine) {
            messages.add(getStatMessage(SubSkillType.SMELTING_FLUX_MINING, str_fluxMiningChance)
                    + (isLucky ? LocaleLoader.getString("Perks.Lucky.Bonus", str_fluxMiningChanceLucky) : ""));
            //messages.add(LocaleLoader.getString("Smelting.Ability.FluxMining", str_fluxMiningChance) + (isLucky ? LocaleLoader.getString("Perks.Lucky.Bonus", str_fluxMiningChanceLucky) : ""));
        }*/

        if (canFuelEfficiency) {
            messages.add(getStatMessage(false, true, SubSkillType.SMELTING_FUEL_EFFICIENCY,
                    burnTimeModifier));
        }

        if (canSecondSmelt) {
            messages.add(getStatMessage(SubSkillType.SMELTING_SECOND_SMELT, str_secondSmeltChance)
                    + (isLucky ? LocaleLoader.getString("Perks.Lucky.Bonus",
                    str_secondSmeltChanceLucky) : ""));
        }

        if (canUnderstandTheArt) {
            messages.add(getStatMessage(false, true, SubSkillType.SMELTING_UNDERSTANDING_THE_ART,
                    String.valueOf(mmoPlayer.getSmeltingManager().getVanillaXpMultiplier())));
        }

        return messages;
    }

    @Override
    protected List<Component> getTextComponents(Player player) {
        List<Component> textComponents = new ArrayList<>();

        TextComponentFactory.appendSubSkillTextComponents(player, textComponents,
                PrimarySkillType.SMELTING);

        return textComponents;
    }
}
