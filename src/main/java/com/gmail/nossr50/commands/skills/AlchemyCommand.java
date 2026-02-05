package com.gmail.nossr50.commands.skills;

import com.gmail.nossr50.datatypes.skills.PrimarySkillType;
import com.gmail.nossr50.datatypes.skills.SubSkillType;
import com.gmail.nossr50.locale.LocaleLoader;
import com.gmail.nossr50.skills.alchemy.AlchemyManager;
import com.gmail.nossr50.util.Permissions;
import com.gmail.nossr50.util.skills.RankUtils;
import com.gmail.nossr50.util.text.TextComponentFactory;
import java.util.ArrayList;
import java.util.List;
import net.kyori.adventure.text.Component;
import org.bukkit.entity.Player;

public class AlchemyCommand extends SkillCommand {
    private String brewSpeed;
    private String brewSpeedLucky;

    private int tier;
    private int ingredientCount;
    private String ingredientList;

    private boolean canCatalysis;
    private boolean canConcoctions;

    public AlchemyCommand() {
        super(PrimarySkillType.ALCHEMY);
    }

    protected String[] calculateAbilityDisplayValues(Player player) {
        AlchemyManager alchemyManager = mmoPlayer.getAlchemyManager();
        String[] displayValues = new String[2];

        boolean isLucky = Permissions.lucky(player, PrimarySkillType.ALCHEMY);

        displayValues[0] = decimal.format(alchemyManager.calculateBrewSpeed(false)) + "x";
        displayValues[1] =
                isLucky ? decimal.format(alchemyManager.calculateBrewSpeed(true)) + "x" : null;

        return displayValues;
    }

    @Override
    protected void dataCalculations(Player player, float skillValue) {
        // ALCHEMY_CATALYSIS
        if (canCatalysis) {
            String[] catalysisStrings = calculateAbilityDisplayValues(player);
            brewSpeed = catalysisStrings[0];
            brewSpeedLucky = catalysisStrings[1];
        }

        // ALCHEMY_CONCOCTIONS
        if (canConcoctions) {
            AlchemyManager alchemyManager = mmoPlayer.getAlchemyManager();
            tier = alchemyManager.getTier();
            ingredientCount = alchemyManager.getIngredients().size();
            ingredientList = alchemyManager.getIngredientList();
        }
    }

    @Override
    protected void permissionsCheck(Player player) {
        canCatalysis = Permissions.canUseSubSkill(player, SubSkillType.ALCHEMY_CATALYSIS);
        canConcoctions = Permissions.canUseSubSkill(player, SubSkillType.ALCHEMY_CONCOCTIONS);
    }

    @Override
    protected List<String> statsDisplay(Player player, float skillValue, boolean hasEndurance,
            boolean isLucky) {
        List<String> messages = new ArrayList<>();

        if (canCatalysis) {
            messages.add(getStatMessage(SubSkillType.ALCHEMY_CATALYSIS, brewSpeed)
                    + (isLucky ? LocaleLoader.getString("Perks.Lucky.Bonus", brewSpeedLucky) : ""));
        }

        if (canConcoctions) {
            messages.add(getStatMessage(false, true, SubSkillType.ALCHEMY_CONCOCTIONS,
                    String.valueOf(tier),
                    String.valueOf(RankUtils.getHighestRank(SubSkillType.ALCHEMY_CONCOCTIONS))));
            messages.add(getStatMessage(true, true, SubSkillType.ALCHEMY_CONCOCTIONS,
                    String.valueOf(ingredientCount), ingredientList));

            //messages.add(LocaleLoader.getString("Alchemy.Concoctions.Rank", tier, RankUtils.getHighestRank(SubSkillType.ALCHEMY_CONCOCTIONS)));
            //messages.add(LocaleLoader.getString("Alchemy.Concoctions.Ingredients", ingredientCount, ingredientList));
        }

        return messages;
    }

    @Override
    protected List<Component> getTextComponents(Player player) {
        List<Component> textComponents = new ArrayList<>();

        TextComponentFactory.appendSubSkillTextComponents(player, textComponents,
                PrimarySkillType.ALCHEMY);

        return textComponents;
    }
}
