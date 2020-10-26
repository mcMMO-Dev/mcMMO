package com.gmail.nossr50.chat.mailer;

import com.gmail.nossr50.chat.author.Author;
import com.gmail.nossr50.chat.message.AdminChatMessage;
import com.gmail.nossr50.chat.message.ChatMessage;
import com.gmail.nossr50.events.chat.McMMOAdminChatEvent;
import com.gmail.nossr50.events.chat.McMMOChatEvent;
import com.gmail.nossr50.locale.LocaleLoader;
import com.gmail.nossr50.mcMMO;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;

import java.util.function.Predicate;

public class AdminChatMailer extends AbstractChatMailer {

    public AdminChatMailer(Plugin pluginRef) {
        super(pluginRef);
    }

    public static final @NotNull String MCMMO_CHAT_ADMINCHAT_PERMISSION = "mcmmo.chat.adminchat";

    /**
     * Constructs an audience of admins
     * @return an audience of admins
     */
    public @NotNull Audience constructAudience() {
        return mcMMO.getAudiences().filter(predicate());
    }

    /**
     * Predicate used to filter the audience
     * @return admin chat audience predicate
     */
    public @NotNull Predicate<CommandSender> predicate() {
        return (commandSender) -> commandSender.isOp()
                || commandSender.hasPermission(MCMMO_CHAT_ADMINCHAT_PERMISSION)
                || commandSender instanceof ConsoleCommandSender;
    }

    /**
     * Styles a string using a locale entry
     * @param author message author
     * @param message message contents
     * @return the styled string, based on a locale entry
     */
    public @NotNull TextComponent addStyle(@NotNull Author author, @NotNull String message) {
        return Component.text(LocaleLoader.getString("Chat.Style.Admin", author.getAuthoredName(), message));
    }

    @Override
    public void sendMail(@NotNull ChatMessage chatMessage) {
        chatMessage.sendMessage();
    }

    public void processChatMessage(@NotNull Author author, @NotNull String rawString, boolean isAsync) {
        AdminChatMessage chatMessage = new AdminChatMessage(pluginRef, author, constructAudience(), rawString, addStyle(author, rawString));

        McMMOChatEvent chatEvent = new McMMOAdminChatEvent(pluginRef, chatMessage, isAsync);
        Bukkit.getPluginManager().callEvent(chatEvent);

        if(!chatEvent.isCancelled()) {
            sendMail(chatMessage);
        }
    }
}
