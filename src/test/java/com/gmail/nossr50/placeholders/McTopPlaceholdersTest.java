package com.gmail.nossr50.placeholders;

import static org.assertj.core.api.Assertions.assertThat;

import com.gmail.nossr50.datatypes.database.PlayerStat;
import com.gmail.nossr50.datatypes.skills.PrimarySkillType;
import java.util.List;
import java.util.logging.Logger;
import org.junit.jupiter.api.Test;

class McTopPlaceholdersTest {
    @Test
    void nameAndValuePlaceholdersShouldUseSameRankRowForSkill() {
        // Given - a skill leaderboard cache with deterministic MINING rows.
        LeaderboardPlaceholderCache cache = new LeaderboardPlaceholderCache(
                10,
                (skill, pageNumber, statsPerPage) -> {
                    if (skill == PrimarySkillType.MINING) {
                        return List.of(
                                new PlayerStat("first", 900),
                                new PlayerStat("second", 800),
                                new PlayerStat("third", 700)
                        );
                    }

                    return List.of();
                },
                Logger.getAnonymousLogger()
        );
        cache.refreshNow();

        // When - resolving name and value placeholders for the same positions.
        McTopNamePlaceholder namePlaceholder = new McTopNamePlaceholder(PrimarySkillType.MINING,
                cache);
        McTopPositionPlaceholder valuePlaceholder = new McTopPositionPlaceholder(
                PrimarySkillType.MINING, cache);

        // Then - both placeholders should map to the same underlying row for each rank.
        assertThat(namePlaceholder.process(null, "2")).isEqualTo("second");
        assertThat(valuePlaceholder.process(null, "2")).isEqualTo("800");
        assertThat(namePlaceholder.process(null, "3")).isEqualTo("third");
        assertThat(valuePlaceholder.process(null, "3")).isEqualTo("700");
    }

    @Test
    void overallPlaceholdersShouldSupportDirectPositionLookup() {
        // Given - a cache snapshot for overall leaderboard rows.
        LeaderboardPlaceholderCache cache = new LeaderboardPlaceholderCache(
                10,
                (skill, pageNumber, statsPerPage) -> {
                    if (skill == null) {
                        return List.of(
                                new PlayerStat("overallTop", 3000),
                                new PlayerStat("overallSecond", 2000),
                                new PlayerStat("overallThird", 1000)
                        );
                    }

                    return List.of();
                },
                Logger.getAnonymousLogger()
        );
        cache.refreshNow();

        // When - resolving overall placeholders by position.
        McTopNamePlaceholder namePlaceholder = new McTopNamePlaceholder(null, cache);
        McTopPositionPlaceholder valuePlaceholder = new McTopPositionPlaceholder(null, cache);

        // Then - valid positions should resolve and out-of-range positions should be empty.
        assertThat(namePlaceholder.process(null, "2")).isEqualTo("overallSecond");
        assertThat(valuePlaceholder.process(null, "2")).isEqualTo("2000");
        assertThat(namePlaceholder.process(null, "10")).isEmpty();
        assertThat(valuePlaceholder.process(null, "10")).isEmpty();
    }

    @Test
    void overallAliasesShouldHaveExpectedPlaceholderNames() {
        // Given - overall placeholder instances with canonical and alias tokens.
        LeaderboardPlaceholderCache cache = new LeaderboardPlaceholderCache(
                5,
                (skill, pageNumber, statsPerPage) -> List.of(),
                Logger.getAnonymousLogger()
        );

        // When - reading registered placeholder names.
        // Then - each alias should expose the expected token name.
        assertThat(new McTopPositionPlaceholder(null, cache).getName()).isEqualTo("mctop_overall");
        assertThat(new McTopNamePlaceholder(null, cache).getName()).isEqualTo("mctop_name_overall");

        assertThat(new McTopPositionPlaceholder(null, "all", cache).getName()).isEqualTo("mctop_all");
        assertThat(new McTopNamePlaceholder(null, "all", cache).getName()).isEqualTo("mctop_name_all");

        assertThat(new McTopPositionPlaceholder(null, "powerlevel", cache).getName()).isEqualTo("mctop_powerlevel");
        assertThat(new McTopNamePlaceholder(null, "powerlevel", cache).getName()).isEqualTo("mctop_name_powerlevel");
    }
}
