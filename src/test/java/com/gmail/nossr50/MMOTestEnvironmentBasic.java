package com.gmail.nossr50;

import com.gmail.nossr50.commands.levelup.LevelUpCommandManager;
import com.gmail.nossr50.config.*;
import com.gmail.nossr50.config.experience.ExperienceConfig;
import com.gmail.nossr50.datatypes.experience.FormulaType;
import com.gmail.nossr50.datatypes.player.McMMOPlayer;
import com.gmail.nossr50.datatypes.player.PlayerProfile;
import com.gmail.nossr50.datatypes.skills.PrimarySkillType;
import com.gmail.nossr50.datatypes.skills.SubSkillType;
import com.gmail.nossr50.events.experience.McMMOPlayerLevelUpEvent;
import com.gmail.nossr50.events.experience.McMMOPlayerPreXpGainEvent;
import com.gmail.nossr50.listeners.SelfListener;
import com.gmail.nossr50.util.*;
import com.gmail.nossr50.util.blockmeta.ChunkManager;
import com.gmail.nossr50.util.experience.FormulaManager;
import com.gmail.nossr50.util.player.NotificationManager;
import com.gmail.nossr50.util.player.UserManager;
import com.gmail.nossr50.util.skills.RankUtils;
import com.gmail.nossr50.util.skills.SkillTools;
import org.bukkit.Bukkit;
import org.bukkit.Server;
import org.bukkit.World;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.plugin.PluginManager;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.mockito.MockedStatic;
import org.mockito.Mockito;

import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public abstract class MMOTestEnvironmentBasic {
    private final java.util.logging.Logger logger = java.util.logging.Logger.getLogger(MMOTestEnvironmentBasic.class.getName());
    protected MockedStatic<mcMMO> mockedMcMMO;
    protected MockedStatic<Bukkit> mockedBukkit;
    protected MockedStatic<ChatConfig> mockedChatConfig;
    protected MockedStatic<ExperienceConfig> experienceConfig;
    private MockedStatic<NotificationManager> mockedNotificationManager;
    protected MockedStatic<Permissions> mockedPermissions;
    protected MockedStatic<RankUtils> mockedRankUtils;
    protected MockedStatic<UserManager> mockedUserManager;
    protected MockedStatic<Misc> mockedMisc;
    protected MockedStatic<EventUtils> mockedEventUtils;
    protected SelfListener selfListener;
    protected TransientEntityTracker transientEntityTracker;
    protected AdvancedConfig advancedConfig;
    protected CommandOnLevelUpConfig commandOnLevelUpConfig;
    protected LevelUpCommandManager levelUpCommandManager;
    protected GeneralConfig generalConfig;
    protected RankConfig rankConfig;
    protected SkillTools skillTools;
    protected Server mockedServer;
    protected PluginManager pluginManager;
    protected World world;

    private FormulaManager formulaManager;

    /* Mocks */
    protected PlayerInventory playerInventory;

    protected ChunkManager chunkManager;

    protected ConsoleCommandSender consoleCommandSender;

    @BeforeEach
    void setUp() {
        mockBaseEnvironment();
    }

    @AfterEach
    void tearDown() {
        cleanupBaseEnvironment();
    }

    protected void mockBaseEnvironment() {
        mockedMcMMO = Mockito.mockStatic(mcMMO.class);
        mcMMO.p = mock(mcMMO.class);
        when(mcMMO.p.getLogger()).thenReturn(logger);

        // formula manager
        formulaManager = new FormulaManager(FormulaType.UNKNOWN);
        when(mcMMO.p.getFormulaManager()).thenReturn(formulaManager);

        // place store
        chunkManager = mock(ChunkManager.class);
        when(mcMMO.getPlaceStore()).thenReturn(chunkManager);

        // shut off mod manager for woodcutting
        when(mcMMO.getModManager()).thenReturn(mock(ModManager.class));
        when(mcMMO.getModManager().isCustomLog(any())).thenReturn(false);

        // chat config
        mockedChatConfig = Mockito.mockStatic(ChatConfig.class);
        when(ChatConfig.getInstance()).thenReturn(mock(ChatConfig.class));

        // general config
        mockGeneralConfig();

        // rank config
        mockRankConfig();

        // wire advanced config
        mockAdvancedConfig();

        // wire command level up config
        mockLevelUpCommand();

        // wire experience config
        mockExperienceConfig();

        // wire skill tools
        this.skillTools = Mockito.spy(new SkillTools(mcMMO.p));
        when(mcMMO.p.getSkillTools()).thenReturn(skillTools);

        this.transientEntityTracker = new TransientEntityTracker();
        when(mcMMO.getTransientEntityTracker()).thenReturn(transientEntityTracker);

        mockPermissions();

        mockNotifications();

        mockedRankUtils = Mockito.mockStatic(RankUtils.class);

        // wire server
        this.mockedServer = mock(Server.class);
        when(mcMMO.p.getServer()).thenReturn(mockedServer);

        // wire plugin manager
        this.pluginManager = mock(PluginManager.class);
        when(mockedServer.getPluginManager()).thenReturn(pluginManager);
        // Process level up events in our self listener
        Mockito.doAnswer(invocation -> {
            selfListener.onPlayerLevelUp(invocation.getArgument(0));
            return null;
        }).when(pluginManager).callEvent(any(McMMOPlayerLevelUpEvent.class));

        // Don't process pre-gain events
        Mockito.doAnswer((ignored) -> null).when(pluginManager).callEvent(any(McMMOPlayerPreXpGainEvent.class));

        // wire world
        this.world = mock(World.class);

        // wire Misc
        this.mockedMisc = Mockito.mockStatic(Misc.class);
        // Mockito.when(Misc.getBlockCenter(any())).thenReturn(new Location(world, 0, 0, 0));

        // wire user manager
        this.mockedUserManager = Mockito.mockStatic(UserManager.class);

        // Self listener
        selfListener = Mockito.spy(new SelfListener(mcMMO.p));

        // Console command sender
        consoleCommandSender = mock(ConsoleCommandSender.class);
        when(consoleCommandSender.getName()).thenReturn("CONSOLE");
        mockedBukkit = Mockito.mockStatic(Bukkit.class);
        when(Bukkit.getConsoleSender()).thenReturn(consoleCommandSender);
    }

    private void mockPermissions() {
        mockedPermissions = Mockito.mockStatic(Permissions.class);
        when(Permissions.isSubSkillEnabled(any(Player.class), any(SubSkillType.class))).thenReturn(true);
        when(Permissions.isSubSkillEnabled(any(Player.class), any(SubSkillType.class))).thenReturn(true);
        when(Permissions.skillEnabled(any(Player.class), any(PrimarySkillType.class))).thenReturn(true);
    }

    private void mockNotifications() {
        mockedNotificationManager = Mockito.mockStatic(NotificationManager.class);
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

        this.levelUpCommandManager = Mockito.spy(new LevelUpCommandManager(mcMMO.p));
        when(mcMMO.p.getLevelUpCommandManager()).thenReturn(levelUpCommandManager);
    }

    private void mockGeneralConfig() {
        generalConfig = mock(GeneralConfig.class);
        when(generalConfig.getLocale()).thenReturn("en_US");
        when(mcMMO.p.getGeneralConfig()).thenReturn(generalConfig);

        // Experience related
        when(generalConfig.getLevelCap(any(PrimarySkillType.class))).thenReturn(Integer.MAX_VALUE);
        when(generalConfig.getPowerLevelCap()).thenReturn(Integer.MAX_VALUE);
    }

    private void mockExperienceConfig() {
        experienceConfig = Mockito.mockStatic(ExperienceConfig.class);

        when(ExperienceConfig.getInstance()).thenReturn(mock(ExperienceConfig.class));

        // Combat
        when(ExperienceConfig.getInstance().getCombatXP(EntityType.COW)).thenReturn(1D);
        when(ExperienceConfig.getInstance().getFormulaType()).thenReturn(FormulaType.LINEAR);
        when(ExperienceConfig.getInstance().getBase(FormulaType.LINEAR)).thenReturn(1020);
        when(ExperienceConfig.getInstance().getMultiplier(FormulaType.LINEAR)).thenReturn(20D);
        when(ExperienceConfig.getInstance().getFormulaSkillModifier(any(PrimarySkillType.class))).thenReturn(1D);
        when(ExperienceConfig.getInstance().getExperienceGainsGlobalMultiplier()).thenReturn(1D);
        when(ExperienceConfig.getInstance().getExpModifier()).thenReturn(1D);
    }

    protected void cleanupBaseEnvironment() {
        // Clean up resources here if needed.
        if (mockedMcMMO != null) {
            mockedMcMMO.close();
        }
        if (mockedBukkit != null) {
            mockedBukkit.close();
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
        if (mockedNotificationManager != null) {
            mockedNotificationManager.close();
        }
    }

    protected McMMOPlayer getMMOPlayer(UUID playerUUID, String playerName, int startingLevel) {
        Player player = mock(Player.class);
        // Player UUID
        when(player.getUniqueId()).thenReturn(playerUUID);
        // Player name
        when(player.getName()).thenReturn(playerName);

        // Player Inventory
        this.playerInventory = mock(PlayerInventory.class);
        when(player.getInventory()).thenReturn(playerInventory);

        // Player Profile
        PlayerProfile playerProfile = Mockito.spy(new PlayerProfile(playerName, player.getUniqueId(), startingLevel));
        when(playerProfile.isLoaded()).thenReturn(true);
        // McMMOPlayer
        McMMOPlayer mmoPlayer = Mockito.spy(new McMMOPlayer(player, playerProfile));
        // Wire UserManager
        when(UserManager.getPlayer(player)).thenReturn(mmoPlayer);
        // Player is online
        when(player.isOnline()).thenReturn(true);

        return mmoPlayer;
    }

    protected McMMOPlayer getMMOPlayer(UUID playerUUID, String playerName) {
        return getMMOPlayer(playerUUID, playerName, 0);
    }
}
