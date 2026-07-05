package com.gmail.nossr50.skills.maces;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.gmail.nossr50.MMOTestEnvironment;
import com.gmail.nossr50.api.exceptions.InvalidSkillException;
import com.gmail.nossr50.datatypes.skills.SubSkillType;
import com.gmail.nossr50.util.Permissions;
import com.gmail.nossr50.util.skills.RankUtils;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

class MacesTest extends MMOTestEnvironment {
    private static final java.util.logging.Logger logger = java.util.logging.Logger.getLogger(
            MacesTest.class.getName());

    private MacesManager macesManager;

    @BeforeEach
    void setUp() throws InvalidSkillException {
        mockBaseEnvironment(logger);
        macesManager = Mockito.spy(new MacesManager(mmoPlayer));
    }

    @AfterEach
    void tearDown() {
        cleanUpStaticMocks();
    }

    @Test
    void crushDamageShouldBeZeroWhenSubSkillNotUsable() {
        Mockito.when(Permissions.canUseSubSkill(player, SubSkillType.MACES_CRUSH)).thenReturn(false);

        assertEquals(0.0D, macesManager.getCrushDamage());
    }

    @Test
    void crushDamageShouldBeZeroAtRankZero() {
        Mockito.when(Permissions.canUseSubSkill(player, SubSkillType.MACES_CRUSH)).thenReturn(true);
        Mockito.when(advancedConfig.getCrushBaseDamage()).thenReturn(0.5D);
        Mockito.when(advancedConfig.getCrushRankDamageMultiplier()).thenReturn(1.0D);
        Mockito.when(RankUtils.getRank(player, SubSkillType.MACES_CRUSH)).thenReturn(0);

        assertEquals(0.0D, macesManager.getCrushDamage());
    }

    @Test
    void crushDamageShouldMatchConfiguredFormula() {
        Mockito.when(Permissions.canUseSubSkill(player, SubSkillType.MACES_CRUSH)).thenReturn(true);
        Mockito.when(advancedConfig.getCrushBaseDamage()).thenReturn(0.5D);
        Mockito.when(advancedConfig.getCrushRankDamageMultiplier()).thenReturn(1.0D);
        Mockito.when(RankUtils.getRank(player, SubSkillType.MACES_CRUSH)).thenReturn(4);

        assertEquals(4.5D, macesManager.getCrushDamage());
    }
}
