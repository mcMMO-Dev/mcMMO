package com.gmail.nossr50.skills.taming;

import com.gmail.nossr50.config.Config;
import com.gmail.nossr50.locale.LocaleLoader;
import com.gmail.nossr50.skills.SkillCommand;
import com.gmail.nossr50.skills.SkillType;
import com.gmail.nossr50.util.Permissions;

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

    public TamingCommand() {
        super(SkillType.TAMING);
    }

    @Override
    protected void dataCalculations() {
        String[] goreStrings = calculateAbilityDisplayValues(Taming.goreMaxBonusLevel, Taming.goreMaxChance);
        goreChance = goreStrings[0];
        goreChanceLucky = goreStrings[1];
    }

    @Override
    protected void permissionsCheck() {
        canBeastLore = Permissions.beastLore(player);
        canCallWild = Permissions.callOfTheWild(player);
        canEnvironmentallyAware = Permissions.environmentallyAware(player);
        canFastFood = Permissions.fastFoodService(player);
        canGore = Permissions.gore(player);
        canSharpenedClaws = Permissions.sharpenedClaws(player);
        canShockProof = Permissions.shockProof(player);
        canThickFur = Permissions.thickFur(player);
    }

    @Override
    protected boolean effectsHeaderPermissions() {
        return canBeastLore || canCallWild || canEnvironmentallyAware || canFastFood || canGore || canSharpenedClaws || canShockProof || canThickFur;
    }

    @Override
    protected void effectsDisplay() {
        luckyEffectsDisplay();

        if (canBeastLore) {
            player.sendMessage(LocaleLoader.getString("Effects.Template", new Object[] { LocaleLoader.getString("Taming.Effect.0"), LocaleLoader.getString("Taming.Effect.1") }));
        }

        if (canGore) {
            player.sendMessage(LocaleLoader.getString("Effects.Template", new Object[] { LocaleLoader.getString("Taming.Effect.2"), LocaleLoader.getString("Taming.Effect.3") }));
        }

        if (canSharpenedClaws) {
            player.sendMessage(LocaleLoader.getString("Effects.Template", new Object[] { LocaleLoader.getString("Taming.Effect.4"), LocaleLoader.getString("Taming.Effect.5") }));
        }

        if (canEnvironmentallyAware) {
            player.sendMessage(LocaleLoader.getString("Effects.Template", new Object[] { LocaleLoader.getString("Taming.Effect.6"), LocaleLoader.getString("Taming.Effect.7") }));
        }

        if (canThickFur) {
            player.sendMessage(LocaleLoader.getString("Effects.Template", new Object[] { LocaleLoader.getString("Taming.Effect.8"), LocaleLoader.getString("Taming.Effect.9") }));
        }

        if (canShockProof) {
            player.sendMessage(LocaleLoader.getString("Effects.Template", new Object[] { LocaleLoader.getString("Taming.Effect.10"), LocaleLoader.getString("Taming.Effect.11") }));
        }

        if (canFastFood) {
            player.sendMessage(LocaleLoader.getString("Effects.Template", new Object[] { LocaleLoader.getString("Taming.Effect.16"), LocaleLoader.getString("Taming.Effect.17") }));
        }

        if (canCallWild) {
            player.sendMessage(LocaleLoader.getString("Effects.Template", new Object[] { LocaleLoader.getString("Taming.Effect.12"), LocaleLoader.getString("Taming.Effect.13") }));
            player.sendMessage(LocaleLoader.getString("Taming.Effect.14", new Object[] { Config.getInstance().getTamingCOTWOcelotCost() }));
            player.sendMessage(LocaleLoader.getString("Taming.Effect.15", new Object[] { Config.getInstance().getTamingCOTWWolfCost() }));
        }
    }

    @Override
    protected boolean statsHeaderPermissions() {
        return canEnvironmentallyAware || canFastFood || canGore || canSharpenedClaws || canShockProof || canThickFur;
    }

    @Override
    protected void statsDisplay() {
        if (canFastFood) {
            if (skillValue < Taming.fastFoodServiceUnlockLevel) {
                player.sendMessage(LocaleLoader.getString("Ability.Generic.Template.Lock", new Object[] { LocaleLoader.getString("Taming.Ability.Locked.4", new Object[] { Taming.fastFoodServiceUnlockLevel } ) }));
            }
            else {
                player.sendMessage(LocaleLoader.getString("Ability.Generic.Template", new Object[] { LocaleLoader.getString("Taming.Ability.Bonus.8"), LocaleLoader.getString("Taming.Ability.Bonus.9", new Object[] { percent.format(Taming.fastFoodServiceActivationChance) } ) }));
            }
        }

        if (canEnvironmentallyAware) {
            if (skillValue < Taming.environmentallyAwareUnlockLevel) {
                player.sendMessage(LocaleLoader.getString("Ability.Generic.Template.Lock", new Object[] { LocaleLoader.getString("Taming.Ability.Locked.0", new Object[] { Taming.environmentallyAwareUnlockLevel } ) }));
            }
            else {
                player.sendMessage(LocaleLoader.getString("Ability.Generic.Template", new Object[] { LocaleLoader.getString("Taming.Ability.Bonus.0"), LocaleLoader.getString("Taming.Ability.Bonus.1") }));
            }
        }

        if (canThickFur) {
            if (skillValue < Taming.thickFurUnlockLevel) {
                player.sendMessage(LocaleLoader.getString("Ability.Generic.Template.Lock", new Object[] { LocaleLoader.getString("Taming.Ability.Locked.1", new Object[] { Taming.thickFurUnlockLevel } ) }));
            }
            else {
                player.sendMessage(LocaleLoader.getString("Ability.Generic.Template", new Object[] { LocaleLoader.getString("Taming.Ability.Bonus.2"), LocaleLoader.getString("Taming.Ability.Bonus.3", new Object[] { Taming.thickFurModifier }) }));
            }
        }

        if (canShockProof) {
            if (skillValue < Taming.shockProofUnlockLevel) {
                player.sendMessage(LocaleLoader.getString("Ability.Generic.Template.Lock", new Object[] { LocaleLoader.getString("Taming.Ability.Locked.2", new Object[] { Taming.shockProofUnlockLevel } ) }));
            }
            else {
                player.sendMessage(LocaleLoader.getString("Ability.Generic.Template", new Object[] { LocaleLoader.getString("Taming.Ability.Bonus.4"), LocaleLoader.getString("Taming.Ability.Bonus.5", new Object[] { Taming.shockProofModifier }) }));
            }
        }

        if (canSharpenedClaws) {
            if (skillValue < Taming.sharpenedClawsUnlockLevel) {
                player.sendMessage(LocaleLoader.getString("Ability.Generic.Template.Lock", new Object[] { LocaleLoader.getString("Taming.Ability.Locked.3", new Object[] { Taming.sharpenedClawsUnlockLevel } ) }));
            }
            else {
                player.sendMessage(LocaleLoader.getString("Ability.Generic.Template", new Object[] { LocaleLoader.getString("Taming.Ability.Bonus.6"), LocaleLoader.getString("Taming.Ability.Bonus.7", new Object[] { Taming.sharpenedClawsBonusDamage }) }));
            }
        }

        if (canGore) {
            if (isLucky) {
                player.sendMessage(LocaleLoader.getString("Taming.Combat.Chance.Gore", new Object[] { goreChance }) + LocaleLoader.getString("Perks.lucky.bonus", new Object[] { goreChanceLucky }));
            }
            else {
                player.sendMessage(LocaleLoader.getString("Taming.Combat.Chance.Gore", new Object[] { goreChance }));
            }
        }
    }
}
