package com.gmail.nossr50.util;

import com.gmail.nossr50.datatypes.MobHealthbarType;
import com.gmail.nossr50.datatypes.meta.OldName;
import com.gmail.nossr50.mcMMO;
import com.gmail.nossr50.runnables.MobHealthDisplayUpdaterTask;
import com.gmail.nossr50.util.text.StringUtils;
import org.bukkit.ChatColor;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.metadata.FixedMetadataValue;

public final class MobHealthbarUtils {
    private MobHealthbarUtils() {}

    /**
     * Fix issues with death messages caused by the mob healthbars.
     *
     * @param deathMessage The original death message
     * @param player The player who died
     * @return the fixed death message
     */
    public static String fixDeathMessage(String deathMessage, Player player) {
        EntityDamageEvent lastDamageCause = player.getLastDamageCause();
        String replaceString = lastDamageCause instanceof EntityDamageByEntityEvent ? StringUtils.getPrettyEntityTypeString(((EntityDamageByEntityEvent) lastDamageCause).getDamager().getType()) : "a mob";

        return deathMessage.replaceAll("(?:(§(?:[0-9A-FK-ORa-fk-or]))*(?:[❤■]{1,10})){1,2}", replaceString);
    }

    /**
     * Handle the creation of mob healthbars.
     *  @param target the targetted entity
     * @param damage damage done by the attack triggering this
     */
    public static void handleMobHealthbars(LivingEntity target, double damage, mcMMO plugin) {
        if (mcMMO.isHealthBarPluginEnabled() || !mcMMO.p.getGeneralConfig().getMobHealthbarEnabled()) {
            return;
        }

        if (isBoss(target) || target instanceof Player) {
            return;
        }

        // Don't mangle invalid entities, they're not going to be rendered anyways
        if (!target.isValid()) {
            return;
        }

        String originalName = target.getName();
        String oldName = target.getCustomName();

        /*
         * Store the name in metadata
         */
        if (target.getMetadata(MetadataConstants.METADATA_KEY_OLD_NAME_KEY).size() <= 0)
            target.setMetadata(MetadataConstants.METADATA_KEY_OLD_NAME_KEY, new OldName(originalName, plugin));

        if (oldName == null) {
            oldName = "";
        }

        boolean oldNameVisible = target.isCustomNameVisible();
        String newName = createHealthDisplay(mcMMO.p.getGeneralConfig().getMobHealthbarDefault(), target, damage);

        target.setCustomName(newName);
        target.setCustomNameVisible(true);

        int displayTime = mcMMO.p.getGeneralConfig().getMobHealthbarTime();

        if (displayTime != -1) {
            boolean updateName = !ChatColor.stripColor(oldName).equalsIgnoreCase(ChatColor.stripColor(newName));

            if (updateName) {
                target.setMetadata(MetadataConstants.METADATA_KEY_CUSTOM_NAME, new FixedMetadataValue(mcMMO.p, oldName));
                target.setMetadata(MetadataConstants.METADATA_KEY_NAME_VISIBILITY, new FixedMetadataValue(mcMMO.p, oldNameVisible));
            } else if (!target.hasMetadata(MetadataConstants.METADATA_KEY_CUSTOM_NAME)) {
                target.setMetadata(MetadataConstants.METADATA_KEY_CUSTOM_NAME, new FixedMetadataValue(mcMMO.p, ""));
                target.setMetadata(MetadataConstants.METADATA_KEY_NAME_VISIBILITY, new FixedMetadataValue(mcMMO.p, false));
            }

            mcMMO.p.getFoliaLib().getScheduler().runAtEntityLater(target, new MobHealthDisplayUpdaterTask(target), (long) displayTime * Misc.TICK_CONVERSION_FACTOR); // Clear health display after 3 seconds
        }
    }

    private static String createHealthDisplay(MobHealthbarType mobHealthbarType, LivingEntity entity, double damage) {
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

        int coloredDisplay = (int) Math.max(Math.ceil(fullDisplay * (healthPercentage / 100.0D)), 0.5);
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
