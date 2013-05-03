package com.gmail.nossr50.runnables.items;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

import com.gmail.nossr50.config.Config;
import com.gmail.nossr50.datatypes.player.McMMOPlayer;
import com.gmail.nossr50.locale.LocaleLoader;
import com.gmail.nossr50.util.ChimaeraWing;
import com.gmail.nossr50.util.ItemUtils;
import com.gmail.nossr50.util.Misc;
import com.gmail.nossr50.util.skills.SkillUtils;

public class ChimaeraWingWarmup extends BukkitRunnable {
    private McMMOPlayer mcMMOPlayer;

    public ChimaeraWingWarmup(McMMOPlayer mcMMOPlayer) {
        this.mcMMOPlayer = mcMMOPlayer;
    }

    @Override
    public void run() {
        checkChimaeraWingTeleport();
    }

    private void checkChimaeraWingTeleport() {
        Player player = mcMMOPlayer.getPlayer();
        Location previousLocation = mcMMOPlayer.getTeleportCommenceLocation();
        long recentlyHurt = mcMMOPlayer.getRecentlyHurt();
        ItemStack inHand = player.getItemInHand();

        mcMMOPlayer.setTeleportCommenceLocation(null);

        if (player.getLocation().distanceSquared(previousLocation) > 1.0 || !player.getInventory().containsAtLeast(ChimaeraWing.getChimaeraWing(0), 1)) {
            player.sendMessage(LocaleLoader.getString("Teleport.Cancelled"));
            return;
        }

        if (!ItemUtils.isChimaeraWing(inHand) || inHand.getAmount() < Config.getInstance().getChimaeraUseCost()) {
            player.sendMessage(LocaleLoader.getString("Skills.NeedMore", LocaleLoader.getString("Item.ChimaeraWing.Name")));
            return;
        }

        int recentlyhurt_cooldown = Config.getInstance().getChimaeraRecentlyHurtCooldown();

        if (!SkillUtils.cooldownOver(recentlyHurt * Misc.TIME_CONVERSION_FACTOR, recentlyhurt_cooldown, player)) {
            player.sendMessage(LocaleLoader.getString("Item.Injured.Wait", SkillUtils.calculateTimeLeft(recentlyHurt * Misc.TIME_CONVERSION_FACTOR, recentlyhurt_cooldown, player)));
            return;
        }

        ChimaeraWing.chimaeraExecuteTeleport();
    }
}
