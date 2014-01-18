package com.gmail.nossr50.runnables.skills;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BrewingStand;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import com.gmail.nossr50.mcMMO;
import com.gmail.nossr50.datatypes.skills.SecondaryAbility;
import com.gmail.nossr50.datatypes.skills.SkillType;
import com.gmail.nossr50.events.skills.alchemy.McMMOPlayerBrewEvent;
import com.gmail.nossr50.events.skills.alchemy.McMMOPlayerCatalysisEvent;
import com.gmail.nossr50.skills.alchemy.Alchemy;
import com.gmail.nossr50.skills.alchemy.AlchemyPotionBrewer;
import com.gmail.nossr50.util.Misc;
import com.gmail.nossr50.util.Permissions;
import com.gmail.nossr50.util.player.UserManager;

public class AlchemyBrewTask extends BukkitRunnable {
    private final double DEFAULT_BREW_SPEED = 1.0;
    private final int    DEFAULT_BREW_TICKS = 400;

    private Block brewingStand;
    private double brewSpeed;
    private double brewTimer;
    private Player player;

    public AlchemyBrewTask(Block brewingStand, Player player) {
        this.brewingStand = brewingStand;
        this.player = player;

        brewSpeed = DEFAULT_BREW_SPEED;
        brewTimer = DEFAULT_BREW_TICKS;

        if (player != null && !Misc.isNPCEntity(player) && Permissions.secondaryAbilityEnabled(player, SecondaryAbility.CATALYSIS)) {
            double catalysis = UserManager.getPlayer(player).getAlchemyManager().getBrewSpeed();

            if (Permissions.lucky(player, SkillType.ALCHEMY)) {
                catalysis = UserManager.getPlayer(player).getAlchemyManager().getBrewSpeedLucky();
            }

            McMMOPlayerCatalysisEvent event = new McMMOPlayerCatalysisEvent(player, catalysis);
            Bukkit.getPluginManager().callEvent(event);
            if (!event.isCancelled()) {
                brewSpeed = catalysis;
            }
        }

        if (Alchemy.brewingStandMap.containsKey(brewingStand)) {
            Alchemy.brewingStandMap.get(brewingStand).cancel();
        }

        Alchemy.brewingStandMap.put(brewingStand, this);
        this.runTaskTimer(mcMMO.p, 1, 1);
    }

    @Override
    public void run() {
        if (brewingStand.getType() != Material.BREWING_STAND) {
            if (Alchemy.brewingStandMap.containsKey(brewingStand)) {
                Alchemy.brewingStandMap.remove(brewingStand);
            }

            this.cancel();

            return;
        }

        brewTimer -= brewSpeed;

        // Vanilla potion brewing completes when BrewingTime == 1
        if (brewTimer < Math.max(brewSpeed, 2)) {
            this.cancel();
            finish();
        }
        else {
            ((BrewingStand) brewingStand.getState()).setBrewingTime((int) brewTimer);
        }
    }

    private void finish() {
        McMMOPlayerBrewEvent event = new McMMOPlayerBrewEvent(player, brewingStand);
        Bukkit.getPluginManager().callEvent(event);

        if (!event.isCancelled()) {
            AlchemyPotionBrewer.finishBrewing(brewingStand, player, false);
        }

        Alchemy.brewingStandMap.remove(brewingStand);
    }

    public void finishImmediately() {
        this.cancel();

        AlchemyPotionBrewer.finishBrewing(brewingStand, player, true);
        Alchemy.brewingStandMap.remove(brewingStand);
    }

    public void cancelBrew() {
        this.cancel();

        ((BrewingStand) brewingStand.getState()).setBrewingTime(-1);
        Alchemy.brewingStandMap.remove(brewingStand);
    }
}
