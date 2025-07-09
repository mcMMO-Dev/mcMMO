package com.gmail.nossr50.chat.mailer;

import com.gmail.nossr50.chat.message.ChatMessage;
import org.jetbrains.annotations.NotNull;

public interface ChatMailer {
    /**
     * Send out a chat message
     *
     * @param chatMessage the {@link ChatMessage}
     */
    void sendMail(@NotNull ChatMessage chatMessage);
}