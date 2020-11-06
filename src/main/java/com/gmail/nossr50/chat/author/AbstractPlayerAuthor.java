package com.gmail.nossr50.chat.author;

import com.gmail.nossr50.util.text.TextUtils;
import com.google.common.base.Objects;
import net.kyori.adventure.text.TextComponent;
import org.bukkit.entity.Player;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;

public abstract class AbstractPlayerAuthor implements Author {
    private final @NotNull Player player;
    private @NotNull String displayName;
    private @Nullable TextComponent componentDisplayName;
    private @Nullable TextComponent componentUserName;

    public AbstractPlayerAuthor(@NotNull Player player) {
        this.player = player;
        this.displayName = player.getDisplayName();
    }

    /**
     * Grabs the {@link TextComponent} version of a players display name
     * Cached and only processed as needed
     * Always checks if the player display name has changed, if it has it regenerates the output
     *
     * @return the {@link TextComponent} version of a players display name
     */
    public @NotNull TextComponent getComponentDisplayName() {
        //Not sure if this is expensive but it ensures always up to date names
        if(!player.getDisplayName().equals(displayName)) {
            displayName = player.getDisplayName();
            componentDisplayName = null;
        }

        if(componentDisplayName != null) {
            return componentDisplayName;
        } else {
            //convert to adventure component
            componentDisplayName = TextUtils.ofBungeeRawStrings(displayName);
        }
        return componentDisplayName;
    }

    /**
     * Grabs the {@link TextComponent} version of a players current minecraft nickname
     * Cached and only processed as needed
     *
     * @return the {@link TextComponent} version of a players current minecraft nickname
     */
    public @NotNull TextComponent getComponentUserName() {
        //Not sure if this is expensive but it ensures always up to date names
        if(componentUserName != null) {
            return componentUserName;
        } else {
            //convert to adventure component
            componentUserName = TextUtils.ofBungeeRawStrings(player.getName());
        }
        return componentUserName;
    }

    @Override
    public boolean isConsole() {
        return false;
    }

    @Override
    public boolean isPlayer() {
        return true;
    }

    public @NotNull Player getPlayer() {
        return player;
    }

    @Override
    public @NonNull UUID uuid() {
        return player.getUniqueId();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        AbstractPlayerAuthor that = (AbstractPlayerAuthor) o;
        return Objects.equal(player, that.player);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(player);
    }
}
