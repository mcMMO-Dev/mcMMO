package com.gmail.nossr50.chat.mailer;

import com.gmail.nossr50.chat.author.Author;
import com.gmail.nossr50.chat.message.AdminChatMessage;
import com.gmail.nossr50.chat.message.ChatMessage;
import com.gmail.nossr50.config.ChatConfig;
import com.gmail.nossr50.datatypes.chat.ChatChannel;
import com.gmail.nossr50.events.chat.McMMOAdminChatEvent;
import com.gmail.nossr50.events.chat.McMMOChatEvent;
import com.gmail.nossr50.locale.LocaleLoader;
import com.gmail.nossr50.mcMMO;
import com.gmail.nossr50.util.text.TextUtils;
import net.kyori.adventure.audience.Audience;
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
     *
     * @return an audience of admins
     */
    public @NotNull Audience constructAudience() {
        return mcMMO.getAudiences().filter(predicate());
    }

    /**
     * Predicate used to filter the audience
     *
     * @return admin chat audience predicate
     */
    public @NotNull Predicate<CommandSender> predicate() {
        return (commandSender) -> commandSender.isOp()
                || commandSender.hasPermission(MCMMO_CHAT_ADMINCHAT_PERMISSION)
                || (ChatConfig.getInstance().isConsoleIncludedInAudience(ChatChannel.ADMIN) && commandSender instanceof ConsoleCommandSender);
    }

    /**
     * Styles a string using a locale entry
     *
     * @param author message author
     * @param message message contents
     * @param canColor whether to replace colors codes with colors in the raw message
     * @return the styled string, based on a locale entry
     */
    public @NotNull TextComponent addStyle(@NotNull Author author, @NotNull String message, boolean canColor) {
        if (canColor) {
            return LocaleLoader.getTextComponent("Chat.Style.Admin", author.getAuthoredName(ChatChannel.ADMIN), message);
        } else {
            return TextUtils.ofLegacyTextRaw(LocaleLoader.getString("Chat.Style.Admin", author.getAuthoredName(ChatChannel.ADMIN), message));
        }
    }

    @Override
    public void sendMail(@NotNull ChatMessage chatMessage) {
        chatMessage.sendMessage();
    }

    /**
     * Processes a chat message from an author to an audience of admins
     *
     * @param author the author
     * @param rawString the raw message as the author typed it before any styling
     * @param isAsync whether this is being processed asynchronously
     * @param canColor whether the author can use colors in chat
     */
    public void processChatMessage(@NotNull Author author, @NotNull String rawString, boolean isAsync, boolean canColor) {
        AdminChatMessage chatMessage = new AdminChatMessage(pluginRef, author, constructAudience(), rawString, addStyle(author, rawString, canColor));

        McMMOChatEvent chatEvent = new McMMOAdminChatEvent(pluginRef, chatMessage, isAsync);
        Bukkit.getPluginManager().callEvent(chatEvent);

        if (!chatEvent.isCancelled()) {
            sendMail(chatMessage);
        }
    }


}