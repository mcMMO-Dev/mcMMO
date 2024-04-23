package com.gmail.nossr50.locale;

import org.bukkit.ChatColor;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

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
    @ValueSource(strings = {"&#FF0000Te&#0000FFst", "&#FF0000Te[[RED]]st", "[[BLUE]]Te[[RED]]st", "§9Te§cst"})
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
}