package com.gmail.nossr50.text;

import com.gmail.nossr50.mcMMO;
import net.kyori.text.TextComponent;
import net.kyori.text.adapter.bukkit.TextAdapter;
import net.kyori.text.serializer.gson.GsonComponentSerializer;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

import java.util.List;

/**
 * Handles some boiler plate related to kyori powered text library
 */
public class TextManager {
    public static final char COLOR_CHAR = '§';
    private mcMMO pluginRef;

    public TextManager(mcMMO pluginRef) {
        this.pluginRef = pluginRef;
    }

    /**
     * Send a message to multiple recipients
     * @param commandSenders target recipients
     * @param textComponent the {@link TextComponent} to send
     */
    public void sendMessage(List<CommandSender> commandSenders, TextComponent textComponent) {
        for(CommandSender commandSender : commandSenders) {
            sendMessage(commandSender, textComponent);
        }
    }

    /**
     * Serializes and sends a text message to a specific recipient
     * @param commandSender target recipient
     * @param textComponent the {@link TextComponent} to serialize and send
     */
    public void sendMessage(CommandSender commandSender, TextComponent textComponent) {
        String json = GsonComponentSerializer.INSTANCE.serialize(textComponent);
        TextAdapter.sendMessage(commandSender, textComponent);
    }

    /**
     * Sends a message to a single recipient with the (mcMMO) watermark at the beginning of the message
     * @param commandSender target recipient
     * @param textComponent the {@link TextComponent} to watermark and send
     */
    public void sendMessageWatermarked(CommandSender commandSender, TextComponent textComponent) {
        TextComponent waterMarkedComponent = buildWaterMarked(textComponent);

        sendMessage(commandSender, waterMarkedComponent);
    }

    /**
     * Sends a message to a list of recipients with the (mcMMO) watermark at the beginning of the message
     * @param commandSenders target recipients
     * @param textComponent the {@link TextComponent} to watermark and send
     */
    public void sendMessageWatermarked(List<CommandSender> commandSenders, TextComponent textComponent) {
        TextComponent waterMarkedComponent = buildWaterMarked(textComponent);

        for(CommandSender commandSender : commandSenders) {
            sendMessage(commandSender, waterMarkedComponent);
        }
    }

    /**
     * Builds a watermarked version of a text component
     * @param textComponent target component to watermark
     * @return a new {@link TextComponent} with the (mcMMO) watermark at the beginning and the contents of {@link TextComponent} appended afterwards
     */
    @NotNull
    private TextComponent buildWaterMarked(TextComponent textComponent) {
        return TextComponent.builder().content(pluginRef.getLocaleManager().getString("mcMMO.Template.Prefix")).append(textComponent).build();
    }

    /**
     * Dissects a string and builds a {@link TextComponent} out of it.
     * Results are cached to avoid needless operations in the future
     * @param legacyText target text to transform
     */
    private TextComponent transformLegacyTexts(String legacyText) {
        //TODO: Cache results
        TextComponent.Builder builder = TextComponent.builder();

        for(int i = 0; i < legacyText.toCharArray().length; i++) {
            char c = legacyText.charAt(i);

            //Found color character
            if(c == COLOR_CHAR) {
                if(i+1 >= legacyText.toCharArray().length) {
                    //No color code because we're at the end of the string
                    builder.append(String.valueOf(c));
                } else {
                    //TODO: finish
                }
            } else {
                //Not a color character
            }
        }
        return builder.build();
    }

}
