package com.gmail.nossr50.mcmmo.bukkit.platform.util;

import com.gmail.nossr50.core.MetadataConstants;
import com.gmail.nossr50.datatypes.MobHealthbarType;
import com.gmail.nossr50.datatypes.meta.OldName;
import com.gmail.nossr50.mcMMO;
import com.gmail.nossr50.mcmmo.api.data.MMOEntity;
import com.gmail.nossr50.mcmmo.api.data.MMOPlayer;
import com.gmail.nossr50.mcmmo.api.platform.util.MobHealthBarManager;
import com.gmail.nossr50.runnables.MobHealthDisplayUpdaterTask;
import com.gmail.nossr50.util.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginManager;

public final class BukkitMobHealthBarManager implements MobHealthBarManager<Player, LivingEntity> {
    private final Plugin plugin;
    private final mcMMO pluginRef;
    private final boolean healthBarPluginEnabled;

    public BukkitMobHealthBarManager(Plugin plugin, mcMMO pluginRef) {
        this.plugin = plugin;
        this.pluginRef = pluginRef;
        PluginManager pluginManager = Bukkit.getServer().getPluginManager();
        healthBarPluginEnabled = pluginManager.getPlugin("HealthBar") != null;

        if (healthBarPluginEnabled) {
            pluginRef.getLogger().info("HealthBar plugin found, mcMMO's healthbars are automatically disabled.");
        }

    }

    /**
     * Fix issues with death messages caused by the mob healthbars.
     *
     * @param deathMessage The original death message
     * @param player       The player who died
     * @return the fixed death message
     */
    @Override
    public String fixDeathMessage(String deathMessage, MMOPlayer<Player> player) {
        EntityDamageEvent lastDamageCause = player.getNative().getLastDamageCause();
        String replaceString = lastDamageCause instanceof EntityDamageByEntityEvent ? StringUtils.getPrettyEntityTypeString(((EntityDamageByEntityEvent) lastDamageCause).getDamager().getType()) : "a mob";

        return deathMessage.replaceAll("(?:\u00A7(?:[0-9A-FK-ORa-fk-or]){1}(?:[\u2764\u25A0]{1,10})){1,2}", replaceString);
    }

    /**
     * Handle the creation of mob healthbars.
     *
     * @param mmoTarget the targetted entity
     * @param damage damage done by the attack triggering this
     */
    @Override
    public void handleMobHealthbars(MMOEntity<LivingEntity> mmoTarget, double damage) {
        if (healthBarPluginEnabled || !pluginRef.getConfigManager().getConfigMobs().getCombat().getHealthBars().isEnableHealthBars()) {
            return;
        }
        LivingEntity target = mmoTarget.getNative();

        if (isBoss(target)) {
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
        if (target.getMetadata("mcMMO_oldName").size() <= 0 && originalName != null)
            target.setMetadata("mcMMO_oldName", new OldName(originalName, pluginRef));

        if (oldName == null) {
            oldName = "";
        }


        boolean oldNameVisible = target.isCustomNameVisible();
        String newName = createHealthDisplay(pluginRef.getConfigManager().getConfigMobs().getCombat().getHealthBars().getDisplayBarType(), target, damage);

        target.setCustomName(newName);
        target.setCustomNameVisible(true);

        int displayTime = Math.max(pluginRef.getConfigManager().getConfigMobs().getCombat().getHealthBars().getDisplayTimeSeconds(), 1);

        if (displayTime != -1) {
            boolean updateName = !ChatColor.stripColor(oldName).equalsIgnoreCase(ChatColor.stripColor(newName));

            if (updateName) {
                target.setMetadata(MetadataConstants.CUSTOM_NAME_METAKEY.getKey(), new FixedMetadataValue(plugin, oldName));
                target.setMetadata(MetadataConstants.NAME_VISIBILITY_METAKEY.getKey(), new FixedMetadataValue(plugin, oldNameVisible));
            } else if (!target.hasMetadata(MetadataConstants.CUSTOM_NAME_METAKEY.getKey())) {
                target.setMetadata(MetadataConstants.CUSTOM_NAME_METAKEY.getKey(), new FixedMetadataValue(plugin, ""));
                target.setMetadata(MetadataConstants.NAME_VISIBILITY_METAKEY.getKey(), new FixedMetadataValue(plugin, false));
            }

            pluginRef.getPlatformProvider().getScheduler().getTaskBuilder()
                    .setDelay(displayTime * pluginRef.getMiscTools().TICK_CONVERSION_FACTOR) // Clear health display after 3 seconds
                    .setTask(new MobHealthDisplayUpdaterTask(pluginRef, target))
                    .schedule();
        }
    }

    private String createHealthDisplay(MobHealthbarType mobHealthbarType, LivingEntity entity, double damage) {
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

        int coloredDisplay = (int) Math.ceil(fullDisplay * (healthPercentage / 100.0D));
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
    private boolean isBoss(LivingEntity livingEntity) {
        switch (livingEntity.getType()) {
            case ENDER_DRAGON:
            case WITHER:
                return true;

            default:
                return false;
        }
    }
}
