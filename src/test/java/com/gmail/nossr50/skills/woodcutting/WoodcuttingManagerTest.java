package com.gmail.nossr50.skills.woodcutting;

import com.gmail.nossr50.config.AdvancedConfig;
import com.gmail.nossr50.config.ChatConfig;
import com.gmail.nossr50.config.GeneralConfig;
import com.gmail.nossr50.config.RankConfig;
import com.gmail.nossr50.datatypes.player.McMMOPlayer;
import com.gmail.nossr50.datatypes.player.PlayerProfile;
import com.gmail.nossr50.datatypes.skills.PrimarySkillType;
import com.gmail.nossr50.datatypes.skills.SubSkillType;
import com.gmail.nossr50.mcMMO;
import com.gmail.nossr50.util.EventUtils;
import com.gmail.nossr50.util.Misc;
import com.gmail.nossr50.util.Permissions;
import com.gmail.nossr50.util.TransientEntityTracker;
import com.gmail.nossr50.util.player.UserManager;
import com.gmail.nossr50.util.skills.RankUtils;
import com.gmail.nossr50.util.skills.SkillTools;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Server;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.plugin.PluginManager;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.mockito.Mockito;

import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;

class WoodcuttingManagerTest {
    private static final java.util.logging.Logger logger = java.util.logging.Logger.getLogger(WoodcuttingManagerTest.class.getName());
    private MockedStatic<mcMMO> mockedMcMMO;
    private MockedStatic<ChatConfig> mockedChatConfig;
    private MockedStatic<Permissions> mockedPermissions;
    private MockedStatic<RankUtils> mockedRankUtils;
    private MockedStatic<UserManager> mockedUserManager;
    private MockedStatic<Misc> mockedMisc;
    private MockedStatic<SkillTools> mockedSkillTools;
    private MockedStatic<EventUtils> mockedEventUtils;
    private TransientEntityTracker transientEntityTracker;
    private AdvancedConfig advancedConfig;
    private GeneralConfig generalConfig;
    private RankConfig rankConfig;
    private SkillTools skillTools;
    private Server server;
    private PluginManager pluginManager;
    private World world;

    private WoodcuttingManager woodcuttingManager;

    /* Mocks */
    Player player;

    UUID playerUUID = UUID.randomUUID();
    ItemStack itemInMainHand;

    PlayerInventory playerInventory;
    PlayerProfile playerProfile;
    McMMOPlayer mmoPlayer;
    String playerName = "testPlayer";

    @BeforeEach
    void setUp() {
        mockedMcMMO = Mockito.mockStatic(mcMMO.class);
        mcMMO.p = Mockito.mock(mcMMO.class);
        Mockito.when(mcMMO.p.getLogger()).thenReturn(logger);

        // chat config
        mockedChatConfig = Mockito.mockStatic(ChatConfig.class);
        Mockito.when(ChatConfig.getInstance()).thenReturn(Mockito.mock(ChatConfig.class));

        // general config
        generalConfig = Mockito.mock(GeneralConfig.class);
        Mockito.when(generalConfig.getTreeFellerThreshold()).thenReturn(100);
        Mockito.when(generalConfig.getDoubleDropsEnabled(PrimarySkillType.WOODCUTTING, Material.OAK_LOG)).thenReturn(true);
        Mockito.when(generalConfig.getLocale()).thenReturn("en_US");
        Mockito.when(mcMMO.p.getGeneralConfig()).thenReturn(generalConfig);

        // rank config
        rankConfig = Mockito.mock(RankConfig.class);
        Mockito.when(rankConfig.getSubSkillUnlockLevel(SubSkillType.WOODCUTTING_HARVEST_LUMBER, 1)).thenReturn(1);

        // wire advanced config
        this.advancedConfig = Mockito.mock(AdvancedConfig.class);
        Mockito.when(advancedConfig.getMaximumProbability(SubSkillType.WOODCUTTING_HARVEST_LUMBER)).thenReturn(100D);
        Mockito.when(advancedConfig.getMaximumProbability(SubSkillType.WOODCUTTING_CLEAN_CUTS)).thenReturn(10D);
        Mockito.when(advancedConfig.getMaxBonusLevel(SubSkillType.WOODCUTTING_HARVEST_LUMBER)).thenReturn(1000);
        Mockito.when(advancedConfig.getMaxBonusLevel(SubSkillType.WOODCUTTING_CLEAN_CUTS)).thenReturn(10000);
        Mockito.when(mcMMO.p.getAdvancedConfig()).thenReturn(advancedConfig);

        // wire skill tools
        this.skillTools = new SkillTools(mcMMO.p);
        Mockito.when(mcMMO.p.getSkillTools()).thenReturn(skillTools);

        this.transientEntityTracker = new TransientEntityTracker();
        Mockito.when(mcMMO.getTransientEntityTracker()).thenReturn(transientEntityTracker);

        mockedPermissions = Mockito.mockStatic(Permissions.class);
        Mockito.when(Permissions.isSubSkillEnabled(any(Player.class), any(SubSkillType.class))).thenReturn(true);
        Mockito.when(Permissions.canUseSubSkill(any(Player.class), any(SubSkillType.class))).thenReturn(true);
        Mockito.when(Permissions.isSubSkillEnabled(any(Player.class), any(SubSkillType.class))).thenReturn(true);
        Mockito.when(Permissions.canUseSubSkill(any(Player.class), any(SubSkillType.class))).thenReturn(true);
        Mockito.when(Permissions.lucky(player, PrimarySkillType.WOODCUTTING)).thenReturn(false); // player is not lucky

        mockedRankUtils = Mockito.mockStatic(RankUtils.class);
        Mockito.when(RankUtils.getRankUnlockLevel(SubSkillType.WOODCUTTING_HARVEST_LUMBER, 1)).thenReturn(1); // needed?
        Mockito.when(RankUtils.getRankUnlockLevel(SubSkillType.WOODCUTTING_CLEAN_CUTS, 1)).thenReturn(1000); // needed?
        Mockito.when(RankUtils.hasReachedRank(eq(1), any(Player.class), eq(SubSkillType.WOODCUTTING_HARVEST_LUMBER))).thenReturn(true);
        Mockito.when(RankUtils.hasReachedRank(eq(1), any(Player.class), eq(SubSkillType.WOODCUTTING_CLEAN_CUTS))).thenReturn(true);

        // wire server
        this.server = Mockito.mock(Server.class);
        Mockito.when(mcMMO.p.getServer()).thenReturn(server);

        // wire plugin manager
        this.pluginManager = Mockito.mock(PluginManager.class);
        Mockito.when(server.getPluginManager()).thenReturn(pluginManager);

        // wire world
        this.world = Mockito.mock(World.class);

        // wire Misc
        this.mockedMisc = Mockito.mockStatic(Misc.class);
        Mockito.when(Misc.getBlockCenter(any())).thenReturn(new Location(world, 0, 0, 0));

        // setup player and player related mocks after everything else
        this.player = Mockito.mock(Player.class);
        Mockito.when(player.getUniqueId()).thenReturn(playerUUID);

        // wire inventory
        this.playerInventory = Mockito.mock(PlayerInventory.class);
        this.itemInMainHand = new ItemStack(Material.DIAMOND_AXE);
        Mockito.when(player.getInventory()).thenReturn(playerInventory);
        Mockito.when(playerInventory.getItemInMainHand()).thenReturn(itemInMainHand);

        // PlayerProfile and McMMOPlayer are partially mocked
        playerProfile = new PlayerProfile("testPlayer", player.getUniqueId(), 0);
        mmoPlayer = Mockito.spy(new McMMOPlayer(player, playerProfile));

        // wire user manager
        this.mockedUserManager = Mockito.mockStatic(UserManager.class);
        Mockito.when(UserManager.getPlayer(player)).thenReturn(mmoPlayer);

        // Set up spy for WoodcuttingManager
        woodcuttingManager = Mockito.spy(new WoodcuttingManager(mmoPlayer));
    }

    @AfterEach
    void tearDown() {
        // Clean up resources here if needed.
        if (mockedMcMMO != null) {
            mockedMcMMO.close();
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

    @Test
    void harvestLumberShouldDoubleDrop() {
        mmoPlayer.modifySkill(PrimarySkillType.WOODCUTTING, 1000);

        BlockState blockState = Mockito.mock(BlockState.class);
        Block block = Mockito.mock(Block.class);
        // wire block
        Mockito.when(blockState.getBlock()).thenReturn(block);

        Mockito.when(blockState.getBlock().getDrops(any())).thenReturn(null);
        Mockito.when(blockState.getType()).thenReturn(Material.OAK_LOG);
        woodcuttingManager.processBonusDropCheck(blockState);

        // verify bonus drops were spawned
        Mockito.verify(woodcuttingManager, Mockito.times(1)).spawnHarvestLumberBonusDrops(blockState);
    }


    @Test
    void harvestLumberShouldNotDoubleDrop() {
        mmoPlayer.modifySkill(PrimarySkillType.WOODCUTTING, 0);

        BlockState blockState = Mockito.mock(BlockState.class);
        Block block = Mockito.mock(Block.class);
        // wire block
        Mockito.when(blockState.getBlock()).thenReturn(block);

        Mockito.when(blockState.getBlock().getDrops(any())).thenReturn(null);
        Mockito.when(blockState.getType()).thenReturn(Material.OAK_LOG);
        woodcuttingManager.processBonusDropCheck(blockState);

        // verify bonus drops were not spawned
        Mockito.verify(woodcuttingManager, Mockito.times(0)).spawnHarvestLumberBonusDrops(blockState);
    }
}
