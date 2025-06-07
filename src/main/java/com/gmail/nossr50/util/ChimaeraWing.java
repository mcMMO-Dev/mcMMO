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

        final ItemStack inHand = player.getInventory().getItemInMainHand();

        if (!ItemUtils.isChimaeraWing(inHand)) {
            return;
        }

        if (!Permissions.chimaeraWing(player)) {
            NotificationManager.sendPlayerInformation(player, NotificationType.NO_PERMISSION, "mcMMO.NoPermission");
            return;
        }

        final McMMOPlayer mcMMOPlayer = UserManager.getPlayer(player);

        //Not loaded
        if (mcMMOPlayer == null)
            return;

        if (mcMMOPlayer.getTeleportCommenceLocation() != null) {
            return;
        }

        int amountInHand = inHand.getAmount();

        if (amountInHand < mcMMO.p.getGeneralConfig().getChimaeraUseCost()) {
            NotificationManager.sendPlayerInformation(player, NotificationType.REQUIREMENTS_NOT_MET, "Item.ChimaeraWing.NotEnough",String.valueOf(mcMMO.p.getGeneralConfig().getChimaeraUseCost() - amountInHand), "Item.ChimaeraWing.Name");
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

        final Location playerLocation = player.getLocation();

        if (mcMMO.p.getGeneralConfig().getChimaeraPreventUseUnderground()) {
            if (playerLocation.getY() < player.getWorld().getHighestBlockYAt(playerLocation)) {
                expendChimaeraWing(player, amountInHand, inHand);
                NotificationManager.sendPlayerInformation(player,
                        NotificationType.REQUIREMENTS_NOT_MET, "Item.ChimaeraWing.Fail");
                player.setVelocity(new Vector(0, 0.5D, 0));
                final int dmg = Misc.getRandom().nextInt((int) (player.getHealth() - 10));
                CombatUtils.safeDealDamage(player, dmg);
                mcMMOPlayer.actualizeChimeraWingLastUse();
                return;
            }
        }

        mcMMOPlayer.actualizeTeleportCommenceLocation(player);
        long teleportDelay = mcMMO.p.getGeneralConfig().getChimaeraWarmup();
        if (teleportDelay > 0) {
            NotificationManager.sendPlayerInformation(player, NotificationType.ITEM_MESSAGE, "Teleport.Commencing", String.valueOf(teleportDelay));
            mcMMO.p.getFoliaLib().getScheduler().runAtEntityLater(player, new ChimaeraWingWarmup(mcMMOPlayer, playerLocation), 20 * teleportDelay);
        } else {
            mcMMO.p.getFoliaLib().getScheduler().runAtEntityLater(player, new ChimaeraWingWarmup(mcMMOPlayer, playerLocation), 0);
        }
    }

    public static void expendChimaeraWing(Player player, int amountInHand, ItemStack inHand) {
        int amountAfterUse = amountInHand - mcMMO.p.getGeneralConfig().getChimaeraUseCost();
        if (amountAfterUse >= 1) {
            inHand.setAmount(amountAfterUse);
            player.getInventory().setItemInMainHand(inHand);
        } else {
            player.getInventory().removeItem(inHand);
        }
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
