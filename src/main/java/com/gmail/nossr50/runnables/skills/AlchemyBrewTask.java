package com.gmail.nossr50.runnables.skills;

import com.gmail.nossr50.datatypes.player.McMMOPlayer;
import com.gmail.nossr50.datatypes.skills.PrimarySkillType;
import com.gmail.nossr50.datatypes.skills.SubSkillType;
import com.gmail.nossr50.events.skills.alchemy.McMMOPlayerBrewEvent;
import com.gmail.nossr50.events.skills.alchemy.McMMOPlayerCatalysisEvent;
import com.gmail.nossr50.mcMMO;
import com.gmail.nossr50.skills.alchemy.Alchemy;
import com.gmail.nossr50.skills.alchemy.AlchemyPotionBrewer;
import com.gmail.nossr50.util.CancellableRunnable;
import com.gmail.nossr50.util.ContainerMetadataUtils;
import com.gmail.nossr50.util.Misc;
import com.gmail.nossr50.util.Permissions;
import com.gmail.nossr50.util.player.UserManager;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.block.BlockState;
import org.bukkit.block.BrewingStand;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class AlchemyBrewTask extends CancellableRunnable {
    private static final double DEFAULT_BREW_SPEED = 1.0;
    private static final int    DEFAULT_BREW_TICKS = 400;

    private final BlockState brewingStand;
    private final OfflinePlayer offlinePlayer;
    private McMMOPlayer mmoPlayer;
    private double brewSpeed;
    private double brewTimer;
    private int fuel;
    private boolean firstRun = true;
    private int ingredientLevel = 1;

    @Deprecated(forRemoval = true, since = "2.2.010")
    public AlchemyBrewTask(@NotNull BlockState brewingStand, Player ignored) {
        this(brewingStand);
    }

    public AlchemyBrewTask(@NotNull BlockState brewingStand) {
        offlinePlayer = ContainerMetadataUtils.getContainerOwner(brewingStand);
        McMMOPlayer mmoPlayer = null;
        if (offlinePlayer != null && offlinePlayer.isOnline()) {
            mmoPlayer = UserManager.getPlayer(offlinePlayer.getPlayer());
        }

        this.brewingStand = brewingStand;

        brewSpeed = DEFAULT_BREW_SPEED;
        brewTimer = DEFAULT_BREW_TICKS;

        if (mmoPlayer != null
                && !Misc.isNPCEntityExcludingVillagers(mmoPlayer.getPlayer())
                && Permissions.isSubSkillEnabled(mmoPlayer.getPlayer(), SubSkillType.ALCHEMY_CATALYSIS)) {
            ingredientLevel = mmoPlayer.getAlchemyManager().getTier();

            double catalysis = mmoPlayer.getAlchemyManager().calculateBrewSpeed(Permissions.lucky(mmoPlayer.getPlayer(),
                    PrimarySkillType.ALCHEMY));

            McMMOPlayerCatalysisEvent event = new McMMOPlayerCatalysisEvent(mmoPlayer, catalysis);
            mcMMO.p.getServer().getPluginManager().callEvent(event);

            if (!event.isCancelled()) {
                brewSpeed = catalysis;
            }
        }

        if (Alchemy.brewingStandMap.containsKey(brewingStand.getLocation())) {
            Alchemy.brewingStandMap.get(brewingStand.getLocation()).cancel();
        }

        fuel = ((BrewingStand) brewingStand).getFuelLevel();

        if (((BrewingStand) brewingStand).getBrewingTime() == -1) // Only decrement on our end if it isn't a vanilla ingredient.
            fuel--;

        Alchemy.brewingStandMap.put(brewingStand.getLocation(), this);
        mcMMO.p.getFoliaLib().getImpl().runAtLocationTimer(brewingStand.getLocation(), this, 1, 1);
    }

    @Override
    public void run() {
        // Check if preconditions for brewing are not met
        if (shouldCancelBrewing()) {
            if (Alchemy.brewingStandMap.containsKey(brewingStand.getLocation())) {
                Alchemy.brewingStandMap.remove(brewingStand.getLocation());
            }
            this.cancel();
            return;
        }

        // Initialize the brewing stand on the first run
        initializeBrewing();

        // Update the brewing process timer
        brewTimer -= brewSpeed;

        // Check if the brewing process should finish
        if (isBrewingComplete()) {
            this.cancel();
            finish();
        } else {
            updateBrewingTime();
        }
    }

    private boolean shouldCancelBrewing() {
        if (offlinePlayer == null) {
            return true;
        }
        if (brewingStand == null) {
            return true;
        }
        if (brewingStand.getType() != Material.BREWING_STAND) {
            return true;
        }
        return !AlchemyPotionBrewer.isValidIngredientByLevel(
                getIngredientLevelUpdated(), ((BrewingStand) brewingStand).getInventory().getContents()[Alchemy.INGREDIENT_SLOT]);
    }

    private int getIngredientLevelUpdated() {
        if (mmoPlayer != null) {
            ingredientLevel = mmoPlayer.getAlchemyManager().getTier();
            return ingredientLevel;
        } else if (offlinePlayer.isOnline() && mmoPlayer == null) {
            final McMMOPlayer fetchedMMOPlayer = UserManager.getPlayer(offlinePlayer.getPlayer());
            if (fetchedMMOPlayer != null) {
                this.mmoPlayer = fetchedMMOPlayer;
                ingredientLevel = mmoPlayer.getAlchemyManager().getTier();
            }
            return ingredientLevel;
        } else {
            return ingredientLevel;
        }
    }

    private void initializeBrewing() {
        if (firstRun) {
            firstRun = false;
            ((BrewingStand) brewingStand).setFuelLevel(fuel);
        }
    }

    private boolean isBrewingComplete() {
        return brewTimer < Math.max(brewSpeed, 2);
    }

    private void updateBrewingTime() {
        ((BrewingStand) brewingStand).setBrewingTime((int) brewTimer);
    }


    private void finish() {
        if (mmoPlayer == null) {
            // Still need to finish brewing if the player is null
            AlchemyPotionBrewer.finishBrewing(brewingStand, null, false);
        } else {
            final McMMOPlayerBrewEvent event = new McMMOPlayerBrewEvent(mmoPlayer, brewingStand);
            mcMMO.p.getServer().getPluginManager().callEvent(event);

            if (!event.isCancelled()) {
                AlchemyPotionBrewer.finishBrewing(brewingStand, mmoPlayer, false);
            }
        }

        Alchemy.brewingStandMap.remove(brewingStand.getLocation());
    }

    public void finishImmediately() {
        this.cancel();

        AlchemyPotionBrewer.finishBrewing(brewingStand, mmoPlayer, true);
        Alchemy.brewingStandMap.remove(brewingStand.getLocation());
    }

    public void cancelBrew() {
        this.cancel();

        ((BrewingStand) brewingStand).setBrewingTime(-1);
        Alchemy.brewingStandMap.remove(brewingStand.getLocation());
    }
}
