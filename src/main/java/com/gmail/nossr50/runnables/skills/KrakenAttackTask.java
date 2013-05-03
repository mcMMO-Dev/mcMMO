package com.gmail.nossr50.runnables.skills;

import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.entity.Squid;
import org.bukkit.scheduler.BukkitRunnable;

import com.gmail.nossr50.config.AdvancedConfig;
import com.gmail.nossr50.util.Misc;

public class KrakenAttackTask extends BukkitRunnable {
    private Squid kraken;
    private Player player;
    private final boolean GLOBAL_SOUNDS = AdvancedConfig.getInstance().getKrakenGlobalSoundsEnabled();

    public KrakenAttackTask(Squid kraken, Player player) {
        this.kraken = kraken;
        this.player = player;
    }

    @Override
    public void run() {
        if (!kraken.isValid()) {
            player.resetPlayerWeather();
            this.cancel();
        }

        if (player.isValid()) {
            Location location = player.getLocation();
            World world = player.getWorld();

            kraken.teleport(player);
            player.damage(AdvancedConfig.getInstance().getKrakenAttackDamage(), kraken);

            if (GLOBAL_SOUNDS) {
                world.playSound(location, Sound.GHAST_SCREAM, Misc.GHAST_VOLUME, Misc.getGhastPitch());
            }
            else {
                player.playSound(location, Sound.GHAST_SCREAM, Misc.GHAST_VOLUME, Misc.getGhastPitch());
            }

            world.playSound(location, Sound.GHAST_SCREAM, Misc.GHAST_VOLUME, Misc.getGhastPitch());
            world.strikeLightningEffect(location);
        }
        else {
            kraken.remove();
            this.cancel();
        }
    }
}
