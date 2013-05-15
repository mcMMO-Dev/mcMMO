package com.gmail.nossr50.commands.party;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import com.gmail.nossr50.locale.LocaleLoader;

public class PartyHelpCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        switch (args.length) {
            case 1:
                sender.sendMessage(LocaleLoader.getString("Party.Help.3", "/party join", "/party quit"));
                sender.sendMessage(LocaleLoader.getString("Party.Help.1", "/party create"));
                sender.sendMessage(LocaleLoader.getString("Party.Help.4", "/party <lock|unlock>"));
                sender.sendMessage(LocaleLoader.getString("Party.Help.5", "/party password"));
                sender.sendMessage(LocaleLoader.getString("Party.Help.6", "/party kick"));
                sender.sendMessage(LocaleLoader.getString("Party.Help.7", "/party leader"));
                sender.sendMessage(LocaleLoader.getString("Party.Help.8", "/party disband"));
                sender.sendMessage(LocaleLoader.getString("Party.Help.9", "/party itemshare"));
                sender.sendMessage(LocaleLoader.getString("Party.Help.10", "/party expshare"));
                return true;

            default:
                sender.sendMessage(LocaleLoader.getString("Commands.Usage.1", "party", "help"));
                return true;
        }
    }
}
