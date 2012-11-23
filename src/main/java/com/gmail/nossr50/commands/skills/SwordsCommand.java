package com.gmail.nossr50.commands.skills;

import java.text.DecimalFormat;

import org.bukkit.ChatColor;

import com.gmail.nossr50.commands.SkillCommand;
import com.gmail.nossr50.config.AdvancedConfig;
import com.gmail.nossr50.datatypes.SkillType;
import com.gmail.nossr50.locale.LocaleLoader;

public class SwordsCommand extends SkillCommand {
	AdvancedConfig advancedConfig = AdvancedConfig.getInstance();
	
    private String counterAttackChance;
    private String bleedLength;
    private String bleedChance;
    private String serratedStrikesLength;

    private int abilityLengthIncreaseLevel = advancedConfig.getAbilityLength();
    private float bleedChanceMax = advancedConfig.getBleedChanceMax();
    private float bleedMaxLevel = advancedConfig.getBleedMaxBonusLevel();
    private int bleedMaxTicks = advancedConfig.getBleedMaxTicks();
    private int bleedBaseTicks = advancedConfig.getBleedBaseTicks();
    private float counterChanceMax = advancedConfig.getCounterChanceMax();
    private float counterMaxLevel = advancedConfig.getCounterMaxBonusLevel();
    private int serratedBleedTicks = advancedConfig.getSerratedStrikesTicks();

    private boolean canCounter;
    private boolean canSerratedStrike;
    private boolean canBleed;

    public SwordsCommand() {
        super(SkillType.SWORDS);
    }

    @Override
    protected void dataCalculations() {
		DecimalFormat df = new DecimalFormat("#.0");
        serratedStrikesLength = String.valueOf(2 + ((int) skillValue / abilityLengthIncreaseLevel));
        
        if (skillValue >= bleedMaxLevel) bleedLength = String.valueOf(bleedMaxTicks);
        else bleedLength = String.valueOf(bleedBaseTicks);

		if(skillValue >= bleedMaxLevel) bleedChance = df.format(bleedChanceMax);
		else bleedChance = df.format((bleedChanceMax / bleedMaxLevel) * skillValue);
        
		if(skillValue >= counterMaxLevel) counterAttackChance = df.format(counterChanceMax);
		else counterAttackChance = df.format((counterChanceMax / counterMaxLevel) * skillValue);
		
		serratedStrikesLength = String.valueOf(serratedBleedTicks);
    }

    @Override
    protected void permissionsCheck() {
        canBleed = permInstance.swordsBleed(player);
        canCounter = permInstance.counterAttack(player);
        canSerratedStrike = permInstance.serratedStrikes(player);
    }

    @Override
    protected boolean effectsHeaderPermissions() {
        return canBleed || canCounter || canSerratedStrike;
    }

    @Override
    protected void effectsDisplay() {
        if (player.hasPermission("mcmmo.perks.lucky.swords")) {
            String perkPrefix = ChatColor.RED + "[mcMMO Perks] ";
            player.sendMessage(perkPrefix + LocaleLoader.getString("Effects.Template", new Object[] { LocaleLoader.getString("Perks.lucky.name"), LocaleLoader.getString("Perks.lucky.desc", new Object[] { "Swords" }) }));
        }

        if (canCounter) {
            player.sendMessage(LocaleLoader.getString("Effects.Template", new Object[] { LocaleLoader.getString("Swords.Effect.0"), LocaleLoader.getString("Swords.Effect.1") }));
        }

        if (canSerratedStrike) {
            player.sendMessage(LocaleLoader.getString("Effects.Template", new Object[] { LocaleLoader.getString("Swords.Effect.2"), LocaleLoader.getString("Swords.Effect.3") }));
            player.sendMessage(LocaleLoader.getString("Effects.Template", new Object[] { LocaleLoader.getString("Swords.Effect.4"), LocaleLoader.getString("Swords.Effect.5") }));
        }

        if (canBleed) {
            player.sendMessage(LocaleLoader.getString("Effects.Template", new Object[] { LocaleLoader.getString("Swords.Effect.6"), LocaleLoader.getString("Swords.Effect.7") }));
        }
    }

    @Override
    protected boolean statsHeaderPermissions() {
        return canBleed || canCounter || canSerratedStrike;
    }

    @Override
    protected void statsDisplay() {
        if (canCounter) {
            player.sendMessage(LocaleLoader.getString("Swords.Combat.Counter.Chance", new Object[] { counterAttackChance }));
        }

        if (canBleed) {
            player.sendMessage(LocaleLoader.getString("Swords.Combat.Bleed.Length", new Object[] { bleedLength }));
            player.sendMessage(LocaleLoader.getString("Swords.Combat.Bleed.Note"));
            player.sendMessage(LocaleLoader.getString("Swords.Combat.Bleed.Chance", new Object[] { bleedChance }));
        }

        if (canSerratedStrike) {
            player.sendMessage(LocaleLoader.getString("Swords.SS.Length", new Object[] { serratedStrikesLength }));
        }
    }
}
