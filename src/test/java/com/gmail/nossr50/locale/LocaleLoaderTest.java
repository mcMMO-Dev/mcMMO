package com.gmail.nossr50.locale;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Stream;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.ChatColor;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;

class LocaleLoaderTest {

    @BeforeEach
    void setUp() {
    }

    @AfterEach
    void tearDown() {
    }

    @ParameterizedTest
    @ValueSource(strings = {"§cTest", "[[RED]]Test"})
    void addColorsShouldAddColorRed(String testString) {
        // When
        final String result = LocaleLoader.addColors(testString);

        // Then
        assertThat(result).isEqualTo(ChatColor.RED + "Test");
    }

    // hex colors test
    @Test
    void translateHexColorCodesShouldAddRed() {
        // Given
        final String testString = "&#FF0000Test";

        // When
        final String result = LocaleLoader.translateHexColorCodes(testString);

        // Then
        final String expectedResult = "§x§F§F§0§0§0§0Test";
        assertThat(result).isEqualTo(expectedResult);
    }

    @Test
    void reverseTranslateHexColorCodesShouldRemoveRed() {
        // Given
        final String testString = "§x§F§F§0§0§0§0Test";

        // When
        final String result = LocaleLoader.reverseTranslateHexColorCodes(testString);

        // Then
        final String expectedResult = "&#FF0000Test";
        assertThat(result).isEqualTo(expectedResult);
    }

    @ParameterizedTest
    @ValueSource(strings = {"&#FF0000Te&#0000FFst", "&#FF0000Te[[RED]]st", "[[BLUE]]Te[[RED]]st",
            "§9Te§cst"})
    void addColorsShouldAddRedAndBlue(String testString) {
        // When
        final String result = LocaleLoader.addColors(testString);

        // TODO: Hacky, clean this up sometime in the future
        // Then
        // All legal representations of the same string
        final List<String> expectedResults = List.of("§x§F§F§0§0§0§0Te§x§0§0§0§0§F§Fst",
                "§x§F§F§0§0§0§0Te§x§0§0§0§0§F§Fst",
                "§x§F§F§0§0§0§0Te§cst",
                "§9Te§cst");
        assertThat(expectedResults).contains(result);
    }

    @ParameterizedTest
    @MethodSource("namedColorTokenProvider")
    void formatComponentShouldSupportNamedColorTokens(final String input,
            final TextColor expectedColor) {
        // When
        final TextComponent component = LocaleLoader.formatComponent(input);

        // Then
        assertThat(component.color()).isEqualTo(expectedColor);
    }

    static Stream<Arguments> namedColorTokenProvider() {
        return Stream.of(
                Arguments.of("[[BLACK]]Test",       NamedTextColor.BLACK),
                Arguments.of("[[DARK_BLUE]]Test",   NamedTextColor.DARK_BLUE),
                Arguments.of("[[DARK_GREEN]]Test",  NamedTextColor.DARK_GREEN),
                Arguments.of("[[DARK_AQUA]]Test",   NamedTextColor.DARK_AQUA),
                Arguments.of("[[DARK_RED]]Test",    NamedTextColor.DARK_RED),
                Arguments.of("[[DARK_PURPLE]]Test", NamedTextColor.DARK_PURPLE),
                Arguments.of("[[GOLD]]Test",        NamedTextColor.GOLD),
                Arguments.of("[[GRAY]]Test",        NamedTextColor.GRAY),
                Arguments.of("[[DARK_GRAY]]Test",   NamedTextColor.DARK_GRAY),
                Arguments.of("[[BLUE]]Test",        NamedTextColor.BLUE),
                Arguments.of("[[GREEN]]Test",       NamedTextColor.GREEN),
                Arguments.of("[[AQUA]]Test",        NamedTextColor.AQUA),
                Arguments.of("[[RED]]Test",         NamedTextColor.RED),
                Arguments.of("[[LIGHT_PURPLE]]Test",NamedTextColor.LIGHT_PURPLE),
                Arguments.of("[[YELLOW]]Test",      NamedTextColor.YELLOW),
                Arguments.of("[[WHITE]]Test",       NamedTextColor.WHITE)
        );
    }

    @ParameterizedTest
    @MethodSource("namedFormattingTokenProvider")
    void formatComponentShouldApplyNamedFormattingTokens(final String input,
            final TextDecoration expectedDecoration) {
        // When
        final TextComponent component = LocaleLoader.formatComponent(input);

        // Then
        assertThat(component.decoration(expectedDecoration)).isEqualTo(TextDecoration.State.TRUE);
    }

    static Stream<Arguments> namedFormattingTokenProvider() {
        return Stream.of(
                Arguments.of("[[BOLD]]Test",      TextDecoration.BOLD),
                Arguments.of("[[ITALIC]]Test",    TextDecoration.ITALIC),
                Arguments.of("[[UNDERLINE]]Test", TextDecoration.UNDERLINED),
                Arguments.of("[[STRIKE]]Test",    TextDecoration.STRIKETHROUGH)
        );
    }

    @ParameterizedTest
    @MethodSource("hexColorProvider")
    void formatComponentShouldSupportHexColors(final String input, final TextColor expectedColor) {
        // When
        final TextComponent component = LocaleLoader.formatComponent(input);

        // Then
        assertThat(component.color()).isEqualTo(expectedColor);
    }

    static Stream<Arguments> hexColorProvider() {
        return Stream.of(
                Arguments.of("&#FF0000Test", TextColor.fromHexString("#FF0000")),
                Arguments.of("&#0000FFTest", TextColor.fromHexString("#0000FF")),
                Arguments.of("&#00FF00Test", TextColor.fromHexString("#00FF00"))
        );
    }

    @ParameterizedTest
    @MethodSource("ampersandColorCodeProvider")
    void addColorsShouldTranslateAmpersandColorCodes(final String input,
            final ChatColor expectedColor) {
        // When/Then
        assertThat(LocaleLoader.addColors(input)).isEqualTo(expectedColor + "Test");
    }

    static Stream<Arguments> ampersandColorCodeProvider() {
        return Stream.of(
                Arguments.of("&0Test", ChatColor.BLACK),
                Arguments.of("&2Test", ChatColor.DARK_GREEN),
                Arguments.of("&4Test", ChatColor.DARK_RED),
                Arguments.of("&9Test", ChatColor.BLUE),
                Arguments.of("&aTest", ChatColor.GREEN),
                Arguments.of("&cTest", ChatColor.RED),
                Arguments.of("&eTest", ChatColor.YELLOW),
                Arguments.of("&fTest", ChatColor.WHITE)
        );
    }

    @ParameterizedTest
    @MethodSource("ampersandFormattingCodeProvider")
    void addColorsShouldTranslateAmpersandFormattingCodes(final String input,
            final ChatColor expectedFormatting) {
        // When/Then
        assertThat(LocaleLoader.addColors(input)).isEqualTo(expectedFormatting + "Test");
    }

    static Stream<Arguments> ampersandFormattingCodeProvider() {
        return Stream.of(
                Arguments.of("&lTest", ChatColor.BOLD),
                Arguments.of("&oTest", ChatColor.ITALIC),
                Arguments.of("&nTest", ChatColor.UNDERLINE),
                Arguments.of("&rTest", ChatColor.RESET)
        );
    }

    @ParameterizedTest
    @MethodSource("namedFormattingTokenAddColorsProvider")
    void addColorsShouldTranslateNamedFormattingTokens(final String input,
            final ChatColor expectedFormatting) {
        // When/Then
        assertThat(LocaleLoader.addColors(input)).isEqualTo(expectedFormatting + "Test");
    }

    static Stream<Arguments> namedFormattingTokenAddColorsProvider() {
        return Stream.of(
                Arguments.of("[[BOLD]]Test",      ChatColor.BOLD),
                Arguments.of("[[ITALIC]]Test",    ChatColor.ITALIC),
                Arguments.of("[[UNDERLINE]]Test", ChatColor.UNDERLINE),
                Arguments.of("[[RESET]]Test",     ChatColor.RESET)
        );
    }

    // -------------------------------------------------------------------------
    // Mixed-format tests — addColors() path
    // -------------------------------------------------------------------------

    @ParameterizedTest
    @MethodSource("mixedFormatAddColorsProvider")
    void addColorsShouldTranslateMixedColorFormats(final String input,
            final String expectedOutput) {
        assertThat(LocaleLoader.addColors(input)).isEqualTo(expectedOutput);
    }

    static Stream<Arguments> mixedFormatAddColorsProvider() {
        // addColors() processes: hex first, then [[NAME]], then &X — so all three survive
        // in any combination and ordering
        return Stream.of(
                // [[NAME]] + &X
                Arguments.of("[[GREEN]]Hello &cWorld",
                        ChatColor.GREEN + "Hello " + ChatColor.RED + "World"),
                // &X + [[NAME]]
                Arguments.of("&aGreen [[RED]]Red",
                        ChatColor.GREEN + "Green " + ChatColor.RED + "Red"),
                // &X + &#RRGGBB
                Arguments.of("&aGreen &#FF0000Red",
                        ChatColor.GREEN + "Green " + LocaleLoader.translateHexColorCodes("&#FF0000") + "Red"),
                // [[NAME]] + &#RRGGBB
                Arguments.of("[[DARK_AQUA]]Aqua &#0000FFBlue",
                        ChatColor.DARK_AQUA + "Aqua " + LocaleLoader.translateHexColorCodes("&#0000FF") + "Blue"),
                // All three formats together
                Arguments.of("[[GOLD]]Gold &cRed &#0000FFBlue",
                        ChatColor.GOLD + "Gold " + ChatColor.RED + "Red "
                                + LocaleLoader.translateHexColorCodes("&#0000FF") + "Blue")
        );
    }

    // -------------------------------------------------------------------------
    // Mixed-format tests — formatComponent() path
    // -------------------------------------------------------------------------

    @ParameterizedTest
    @MethodSource("mixedFormatComponentProvider")
    void formatComponentShouldSupportMixedColorFormats(final String input,
            final List<TextColor> expectedColors) {
        final Set<TextColor> actual = collectColors(LocaleLoader.formatComponent(input));
        assertThat(actual).containsAll(expectedColors);
    }

    static Stream<Arguments> mixedFormatComponentProvider() {
        return Stream.of(
                // [[NAME]] + &X
                Arguments.of("[[GREEN]]Green &cRed",
                        List.of(NamedTextColor.GREEN, NamedTextColor.RED)),
                // &X + [[NAME]]
                Arguments.of("&aAqua [[GOLD]]Gold",
                        List.of(NamedTextColor.GREEN, NamedTextColor.GOLD)),
                // &X + &#RRGGBB
                Arguments.of("&aGreen &#FF0000Red",
                        List.of(NamedTextColor.GREEN, TextColor.fromHexString("#FF0000"))),
                // [[NAME]] + &#RRGGBB
                Arguments.of("[[DARK_AQUA]]Aqua &#0000FFBlue",
                        List.of(NamedTextColor.DARK_AQUA, TextColor.fromHexString("#0000FF"))),
                // All three formats together
                Arguments.of("[[GOLD]]Gold &cRed &#0000FFBlue",
                        List.of(NamedTextColor.GOLD, NamedTextColor.RED,
                                TextColor.fromHexString("#0000FF")))
        );
    }

    // -------------------------------------------------------------------------
    // formatString() — argument substitution and null-arg path
    // -------------------------------------------------------------------------

    @Test
    void formatStringShouldSubstituteSingleArgument() {
        assertThat(LocaleLoader.formatString("Hello {0}", "World"))
                .isEqualTo("Hello World");
    }

    @Test
    void formatStringShouldSubstituteMultipleArguments() {
        assertThat(LocaleLoader.formatString("{0} and {1}", "foo", "bar"))
                .isEqualTo("foo and bar");
    }

    @Test
    void formatStringShouldApplyColorsWithNullArgs() {
        // Null messageArguments → skips MessageFormat, goes straight to addColors()
        assertThat(LocaleLoader.formatString("&cHello", (Object[]) null))
                .isEqualTo(ChatColor.RED + "Hello");
    }

    @Test
    void formatStringShouldHandleApostropheInString() {
        // Single quotes in the raw string are doubled before MessageFormat so they are
        // treated as literal characters, not MessageFormat quoting delimiters.
        assertThat(LocaleLoader.formatString("It's {0}", "cool"))
                .isEqualTo("It's cool");
    }

    @Test
    void formatStringShouldSubstituteArgAndApplyColor() {
        assertThat(LocaleLoader.formatString("&aLevel {0}", "42"))
                .isEqualTo(ChatColor.GREEN + "Level 42");
    }

    // -------------------------------------------------------------------------
    // formatComponent() — argument substitution and null-arg path
    // -------------------------------------------------------------------------

    @Test
    void formatComponentShouldSubstituteArgument() {
        final TextComponent component = LocaleLoader.formatComponent("&cHello {0}", "World");
        assertThat(collectText(component)).isEqualTo("Hello World");
    }

    @Test
    void formatComponentShouldColorComponentAfterArgSubstitution() {
        final TextComponent component = LocaleLoader.formatComponent("&cHello {0}", "World");
        final Set<TextColor> colors = collectColors(component);
        assertThat(colors).contains(NamedTextColor.RED);
    }

    @Test
    void formatComponentShouldHandleNullArgs() {
        final TextComponent component = LocaleLoader.formatComponent("&aHello", (Object[]) null);
        assertThat(component.color()).isEqualTo(NamedTextColor.GREEN);
    }

    @Test
    void formatComponentShouldHandleApostropheInString() {
        final TextComponent component = LocaleLoader.formatComponent("It's {0}", "nice");
        assertThat(collectText(component)).isEqualTo("It's nice");
    }

    // -------------------------------------------------------------------------
    // addColors() — complete [[NAME]] color token coverage (all 16 colors)
    // -------------------------------------------------------------------------

    @ParameterizedTest
    @MethodSource("namedColorAddColorsProvider")
    void addColorsShouldTranslateAllNamedColorTokens(final String input,
            final ChatColor expectedColor) {
        assertThat(LocaleLoader.addColors(input)).isEqualTo(expectedColor + "Test");
    }

    static Stream<Arguments> namedColorAddColorsProvider() {
        return Stream.of(
                Arguments.of("[[BLACK]]Test",       ChatColor.BLACK),
                Arguments.of("[[DARK_BLUE]]Test",   ChatColor.DARK_BLUE),
                Arguments.of("[[DARK_GREEN]]Test",  ChatColor.DARK_GREEN),
                Arguments.of("[[DARK_AQUA]]Test",   ChatColor.DARK_AQUA),
                Arguments.of("[[DARK_RED]]Test",    ChatColor.DARK_RED),
                Arguments.of("[[DARK_PURPLE]]Test", ChatColor.DARK_PURPLE),
                Arguments.of("[[GOLD]]Test",        ChatColor.GOLD),
                Arguments.of("[[GRAY]]Test",        ChatColor.GRAY),
                Arguments.of("[[DARK_GRAY]]Test",   ChatColor.DARK_GRAY),
                Arguments.of("[[BLUE]]Test",        ChatColor.BLUE),
                Arguments.of("[[GREEN]]Test",       ChatColor.GREEN),
                Arguments.of("[[AQUA]]Test",        ChatColor.AQUA),
                Arguments.of("[[RED]]Test",         ChatColor.RED),
                Arguments.of("[[LIGHT_PURPLE]]Test",ChatColor.LIGHT_PURPLE),
                Arguments.of("[[YELLOW]]Test",      ChatColor.YELLOW),
                Arguments.of("[[WHITE]]Test",       ChatColor.WHITE)
        );
    }

    // -------------------------------------------------------------------------
    // addColors() — remaining formatting tokens ([[STRIKE]], [[MAGIC]])
    // -------------------------------------------------------------------------

    @ParameterizedTest
    @MethodSource("extraFormattingTokenAddColorsProvider")
    void addColorsShouldTranslateStrikeAndMagicTokens(final String input,
            final ChatColor expectedFormatting) {
        assertThat(LocaleLoader.addColors(input)).isEqualTo(expectedFormatting + "Test");
    }

    static Stream<Arguments> extraFormattingTokenAddColorsProvider() {
        return Stream.of(
                Arguments.of("[[STRIKE]]Test", ChatColor.STRIKETHROUGH),
                Arguments.of("[[MAGIC]]Test",  ChatColor.MAGIC)
        );
    }

    // -------------------------------------------------------------------------
    // addColors() — complete &X shorthand coverage
    // -------------------------------------------------------------------------

    @ParameterizedTest
    @MethodSource("remainingAmpersandColorCodeProvider")
    void addColorsShouldTranslateRemainingAmpersandColorCodes(final String input,
            final ChatColor expectedColor) {
        assertThat(LocaleLoader.addColors(input)).isEqualTo(expectedColor + "Test");
    }

    static Stream<Arguments> remainingAmpersandColorCodeProvider() {
        return Stream.of(
                Arguments.of("&1Test", ChatColor.DARK_BLUE),
                Arguments.of("&3Test", ChatColor.DARK_AQUA),
                Arguments.of("&5Test", ChatColor.DARK_PURPLE),
                Arguments.of("&6Test", ChatColor.GOLD),
                Arguments.of("&7Test", ChatColor.GRAY),
                Arguments.of("&8Test", ChatColor.DARK_GRAY),
                Arguments.of("&bTest", ChatColor.AQUA),
                Arguments.of("&dTest", ChatColor.LIGHT_PURPLE)
        );
    }

    @ParameterizedTest
    @MethodSource("remainingAmpersandFormattingCodeProvider")
    void addColorsShouldTranslateRemainingAmpersandFormattingCodes(final String input,
            final ChatColor expectedFormatting) {
        assertThat(LocaleLoader.addColors(input)).isEqualTo(expectedFormatting + "Test");
    }

    static Stream<Arguments> remainingAmpersandFormattingCodeProvider() {
        return Stream.of(
                Arguments.of("&mTest", ChatColor.STRIKETHROUGH), // strikethrough
                Arguments.of("&?Test", ChatColor.MAGIC)          // magic — mcMMO uses &? not &k
        );
    }

    // -------------------------------------------------------------------------
    // formatComponent() — [[MAGIC]] obfuscation token
    // -------------------------------------------------------------------------

    @Test
    void formatComponentShouldApplyObfuscationForMagicToken() {
        final TextComponent component = LocaleLoader.formatComponent("[[MAGIC]]Test");
        assertThat(component.decoration(TextDecoration.OBFUSCATED))
                .isEqualTo(TextDecoration.State.TRUE);
    }

    // -------------------------------------------------------------------------
    // translateHexColorCodes() — edge cases
    // -------------------------------------------------------------------------

    @Test
    void translateHexColorCodesShouldReturnNullForNullInput() {
        assertThat(LocaleLoader.translateHexColorCodes(null)).isNull();
    }

    @Test
    void translateHexColorCodesShouldHandleLowercaseHex() {
        // Pattern is [A-Fa-f0-9] so lowercase should translate correctly
        assertThat(LocaleLoader.translateHexColorCodes("&#aabbccTest"))
                .isEqualTo("§x§a§a§b§b§c§cTest");
    }

    @Test
    void translateHexColorCodesShouldHandleMixedCaseHex() {
        assertThat(LocaleLoader.translateHexColorCodes("&#AaBbCcTest"))
                .isEqualTo("§x§A§a§B§b§C§cTest");
    }

    @Test
    void translateHexColorCodesShouldLeaveNonHexUnchanged() {
        assertThat(LocaleLoader.translateHexColorCodes("Hello World"))
                .isEqualTo("Hello World");
    }

    // -------------------------------------------------------------------------
    // reverseTranslateHexColorCodes() — additional colors
    // -------------------------------------------------------------------------

    @ParameterizedTest
    @MethodSource("reverseHexProvider")
    void reverseTranslateHexColorCodesShouldRecoverOriginalHex(final String original) {
        final String translated = LocaleLoader.translateHexColorCodes(original);
        final String recovered = LocaleLoader.reverseTranslateHexColorCodes(translated);
        assertThat(recovered).isEqualTo(original);
    }

    static Stream<String> reverseHexProvider() {
        return Stream.of(
                "&#FF0000Test",
                "&#0000FFTest",
                "&#00FF00Test",
                "&#FFFFFFTest",
                "&#000000Test",
                "&#FFFF00Test",
                "&#FF00FFTest",
                "&#00FFFFTest"
        );
    }

    /** Recursively collects all text content in the component tree. */
    private static String collectText(final Component component) {
        final StringBuilder sb = new StringBuilder();
        if (component instanceof TextComponent tc) {
            sb.append(tc.content());
        }
        for (final Component child : component.children()) {
            sb.append(collectText(child));
        }
        return sb.toString();
    }

    /** Recursively collects every non-null {@link TextColor} in the component tree. */
    private static Set<TextColor> collectColors(final Component component) {
        final Set<TextColor> colors = new HashSet<>();
        if (component.color() != null) {
            colors.add(component.color());
        }
        for (final Component child : component.children()) {
            colors.addAll(collectColors(child));
        }
        return colors;
    }
}