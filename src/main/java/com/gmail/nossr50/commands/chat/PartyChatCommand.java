package com.gmail.nossr50.commands.chat;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.BukkitCommandIssuer;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.CommandPermission;
import co.aikar.commands.annotation.Conditions;
import co.aikar.commands.annotation.Default;
import com.gmail.nossr50.commands.CommandManager;
import com.gmail.nossr50.datatypes.chat.ChatChannel;
import com.gmail.nossr50.datatypes.party.Party;
import com.gmail.nossr50.datatypes.player.McMMOPlayer;
import com.gmail.nossr50.mcMMO;
import com.gmail.nossr50.util.player.UserManager;
import com.gmail.nossr50.util.text.StringUtils;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

@CommandPermission("mcmmo.chat.partychat")
@CommandAlias("pc|p|partychat|pchat") //Kept for historical reasons
public class PartyChatCommand extends BaseCommand {
    private final @NotNull mcMMO pluginRef;

    public PartyChatCommand(@NotNull mcMMO pluginRef) {
        this.pluginRef = pluginRef;
    }

    @Default
    @Conditions(CommandManager.PARTY_CONDITION)
    public void processCommand(String[] args) {
        BukkitCommandIssuer bukkitCommandIssuer = (BukkitCommandIssuer) getCurrentCommandIssuer();

        if(args == null || args.length == 0) {
            //Process with no arguments
            if(bukkitCommandIssuer.isPlayer()) {
                McMMOPlayer mmoPlayer = UserManager.getPlayer(bukkitCommandIssuer.getPlayer());
                pluginRef.getChatManager().setOrToggleChatChannel(mmoPlayer, ChatChannel.PARTY);
            } else {
                //Not support for console
                mcMMO.p.getLogger().info("You cannot switch chat channels as console, please provide full arguments.");
            }
        } else {
            //Here we split the logic, consoles need to target a party name and players do not

            /*
             * Player Logic
             */
            if(bukkitCommandIssuer.getIssuer() instanceof Player) {
                McMMOPlayer mmoPlayer = UserManager.getPlayer(bukkitCommandIssuer.getPlayer());
                processCommandArgsPlayer(mmoPlayer, args);
            /*
             * Console Logic
             */
            } else {
                processCommandArgsConsole(args);
            }
        }
    }

    /**
     * Processes the command with arguments for a {@link McMMOPlayer}
     * @param mmoPlayer target player
     * @param args command arguments
     */
    private void processCommandArgsPlayer(@NotNull McMMOPlayer mmoPlayer, @NotNull String[] args) {
        //Player is not toggling and is chatting directly to party
        pluginRef.getChatManager().processPlayerMessage(mmoPlayer, args, ChatChannel.PARTY);
    }

    /**
     * Processes the command with arguments for a {@link com.gmail.nossr50.chat.author.ConsoleAuthor}
     * @param args command arguments
     */
    private void processCommandArgsConsole(@NotNull String[] args) {
        if(args.length <= 1) {
            //Only specific a party and not the message
            mcMMO.p.getLogger().severe("You need to specify a party name and then write a message afterwards.");
        } else {
            //Grab party
            Party targetParty = mcMMO.p.getPartyManager().getParty(args[0]);

            if(targetParty != null) {
                pluginRef.getChatManager().processConsoleMessage(StringUtils.buildStringAfterNthElement(args, 1), targetParty);
            } else {
                mcMMO.p.getLogger().severe("A party with that name doesn't exist!");
            }
        }
    }
}
