package com.gmail.nossr50.runnables.skills;

import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import com.gmail.nossr50.config.AdvancedConfig;
import com.gmail.nossr50.util.Misc;

public class KrakenAttackTask extends BukkitRunnable {
    private LivingEntity kraken;
    private Player player;
    private Location location;
    private final boolean GLOBAL_EFFECTS = AdvancedConfig.getInstance().getKrakenGlobalEffectsEnabled();
    private final String DEFEAT_MESSAGE = AdvancedConfig.getInstance().getPlayerDefeatMessage();
    private final String ESCAPE_MESSAGE = AdvancedConfig.getInstance().getPlayerEscapeMessage();

    public KrakenAttackTask(LivingEntity kraken2, Player player) {
        this.kraken = kraken2;
        this.player = player;
    }

    public KrakenAttackTask(LivingEntity kraken2, Player player, Location location) {
        this.kraken = kraken2;
        this.player = player;
        this.location = location;
    }

    @Override
    public void run() {
        if (location != null) {
            Location playerLocation = player.getLocation();

            if (player.isValid() && playerLocation.getBlock().isLiquid()) {
                World world = player.getWorld();

                krakenAttack(playerLocation, world);
            }
            else {
                player.sendMessage(AdvancedConfig.getInstance().getPlayerEscapeMessage());
                player.resetPlayerWeather();
                cancel();
            }

            return;
        }

        if (!kraken.isValid()) {
            if (!DEFEAT_MESSAGE.isEmpty()) {
                player.sendMessage(DEFEAT_MESSAGE);
            }

            player.resetPlayerWeather();
            cancel();
        }

        if (player.isValid()) {
            Location location = player.getLocation();

            if (!location.getBlock().isLiquid() && AdvancedConfig.getInstance().getKrakenEscapeAllowed()) {
                if (!ESCAPE_MESSAGE.isEmpty()) {
                    player.sendMessage(AdvancedConfig.getInstance().getPlayerEscapeMessage());
                }

                kraken.remove();
                player.resetPlayerWeather();
                cancel();
                return;
            }

            World world = player.getWorld();

            kraken.teleport(player);
            krakenAttack(location, world);
        }
        else {
            kraken.remove();
            cancel();
        }
    }

    private void krakenAttack(Location playerLocation, World world) {
        player.damage(AdvancedConfig.getInstance().getKrakenAttackDamage(), kraken);

        if (GLOBAL_EFFECTS) {
            world.playSound(playerLocation, Sound.GHAST_SCREAM, Misc.GHAST_VOLUME, Misc.getGhastPitch());
            world.strikeLightningEffect(playerLocation);
        }
        else {
            player.playSound(playerLocation, Sound.GHAST_SCREAM, Misc.GHAST_VOLUME, Misc.getGhastPitch());
            world.createExplosion(playerLocation.getX(), playerLocation.getY(), playerLocation.getZ(), 0F, false, false);
        }
    }
}
