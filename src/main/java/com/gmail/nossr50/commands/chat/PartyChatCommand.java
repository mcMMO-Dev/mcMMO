package com.gmail.nossr50.commands.chat;

import com.gmail.nossr50.datatypes.chat.ChatMode;
import com.gmail.nossr50.datatypes.party.Party;
import com.gmail.nossr50.datatypes.party.PartyFeature;
import com.gmail.nossr50.mcMMO;
import com.gmail.nossr50.util.player.UserManager;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class PartyChatCommand extends ChatCommand {
    public PartyChatCommand(mcMMO pluginRef) {
        super(ChatMode.PARTY, pluginRef);
    }

    @Override
    protected void handleChatSending(CommandSender sender, String[] args) {
        Party party;
        String message;

        if (sender instanceof Player) {
            //Check if player profile is loaded
            if (UserManager.getPlayer((Player) sender) == null)
                return;

            party = UserManager.getPlayer((Player) sender).getParty();

            if (party == null) {
                sender.sendMessage(pluginRef.getLocaleManager().getString("Commands.Party.None"));
                return;
            }

            if (party.getLevel() < pluginRef.getPartyManager().getPartyFeatureUnlockLevel(PartyFeature.CHAT)) {
                sender.sendMessage(pluginRef.getLocaleManager().getString("Party.Feature.Disabled.1"));
                return;
            }

            message = buildChatMessage(args, 0);
        } else {
            if (args.length < 2) {
                sender.sendMessage(pluginRef.getLocaleManager().getString("Party.Specify"));
                return;
            }

            party = pluginRef.getPartyManager().getParty(args[0]);

            if (party == null) {
                sender.sendMessage(pluginRef.getLocaleManager().getString("Party.InvalidName"));
                return;
            }

            message = buildChatMessage(args, 1);
        }

        pluginRef.getChatManager().processPartyChat(party, getDisplayName(sender), message);
    }
}
