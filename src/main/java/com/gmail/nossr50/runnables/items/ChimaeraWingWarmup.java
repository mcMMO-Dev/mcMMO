package com.gmail.nossr50.runnables.items;

import static com.gmail.nossr50.util.ChimaeraWing.expendChimaeraWing;

import com.gmail.nossr50.datatypes.interactions.NotificationType;
import com.gmail.nossr50.datatypes.player.McMMOPlayer;
import com.gmail.nossr50.locale.LocaleLoader;
import com.gmail.nossr50.mcMMO;
import com.gmail.nossr50.util.CancellableRunnable;
import com.gmail.nossr50.util.ChimaeraWing;
import com.gmail.nossr50.util.ItemUtils;
import com.gmail.nossr50.util.Misc;
import com.gmail.nossr50.util.player.NotificationManager;
import com.gmail.nossr50.util.skills.SkillUtils;
import com.gmail.nossr50.util.sounds.SoundManager;
import com.gmail.nossr50.util.sounds.SoundType;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class ChimaeraWingWarmup extends CancellableRunnable {
    private final McMMOPlayer mmoPlayer;
    private final Location location;

    public ChimaeraWingWarmup(McMMOPlayer mmoPlayer, Location location) {
        this.mmoPlayer = mmoPlayer;
        this.location = location;
    }

    @Override
    public void run() {
        checkChimaeraWingTeleport();
    }

    private void checkChimaeraWingTeleport() {
        final Player player = mmoPlayer.getPlayer();
        final Location previousLocation = mmoPlayer.getTeleportCommenceLocation();

        if (player.getLocation().distanceSquared(previousLocation) > 1.0
                || !player.getInventory().containsAtLeast(ChimaeraWing.getChimaeraWing(1), 1)) {
            player.sendMessage(LocaleLoader.getString("Teleport.Cancelled"));
            mmoPlayer.setTeleportCommenceLocation(null);
            return;
        }

        final ItemStack inHand = player.getInventory().getItemInMainHand();

        if (!ItemUtils.isChimaeraWing(inHand) || inHand.getAmount() < mcMMO.p.getGeneralConfig()
                .getChimaeraUseCost()) {
            player.sendMessage(LocaleLoader.getString("Skills.NeedMore",
                    LocaleLoader.getString("Item.ChimaeraWing.Name")));
            return;
        }

        long recentlyHurt = mmoPlayer.getRecentlyHurt();
        int hurtCooldown = mcMMO.p.getGeneralConfig().getChimaeraRecentlyHurtCooldown();

        if (hurtCooldown > 0) {
            int timeRemaining = SkillUtils.calculateTimeLeft(
                    recentlyHurt * Misc.TIME_CONVERSION_FACTOR, hurtCooldown, player);

            if (timeRemaining > 0) {
                player.sendMessage(LocaleLoader.getString("Item.Injured.Wait", timeRemaining));
                return;
            }
        }

        chimaeraExecuteTeleport();
    }

    private void chimaeraExecuteTeleport() {
        final Player player = mmoPlayer.getPlayer();

        if (mcMMO.p.getGeneralConfig().getChimaeraUseBedSpawn()
                && player.getBedSpawnLocation() != null) {
            mcMMO.p.getFoliaLib().getScheduler()
                    .teleportAsync(player, player.getBedSpawnLocation());
        } else {
            final Location spawnLocation = player.getWorld().getSpawnLocation();
            if (spawnLocation.getBlock().getType() == Material.AIR) {
                mcMMO.p.getFoliaLib().getScheduler().teleportAsync(player, spawnLocation);
            } else {
                mcMMO.p.getFoliaLib().getScheduler().teleportAsync(
                        player, player.getWorld().getHighestBlockAt(spawnLocation).getLocation());
            }
        }

        expendChimaeraWing(player, mcMMO.p.getGeneralConfig().getChimaeraUseCost(),
                player.getInventory().getItemInMainHand());
        mmoPlayer.actualizeChimeraWingLastUse();
        mmoPlayer.setTeleportCommenceLocation(null);

        if (mcMMO.p.getGeneralConfig().getChimaeraSoundEnabled()) {
            SoundManager.sendSound(player, location, SoundType.CHIMAERA_WING);
        }

        NotificationManager.sendPlayerInformation(player, NotificationType.ITEM_MESSAGE,
                "Item.ChimaeraWing.Pass");
    }
}
