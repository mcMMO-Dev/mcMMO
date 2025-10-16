package com.gmail.nossr50;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.anyInt;
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
import com.gmail.nossr50.util.compat.CompatibilityManager;
import com.gmail.nossr50.util.experience.FormulaManager;
import com.gmail.nossr50.util.platform.MinecraftGameVersion;
import com.gmail.nossr50.util.player.NotificationManager;
import com.gmail.nossr50.util.player.UserManager;
import com.gmail.nossr50.util.skills.RankUtils;
import com.gmail.nossr50.util.skills.SkillTools;
import com.gmail.nossr50.util.sounds.SoundManager;
import java.util.UUID;
import java.util.logging.Logger;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Server;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemFactory;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.plugin.PluginManager;
import org.mockito.MockedStatic;

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
    private FormulaManager formulaManager;
    protected ItemFactory itemFactory;
    protected ChunkManager chunkManager;
    protected MaterialMapStore materialMapStore;
    protected CompatibilityManager compatibilityManager;

    protected void mockBaseEnvironment(Logger logger) throws InvalidSkillException {
        compatibilityManager = mock(CompatibilityManager.class);
        final MinecraftGameVersion minecraftGameVersion = mock(MinecraftGameVersion.class);
        when(compatibilityManager.getMinecraftGameVersion()).thenReturn(minecraftGameVersion);
        // TODO: We should change minecraftGameVersion to be a passed in parameter instead of always returning true
        when(minecraftGameVersion.isAtLeast(anyInt(), anyInt(), anyInt())).thenReturn(true);
        mockedMcMMO = mockStatic(mcMMO.class);
        when(mcMMO.getCompatibilityManager()).thenReturn(compatibilityManager);
        mcMMO.p = mock(mcMMO.class);
        when(mcMMO.p.getLogger()).thenReturn(logger);

        // formula manager
        formulaManager = new FormulaManager(FormulaType.UNKNOWN);
        when(mcMMO.p.getFormulaManager()).thenReturn(formulaManager);

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

        // wire command level up config
        mockLevelUpCommand();

        // wire experience config
        mockExperienceConfig();

        // wire skill tools
        this.skillTools = new SkillTools(mcMMO.p);
        when(mcMMO.p.getSkillTools()).thenReturn(skillTools);

        this.transientEntityTracker = new TransientEntityTracker();
        when(mcMMO.getTransientEntityTracker()).thenReturn(transientEntityTracker);

        mockedRankUtils = mockStatic(RankUtils.class);

        // wire server
        this.server = mock(Server.class);
        when(mcMMO.p.getServer()).thenReturn(server);

        // wire Bukkit
        mockedBukkit = mockStatic(Bukkit.class);
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

        // wire world
        this.world = mock(World.class);

        // wire Misc
        this.mockedMisc = mockStatic(Misc.class);
        when(Misc.getBlockCenter(any(Block.class))).thenReturn(new Location(world, 0, 0, 0));
        when(Misc.getBlockCenter(any(BlockState.class))).thenReturn(new Location(world, 0, 0, 0));

        // wire user manager
        this.mockedUserManager = mockStatic(UserManager.class);

        this.materialMapStore = new MaterialMapStore();
        when(mcMMO.getMaterialMapStore()).thenReturn(materialMapStore);

        // wire notification manager
        notificationManager = mockStatic(NotificationManager.class);

        // wire sound manager
        mockedSoundManager = mockStatic(SoundManager.class);
    }

    private void mockPermissions(Player player) {
        if (mockedPermissions == null) {
            mockedPermissions = mockStatic(Permissions.class);
        }
        when(Permissions.isSubSkillEnabled(eq(player), any(SubSkillType.class))).thenReturn(true);
        when(Permissions.canUseSubSkill(eq(player), any(SubSkillType.class))).thenReturn(true);
        when(Permissions.isSubSkillEnabled(eq(player), any(SubSkillType.class))).thenReturn(true);
        when(Permissions.canUseSubSkill(eq(player), any(SubSkillType.class))).thenReturn(true);
        when(Permissions.skillEnabled(eq(player), any(PrimarySkillType.class))).thenReturn(true);
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
        when(mcMMO.p.getGeneralConfig()).thenReturn(generalConfig);
        when(generalConfig.getTreeFellerThreshold()).thenReturn(100);
        when(generalConfig.getDoubleDropsEnabled(PrimarySkillType.WOODCUTTING,
                Material.OAK_LOG)).thenReturn(true);
        when(generalConfig.getLocale()).thenReturn("en_US");
        // Allows pseudo functional testing of experience gains
        when(generalConfig.getPowerLevelCap())
                .thenReturn(Integer.MAX_VALUE);
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

        // PlayerProfile and McMMOPlayer are partially mocked
        final PlayerProfile playerProfile = spy(new PlayerProfile("testPlayer", player.getUniqueId(),
                startingLevel));
        when(playerProfile.isLoaded()).thenReturn(true);
        final McMMOPlayer mmoPlayer = spy(new McMMOPlayer(player, playerProfile));
        when(UserManager.getPlayer(player)).thenReturn(mmoPlayer);

        // Permissions
        mockPermissions(player);
        // TODO: Move this to the woodcutting tests
        when(Permissions.lucky(player, PrimarySkillType.WOODCUTTING)).thenReturn(
                false); // player is not lucky

        return new TestPlayerMock(player, playerInventory, playerLocation, playerProfile,
                mmoPlayer);
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
    }
}
