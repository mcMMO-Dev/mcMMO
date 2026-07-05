package com.gmail.nossr50.util.scoreboards.backend;

import java.util.List;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import net.megavex.scoreboardlibrary.api.objective.ScoreFormat;
import net.megavex.scoreboardlibrary.api.sidebar.Sidebar;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.Scoreboard;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class PacketPlayerBoard implements PlayerBoard {
    private final @NotNull Player owner;
    private final @NotNull Sidebar sidebar;
    private final LegacyComponentSerializer serializer = LegacyComponentSerializer.legacySection();

    public PacketPlayerBoard(final @NotNull Player owner, final @NotNull Sidebar sidebar) {
        this.owner = owner;
        this.sidebar = sidebar;
    }

    @Override
    public @Nullable Scoreboard show() {
        sidebar.addPlayer(owner);
        return null;
    }

    @Override
    public void hide(final @NotNull Player targetPlayer, final @Nullable Scoreboard targetBoard) {
        sidebar.removePlayer(targetPlayer);
    }

    @Override
    public boolean isShown() {
        return sidebar.players().contains(owner);
    }

    @Override
    public void setTitle(final @NotNull String displayName) {
        final String safeDisplayName = displayName.length() > 32
                ? displayName.substring(0, 32)
                : displayName;
        sidebar.title(serializer.deserialize(safeDisplayName));
    }

    @Override
    public void draw(final @NotNull List<SidebarLine> lines) {
        final int count = Math.min(lines.size(), Sidebar.MAX_LINES);
        sidebar.clearLines();

        for (int i = 0; i < count; i++) {
            final SidebarLine line = lines.get(i);
            final Component component = serializer.deserialize(line.label() == null ? "" : line.label());
            sidebar.line(i, component, ScoreFormat.fixed(Component.text(line.value(), NamedTextColor.RED)));
        }
    }

    @Override
    public void close() {
        if (!sidebar.closed()) {
            sidebar.close();
        }
    }
}
