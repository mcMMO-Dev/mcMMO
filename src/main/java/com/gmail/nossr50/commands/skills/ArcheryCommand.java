package com.gmail.nossr50.commands.skills;

import java.text.DecimalFormat;

import org.bukkit.ChatColor;

import com.gmail.nossr50.commands.SkillCommand;
import com.gmail.nossr50.config.AdvancedConfig;
import com.gmail.nossr50.datatypes.SkillType;
import com.gmail.nossr50.locale.LocaleLoader;
import com.gmail.nossr50.util.Permissions;

public class ArcheryCommand extends SkillCommand {
    AdvancedConfig advancedConfig = AdvancedConfig.getInstance();

    private String skillShotBonus;
    private String dazeChance;
    private String retrieveChance;

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
    private boolean lucky;

    public ArcheryCommand() {
        super(SkillType.ARCHERY);
    }

    @Override
    protected void dataCalculations() {
        DecimalFormat df = new DecimalFormat("0.0");
        // SkillShot
        double bonus = (int)((double) skillValue / (double) skillShotIncreaseLevel) * skillShotIncreasePercentage;
        if (bonus > skillShotBonusMax) skillShotBonus = percent.format(skillShotBonusMax);
        else skillShotBonus = percent.format(bonus);

        // Daze
        if(skillValue >= dazeMaxBonusLevel) dazeChance = df.format(dazeBonusMax);
        else dazeChance = df.format(((double) dazeBonusMax / (double) dazeMaxBonusLevel) * skillValue);

        // Retrieve
        if(skillValue >= retrieveMaxBonusLevel)    retrieveChance = df.format(retrieveBonusMax);
        else retrieveChance = df.format(((double) retrieveBonusMax / (double) retrieveMaxBonusLevel) * skillValue);
    }

    @Override
    protected void permissionsCheck() {
        canSkillShot = Permissions.archeryBonus(player);
        canDaze = Permissions.daze(player);
        canRetrieve = Permissions.trackArrows(player);
        lucky = Permissions.luckyArchery(player);
    }

    @Override
    protected boolean effectsHeaderPermissions() {
        return canSkillShot || canDaze || canRetrieve;
    }

    @Override
    protected void effectsDisplay() {
        if (lucky) {
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
            player.sendMessage(LocaleLoader.getString("Archery.Combat.DazeChance", new Object[] { dazeChance }));
        }

        if (canRetrieve) {
            player.sendMessage(LocaleLoader.getString("Archery.Combat.RetrieveChance", new Object[] { retrieveChance }));
        }
    }
}
