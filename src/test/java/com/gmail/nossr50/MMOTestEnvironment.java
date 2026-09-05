package com.gmail.nossr50;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.anyInt;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.when;

import com.gmail.nossr50.api.exceptions.InvalidSkillException;
import com.gmail.nossr50.commands.levelup.LevelUpCommandManager;
import com.gmail.nossr50.config.AdvancedConfig;
import com.gmail.nossr50.config.ChatConfig;
import com.gmail.nossr50.config.CommandOnLevelUpConfig;
import com.gmail.nossr50.config.GeneralConfig;
import com.gmail.nossr50.config.RankConfig;
import com.gmail.nossr50.config.experience.ExperienceConfig;
import com.gmail.nossr50.config.party.PartyConfig;
import com.gmail.nossr50.datatypes.experience.FormulaType;
import com.gmail.nossr50.datatypes.player.McMMOPlayer;
import com.gmail.nossr50.datatypes.player.PlayerProfile;
import com.gmail.nossr50.datatypes.skills.PrimarySkillType;
import com.gmail.nossr50.datatypes.skills.SubSkillType;
import com.gmail.nossr50.util.EventUtils;
import com.gmail.nossr50.util.MaterialMapStore;
import com.gmail.nossr50.util.Misc;
import com.gmail.nossr50.util.Permissions;
import com.gmail.nossr50.util.TestPlayerMock;
import com.gmail.nossr50.util.TransientEntityTracker;
import com.gmail.nossr50.util.blockmeta.ChunkManager;
import com.gmail.nossr50.util.experience.FormulaManager;
import com.gmail.nossr50.util.platform.MinecraftGameVersion;
import com.gmail.nossr50.util.player.NotificationManager;
import com.gmail.nossr50.util.player.UserManager;
import com.gmail.nossr50.util.skills.RankUtils;
import com.gmail.nossr50.util.skills.SkillTools;
import com.gmail.nossr50.util.sounds.SoundManager;
import com.tcoded.folialib.FoliaLib;
import com.tcoded.folialib.impl.PlatformScheduler;
import com.tcoded.folialib.wrapper.task.WrappedTask;
import java.io.File;
import java.io.IOException;
import java.util.UUID;
import java.util.function.Consumer;
import java.util.logging.Logger;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Server;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.inventory.ItemFactory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.plugin.PluginManager;
import org.mockito.MockedStatic;
import org.mockito.Mockito;

public abstract class MMOTestEnvironment {
    protected MockedStatic<Bukkit> mockedBukkit;
    protected MockedStatic<mcMMO> mockedMcMMO;
    protected MockedStatic<ChatConfig> mockedChatConfig;
    protected MockedStatic<ExperienceConfig> experienceConfig;
    protected ExperienceConfig experienceConfigInstance;
    protected MockedStatic<Permissions> mockedPermissions;
    protected MockedStatic<RankUtils> mockedRankUtils;
    protected MockedStatic<UserManager> mockedUserManager;
    protected MockedStatic<Misc> mockedMisc;
    protected MockedStatic<EventUtils> mockedEventUtils;
    protected MockedStatic<NotificationManager> notificationManager;
    protected MockedStatic<SoundManager> mockedSoundManager;
    protected TransientEntityTracker transientEntityTracker;
    protected AdvancedConfig advancedConfig;
    protected CommandOnLevelUpConfig commandOnLevelUpConfig;
    protected LevelUpCommandManager levelUpCommandManager;
    protected PartyConfig partyConfig;
    protected GeneralConfig generalConfig;
    protected RankConfig rankConfig;
    protected SkillTools skillTools;
    protected Server server;
    protected PluginManager pluginManager;
    protected World world;

    /* Mocks */
    protected Player player;

    protected UUID playerUUID = UUID.randomUUID();
    protected ItemStack itemInMainHand;

    protected PlayerInventory playerInventory;
    protected PlayerProfile playerProfile;
    protected McMMOPlayer mmoPlayer;
    protected ItemFactory itemFactory;

    protected ChunkManager chunkManager;
    protected MaterialMapStore materialMapStore;

    protected MinecraftGameVersion minecraftGameVersion;
    protected File testDataFolder;
    protected FoliaLib foliaLib;
    private FormulaManager formulaManager;

    protected void mockBaseEnvironment(Logger logger) throws InvalidSkillException {
        mockedMcMMO = mockStatic(mcMMO.class);
        mcMMO.p = mock(mcMMO.class);
        when(mcMMO.p.getLogger()).thenReturn(logger);
        when(mcMMO.p.getName()).thenReturn("mcMMO");
        try {
            testDataFolder = java.nio.file.Files.createTempDirectory("mcmmo-test-data-").toFile();
        } catch (IOException e) {
            throw new RuntimeException("Failed to create temp test data folder", e);
        }
        when(mcMMO.p.getDataFolder()).thenReturn(testDataFolder);
        // Keep locale file creation inside the temp folder instead of the working directory
        when(mcMMO.getLocalesDirectory()).thenReturn(testDataFolder.getPath() + File.separator);

        // Game version
        minecraftGameVersion = mock(MinecraftGameVersion.class);
        when(minecraftGameVersion.isAtLeast(anyInt(), anyInt(), anyInt())).thenReturn(true);
        when(mcMMO.getMinecraftGameVersion()).thenReturn(minecraftGameVersion);

        // formula manager
        formulaManager = new FormulaManager(FormulaType.UNKNOWN);
        when(mcMMO.getFormulaManager()).thenReturn(formulaManager);

        // FoliaLib scheduler that runs tasks inline so scheduled work is observable in tests
        mockFoliaLib();

        // place store
        chunkManager = mock(ChunkManager.class);
        when(mcMMO.getUserBlockTracker()).thenReturn(chunkManager);

        // chat config
        mockedChatConfig = mockStatic(ChatConfig.class);
        when(ChatConfig.getInstance()).thenReturn(mock(ChatConfig.class));

        // general config
        mockGeneralConfig();

        // party config
        mockPartyConfig();

        // rank config
        mockRankConfig();

        // wire advanced config
        mockAdvancedConfig();

        // wire command on level up config
        mockLevelUpCommand();

        // wire experience config
        mockExperienceConfig();

        // wire skill tools
        this.skillTools = new SkillTools(mcMMO.p);
        when(mcMMO.p.getSkillTools()).thenReturn(skillTools);

        this.transientEntityTracker = new TransientEntityTracker();
        when(mcMMO.getTransientEntityTracker()).thenReturn(transientEntityTracker);

        mockPermissions();

        mockedRankUtils = mockStatic(RankUtils.class);

        // wire server
        this.server = mock(Server.class);
        when(mcMMO.p.getServer()).thenReturn(server);

        // wire Bukkit
        mockedBukkit = mockStatic(Bukkit.class);
        // Whichever test triggers org.bukkit.Registry initialization welds the lookup
        // results into its constants for every later test in the JVM, so every harness
        // test must serve registry lookups through the shared bootstrap
        TestRegistryBootstrap.bootstrap(mockedBukkit);
        when(Bukkit.getItemFactory()).thenReturn(itemFactory);
        itemFactory = mock(ItemFactory.class);

        // wire Bukkit call to get server
        when(Bukkit.getServer()).thenReturn(server);

        // wire plugin manager
        this.pluginManager = mock(PluginManager.class);
        // wire server -> plugin manager
        when(server.getPluginManager()).thenReturn(pluginManager);
        // wire Bukkit -> plugin manager
        when(Bukkit.getPluginManager()).thenReturn(pluginManager);
        // return the argument provided when call event is invoked on plugin manager mock
        doAnswer(invocation -> {
            Object[] args = invocation.getArguments();
            return args[0];
        }).when(pluginManager).callEvent(any(Event.class));

        // wire world
        this.world = mock(World.class);

        // wire Misc
        this.mockedMisc = mockStatic(Misc.class);
        when(Misc.getBlockCenter(any(Block.class))).thenReturn(new Location(world, 0, 0, 0));
        when(Misc.getBlockCenter(any(BlockState.class))).thenReturn(new Location(world, 0, 0, 0));

        // setup player and player related mocks after everything else
        this.player = mock(Player.class);
        when(player.getUniqueId()).thenReturn(playerUUID);
        when(player.isValid()).thenReturn(true);
        when(player.isOnline()).thenReturn(true);
        // health
        when(player.getHealth()).thenReturn(20D);
        // wire inventory
        this.playerInventory = mock(PlayerInventory.class);
        when(player.getInventory()).thenReturn(playerInventory);
        // player location
        Location playerLocation = mock(Location.class);
        Block playerLocationBlock = mock(Block.class);
        when(player.getLocation()).thenReturn(playerLocation);
        when(playerLocation.getBlock()).thenReturn(playerLocationBlock);
        // when(playerLocationBlock.getType()).thenReturn(Material.AIR);
        // pdc
        PersistentDataContainer pdc = mock(PersistentDataContainer.class);
        when(player.getPersistentDataContainer()).thenReturn(pdc);

        // PlayerProfile and McMMOPlayer are partially mocked
        playerProfile = new PlayerProfile("testPlayer", player.getUniqueId(), 0);
        mmoPlayer = Mockito.spy(new McMMOPlayer(player, playerProfile));

        // wire user manager
        this.mockedUserManager = mockStatic(UserManager.class);
        when(UserManager.getPlayer(player)).thenReturn(mmoPlayer);

        this.materialMapStore = new MaterialMapStore();
        when(mcMMO.getMaterialMapStore()).thenReturn(materialMapStore);

        // wire notification manager
        notificationManager = mockStatic(NotificationManager.class);

        // wire sound manager
        mockedSoundManager = mockStatic(SoundManager.class);
    }

    /**
     * Creates an additional fully wired player mock, independent of the shared {@link #player}.
     * Useful for tests that need named players, custom starting levels, or several players.
     */
    protected TestPlayerMock mockPlayer() {
        final UUID uuid = UUID.randomUUID();
        return mockPlayer(uuid, uuid.toString(), 0);
    }

    protected TestPlayerMock mockPlayer(UUID uuid, String playerName, int startingLevel) {
        final Player player = mock(Player.class);
        when(player.getUniqueId()).thenReturn(uuid);
        when(player.isValid()).thenReturn(true);
        when(player.isOnline()).thenReturn(true);
        // Player name
        when(player.getName()).thenReturn(playerName);
        // health
        when(player.getHealth()).thenReturn(20D);

        // inventory
        final PlayerInventory playerInventory = mock(PlayerInventory.class);
        when(player.getInventory()).thenReturn(playerInventory);

        // player location
        final Location playerLocation = mock(Location.class);
        final Block playerLocationBlock = mock(Block.class);
        when(player.getLocation()).thenReturn(playerLocation);
        when(playerLocation.getBlock()).thenReturn(playerLocationBlock);

        // pdc
        PersistentDataContainer pdc = mock(PersistentDataContainer.class);
        when(player.getPersistentDataContainer()).thenReturn(pdc);

        // PlayerProfile and McMMOPlayer are partially mocked
        final PlayerProfile playerProfile = spy(new PlayerProfile(playerName, uuid,
                startingLevel));
        when(playerProfile.isLoaded()).thenReturn(true);
        final McMMOPlayer mmoPlayer = spy(new McMMOPlayer(player, playerProfile));
        when(UserManager.getPlayer(player)).thenReturn(mmoPlayer);

        // the XP gain path consults this permission per skill
        when(Permissions.skillEnabled(eq(player), any(PrimarySkillType.class))).thenReturn(true);

        return new TestPlayerMock(player, playerInventory, playerLocation, playerProfile,
                mmoPlayer);
    }

    @SuppressWarnings("unchecked")
    private void mockFoliaLib() {
        foliaLib = mock(FoliaLib.class);
        final PlatformScheduler scheduler = mock(PlatformScheduler.class);
        when(foliaLib.getScheduler()).thenReturn(scheduler);
        when(scheduler.runNextTick(any(Consumer.class))).thenAnswer(invocation -> {
            invocation.getArgument(0, Consumer.class).accept(mock(WrappedTask.class));
            return null;
        });
        when(scheduler.runAtEntity(any(Entity.class), any(Consumer.class)))
                .thenAnswer(invocation -> {
                    invocation.getArgument(1, Consumer.class).accept(mock(WrappedTask.class));
                    return null;
                });
        when(mcMMO.p.getFoliaLib()).thenReturn(foliaLib);
    }

    private void mockPermissions() {
        mockedPermissions = mockStatic(Permissions.class);
        when(Permissions.isSubSkillEnabled(any(Player.class), any(SubSkillType.class))).thenReturn(
                true);
        when(Permissions.canUseSubSkill(any(Player.class), any(SubSkillType.class))).thenReturn(
                true);
        when(Permissions.isSubSkillEnabled(any(Player.class), any(SubSkillType.class))).thenReturn(
                true);
        when(Permissions.canUseSubSkill(any(Player.class), any(SubSkillType.class))).thenReturn(
                true);
        when(Permissions.lucky(player, PrimarySkillType.WOODCUTTING)).thenReturn(
                false); // player is not lucky
    }

    private void mockRankConfig() {
        rankConfig = mock(RankConfig.class);
    }

    private void mockAdvancedConfig() {
        this.advancedConfig = mock(AdvancedConfig.class);
        when(mcMMO.p.getAdvancedConfig()).thenReturn(advancedConfig);
    }

    private void mockLevelUpCommand() {
        this.commandOnLevelUpConfig = mock(CommandOnLevelUpConfig.class);
        when(mcMMO.p.getCommandOnLevelUpConfig()).thenReturn(commandOnLevelUpConfig);

        this.levelUpCommandManager = spy(new LevelUpCommandManager(mcMMO.p));
        when(mcMMO.p.getLevelUpCommandManager()).thenReturn(levelUpCommandManager);
    }

    private void mockGeneralConfig() {
        generalConfig = mock(GeneralConfig.class);
        when(generalConfig.getTreeFellerThreshold()).thenReturn(100);
        when(generalConfig.getDoubleDropsEnabled(PrimarySkillType.WOODCUTTING,
                Material.OAK_LOG)).thenReturn(true);
        when(generalConfig.getLocale()).thenReturn("en_US");
        when(mcMMO.p.getGeneralConfig()).thenReturn(generalConfig);
        // Allows pseudo functional testing of experience gains up to high levels
        when(generalConfig.getPowerLevelCap()).thenReturn(Integer.MAX_VALUE);
        when(generalConfig.getLevelCap(any(PrimarySkillType.class)))
                .thenReturn(Integer.MAX_VALUE);
    }

    private void mockPartyConfig() {
        partyConfig = mock(PartyConfig.class);
        when(partyConfig.isPartyEnabled()).thenReturn(false);
        when(mcMMO.p.getPartyConfig()).thenReturn(partyConfig);
    }

    private void mockExperienceConfig() {
        experienceConfig = mockStatic(ExperienceConfig.class);
        experienceConfigInstance = mock(ExperienceConfig.class);
        when(ExperienceConfig.getInstance()).thenReturn(experienceConfigInstance);

        // Combat
        when(experienceConfigInstance.getCombatXP("Cow")).thenReturn(1D);

        // Experience formula wiring so XP gains can level players up in tests
        when(experienceConfigInstance.getFormulaType()).thenReturn(FormulaType.LINEAR);
        when(experienceConfigInstance.getFormulaSkillModifier(any(PrimarySkillType.class)))
                .thenReturn(1D);
        when(experienceConfigInstance.getBase(any(FormulaType.class)))
                .thenReturn(1000);
        when(experienceConfigInstance.getExponent(any())).thenReturn(1D);
        when(experienceConfigInstance.getExperienceGainsGlobalMultiplier())
                .thenReturn(1D);
        when(experienceConfigInstance.getMultiplier(any(FormulaType.class)))
                .thenReturn(1D);
        // Conversion
        when(experienceConfigInstance.getExpModifier())
                .thenReturn(1D);
    }

    protected void cleanUpStaticMocks() {
        // Clean up resources here if needed.
        if (mockedMcMMO != null) {
            mockedMcMMO.close();
        }
        if (experienceConfig != null) {
            experienceConfig.close();
        }
        if (mockedChatConfig != null) {
            mockedChatConfig.close();
        }
        if (mockedPermissions != null) {
            mockedPermissions.close();
        }
        if (mockedRankUtils != null) {
            mockedRankUtils.close();
        }
        if (mockedUserManager != null) {
            mockedUserManager.close();
        }
        if (mockedMisc != null) {
            mockedMisc.close();
        }
        if (mockedEventUtils != null) {
            mockedEventUtils.close();
        }
        if (mockedBukkit != null) {
            mockedBukkit.close();
        }
        if (notificationManager != null) {
            notificationManager.close();
        }
        if (mockedSoundManager != null) {
            mockedSoundManager.close();
        }
        if (testDataFolder != null) {
            deleteRecursively(testDataFolder);
            testDataFolder = null;
        }
    }

    private static void deleteRecursively(final File file) {
        if (file.isDirectory()) {
            final File[] children = file.listFiles();
            if (children != null) {
                for (final File child : children) {
                    deleteRecursively(child);
                }
            }
        }
        file.delete();
    }
}
