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

public class WoodcuttingCommand implements CommandExecutor {
    private float skillValue;
    private String treeFellerLength;
    private String doubleDropChance;

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (CommandHelper.noConsoleUsage(sender)) {
            return true;
        }

        if (CommandHelper.noCommandPermissions(sender, "mcmmo.skills.woodcutting")) {
            return true;
        }

        Player player = (Player) sender;
        PlayerProfile PP = Users.getProfile(player);

        skillValue = (float) PP.getSkillLevel(SkillType.UNARMED);
        dataCalculations(skillValue);

        player.sendMessage(mcLocale.getString("Skills.Header", new Object[] { mcLocale.getString("WoodCutting.SkillName") }));
        player.sendMessage(mcLocale.getString("Commands.XPGain", new Object[] { mcLocale.getString("Commands.XPGain.WoodCutting") }));
        player.sendMessage(mcLocale.getString("Effects.Level", new Object[] { PP.getSkillLevel(SkillType.WOODCUTTING), PP.getSkillXpLevel(SkillType.WOODCUTTING), PP.getXpToLevel(SkillType.WOODCUTTING) }));

        player.sendMessage(mcLocale.getString("Skills.Header", new Object[] { mcLocale.getString("Effects.Effects") }));
        player.sendMessage(mcLocale.getString("Effects.Template", new Object[] { mcLocale.getString("Woodcutting.Effect.0"), mcLocale.getString("Woodcutting.Effect.1") }));
        player.sendMessage(mcLocale.getString("Effects.Template", new Object[] { mcLocale.getString("Woodcutting.Effect.2"), mcLocale.getString("Woodcutting.Effect.3") }));
        player.sendMessage(mcLocale.getString("Effects.Template", new Object[] { mcLocale.getString("Woodcutting.Effect.4"), mcLocale.getString("Woodcutting.Effect.5") }));

        player.sendMessage(mcLocale.getString("Skills.Header", new Object[] { mcLocale.getString("Commands.Stats.Self") }));

        if (PP.getSkillLevel(SkillType.WOODCUTTING) < 100) {
            player.sendMessage(mcLocale.getString("Ability.Generic.Template.Lock", new Object[] { mcLocale.getString("Woodcutting.Ability.Locked.0") }));
        }
        else {
            player.sendMessage(mcLocale.getString("Ability.Generic.Template", new Object[] { mcLocale.getString("Woodcutting.Ability.0"), mcLocale.getString("Woodcutting.Ability.1") }));
        }

        player.sendMessage(mcLocale.getString("Woodcutting.Ability.Chance.DDrop", new Object[] { doubleDropChance }));
        player.sendMessage(mcLocale.getString("Woodcutting.Ability.Length", new Object[] { treeFellerLength }));
        
        Page.grabGuidePageForSkill(SkillType.WOODCUTTING, player, args);
        
        return true;
    }

    private void dataCalculations(float skillValue) {
        treeFellerLength = String.valueOf(2 + ((int) skillValue / 50));

        if (skillValue >= 1000) {
            doubleDropChance = "100";
        }
        else {
            doubleDropChance = String.valueOf(skillValue / 10);
        }
    }
}
