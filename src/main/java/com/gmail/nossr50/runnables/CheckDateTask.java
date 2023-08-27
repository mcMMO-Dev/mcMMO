//package com.gmail.nossr50.runnables;
//
//import com.gmail.nossr50.mcMMO;
//import com.gmail.nossr50.runnables.skills.AprilTask;
//import com.gmail.nossr50.util.Misc;
//import com.gmail.nossr50.util.CancellableRunnable;
//
//public class CheckDateTask extends CancellableRunnable {
//
//    @Override
//    public void run() {
//        if (!mcMMO.getHolidayManager().isAprilFirst()) {
//            return;
//        }
//
//        // Set up jokes
//        new AprilTask().runTaskTimer(mcMMO.p, 60L * Misc.TICK_CONVERSION_FACTOR, 10L * 60L * Misc.TICK_CONVERSION_FACTOR);
//        mcMMO.getHolidayManager().registerAprilCommand();
//
//        // Jokes deployed.
//        this.cancel();
//    }
//}
