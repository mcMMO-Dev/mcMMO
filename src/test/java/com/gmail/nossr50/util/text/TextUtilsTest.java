package com.gmail.nossr50.util.text;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.ArrayList;
import java.util.List;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.format.NamedTextColor;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

/**
 * This Unit Test checks if Adventure was set up correctly and works as expected. Normally, we can
 * rely on this to be the case. However sometimes our dependencies lack so far behind that things
 * stop working correctly. This test ensures that basic functionality is guaranteed to work as we
 * would expect.
 * <p>
 * See https://github.com/mcMMO-Dev/mcMMO/pull/4446
 */
class TextUtilsTest {

    @Nested
    class ColorizeText {

        @Test
        void parsesLegacyColorCodes() {
            // Given
            final String inputText = "&4This text should be red.";

            // When
            final TextComponent component = TextUtils.colorizeText(inputText);

            // Then
            final String failureMessage = "Looks like Adventure is not working correctly. We likely need to update our dependency!";
            assertThat(component.color()).withFailMessage(failureMessage)
                    .isEqualTo(NamedTextColor.DARK_RED);
        }
    }

    @Nested
    class LiteralizeLegacyColorCodes {

        @Test
        void keepsAmpersandFormattingCodesLiteral() {
            // Given
            final String rawMessage = "&aHello &lWorld&r!";

            // When
            final String literalizedMessage = TextUtils.literalizeLegacyColorCodes(rawMessage);

            // Then
            assertThat(literalizedMessage).isEqualTo("&aHello &lWorld&r!");
        }

        @Test
        void convertsSectionSignFormattingCodesToAmpersands() {
            // Given
            final String rawMessage = "\u00A7aHello \u00A7lWorld\u00A7r!";

            // When
            final String literalizedMessage = TextUtils.literalizeLegacyColorCodes(rawMessage);

            // Then
            assertThat(literalizedMessage).isEqualTo("&aHello &lWorld&r!");
        }

        @Test
        void preservesHexTokensAndNormalAmpersandsAsLiteralText() {
            // Given
            final String rawMessage = "Color: &#12AB9FHello & welcome";

            // When
            final String literalizedMessage = TextUtils.literalizeLegacyColorCodes(rawMessage);

            // Then
            assertThat(literalizedMessage).isEqualTo("Color: &#12AB9FHello & welcome");
        }
    }

    @Nested
    class SplitComponentsIntoGroups {

        /**
         * Regression coverage: the group offset was hardcoded to 3, so any other group size
         * grouped the wrong components together.
         */
        @Test
        void shouldHonorGroupSizesOtherThanThree() {
            // Given - five components split into groups of two
            final List<Component> components = List.of(Component.text("a"), Component.text("b"),
                    Component.text("c"), Component.text("d"), Component.text("e"));

            // When - the components are split
            final Component[][] groups = TextUtils.splitComponentsIntoGroups(components, 2);

            // Then - the groups are [a,b], [c,d], [e,null]
            assertThat(groups).hasNumberOfRows(3);
            assertThat(groups[0][0]).isEqualTo(components.get(0));
            assertThat(groups[0][1]).isEqualTo(components.get(1));
            assertThat(groups[1][0]).isEqualTo(components.get(2));
            assertThat(groups[1][1]).isEqualTo(components.get(3));
            assertThat(groups[2][0]).isEqualTo(components.get(4));
            assertThat(groups[2][1]).isNull();
        }

        /** Guard: the group size of three used by the skill hover display keeps working. */
        @Test
        void shouldSplitIntoGroupsOfThree() {
            // Given - four components split into groups of three
            final List<Component> components = List.of(Component.text("a"), Component.text("b"),
                    Component.text("c"), Component.text("d"));

            // When - the components are split
            final Component[][] groups = TextUtils.splitComponentsIntoGroups(components, 3);

            // Then - the second group starts with the fourth component
            assertThat(groups).hasNumberOfRows(2);
            assertThat(groups[1][0]).isEqualTo(components.get(3));
        }
    }

    @Nested
    class InsertLiteralTextAtMarkers {

        @Test
        void preservesLiteralColorTokensInInsertedMessage() {
            // Given
            final String startMarker = "\u0002START\u0003";
            final String endMarker = "\u0002END\u0003";
            final String template = "\u00A7aPrefix " + startMarker + endMarker + " \u00A7bSuffix";
            final String literalMessage = "&cMessage &lTokens";

            // When
            final TextComponent formattedComponent = TextUtils.insertLiteralTextAtMarkers(template,
                    startMarker, endMarker, literalMessage);
            final String aggregatedContent = aggregateTextContent(formattedComponent);

            // Then
            assertThat(aggregatedContent).isEqualTo("Prefix &cMessage &lTokens Suffix");
        }

        @Test
        void onlyReplacesTextBetweenMarkerBoundaries() {
            // Given
            final String startMarker = "\u0002START\u0003";
            final String endMarker = "\u0002END\u0003";
            final String template = "Header START " + startMarker + endMarker + " END Footer";
            final String literalMessage = "payload";

            // When
            final TextComponent formattedComponent = TextUtils.insertLiteralTextAtMarkers(template,
                    startMarker, endMarker, literalMessage);
            final String aggregatedContent = aggregateTextContent(formattedComponent);

            // Then
            assertThat(aggregatedContent).isEqualTo("Header START payload END Footer");
        }

        private String aggregateTextContent(final Component rootComponent) {
            final List<String> parts = new ArrayList<>();
            collectTextContent(rootComponent, parts);
            return String.join("", parts);
        }

        private void collectTextContent(final Component component, final List<String> parts) {
            if (component instanceof TextComponent textComponent) {
                parts.add(textComponent.content());
            }

            for (final Component childComponent : component.children()) {
                collectTextContent(childComponent, parts);
            }
        }
    }
}
