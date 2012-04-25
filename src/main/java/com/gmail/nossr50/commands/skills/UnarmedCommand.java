package com.gmail.nossr50.commands.skills;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.gmail.nossr50.Users;
import com.gmail.nossr50.commands.CommandHelper;
import com.gmail.nossr50.datatypes.PlayerProfile;
import com.gmail.nossr50.datatypes.SkillType;
import com.gmail.nossr50.locale.mcLocale;
import com.gmail.nossr50.util.Page;

public class UnarmedCommand implements CommandExecutor {
    private float skillValue;
    private String berserkLength;
    private String deflectChance;
    private String disarmChance;
    private String ironArmBonus;

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (CommandHelper.noConsoleUsage(sender)) {
            return true;
        }

        if (CommandHelper.noCommandPermissions(sender, "mcmmo.skills.unarmed")) {
            return true;
        }

        Player player = (Player) sender;
        PlayerProfile PP = Users.getProfile(player);

        skillValue = (float) PP.getSkillLevel(SkillType.UNARMED);
        dataCalculations(skillValue);

        player.sendMessage(mcLocale.getString("Skills.Header", new Object[] { mcLocale.getString("Unarmed.SkillName") }));
        player.sendMessage(mcLocale.getString("Commands.XPGain", new Object[] { mcLocale.getString("Commands.XPGain.Unarmed") }));
        player.sendMessage(mcLocale.getString("Effects.Level", new Object[] { PP.getSkillLevel(SkillType.UNARMED), PP.getSkillXpLevel(SkillType.UNARMED), PP.getXpToLevel(SkillType.UNARMED) }));

        player.sendMessage(mcLocale.getString("Skills.Header", new Object[] { mcLocale.getString("Effects.Effects") }));
        player.sendMessage(mcLocale.getString("Effects.Template", new Object[] { mcLocale.getString("Unarmed.Effect.0"), mcLocale.getString("Unarmed.Effect.1") }));
        player.sendMessage(mcLocale.getString("Effects.Template", new Object[] { mcLocale.getString("Unarmed.Effect.2"), mcLocale.getString("Unarmed.Effect.3") }));
        player.sendMessage(mcLocale.getString("Effects.Template", new Object[] { mcLocale.getString("Unarmed.Effect.4"), mcLocale.getString("Unarmed.Effect.5") }));
        player.sendMessage(mcLocale.getString("Effects.Template", new Object[] { mcLocale.getString("Unarmed.Effect.6"), mcLocale.getString("Unarmed.Effect.7") }));

        player.sendMessage(mcLocale.getString("Skills.Header", new Object[] { mcLocale.getString("Commands.Stats.Self") }));
        player.sendMessage(mcLocale.getString("Ability.Generic.Template", new Object[] { mcLocale.getString("Unarmed.Ability.Bonus.0"), mcLocale.getString("Unarmed.Ability.Bonus.1", new Object[] {ironArmBonus}) }));
        player.sendMessage(mcLocale.getString("Unarmed.Ability.Chance.ArrowDeflect", new Object[] { deflectChance }));
        player.sendMessage(mcLocale.getString("Unarmed.Ability.Chance.Disarm", new Object[] { disarmChance }));
        player.sendMessage(mcLocale.getString("Unarmed.Ability.Berserk.Length", new Object[] { berserkLength }));

        Page.grabGuidePageForSkill(SkillType.UNARMED, player, args);

        return true;
    }

    private void dataCalculations(float skillValue) {
        berserkLength = String.valueOf(2 + ((int) skillValue / 50));

        if (skillValue >= 1000) {
            disarmChance = "33.3";
            deflectChance = "50";
            ironArmBonus = "8";
        }
        else if (skillValue >= 250) {
            disarmChance = String.valueOf(skillValue / 30);
            deflectChance = String.valueOf(skillValue / 20);
            ironArmBonus = "8";
        }
        else {
            disarmChance = String.valueOf(skillValue / 30);
            deflectChance = String.valueOf(skillValue / 20);
            ironArmBonus = String.valueOf(3 + ((int) skillValue / 50));
        }
    }
}
