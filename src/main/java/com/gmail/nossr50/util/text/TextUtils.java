package com.gmail.nossr50.util.text;

import com.gmail.nossr50.mcMMO;
import java.util.List;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.ComponentBuilder;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.event.HoverEvent;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.Style;
import net.kyori.adventure.text.format.TextDecoration;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import net.md_5.bungee.api.chat.BaseComponent;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class TextUtils {
    private static @Nullable LegacyComponentSerializer customLegacySerializer;

    private TextUtils() {
        // We don't want any instances of this class.
    }

    /**
     * Makes a single component from an array of components, can optionally add prefixes and
     * suffixes to come before and after each component
     *
     * @param componentsArray target array
     * @return a component with optional styling built from an array
     */
    static @NotNull Component fromArray(@NotNull Component[] componentsArray,
            @Nullable Component prefixComponent, @Nullable Component suffixComponent) {
        TextComponent.Builder componentBuilder = Component.text();

        for (Component component : componentsArray) {
            if (component == null) //Individual elements can be null
            {
                continue;
            }

            if (prefixComponent != null) {
                componentBuilder.append(prefixComponent);
            }

            componentBuilder.append(component);

            if (suffixComponent != null) {
                componentBuilder.append(suffixComponent);
            }

        }

        return componentBuilder.build();
    }

    /**
     * Takes a list of components and splits them into arrays each with a maximum element limit
     * Individual elements in [][X] may be null
     *
     * @param components target component list
     * @param groupsSize maximum size per array
     * @return a 2D array with components split into groups
     */
    static @NotNull Component[][] splitComponentsIntoGroups(@NotNull List<Component> components,
            int groupsSize) {
        int groupCount = (int) Math.ceil((double) components.size() / (double) groupsSize);

        Component[][] splitGroups = new Component[groupCount][groupsSize];

        int groupsFinished = 0;

        while (groupsFinished < groupCount) {
            //Fill group with members
            for (int i = 0; i < groupsSize; i++) {
                int indexOfPotentialMember =
                        i + (groupsFinished * 3); //Groups don't always fill all members neatly

                //Some groups won't have entirely non-null elements
                if (indexOfPotentialMember > components.size() - 1) {
                    break;
                }

                Component potentialMember = components.get(indexOfPotentialMember);

                //Make sure the potential member exists because of rounding
                if (potentialMember != null) {
                    splitGroups[groupsFinished][i] = potentialMember;
                }
            }

            //Another group is finished
            groupsFinished++;
        }

        return splitGroups;
    }

    static void addChildWebComponent(@NotNull ComponentBuilder<?, ?> webTextComponent,
            @NotNull String childName) {
        TextComponent childComponent = Component.text(childName).color(NamedTextColor.BLUE);
        webTextComponent.append(childComponent);
    }

    static void addNewHoverComponentToTextComponent(@NotNull TextComponent.Builder textComponent,
            @NotNull Component baseComponent) {
        textComponent.hoverEvent(HoverEvent.showText(baseComponent));
    }

    public static BaseComponent[] convertToBungeeComponent(@NotNull String displayName) {
        return net.md_5.bungee.api.chat.TextComponent.fromLegacyText(displayName);
    }

    public static @NotNull TextComponent ofBungeeComponents(@NotNull BaseComponent[] bungeeName) {
        return Component.textOfChildren(
                mcMMO.getCompatibilityManager().getBungeeSerializerCompatibilityLayer()
                        .deserialize(bungeeName));
    }

    public static @NotNull TextComponent ofBungeeRawStrings(@NotNull String bungeeRawString) {
        return ofBungeeComponents(convertToBungeeComponent(bungeeRawString));
    }

    public static @NotNull TextComponent ofLegacyTextRaw(@NotNull String rawString) {
        return LegacyComponentSerializer.legacySection().deserialize(rawString);
    }

    public static @NotNull TextComponent colorizeText(@NotNull String rawtext) {
        if (customLegacySerializer == null) {
            customLegacySerializer = getSerializer();
        }

        return customLegacySerializer.deserialize(rawtext);
    }

    @NotNull
    private static LegacyComponentSerializer getSerializer() {
        return LegacyComponentSerializer.builder()
                .hexColors()
                .useUnusualXRepeatedCharacterHexFormat()
                .character('&')
                .hexCharacter('#')
                .extractUrls(Style.style()
                        .decorate(getURLStyle())
                        .color(NamedTextColor.DARK_AQUA)
                        .build())
                .build();
    }

    public static @NotNull TextDecoration[] getURLStyle() {
        return new TextDecoration[]{TextDecoration.UNDERLINED};
    }

    public static @NotNull String sanitizeForSerializer(@NotNull String string) {
        if (customLegacySerializer == null) {
            customLegacySerializer = getSerializer();
        }

        TextComponent componentForm = ofLegacyTextRaw(string);
        return customLegacySerializer.serialize(componentForm);
    }
}
