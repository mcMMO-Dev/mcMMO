package com.gmail.nossr50.runnables.items;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import com.gmail.nossr50.datatypes.player.McMMOPlayer;
import com.gmail.nossr50.locale.LocaleLoader;
import com.gmail.nossr50.util.ChimaeraWing;
import com.gmail.nossr50.util.Misc;
import com.gmail.nossr50.util.skills.SkillUtils;

public class ChimaeraWingWarmup extends BukkitRunnable {
    private McMMOPlayer mcMMOPlayer;

    public ChimaeraWingWarmup(McMMOPlayer mcMMOPlayer) {
        this.mcMMOPlayer = mcMMOPlayer;
    }

    @Override
    public void run() {
        checkChimaeraWingTeleport();
    }

    private void checkChimaeraWingTeleport() {
        Player player = mcMMOPlayer.getPlayer();
        Location previousLocation = mcMMOPlayer.getChimaeraCommenceLocation();
        Location newLocation = mcMMOPlayer.getPlayer().getLocation();
        long recentlyHurt = mcMMOPlayer.getRecentlyHurt();

        if (newLocation.distanceSquared(previousLocation) > 1.0) {
            player.sendMessage(ChatColor.RED + "Teleportation canceled!"); //TODO Locale!

            mcMMOPlayer.setChimaeraCommenceLocation(null);
            return;
        }

        if (!SkillUtils.cooldownOver(recentlyHurt * Misc.TIME_CONVERSION_FACTOR, 60, player)) {
            player.sendMessage(LocaleLoader.getString("Item.Injured.Wait", SkillUtils.calculateTimeLeft(recentlyHurt * Misc.TIME_CONVERSION_FACTOR, 60, player)));

            mcMMOPlayer.setChimaeraCommenceLocation(null);
            return;
        }
        ChimaeraWing.chimaeraExecuteTeleport();

        mcMMOPlayer.setChimaeraCommenceLocation(null);
    }
}
