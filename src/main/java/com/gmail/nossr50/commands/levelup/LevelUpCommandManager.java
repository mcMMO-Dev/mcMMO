package com.gmail.nossr50.commands.levelup;

import static java.util.Objects.requireNonNull;

import com.gmail.nossr50.datatypes.player.McMMOPlayer;
import com.gmail.nossr50.datatypes.skills.PrimarySkillType;
import com.gmail.nossr50.mcMMO;
import com.gmail.nossr50.util.LogUtils;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Level;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Registry of everything that reacts to level ups: commands loaded from
 * {@code level_up_commands.yml} and commands or handlers registered by other plugins through
 * {@link com.gmail.nossr50.api.LevelUpCommandAPI}. Each registration gets a {@link UUID} that
 * can later be used to remove it. Config reloads clear only config-sourced registrations.
 * <p>
 * The registry is thread safe: on Folia, level up events fire on region threads while plugins
 * register from the global thread.
 */
public class LevelUpCommandManager {
    private final @NotNull Map<UUID, Registration> registrations = new ConcurrentHashMap<>();
    private final @NotNull mcMMO plugin;

    public LevelUpCommandManager(@NotNull mcMMO plugin) {
        this.plugin = requireNonNull(plugin, "plugin cannot be null");
    }

    /**
     * Registers an action to run on level ups.
     *
     * @param action the action to run
     * @param source where the registration came from
     * @return the id used to remove this registration later
     */
    public @NotNull UUID register(@NotNull LevelUpAction action,
            @NotNull RegistrationSource source) {
        return register(action, source, null);
    }

    /**
     * Registers an action to run on level ups on behalf of a plugin. Registrations with an
     * owner are removed automatically when that plugin is disabled.
     *
     * @param action the action to run
     * @param source where the registration came from
     * @param owner the plugin that owns this registration, or null for config entries
     * @return the id used to remove this registration later
     */
    public @NotNull UUID register(@NotNull LevelUpAction action,
            @NotNull RegistrationSource source, @Nullable Plugin owner) {
        requireNonNull(action, "action cannot be null");
        requireNonNull(source, "source cannot be null");
        final UUID id = UUID.randomUUID();
        registrations.put(id, new Registration(source, action, owner));
        LogUtils.debug(plugin.getLogger(),
                "Registered level up action " + id + " from " + source + ": " + action);
        return id;
    }

    /**
     * Removes a registration by id.
     *
     * @param id the id returned when the registration was added
     * @return true if a registration was removed
     */
    public boolean unregister(@NotNull UUID id) {
        requireNonNull(id, "id cannot be null");
        final Registration removed = registrations.remove(id);
        if (removed != null) {
            LogUtils.debug(plugin.getLogger(), "Removed level up action " + id);
        }
        return removed != null;
    }

    public boolean isRegistered(@NotNull UUID id) {
        return registrations.containsKey(id);
    }

    public int registrationCount() {
        return registrations.size();
    }

    /**
     * Whether anything is registered at all. Lets callers skip level up bookkeeping entirely
     * when nothing would react to it.
     */
    public boolean hasRegistrations() {
        return !registrations.isEmpty();
    }

    /**
     * Runs every registration against a level up.
     *
     * @param mmoPlayer the player who leveled up
     * @param primarySkillType the skill that leveled up
     * @param levelsGained every skill level reached during this level up
     * @param powerLevelsGained every power level reached during this level up
     */
    public void applyLevelUp(@NotNull McMMOPlayer mmoPlayer,
            @NotNull PrimarySkillType primarySkillType, @NotNull Set<Integer> levelsGained,
            @NotNull Set<Integer> powerLevelsGained) {
        if (!mmoPlayer.getPlayer().isOnline()) {
            return;
        }

        for (Map.Entry<UUID, Registration> entry : registrations.entrySet()) {
            try {
                entry.getValue().action().onLevelUp(mmoPlayer, primarySkillType, levelsGained,
                        powerLevelsGained);
            } catch (RuntimeException e) {
                plugin.getLogger().log(Level.SEVERE,
                        "Level up action " + entry.getKey() + " threw an exception", e);
            }
        }
    }

    /**
     * Removes all registrations loaded from config. Called when the config (re)loads so API
     * registrations from other plugins survive.
     */
    public void clearConfigRegistrations() {
        final int before = registrations.size();
        registrations.values().removeIf(registration ->
                registration.source() == RegistrationSource.CONFIG);
        final int removed = before - registrations.size();
        if (removed > 0) {
            LogUtils.debug(plugin.getLogger(),
                    "Cleared " + removed + " config level up registrations");
        }
    }

    /**
     * Removes every registration owned by the given plugin. Called when a plugin is disabled
     * so its registrations never outlive it.
     *
     * @param owner the plugin whose registrations should be removed
     */
    public void clearPluginRegistrations(@NotNull Plugin owner) {
        requireNonNull(owner, "owner cannot be null");
        final int before = registrations.size();
        registrations.values().removeIf(registration -> registration.owner() == owner);
        final int removed = before - registrations.size();
        if (removed > 0) {
            LogUtils.debug(plugin.getLogger(), "Cleared " + removed
                    + " level up registrations owned by " + owner.getName());
        }
    }

    /**
     * Removes every registration, including those registered by other plugins.
     */
    public void clearAll() {
        registrations.clear();
    }

    /**
     * The commands registered from the given source. Handler registrations are not included.
     */
    public @NotNull List<LevelUpCommand> getCommands(@NotNull RegistrationSource source) {
        final List<LevelUpCommand> commands = new ArrayList<>();
        for (Registration registration : registrations.values()) {
            if (registration.source() == source
                    && registration.action() instanceof LevelUpCommand command) {
                commands.add(command);
            }
        }
        return commands;
    }

    /**
     * The command registered under the given id, or null when the id is unknown or belongs to
     * a handler registration.
     */
    public @Nullable LevelUpCommand getCommand(@NotNull UUID id) {
        final Registration registration = registrations.get(id);
        if (registration != null && registration.action() instanceof LevelUpCommand command) {
            return command;
        }
        return null;
    }

    private record Registration(@NotNull RegistrationSource source,
                                @NotNull LevelUpAction action, @Nullable Plugin owner) {
    }
}
