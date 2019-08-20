package com.gmail.nossr50.util;

import com.gmail.nossr50.datatypes.interactions.NotificationType;
import com.gmail.nossr50.datatypes.player.McMMOPlayer;
import com.gmail.nossr50.mcMMO;
import com.gmail.nossr50.runnables.items.ChimaeraWingWarmup;
import com.gmail.nossr50.util.sounds.SoundManager;
import com.gmail.nossr50.util.sounds.SoundType;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;

public final class ChimaeraWing {
    private final mcMMO pluginRef;
    private final McMMOPlayer mcMMOPlayer;
    private final Player player;
    private final Location location;

    public ChimaeraWing(mcMMO pluginRef, McMMOPlayer mcMMOPlayer) {
        this.pluginRef = pluginRef;
        this.mcMMOPlayer = mcMMOPlayer;
        this.player = mcMMOPlayer.getPlayer();
        this.location = player.getLocation();
    }

    /**
     * Check for item usage.
     *
     */
    public void activationCheck() {
        ItemStack inHand = player.getInventory().getItemInMainHand();

        if (!pluginRef.getItemTools().isChimaeraWing(inHand)) {
            return;
        }

        if (!pluginRef.getPermissionTools().chimaeraWing(player)) {
            pluginRef.getNotificationManager().sendPlayerInformation(player, NotificationType.NO_PERMISSION, "mcMMO.NoPermission");
            return;
        }

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
            int timeRemaining = pluginRef.getSkillTools().calculateTimeLeft(lastTeleport * Misc.TIME_CONVERSION_FACTOR, cooldown, player);

            if (timeRemaining > 0) {
                pluginRef.getNotificationManager().sendPlayerInformation(player, NotificationType.ABILITY_COOLDOWN, "Item.Generic.Wait", String.valueOf(timeRemaining));
                return;
            }
        }

        long recentlyHurt = mcMMOPlayer.getRecentlyHurt();
        int hurtCooldown = pluginRef.getConfigManager().getConfigItems().getRecentlyHurtCooldown();

        if (hurtCooldown > 0) {
            int timeRemaining = pluginRef.getSkillTools().calculateTimeLeft(recentlyHurt * Misc.TIME_CONVERSION_FACTOR, hurtCooldown, player);

            if (timeRemaining > 0) {
                pluginRef.getNotificationManager().sendPlayerInformation(player, NotificationType.ITEM_MESSAGE, "Item.Injured.Wait", String.valueOf(timeRemaining));
                return;
            }
        }

        if (pluginRef.getConfigManager().getConfigItems().isPreventUndergroundUse()) {
            if (location.getY() < player.getWorld().getHighestBlockYAt(location)) {
                player.getInventory().getItemInMainHand().setAmount(player.getInventory().getItemInMainHand().getAmount() - pluginRef.getConfigManager().getConfigItems().getChimaeraWingUseCost());
                pluginRef.getNotificationManager().sendPlayerInformation(player, NotificationType.REQUIREMENTS_NOT_MET, "Item.ChimaeraWing.Fail");
                player.updateInventory();
                player.setVelocity(new Vector(0, 0.5D, 0));
                pluginRef.getCombatTools().dealDamage(player, Misc.getRandom().nextInt((int) (player.getHealth() - 10)));
                mcMMOPlayer.actualizeChimeraWingLastUse();
                return;
            }
        }

        mcMMOPlayer.actualizeTeleportCommenceLocation(player);

        long warmup = pluginRef.getConfigManager().getConfigItems().getChimaeraWingWarmup();

        if (warmup > 0) {
            pluginRef.getNotificationManager().sendPlayerInformation(player, NotificationType.ITEM_MESSAGE, "Teleport.Commencing", String.valueOf(warmup));
            new ChimaeraWingWarmup(pluginRef, mcMMOPlayer).runTaskLater(pluginRef, 20 * warmup);
        } else {
            chimaeraExecuteTeleport();
        }
    }

    public void chimaeraExecuteTeleport() {
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

        player.getInventory().getItemInMainHand().setAmount(player.getInventory().getItemInMainHand().getAmount() - pluginRef.getConfigManager().getConfigItems().getChimaeraWingUseCost());
        player.updateInventory();
        mcMMOPlayer.actualizeChimeraWingLastUse();
        mcMMOPlayer.setTeleportCommenceLocation(null);

        if (pluginRef.getConfigManager().getConfigItems().isChimaeraWingSoundEnabled()) {
            SoundManager.sendSound(player, location, SoundType.CHIMAERA_WING);
        }

        pluginRef.getNotificationManager().sendPlayerInformation(player, NotificationType.ITEM_MESSAGE, "Item.ChimaeraWing.Pass");
    }
}
