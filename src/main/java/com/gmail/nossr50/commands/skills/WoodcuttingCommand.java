package com.gmail.nossr50.commands.skills;

import com.gmail.nossr50.datatypes.skills.PrimarySkillType;
import com.gmail.nossr50.datatypes.skills.SubSkillType;
import com.gmail.nossr50.locale.LocaleLoader;
import com.gmail.nossr50.mcMMO;
import com.gmail.nossr50.util.Permissions;
import com.gmail.nossr50.util.random.ProbabilityUtil;
import com.gmail.nossr50.util.skills.RankUtils;
import com.gmail.nossr50.util.text.TextComponentFactory;
import java.util.ArrayList;
import java.util.List;
import net.kyori.adventure.text.Component;
import org.bukkit.entity.Player;

public class WoodcuttingCommand extends SkillCommand {
    private String treeFellerLength;
    private String treeFellerLengthEndurance;
    private String doubleDropChance;
    private String tripleDropChance;
    private String doubleDropChanceLucky;
    private String tripleDropChanceLucky;

    private boolean canTreeFell;
    private boolean canLeafBlow;
    private boolean canDoubleDrop;
    private boolean canTripleDrop;
    private boolean canKnockOnWood;

    public WoodcuttingCommand() {
        super(PrimarySkillType.WOODCUTTING);
    }

    @Override
    protected void dataCalculations(Player player, float skillValue) {
        // DOUBLE DROPS
        if (canDoubleDrop) {
            setDoubleDropClassicChanceStrings(player);
        }

        //Clean Cuts
        if (canTripleDrop) {
            String[] tripleDropStrings = ProbabilityUtil.getRNGDisplayValues(mmoPlayer,
                    SubSkillType.WOODCUTTING_CLEAN_CUTS);
            tripleDropChance = tripleDropStrings[0];
            tripleDropChanceLucky = tripleDropStrings[1];
        }

        // TREE FELLER
        if (canTreeFell) {
            String[] treeFellerStrings = calculateLengthDisplayValues(player, skillValue);
            treeFellerLength = treeFellerStrings[0];
            treeFellerLengthEndurance = treeFellerStrings[1];
        }
    }

    private void setDoubleDropClassicChanceStrings(Player player) {
        String[] doubleDropStrings = ProbabilityUtil.getRNGDisplayValues(mmoPlayer,
                SubSkillType.WOODCUTTING_HARVEST_LUMBER);
        doubleDropChance = doubleDropStrings[0];
        doubleDropChanceLucky = doubleDropStrings[1];
    }

    @Override
    protected void permissionsCheck(Player player) {
        canTreeFell = RankUtils.hasUnlockedSubskill(player, SubSkillType.WOODCUTTING_TREE_FELLER)
                && Permissions.treeFeller(player);
        canDoubleDrop = !mcMMO.p.getGeneralConfig().getDoubleDropsDisabled(skill)
                && Permissions.canUseSubSkill(player, SubSkillType.WOODCUTTING_HARVEST_LUMBER)
                && RankUtils.getRank(player, SubSkillType.WOODCUTTING_HARVEST_LUMBER) >= 1;
        canTripleDrop = !mcMMO.p.getGeneralConfig().getDoubleDropsDisabled(skill)
                && Permissions.canUseSubSkill(player, SubSkillType.WOODCUTTING_CLEAN_CUTS);
        canLeafBlow = Permissions.canUseSubSkill(player, SubSkillType.WOODCUTTING_LEAF_BLOWER);
        canKnockOnWood = canTreeFell && Permissions.canUseSubSkill(player,
                SubSkillType.WOODCUTTING_KNOCK_ON_WOOD);
    }

    @Override
    protected List<String> statsDisplay(Player player, float skillValue, boolean hasEndurance,
            boolean isLucky) {
        List<String> messages = new ArrayList<>();

        if (canDoubleDrop) {
            messages.add(getStatMessage(SubSkillType.WOODCUTTING_HARVEST_LUMBER, doubleDropChance)
                    + (isLucky ? LocaleLoader.getString("Perks.Lucky.Bonus", doubleDropChanceLucky)
                    : ""));
        }

        if (canTripleDrop) {
            messages.add(getStatMessage(SubSkillType.WOODCUTTING_CLEAN_CUTS, tripleDropChance)
                    + (isLucky ? LocaleLoader.getString("Perks.Lucky.Bonus", tripleDropChanceLucky)
                    : ""));
        }

        if (canKnockOnWood) {
            String lootNote;

            if (RankUtils.hasReachedRank(2, player, SubSkillType.WOODCUTTING_KNOCK_ON_WOOD)) {
                lootNote = LocaleLoader.getString("Woodcutting.SubSkill.KnockOnWood.Loot.Rank2");
            } else {
                lootNote = LocaleLoader.getString("Woodcutting.SubSkill.KnockOnWood.Loot.Normal");
            }

            messages.add(getStatMessage(SubSkillType.WOODCUTTING_KNOCK_ON_WOOD, lootNote));
        }

        if (canLeafBlow) {
            messages.add(LocaleLoader.getString("Ability.Generic.Template",
                    LocaleLoader.getString("Woodcutting.Ability.0"),
                    LocaleLoader.getString("Woodcutting.Ability.1")));
        }

        if (canTreeFell) {
            messages.add(getStatMessage(SubSkillType.WOODCUTTING_TREE_FELLER, treeFellerLength)
                    + (hasEndurance ? LocaleLoader.getString("Perks.ActivationTime.Bonus",
                    treeFellerLengthEndurance) : ""));
        }

        return messages;
    }

    @Override
    protected List<Component> getTextComponents(Player player) {
        List<Component> textComponents = new ArrayList<>();

        TextComponentFactory.appendSubSkillTextComponents(player, textComponents,
                PrimarySkillType.WOODCUTTING);

        return textComponents;
    }


}
