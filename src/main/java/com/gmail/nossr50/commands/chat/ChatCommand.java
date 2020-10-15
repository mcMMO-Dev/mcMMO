package com.gmail.nossr50.commands.chat;

import com.gmail.nossr50.chat.ChatManager;
import com.gmail.nossr50.chat.ChatManagerFactory;
import com.gmail.nossr50.datatypes.chat.ChatMode;
import com.gmail.nossr50.datatypes.party.Party;
import com.gmail.nossr50.datatypes.party.PartyFeature;
import com.gmail.nossr50.datatypes.player.McMMOPlayer;
import com.gmail.nossr50.locale.LocaleLoader;
import com.gmail.nossr50.mcMMO;
import com.gmail.nossr50.util.commands.CommandUtils;
import com.gmail.nossr50.util.player.PartyUtils;
import com.google.common.collect.ImmutableList;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;
import org.bukkit.util.StringUtil;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public abstract class ChatCommand implements TabExecutor {
    private final ChatMode chatMode;
    protected ChatManager chatManager;

    public ChatCommand(ChatMode chatMode) {
        this.chatMode = chatMode;
        this.chatManager = ChatManagerFactory.getChatManager(mcMMO.p, chatMode);
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, String[] args) {
        McMMOPlayer mmoPlayer;
        Player player;

        if(sender instanceof Player) {
            player = (Player) sender;

            if (!CommandUtils.hasPlayerDataKey(sender)) {
                return true;
            } else {
                mmoPlayer = mcMMO.getUserManager().queryMcMMOPlayer(player);

                switch (args.length) {
                    case 0:
                        if (CommandUtils.noConsoleUsage(sender)) {
                            return true;
                        }

                        if (!CommandUtils.hasPlayerDataKey(sender)) {
                            return true;
                        }

                        if (mmoPlayer.isChatEnabled(chatMode)) {
                            disableChatMode(mmoPlayer, sender);
                        }
                        else {
                            enableChatMode(mmoPlayer, sender);
                        }

                        return true;

                    case 1:
                        if (CommandUtils.shouldEnableToggle(args[0])) {
                            if (CommandUtils.noConsoleUsage(sender)) {
                                return true;
                            }
                            if (!CommandUtils.hasPlayerDataKey(sender)) {
                                return true;
                            }

                            enableChatMode(mcMMO.getUserManager().queryMcMMOPlayer(player), sender);
                            return true;
                        }

                        if (CommandUtils.shouldDisableToggle(args[0])) {
                            if (CommandUtils.noConsoleUsage(sender)) {
                                return true;
                            }
                            if (!CommandUtils.hasPlayerDataKey(sender)) {
                                return true;
                            }

                            disableChatMode(mcMMO.getUserManager().queryMcMMOPlayer(player), sender);
                            return true;
                        }

                        // Fallthrough

                    default:
                        handleChatSending(sender, args);
                        return true;
                }


            }
        } else {
            sender.sendMessage(LocaleLoader.getString("Commands.NoConsole"));
            return true;
        }
    }

    @Override
    public List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String alias, String[] args) {
        if (args.length == 1) {
            return StringUtil.copyPartialMatches(args[0], CommandUtils.TRUE_FALSE_OPTIONS, new ArrayList<>(CommandUtils.TRUE_FALSE_OPTIONS.size()));
        }
        return ImmutableList.of();
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

    protected String getDisplayName(CommandSender sender) {
        return (sender instanceof Player) ? ((Player) sender).getDisplayName() : LocaleLoader.getString("Commands.Chat.Console");
    }

    protected abstract void handleChatSending(CommandSender sender, @NotNull String[] args);

    private void enableChatMode(@NotNull McMMOPlayer mmoPlayer, @NotNull CommandSender sender) {
        if (chatMode == ChatMode.PARTY) {
            Party party = mmoPlayer.getParty();
            if(party == null) {
                sender.sendMessage(LocaleLoader.getString("Commands.Party.None"));
                return;
            }

            if(PartyUtils.isAllowed(party, PartyFeature.CHAT)) {
                sender.sendMessage(LocaleLoader.getString("Party.Feature.Disabled.1"));
                return;
            }
        }

        mmoPlayer.enableChat(chatMode);
        sender.sendMessage(chatMode.getEnabledMessage());
    }

    private void disableChatMode(McMMOPlayer mmoPlayer, CommandSender sender) {
        if (chatMode == ChatMode.PARTY && mmoPlayer.getParty() == null) {
            sender.sendMessage(LocaleLoader.getString("Commands.Party.None"));
            return;
        }

        mmoPlayer.disableChat(chatMode);
        sender.sendMessage(chatMode.getDisabledMessage());
    }
}
