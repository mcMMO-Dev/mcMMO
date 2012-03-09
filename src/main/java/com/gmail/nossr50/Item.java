package com.gmail.nossr50;

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
        int chimaeraID = LoadProperties.chimaeraId;
        int itemsUsed = LoadProperties.feathersConsumedByChimaeraWing;
        int amount = is.getAmount();

        if (mcPermissions.getInstance().chimaeraWing(player) && is.getTypeId() == chimaeraID) {
            if (Skills.cooldownOver(player, PP.getRecentlyHurt(), 60) && amount >= itemsUsed) {
                player.setItemInHand(new ItemStack(chimaeraID, amount - itemsUsed));

                for (int blockY = block.getY(); blockY < player.getWorld().getMaxHeight(); blockY++) {
                    if (player.getLocation().getWorld().getBlockAt(block.getX(), blockY, block.getZ()).getType() != Material.AIR) {
                        player.sendMessage(mcLocale.getString("Item.ChimaeraWingFail"));
                        player.teleport(player.getLocation().getWorld().getBlockAt(block.getX(), (blockY - 1), block.getZ()).getLocation());
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
            else if (!Skills.cooldownOver(player, PP.getRecentlyHurt(), 60) && is.getAmount() >= itemsUsed) {
                player.sendMessage(mcLocale.getString("Item.InjuredWait", new Object[] {Skills.calculateTimeLeft(player, PP.getRecentlyHurt(), 60)}));
            }
            else if (is.getTypeId() == LoadProperties.chimaeraId && is.getAmount() <= itemsUsed) {
                player.sendMessage(mcLocale.getString("Item.NeedFeathers"));
            }
        }
    }
}
