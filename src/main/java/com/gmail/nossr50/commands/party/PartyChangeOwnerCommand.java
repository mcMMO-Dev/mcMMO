package com.gmail.nossr50.commands.party;

import com.gmail.nossr50.datatypes.party.Party;
import com.gmail.nossr50.mcMMO;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class PartyChangeOwnerCommand implements CommandExecutor {

    private mcMMO pluginRef;

    public PartyChangeOwnerCommand(mcMMO pluginRef) {
        this.pluginRef = pluginRef;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        switch (args.length) {
            case 2:
                //Check if player profile is loaded
                if (pluginRef.getUserManager().getPlayer((Player) sender) == null) {
                    sender.sendMessage(pluginRef.getLocaleManager().getString("Profile.PendingLoad"));
                    return true;
                }

                Party playerParty = pluginRef.getUserManager().getPlayer((Player) sender).getParty();
                String targetName = pluginRef.getCommandTools().getMatchedPlayerName(args[1]);
                OfflinePlayer target = pluginRef.getServer().getOfflinePlayer(targetName);

                if (!playerParty.hasMember(target.getUniqueId())) {
                    sender.sendMessage(pluginRef.getLocaleManager().getString("Party.NotInYourParty", targetName));
                    return true;
                }

                pluginRef.getPartyManager().setPartyLeader(target.getUniqueId(), playerParty);
                return true;

            default:
                sender.sendMessage(pluginRef.getLocaleManager().getString("Commands.Usage.2", "party", "owner", "<" + pluginRef.getLocaleManager().getString("Commands.Usage.Player") + ">"));
                return true;
        }
    }
}
