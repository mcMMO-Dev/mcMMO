package com.gmail.nossr50.util;

import static com.gmail.nossr50.listeners.EntityListener.isArmorStandEntity;
import static com.gmail.nossr50.listeners.EntityListener.isMannequinEntity;

import com.gmail.nossr50.datatypes.MobHealthbarType;
import com.gmail.nossr50.datatypes.meta.HealthbarSnapshot;
import com.gmail.nossr50.mcMMO;
import com.gmail.nossr50.runnables.MobHealthDisplayUpdaterTask;
import java.util.List;
import org.bukkit.ChatColor;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.metadata.MetadataValue;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public final class MobHealthbarUtils {
    private MobHealthbarUtils() {
    }

    /**
     * Fix issues with death messages caused by the mob healthbars.
     *
     * <p><b>Not called internally.</b> mcMMO's own fix path is
     * {@code PlayerListener.onEntityDamageByEntityHighest}, which calls
     * {@link #restoreNameFromSnapshot} on the attacker <em>before</em> the death message fires so
     * the message uses the real mob name naturally. That approach is preferred over post-hoc regex
     * replacement.
     *
     * <p>As a best-effort side effect this method now also calls {@link #restoreNameFromSnapshot}
     * on the attacker (when available from the player's last damage cause), so external callers
     * that still use this method benefit from both the proactive name restore and the regex
     * fallback for any healthbar characters that may have slipped through.
     *
     * @param deathMessage The original death message
     * @param player The player who died
     * @return the fixed death message
     * @deprecated Prefer proactively calling {@link #restoreNameFromSnapshot} on the attacker
     *     before the death message fires rather than fixing the message string after the fact.
     */
    @Deprecated
    public static String fixDeathMessage(String deathMessage, Player player) {
        if (player.getLastDamageCause() instanceof EntityDamageByEntityEvent edbe
                && edbe.getDamager() instanceof LivingEntity attacker) {
            restoreNameFromSnapshot(attacker);
        }
        return deathMessage;
    }

    /**
     * Handle the creation of mob healthbars.
     *
     * @param target the targetted entity
     * @param damage damage done by the attack triggering this
     */
    public static void handleMobHealthbars(LivingEntity target, double damage, mcMMO plugin) {
        if (isArmorStandEntity(target) || isMannequinEntity(target)) {
            return;
        }

        if (mcMMO.isHealthBarPluginEnabled()
                || !mcMMO.p.getGeneralConfig().getMobHealthbarEnabled()) {
            return;
        }

        if (isBoss(target) || target instanceof Player) {
            return;
        }

        // Don't mangle invalid entities, they're not going to be rendered anyways
        if (!target.isValid()) {
            return;
        }

        // Capture the pre-healthbar name state. null is preserved as null — never coerced to ""
        // so that restoration calls setCustomName(null) and correctly clears the custom name slot.
        final @Nullable String previousCustomName = target.getCustomName();
        final boolean previousNameVisible = target.isCustomNameVisible();

        final String newName = createHealthDisplay(mcMMO.p.getGeneralConfig().getMobHealthbarDefault(),
                target, damage);

        target.setCustomName(newName);
        target.setCustomNameVisible(true);

        final int displayTime = mcMMO.p.getGeneralConfig().getMobHealthbarTime();

        final long now = System.currentTimeMillis();
        final long displayTimeMs = (long) displayTime * 1000L;
        final long initialDelayTicks = (long) displayTime * Misc.TICK_CONVERSION_FACTOR;

        if (!target.hasMetadata(MetadataConstants.METADATA_KEY_HEALTHBAR_SNAPSHOT)) {
            // First hit: capture original name and schedule ONE self-managing cleanup task.
            // The task polls every few ticks after the initial delay and extends itself whenever
            // the mob is hit again before the display window expires.
            target.setMetadata(MetadataConstants.METADATA_KEY_HEALTHBAR_SNAPSHOT,
                    new FixedMetadataValue(plugin,
                            new HealthbarSnapshot(previousCustomName, previousNameVisible, now)));

            mcMMO.p.getFoliaLib().getScheduler()
                    .runAtEntityTimer(target,
                            new MobHealthDisplayUpdaterTask(target, displayTimeMs),
                            initialDelayTicks,
                            MobHealthDisplayUpdaterTask.POLL_INTERVAL_TICKS);
        } else {
            // Re-hit: refresh lastHitMs so the existing task extends the display window.
            // Original name fields are preserved from the first-hit snapshot — overwriting them
            // here would replace the real name with the current healthbar string.
            final HealthbarSnapshot existing = getHealthbarSnapshot(target);
            if (existing != null) {
                target.setMetadata(MetadataConstants.METADATA_KEY_HEALTHBAR_SNAPSHOT,
                        new FixedMetadataValue(plugin,
                                new HealthbarSnapshot(
                                        existing.previousCustomName(),
                                        existing.previousNameVisible(),
                                        now)));
            }
        }
    }

    /**
     * Restores a mob's custom name and name-visibility to their pre-healthbar state using the
     * {@link com.gmail.nossr50.datatypes.meta.HealthbarSnapshot} stored in entity metadata,
     * then removes the snapshot key.
     *
     * <p>This is the single canonical restore path. All callers — the display timer task,
     * the lethal-damage handler, and the entity-cleanup path — must use this method rather than
     * duplicating the check-restore-remove pattern.
     *
     * @param entity the entity to restore
     */
    public static void restoreNameFromSnapshot(@NotNull LivingEntity entity) {
        final List<MetadataValue> meta =
                entity.getMetadata(MetadataConstants.METADATA_KEY_HEALTHBAR_SNAPSHOT);
        if (meta.isEmpty()) {
            return;
        }

        final HealthbarSnapshot snapshot = (HealthbarSnapshot) meta.get(0).value();
        // Restore null as null — setCustomName(null) correctly clears the slot.
        entity.setCustomName(snapshot.previousCustomName());
        entity.setCustomNameVisible(snapshot.previousNameVisible());
        entity.removeMetadata(MetadataConstants.METADATA_KEY_HEALTHBAR_SNAPSHOT, mcMMO.p);
    }

    /**
     * Returns {@code true} if this entity currently has an active healthbar snapshot, meaning
     * mcMMO has replaced its custom name with a healthbar and the restore has not yet fired.
     *
     * @param entity the entity to check
     * @return true if a snapshot is present
     */
    public static boolean hasHealthbarSnapshot(@NotNull LivingEntity entity) {
        return entity.hasMetadata(MetadataConstants.METADATA_KEY_HEALTHBAR_SNAPSHOT);
    }

    /**
     * Returns the active {@link HealthbarSnapshot} for this entity, or {@code null} if none
     * exists. Prefer {@link #hasHealthbarSnapshot} when only existence needs to be checked.
     *
     * @param entity the entity to query
     * @return the snapshot, or {@code null} if the entity has no active healthbar display
     */
    public static @Nullable HealthbarSnapshot getHealthbarSnapshot(@NotNull LivingEntity entity) {
        final List<MetadataValue> meta =
                entity.getMetadata(MetadataConstants.METADATA_KEY_HEALTHBAR_SNAPSHOT);
        return meta.isEmpty() ? null : (HealthbarSnapshot) meta.get(0).value();
    }

    private static String createHealthDisplay(MobHealthbarType mobHealthbarType,
            LivingEntity entity, double damage) {
        double maxHealth = entity.getMaxHealth();
        double currentHealth = Math.max(entity.getHealth() - damage, 0);
        double healthPercentage = (currentHealth / maxHealth) * 100.0D;

        int fullDisplay;
        ChatColor color = ChatColor.BLACK;
        String symbol;

        switch (mobHealthbarType) {
            case HEARTS:
                fullDisplay = Math.min((int) (maxHealth / 2), 10);
                color = ChatColor.DARK_RED;
                symbol = "❤";
                break;

            case BAR:
                fullDisplay = 10;

                if (healthPercentage >= 85) {
                    color = ChatColor.DARK_GREEN;
                } else if (healthPercentage >= 70) {
                    color = ChatColor.GREEN;
                } else if (healthPercentage >= 55) {
                    color = ChatColor.GOLD;
                } else if (healthPercentage >= 40) {
                    color = ChatColor.YELLOW;
                } else if (healthPercentage >= 25) {
                    color = ChatColor.RED;
                } else if (healthPercentage >= 0) {
                    color = ChatColor.DARK_RED;
                }

                symbol = "■";
                break;

            default:
                return null;
        }

        int coloredDisplay = (int) Math.max(Math.ceil(fullDisplay * (healthPercentage / 100.0D)),
                0.5);
        int grayDisplay = fullDisplay - coloredDisplay;

        StringBuilder healthbar = new StringBuilder(color + "");

        for (int i = 0; i < coloredDisplay; i++) {
            healthbar.append(symbol);
        }

        healthbar.append(ChatColor.GRAY);

        for (int i = 0; i < grayDisplay; i++) {
            healthbar.append(symbol);
        }

        return healthbar.toString();
    }

    /**
     * Check if a given LivingEntity is a boss.
     *
     * @param livingEntity The {@link LivingEntity} of the livingEntity to check
     * @return true if the livingEntity is a boss, false otherwise
     */
    private static boolean isBoss(LivingEntity livingEntity) {
        switch (livingEntity.getType()) {
            case ENDER_DRAGON:
            case WITHER:
                return true;

            default:
                return false;
        }
    }
}
