package com.gmail.nossr50.util;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ShapelessRecipe;
import org.bukkit.inventory.meta.ItemMeta;

import com.gmail.nossr50.config.Config;
import com.gmail.nossr50.locale.LocaleLoader;
import com.gmail.nossr50.metrics.MetricsManager;
import com.gmail.nossr50.util.player.UserManager;
import com.gmail.nossr50.util.skills.SkillUtils;

public final class ChimaeraWing {
    private ChimaeraWing() {}

    /**
     * Check for item usage.
     *
     * @param player Player whose item usage to check
     */
    public static void activationCheck(Player player) {
        ItemStack inHand = player.getItemInHand();

        if (!Config.getInstance().getChimaeraEnabled() || !ItemUtils.isChimaeraWing(inHand)) {
            return;
        }

        Block block = player.getLocation().getBlock();
        int amount = inHand.getAmount();
        long recentlyHurt = UserManager.getPlayer(player).getRecentlyHurt();
        long lastChimaeraWing = (UserManager.getPlayer(player).getLastChimaeraTeleport());

        if (Permissions.chimaeraWing(player) && ItemUtils.isChimaeraWing(inHand)) {
            if (!SkillUtils.cooldownOver(lastChimaeraWing * Misc.TIME_CONVERSION_FACTOR, Config.getInstance().getChimaeraCooldown(), player)) {
                player.sendMessage(ChatColor.RED + "You need to wait before you can use this again! " + ChatColor.YELLOW + "(" + SkillUtils.calculateTimeLeft(lastChimaeraWing * Misc.TIME_CONVERSION_FACTOR, Config.getInstance().getChimaeraCooldown(), player) + ")"); //TODO Locale!
                return;
            }

            if (SkillUtils.cooldownOver(recentlyHurt * Misc.TIME_CONVERSION_FACTOR, 60, player) && amount >= Config.getInstance().getChimaeraUseCost()) {
                player.setItemInHand(new ItemStack(getChimaeraWing(amount - Config.getInstance().getChimaeraUseCost())));

                if (Config.getInstance().getChimaeraPreventUseUnderground()) {
                    for (int y = 1; block.getY() + y < player.getWorld().getMaxHeight(); y++) {
                        if (!(block.getRelative(0, y, 0).getType() == Material.AIR)) {
                            player.sendMessage(LocaleLoader.getString("Item.ChimaeraWing.Fail"));
                            player.teleport(block.getRelative(0, y - 1, 0).getLocation());
                            return;
                        }
                    }
                }

                if (player.getBedSpawnLocation() != null) {
                    player.teleport(player.getBedSpawnLocation());
                }
                else {
                    player.teleport(player.getWorld().getSpawnLocation());
                }

                UserManager.getPlayer(player).actualizeLastChimaeraTeleport();
                MetricsManager.chimeraWingUsed();
                player.sendMessage(LocaleLoader.getString("Item.ChimaeraWing.Pass"));
            }
            else if (!SkillUtils.cooldownOver(recentlyHurt, 60 * Misc.TIME_CONVERSION_FACTOR, player) && amount >= Config.getInstance().getChimaeraUseCost()) {
                player.sendMessage(LocaleLoader.getString("Item.Injured.Wait", SkillUtils.calculateTimeLeft(recentlyHurt, 60, player)));
            }
            else if (amount <= Config.getInstance().getChimaeraUseCost()) {
                player.sendMessage(LocaleLoader.getString("Skills.NeedMore", "Chimaera Wings")); //TODO Locale!
            }
        }
    }

    public static ItemStack getChimaeraWing(int amount) {
        ItemStack itemStack = new ItemStack(Material.FEATHER, amount);
        ItemMeta itemMeta = itemStack.getItemMeta();
        itemMeta.setDisplayName(ChatColor.GOLD + "Chimaera Wing"); //TODO Locale!
        List<String> itemLore = new ArrayList<String>();
        itemLore.add("mcMMO Item");
        itemLore.add(ChatColor.GRAY + "Teleports you to your bed."); //TODO Locale!
        itemMeta.setLore(itemLore);
        itemStack.setItemMeta(itemMeta);
        return itemStack;
    }

    public static ShapelessRecipe getChimaeraWingRecipe() {
        Material ingredient = Material.getMaterial(Config.getInstance().getChimaeraItemId());
        int amount = Config.getInstance().getChimaeraRecipeCost();
        if (amount > 9) {
            amount = 9;
        }

        ShapelessRecipe ChimaeraWing = new ShapelessRecipe(getChimaeraWing(1));
        ChimaeraWing.addIngredient(amount, ingredient);
        return ChimaeraWing;
    }
}
