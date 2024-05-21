package com.gmail.nossr50.commands;

import co.aikar.commands.BukkitCommandIssuer;
import co.aikar.commands.BukkitCommandManager;
import co.aikar.commands.ConditionFailedException;
import com.gmail.nossr50.commands.chat.AdminChatCommand;
import com.gmail.nossr50.commands.chat.PartyChatCommand;
import com.gmail.nossr50.commands.skills.PowerLevelCommand;
import com.gmail.nossr50.config.ChatConfig;
import com.gmail.nossr50.datatypes.chat.ChatChannel;
import com.gmail.nossr50.datatypes.player.McMMOPlayer;
import com.gmail.nossr50.locale.LocaleLoader;
import com.gmail.nossr50.mcMMO;
import com.gmail.nossr50.util.Permissions;
import com.gmail.nossr50.util.player.UserManager;
import org.bukkit.entity.Player;
import org.bukkit.permissions.Permissible;
import org.jetbrains.annotations.NotNull;

/*
 * For now this class will only handle ACF converted commands, all other commands will be handled elsewhere
 */
public class CommandManager {
    public static final @NotNull String MMO_DATA_LOADED = "mmoDataLoaded";

    //CHAT
    public static final @NotNull String ADMIN_CONDITION = "adminCondition";
    public static final @NotNull String PARTY_CONDITION = "partyCondition";

    //SKILLS
    public static final @NotNull String POWER_LEVEL_CONDITION = "powerLevelCondition";

    private final @NotNull mcMMO pluginRef;
    private final @NotNull BukkitCommandManager bukkitCommandManager;

    public CommandManager(@NotNull mcMMO pluginRef) {
        this.pluginRef = pluginRef;
        bukkitCommandManager = new BukkitCommandManager(pluginRef);

        registerConditions();
        registerCommands();
    }

    private void registerCommands() {
        registerSkillCommands(); //TODO: Implement other skills not just power level
        registerChatCommands();
    }

    private void registerSkillCommands() {
        if (mcMMO.p.getGeneralConfig().isMasterySystemEnabled()) {
            bukkitCommandManager.registerCommand(new PowerLevelCommand(pluginRef));
        }
    }

    /**
     * Registers chat commands if the chat system is enabled
     */
    private void registerChatCommands() {
        if (ChatConfig.getInstance().isChatEnabled()) {
            if (ChatConfig.getInstance().isChatChannelEnabled(ChatChannel.ADMIN)) {
                bukkitCommandManager.registerCommand(new AdminChatCommand(pluginRef));
            }
            if (pluginRef.getPartyConfig().isPartyEnabled() && ChatConfig.getInstance().isChatChannelEnabled(ChatChannel.PARTY)) {
                bukkitCommandManager.registerCommand(new PartyChatCommand(pluginRef));
            }
        }
    }

    public void registerConditions() {
        registerChatCommandConditions(); //Chat Commands
        registerSkillConditions();
    }

    private void registerSkillConditions() {
        bukkitCommandManager.getCommandConditions().addCondition(POWER_LEVEL_CONDITION, (context) -> {
            BukkitCommandIssuer issuer = context.getIssuer();

            if (issuer.getIssuer() instanceof Player) {
                validateLoadedData(issuer.getPlayer());
            } else {
                throw new ConditionFailedException(LocaleLoader.getString("Commands.NoConsole"));
            }
        });
    }

    private void registerChatCommandConditions() {
        // Method or Class based - Can only be used on methods
        bukkitCommandManager.getCommandConditions().addCondition(ADMIN_CONDITION, (context) -> {
            BukkitCommandIssuer issuer = context.getIssuer();

            if (issuer.getIssuer() instanceof Player) {
                validateLoadedData(issuer.getPlayer());
                validateAdmin(issuer.getPlayer());
            }
        });

        bukkitCommandManager.getCommandConditions().addCondition(MMO_DATA_LOADED, (context) -> {
            BukkitCommandIssuer bukkitCommandIssuer = context.getIssuer();

            if (bukkitCommandIssuer.getIssuer() instanceof Player) {
                validateLoadedData(bukkitCommandIssuer.getPlayer());
            }
        });

        bukkitCommandManager.getCommandConditions().addCondition(PARTY_CONDITION, (context) -> {
            BukkitCommandIssuer bukkitCommandIssuer = context.getIssuer();

            if (bukkitCommandIssuer.getIssuer() instanceof Player) {
                validateLoadedData(bukkitCommandIssuer.getPlayer());
                validatePlayerParty(bukkitCommandIssuer.getPlayer());
                //TODO: Is there even a point in validating permission? look into this later
                validatePermission("mcmmo.chat.partychat", bukkitCommandIssuer.getPlayer());
            }
        });
    }

    private void validatePermission(@NotNull String permissionNode, @NotNull Permissible permissible) {
        if (!permissible.hasPermission(permissionNode)) {
            throw new ConditionFailedException(LocaleLoader.getString("mcMMO.NoPermission"));
        }
    }


    public void validateAdmin(@NotNull Player player) {
        if (!player.isOp() && !Permissions.adminChat(player)) {
            throw new ConditionFailedException("You are lacking the correct permissions to use this command.");
        }
    }

    public void validateLoadedData(@NotNull Player player) {
        if (UserManager.getPlayer(player) == null) {
            throw new ConditionFailedException(LocaleLoader.getString("Profile.PendingLoad"));
        }
    }

    public void validatePlayerParty(@NotNull Player player) {
        McMMOPlayer mmoPlayer = UserManager.getPlayer(player);

        if (!pluginRef.getPartyConfig().isPartyEnabled() || mmoPlayer.getParty() == null) {
            throw new ConditionFailedException(LocaleLoader.getString("Commands.Party.None"));
        }
    }

    public @NotNull BukkitCommandManager getBukkitCommandManager() {
        return bukkitCommandManager;
    }
}
