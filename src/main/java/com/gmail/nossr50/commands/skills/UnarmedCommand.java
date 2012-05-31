package com.gmail.nossr50.commands.skills;

import com.gmail.nossr50.commands.SkillCommand;
import com.gmail.nossr50.datatypes.SkillType;
import com.gmail.nossr50.locale.LocaleLoader;

public class UnarmedCommand extends SkillCommand {
    private String berserkLength;
    private String deflectChance;
    private String disarmChance;
    private String ironArmBonus;

    private boolean canBerserk;
    private boolean canDisarm;
    private boolean canBonusDamage;
    private boolean canDeflect;

    public UnarmedCommand() {
        super(SkillType.UNARMED);
    }

    @Override
    protected void dataCalculations() {
        berserkLength = String.valueOf(2 + ((int) skillValue / 50));

        if (skillValue >= 1000) {
            disarmChance = "33.33%";
            deflectChance = "50.00%";
            ironArmBonus = "8";
        }
        else if (skillValue >= 250) {
            disarmChance = percent.format(skillValue / 3000);
            deflectChance = percent.format(skillValue / 2000);
            ironArmBonus = "8";
        }
        else {
            disarmChance = percent.format(skillValue / 3000);
            deflectChance = percent.format(skillValue / 2000);
            ironArmBonus = String.valueOf(3 + ((int) skillValue / 50));
        }    }

    @Override
    protected void permissionsCheck() {
        canBerserk = permInstance.berserk(player);
        canBonusDamage = permInstance.unarmedBonus(player);
        canDeflect = permInstance.deflect(player);
        canDisarm = permInstance.disarm(player);
    }

    @Override
    protected boolean effectsHeaderPermissions() {
        return canBerserk || canBonusDamage || canDeflect || canDisarm;
    }

    @Override
    protected void effectsDisplay() {
        if (canBerserk) {
            player.sendMessage(LocaleLoader.getString("Effects.Template", new Object[] { LocaleLoader.getString("Unarmed.Effect.0"), LocaleLoader.getString("Unarmed.Effect.1") }));
        }

        if (canDisarm) {
            player.sendMessage(LocaleLoader.getString("Effects.Template", new Object[] { LocaleLoader.getString("Unarmed.Effect.2"), LocaleLoader.getString("Unarmed.Effect.3") }));
        }

        if (canBonusDamage) {
            player.sendMessage(LocaleLoader.getString("Effects.Template", new Object[] { LocaleLoader.getString("Unarmed.Effect.4"), LocaleLoader.getString("Unarmed.Effect.5") }));
        }

        if (canDeflect) {
            player.sendMessage(LocaleLoader.getString("Effects.Template", new Object[] { LocaleLoader.getString("Unarmed.Effect.6"), LocaleLoader.getString("Unarmed.Effect.7") }));
        }
    }

    @Override
    protected boolean statsHeaderPermissions() {
        return canBerserk || canBonusDamage || canDeflect || canDisarm;
    }

    @Override
    protected void statsDisplay() {
        if (canBonusDamage) {
            player.sendMessage(LocaleLoader.getString("Ability.Generic.Template", new Object[] { LocaleLoader.getString("Unarmed.Ability.Bonus.0"), LocaleLoader.getString("Unarmed.Ability.Bonus.1", new Object[] {ironArmBonus}) }));
        }

        if (canDeflect) {
            player.sendMessage(LocaleLoader.getString("Unarmed.Ability.Chance.ArrowDeflect", new Object[] { deflectChance }));
        }

        if (canDisarm) {
            player.sendMessage(LocaleLoader.getString("Unarmed.Ability.Chance.Disarm", new Object[] { disarmChance }));
        }

        if (canBerserk) {
            player.sendMessage(LocaleLoader.getString("Unarmed.Ability.Berserk.Length", new Object[] { berserkLength }));
        }
    }
}
