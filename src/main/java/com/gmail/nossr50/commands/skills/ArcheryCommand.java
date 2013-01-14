package com.gmail.nossr50.commands.skills;

import org.bukkit.ChatColor;

import com.gmail.nossr50.commands.SkillCommand;
import com.gmail.nossr50.config.AdvancedConfig;
import com.gmail.nossr50.datatypes.SkillType;
import com.gmail.nossr50.locale.LocaleLoader;
import com.gmail.nossr50.skills.archery.Archery;
import com.gmail.nossr50.util.Permissions;

public class ArcheryCommand extends SkillCommand {
    AdvancedConfig advancedConfig = AdvancedConfig.getInstance();

    private String skillShotBonus;
    private String dazeChance;
    private String dazeChanceLucky;
    private String retrieveChance;
    private String retrieveChanceLucky;

    private boolean canSkillShot;
    private boolean canDaze;
    private boolean canRetrieve;
    private boolean lucky;

    public ArcheryCommand() {
        super(SkillType.ARCHERY);
    }

    @Override
    protected void dataCalculations() {
        float dazeChanceF;
        float retrieveChanceF;

        // SkillShot
        double bonus = (int)((double) skillValue / (double) Archery.skillShotIncreaseLevel) * Archery.skillShotIncreasePercentage;
        if (bonus > Archery.skillShotMaxBonusPercentage) skillShotBonus = percent.format(Archery.skillShotMaxBonusPercentage);
        else skillShotBonus = percent.format(bonus);

        // Daze
        if (skillValue >= Archery.dazeMaxBonusLevel) dazeChanceF = (float) Archery.dazeMaxBonus;
        else dazeChanceF = (float) (( Archery.dazeMaxBonus / Archery.dazeMaxBonusLevel) * skillValue);
        dazeChance = percent.format(dazeChanceF / 100D);
        if (dazeChanceF * 1.3333D >= 100D) dazeChanceLucky = percent.format(1D);
        else dazeChanceLucky = percent.format(dazeChanceF * 1.3333D / 100D);

        // Retrieve
        if (skillValue >= Archery.retrieveMaxBonusLevel) retrieveChanceF = (float) Archery.retrieveMaxChance;
        else retrieveChanceF = (float) ((Archery.retrieveMaxChance / Archery.retrieveMaxBonusLevel) * skillValue);
        retrieveChance = percent.format(retrieveChanceF / 100D);
        if (retrieveChanceF * 1.3333D >= 100D) retrieveChanceLucky = percent.format(1D);
        else retrieveChanceLucky = percent.format(retrieveChanceF * 1.3333D / 100D);
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
            if (lucky)
        		player.sendMessage(LocaleLoader.getString("Archery.Combat.DazeChance", new Object[] { dazeChance }) + LocaleLoader.getString("Perks.lucky.bonus", new Object[] { dazeChanceLucky }));
        	else
        		player.sendMessage(LocaleLoader.getString("Archery.Combat.DazeChance", new Object[] { dazeChance }));
        }

        if (canRetrieve) {
            if (lucky)
        		player.sendMessage(LocaleLoader.getString("Archery.Combat.RetrieveChance", new Object[] { retrieveChance }) + LocaleLoader.getString("Perks.lucky.bonus", new Object[] { retrieveChanceLucky }));
        	else
        		player.sendMessage(LocaleLoader.getString("Archery.Combat.RetrieveChance", new Object[] { retrieveChance }));
        }
    }
}
