package com.gmail.nossr50.runnables.skills;

import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.entity.Creature;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import com.gmail.nossr50.config.AdvancedConfig;
import com.gmail.nossr50.util.Misc;

public class KrakenAttackTask extends BukkitRunnable {
    private Creature kraken;
    private Player player;
    private Location location;
    private final boolean GLOBAL_SOUNDS = AdvancedConfig.getInstance().getKrakenGlobalSoundsEnabled();

    public KrakenAttackTask(Creature kraken, Player player) {
        this.kraken = kraken;
        this.player = player;
    }

    public KrakenAttackTask(Creature kraken, Player player, Location location) {
        this.kraken = kraken;
        this.player = player;
        this.location = location;
    }

    @Override
    public void run() {
        if (location != null) {
            Location playerLocation = player.getLocation();

            if (player.isValid() && playerLocation.getBlock().isLiquid()) {
                World world = player.getWorld();

                player.damage(AdvancedConfig.getInstance().getKrakenAttackDamage(), kraken);

                if (GLOBAL_SOUNDS) {
                    world.playSound(playerLocation, Sound.GHAST_SCREAM, Misc.GHAST_VOLUME, Misc.getGhastPitch());
                }
                else {
                    player.playSound(playerLocation, Sound.GHAST_SCREAM, Misc.GHAST_VOLUME, Misc.getGhastPitch());
                }

                world.strikeLightningEffect(playerLocation);
            }
            else {
                player.sendMessage(AdvancedConfig.getInstance().getPlayerEscapeMessage());
                player.resetPlayerWeather();
                cancel();
            }

            return;
        }

        if (!kraken.isValid()) {
            player.sendMessage(AdvancedConfig.getInstance().getPlayerDefeatMessage());
            player.resetPlayerWeather();
            cancel();
        }

        if (player.isValid()) {
            Location location = player.getLocation();

            if (!location.getBlock().isLiquid() && AdvancedConfig.getInstance().getKrakenEscapeAllowed()) {
                player.sendMessage(AdvancedConfig.getInstance().getPlayerEscapeMessage());
                kraken.remove();
                player.resetPlayerWeather();
                cancel();
                return;
            }

            World world = player.getWorld();

            kraken.teleport(player);
            player.damage(AdvancedConfig.getInstance().getKrakenAttackDamage(), kraken);

            if (GLOBAL_SOUNDS) {
                world.playSound(location, Sound.GHAST_SCREAM, Misc.GHAST_VOLUME, Misc.getGhastPitch());
            }
            else {
                player.playSound(location, Sound.GHAST_SCREAM, Misc.GHAST_VOLUME, Misc.getGhastPitch());
            }

            world.strikeLightningEffect(location);
        }
        else {
            kraken.remove();
            cancel();
        }
    }
}
