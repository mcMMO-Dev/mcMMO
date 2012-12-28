package com.gmail.nossr50.commands.skills;

import java.text.DecimalFormat;

import org.bukkit.ChatColor;

import com.gmail.nossr50.commands.SkillCommand;
import com.gmail.nossr50.config.AdvancedConfig;
import com.gmail.nossr50.config.Config;
import com.gmail.nossr50.datatypes.SkillType;
import com.gmail.nossr50.locale.LocaleLoader;

public class TamingCommand extends SkillCommand {
    AdvancedConfig advancedConfig = AdvancedConfig.getInstance();
    private String goreChance;

    private float goreChanceMax = advancedConfig.getGoreChanceMax();
    private float goreMaxLevel = advancedConfig.getGoreMaxBonusLevel();
    private int fastFoodUnlock = advancedConfig.getFastFoodUnlock();
    private float fastFoodChance = advancedConfig.getFastFoodChance();
    private int enviromentallyAwareUnlock = advancedConfig.getEnviromentallyAwareUnlock();
    private int thickFurUnlock = advancedConfig.getThickFurUnlock();
    private int shockProofUnlock = advancedConfig.getShockProofUnlock();
    private int sharpenedClawUnlock = advancedConfig.getSharpenedClawsUnlock();

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
        DecimalFormat df = new DecimalFormat("0.0");
        if(skillValue >= goreMaxLevel) goreChance = df.format(goreChanceMax);
        else goreChance = df.format(((double) goreChanceMax / (double) goreMaxLevel) * skillValue);
    }

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
        if (player.hasPermission("mcmmo.perks.lucky.taming")) {
            String perkPrefix = ChatColor.RED + "[mcMMO Perks] ";
            player.sendMessage(perkPrefix + LocaleLoader.getString("Effects.Template", new Object[] { LocaleLoader.getString("Perks.lucky.name"), LocaleLoader.getString("Perks.lucky.desc", new Object[] { "Taming" }) }));
        }

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
            if (skillValue < fastFoodUnlock) {
                player.sendMessage(LocaleLoader.getString("Ability.Generic.Template.Lock", new Object[] { LocaleLoader.getString("Taming.Ability.Locked.4", new Object[] { fastFoodUnlock } ) }));
            }
            else {
                player.sendMessage(LocaleLoader.getString("Ability.Generic.Template", new Object[] { LocaleLoader.getString("Taming.Ability.Bonus.8"), LocaleLoader.getString("Taming.Ability.Bonus.9", new Object[] { fastFoodChance } ) }));
            }
        }

        if (canEnvironmentallyAware) {
            if (skillValue < enviromentallyAwareUnlock) {
                player.sendMessage(LocaleLoader.getString("Ability.Generic.Template.Lock", new Object[] { LocaleLoader.getString("Taming.Ability.Locked.0", new Object[] { enviromentallyAwareUnlock } ) }));
            }
            else {
                player.sendMessage(LocaleLoader.getString("Ability.Generic.Template", new Object[] { LocaleLoader.getString("Taming.Ability.Bonus.0"), LocaleLoader.getString("Taming.Ability.Bonus.1") }));
            }
        }

        if (canThickFur) {
            if (skillValue < thickFurUnlock) {
                player.sendMessage(LocaleLoader.getString("Ability.Generic.Template.Lock", new Object[] { LocaleLoader.getString("Taming.Ability.Locked.1", new Object[] { thickFurUnlock } ) }));
            }
            else {
                player.sendMessage(LocaleLoader.getString("Ability.Generic.Template", new Object[] { LocaleLoader.getString("Taming.Ability.Bonus.2"), LocaleLoader.getString("Taming.Ability.Bonus.3") }));
            }
        }

        if (canShockProof) {
            if (skillValue < shockProofUnlock) {
                player.sendMessage(LocaleLoader.getString("Ability.Generic.Template.Lock", new Object[] { LocaleLoader.getString("Taming.Ability.Locked.2", new Object[] { shockProofUnlock } ) }));
            }
            else {
                player.sendMessage(LocaleLoader.getString("Ability.Generic.Template", new Object[] { LocaleLoader.getString("Taming.Ability.Bonus.4"), LocaleLoader.getString("Taming.Ability.Bonus.5") }));
            }
        }

        if (canSharpenedClaws) {
            if (skillValue < sharpenedClawUnlock) {
                player.sendMessage(LocaleLoader.getString("Ability.Generic.Template.Lock", new Object[] { LocaleLoader.getString("Taming.Ability.Locked.3", new Object[] { sharpenedClawUnlock } ) }));
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
