package com.gmail.nossr50.commands.skills;

import java.text.DecimalFormat;

import org.bukkit.ChatColor;

import com.gmail.nossr50.commands.SkillCommand;
import com.gmail.nossr50.config.AdvancedConfig;
import com.gmail.nossr50.config.Config;
import com.gmail.nossr50.datatypes.SkillType;
import com.gmail.nossr50.locale.LocaleLoader;

public class WoodcuttingCommand extends SkillCommand {
    AdvancedConfig advancedConfig = AdvancedConfig.getInstance();
    private String treeFellerLength;
    private String doubleDropChance;

    private int abilityLengthIncreaseLevel = advancedConfig.getAbilityLength();
    private double doubleDropsMaxBonus = advancedConfig.getWoodcuttingDoubleDropChance();
    private int doubleDropsMaxLevel = advancedConfig.getWoodcuttingDoubleDropMaxLevel();
    private int leafBlowUnlock = advancedConfig.getLeafBlowUnlockLevel();
    
    private boolean canTreeFell;
    private boolean canLeafBlow;
    private boolean canDoubleDrop;
    private boolean doubleDropsDisabled;

    public WoodcuttingCommand() {
        super(SkillType.WOODCUTTING);
    }

    @Override
    protected void dataCalculations() {
        DecimalFormat df = new DecimalFormat("0.0");

        treeFellerLength = String.valueOf(2 + (int) ((double) skillValue / (double) abilityLengthIncreaseLevel));
        if(skillValue >= doubleDropsMaxLevel) doubleDropChance = df.format(doubleDropsMaxBonus);
        else doubleDropChance = df.format(((double) doubleDropsMaxBonus / (double) doubleDropsMaxLevel) * (double) skillValue);
    }

    @Override
    protected void permissionsCheck() {
        Config configInstance = Config.getInstance();

        canTreeFell = permInstance.treeFeller(player);
        canDoubleDrop = permInstance.woodcuttingDoubleDrops(player);
        canLeafBlow = permInstance.leafBlower(player);
        doubleDropsDisabled = configInstance.woodcuttingDoubleDropsDisabled();
    }

    @Override
    protected boolean effectsHeaderPermissions() {
        return (canDoubleDrop && !doubleDropsDisabled) || canLeafBlow || canTreeFell;
    }

    @Override
    protected void effectsDisplay() {
        if (player.hasPermission("mcmmo.perks.lucky.woodcutting")) {
            String perkPrefix = ChatColor.RED + "[mcMMO Perks] ";
            player.sendMessage(perkPrefix + LocaleLoader.getString("Effects.Template", new Object[] { LocaleLoader.getString("Perks.lucky.name"), LocaleLoader.getString("Perks.lucky.desc", new Object[] { "Woodcutting" }) }));
        }

        if (canTreeFell) {
            player.sendMessage(LocaleLoader.getString("Effects.Template", new Object[] { LocaleLoader.getString("Woodcutting.Effect.0"), LocaleLoader.getString("Woodcutting.Effect.1") }));
        }

        if (canLeafBlow) {
            player.sendMessage(LocaleLoader.getString("Effects.Template", new Object[] { LocaleLoader.getString("Woodcutting.Effect.2"), LocaleLoader.getString("Woodcutting.Effect.3") }));
        }

        if (canDoubleDrop && !doubleDropsDisabled) {
            player.sendMessage(LocaleLoader.getString("Effects.Template", new Object[] { LocaleLoader.getString("Woodcutting.Effect.4"), LocaleLoader.getString("Woodcutting.Effect.5") }));
        }
    }

    @Override
    protected boolean statsHeaderPermissions() {
        return (canDoubleDrop && !doubleDropsDisabled) || canLeafBlow || canTreeFell;
    }

    @Override
    protected void statsDisplay() {
        //TODO: Remove? Basically duplicates the above.
        if (canLeafBlow) {
            if (skillValue < leafBlowUnlock) {
                player.sendMessage(LocaleLoader.getString("Ability.Generic.Template.Lock", new Object[] { LocaleLoader.getString("Woodcutting.Ability.Locked.0", new Object[] { leafBlowUnlock }) }));
            }
            else {
                player.sendMessage(LocaleLoader.getString("Ability.Generic.Template", new Object[] { LocaleLoader.getString("Woodcutting.Ability.0"), LocaleLoader.getString("Woodcutting.Ability.1") }));
            }
        }

        if (canDoubleDrop && !doubleDropsDisabled) {
            player.sendMessage(LocaleLoader.getString("Woodcutting.Ability.Chance.DDrop", new Object[] { doubleDropChance }));
        }

        if (canTreeFell) {
            player.sendMessage(LocaleLoader.getString("Woodcutting.Ability.Length", new Object[] { treeFellerLength }));
        }
    }
}
