package com.gmail.nossr50.util.text;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.gmail.nossr50.datatypes.skills.PrimarySkillType;
import com.gmail.nossr50.datatypes.skills.SubSkillType;
import com.gmail.nossr50.mcMMO;
import com.gmail.nossr50.util.skills.SkillTools;
import java.util.logging.Logger;
import java.util.stream.Stream;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.MockedStatic;
import org.mockito.Mockito;

@TestInstance(Lifecycle.PER_CLASS)
class TextComponentFactoryTest {

    private static final @NotNull Logger logger = Logger.getLogger(Logger.GLOBAL_LOGGER_NAME);

    private static MockedStatic<mcMMO> mockedMcMMO;
    private SkillTools skillTools;

    @BeforeAll
    void setUpAll() {
        mockedMcMMO = Mockito.mockStatic(mcMMO.class);

        mcMMO.p = mock(mcMMO.class);
        when(mcMMO.p.getLogger()).thenReturn(logger);

        skillTools = mock(SkillTools.class);
        when(mcMMO.p.getSkillTools()).thenReturn(skillTools);

        // Map every SubSkillType to its expected parent via the enum name prefix
        for (final SubSkillType subSkillType : SubSkillType.values()) {
            final PrimarySkillType parent = parentFromEnumName(subSkillType.name());
            if (parent != null) {
                when(skillTools.getPrimarySkillBySubSkill(subSkillType)).thenReturn(parent);
            }
        }
    }

    @AfterAll
    void tearDownAll() {
        mockedMcMMO.close();
    }

    // -------------------------------------------------------------------------
    // getSubSkillWikiLink()
    // -------------------------------------------------------------------------

    @ParameterizedTest
    @MethodSource("wikiLinkProvider")
    void getSubSkillWikiLinkShouldProduceCorrectUrl(
            final SubSkillType subSkillType,
            final String expectedUrl) {
        assertThat(TextComponentFactory.getSubSkillWikiLink(subSkillType))
                .isEqualTo(expectedUrl);
    }

    static Stream<Arguments> wikiLinkProvider() {
        return Stream.of(
                Arguments.of(
                        SubSkillType.MINING_BLAST_MINING,
                        "https://wiki.mcmmo.org/en/skills/mining#blast-mining"),
                Arguments.of(
                        SubSkillType.ACROBATICS_DODGE,
                        "https://wiki.mcmmo.org/en/skills/acrobatics#dodge"),
                Arguments.of(
                        SubSkillType.SWORDS_COUNTER_ATTACK,
                        "https://wiki.mcmmo.org/en/skills/swords#counter-attack"),
                Arguments.of(
                        SubSkillType.WOODCUTTING_TREE_FELLER,
                        "https://wiki.mcmmo.org/en/skills/woodcutting#tree-feller"),
                Arguments.of(
                        SubSkillType.UNARMED_ARROW_DEFLECT,
                        "https://wiki.mcmmo.org/en/skills/unarmed#arrow-deflect")
        );
    }

    @ParameterizedTest
    @MethodSource("wikiLinkProvider")
    void getSubSkillWikiLinkShouldAlwaysStartWithBaseUrl(
            final SubSkillType subSkillType, final String ignored) {
        assertThat(TextComponentFactory.getSubSkillWikiLink(subSkillType))
                .startsWith("https://wiki.mcmmo.org/en/skills/");
    }

    @ParameterizedTest
    @MethodSource("wikiLinkProvider")
    void getSubSkillWikiLinkShouldContainParentSkillName(
            final SubSkillType subSkillType, final String ignored) {
        final String url = TextComponentFactory.getSubSkillWikiLink(subSkillType);
        final String parentName = subSkillType.getParentSkill().toString().toLowerCase();
        assertThat(url).contains(parentName);
    }

    @ParameterizedTest
    @MethodSource("wikiLinkProvider")
    void getSubSkillWikiLinkShouldBeLowercase(
            final SubSkillType subSkillType, final String ignored) {
        assertThat(TextComponentFactory.getSubSkillWikiLink(subSkillType))
                .isEqualTo(TextComponentFactory.getSubSkillWikiLink(subSkillType).toLowerCase());
    }

    // -------------------------------------------------------------------------
    // Helpers
    // -------------------------------------------------------------------------

    /**
     * Derives a {@link PrimarySkillType} from the enum constant name prefix.
     * {@code MINING_BLAST_MINING} → {@code PrimarySkillType.MINING}.
     */
    private static PrimarySkillType parentFromEnumName(final @NotNull String enumName) {
        final String prefix = enumName.substring(0, enumName.indexOf('_')).toUpperCase();
        for (final PrimarySkillType primary : PrimarySkillType.values()) {
            if (primary.name().equals(prefix)) {
                return primary;
            }
        }
        return null;
    }
}
