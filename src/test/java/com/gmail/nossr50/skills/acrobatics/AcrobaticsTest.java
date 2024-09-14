package com.gmail.nossr50.skills.acrobatics;

import com.gmail.nossr50.MMOTestEnvironment;
import com.gmail.nossr50.api.exceptions.InvalidSkillException;
import com.gmail.nossr50.datatypes.skills.PrimarySkillType;
import com.gmail.nossr50.datatypes.skills.SubSkillType;
import com.gmail.nossr50.datatypes.skills.subskills.AbstractSubSkill;
import com.gmail.nossr50.datatypes.skills.subskills.acrobatics.Roll;
import com.gmail.nossr50.mcMMO;
import com.gmail.nossr50.util.skills.RankUtils;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageEvent;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.logging.Logger;

import static java.util.logging.Logger.getLogger;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

class AcrobaticsTest extends MMOTestEnvironment {
    private static final Logger logger = getLogger(AcrobaticsTest.class.getName());

    @BeforeEach
    void setUp() throws InvalidSkillException {
        mockBaseEnvironment(logger);
        when(rankConfig.getSubSkillUnlockLevel(SubSkillType.ACROBATICS_ROLL, 1)).thenReturn(1);
        when(rankConfig.getSubSkillUnlockLevel(SubSkillType.ACROBATICS_DODGE, 1)).thenReturn(1);

        // wire advanced config
        when(advancedConfig.getMaximumProbability(SubSkillType.ACROBATICS_ROLL)).thenReturn(100D);
        when(advancedConfig.getMaxBonusLevel(SubSkillType.ACROBATICS_ROLL)).thenReturn(1000);
        when(advancedConfig.getRollDamageThreshold()).thenReturn(7D);

        Mockito.when(RankUtils.getRankUnlockLevel(SubSkillType.ACROBATICS_ROLL, 1)).thenReturn(1); // needed?
        Mockito.when(RankUtils.getRankUnlockLevel(SubSkillType.ACROBATICS_DODGE, 1)).thenReturn(1000); // needed?

        when(RankUtils.getRankUnlockLevel(SubSkillType.ACROBATICS_ROLL, 1)).thenReturn(1); // needed?
        when(RankUtils.hasReachedRank(eq(1), any(Player.class), eq(SubSkillType.ACROBATICS_ROLL))).thenReturn(true);
        when(RankUtils.hasReachedRank(eq(1), any(Player.class), any(AbstractSubSkill.class))).thenReturn(true);
    }

    @AfterEach
    void tearDown() {
        cleanUpStaticMocks();
    }

    @SuppressWarnings("deprecation")
    @Test
    public void rollShouldLowerDamage() {
        // Given
        final Roll roll = new Roll();
        final double damage = 2D;
        final EntityDamageEvent mockEvent = mockEntityDamageEvent(damage);
        mmoPlayer.modifySkill(PrimarySkillType.ACROBATICS, 1000);
        when(roll.canRoll(mmoPlayer)).thenReturn(true);
        assertThat(roll.canRoll(mmoPlayer)).isTrue();

        // When
        roll.doInteraction(mockEvent, mcMMO.p);

        // Then
        verify(mockEvent, atLeastOnce()).setDamage(0);
    }

    @SuppressWarnings("deprecation")
    @Test
    public void rollShouldNotLowerDamage() {
        // Given
        final Roll roll = new Roll();
        final double damage = 100D;
        final EntityDamageEvent mockEvent = mockEntityDamageEvent(damage);
        mmoPlayer.modifySkill(PrimarySkillType.ACROBATICS, 0);
        when(roll.canRoll(mmoPlayer)).thenReturn(true);
        assertThat(roll.canRoll(mmoPlayer)).isTrue();

        // When
        roll.doInteraction(mockEvent, mcMMO.p);

        // Then
        assertThat(roll.canRoll(mmoPlayer)).isTrue();
        verify(mockEvent, Mockito.never()).setDamage(any(Double.class));
    }

    private @NotNull EntityDamageEvent mockEntityDamageEvent(double damage) {
        final EntityDamageEvent mockEvent = mock(EntityDamageEvent.class);
        when(mockEvent.getCause()).thenReturn(EntityDamageEvent.DamageCause.FALL);
        when(mockEvent.getFinalDamage()).thenReturn(damage);
        when(mockEvent.getDamage(any(EntityDamageEvent.DamageModifier.class))).thenReturn(damage);
        when(mockEvent.getDamage()).thenReturn(damage);
        when(mockEvent.isCancelled()).thenReturn(false);
        when(mockEvent.getEntity()).thenReturn(player);
        return mockEvent;
    }
}