//package com.gmail.nossr50.listeners;
//
//import com.gmail.nossr50.datatypes.player.McMMOPlayer;
//import com.gmail.nossr50.datatypes.skills.SuperAbilityType;
//import com.gmail.nossr50.mcMMO;
//import com.gmail.nossr50.util.player.UserManager;
//import com.gmail.nossr50.util.skills.SkillUtils;
//import org.bukkit.Bukkit;
//import org.bukkit.entity.Player;
//import org.bukkit.event.EventHandler;
//import org.bukkit.event.EventPriority;
//import org.bukkit.event.Listener;
//import org.bukkit.event.player.PlayerCommandPreprocessEvent;
//
//public class CommandListener implements Listener {
//
//    private final mcMMO pluginRef;
//
//    public CommandListener(mcMMO plugin) {
//        this.pluginRef = plugin;
//    }
//
//    @EventHandler(ignoreCancelled = true, priority = EventPriority.LOWEST)
//    public void onPlayerCommandPreprocess(PlayerCommandPreprocessEvent event) {
//        Player player = event.getPlayer();
//
//        SkillUtils.removeAbilityBoostsFromInventory(player);
//
//        McMMOPlayer mmoPlayer = UserManager.getPlayer(player);
//
//        if(mmoPlayer == null)
//            return;
//
//        Bukkit.getServer().getScheduler().runTaskLater(pluginRef, () -> {
//            if(mmoPlayer.getAbilityMode(SuperAbilityType.GIGA_DRILL_BREAKER) || mmoPlayer.getAbilityMode(SuperAbilityType.SUPER_BREAKER)) {
//                SkillUtils.handleAbilitySpeedIncrease(player);
//            }
//        }, 5);
//    }
//}
