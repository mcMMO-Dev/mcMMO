package com.gmail.nossr50.skills.woodcutting;

import com.gmail.nossr50.config.AdvancedConfig;
import com.gmail.nossr50.config.Config;
import com.gmail.nossr50.locale.LocaleLoader;
import com.gmail.nossr50.skills.SkillCommand;
import com.gmail.nossr50.skills.SkillType;
import com.gmail.nossr50.util.Permissions;

public class WoodcuttingCommand extends SkillCommand {
    private String treeFellerLength;
    private String treeFellerLengthEndurance;
    private String doubleDropChance;
    private String doubleDropChanceLucky;

    private boolean canTreeFell;
    private boolean canLeafBlow;
    private boolean canDoubleDrop;

    public WoodcuttingCommand() {
        super(SkillType.WOODCUTTING);
    }

    @Override
    protected void dataCalculations() {
        //TREE FELLER
        String[] treeFellerStrings = calculateLengthDisplayValues();
        treeFellerLength = treeFellerStrings[0];
        treeFellerLengthEndurance = treeFellerStrings[1];

        //DOUBLE DROPS
        AdvancedConfig advancedConfig = AdvancedConfig.getInstance();
        String[] doubleDropStrings = calculateAbilityDisplayValues(advancedConfig.getWoodcuttingDoubleDropMaxLevel(), advancedConfig.getWoodcuttingDoubleDropChance());
        doubleDropChance = doubleDropStrings[0];
        doubleDropChanceLucky = doubleDropStrings[1];
    }

    @Override
    protected void permissionsCheck() {
        canTreeFell = Permissions.treeFeller(player);
        canDoubleDrop = Permissions.woodcuttingDoubleDrops(player);
        canLeafBlow = Permissions.leafBlower(player);
    }

    @Override
    protected boolean effectsHeaderPermissions() {
        return (canDoubleDrop && !Config.getInstance().woodcuttingDoubleDropsDisabled()) || canLeafBlow || canTreeFell;
    }

    @Override
    protected void effectsDisplay() {
        luckyEffectsDisplay();

        if (canTreeFell) {
            player.sendMessage(LocaleLoader.getString("Effects.Template", new Object[] { LocaleLoader.getString("Woodcutting.Effect.0"), LocaleLoader.getString("Woodcutting.Effect.1") }));
        }

        if (canLeafBlow) {
            player.sendMessage(LocaleLoader.getString("Effects.Template", new Object[] { LocaleLoader.getString("Woodcutting.Effect.2"), LocaleLoader.getString("Woodcutting.Effect.3") }));
        }

        if (canDoubleDrop && !Config.getInstance().woodcuttingDoubleDropsDisabled()) {
            player.sendMessage(LocaleLoader.getString("Effects.Template", new Object[] { LocaleLoader.getString("Woodcutting.Effect.4"), LocaleLoader.getString("Woodcutting.Effect.5") }));
        }
    }

    @Override
    protected boolean statsHeaderPermissions() {
        return (canDoubleDrop && !Config.getInstance().woodcuttingDoubleDropsDisabled()) || canLeafBlow || canTreeFell;
    }

    @Override
    protected void statsDisplay() {
        if (canLeafBlow) {
            int leafBlowerUnlockLevel = AdvancedConfig.getInstance().getLeafBlowUnlockLevel();

            if (skillValue < leafBlowerUnlockLevel) {
                player.sendMessage(LocaleLoader.getString("Ability.Generic.Template.Lock", new Object[] { LocaleLoader.getString("Woodcutting.Ability.Locked.0", new Object[] { leafBlowerUnlockLevel }) }));
            }
            else {
                player.sendMessage(LocaleLoader.getString("Ability.Generic.Template", new Object[] { LocaleLoader.getString("Woodcutting.Ability.0"), LocaleLoader.getString("Woodcutting.Ability.1") }));
            }
        }

        if (canDoubleDrop && !Config.getInstance().woodcuttingDoubleDropsDisabled()) {
            if (isLucky) {
                player.sendMessage(LocaleLoader.getString("Woodcutting.Ability.Chance.DDrop", new Object[] { doubleDropChance }) + LocaleLoader.getString("Perks.lucky.bonus", new Object[] { doubleDropChanceLucky }));
            }
            else {
                player.sendMessage(LocaleLoader.getString("Woodcutting.Ability.Chance.DDrop", new Object[] { doubleDropChance }));
            }
        }

        if (canTreeFell) {
            if (hasEndurance) {
                player.sendMessage(LocaleLoader.getString("Woodcutting.Ability.Length", new Object[] { treeFellerLength }) + LocaleLoader.getString("Perks.activationtime.bonus", new Object[] { treeFellerLengthEndurance }));
            }
            else {
                player.sendMessage(LocaleLoader.getString("Woodcutting.Ability.Length", new Object[] { treeFellerLength }));
            }
        }
    }
}
