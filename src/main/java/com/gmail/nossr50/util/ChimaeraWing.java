package com.gmail.nossr50.util;

import com.gmail.nossr50.datatypes.interactions.NotificationType;
import com.gmail.nossr50.datatypes.player.McMMOPlayer;
import com.gmail.nossr50.runnables.items.ChimaeraWingWarmup;
import com.gmail.nossr50.util.player.UserManager;
import com.gmail.nossr50.util.skills.CombatUtils;
import com.gmail.nossr50.util.skills.SkillUtils;
import com.gmail.nossr50.util.sounds.SoundManager;
import com.gmail.nossr50.util.sounds.SoundType;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ShapelessRecipe;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.util.Vector;

import java.util.ArrayList;
import java.util.List;

public final class ChimaeraWing {
    private static McMMOPlayer mcMMOPlayer;
    private static Location location;

    private ChimaeraWing() {
    }

    /**
     * Check for item usage.
     *
     * @param player Player whose item usage to check
     */
    public static void activationCheck(Player player) {
        if (!pluginRef.getConfigManager().getConfigItems().isChimaeraWingEnabled()) {
            return;
        }

        ItemStack inHand = player.getInventory().getItemInMainHand();

        if (!ItemUtils.isChimaeraWing(inHand)) {
            return;
        }

        if (!Permissions.chimaeraWing(player)) {
            pluginRef.getNotificationManager().sendPlayerInformation(player, NotificationType.NO_PERMISSION, "mcMMO.NoPermission");
            return;
        }

        mcMMOPlayer = UserManager.getPlayer(player);

        //Not loaded
        if (mcMMOPlayer == null)
            return;

        if (mcMMOPlayer.getTeleportCommenceLocation() != null) {
            return;
        }

        int amount = inHand.getAmount();

        if (amount < pluginRef.getConfigManager().getConfigItems().getChimaeraWingUseCost()) {
            pluginRef.getNotificationManager().sendPlayerInformation(player, NotificationType.REQUIREMENTS_NOT_MET, "Item.ChimaeraWing.NotEnough",
                    String.valueOf(pluginRef.getConfigManager().getConfigItems().getChimaeraWingUseCost() - amount), "Item.ChimaeraWing.Name");
            return;
        }

        long lastTeleport = mcMMOPlayer.getChimeraWingLastUse();
        int cooldown = pluginRef.getConfigManager().getConfigItems().getCooldown();

        if (cooldown > 0) {
            int timeRemaining = SkillUtils.calculateTimeLeft(lastTeleport * Misc.TIME_CONVERSION_FACTOR, cooldown, player);

            if (timeRemaining > 0) {
                pluginRef.getNotificationManager().sendPlayerInformation(player, NotificationType.ABILITY_COOLDOWN, "Item.Generic.Wait", String.valueOf(timeRemaining));
                return;
            }
        }

        long recentlyHurt = mcMMOPlayer.getRecentlyHurt();
        int hurtCooldown = pluginRef.getConfigManager().getConfigItems().getRecentlyHurtCooldown();

        if (hurtCooldown > 0) {
            int timeRemaining = SkillUtils.calculateTimeLeft(recentlyHurt * Misc.TIME_CONVERSION_FACTOR, hurtCooldown, player);

            if (timeRemaining > 0) {
                pluginRef.getNotificationManager().sendPlayerInformation(player, NotificationType.ITEM_MESSAGE, "Item.Injured.Wait", String.valueOf(timeRemaining));
                return;
            }
        }

        location = player.getLocation();

        if (pluginRef.getConfigManager().getConfigItems().isPreventUndergroundUse()) {
            if (location.getY() < player.getWorld().getHighestBlockYAt(location)) {
                player.getInventory().setItemInMainHand(new ItemStack(getChimaeraWing(amount - pluginRef.getConfigManager().getConfigItems().getChimaeraWingUseCost())));
                pluginRef.getNotificationManager().sendPlayerInformation(player, NotificationType.REQUIREMENTS_NOT_MET, "Item.ChimaeraWing.Fail");
                player.updateInventory();
                player.setVelocity(new Vector(0, 0.5D, 0));
                CombatUtils.dealDamage(player, Misc.getRandom().nextInt((int) (player.getHealth() - 10)));
                mcMMOPlayer.actualizeChimeraWingLastUse();
                return;
            }
        }

        mcMMOPlayer.actualizeTeleportCommenceLocation(player);

        long warmup = pluginRef.getConfigManager().getConfigItems().getChimaeraWingWarmup();

        if (warmup > 0) {
            pluginRef.getNotificationManager().sendPlayerInformation(player, NotificationType.ITEM_MESSAGE, "Teleport.Commencing", String.valueOf(warmup));
            new ChimaeraWingWarmup(mcMMOPlayer).runTaskLater(pluginRef, 20 * warmup);
        } else {
            chimaeraExecuteTeleport();
        }
    }

    public static void chimaeraExecuteTeleport() {
        Player player = mcMMOPlayer.getPlayer();

        if (pluginRef.getConfigManager().getConfigItems().doesChimaeraUseBedSpawn() && player.getBedSpawnLocation() != null) {
            player.teleport(player.getBedSpawnLocation());
        } else {
            Location spawnLocation = player.getWorld().getSpawnLocation();
            if (spawnLocation.getBlock().getType() == Material.AIR) {
                player.teleport(spawnLocation);
            } else {
                player.teleport(player.getWorld().getHighestBlockAt(spawnLocation).getLocation());
            }
        }

        player.getInventory().setItemInMainHand(new ItemStack(getChimaeraWing(player.getInventory().getItemInMainHand().getAmount() - pluginRef.getConfigManager().getConfigItems().getChimaeraWingUseCost())));
        player.updateInventory();
        mcMMOPlayer.actualizeChimeraWingLastUse();
        mcMMOPlayer.setTeleportCommenceLocation(null);

        if (pluginRef.getConfigManager().getConfigItems().isChimaeraWingSoundEnabled()) {
            SoundManager.sendSound(player, location, SoundType.CHIMAERA_WING);
        }

        pluginRef.getNotificationManager().sendPlayerInformation(player, NotificationType.ITEM_MESSAGE, "Item.ChimaeraWing.Pass");
    }

    public static ItemStack getChimaeraWing(int amount) {
        Material ingredient = Material.matchMaterial(pluginRef.getConfigManager().getConfigItems().getChimaeraWingRecipeMats());

        if(ingredient == null)
            ingredient = Material.FEATHER;

        ItemStack itemStack = new ItemStack(ingredient, amount);

        ItemMeta itemMeta = itemStack.getItemMeta();
        itemMeta.setDisplayName(ChatColor.GOLD + pluginRef.getLocaleManager().getString("Item.ChimaeraWing.Name"));

        List<String> itemLore = new ArrayList<>();
        itemLore.add("mcMMO Item");
        itemLore.add(pluginRef.getLocaleManager().getString("Item.ChimaeraWing.Lore"));
        itemMeta.setLore(itemLore);

        itemStack.setItemMeta(itemMeta);
        return itemStack;
    }

    public static ShapelessRecipe getChimaeraWingRecipe() {
        Material ingredient = Material.matchMaterial(pluginRef.getConfigManager().getConfigItems().getChimaeraWingRecipeMats());

        if(ingredient == null)
            ingredient = Material.FEATHER;

        int amount = pluginRef.getConfigManager().getConfigItems().getChimaeraWingUseCost();

        ShapelessRecipe chimaeraWing = new ShapelessRecipe(new NamespacedKey(pluginRef, "Chimaera"), getChimaeraWing(1));
        chimaeraWing.addIngredient(amount, ingredient);
        return chimaeraWing;
    }
}
