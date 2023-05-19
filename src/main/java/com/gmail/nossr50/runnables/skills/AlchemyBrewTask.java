package com.gmail.nossr50.runnables.skills;

import com.gmail.nossr50.datatypes.player.McMMOPlayer;
import com.gmail.nossr50.datatypes.skills.PrimarySkillType;
import com.gmail.nossr50.datatypes.skills.SubSkillType;
import com.gmail.nossr50.events.skills.alchemy.McMMOPlayerBrewEvent;
import com.gmail.nossr50.events.skills.alchemy.McMMOPlayerCatalysisEvent;
import com.gmail.nossr50.mcMMO;
import com.gmail.nossr50.skills.alchemy.Alchemy;
import com.gmail.nossr50.skills.alchemy.AlchemyPotionBrewer;
import com.gmail.nossr50.util.Misc;
import com.gmail.nossr50.util.Permissions;
import com.gmail.nossr50.util.player.UserManager;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.BrewingStand;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

public class AlchemyBrewTask extends BukkitRunnable {
    private static final double DEFAULT_BREW_SPEED = 1.0;
    private static final int    DEFAULT_BREW_TICKS = 400;

    private final BrewingStand brewingStand;
    private final Location location;
    private double brewSpeed;
    private double brewTimer;
    private final Player player;
    private int fuel;
    private boolean firstRun = true;

    public AlchemyBrewTask(BrewingStand brewingStand, Player player) {
        this.brewingStand = brewingStand;
        this.location = brewingStand.getLocation();
        this.player = player;

        brewSpeed = DEFAULT_BREW_SPEED;
        brewTimer = DEFAULT_BREW_TICKS;

        McMMOPlayer mcMMOPlayer = UserManager.getPlayer(player);

        if (player != null
                && !Misc.isNPCEntityExcludingVillagers(player)
                && Permissions.isSubSkillEnabled(player, SubSkillType.ALCHEMY_CATALYSIS)
                && mcMMOPlayer != null) {

            double catalysis = mcMMOPlayer.getAlchemyManager().calculateBrewSpeed(Permissions.lucky(player, PrimarySkillType.ALCHEMY));

            McMMOPlayerCatalysisEvent event = new McMMOPlayerCatalysisEvent(player, catalysis);
            mcMMO.p.getServer().getPluginManager().callEvent(event);

            if (!event.isCancelled()) {
                brewSpeed = catalysis;
            }
        }

        AlchemyBrewTask existing = Alchemy.brewingStandMap.get(location);
        if (existing != null) {
            existing.cancel();
        }

        fuel = brewingStand.getFuelLevel();

        if (brewingStand.getBrewingTime() == -1) // Only decrement on our end if it isn't a vanilla ingredient.
            fuel--;

        Alchemy.brewingStandMap.put(location, this);
        this.runTaskTimer(mcMMO.p, 1, 1);
    }

    @Override
    public void run() {
        if (player == null || !player.isValid() || location.getBlock().getType() != Material.BREWING_STAND || !AlchemyPotionBrewer.isValidIngredient(player, brewingStand.getInventory().getContents()[Alchemy.INGREDIENT_SLOT])) {
            Alchemy.brewingStandMap.remove(location);

            this.cancel();

            return;
        }

        if (firstRun) {
            firstRun = false;
            brewingStand.setFuelLevel(fuel);
        }

        brewTimer -= brewSpeed;

        // Vanilla potion brewing completes when BrewingTime == 1
        if (brewTimer < Math.max(brewSpeed, 2)) {
            this.cancel();
            finish();
        }
        else {
            brewingStand.setBrewingTime((int) brewTimer);
        }
    }

    private void finish() {
        McMMOPlayerBrewEvent event = new McMMOPlayerBrewEvent(player, brewingStand);
        mcMMO.p.getServer().getPluginManager().callEvent(event);

        if (!event.isCancelled()) {
            AlchemyPotionBrewer.finishBrewing(brewingStand, player, false);
        }

        Alchemy.brewingStandMap.remove(location);
    }

    public void finishImmediately() {
        this.cancel();

        AlchemyPotionBrewer.finishBrewing(brewingStand, player, true);
        Alchemy.brewingStandMap.remove(location);
    }

    public void cancelBrew() {
        this.cancel();

        ((BrewingStand) brewingStand).setBrewingTime(-1);
        Alchemy.brewingStandMap.remove(location);
    }
}
