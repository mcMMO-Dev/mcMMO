package com.gmail.nossr50.commands.skills;

import com.gmail.nossr50.datatypes.skills.PrimarySkillType;
import com.gmail.nossr50.datatypes.skills.SubSkillType;
import com.gmail.nossr50.locale.LocaleLoader;
import com.gmail.nossr50.skills.taming.Taming;
import com.gmail.nossr50.util.Permissions;
import com.gmail.nossr50.util.random.ProbabilityUtil;
import com.gmail.nossr50.util.text.TextComponentFactory;
import java.util.ArrayList;
import java.util.List;
import net.kyori.adventure.text.Component;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;

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

    public TamingCommand() {
        super(PrimarySkillType.TAMING);
    }

    @Override
    protected void dataCalculations(Player player, float skillValue) {
        if (canGore) {
            String[] goreStrings = ProbabilityUtil.getRNGDisplayValues(mmoPlayer,
                    SubSkillType.TAMING_GORE);
            goreChance = goreStrings[0];
            goreChanceLucky = goreStrings[1];
        }
    }

    @Override
    protected void permissionsCheck(Player player) {
        canBeastLore = Permissions.canUseSubSkill(player, SubSkillType.TAMING_BEAST_LORE);
        canCallWild =
                Permissions.callOfTheWild(player, EntityType.HORSE) || Permissions.callOfTheWild(
                        player, EntityType.WOLF) || Permissions.callOfTheWild(player,
                        EntityType.OCELOT);
        canEnvironmentallyAware = Permissions.canUseSubSkill(player,
                SubSkillType.TAMING_ENVIRONMENTALLY_AWARE);
        canFastFood = Permissions.canUseSubSkill(player, SubSkillType.TAMING_FAST_FOOD_SERVICE);
        canGore = Permissions.canUseSubSkill(player, SubSkillType.TAMING_GORE);
        canSharpenedClaws = Permissions.canUseSubSkill(player, SubSkillType.TAMING_SHARPENED_CLAWS);
        canShockProof = Permissions.canUseSubSkill(player, SubSkillType.TAMING_SHOCK_PROOF);
        canThickFur = Permissions.canUseSubSkill(player, SubSkillType.TAMING_THICK_FUR);
        canHolyHound = Permissions.canUseSubSkill(player, SubSkillType.TAMING_HOLY_HOUND);
    }

    @Override
    protected List<String> statsDisplay(Player player, float skillValue, boolean hasEndurance,
            boolean isLucky) {
        List<String> messages = new ArrayList<>();

        if (canEnvironmentallyAware) {
            messages.add(LocaleLoader.getString("Ability.Generic.Template",
                    LocaleLoader.getString("Taming.Ability.Bonus.0"),
                    LocaleLoader.getString("Taming.Ability.Bonus.1")));
        }

        if (canFastFood) {
            messages.add(LocaleLoader.getString("Ability.Generic.Template",
                    LocaleLoader.getString("Taming.Ability.Bonus.8"),
                    LocaleLoader.getString("Taming.Ability.Bonus.9",
                            percent.format(Taming.fastFoodServiceActivationChance / 100D))));
        }

        if (canGore) {
            messages.add(LocaleLoader.getString("Ability.Generic.Template",
                    LocaleLoader.getString("Taming.Combat.Chance.Gore"),
                    goreChance) + (isLucky ? LocaleLoader.getString("Perks.Lucky.Bonus",
                    goreChanceLucky) : ""));
        }

        if (canHolyHound) {
            messages.add(LocaleLoader.getString("Ability.Generic.Template",
                    LocaleLoader.getString("Taming.Ability.Bonus.10"),
                    LocaleLoader.getString("Taming.Ability.Bonus.11")));
        }

        if (canSharpenedClaws) {
            messages.add(LocaleLoader.getString("Ability.Generic.Template",
                    LocaleLoader.getString("Taming.Ability.Bonus.6"),
                    LocaleLoader.getString("Taming.Ability.Bonus.7",
                            Taming.sharpenedClawsBonusDamage)));
        }

        if (canShockProof) {
            messages.add(LocaleLoader.getString("Ability.Generic.Template",
                    LocaleLoader.getString("Taming.Ability.Bonus.4"),
                    LocaleLoader.getString("Taming.Ability.Bonus.5", Taming.shockProofModifier)));
        }

        if (canThickFur) {
            messages.add(LocaleLoader.getString("Ability.Generic.Template",
                    LocaleLoader.getString("Taming.Ability.Bonus.2"),
                    LocaleLoader.getString("Taming.Ability.Bonus.3", Taming.thickFurModifier)));
        }

        return messages;
    }

    @Override
    protected List<Component> getTextComponents(Player player) {
        List<Component> textComponents = new ArrayList<>();

        TextComponentFactory.appendSubSkillTextComponents(player, textComponents, this.skill);

        return textComponents;
    }
}
