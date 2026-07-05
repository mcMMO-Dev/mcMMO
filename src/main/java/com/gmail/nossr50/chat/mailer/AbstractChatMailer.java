package com.gmail.nossr50.chat.mailer;

import com.gmail.nossr50.locale.LocaleLoader;
import com.gmail.nossr50.util.text.TextUtils;
import java.util.UUID;
import net.kyori.adventure.text.TextComponent;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;


public abstract class AbstractChatMailer implements ChatMailer {
    protected final @NotNull Plugin pluginRef;

    public AbstractChatMailer(@NotNull Plugin pluginRef) {
        this.pluginRef = pluginRef;
    }

    protected final @NotNull TextComponent formatLocaleStyleWithLiteralMessage(
            @NotNull String localeKey,
            @NotNull String authoredName,
            @NotNull String literalMessage) {
        final String messageStartMarker = createLiteralMessageMarker("START");
        final String messageEndMarker = createLiteralMessageMarker("END");
        final String formattedTemplate = LocaleLoader.getString(localeKey, authoredName,
                messageStartMarker + messageEndMarker);

        return TextUtils.insertLiteralTextAtMarkers(formattedTemplate, messageStartMarker,
                messageEndMarker, literalMessage);
    }

    private static @NotNull String createLiteralMessageMarker(@NotNull String markerRole) {
        return "\u0002MCMMO_" + markerRole + "_" + UUID.randomUUID() + "\u0003";
    }
}
