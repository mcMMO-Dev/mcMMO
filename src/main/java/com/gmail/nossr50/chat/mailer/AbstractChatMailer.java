package com.gmail.nossr50.chat.mailer;

import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;


public abstract class AbstractChatMailer implements ChatMailer {
    protected final @NotNull Plugin pluginRef;

    public AbstractChatMailer(@NotNull Plugin pluginRef) {
        this.pluginRef = pluginRef;
    }
}
