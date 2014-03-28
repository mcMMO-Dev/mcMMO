package com.gmail.nossr50.runnables.skills;

import org.bukkit.ChatColor;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import com.gmail.nossr50.mcMMO;
import com.gmail.nossr50.util.HolidayManager;
import com.gmail.nossr50.util.Misc;

public class AprilTask extends BukkitRunnable {

    @Override
    public void run() {
        if (!mcMMO.getHolidayManager().isAprilFirst()) {
            this.cancel();
            return;
        }

        for (Player player : mcMMO.p.getServer().getOnlinePlayers()) {
            int betterRandom = Misc.getRandom().nextInt(2000);
            if (betterRandom == 0) {
                player.playSound(player.getLocation(), Sound.LEVEL_UP, Misc.LEVELUP_VOLUME, Misc.LEVELUP_PITCH);
                player.sendMessage(unknown("superskill") + " skill increased by 1. Total (" + unknown("12") + ")");
                fireworksShow(player);
            }

            for (HolidayManager.FakeSkillType fakeSkillType : HolidayManager.FakeSkillType.values()) {
                int random = Misc.getRandom().nextInt(250);
                if (random == 0) {
                    mcMMO.getHolidayManager().levelUpApril(player, fakeSkillType);
                    break;
                }
            }
        }
    }

    private void fireworksShow(final Player player) {
        final int firework_amount = 10;
        for (int i = 0; i < firework_amount; i++) {
            int delay = (int) (Misc.getRandom().nextDouble() * 3 * Misc.TICK_CONVERSION_FACTOR) + 4;
            mcMMO.p.getServer().getScheduler().runTaskLater(mcMMO.p, new Runnable() {
                @Override
                public void run() {
                    mcMMO.getHolidayManager().spawnFireworks(player);
                }
            }, delay);
        }
    }

    private String unknown(String string) {
        return ChatColor.MAGIC + string + ChatColor.RESET + ChatColor.YELLOW;
    }
}
