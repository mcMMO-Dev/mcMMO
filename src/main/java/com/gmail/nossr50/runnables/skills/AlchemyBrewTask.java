package com.gmail.nossr50.runnables.skills;

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
import org.bukkit.block.BlockState;
import org.bukkit.block.BrewingStand;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

public class AlchemyBrewTask extends BukkitRunnable {
    private static final double DEFAULT_BREW_SPEED = 1.0;
    private static final int    DEFAULT_BREW_TICKS = 400;

    private final BlockState brewingStand;
    private final Location location;
    private double brewSpeed;
    private double brewTimer;
    private final Player player;
    private int fuel;
    private boolean firstRun = true;

    public AlchemyBrewTask(BlockState brewingStand, Player player) {
        this.brewingStand = brewingStand;
        this.location = brewingStand.getLocation();
        this.player = player;

        brewSpeed = DEFAULT_BREW_SPEED;
        brewTimer = DEFAULT_BREW_TICKS;

        if (player != null
                && !Misc.isNPCEntityExcludingVillagers(player)
                && Permissions.isSubSkillEnabled(player, SubSkillType.ALCHEMY_CATALYSIS)
                && UserManager.getPlayer(player) != null) {

            double catalysis = UserManager.getPlayer(player).getAlchemyManager().calculateBrewSpeed(Permissions.lucky(player, PrimarySkillType.ALCHEMY));

            McMMOPlayerCatalysisEvent event = new McMMOPlayerCatalysisEvent(player, catalysis);
            mcMMO.p.getServer().getPluginManager().callEvent(event);

            if (!event.isCancelled()) {
                brewSpeed = catalysis;
            }
        }

        if (Alchemy.brewingStandMap.containsKey(location)) {
            Alchemy.brewingStandMap.get(location).cancel();
        }

        fuel = ((BrewingStand) brewingStand).getFuelLevel();

        if (((BrewingStand) brewingStand).getBrewingTime() == -1) // Only decrement on our end if it isn't a vanilla ingredient.
            fuel--;

        Alchemy.brewingStandMap.put(location, this);
        this.runTaskTimer(mcMMO.p, 1, 1);
    }

    @Override
    public void run() {
        if (player == null || !player.isValid() || brewingStand == null || brewingStand.getType() != Material.BREWING_STAND || !AlchemyPotionBrewer.isValidIngredient(player, ((BrewingStand) brewingStand).getInventory().getContents()[Alchemy.INGREDIENT_SLOT])) {
            if (Alchemy.brewingStandMap.containsKey(location)) {
                Alchemy.brewingStandMap.remove(location);
            }

            this.cancel();

            return;
        }

        if (firstRun) {
            firstRun = false;
            ((BrewingStand) brewingStand).setFuelLevel(fuel);
        }

        brewTimer -= brewSpeed;

        // Vanilla potion brewing completes when BrewingTime == 1
        if (brewTimer < Math.max(brewSpeed, 2)) {
            this.cancel();
            finish();
        }
        else {
            ((BrewingStand) brewingStand).setBrewingTime((int) brewTimer);
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
