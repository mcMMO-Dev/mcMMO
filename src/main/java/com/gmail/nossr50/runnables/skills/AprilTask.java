//package com.gmail.nossr50.runnables.skills;
//
//import com.gmail.nossr50.mcMMO;
//import com.gmail.nossr50.util.HolidayManager;
//import com.gmail.nossr50.util.Misc;
//import com.gmail.nossr50.util.sounds.SoundManager;
//import com.gmail.nossr50.util.sounds.SoundType;
//import org.bukkit.ChatColor;
//import org.bukkit.Statistic;
//import org.bukkit.entity.Player;
//import com.gmail.nossr50.util.CancellableRunnable;
//
//public class AprilTask extends CancellableRunnable {
//
//    @Override
//    public void run() {
//        if (!mcMMO.getHolidayManager().isAprilFirst()) {
//            this.cancel();
//            return;
//        }
//
//        for (Player player : mcMMO.p.getServer().getOnlinePlayers()) {
//            int random = Misc.getRandom().nextInt(40) + 11;
//            int betterRandom = Misc.getRandom().nextInt(2000);
//            if (betterRandom == 0) {
//                SoundManager.sendSound(player, player.getLocation(), SoundType.LEVEL_UP);
//                player.sendMessage(unknown("superskill") + " skill increased by 1. Total (" + unknown("12") + ")");
/// /                fireworksShow(player);
//            }
//
//            for (Statistic statistic : mcMMO.getHolidayManager().movementStatistics) {
//                if (player.getStatistic(statistic) > 0 && player.getStatistic(statistic) % random == 0) {
//                    mcMMO.getHolidayManager().levelUpApril(player, HolidayManager.FakeSkillType.getByStatistic(statistic));
//                    break;
//                }
//            }
//        }
//    }
//
//    /*private void fireworksShow(final Player player) {
//        final int firework_amount = 10;
//        for (int i = 0; i < firework_amount; i++) {
//            int delay = (int) (Misc.getRandom().nextDouble() * 3 * Misc.TICK_CONVERSION_FACTOR) + 4;
//            mcMMO.p.getServer().getScheduler().runTaskLater(mcMMO.p, new Runnable() {
//                @Override
//                public void run() {
//                    mcMMO.getHolidayManager().spawnFireworks(player);
//                }
//            }, delay);
//        }
//    }*/
//
//    private String unknown(String string) {
//        return ChatColor.MAGIC + string + ChatColor.RESET + ChatColor.YELLOW;
//    }
//}
