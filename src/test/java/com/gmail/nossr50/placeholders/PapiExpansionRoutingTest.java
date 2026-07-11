package com.gmail.nossr50.placeholders;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.gmail.nossr50.config.GeneralConfig;
import com.gmail.nossr50.datatypes.skills.PrimarySkillType;
import com.gmail.nossr50.mcMMO;
import com.gmail.nossr50.util.skills.SkillTools;
import java.util.logging.Logger;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

/**
 * Pins the {@code onPlaceholderRequest} routing seam: PAPI hands the expansion one string
 * like {@code mctop_mining:1}, and the expansion must split it into a registered token plus
 * a position parameter. A routed leaderboard token with an empty (never refreshed) cache
 * resolves to an empty string, while an unregistered token resolves to {@code null} - that
 * difference is what these tests assert, so no database or cache refresh is needed. The
 * skill name tokens resolve through a stubbed SkillTools instead.
 */
class PapiExpansionRoutingTest {
    private static GeneralConfig generalConfig;

    @BeforeAll
    static void setUpClass() {
        mcMMO.p = mock(mcMMO.class);
        when(mcMMO.p.getLogger()).thenReturn(Logger.getAnonymousLogger());

        generalConfig = mock(GeneralConfig.class);
        when(mcMMO.p.getGeneralConfig()).thenReturn(generalConfig);
        when(generalConfig.getPapiLeaderboardMaxTrackedRank()).thenReturn(100);
        when(generalConfig.getUseMySQL()).thenReturn(false);
        when(generalConfig.getLeaderboardRefreshIntervalSecondsFlatFile()).thenReturn(600);

        final SkillTools skillTools = mock(SkillTools.class);
        when(mcMMO.p.getSkillTools()).thenReturn(skillTools);
        when(skillTools.getLocalizedSkillName(PrimarySkillType.MINING)).thenReturn("Mining");
        when(skillTools.getHeaderBannerSkillName(PrimarySkillType.MINING)).thenReturn("MINING");
    }

    @AfterAll
    static void tearDownClass() {
        mcMMO.p = null;
    }

    @Test
    void onPlaceholderRequestShouldRouteMcTopTokenWithPositionParam() {
        // Given - a freshly constructed expansion whose leaderboard cache is empty
        final PapiExpansion expansion = new PapiExpansion();

        // When - PAPI requests a leaderboard token with a position parameter
        final String result = expansion.onPlaceholderRequest(null, "mctop_mining:1");

        // Then - the token routes to the cache-backed placeholder, which resolves the empty
        // cache to an empty string rather than the null an unknown token would produce
        assertThat(result).isEmpty();
    }

    /**
     * Gotcha coverage: every registered mctop variant (value, name, and each overall alias)
     * must survive the token/position split, or the placeholder silently renders as raw text.
     */
    @ParameterizedTest(name = "\"{0}\" should route to a registered placeholder")
    @ValueSource(strings = {
            "mctop_name_mining:1",
            "mctop_overall:1",
            "mctop_name_overall:1",
            "mctop_all:2",
            "mctop_name_all:2",
            "mctop_powerlevel:3",
            "mctop_name_powerlevel:3",
            "MCTOP_MINING:1"
    })
    void onPlaceholderRequestShouldRouteAllMcTopVariants(String params) {
        // Given - a freshly constructed expansion whose leaderboard cache is empty
        final PapiExpansion expansion = new PapiExpansion();

        // When - PAPI requests each registered mctop variant
        final String result = expansion.onPlaceholderRequest(null, params);

        // Then - the request routes (empty string), proving the token registered and the
        // ':' split reached it; null would mean the token fell through unrouted
        assertThat(result).isNotNull().isEmpty();
    }

    @Test
    void onPlaceholderRequestShouldResolveLocalizedSkillNameToken() {
        // Given - a freshly constructed expansion with a stubbed localized name for Mining
        final PapiExpansion expansion = new PapiExpansion();

        // When - PAPI requests the plain skill name token
        final String result = expansion.onPlaceholderRequest(null, "skillname_mining");

        // Then - the token resolves to the message-style localized skill name
        assertThat(result).isEqualTo("Mining");
    }

    @Test
    void onPlaceholderRequestShouldResolveHeaderSkillNameToken() {
        // Given - a freshly constructed expansion with a stubbed header name for Mining
        final PapiExpansion expansion = new PapiExpansion();

        // When - PAPI requests the header skill name token
        final String result = expansion.onPlaceholderRequest(null, "skillname_header_mining");

        // Then - the token resolves to the stylized header skill name
        assertThat(result).isEqualTo("MINING");
    }

    @Test
    void onPlaceholderRequestShouldReturnNullWhenTokenIsUnknown() {
        // Given - a freshly constructed expansion
        final PapiExpansion expansion = new PapiExpansion();

        // When - PAPI requests a token no placeholder registered
        final String result = expansion.onPlaceholderRequest(null, "mctop_bogus:1");

        // Then - the expansion signals "not mine" with null so PAPI leaves the text untouched
        assertThat(result).isNull();
    }

    @Test
    void onPlaceholderRequestShouldRouteMcTopTokenWhenPositionIsMissing() {
        // Given - a freshly constructed expansion whose leaderboard cache is empty
        final PapiExpansion expansion = new PapiExpansion();

        // When - PAPI requests a leaderboard token without any ':' position segment
        final String result = expansion.onPlaceholderRequest(null, "mctop_mining");

        // Then - the token still routes and the missing position is treated as invalid,
        // resolving to an empty string instead of an error or raw text
        assertThat(result).isNotNull().isEmpty();
    }
}
