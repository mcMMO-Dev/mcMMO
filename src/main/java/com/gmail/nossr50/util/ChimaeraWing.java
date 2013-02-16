package com.gmail.nossr50.util;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import com.gmail.nossr50.config.Config;
import com.gmail.nossr50.datatypes.PlayerProfile;
import com.gmail.nossr50.locale.LocaleLoader;
import com.gmail.nossr50.skills.utilities.SkillTools;
import com.gmail.nossr50.util.metrics.MetricsManager;

public final class ChimaeraWing {
    private ChimaeraWing() {}

    /**
     * Check for item usage.
     *
     * @param player Player whose item usage to check
     */
    public static void activationCheck(Player player) {
        ItemStack inHand = player.getItemInHand();

        if (!Config.getInstance().getChimaeraEnabled() || inHand.getTypeId() != Config.getInstance().getChimaeraItemId()) {
            return;
        }

        PlayerProfile profile = Users.getPlayer(player).getProfile();
        Block block = player.getLocation().getBlock();
        int amount = inHand.getAmount();
        long recentlyHurt = profile.getRecentlyHurt();

        if (Permissions.chimaeraWing(player) && inHand.getTypeId() == Config.getInstance().getChimaeraItemId()) {
            if (SkillTools.cooldownOver(recentlyHurt, 60, player) && amount >= Config.getInstance().getChimaeraCost()) {
                player.setItemInHand(new ItemStack(Config.getInstance().getChimaeraItemId(), amount - Config.getInstance().getChimaeraCost()));

                for (int y = 1; block.getY() + y < player.getWorld().getMaxHeight(); y++) {
                    if (!(block.getRelative(0, y, 0).getType() == Material.AIR)) {
                        player.sendMessage(LocaleLoader.getString("Item.ChimaeraWing.Fail"));
                        player.teleport(block.getRelative(0, y - 1, 0).getLocation());
                        return;
                    }
                }

                if (player.getBedSpawnLocation() != null && player.getBedSpawnLocation().getBlock().getType() == Material.BED_BLOCK) {
                    player.teleport(player.getBedSpawnLocation());
                }
                else {
                    player.teleport(player.getWorld().getSpawnLocation());
                }

                MetricsManager.chimeraWingUsed();
                player.sendMessage(LocaleLoader.getString("Item.ChimaeraWing.Pass"));
            }
            else if (!SkillTools.cooldownOver(recentlyHurt, 60, player) && amount >= Config.getInstance().getChimaeraCost()) {
                player.sendMessage(LocaleLoader.getString("Item.Injured.Wait", SkillTools.calculateTimeLeft(recentlyHurt, 60, player)));
            }
            else if (amount <= Config.getInstance().getChimaeraCost()) {
                player.sendMessage(LocaleLoader.getString("Skills.NeedMore", StringUtils.getPrettyItemString(Config.getInstance().getChimaeraItemId())));
            }
        }
    }
}
