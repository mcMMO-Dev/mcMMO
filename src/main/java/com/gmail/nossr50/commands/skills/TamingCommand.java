package com.gmail.nossr50.commands.skills;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;

import com.gmail.nossr50.config.Config;
import com.gmail.nossr50.datatypes.skills.SecondaryAbility;
import com.gmail.nossr50.datatypes.skills.SkillType;
import com.gmail.nossr50.locale.LocaleLoader;
import com.gmail.nossr50.skills.taming.Taming;
import com.gmail.nossr50.util.Permissions;
import com.gmail.nossr50.util.StringUtils;

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
        super(SkillType.TAMING);
    }

    @Override
    protected void dataCalculations(Player player, float skillValue, boolean isLucky) {
        if (canGore) {
            String[] goreStrings = calculateAbilityDisplayValues(skillValue, SecondaryAbility.GORE, isLucky);
            goreChance = goreStrings[0];
            goreChanceLucky = goreStrings[1];
        }
    }

    @Override
    protected void permissionsCheck(Player player) {
        canBeastLore = Permissions.secondaryAbilityEnabled(player, SecondaryAbility.BEAST_LORE);
        canCallWild = Permissions.callOfTheWild(player, EntityType.HORSE) || Permissions.callOfTheWild(player, EntityType.WOLF) || Permissions.callOfTheWild(player, EntityType.OCELOT);
        canEnvironmentallyAware = Permissions.secondaryAbilityEnabled(player, SecondaryAbility.ENVIROMENTALLY_AWARE);
        canFastFood = Permissions.secondaryAbilityEnabled(player, SecondaryAbility.FAST_FOOD);
        canGore = Permissions.secondaryAbilityEnabled(player, SecondaryAbility.GORE);
        canSharpenedClaws = Permissions.secondaryAbilityEnabled(player, SecondaryAbility.SHARPENED_CLAWS);
        canShockProof = Permissions.secondaryAbilityEnabled(player, SecondaryAbility.SHOCK_PROOF);
        canThickFur = Permissions.secondaryAbilityEnabled(player, SecondaryAbility.THICK_FUR);
        canHolyHound = Permissions.secondaryAbilityEnabled(player, SecondaryAbility.HOLY_HOUND);
    }

    @Override
    protected List<String> effectsDisplay() {
        List<String> messages = new ArrayList<String>();

        if (canBeastLore) {
            messages.add(LocaleLoader.getString("Effects.Template", LocaleLoader.getString("Taming.Effect.0"), LocaleLoader.getString("Taming.Effect.1")));
        }

        if (canGore) {
            messages.add(LocaleLoader.getString("Effects.Template", LocaleLoader.getString("Taming.Effect.2"), LocaleLoader.getString("Taming.Effect.3")));
        }

        if (canSharpenedClaws) {
            messages.add(LocaleLoader.getString("Effects.Template", LocaleLoader.getString("Taming.Effect.4"), LocaleLoader.getString("Taming.Effect.5")));
        }

        if (canEnvironmentallyAware) {
            messages.add(LocaleLoader.getString("Effects.Template", LocaleLoader.getString("Taming.Effect.6"), LocaleLoader.getString("Taming.Effect.7")));
        }

        if (canThickFur) {
            messages.add(LocaleLoader.getString("Effects.Template", LocaleLoader.getString("Taming.Effect.8"), LocaleLoader.getString("Taming.Effect.9")));
        }

        if (canShockProof) {
            messages.add(LocaleLoader.getString("Effects.Template", LocaleLoader.getString("Taming.Effect.10"), LocaleLoader.getString("Taming.Effect.11")));
        }

        if (canFastFood) {
            messages.add(LocaleLoader.getString("Effects.Template", LocaleLoader.getString("Taming.Effect.16"), LocaleLoader.getString("Taming.Effect.17")));
        }

        if (canHolyHound) {
            messages.add(LocaleLoader.getString("Effects.Template", LocaleLoader.getString("Taming.Effect.18"), LocaleLoader.getString("Taming.Effect.19")));
        }

        if (canCallWild) {
            messages.add(LocaleLoader.getString("Effects.Template", LocaleLoader.getString("Taming.Effect.12"), LocaleLoader.getString("Taming.Effect.13")));
            int costOcelot = Config.getInstance().getTamingCOTWCost(EntityType.OCELOT);
            String itemOcelot = StringUtils.getPrettyItemString(Config.getInstance().getTamingCOTWMaterial(EntityType.OCELOT));
            int costWolf = Config.getInstance().getTamingCOTWCost(EntityType.WOLF);
            String itemWolf = StringUtils.getPrettyItemString(Config.getInstance().getTamingCOTWMaterial(EntityType.WOLF));
            int costHorse = Config.getInstance().getTamingCOTWCost(EntityType.HORSE);
            String itemHorse = StringUtils.getPrettyItemString(Config.getInstance().getTamingCOTWMaterial(EntityType.HORSE));
            messages.add(LocaleLoader.getString("Taming.Effect.14", costOcelot, itemOcelot, costWolf, itemWolf, costHorse, itemHorse));
        }

        return messages;
    }

    @Override
    protected List<String> statsDisplay(Player player, float skillValue, boolean hasEndurance, boolean isLucky) {
        List<String> messages = new ArrayList<String>();

        if (canFastFood) {
            if (skillValue < Taming.fastFoodServiceUnlockLevel) {
                messages.add(LocaleLoader.getString("Ability.Generic.Template.Lock", LocaleLoader.getString("Taming.Ability.Locked.4", Taming.fastFoodServiceUnlockLevel)));
            }
            else {
                messages.add(LocaleLoader.getString("Ability.Generic.Template", LocaleLoader.getString("Taming.Ability.Bonus.8"), LocaleLoader.getString("Taming.Ability.Bonus.9", percent.format(Taming.fastFoodServiceActivationChance / 100D))));
            }
        }

        if (canEnvironmentallyAware) {
            if (skillValue < Taming.environmentallyAwareUnlockLevel) {
                messages.add(LocaleLoader.getString("Ability.Generic.Template.Lock", LocaleLoader.getString("Taming.Ability.Locked.0", Taming.environmentallyAwareUnlockLevel)));
            }
            else {
                messages.add(LocaleLoader.getString("Ability.Generic.Template", LocaleLoader.getString("Taming.Ability.Bonus.0"), LocaleLoader.getString("Taming.Ability.Bonus.1")));
            }
        }

        if (canThickFur) {
            if (skillValue < Taming.thickFurUnlockLevel) {
                messages.add(LocaleLoader.getString("Ability.Generic.Template.Lock", LocaleLoader.getString("Taming.Ability.Locked.1", Taming.thickFurUnlockLevel)));
            }
            else {
                messages.add(LocaleLoader.getString("Ability.Generic.Template", LocaleLoader.getString("Taming.Ability.Bonus.2"), LocaleLoader.getString("Taming.Ability.Bonus.3", Taming.thickFurModifier)));
            }
        }

        if (canHolyHound) {
            if (skillValue < Taming.holyHoundUnlockLevel) {
                messages.add(LocaleLoader.getString("Ability.Generic.Template.Lock", LocaleLoader.getString("Taming.Ability.Locked.5", Taming.holyHoundUnlockLevel)));
            }
            else {
                messages.add(LocaleLoader.getString("Ability.Generic.Template", LocaleLoader.getString("Taming.Ability.Bonus.10"), LocaleLoader.getString("Taming.Ability.Bonus.11")));
            }
        }

        if (canShockProof) {
            if (skillValue < Taming.shockProofUnlockLevel) {
                messages.add(LocaleLoader.getString("Ability.Generic.Template.Lock", LocaleLoader.getString("Taming.Ability.Locked.2", Taming.shockProofUnlockLevel)));
            }
            else {
                messages.add(LocaleLoader.getString("Ability.Generic.Template", LocaleLoader.getString("Taming.Ability.Bonus.4"), LocaleLoader.getString("Taming.Ability.Bonus.5", Taming.shockProofModifier)));
            }
        }

        if (canSharpenedClaws) {
            if (skillValue < Taming.sharpenedClawsUnlockLevel) {
                messages.add(LocaleLoader.getString("Ability.Generic.Template.Lock", LocaleLoader.getString("Taming.Ability.Locked.3", Taming.sharpenedClawsUnlockLevel)));
            }
            else {
                messages.add(LocaleLoader.getString("Ability.Generic.Template", LocaleLoader.getString("Taming.Ability.Bonus.6"), LocaleLoader.getString("Taming.Ability.Bonus.7", Taming.sharpenedClawsBonusDamage)));
            }
        }

        if (canGore) {
            messages.add(LocaleLoader.getString("Taming.Combat.Chance.Gore", goreChance) + (isLucky ? LocaleLoader.getString("Perks.Lucky.Bonus", goreChanceLucky) : ""));
        }

        return messages;
    }
}
