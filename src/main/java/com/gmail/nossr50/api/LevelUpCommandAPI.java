package com.gmail.nossr50.api;

import static java.util.Objects.requireNonNull;

import com.gmail.nossr50.commands.levelup.LevelUpCommand;
import com.gmail.nossr50.commands.levelup.LevelUpHandler;
import com.gmail.nossr50.commands.levelup.RegistrationSource;
import com.gmail.nossr50.mcMMO;
import java.util.Collections;
import java.util.UUID;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;

/**
 * Lets other plugins react to mcMMO level ups, either by having mcMMO dispatch commands or by
 * running their own code. Registrations return a {@link UUID}; hold on to it to
 * {@link #unregister(UUID)} later. Registrations do not persist across server restarts,
 * survive mcMMO config reloads, and are removed automatically when the registering plugin is
 * disabled.
 *
 * <pre>{@code
 * // dispatch a command when a player reaches Mining 100
 * UUID id = LevelUpCommandAPI.registerCommand(myPlugin, LevelUpCommand.builder()
 *         .withSkill(PrimarySkillType.MINING)
 *         .withLevels(List.of(100))
 *         .command("say {@player} mastered {@skill}!")
 *         .build());
 *
 * // or run your own code on every level up
 * UUID handlerId = LevelUpCommandAPI.registerHandler(myPlugin,
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
     * Registers a command to dispatch when its condition matches a level up. The registration
     * is removed automatically when the registering plugin is disabled.
     *
     * @param plugin the plugin registering the command
     * @param command the command definition, built via {@link LevelUpCommand#builder()}
     * @return the id used to {@link #unregister(UUID)} this command
     */
    public static @NotNull UUID registerCommand(@NotNull Plugin plugin,
            @NotNull LevelUpCommand command) {
        requireNonNull(plugin, "plugin cannot be null");
        return mcMMO.p.getLevelUpCommandManager().register(command, RegistrationSource.API,
                plugin);
    }

    /**
     * Registers a callback invoked on every level up of every skill. Filter inside the
     * handler for the skills and levels you care about. The registration is removed
     * automatically when the registering plugin is disabled.
     *
     * @param plugin the plugin registering the handler
     * @param handler the callback to invoke
     * @return the id used to {@link #unregister(UUID)} this handler
     */
    public static @NotNull UUID registerHandler(@NotNull Plugin plugin,
            @NotNull LevelUpHandler handler) {
        requireNonNull(plugin, "plugin cannot be null");
        requireNonNull(handler, "handler cannot be null");
        return mcMMO.p.getLevelUpCommandManager().register(
                (mmoPlayer, primarySkillType, levelsGained, powerLevelsGained) -> {
                    final int powerLevel = powerLevelsGained.isEmpty()
                            ? mmoPlayer.getPowerLevel() : Collections.max(powerLevelsGained);
                    handler.onLevelUp(mmoPlayer.getPlayer(), primarySkillType, levelsGained,
                            powerLevel);
                }, RegistrationSource.API, plugin);
    }

    /**
     * Removes a registration created by {@link #registerCommand(Plugin, LevelUpCommand)} or
     * {@link #registerHandler(Plugin, LevelUpHandler)}.
     *
     * @param registrationId the id returned at registration
     * @return true if a registration was removed
     */
    public static boolean unregister(@NotNull UUID registrationId) {
        return mcMMO.p.getLevelUpCommandManager().unregister(registrationId);
    }
}
