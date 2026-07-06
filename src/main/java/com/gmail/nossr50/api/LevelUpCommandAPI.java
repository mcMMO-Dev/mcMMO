package com.gmail.nossr50.api;

import com.gmail.nossr50.commands.levelup.LevelUpCommand;
import com.gmail.nossr50.commands.levelup.LevelUpHandler;
import com.gmail.nossr50.commands.levelup.RegistrationSource;
import com.gmail.nossr50.mcMMO;
import java.util.UUID;
import org.jetbrains.annotations.NotNull;

/**
 * Lets other plugins react to mcMMO level ups, either by having mcMMO dispatch commands or by
 * running their own code. Registrations return a {@link UUID}; hold on to it to
 * {@link #unregister(UUID)} later. Registrations do not persist across server restarts and
 * survive mcMMO config reloads.
 *
 * <pre>{@code
 * // dispatch a command when a player reaches Mining 100
 * UUID id = LevelUpCommandAPI.registerCommand(LevelUpCommand.builder()
 *         .withSkill(PrimarySkillType.MINING)
 *         .withLevels(List.of(100))
 *         .command("say {@player} mastered {@skill}!")
 *         .build());
 *
 * // or run your own code on every level up
 * UUID handlerId = LevelUpCommandAPI.registerHandler(
 *         (player, skill, levelsGained, powerLevel) -> {
 *             // your logic here
 *         });
 *
 * LevelUpCommandAPI.unregister(id);
 * }</pre>
 */
public final class LevelUpCommandAPI {

    private LevelUpCommandAPI() {
    }

    /**
     * Registers a command to dispatch when its condition matches a level up.
     *
     * @param command the command definition, built via {@link LevelUpCommand#builder()}
     * @return the id used to {@link #unregister(UUID)} this command
     */
    public static @NotNull UUID registerCommand(@NotNull LevelUpCommand command) {
        return mcMMO.p.getLevelUpCommandManager().register(command, RegistrationSource.API);
    }

    /**
     * Registers a callback invoked on every level up of every skill. Filter inside the
     * handler for the skills and levels you care about.
     *
     * @param handler the callback to invoke
     * @return the id used to {@link #unregister(UUID)} this handler
     */
    public static @NotNull UUID registerHandler(@NotNull LevelUpHandler handler) {
        return mcMMO.p.getLevelUpCommandManager().register(
                (mmoPlayer, primarySkillType, levelsGained, powerLevelsGained) ->
                        handler.onLevelUp(mmoPlayer.getPlayer(), primarySkillType, levelsGained,
                                mmoPlayer.getPowerLevel()),
                RegistrationSource.API);
    }

    /**
     * Removes a registration created by {@link #registerCommand(LevelUpCommand)} or
     * {@link #registerHandler(LevelUpHandler)}.
     *
     * @param registrationId the id returned at registration
     * @return true if a registration was removed
     */
    public static boolean unregister(@NotNull UUID registrationId) {
        return mcMMO.p.getLevelUpCommandManager().unregister(registrationId);
    }
}
