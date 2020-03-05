package com.gmail.nossr50.mcmmo.bukkit;

import com.gmail.nossr50.core.MetadataConstants;
import com.gmail.nossr50.datatypes.player.BukkitMMOPlayer;
import com.gmail.nossr50.listeners.*;
import com.gmail.nossr50.mcMMO;
import com.gmail.nossr50.mcmmo.api.data.MMOEntity;
import com.gmail.nossr50.mcmmo.api.platform.PlatformProvider;
import com.gmail.nossr50.mcmmo.api.platform.ServerSoftwareType;
import com.gmail.nossr50.mcmmo.api.platform.scheduler.PlatformScheduler;
import com.gmail.nossr50.mcmmo.api.platform.util.MetadataStore;
import com.gmail.nossr50.mcmmo.api.platform.util.MobHealthBarManager;
import com.gmail.nossr50.mcmmo.bukkit.platform.entity.BukkitMMOEntity;
import com.gmail.nossr50.mcmmo.bukkit.platform.scheduler.BukkitPlatformScheduler;
import com.gmail.nossr50.mcmmo.bukkit.platform.util.BukkitMetadataStore;
import com.gmail.nossr50.mcmmo.bukkit.platform.util.BukkitMobHealthBarManager;
import org.bstats.bukkit.Metrics;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.Recipe;
import org.bukkit.inventory.ShapelessRecipe;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.UUID;
import java.util.logging.Logger;

import co.aikar.commands.CommandManager;
import co.aikar.commands.PaperCommandManager;

public class BukkitBootstrap extends JavaPlugin implements PlatformProvider<Entity> {

    private mcMMO core = new mcMMO(this);
    private final BukkitPlatformScheduler scheduler = new BukkitPlatformScheduler(this);
    private final MobHealthBarManager healthBarManager = new BukkitMobHealthBarManager(this, core);
    private final BukkitMetadataStore bukkitMetadataStore = new BukkitMetadataStore(this);

    private PaperCommandManager paperCommandManager;


    @Override
    public @NotNull Logger getLogger() {
        return super.getLogger();
    }

    @Override
    public void tearDown() {
        core.debug("Canceling all tasks...");
        getServer().getScheduler().cancelTasks(this); // This removes our tasks
        core.debug("Unregister all events...");
        HandlerList.unregisterAll(this); // Cancel event registrations
    }

    @Override
    public MetadataStore<MMOEntity<Entity>> getMetadataStore() {
        return bukkitMetadataStore;
    }

    @Override
    public String getVersion() {
        return getDescription().getVersion();
    }

    @Override
    public void earlyInit() {
        PluginManager pluginManager = Bukkit.getPluginManager();
        if (pluginManager.getPlugin("NoCheatPlus") != null && pluginManager.getPlugin("CompatNoCheatPlus") == null) {
            getLogger().warning("NoCheatPlus plugin found, but CompatNoCheatPlus was not found!");
            getLogger().warning("mcMMO will not work properly alongside NoCheatPlus without CompatNoCheatPlus");
        }

        registerEvents();
        paperCommandManager = new PaperCommandManager(this);
        paperCommandManager.registerDependency(mcMMO.class, core);
        MetadataConstants.metadataValue = new FixedMetadataValue(this, true);
    }

    @Override
    public boolean isSupported(boolean print) {
        boolean ret = getServerType() != ServerSoftwareType.CRAFTBUKKIT;
        if (!ret) {
            Bukkit
                    .getScheduler()
                    .scheduleSyncRepeatingTask(this,
                            () -> getLogger().severe("You are running an outdated version of " + getServerType() + ", mcMMO will not work unless you update to a newer version!"),
                            20, 20 * 60 * 30);

            if (getServerType() == ServerSoftwareType.CRAFTBUKKIT) {
                Bukkit.getScheduler()
                        .scheduleSyncRepeatingTask(this,
                                () -> getLogger().severe("We have detected you are using incompatible server software, our best guess is that you are using CraftBukkit. mcMMO requires Spigot or Paper, if you are not using CraftBukkit, you will still need to update your custom server software before mcMMO will work."),
                                20, 20 * 60 * 30);
            }
        }

        return ret;
    }

    @Override
    public ServerSoftwareType getServerType() {
        if (Bukkit.getVersion().toLowerCase(Locale.ENGLISH).contains("paper"))
            return ServerSoftwareType.PAPER;
        else if (Bukkit.getVersion().toLowerCase(Locale.ENGLISH).contains("spigot"))
            return ServerSoftwareType.SPIGOT;
        else
            return ServerSoftwareType.CRAFTBUKKIT;
    }

    @Override
    public void printUnsupported() {

    }

    @Override
    public PlatformScheduler getScheduler() {
        return scheduler;
    }

    @Override
    public void checkMetrics() {
        //If anonymous statistics are enabled then use them
        if (core.getConfigManager().getConfigMetrics().isAllowAnonymousUsageStatistics()) {
            Metrics metrics;
            metrics = new Metrics(this);
            metrics.addCustomChart(new Metrics.SimplePie("version", this::getVersion));

            int levelScaleModifier = core.getConfigManager().getConfigLeveling().getConfigSectionLevelingGeneral().getConfigSectionLevelScaling().getCosmeticLevelScaleModifier();

            if (levelScaleModifier == 10)
                metrics.addCustomChart(new Metrics.SimplePie("scaling", () -> "Standard"));
            else if (levelScaleModifier == 1)
                metrics.addCustomChart(new Metrics.SimplePie("scaling", () -> "Retro"));
            else
                metrics.addCustomChart(new Metrics.SimplePie("scaling", () -> "Custom"));
        }
    }

    @Override
    public MobHealthBarManager getHealthBarManager() {
        return healthBarManager;
    }

    @Override
    public void registerCustomRecipes() {
        getServer().getScheduler().scheduleSyncDelayedTask(this, () -> {
            if (core.getConfigManager().getConfigItems().isChimaeraWingEnabled()) {
                Recipe recipe = getChimaeraWingRecipe();

                if(!core.getSkillTools().hasRecipeBeenRegistered(recipe))
                    getServer().addRecipe(getChimaeraWingRecipe());
            }
        }, 40);
    }

    @Override
    public CommandManager getCommandManager() {
        return paperCommandManager;
    }

    @Override
    @Deprecated // TODO: This needs proper registration...
    public MMOEntity<?> getEntity(UUID uniqueId) {
        return getEntity(Bukkit.getEntity(uniqueId));
    }

    @Override
    public MMOEntity<?> getEntity(Entity entity) {
        if (entity instanceof Player) {
            core.getUserManager().getPlayer((Player) entity);
        } else if (entity instanceof LivingEntity) {
            return new BukkitMMOEntity(entity);
        } else if (entity != null){
            return new BukkitMMOEntity(entity);
        }
        return null;
    }

    //TODO: Add this stuff to DSM, this location is temporary
    //TODO: even more temp here....
    private ShapelessRecipe getChimaeraWingRecipe() {
            Material ingredient = Material.matchMaterial(core.getConfigManager().getConfigItems().getChimaeraWingRecipeMats());

            if(ingredient == null)
                ingredient = Material.FEATHER;

            int amount = core.getConfigManager().getConfigItems().getChimaeraWingUseCost();

            ShapelessRecipe chimaeraWing = new ShapelessRecipe(new NamespacedKey(this, "Chimaera"), getChimaeraWing());
            chimaeraWing.addIngredient(amount, ingredient);
            return chimaeraWing;
    }


    //TODO: Add this stuff to DSM, this location is temporary
    public ItemStack getChimaeraWing() {
        Material ingredient = Material.matchMaterial(core.getConfigManager().getConfigItems().getChimaeraWingRecipeMats());

        if(ingredient == null)
            ingredient = Material.FEATHER;

        //TODO: Make it so Chimaera wing amounts made is customizeable
        ItemStack itemStack = new ItemStack(ingredient, 1);

        ItemMeta itemMeta = itemStack.getItemMeta();
        itemMeta.setDisplayName(ChatColor.GOLD + core.getLocaleManager().getString("Item.ChimaeraWing.Name"));

        List<String> itemLore = new ArrayList<>();
        itemLore.add("mcMMO Item");
        itemLore.add(core.getLocaleManager().getString("Item.ChimaeraWing.Lore"));
        itemMeta.setLore(itemLore);

        itemStack.setItemMeta(itemMeta);
        return itemStack;
    }

    private void registerEvents() {
        PluginManager pluginManager = getServer().getPluginManager();

        // Register events
        pluginManager.registerEvents(new PlayerListener(core), this);
        pluginManager.registerEvents(new BlockListener(core), this);
        pluginManager.registerEvents(new EntityListener(core), this);
        pluginManager.registerEvents(new InventoryListener(core), this);
        pluginManager.registerEvents(new SelfListener(core), this);
        pluginManager.registerEvents(new WorldListener(core), this);
    }


    @Override
    public void onLoad() {
        core.onLoad();
    }

    @Override
    public void onEnable() {
        core.onEnable();
    }
}
