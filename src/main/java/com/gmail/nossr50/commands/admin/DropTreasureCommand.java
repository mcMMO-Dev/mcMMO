//package com.gmail.nossr50.commands.admin;
//
//import com.gmail.nossr50.config.treasure.FishingTreasureConfig;
//import com.gmail.nossr50.datatypes.player.McMMOPlayer;
//import com.gmail.nossr50.datatypes.treasure.FishingTreasure;
//import com.gmail.nossr50.datatypes.treasure.Rarity;
//import com.gmail.nossr50.mcMMO;
//import com.gmail.nossr50.skills.fishing.FishingManager;
//import com.gmail.nossr50.util.player.UserManager;
//import org.bukkit.Location;
//import org.bukkit.command.Command;
//import org.bukkit.command.CommandExecutor;
//import org.bukkit.command.CommandSender;
//import org.bukkit.entity.Player;
//import org.jetbrains.annotations.NotNull;
//
//public class DropTreasureCommand implements CommandExecutor {
//    @Override
//    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
//        if (sender instanceof Player) {
//            if (!sender.isOp()) {
//                sender.sendMessage("This command is for Operators only");
//                return false;
//            }
//
//            Player player = (Player) sender;
//            Location location = player.getLocation();
//            McMMOPlayer mmoPlayer = UserManager.getPlayer(player);
//
//            if (mmoPlayer == null) {
//                //TODO: Localize
//                player.sendMessage("Your player data is not loaded yet");
//                return false;
//            }
//
//            if (args.length == 0) {
//                mcMMO.p.getLogger().info(player.toString() +" is dropping all mcMMO treasures via admin command at location "+location.toString());
//                for(Rarity rarity : FishingTreasureConfig.getInstance().fishingRewards.keySet()) {
//                    for(FishingTreasure fishingTreasure : FishingTreasureConfig.getInstance().fishingRewards.get(rarity)) {
//                        FishingManager fishingManager = mmoPlayer.getFishingManager();
//                    }
//                }
//                //TODO: impl
//            } else {
//                String targetTreasure = args[1];
//
//                //Drop all treasures matching the name
//                //TODO: impl
//            }
//
//            return true;
//        } else {
//            sender.sendMessage("No console support for this command");
//            return false;
//        }
//    }
//}
