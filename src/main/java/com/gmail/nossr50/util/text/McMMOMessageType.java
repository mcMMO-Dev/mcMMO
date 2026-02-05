package com.gmail.nossr50.util.text;

import java.util.function.BiConsumer;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.audience.MessageType;
import net.kyori.adventure.identity.Identity;
import net.kyori.adventure.text.Component;

public enum McMMOMessageType {
    ACTION_BAR(Audience::sendActionBar),
    SYSTEM((audience, message) -> audience.sendMessage(Identity.nil(), message,
            MessageType.SYSTEM));

    private final BiConsumer<Audience, Component> sender;

    McMMOMessageType(final BiConsumer<Audience, Component> sender) {
        this.sender = sender;
    }

    public void send(final Audience audience, final Component message) {
        this.sender.accept(audience, message);
    }
}
