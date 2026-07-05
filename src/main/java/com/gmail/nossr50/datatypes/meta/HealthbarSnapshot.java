package com.gmail.nossr50.datatypes.meta;

import org.jetbrains.annotations.Nullable;

/**
 * Immutable snapshot of a mob's name state captured before mcMMO replaces it with a healthbar
 * display. Stored in entity metadata under
 * {@link com.gmail.nossr50.util.MetadataConstants#METADATA_KEY_HEALTHBAR_SNAPSHOT} and used to
 * restore the entity to its exact pre-healthbar state.
 *
 * <p>{@code previousCustomName} is {@code null} when the mob had no custom name before the
 * healthbar was applied. This is preserved exactly — never coerced to an empty string — so that
 * restoration via {@code setCustomName(null)} correctly clears the custom name slot rather than
 * setting it to an empty string.
 *
 * @param previousCustomName  the mob's custom name before healthbar was applied, or {@code null}
 * @param previousNameVisible whether the mob's custom name was visible before healthbar was applied
 * @param lastHitMs           wall-clock time of the most recent hit that refreshed this display,
 *                            in milliseconds from {@link System#currentTimeMillis()}; updated on
 *                            every re-hit so the cleanup task can extend the display window
 */
public record HealthbarSnapshot(
        @Nullable String previousCustomName,
        boolean previousNameVisible,
        long lastHitMs
) {
}
