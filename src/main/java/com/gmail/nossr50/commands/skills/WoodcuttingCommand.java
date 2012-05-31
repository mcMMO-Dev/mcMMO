package com.gmail.nossr50.commands.skills;

import com.gmail.nossr50.commands.SkillCommand;
import com.gmail.nossr50.config.Config;
import com.gmail.nossr50.datatypes.SkillType;
import com.gmail.nossr50.locale.LocaleLoader;

public class WoodcuttingCommand extends SkillCommand {
    private String treeFellerLength;
    private String doubleDropChance;

    private boolean canTreeFell;
    private boolean canLeafBlow;
    private boolean canDoubleDrop;
    private boolean doubleDropsDisabled;

    public WoodcuttingCommand() {
        super(SkillType.WOODCUTTING);
    }

    @Override
    protected void dataCalculations() {
        treeFellerLength = String.valueOf(2 + ((int) skillValue / 50));

        if (skillValue >= 1000) {
            doubleDropChance = "100.00%";
        }
        else {
            doubleDropChance = percent.format(skillValue / 1000);
        }
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
            if (skillValue < 100) {
                player.sendMessage(LocaleLoader.getString("Ability.Generic.Template.Lock", new Object[] { LocaleLoader.getString("Woodcutting.Ability.Locked.0") }));
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
