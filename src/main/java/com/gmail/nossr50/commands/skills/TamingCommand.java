package com.gmail.nossr50.commands.skills;

import com.gmail.nossr50.datatypes.skills.SubSkillType;
import com.gmail.nossr50.locale.LocaleLoader;
import com.gmail.nossr50.skills.taming.Taming;
import com.gmail.nossr50.util.Permissions;
import com.gmail.nossr50.util.skills.SkillActivationType;
import com.gmail.nossr50.util.text.TextComponentFactory;
import com.neetgames.mcmmo.player.OnlineMMOPlayer;
import net.kyori.adventure.text.Component;
import org.bukkit.entity.EntityType;
import org.jetbrains.annotations.NotNull;

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

    public TamingCommand() {
        super(CoreSkills.TAMING);
    }

    @Override
    protected void dataCalculations(@NotNull OnlineMMOPlayer mmoPlayer, float skillValue) {
        if (canGore) {
            String[] goreStrings = getAbilityDisplayValues(SkillActivationType.RANDOM_LINEAR_100_SCALE_WITH_CAP, mmoPlayer, SubSkillType.TAMING_GORE);
            goreChance = goreStrings[0];
            goreChanceLucky = goreStrings[1];
        }
    }

    @Override
    protected void permissionsCheck(@NotNull OnlineMMOPlayer mmoPlayer) {
        canBeastLore = canUseSubskill(mmoPlayer, SubSkillType.TAMING_BEAST_LORE);
        canCallWild = Permissions.callOfTheWild(Misc.adaptPlayer(mmoPlayer), EntityType.HORSE) || Permissions.callOfTheWild(Misc.adaptPlayer(mmoPlayer), EntityType.WOLF) || Permissions.callOfTheWild(Misc.adaptPlayer(mmoPlayer), EntityType.OCELOT);
        canEnvironmentallyAware = canUseSubskill(mmoPlayer, SubSkillType.TAMING_ENVIRONMENTALLY_AWARE);
        canFastFood = canUseSubskill(mmoPlayer, SubSkillType.TAMING_FAST_FOOD_SERVICE);
        canGore = canUseSubskill(mmoPlayer, SubSkillType.TAMING_GORE);
        canSharpenedClaws = canUseSubskill(mmoPlayer, SubSkillType.TAMING_SHARPENED_CLAWS);
        canShockProof = canUseSubskill(mmoPlayer, SubSkillType.TAMING_SHOCK_PROOF);
        canThickFur = canUseSubskill(mmoPlayer, SubSkillType.TAMING_THICK_FUR);
        canHolyHound = canUseSubskill(mmoPlayer, SubSkillType.TAMING_HOLY_HOUND);
    }

    @Override
    protected @NotNull List<String> statsDisplay(@NotNull OnlineMMOPlayer mmoPlayer, float skillValue, boolean hasEndurance, boolean isLucky) {
        List<String> messages = new ArrayList<>();

        if (canEnvironmentallyAware) {
            messages.add(LocaleLoader.getString("Ability.Generic.Template", LocaleLoader.getString("Taming.Ability.Bonus.0"), LocaleLoader.getString("Taming.Ability.Bonus.1")));
        }
        
        if (canFastFood) {
            messages.add(LocaleLoader.getString("Ability.Generic.Template", LocaleLoader.getString("Taming.Ability.Bonus.8"), LocaleLoader.getString("Taming.Ability.Bonus.9", percent.format(Taming.fastFoodServiceActivationChance / 100D))));
        }
        
        if (canGore) {
            messages.add(LocaleLoader.getString("Ability.Generic.Template",
                    LocaleLoader.getString("Taming.Combat.Chance.Gore"),
                    goreChance) + (isLucky ? LocaleLoader.getString("Perks.Lucky.Bonus", goreChanceLucky) : ""));
        }
        
        if (canHolyHound) {
            messages.add(LocaleLoader.getString("Ability.Generic.Template", LocaleLoader.getString("Taming.Ability.Bonus.10"), LocaleLoader.getString("Taming.Ability.Bonus.11")));
        }

        if (canSharpenedClaws) {
            messages.add(LocaleLoader.getString("Ability.Generic.Template", LocaleLoader.getString("Taming.Ability.Bonus.6"), LocaleLoader.getString("Taming.Ability.Bonus.7", Taming.sharpenedClawsBonusDamage)));
        }
        
        if (canShockProof) {
            messages.add(LocaleLoader.getString("Ability.Generic.Template", LocaleLoader.getString("Taming.Ability.Bonus.4"), LocaleLoader.getString("Taming.Ability.Bonus.5", Taming.shockProofModifier)));
        }
        
        if (canThickFur) {
            messages.add(LocaleLoader.getString("Ability.Generic.Template", LocaleLoader.getString("Taming.Ability.Bonus.2"), LocaleLoader.getString("Taming.Ability.Bonus.3", Taming.thickFurModifier)));
        }

        return messages;
    }

    @Override
    protected @NotNull List<Component> getTextComponents(@NotNull OnlineMMOPlayer mmoPlayer) {
        List<Component> textComponents = new ArrayList<>();

        TextComponentFactory.getSubSkillTextComponents(mmoPlayer, textComponents, this.rootSkill);

        return textComponents;
    }
}
