package com.gmail.nossr50.commands.levelup;

import com.gmail.nossr50.MMOTestEnvironmentBasic;
import com.gmail.nossr50.datatypes.experience.XPGainReason;
import com.gmail.nossr50.datatypes.player.McMMOPlayer;
import com.gmail.nossr50.datatypes.player.PlayerProfile;
import com.gmail.nossr50.datatypes.skills.PrimarySkillType;
import com.gmail.nossr50.events.experience.McMMOPlayerLevelUpEvent;
import com.gmail.nossr50.listeners.SelfListener;
import com.gmail.nossr50.mcMMO;
import org.bukkit.entity.Player;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.mockito.Mockito;

import java.util.Set;
import java.util.UUID;
import java.util.function.Predicate;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class LevelUpCommandTest extends MMOTestEnvironmentBasic {

    @BeforeEach
    void setUp() {
        mockBaseEnvironment();
    }

    @AfterEach
    void tearDown() {
        cleanupBaseEnvironment();
    }

    @Test
    void levelInMiningShouldRunCommand() {
        // validate command manager has zero registered commands
        assert mcMMO.p.getLevelUpCommandManager().isEmpty();
        final PrimarySkillType skillType = PrimarySkillType.MINING;
        final Predicate<Integer> predicate = (i) -> true;
        final LevelUpCommand levelUpCommand = spy(new LevelUpCommandImpl(
                predicate,
                "say hello",
                Set.of(skillType),
                true));
        mcMMO.p.getLevelUpCommandManager().registerCommand(levelUpCommand);

        // GIVEN level up command that should always execute for Mining is registered with command manager

        int levelsGained = 5;
        // WHEN player gains 5 levels in mining
        McMMOPlayerLevelUpEvent event = new McMMOPlayerLevelUpEvent(player, PrimarySkillType.MINING, levelsGained, XPGainReason.PVE);
        selfListener.onPlayerLevelUp(event);

        // THEN the command should be run
        // check the mockito spy for level up command manager for executing the command
        Mockito.verify(levelUpCommandManager).apply(any(), any(), any());
        Mockito.verify(levelUpCommand).apply(any(), any(), any());
    }
}