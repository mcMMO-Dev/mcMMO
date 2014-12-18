package com.gmail.nossr50.util;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ShapelessRecipe;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.util.Vector;

import com.gmail.nossr50.mcMMO;
import com.gmail.nossr50.config.Config;
import com.gmail.nossr50.datatypes.player.McMMOPlayer;
import com.gmail.nossr50.locale.LocaleLoader;
import com.gmail.nossr50.metrics.MetricsManager;
import com.gmail.nossr50.runnables.items.ChimaeraWingWarmup;
import com.gmail.nossr50.util.player.UserManager;
import com.gmail.nossr50.util.skills.CombatUtils;
import com.gmail.nossr50.util.skills.SkillUtils;

public final class ChimaeraWing {
    private static McMMOPlayer mcMMOPlayer;
    private static Location location;

    private ChimaeraWing() {}

    /**
     * Check for item usage.
     *
     * @param player Player whose item usage to check
     */
    public static void activationCheck(Player player) {
        if (!Config.getInstance().getChimaeraEnabled()) {
            return;
        }

        ItemStack inHand = player.getItemInHand();

        if (!ItemUtils.isChimaeraWing(inHand)) {
            return;
        }

        if (!Permissions.chimaeraWing(player)) {
            player.sendMessage(LocaleLoader.getString("mcMMO.NoPermission"));
            return;
        }

        mcMMOPlayer = UserManager.getPlayer(player);

        if (mcMMOPlayer.getTeleportCommenceLocation() != null) {
            return;
        }

        int amount = inHand.getAmount();

        if (amount < Config.getInstance().getChimaeraUseCost()) {
            player.sendMessage(LocaleLoader.getString("Skills.NeedMore", LocaleLoader.getString("Item.ChimaeraWing.Name")));
            return;
        }

        long lastTeleport = mcMMOPlayer.getChimeraWingLastUse();
        int cooldown = Config.getInstance().getChimaeraCooldown();

        if (cooldown > 0) {
            int timeRemaining = SkillUtils.calculateTimeLeft(lastTeleport * Misc.TIME_CONVERSION_FACTOR, cooldown, player);

            if (timeRemaining > 0) {
                player.sendMessage(LocaleLoader.getString("Item.Generic.Wait", timeRemaining));
                return;
            }
        }

        long recentlyHurt = mcMMOPlayer.getRecentlyHurt();
        int hurtCooldown = Config.getInstance().getChimaeraRecentlyHurtCooldown();

        if (hurtCooldown > 0) {
            int timeRemaining = SkillUtils.calculateTimeLeft(recentlyHurt * Misc.TIME_CONVERSION_FACTOR, hurtCooldown, player);

            if (timeRemaining > 0) {
                player.sendMessage(LocaleLoader.getString("Item.Injured.Wait", timeRemaining));
                return;
            }
        }

        location = player.getLocation();

        if (Config.getInstance().getChimaeraPreventUseUnderground()) {
            if (location.getY() < player.getWorld().getHighestBlockYAt(location)) {
                player.setItemInHand(new ItemStack(getChimaeraWing(amount - Config.getInstance().getChimaeraUseCost())));
                player.sendMessage(LocaleLoader.getString("Item.ChimaeraWing.Fail"));
                player.updateInventory();
                player.setVelocity(new Vector(0, 0.5D, 0));
                CombatUtils.dealDamage(player, Misc.getRandom().nextInt((int) (player.getHealth() - 10)));
                mcMMOPlayer.actualizeChimeraWingLastUse();
                return;
            }
        }

        mcMMOPlayer.actualizeTeleportCommenceLocation(player);

        long warmup = Config.getInstance().getChimaeraWarmup();

        if (warmup > 0) {
            player.sendMessage(LocaleLoader.getString("Teleport.Commencing", warmup));
            new ChimaeraWingWarmup(mcMMOPlayer).runTaskLater(mcMMO.p, 20 * warmup);
        }
        else {
            chimaeraExecuteTeleport();
        }
    }

    public static void chimaeraExecuteTeleport() {
        Player player = mcMMOPlayer.getPlayer();

        if (Config.getInstance().getChimaeraUseBedSpawn() && player.getBedSpawnLocation() != null) {
            player.teleport(player.getBedSpawnLocation());
        }
        else {
            Location spawnLocation = player.getWorld().getSpawnLocation();
            if (spawnLocation.getBlock().getType() == Material.AIR) {
                player.teleport(spawnLocation);
            }
            else {
                player.teleport(player.getWorld().getHighestBlockAt(spawnLocation).getLocation());
            }
        }

        player.setItemInHand(new ItemStack(getChimaeraWing(player.getItemInHand().getAmount() - Config.getInstance().getChimaeraUseCost())));
        player.updateInventory();
        mcMMOPlayer.actualizeChimeraWingLastUse();
        mcMMOPlayer.setTeleportCommenceLocation(null);

        if (Config.getInstance().getStatsTrackingEnabled()) {
            MetricsManager.chimeraWingUsed();
        }

        if (Config.getInstance().getChimaeraSoundEnabled()) {
            player.playSound(location, Sound.BAT_TAKEOFF, Misc.BAT_VOLUME, Misc.BAT_PITCH);
        }

        player.sendMessage(LocaleLoader.getString("Item.ChimaeraWing.Pass"));
    }

    public static ItemStack getChimaeraWing(int amount) {
        ItemStack itemStack = new ItemStack(Config.getInstance().getChimaeraItem(), amount);

        ItemMeta itemMeta = itemStack.getItemMeta();
        itemMeta.setDisplayName(ChatColor.GOLD + LocaleLoader.getString("Item.ChimaeraWing.Name"));

        List<String> itemLore = new ArrayList<String>();
        itemLore.add("mcMMO Item");
        itemLore.add(LocaleLoader.getString("Item.ChimaeraWing.Lore"));
        itemMeta.setLore(itemLore);

        itemStack.setItemMeta(itemMeta);
        return itemStack;
    }

    public static ShapelessRecipe getChimaeraWingRecipe() {
        Material ingredient = Config.getInstance().getChimaeraItem();
        int amount = Config.getInstance().getChimaeraRecipeCost();

        ShapelessRecipe chimeraWing = new ShapelessRecipe(getChimaeraWing(1));
        chimeraWing.addIngredient(amount, ingredient);
        return chimeraWing;
    }
}
