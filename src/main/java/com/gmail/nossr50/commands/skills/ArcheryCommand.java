package com.gmail.nossr50.commands.skills;

import java.text.DecimalFormat;

import org.bukkit.ChatColor;

import com.gmail.nossr50.commands.SkillCommand;
import com.gmail.nossr50.config.AdvancedConfig;
import com.gmail.nossr50.datatypes.SkillType;
import com.gmail.nossr50.locale.LocaleLoader;

public class ArcheryCommand extends SkillCommand {
    AdvancedConfig advancedConfig = AdvancedConfig.getInstance();

    private String skillShotBonus;
    private String dazeChance;
    private String dazeChanceLucky;
    private String retrieveChance;
    private String retrieveChanceLucky;

    private int skillShotIncreaseLevel = advancedConfig.getSkillShotIncreaseLevel();
    private double skillShotIncreasePercentage = advancedConfig.getSkillShotIncreasePercentage();
    private double skillShotBonusMax = advancedConfig.getSkillShotBonusMax();

    private float dazeBonusMax = advancedConfig.getDazeBonusMax();
    private float dazeMaxBonusLevel = advancedConfig.getDazeMaxBonusLevel();

    private float retrieveBonusMax = advancedConfig.getRetrieveBonusMax();
    private float retrieveMaxBonusLevel = advancedConfig.getRetrieveMaxBonusLevel();

    private boolean canSkillShot;
    private boolean canDaze;
    private boolean canRetrieve;

    public ArcheryCommand() {
        super(SkillType.ARCHERY);
    }

    @Override
    protected void dataCalculations() {
        DecimalFormat df = new DecimalFormat("0.0");
        float dazeChanceF;
        float retrieveChanceF;

        // SkillShot
        double bonus = (int)((double) skillValue / (double) skillShotIncreaseLevel) * skillShotIncreasePercentage;
        if (bonus > skillShotBonusMax) skillShotBonus = percent.format(skillShotBonusMax);
        else skillShotBonus = percent.format(bonus);

        // Daze
        if(skillValue >= dazeMaxBonusLevel) dazeChanceF = dazeBonusMax;
        else dazeChanceF = (float) (((double) dazeBonusMax / (double) dazeMaxBonusLevel) * skillValue);
        dazeChance = df.format(dazeChanceF);
        if(dazeChanceF + dazeChanceF * 0.3333D >= 100D) dazeChanceLucky = df.format(100D);
        else dazeChanceLucky = df.format(dazeChanceF + dazeChanceF * 0.3333D);

        // Retrieve
        if(skillValue >= retrieveMaxBonusLevel) retrieveChanceF = retrieveBonusMax;
        else retrieveChanceF = (float) (((double) retrieveBonusMax / (double) retrieveMaxBonusLevel) * skillValue);
        retrieveChance = df.format(retrieveChanceF);
        if(retrieveChanceF + retrieveChanceF * 0.3333D >= 100D) retrieveChanceLucky = df.format(100D);
        else retrieveChanceLucky = df.format(retrieveChanceF + retrieveChanceF * 0.3333D);
    }

    @Override
    protected void permissionsCheck() {
        canSkillShot = permInstance.archeryBonus(player);
        canDaze = permInstance.daze(player);
        canRetrieve = permInstance.trackArrows(player);
    }

    @Override
    protected boolean effectsHeaderPermissions() {
        return canSkillShot || canDaze || canRetrieve;
    }

    @Override
    protected void effectsDisplay() {
        if (player.hasPermission("mcmmo.perks.lucky.archery")) {
            String perkPrefix = ChatColor.RED + "[mcMMO Perks] ";
            player.sendMessage(perkPrefix + LocaleLoader.getString("Effects.Template", new Object[] { LocaleLoader.getString("Perks.lucky.name"), LocaleLoader.getString("Perks.lucky.desc", new Object[] { "Archery" }) }));
        }

        if (canSkillShot) {
            player.sendMessage(LocaleLoader.getString("Effects.Template", new Object[] { LocaleLoader.getString("Archery.Effect.0"), LocaleLoader.getString("Archery.Effect.1") }));
        }

        if (canDaze) {
            player.sendMessage(LocaleLoader.getString("Effects.Template", new Object[] { LocaleLoader.getString("Archery.Effect.2"), LocaleLoader.getString("Archery.Effect.3") }));
        }

        if (canRetrieve) {
            player.sendMessage(LocaleLoader.getString("Effects.Template", new Object[] { LocaleLoader.getString("Archery.Effect.4"), LocaleLoader.getString("Archery.Effect.5") }));
        }
    }

    @Override
    protected boolean statsHeaderPermissions() {
        return canSkillShot || canDaze || canRetrieve;
    }

    @Override
    protected void statsDisplay() {
        if (canSkillShot) {
        	player.sendMessage(LocaleLoader.getString("Archery.Combat.SkillshotBonus", new Object[] { skillShotBonus }));
        }

        if (canDaze) {
        	if (player.hasPermission("mcmmo.perks.lucky.archery"))
        		player.sendMessage(LocaleLoader.getString("Archery.Combat.DazeChance", new Object[] { dazeChance }) + LocaleLoader.getString("Perks.lucky.bonus", new Object[] { dazeChanceLucky }));
        	else
        		player.sendMessage(LocaleLoader.getString("Archery.Combat.DazeChance", new Object[] { dazeChance }));
        }

        if (canRetrieve) {
        	if (player.hasPermission("mcmmo.perks.lucky.archery"))
        		player.sendMessage(LocaleLoader.getString("Archery.Combat.RetrieveChance", new Object[] { retrieveChance }) + LocaleLoader.getString("Perks.lucky.bonus", new Object[] { retrieveChanceLucky }));
        	else
        		player.sendMessage(LocaleLoader.getString("Archery.Combat.RetrieveChance", new Object[] { retrieveChance }));
        }
    }
}
