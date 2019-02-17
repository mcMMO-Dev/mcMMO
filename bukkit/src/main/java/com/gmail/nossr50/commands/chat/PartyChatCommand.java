package com.gmail.nossr50.commands.chat;

import com.gmail.nossr50.core.chat.PartyChatManager;
import com.gmail.nossr50.core.config.MainConfig;
import com.gmail.nossr50.core.data.UserManager;
import com.gmail.nossr50.core.datatypes.chat.ChatMode;
import com.gmail.nossr50.core.datatypes.party.Party;
import com.gmail.nossr50.core.datatypes.party.PartyFeature;
import com.gmail.nossr50.core.locale.LocaleLoader;
import com.gmail.nossr50.core.party.PartyManager;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class PartyChatCommand extends ChatCommand {
    public PartyChatCommand() {
        super(ChatMode.PARTY);
    }

    @Override
    protected void handleChatSending(CommandSender sender, String[] args) {
        Party party;
        String message;

        if (sender instanceof Player) {
            party = UserManager.getPlayer((Player) sender).getParty();

            if (party == null) {
                sender.sendMessage(LocaleLoader.getString("Commands.Party.None"));
                return;
            }

            if (party.getLevel() < MainConfig.getInstance().getPartyFeatureUnlockLevel(PartyFeature.CHAT)) {
                sender.sendMessage(LocaleLoader.getString("Party.Feature.Disabled.1"));
                return;
            }

            message = buildChatMessage(args, 0);
        } else {
            if (args.length < 2) {
                sender.sendMessage(LocaleLoader.getString("Party.Specify"));
                return;
            }

            party = PartyManager.getParty(args[0]);

            if (party == null) {
                sender.sendMessage(LocaleLoader.getString("Party.InvalidName"));
                return;
            }

            message = buildChatMessage(args, 1);
        }

        ((PartyChatManager) chatManager).setParty(party);
        chatManager.handleChat(sender.getName(), getDisplayName(sender), message);
    }
}
