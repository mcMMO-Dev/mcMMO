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
import com.gmail.nossr50.skills.Fishing;
import com.gmail.nossr50.util.Page;

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

        player.sendMessage(mcLocale.getString("Skills.Header", new Object[] { mcLocale.getString("Fishing.SkillName") }));
        player.sendMessage(mcLocale.getString("Commands.XPGain", new Object[] { mcLocale.getString("Commands.XPGain.Fishing") }));
        player.sendMessage(mcLocale.getString("Effects.Level", new Object[] { PP.getSkillLevel(SkillType.FISHING), PP.getSkillXpLevel(SkillType.FISHING), PP.getXpToLevel(SkillType.FISHING) }));

        player.sendMessage(mcLocale.getString("Skills.Header", new Object[] { mcLocale.getString("Effects.Effects") }));
        player.sendMessage(mcLocale.getString("Effects.Template", new Object[] { mcLocale.getString("Fishing.Effect.0"), mcLocale.getString("Fishing.Effect.1") }));
        player.sendMessage(mcLocale.getString("Effects.Template", new Object[] { mcLocale.getString("Fishing.Effect.2"), mcLocale.getString("Fishing.Effect.3") }));
        player.sendMessage(mcLocale.getString("Effects.Template", new Object[] { mcLocale.getString("Fishing.Effect.4"), mcLocale.getString("Fishing.Effect.5") }));

        player.sendMessage(mcLocale.getString("Skills.Header", new Object[] { mcLocale.getString("Commands.Stats.Self") }));
        player.sendMessage(mcLocale.getString("Fishing.Ability.Rank", new Object[] { Fishing.getFishingLootTier(PP) }));
        player.sendMessage(mcLocale.getString("Fishing.Ability.Info"));

        //TODO: Do we really need to display this twice? Not like there are any associated stats.
        if (PP.getSkillLevel(SkillType.FISHING) < 150) {
            player.sendMessage(mcLocale.getString("Ability.Generic.Template.Lock", new Object[] { mcLocale.getString("Fishing.Ability.Locked.0") }));
        }
        else {
            player.sendMessage(mcLocale.getString("Fishing.Ability.Shake"));
        }

        Page.grabGuidePageForSkill(SkillType.FISHING, player, args);

        return true;
    }
}
