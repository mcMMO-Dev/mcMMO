package com.gmail.nossr50.commands.skills;

import com.gmail.nossr50.commands.SkillCommand;
import com.gmail.nossr50.config.Config;
import com.gmail.nossr50.datatypes.SkillType;
import com.gmail.nossr50.locale.LocaleLoader;

public class TamingCommand extends SkillCommand {
    private String goreChance;

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
        if (skillValue >= 1000) {
            goreChance = "100.00%";
        }
        else {
            goreChance = percent.format(skillValue / 1000);
        }    }

    @Override
    protected void permissionsCheck() {
        canBeastLore = permInstance.beastLore(player);
        canCallWild = permInstance.callOfTheWild(player);
        canEnvironmentallyAware = permInstance.environmentallyAware(player);
        canFastFood = permInstance.fastFoodService(player);
        canGore = permInstance.gore(player);
        canSharpenedClaws = permInstance.sharpenedClaws(player);
        canShockProof = permInstance.shockProof(player);
        canThickFur = permInstance.thickFur(player);
    }

    @Override
    protected boolean effectsHeaderPermissions() {
        return canBeastLore || canCallWild || canEnvironmentallyAware || canFastFood || canGore || canSharpenedClaws || canShockProof || canThickFur;
    }

    @Override
    protected void effectsDisplay() {
        Config configInstance = Config.getInstance();

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
            player.sendMessage(LocaleLoader.getString("Taming.Effect.14", new Object[] { configInstance.getTamingCOTWOcelotCost() }));
            player.sendMessage(LocaleLoader.getString("Taming.Effect.15", new Object[] { configInstance.getTamingCOTWWolfCost() }));
        }
    }

    @Override
    protected boolean statsHeaderPermissions() {
        return canEnvironmentallyAware || canFastFood || canGore || canSharpenedClaws || canShockProof || canThickFur;
    }

    @Override
    protected void statsDisplay() {
        if (canFastFood) {
            if (skillValue < 50) {
                player.sendMessage(LocaleLoader.getString("Ability.Generic.Template.Lock", new Object[] { LocaleLoader.getString("Taming.Ability.Locked.4") }));
            }
            else {
                player.sendMessage(LocaleLoader.getString("Ability.Generic.Template", new Object[] { LocaleLoader.getString("Taming.Ability.Bonus.8"), LocaleLoader.getString("Taming.Ability.Bonus.9") }));
            }
        }

        if (canEnvironmentallyAware) {
            if (skillValue < 100) {
                player.sendMessage(LocaleLoader.getString("Ability.Generic.Template.Lock", new Object[] { LocaleLoader.getString("Taming.Ability.Locked.0") }));
            }
            else {
                player.sendMessage(LocaleLoader.getString("Ability.Generic.Template", new Object[] { LocaleLoader.getString("Taming.Ability.Bonus.0"), LocaleLoader.getString("Taming.Ability.Bonus.1") }));
            }
        }

        if (canThickFur) {
            if (skillValue < 250) {
                player.sendMessage(LocaleLoader.getString("Ability.Generic.Template.Lock", new Object[] { LocaleLoader.getString("Taming.Ability.Locked.1") }));
            }
            else {
                player.sendMessage(LocaleLoader.getString("Ability.Generic.Template", new Object[] { LocaleLoader.getString("Taming.Ability.Bonus.2"), LocaleLoader.getString("Taming.Ability.Bonus.3") }));
            }
        }

        if (canShockProof) {
            if (skillValue < 500) {
                player.sendMessage(LocaleLoader.getString("Ability.Generic.Template.Lock", new Object[] { LocaleLoader.getString("Taming.Ability.Locked.2") }));
            }
            else {
                player.sendMessage(LocaleLoader.getString("Ability.Generic.Template", new Object[] { LocaleLoader.getString("Taming.Ability.Bonus.4"), LocaleLoader.getString("Taming.Ability.Bonus.5") }));
            }
        }

        if (canSharpenedClaws) {
            if (skillValue < 750) {
                player.sendMessage(LocaleLoader.getString("Ability.Generic.Template.Lock", new Object[] { LocaleLoader.getString("Taming.Ability.Locked.3") }));
            }
            else {
                player.sendMessage(LocaleLoader.getString("Ability.Generic.Template", new Object[] { LocaleLoader.getString("Taming.Ability.Bonus.6"), LocaleLoader.getString("Taming.Ability.Bonus.7") }));
            }
        }

        if (canGore) {
            player.sendMessage(LocaleLoader.getString("Taming.Combat.Chance.Gore", new Object[] { goreChance }));
        }
    }
}
