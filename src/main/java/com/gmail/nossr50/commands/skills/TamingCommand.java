package com.gmail.nossr50.commands.skills;

import com.gmail.nossr50.datatypes.skills.PrimarySkillType;
import com.gmail.nossr50.datatypes.skills.SubSkillType;
import com.gmail.nossr50.mcMMO;
import com.gmail.nossr50.skills.taming.Taming;
import com.gmail.nossr50.util.Permissions;
import com.gmail.nossr50.util.TextComponentFactory;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class TamingCommand extends SkillCommand {
    private String goreChance;
    private String goreChanceLucky;

    private boolean canBeastLore;
    private boolean canGore;
    private boolean canSharpenedClaws;
    private boolean canEnvironmentallyAware;
    private boolean canThickFur;
    private boolean canShockProof;
    private boolean canCallWild;
    private boolean canFastFood;
    private boolean canHolyHound;

    public TamingCommand(mcMMO pluginRef) {
        super(PrimarySkillType.TAMING, pluginRef);
    }

    @Override
    protected void dataCalculations(Player player, double skillValue) {
        if (canGore) {
            String[] goreStrings = getAbilityDisplayValues(player, SubSkillType.TAMING_GORE);
            goreChance = goreStrings[0];
            goreChanceLucky = goreStrings[1];
        }
    }

    @Override
    protected void permissionsCheck(Player player) {
        canBeastLore = canUseSubskill(player, SubSkillType.TAMING_BEAST_LORE);
        canCallWild = Permissions.callOfTheWild(player, EntityType.HORSE) || Permissions.callOfTheWild(player, EntityType.WOLF) || Permissions.callOfTheWild(player, EntityType.OCELOT);
        canEnvironmentallyAware = canUseSubskill(player, SubSkillType.TAMING_ENVIRONMENTALLY_AWARE);
        canFastFood = canUseSubskill(player, SubSkillType.TAMING_FAST_FOOD_SERVICE);
        canGore = canUseSubskill(player, SubSkillType.TAMING_GORE);
        canSharpenedClaws = canUseSubskill(player, SubSkillType.TAMING_SHARPENED_CLAWS);
        canShockProof = canUseSubskill(player, SubSkillType.TAMING_SHOCK_PROOF);
        canThickFur = canUseSubskill(player, SubSkillType.TAMING_THICK_FUR);
        canHolyHound = canUseSubskill(player, SubSkillType.TAMING_HOLY_HOUND);
    }

    @Override
    protected List<String> statsDisplay(Player player, double skillValue, boolean hasEndurance, boolean isLucky) {
        List<String> messages = new ArrayList<>();

        if (canEnvironmentallyAware) {
            messages.add(pluginRef.getLocaleManager().getString("Ability.Generic.Template", pluginRef.getLocaleManager().getString("Taming.Ability.Bonus.0"), pluginRef.getLocaleManager().getString("Taming.Ability.Bonus.1")));
        }

        if (canFastFood) {
            messages.add(pluginRef.getLocaleManager().getString("Ability.Generic.Template",
                    pluginRef.getLocaleManager().getString("Taming.Ability.Bonus.8"),
                    pluginRef.getLocaleManager().getString("Taming.Ability.Bonus.9",
                            percent.format(Taming.getInstance().getFastFoodServiceActivationChance() / 100D))));
        }

        if (canGore) {
            messages.add(pluginRef.getLocaleManager().getString("Ability.Generic.Template",
                    pluginRef.getLocaleManager().getString("Taming.Combat.Chance.Gore"),
                    goreChance) + (isLucky ? pluginRef.getLocaleManager().getString("Perks.Lucky.Bonus", goreChanceLucky) : ""));
        }

        if (canHolyHound) {
            messages.add(pluginRef.getLocaleManager().getString("Ability.Generic.Template",
                    pluginRef.getLocaleManager().getString("Taming.Ability.Bonus.10"),
                    pluginRef.getLocaleManager().getString("Taming.Ability.Bonus.11")));
        }

        if (canSharpenedClaws) {
            messages.add(pluginRef.getLocaleManager().getString("Ability.Generic.Template",
                    pluginRef.getLocaleManager().getString("Taming.Ability.Bonus.6"),
                    pluginRef.getLocaleManager().getString("Taming.Ability.Bonus.7",
                            Taming.getInstance().getSharpenedClawsBonusDamage())));
        }

        if (canShockProof) {
            messages.add(pluginRef.getLocaleManager().getString("Ability.Generic.Template",
                    pluginRef.getLocaleManager().getString("Taming.Ability.Bonus.4"),
                    pluginRef.getLocaleManager().getString("Taming.Ability.Bonus.5",
                            Taming.getInstance().getShockProofModifier())));
        }

        if (canThickFur) {
            messages.add(pluginRef.getLocaleManager().getString("Ability.Generic.Template",
                    pluginRef.getLocaleManager().getString("Taming.Ability.Bonus.2"),
                    pluginRef.getLocaleManager().getString("Taming.Ability.Bonus.3",
                            Taming.getInstance().getThickFurModifier())));
        }

        return messages;
    }

    @Override
    protected List<TextComponent> getTextComponents(Player player) {
        List<TextComponent> textComponents = new ArrayList<>();

        pluginRef.getTextComponentFactory().getSubSkillTextComponents(player, textComponents, this.skill);

        return textComponents;
    }
}
