package com.gmail.nossr50;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import com.gmail.nossr50.locale.mcLocale;
import com.gmail.nossr50.skills.Skills;
import com.gmail.nossr50.config.LoadProperties;
import com.gmail.nossr50.datatypes.PlayerProfile;

public class Item {

    /**
     * Check for item usage.
     *
     * @param player Player whose item usage to check
     */
    public static void itemchecks(Player player) {
        ItemStack inhand = player.getItemInHand();

        if (LoadProperties.chimaeraWingEnable && inhand.getTypeId() == LoadProperties.chimaeraId) {
            chimaerawing(player);
        }
    }

    private static void chimaerawing(Player player) {
        PlayerProfile PP = Users.getProfile(player);
        ItemStack is = player.getItemInHand();
        Block block = player.getLocation().getBlock();
        int amount = is.getAmount();

        if (mcPermissions.getInstance().chimaeraWing(player) && is.getTypeId() == LoadProperties.chimaeraId) {
            if (Skills.cooldownOver(PP.getRecentlyHurt(), 60) && amount >= LoadProperties.feathersConsumedByChimaeraWing) {
                player.setItemInHand(new ItemStack(LoadProperties.chimaeraId, amount - LoadProperties.feathersConsumedByChimaeraWing));

                for (int y = 0; block.getY() + y < player.getWorld().getMaxHeight(); y++) {
                    if (!block.getRelative(0, y, 0).getType().equals(Material.AIR)) {
                        player.sendMessage(mcLocale.getString("Item.ChimaeraWingFail"));
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

                player.sendMessage(mcLocale.getString("Item.ChimaeraWingPass"));
            }
            else if (!Skills.cooldownOver(PP.getRecentlyHurt(), 60) && is.getAmount() >= LoadProperties.feathersConsumedByChimaeraWing) {
                player.sendMessage(mcLocale.getString("Item.InjuredWait", new Object[] {Skills.calculateTimeLeft(PP.getRecentlyHurt(), 60)}));
            }
            else if (is.getAmount() <= LoadProperties.feathersConsumedByChimaeraWing) {
                player.sendMessage(mcLocale.getString("Skills.NeedMore")+ " " + ChatColor.GRAY + m.prettyItemString(LoadProperties.chimaeraId));
            }
        }
    }
}
