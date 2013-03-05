package com.gmail.nossr50.commands.chat;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.gmail.nossr50.mcMMO;
import com.gmail.nossr50.chat.ChatManager;
import com.gmail.nossr50.chat.ChatMode;
import com.gmail.nossr50.datatypes.party.Party;
import com.gmail.nossr50.locale.LocaleLoader;
import com.gmail.nossr50.party.PartyManager;
import com.gmail.nossr50.util.player.UserManager;

public class PartyChatCommand extends ChatCommand {
    public PartyChatCommand() {
        super(ChatMode.PARTY);
    }

    @Override
    protected void handleChatSending(CommandSender sender, String[] args) {
        if (sender instanceof Player) {
            Player player = (Player) sender;
            Party party = UserManager.getPlayer(player).getParty();

            if (party == null) {
                sender.sendMessage(LocaleLoader.getString("Commands.Party.None"));
                return;
            }

            ChatManager.handlePartyChat(mcMMO.p, party, player.getName(), player.getDisplayName(), buildChatMessage(args, 0));
        }
        else {
            if (args.length < 2) {
                sender.sendMessage(LocaleLoader.getString("Party.Specify"));
                return;
            }

            Party party = PartyManager.getParty(args[0]);

            if (party == null) {
                sender.sendMessage(LocaleLoader.getString("Party.InvalidName"));
                return;
            }

            ChatManager.handlePartyChat(mcMMO.p, party, LocaleLoader.getString("Commands.Chat.Console"), buildChatMessage(args, 1));
        }
    }
}
