package com.gmail.nossr50.util.scoreboards.backend;

import com.gmail.nossr50.events.scoreboard.McMMOScoreboardObjectiveEvent;
import com.gmail.nossr50.events.scoreboard.ScoreboardEventReason;
import com.gmail.nossr50.events.scoreboard.ScoreboardObjectiveEventReason;
import com.gmail.nossr50.locale.LocaleLoader;
import com.gmail.nossr50.mcMMO;
import com.gmail.nossr50.util.player.UserManager;
import com.gmail.nossr50.datatypes.player.McMMOPlayer;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Scoreboard;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class BukkitPlayerBoard implements PlayerBoard {
    private static final String SIDEBAR_OBJECTIVE = "mcmmo_sidebar";
    private static final String SIDE_OBJECTIVE = "mcMMO_sideObjective";
    private static final String POWER_OBJECTIVE = "mcmmo_pwrlvl";

    private final @NotNull Player owner;
    private @NotNull Scoreboard scoreboard;
    private final Set<String> renderedEntries;
    private @Nullable Objective sidebarObjective;
    private @Nullable Objective powerObjective;

    public BukkitPlayerBoard(final @NotNull Player owner, final @NotNull Scoreboard scoreboard) {
        this.owner = owner;
        this.scoreboard = scoreboard;
        this.renderedEntries = new HashSet<>();
        this.sidebarObjective = scoreboard.getObjective(SIDEBAR_OBJECTIVE);
        this.powerObjective = null;
        setupPowerObjectiveIfEnabled();
    }

    @Override
    public @Nullable Scoreboard show() {
        final Scoreboard previousBoard = owner.getScoreboard();
        owner.setScoreboard(scoreboard);
        return previousBoard;
    }

    @Override
    public void hide(final @NotNull Player targetPlayer, final @Nullable Scoreboard targetBoard) {
        if (targetBoard != null) {
            targetPlayer.setScoreboard(targetBoard);
            return;
        }

        if (Bukkit.getScoreboardManager() != null) {
            targetPlayer.setScoreboard(Bukkit.getScoreboardManager().getMainScoreboard());
        }
    }

    @Override
    public boolean isShown() {
        return owner.getScoreboard() == scoreboard;
    }

    @Override
    public void setTitle(final @NotNull String displayName) {
        final McMMOScoreboardObjectiveEvent unregisterEvent = callObjectiveEvent(
                ScoreboardObjectiveEventReason.UNREGISTER_THIS_OBJECTIVE);
        if (!unregisterEvent.isCancelled() && sidebarObjective != null) {
            try {
                sidebarObjective.unregister();
            } catch (IllegalStateException e) {
                mcMMO.p.getLogger().fine("Ignoring stale sidebar objective while updating bukkit scoreboard for "
                        + owner.getName() + ": " + e.getMessage());
            }
        }

        final McMMOScoreboardObjectiveEvent registerEvent = callObjectiveEvent(
                ScoreboardObjectiveEventReason.REGISTER_NEW_OBJECTIVE);

        if (!registerEvent.isCancelled()) {
            scoreboard = registerEvent.getTargetBoard();
            Objective existingObjective = scoreboard.getObjective(SIDEBAR_OBJECTIVE);

            if (existingObjective != null) {
                try {
                    existingObjective.unregister();
                } catch (IllegalStateException e) {
                    mcMMO.p.getLogger().fine("Ignoring stale existing objective while replacing bukkit scoreboard for "
                            + owner.getName() + ": " + e.getMessage());
                }
            }

            sidebarObjective = scoreboard.registerNewObjective(SIDEBAR_OBJECTIVE, "dummy", SIDE_OBJECTIVE);
        }

        if (sidebarObjective == null) {
            return;
        }

        final String safeDisplayName = displayName.length() > 32
                ? displayName.substring(0, 32)
                : displayName;
        sidebarObjective.setDisplayName(safeDisplayName);
        sidebarObjective.setDisplaySlot(DisplaySlot.SIDEBAR);
        resetRenderedEntries();
    }

    @Override
    public void draw(final @NotNull List<SidebarLine> lines) {
        if (sidebarObjective == null) {
            return;
        }

        resetRenderedEntries();
        final int count = Math.min(lines.size(), 15);
        for (int i = 0; i < count; i++) {
            final SidebarLine line = lines.get(i);
            sidebarObjective.getScore(line.label()).setScore(line.value());
            renderedEntries.add(line.label());
        }
    }

    public void updatePowerLevel(final @NotNull String playerName, final int powerLevel) {
        if (powerObjective != null) {
            powerObjective.getScore(playerName).setScore(powerLevel);
        }
    }

    private @NotNull McMMOScoreboardObjectiveEvent callObjectiveEvent(
            final @NotNull ScoreboardObjectiveEventReason reason) {
        final McMMOScoreboardObjectiveEvent event = new McMMOScoreboardObjectiveEvent(
                sidebarObjective,
                reason,
                scoreboard,
                scoreboard,
                owner,
                ScoreboardEventReason.OBJECTIVE);
        owner.getServer().getPluginManager().callEvent(event);
        return event;
    }

    private void resetRenderedEntries() {
        for (String entry : renderedEntries) {
            scoreboard.resetScores(entry);
        }
        renderedEntries.clear();
    }

    private void setupPowerObjectiveIfEnabled() {
        if (!mcMMO.p.getGeneralConfig().getPowerLevelTagsEnabled()) {
            return;
        }

        Objective existingPowerObjective = scoreboard.getObjective(POWER_OBJECTIVE);
        if (existingPowerObjective == null) {
            existingPowerObjective = scoreboard.registerNewObjective(POWER_OBJECTIVE, "dummy", "mcMMO_powerObjective");
        }

        existingPowerObjective.setDisplayName(LocaleLoader.getString("Scoreboard.Header.PowerLevel"));
        existingPowerObjective.setDisplaySlot(DisplaySlot.BELOW_NAME);

        for (McMMOPlayer mmoPlayer : UserManager.getPlayers()) {
            existingPowerObjective.getScore(mmoPlayer.getProfile().getPlayerName())
                    .setScore(mmoPlayer.getPowerLevel());
        }

        this.powerObjective = existingPowerObjective;
    }

    @Override
    public void close() {
        renderedEntries.clear();
    }
}
