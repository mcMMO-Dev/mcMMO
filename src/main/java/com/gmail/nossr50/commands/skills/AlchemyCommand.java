package com.gmail.nossr50.commands.skills;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.entity.Player;

import com.gmail.nossr50.config.AdvancedConfig;
import com.gmail.nossr50.datatypes.skills.SkillType;
import com.gmail.nossr50.locale.LocaleLoader;
import com.gmail.nossr50.skills.alchemy.Alchemy.Tier;
import com.gmail.nossr50.util.Permissions;
import com.gmail.nossr50.util.player.UserManager;

public class AlchemyCommand extends SkillCommand {
    private String brewSpeed;
    private String brewSpeedLucky;

    private int    tier;
    private String ingredients;

    private boolean canCatalysis;
    private boolean canConcoctions;

    public AlchemyCommand() {
        super(SkillType.ALCHEMY);
    }

    protected String[] calculateAbilityDisplayValues(Player player, float skillValue, boolean isLucky) {
        String[] displayValues = new String[2];

        displayValues[0] = decimal.format(UserManager.getPlayer(player).getAlchemyManager().getBrewSpeed()) + "x";
        displayValues[1] = isLucky ? decimal.format(UserManager.getPlayer(player).getAlchemyManager().getBrewSpeedLucky()) + "x" : null;

        return displayValues;
    }

    @Override
    protected void dataCalculations(Player player, float skillValue, boolean isLucky) {
        // DODGE
        if (canCatalysis) {
            String[] catalysisStrings = calculateAbilityDisplayValues(player, skillValue, isLucky);
            brewSpeed = catalysisStrings[0];
            brewSpeedLucky = catalysisStrings[1];
        }

        // ROLL
        if (canConcoctions) {
            tier = UserManager.getPlayer(player).getAlchemyManager().getTier();
            ingredients = UserManager.getPlayer(player).getAlchemyManager().getIngredientList();
        }
    }

    @Override
    protected void permissionsCheck(Player player) {
        canCatalysis = Permissions.catalysis(player);
        canConcoctions = Permissions.concoctions(player);
    }

    @Override
    protected List<String> effectsDisplay() {
        List<String> messages = new ArrayList<String>();
        
        if (canCatalysis) {
            messages.add(LocaleLoader.getString("Effects.Template", LocaleLoader.getString("Alchemy.Effect.0"), LocaleLoader.getString("Alchemy.Effect.1")));
        }

        if (canConcoctions) {
            messages.add(LocaleLoader.getString("Effects.Template", LocaleLoader.getString("Alchemy.Effect.2"), LocaleLoader.getString("Alchemy.Effect.3")));
        }
        
        return messages;
    }

    @Override
    protected List<String> statsDisplay(Player player, float skillValue, boolean hasEndurance, boolean isLucky) {
        List<String> messages = new ArrayList<String>();

        if (canCatalysis) {
            int unlockLevel = AdvancedConfig.getInstance().getCatalysisUnlockLevel();

            if (skillValue < unlockLevel) {
                messages.add(LocaleLoader.getString("Ability.Generic.Template.Lock", LocaleLoader.getString("Alchemy.Ability.Locked.0", unlockLevel)));
            }
            else {
                messages.add(LocaleLoader.getString("Alchemy.Catalysis.Speed", brewSpeed) + (isLucky ? LocaleLoader.getString("Perks.Lucky.Bonus", brewSpeedLucky) : ""));
            }
        }

        if (canConcoctions) {
            messages.add(LocaleLoader.getString("Alchemy.Concoctions.Rank", tier, Tier.FIVE.toNumerical()));
            messages.add(LocaleLoader.getString("Alchemy.Concoctions.Ingredients", ingredients));
        }

        return messages;
    }
}
