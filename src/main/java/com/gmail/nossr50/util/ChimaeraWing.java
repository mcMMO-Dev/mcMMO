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
        ItemStack inHand = player.getItemInHand();

        if (!Config.getInstance().getChimaeraEnabled() || !ItemUtils.isChimaeraWing(inHand)) {
            return;
        }

        if (!Permissions.chimaeraWing(player)) {
            player.sendMessage(LocaleLoader.getString("mcMMO.NoPermission"));
            return;
        }

        mcMMOPlayer = UserManager.getPlayer(player);

        location = player.getLocation();
        int amount = inHand.getAmount();
        long recentlyHurt = mcMMOPlayer.getRecentlyHurt();
        long lastTeleport = mcMMOPlayer.getLastTeleport();

        if (mcMMOPlayer.getTeleportCommenceLocation() != null) {
            return;
        }

        if (Config.getInstance().getChimaeraCooldown() > 0 && !SkillUtils.cooldownOver(lastTeleport * Misc.TIME_CONVERSION_FACTOR, Config.getInstance().getChimaeraCooldown(), player)) {
            player.sendMessage(LocaleLoader.getString("Item.Generic.Wait", SkillUtils.calculateTimeLeft(lastTeleport * Misc.TIME_CONVERSION_FACTOR, Config.getInstance().getChimaeraCooldown(), player)));
            return;
        }

        int recentlyhurt_cooldown = Config.getInstance().getChimaeraRecentlyHurtCooldown();

        if (!SkillUtils.cooldownOver(recentlyHurt * Misc.TIME_CONVERSION_FACTOR, recentlyhurt_cooldown, player)) {
            player.sendMessage(LocaleLoader.getString("Item.Injured.Wait", SkillUtils.calculateTimeLeft(recentlyHurt * Misc.TIME_CONVERSION_FACTOR, recentlyhurt_cooldown, player)));
            return;
        }

        if (amount < Config.getInstance().getChimaeraUseCost()) {
            player.sendMessage(LocaleLoader.getString("Skills.NeedMore", LocaleLoader.getString("Item.ChimaeraWing.Name")));
            return;
        }

        if (Config.getInstance().getChimaeraPreventUseUnderground()) {
            if (location.getY() < player.getWorld().getHighestBlockYAt(location)) {
                player.setItemInHand(new ItemStack(getChimaeraWing(amount - Config.getInstance().getChimaeraUseCost())));
                player.sendMessage(LocaleLoader.getString("Item.ChimaeraWing.Fail"));
                player.setVelocity(new Vector(0, 0.5D, 0));
                CombatUtils.dealDamage(player, Misc.getRandom().nextInt(player.getHealth() - 10));
                mcMMOPlayer.actualizeLastTeleport();
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

        if (player.getBedSpawnLocation() != null) {
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
        UserManager.getPlayer(player).actualizeLastTeleport();
        if (Config.getInstance().getStatsTrackingEnabled()) {
            MetricsManager.chimeraWingUsed();
        }
        player.playSound(location, Sound.BAT_TAKEOFF, Misc.BAT_VOLUME, Misc.BAT_PITCH);
        player.sendMessage(LocaleLoader.getString("Item.ChimaeraWing.Pass"));
    }

    public static ItemStack getChimaeraWing(int amount) {
        Material ingredient = Material.getMaterial(Config.getInstance().getChimaeraItemId());
        ItemStack itemStack = new ItemStack(ingredient, amount);

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
