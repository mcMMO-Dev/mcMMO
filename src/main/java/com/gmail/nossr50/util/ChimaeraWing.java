package com.gmail.nossr50.util;

import com.gmail.nossr50.datatypes.interactions.NotificationType;
import com.gmail.nossr50.datatypes.player.McMMOPlayer;
import com.gmail.nossr50.locale.LocaleLoader;
import com.gmail.nossr50.mcMMO;
import com.gmail.nossr50.runnables.items.ChimaeraWingWarmup;
import com.gmail.nossr50.util.player.NotificationManager;
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

    private ChimaeraWing() {}

    /**
     * Check for item usage.
     *
     * @param player Player whose item usage to check
     */
    public static void activationCheck(Player player) {
        if (!mcMMO.p.getGeneralConfig().getChimaeraEnabled()) {
            return;
        }

        ItemStack inHand = player.getInventory().getItemInMainHand();

        if (!ItemUtils.isChimaeraWing(inHand)) {
            return;
        }

        if (!Permissions.chimaeraWing(player)) {
            NotificationManager.sendPlayerInformation(player, NotificationType.NO_PERMISSION, "mcMMO.NoPermission");
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

        if (amount < mcMMO.p.getGeneralConfig().getChimaeraUseCost()) {
            NotificationManager.sendPlayerInformation(player, NotificationType.REQUIREMENTS_NOT_MET, "Item.ChimaeraWing.NotEnough",String.valueOf(mcMMO.p.getGeneralConfig().getChimaeraUseCost() - amount), "Item.ChimaeraWing.Name");
            return;
        }

        long lastTeleport = mcMMOPlayer.getChimeraWingLastUse();
        int cooldown = mcMMO.p.getGeneralConfig().getChimaeraCooldown();

        if (cooldown > 0) {
            int timeRemaining = SkillUtils.calculateTimeLeft(lastTeleport * Misc.TIME_CONVERSION_FACTOR, cooldown, player);

            if (timeRemaining > 0) {
                NotificationManager.sendPlayerInformation(player, NotificationType.ABILITY_COOLDOWN, "Item.Generic.Wait", String.valueOf(timeRemaining));
                return;
            }
        }

        long recentlyHurt = mcMMOPlayer.getRecentlyHurt();
        int hurtCooldown = mcMMO.p.getGeneralConfig().getChimaeraRecentlyHurtCooldown();

        if (hurtCooldown > 0) {
            int timeRemaining = SkillUtils.calculateTimeLeft(recentlyHurt * Misc.TIME_CONVERSION_FACTOR, hurtCooldown, player);

            if (timeRemaining > 0) {
                NotificationManager.sendPlayerInformation(player, NotificationType.ITEM_MESSAGE, "Item.Injured.Wait", String.valueOf(timeRemaining));
                return;
            }
        }

        location = player.getLocation();

        if (mcMMO.p.getGeneralConfig().getChimaeraPreventUseUnderground()) {
            if (location.getY() < player.getWorld().getHighestBlockYAt(location)) {
                player.getInventory().setItemInMainHand(new ItemStack(getChimaeraWing(amount - mcMMO.p.getGeneralConfig().getChimaeraUseCost())));
                NotificationManager.sendPlayerInformation(player, NotificationType.REQUIREMENTS_NOT_MET, "Item.ChimaeraWing.Fail");
                player.updateInventory();
                player.setVelocity(new Vector(0, 0.5D, 0));
                CombatUtils.dealDamage(player, Misc.getRandom().nextInt((int) (player.getHealth() - 10)));
                mcMMOPlayer.actualizeChimeraWingLastUse();
                return;
            }
        }

        mcMMOPlayer.actualizeTeleportCommenceLocation(player);

        long warmup = mcMMO.p.getGeneralConfig().getChimaeraWarmup();

        if (warmup > 0) {
            NotificationManager.sendPlayerInformation(player, NotificationType.ITEM_MESSAGE, "Teleport.Commencing", String.valueOf(warmup));
            mcMMO.p.getFoliaLib().getImpl().runAtEntityLater(player, new ChimaeraWingWarmup(mcMMOPlayer), 20 * warmup);
        } else {
            chimaeraExecuteTeleport();
        }
    }

    public static void chimaeraExecuteTeleport() {
        Player player = mcMMOPlayer.getPlayer();

        if (mcMMO.p.getGeneralConfig().getChimaeraUseBedSpawn() && player.getBedSpawnLocation() != null) {
//            player.teleport(player.getBedSpawnLocation());
            mcMMO.p.getFoliaLib().getImpl().teleportAsync(player, player.getBedSpawnLocation());
        } else {
            Location spawnLocation = player.getWorld().getSpawnLocation();
            if (spawnLocation.getBlock().getType() == Material.AIR) {
//                player.teleport(spawnLocation);
                mcMMO.p.getFoliaLib().getImpl().teleportAsync(player, spawnLocation);
            } else {
//                player.teleport(player.getWorld().getHighestBlockAt(spawnLocation).getLocation());
                mcMMO.p.getFoliaLib().getImpl().teleportAsync(player, player.getWorld().getHighestBlockAt(spawnLocation).getLocation());
            }
        }

        player.getInventory().setItemInMainHand(new ItemStack(getChimaeraWing(player.getInventory().getItemInMainHand().getAmount() - mcMMO.p.getGeneralConfig().getChimaeraUseCost())));
        player.updateInventory();
        mcMMOPlayer.actualizeChimeraWingLastUse();
        mcMMOPlayer.setTeleportCommenceLocation(null);

        if (mcMMO.p.getGeneralConfig().getChimaeraSoundEnabled()) {
            SoundManager.sendSound(player, location, SoundType.CHIMAERA_WING);
        }

        NotificationManager.sendPlayerInformation(player, NotificationType.ITEM_MESSAGE, "Item.ChimaeraWing.Pass");
    }

    public static ItemStack getChimaeraWing(int amount) {
        ItemStack itemStack = new ItemStack(mcMMO.p.getGeneralConfig().getChimaeraItem(), amount);

        ItemMeta itemMeta = itemStack.getItemMeta();
        //noinspection ConstantConditions
        itemMeta.setDisplayName(ChatColor.GOLD + LocaleLoader.getString("Item.ChimaeraWing.Name"));

        List<String> itemLore = new ArrayList<>();
        itemLore.add("mcMMO Item");
        itemLore.add(LocaleLoader.getString("Item.ChimaeraWing.Lore"));
        itemMeta.setLore(itemLore);

        itemStack.setItemMeta(itemMeta);
        return itemStack;
    }

    public static ShapelessRecipe getChimaeraWingRecipe() {
        Material ingredient = mcMMO.p.getGeneralConfig().getChimaeraItem();
        int amount = mcMMO.p.getGeneralConfig().getChimaeraRecipeCost();

        ShapelessRecipe chimeraWing = new ShapelessRecipe(new NamespacedKey(mcMMO.p, "Chimera"), getChimaeraWing(1));
        chimeraWing.addIngredient(amount, ingredient);
        return chimeraWing;
    }
}
