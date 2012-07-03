package com.gmail.nossr50.util;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import com.gmail.nossr50.config.Config;
import com.gmail.nossr50.datatypes.PlayerProfile;
import com.gmail.nossr50.locale.LocaleLoader;

public class Item {

    /**
     * Check for item usage.
     *
     * @param player Player whose item usage to check
     */
    public static void itemChecks(Player player) {
        ItemStack inHand = player.getItemInHand();

        if (Config.getInstance().getChimaeraEnabled() && inHand.getTypeId() == Config.getInstance().getChimaeraItemId()) {
            chimaeraWing(player);
        }
    }

    private static void chimaeraWing(Player player) {
        PlayerProfile profile = Users.getProfile(player);
        ItemStack inHand = player.getItemInHand();
        Block block = player.getLocation().getBlock();
        int amount = inHand.getAmount();

        if (Permissions.getInstance().chimaeraWing(player) && inHand.getTypeId() == Config.getInstance().getChimaeraItemId()) {
            if (Skills.cooldownOver(profile.getRecentlyHurt(), 60, player) && amount >= Config.getInstance().getChimaeraCost()) {
                player.setItemInHand(new ItemStack(Config.getInstance().getChimaeraItemId(), amount - Config.getInstance().getChimaeraCost()));

                for (int y = 1; block.getY() + y < player.getWorld().getMaxHeight(); y++) {
                    if (!block.getRelative(0, y, 0).getType().equals(Material.AIR)) {
                        player.sendMessage(LocaleLoader.getString("Item.ChimaeraWing.Fail"));
                        player.teleport(block.getRelative(0, y - 1, 0).getLocation());
                        return;
                    }
                }

                if (player.getBedSpawnLocation() != null && player.getBedSpawnLocation().getBlock().getType().equals(Material.BED_BLOCK)) {
                    player.teleport(player.getBedSpawnLocation());
                }
                else {
                    player.teleport(player.getWorld().getSpawnLocation());
                }

                player.sendMessage(LocaleLoader.getString("Item.ChimaeraWing.Pass"));
            }
            else if (!Skills.cooldownOver(profile.getRecentlyHurt(), 60, player) && amount >= Config.getInstance().getChimaeraCost()) {
                player.sendMessage(LocaleLoader.getString("Item.Injured.Wait", new Object[] {Skills.calculateTimeLeft(profile.getRecentlyHurt(), 60)}));
            }
            else if (amount <= Config.getInstance().getChimaeraCost()) {
                player.sendMessage(LocaleLoader.getString("Skills.NeedMore")+ " " + ChatColor.GRAY + Misc.prettyItemString(Config.getInstance().getChimaeraItemId()));
            }
        }
    }
}
