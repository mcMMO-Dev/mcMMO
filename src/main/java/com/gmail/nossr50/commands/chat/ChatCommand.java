package com.gmail.nossr50.commands.chat;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.gmail.nossr50.chat.ChatMode;
import com.gmail.nossr50.datatypes.player.McMMOPlayer;
import com.gmail.nossr50.util.player.UserManager;

public abstract class ChatCommand implements CommandExecutor {
    protected McMMOPlayer mcMMOPlayer;
    protected ChatMode chatMode;

    public ChatCommand(ChatMode chatMode) {
        this.chatMode = chatMode;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        switch (args.length) {
            case 0:
                if (!(sender instanceof Player)) {
                    return false;
                }

                mcMMOPlayer = UserManager.getPlayer((Player) sender);

                if (chatMode.isEnabled(mcMMOPlayer)) {
                    disableChatMode(sender);
                }
                else {
                    enableChatMode(sender);
                }

                return true;

            case 1:
                if (args[0].equalsIgnoreCase("on")) {
                    if (!(sender instanceof Player)) {
                        return false;
                    }

                    enableChatMode(sender);
                    return true;
                }

                if (args[0].equalsIgnoreCase("off")) {
                    if (!(sender instanceof Player)) {
                        return false;
                    }

                    disableChatMode(sender);
                    return true;
                }

                // Fallthrough

            default:
                handleChatSending(sender, args);
                return true;
        }
    }

    protected String buildChatMessage(String[] args, int index) {
        StringBuilder builder = new StringBuilder();
        builder.append(args[index]);

        for (int i = index + 1; i < args.length; i++) {
            builder.append(" ");
            builder.append(args[i]);
        }

        return builder.toString();
    }

    protected abstract void handleChatSending(CommandSender sender, String[] args);

    private void enableChatMode(CommandSender sender) {
        chatMode.enable(mcMMOPlayer);
        sender.sendMessage(chatMode.getEnabledMessage());
    }

    private void disableChatMode(CommandSender sender) {
        chatMode.disable(mcMMOPlayer);
        sender.sendMessage(chatMode.getDisabledMessage());
    }
}
