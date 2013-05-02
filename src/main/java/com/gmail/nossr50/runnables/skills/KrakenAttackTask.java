package com.gmail.nossr50.runnables.skills;

import org.bukkit.entity.Player;
import org.bukkit.entity.Squid;
import org.bukkit.scheduler.BukkitRunnable;

public class KrakenAttackTask extends BukkitRunnable {
    private Squid kraken;
    private Player player;

    public KrakenAttackTask(Squid kraken, Player player) {
        this.kraken = kraken;
        this.player = player;
    }

    @Override
    public void run() {
        if (!player.isDead()) {
            kraken.teleport(player);
            player.damage(1, kraken);
            player.getWorld().strikeLightningEffect(player.getLocation());
        }
        else {
            kraken.remove();
            this.cancel();
        }
    }
}
