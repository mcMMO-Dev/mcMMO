package com.gmail.nossr50.listeners;

import java.util.function.BooleanSupplier;
import org.jetbrains.annotations.NotNull;

/**
 * Pure decision logic for player-vs-player friendly fire, shared by the damage and combust
 * listeners. Callers decide what each outcome means for their event: the damage path skips
 * skill processing for every outcome except {@link Outcome#PROCESS}, while only
 * {@link Outcome#SELF} and {@link Outcome#CANCEL_FRIENDLY_FIRE} justify cancelling an event.
 */
final class FriendlyFire {

    enum Outcome {
        /** Attacker and defender are the same player. */
        SELF,
        /** Disallowed party/ally friendly fire; the event should be cancelled. */
        CANCEL_FRIENDLY_FIRE,
        /** A player's mcMMO data has not loaded; skip skill processing but never cancel. */
        DATA_NOT_LOADED,
        /** Normal PvP; continue processing. */
        PROCESS
    }

    private FriendlyFire() {
    }

    /**
     * Resolves the friendly-fire outcome for an attack between two players. The suppliers are
     * evaluated lazily in cheap-to-expensive order, so party lookups and permission checks only
     * run when an earlier gate has not already decided the outcome.
     */
    static @NotNull Outcome resolve(boolean samePlayer, boolean partySystemEnabled,
            @NotNull BooleanSupplier bothPlayersDataLoaded,
            @NotNull BooleanSupplier partyFriendlyFireAllowed,
            @NotNull BooleanSupplier inSamePartyOrAllied,
            @NotNull BooleanSupplier bothHaveFriendlyFirePermission) {
        if (samePlayer) {
            return Outcome.SELF;
        }

        if (!partySystemEnabled) {
            return Outcome.PROCESS;
        }

        if (!bothPlayersDataLoaded.getAsBoolean()) {
            return Outcome.DATA_NOT_LOADED;
        }

        if (!partyFriendlyFireAllowed.getAsBoolean() && inSamePartyOrAllied.getAsBoolean()
                && !bothHaveFriendlyFirePermission.getAsBoolean()) {
            return Outcome.CANCEL_FRIENDLY_FIRE;
        }

        return Outcome.PROCESS;
    }
}
