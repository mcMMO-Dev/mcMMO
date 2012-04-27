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

        player.sendMessage(LocaleLoader.getString("Skills.Header", new Object[] { LocaleLoader.getString("WoodCutting.SkillName") }));
        player.sendMessage(LocaleLoader.getString("Commands.XPGain", new Object[] { LocaleLoader.getString("Commands.XPGain.WoodCutting") }));
        player.sendMessage(LocaleLoader.getString("Effects.Level", new Object[] { PP.getSkillLevel(SkillType.WOODCUTTING), PP.getSkillXpLevel(SkillType.WOODCUTTING), PP.getXpToLevel(SkillType.WOODCUTTING) }));

        player.sendMessage(LocaleLoader.getString("Skills.Header", new Object[] { LocaleLoader.getString("Effects.Effects") }));
        player.sendMessage(LocaleLoader.getString("Effects.Template", new Object[] { LocaleLoader.getString("Woodcutting.Effect.0"), LocaleLoader.getString("Woodcutting.Effect.1") }));
        player.sendMessage(LocaleLoader.getString("Effects.Template", new Object[] { LocaleLoader.getString("Woodcutting.Effect.2"), LocaleLoader.getString("Woodcutting.Effect.3") }));
        player.sendMessage(LocaleLoader.getString("Effects.Template", new Object[] { LocaleLoader.getString("Woodcutting.Effect.4"), LocaleLoader.getString("Woodcutting.Effect.5") }));

        player.sendMessage(LocaleLoader.getString("Skills.Header", new Object[] { LocaleLoader.getString("Commands.Stats.Self") }));

        if (PP.getSkillLevel(SkillType.WOODCUTTING) < 100) {
            player.sendMessage(LocaleLoader.getString("Ability.Generic.Template.Lock", new Object[] { LocaleLoader.getString("Woodcutting.Ability.Locked.0") }));
        }
        else {
            player.sendMessage(LocaleLoader.getString("Ability.Generic.Template", new Object[] { LocaleLoader.getString("Woodcutting.Ability.0"), LocaleLoader.getString("Woodcutting.Ability.1") }));
        }

        player.sendMessage(LocaleLoader.getString("Woodcutting.Ability.Chance.DDrop", new Object[] { doubleDropChance }));
        player.sendMessage(LocaleLoader.getString("Woodcutting.Ability.Length", new Object[] { treeFellerLength }));
        
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
