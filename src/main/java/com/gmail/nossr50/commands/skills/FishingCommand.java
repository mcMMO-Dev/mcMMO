package com.gmail.nossr50.commands.skills;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.gmail.nossr50.commands.CommandHelper;
import com.gmail.nossr50.datatypes.PlayerProfile;
import com.gmail.nossr50.datatypes.SkillType;
import com.gmail.nossr50.locale.LocaleLoader;
import com.gmail.nossr50.skills.Fishing;
import com.gmail.nossr50.util.Page;
import com.gmail.nossr50.util.Users;

public class FishingCommand implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (CommandHelper.noConsoleUsage(sender)) {
            return true;
        }

        if (CommandHelper.noCommandPermissions(sender, "mcmmo.skills.fishing")) {
            return true;
        }

        Player player = (Player) sender;
        PlayerProfile PP = Users.getProfile(player);

        player.sendMessage(LocaleLoader.getString("Skills.Header", new Object[] { LocaleLoader.getString("Fishing.SkillName") }));
        player.sendMessage(LocaleLoader.getString("Commands.XPGain", new Object[] { LocaleLoader.getString("Commands.XPGain.Fishing") }));
        player.sendMessage(LocaleLoader.getString("Effects.Level", new Object[] { PP.getSkillLevel(SkillType.FISHING), PP.getSkillXpLevel(SkillType.FISHING), PP.getXpToLevel(SkillType.FISHING) }));

        player.sendMessage(LocaleLoader.getString("Skills.Header", new Object[] { LocaleLoader.getString("Effects.Effects") }));
        player.sendMessage(LocaleLoader.getString("Effects.Template", new Object[] { LocaleLoader.getString("Fishing.Effect.0"), LocaleLoader.getString("Fishing.Effect.1") }));
        player.sendMessage(LocaleLoader.getString("Effects.Template", new Object[] { LocaleLoader.getString("Fishing.Effect.2"), LocaleLoader.getString("Fishing.Effect.3") }));
        player.sendMessage(LocaleLoader.getString("Effects.Template", new Object[] { LocaleLoader.getString("Fishing.Effect.4"), LocaleLoader.getString("Fishing.Effect.5") }));

        player.sendMessage(LocaleLoader.getString("Skills.Header", new Object[] { LocaleLoader.getString("Commands.Stats.Self") }));
        player.sendMessage(LocaleLoader.getString("Fishing.Ability.Rank", new Object[] { Fishing.getFishingLootTier(PP) }));
        player.sendMessage(LocaleLoader.getString("Fishing.Ability.Info"));

        //TODO: Do we really need to display this twice? Not like there are any associated stats.
        if (PP.getSkillLevel(SkillType.FISHING) < 150) {
            player.sendMessage(LocaleLoader.getString("Ability.Generic.Template.Lock", new Object[] { LocaleLoader.getString("Fishing.Ability.Locked.0") }));
        }
        else {
            player.sendMessage(LocaleLoader.getString("Fishing.Ability.Shake"));
        }

        Page.grabGuidePageForSkill(SkillType.FISHING, player, args);

        return true;
    }
}
