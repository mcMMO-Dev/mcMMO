package com.gmail.nossr50.commands.party;

import com.gmail.nossr50.locale.LocaleLoader;
import com.gmail.nossr50.mcMMO;
import com.gmail.nossr50.util.commands.CommandSyntaxFormatter;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

public class PartyHelpCommand implements CommandExecutor {

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command,
            @NotNull String label, String[] args) {
        if (args.length == 1) {
            sender.sendMessage(
                    LocaleLoader.getString("Party.Help.3",
                            CommandSyntaxFormatter.command("party", "join"),
                            CommandSyntaxFormatter.command("party", "quit")));
            sender.sendMessage(LocaleLoader.getString("Party.Help.1",
                    CommandSyntaxFormatter.command("party", "create")));
            sender.sendMessage(LocaleLoader.getString("Party.Help.4",
                    "/" + mcMMO.p.getCommandExposureRegistry().getDisplayRoot("party") + " <"
                            + mcMMO.p.getCommandExposureRegistry().getPreferredDisplayToken(
                                    "party", "lock")
                            + "|"
                            + mcMMO.p.getCommandExposureRegistry().getPreferredDisplayToken(
                                    "party", "unlock")
                            + ">"));
            sender.sendMessage(LocaleLoader.getString("Party.Help.5",
                    CommandSyntaxFormatter.command("party", "password")));
            sender.sendMessage(LocaleLoader.getString("Party.Help.6",
                    CommandSyntaxFormatter.command("party", "kick")));
            sender.sendMessage(LocaleLoader.getString("Party.Help.7",
                    CommandSyntaxFormatter.command("party", "owner")));
            sender.sendMessage(LocaleLoader.getString("Party.Help.8",
                    CommandSyntaxFormatter.command("party", "disband")));
            sender.sendMessage(LocaleLoader.getString("Party.Help.9",
                    CommandSyntaxFormatter.command("party", "itemshare")));
            sender.sendMessage(LocaleLoader.getString("Party.Help.10",
                    CommandSyntaxFormatter.command("party", "xpshare")));
            return true;
        }
        sender.sendMessage(CommandSyntaxFormatter.transformText(
                LocaleLoader.getString("Commands.Usage.1", "party", "help")));
        return true;
    }
}
