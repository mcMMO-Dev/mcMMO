package com.gmail.nossr50;

import com.gmail.nossr50.commands.levelup.LevelUpCommandManager;
import com.gmail.nossr50.config.*;
import com.gmail.nossr50.config.experience.ExperienceConfig;
import com.gmail.nossr50.datatypes.player.McMMOPlayer;
import com.gmail.nossr50.datatypes.player.PlayerProfile;
import com.gmail.nossr50.datatypes.skills.SubSkillType;
import com.gmail.nossr50.listeners.SelfListener;
import com.gmail.nossr50.util.*;
import com.gmail.nossr50.util.blockmeta.ChunkManager;
import com.gmail.nossr50.util.player.UserManager;
import com.gmail.nossr50.util.skills.RankUtils;
import com.gmail.nossr50.util.skills.SkillTools;
import org.bukkit.Bukkit;
import org.bukkit.Server;
import org.bukkit.World;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.plugin.PluginManager;
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
    protected MockedStatic<Permissions> mockedPermissions;
    protected MockedStatic<RankUtils> mockedRankUtils;
    protected MockedStatic<UserManager> mockedUserManager;
    protected MockedStatic<Misc> mockedMisc;
    protected MockedStatic<SkillTools> mockedSkillTools;
    protected MockedStatic<EventUtils> mockedEventUtils;
    protected SelfListener selfListener;
    protected TransientEntityTracker transientEntityTracker;
    protected AdvancedConfig advancedConfig;
    protected CommandOnLevelUpConfig commandOnLevelUpConfig;
    protected LevelUpCommandManager levelUpCommandManager;
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
    protected String playerName = "testPlayer";

    protected ChunkManager chunkManager;

    protected ConsoleCommandSender consoleCommandSender;

    protected void mockBaseEnvironment() {
        mockedMcMMO = Mockito.mockStatic(mcMMO.class);
        mcMMO.p = mock(mcMMO.class);
        when(mcMMO.p.getLogger()).thenReturn(logger);

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
        this.skillTools = new SkillTools(mcMMO.p);
        when(mcMMO.p.getSkillTools()).thenReturn(skillTools);

        this.transientEntityTracker = new TransientEntityTracker();
        when(mcMMO.getTransientEntityTracker()).thenReturn(transientEntityTracker);

        mockPermissions();

        mockedRankUtils = Mockito.mockStatic(RankUtils.class);

        // wire server
        this.server = mock(Server.class);
        when(mcMMO.p.getServer()).thenReturn(server);

        // wire plugin manager
        this.pluginManager = mock(PluginManager.class);
        when(server.getPluginManager()).thenReturn(pluginManager);

        // wire world
        this.world = mock(World.class);

        // wire Misc
        this.mockedMisc = Mockito.mockStatic(Misc.class);
        // Mockito.when(Misc.getBlockCenter(any())).thenReturn(new Location(world, 0, 0, 0));

        // setup player and player related mocks after everything else
        this.player = mock(Player.class);
        when(player.getUniqueId()).thenReturn(playerUUID);

        // wire inventory
        this.playerInventory = mock(PlayerInventory.class);
        when(player.getInventory()).thenReturn(playerInventory);

        // PlayerProfile and McMMOPlayer are partially mocked
        playerProfile = Mockito.spy(new PlayerProfile("testPlayer", player.getUniqueId(), 0));
        when(playerProfile.isLoaded()).thenReturn(true);
        mmoPlayer = Mockito.spy(new McMMOPlayer(player, playerProfile));

        // wire user manager
        this.mockedUserManager = Mockito.mockStatic(UserManager.class);
        when(UserManager.getPlayer(player)).thenReturn(mmoPlayer);

        // Self listener
        selfListener = Mockito.spy(new SelfListener(mcMMO.p));

        // Player online status
        when(player.isOnline()).thenReturn(true);

        // Console command sender
        consoleCommandSender = mock(ConsoleCommandSender.class);
        when(consoleCommandSender.getName()).thenReturn("CONSOLE");
        mockedBukkit = Mockito.mockStatic(Bukkit.class);
        when(Bukkit.getConsoleSender()).thenReturn(consoleCommandSender);
    }

    private void mockPermissions() {
        mockedPermissions = Mockito.mockStatic(Permissions.class);
        when(Permissions.isSubSkillEnabled(any(Player.class), any(SubSkillType.class))).thenReturn(true);
        // Mockito.when(Permissions.canUseSubSkill(any(Player.class), any(SubSkillType.class))).thenReturn(true);
        when(Permissions.isSubSkillEnabled(any(Player.class), any(SubSkillType.class))).thenReturn(true);
        // Mockito.when(Permissions.canUseSubSkill(any(Player.class), any(SubSkillType.class))).thenReturn(true);
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
    }

    private void mockExperienceConfig() {
        experienceConfig = Mockito.mockStatic(ExperienceConfig.class);

        when(ExperienceConfig.getInstance()).thenReturn(mock(ExperienceConfig.class));

        // Combat
        when(ExperienceConfig.getInstance().getCombatXP(EntityType.COW)).thenReturn(1D);
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
    }
}
