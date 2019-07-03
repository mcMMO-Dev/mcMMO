package com.gmail.nossr50.commands.party;

import com.gmail.nossr50.datatypes.party.Party;
import com.gmail.nossr50.events.party.McMMOPartyChangeEvent.EventReason;
import com.gmail.nossr50.mcMMO;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class PartyKickCommand implements CommandExecutor {

    private final mcMMO pluginRef;

    public PartyKickCommand(mcMMO pluginRef) {
        this.pluginRef = pluginRef;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        switch (args.length) {
            case 2:
                if (pluginRef.getUserManager().getPlayer((Player) sender) == null) {
                    sender.sendMessage(pluginRef.getLocaleManager().getString("Profile.PendingLoad"));
                    return true;
                }

                Party playerParty = pluginRef.getUserManager().getPlayer((Player) sender).getParty();
                String targetName = pluginRef.getCommandTools().getMatchedPlayerName(args[1]);

                if (!playerParty.hasMember(targetName)) {
                    sender.sendMessage(pluginRef.getLocaleManager().getString("Party.NotInYourParty", targetName));
                    return true;
                }

                OfflinePlayer target = pluginRef.getServer().getOfflinePlayer(targetName);

                if (target.isOnline()) {
                    Player onlineTarget = target.getPlayer();
                    String partyName = playerParty.getName();

                    if (!pluginRef.getPartyManager().handlePartyChangeEvent(onlineTarget, partyName, null, EventReason.KICKED_FROM_PARTY)) {
                        return true;
                    }

                    pluginRef.getPartyManager().processPartyLeaving(pluginRef.getUserManager().getPlayer(onlineTarget));
                    onlineTarget.sendMessage(pluginRef.getLocaleManager().getString("Commands.Party.Kick", partyName));
                }

                pluginRef.getPartyManager().removeFromParty(target, playerParty);
                return true;

            default:
                sender.sendMessage(pluginRef.getLocaleManager().getString("Commands.Usage.2", "party", "kick", "<" + pluginRef.getLocaleManager().getString("Commands.Usage.Player") + ">"));
                return true;
        }
    }
}
