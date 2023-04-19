package com.gmail.nossr50;

import com.gmail.nossr50.config.AdvancedConfig;
import com.gmail.nossr50.config.ChatConfig;
import com.gmail.nossr50.config.GeneralConfig;
import com.gmail.nossr50.config.RankConfig;
import com.gmail.nossr50.config.experience.ExperienceConfig;
import com.gmail.nossr50.datatypes.player.McMMOPlayer;
import com.gmail.nossr50.datatypes.player.PlayerProfile;
import com.gmail.nossr50.datatypes.skills.PrimarySkillType;
import com.gmail.nossr50.datatypes.skills.SubSkillType;
import com.gmail.nossr50.util.*;
import com.gmail.nossr50.util.blockmeta.ChunkManager;
import com.gmail.nossr50.util.player.UserManager;
import com.gmail.nossr50.util.skills.RankUtils;
import com.gmail.nossr50.util.skills.SkillTools;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Server;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.plugin.PluginManager;
import org.mockito.MockedStatic;
import org.mockito.Mockito;

import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;

public abstract class MMOTestEnvironment {
    private final java.util.logging.Logger logger = java.util.logging.Logger.getLogger(MMOTestEnvironment.class.getName());
    protected MockedStatic<mcMMO> mockedMcMMO;
    protected MockedStatic<ChatConfig> mockedChatConfig;
    protected MockedStatic<ExperienceConfig> experienceConfig;
    protected MockedStatic<Permissions> mockedPermissions;
    protected MockedStatic<RankUtils> mockedRankUtils;
    protected MockedStatic<UserManager> mockedUserManager;
    protected MockedStatic<Misc> mockedMisc;
    protected MockedStatic<SkillTools> mockedSkillTools;
    protected MockedStatic<EventUtils> mockedEventUtils;
    protected TransientEntityTracker transientEntityTracker;
    protected AdvancedConfig advancedConfig;
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

    protected void mockBaseEnvironment() {
        mockedMcMMO = Mockito.mockStatic(mcMMO.class);
        mcMMO.p = Mockito.mock(mcMMO.class);
        Mockito.when(mcMMO.p.getLogger()).thenReturn(logger);

        // place store
        chunkManager = Mockito.mock(ChunkManager.class);
        Mockito.when(mcMMO.getPlaceStore()).thenReturn(chunkManager);

        // shut off mod manager for woodcutting
        Mockito.when(mcMMO.getModManager()).thenReturn(Mockito.mock(ModManager.class));
        Mockito.when(mcMMO.getModManager().isCustomLog(any())).thenReturn(false);

        // chat config
        mockedChatConfig = Mockito.mockStatic(ChatConfig.class);
        Mockito.when(ChatConfig.getInstance()).thenReturn(Mockito.mock(ChatConfig.class));

        // general config
        mockGeneralConfig();

        // rank config
        mockRankConfig();

        // wire advanced config
        mockAdvancedConfig();

        // wire experience config
        mockExperienceConfig();

        // wire skill tools
        this.skillTools = new SkillTools(mcMMO.p);
        Mockito.when(mcMMO.p.getSkillTools()).thenReturn(skillTools);

        this.transientEntityTracker = new TransientEntityTracker();
        Mockito.when(mcMMO.getTransientEntityTracker()).thenReturn(transientEntityTracker);

        mockPermissions();

        mockedRankUtils = Mockito.mockStatic(RankUtils.class);

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
        Mockito.when(player.getInventory()).thenReturn(playerInventory);

        // PlayerProfile and McMMOPlayer are partially mocked
        playerProfile = new PlayerProfile("testPlayer", player.getUniqueId(), 0);
        mmoPlayer = Mockito.spy(new McMMOPlayer(player, playerProfile));

        // wire user manager
        this.mockedUserManager = Mockito.mockStatic(UserManager.class);
        Mockito.when(UserManager.getPlayer(player)).thenReturn(mmoPlayer);
    }

    private void mockPermissions() {
        mockedPermissions = Mockito.mockStatic(Permissions.class);
        Mockito.when(Permissions.isSubSkillEnabled(any(Player.class), any(SubSkillType.class))).thenReturn(true);
        Mockito.when(Permissions.canUseSubSkill(any(Player.class), any(SubSkillType.class))).thenReturn(true);
        Mockito.when(Permissions.isSubSkillEnabled(any(Player.class), any(SubSkillType.class))).thenReturn(true);
        Mockito.when(Permissions.canUseSubSkill(any(Player.class), any(SubSkillType.class))).thenReturn(true);
        Mockito.when(Permissions.lucky(player, PrimarySkillType.WOODCUTTING)).thenReturn(false); // player is not lucky
    }

    private void mockRankConfig() {
        rankConfig = Mockito.mock(RankConfig.class);
    }

    private void mockAdvancedConfig() {
        this.advancedConfig = Mockito.mock(AdvancedConfig.class);
        Mockito.when(mcMMO.p.getAdvancedConfig()).thenReturn(advancedConfig);
    }

    private void mockGeneralConfig() {
        generalConfig = Mockito.mock(GeneralConfig.class);
        Mockito.when(generalConfig.getTreeFellerThreshold()).thenReturn(100);
        Mockito.when(generalConfig.getDoubleDropsEnabled(PrimarySkillType.WOODCUTTING, Material.OAK_LOG)).thenReturn(true);
        Mockito.when(generalConfig.getLocale()).thenReturn("en_US");
        Mockito.when(mcMMO.p.getGeneralConfig()).thenReturn(generalConfig);
    }

    private void mockExperienceConfig() {
        experienceConfig = Mockito.mockStatic(ExperienceConfig.class);

        Mockito.when(ExperienceConfig.getInstance()).thenReturn(Mockito.mock(ExperienceConfig.class));

        // Combat
        Mockito.when(ExperienceConfig.getInstance().getCombatXP("Cow")).thenReturn(1D);
    }

    protected void cleanupBaseEnvironment() {
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
    }
}
