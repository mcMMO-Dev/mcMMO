package com.gmail.nossr50.commands.skills;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.gmail.nossr50.commands.CommandHelper;
import com.gmail.nossr50.datatypes.PlayerProfile;
import com.gmail.nossr50.datatypes.SkillType;
import com.gmail.nossr50.locale.LocaleLoader;
import com.gmail.nossr50.util.Page;
import com.gmail.nossr50.util.Users;

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

        player.sendMessage(LocaleLoader.getString("Skills.Header", new Object[] { LocaleLoader.getString("Unarmed.SkillName") }));
        player.sendMessage(LocaleLoader.getString("Commands.XPGain", new Object[] { LocaleLoader.getString("Commands.XPGain.Unarmed") }));
        player.sendMessage(LocaleLoader.getString("Effects.Level", new Object[] { PP.getSkillLevel(SkillType.UNARMED), PP.getSkillXpLevel(SkillType.UNARMED), PP.getXpToLevel(SkillType.UNARMED) }));

        player.sendMessage(LocaleLoader.getString("Skills.Header", new Object[] { LocaleLoader.getString("Effects.Effects") }));
        player.sendMessage(LocaleLoader.getString("Effects.Template", new Object[] { LocaleLoader.getString("Unarmed.Effect.0"), LocaleLoader.getString("Unarmed.Effect.1") }));
        player.sendMessage(LocaleLoader.getString("Effects.Template", new Object[] { LocaleLoader.getString("Unarmed.Effect.2"), LocaleLoader.getString("Unarmed.Effect.3") }));
        player.sendMessage(LocaleLoader.getString("Effects.Template", new Object[] { LocaleLoader.getString("Unarmed.Effect.4"), LocaleLoader.getString("Unarmed.Effect.5") }));
        player.sendMessage(LocaleLoader.getString("Effects.Template", new Object[] { LocaleLoader.getString("Unarmed.Effect.6"), LocaleLoader.getString("Unarmed.Effect.7") }));

        player.sendMessage(LocaleLoader.getString("Skills.Header", new Object[] { LocaleLoader.getString("Commands.Stats.Self") }));
        player.sendMessage(LocaleLoader.getString("Ability.Generic.Template", new Object[] { LocaleLoader.getString("Unarmed.Ability.Bonus.0"), LocaleLoader.getString("Unarmed.Ability.Bonus.1", new Object[] {ironArmBonus}) }));
        player.sendMessage(LocaleLoader.getString("Unarmed.Ability.Chance.ArrowDeflect", new Object[] { deflectChance }));
        player.sendMessage(LocaleLoader.getString("Unarmed.Ability.Chance.Disarm", new Object[] { disarmChance }));
        player.sendMessage(LocaleLoader.getString("Unarmed.Ability.Berserk.Length", new Object[] { berserkLength }));

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
