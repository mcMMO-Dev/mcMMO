package com.gmail.nossr50.placeholders;

import static org.assertj.core.api.Assertions.assertThat;

import com.gmail.nossr50.datatypes.database.LeaderboardSnapshot;
import com.gmail.nossr50.datatypes.database.PlayerStat;
import com.gmail.nossr50.datatypes.skills.PrimarySkillType;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;
import org.junit.jupiter.api.Test;

class McTopPlaceholdersTest {
    @Test
    void nameAndValuePlaceholdersShouldUseSameRankRowForSkill() {
        // Given - a skill leaderboard cache with deterministic MINING rows.
        LeaderboardPlaceholderCache cache = new LeaderboardPlaceholderCache(10,
                perScopeLimit -> new LeaderboardSnapshot(
                        Map.of(PrimarySkillType.MINING, List.of(
                                new PlayerStat("first", 900),
                                new PlayerStat("second", 800),
                                new PlayerStat("third", 700)
                        )),
                        List.of()
                ), Logger.getAnonymousLogger());
        cache.refreshNow();

        // When - resolving name and value placeholders for the same positions.
        McTopPlaceholder namePlaceholder = McTopPlaceholder.name(PrimarySkillType.MINING, cache);
        McTopPlaceholder valuePlaceholder = McTopPlaceholder.value(PrimarySkillType.MINING, cache);

        // Then - both placeholders should map to the same underlying row for each rank.
        assertThat(namePlaceholder.process(null, "2")).isEqualTo("second");
        assertThat(valuePlaceholder.process(null, "2")).isEqualTo("800");
        assertThat(namePlaceholder.process(null, "3")).isEqualTo("third");
        assertThat(valuePlaceholder.process(null, "3")).isEqualTo("700");
    }

    @Test
    void overallPlaceholdersShouldSupportDirectPositionLookup() {
        // Given - a cache snapshot for overall leaderboard rows.
        LeaderboardPlaceholderCache cache = new LeaderboardPlaceholderCache(10,
                perScopeLimit -> new LeaderboardSnapshot(Map.of(), List.of(
                        new PlayerStat("overallTop", 3000),
                        new PlayerStat("overallSecond", 2000),
                        new PlayerStat("overallThird", 1000)
                )), Logger.getAnonymousLogger());
        cache.refreshNow();

        // When - resolving overall placeholders by position.
        McTopPlaceholder namePlaceholder = McTopPlaceholder.name(null, cache);
        McTopPlaceholder valuePlaceholder = McTopPlaceholder.value(null, cache);

        // Then - valid positions should resolve and out-of-range positions should be empty.
        assertThat(namePlaceholder.process(null, "2")).isEqualTo("overallSecond");
        assertThat(valuePlaceholder.process(null, "2")).isEqualTo("2000");
        assertThat(namePlaceholder.process(null, "10")).isEmpty();
        assertThat(valuePlaceholder.process(null, "10")).isEmpty();
    }

    @Test
    void overallAliasesShouldHaveExpectedPlaceholderNames() {
        // Given - overall placeholder instances with canonical and alias tokens.
        LeaderboardPlaceholderCache cache = new LeaderboardPlaceholderCache(5,
                perScopeLimit -> new LeaderboardSnapshot(Map.of(), List.of()),
                Logger.getAnonymousLogger());

        // When - reading registered placeholder names.
        // Then - each alias should expose the expected token name.
        assertThat(McTopPlaceholder.value(null, cache).getName()).isEqualTo("mctop_overall");
        assertThat(McTopPlaceholder.name(null, cache).getName()).isEqualTo("mctop_name_overall");

        assertThat(McTopPlaceholder.value(null, "all", cache).getName()).isEqualTo("mctop_all");
        assertThat(McTopPlaceholder.name(null, "all", cache).getName()).isEqualTo("mctop_name_all");

        assertThat(McTopPlaceholder.value(null, "powerlevel", cache).getName()).isEqualTo("mctop_powerlevel");
        assertThat(McTopPlaceholder.name(null, "powerlevel", cache).getName()).isEqualTo("mctop_name_powerlevel");
    }

    /**
     * Skill-scoped registration depends on these exact token names; a change here would break
     * every existing %mcmmo_mctop_<skill>% placeholder in user configs.
     */
    @Test
    void skillScopedPlaceholdersShouldUseLowercaseSkillTokenNames() {
        // Given - a cache and a skill-scoped placeholder pair.
        LeaderboardPlaceholderCache cache = new LeaderboardPlaceholderCache(5,
                perScopeLimit -> new LeaderboardSnapshot(Map.of(), List.of()),
                Logger.getAnonymousLogger());

        // When - reading the registered placeholder names.
        // Then - both should use the lowercase skill token.
        assertThat(McTopPlaceholder.value(PrimarySkillType.MINING, cache).getName())
                .isEqualTo("mctop_mining");
        assertThat(McTopPlaceholder.name(PrimarySkillType.MINING, cache).getName())
                .isEqualTo("mctop_name_mining");
    }
}
