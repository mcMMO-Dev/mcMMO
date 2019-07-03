package com.gmail.nossr50.util.commands;

import com.gmail.nossr50.commands.*;
import com.gmail.nossr50.commands.admin.ReloadLocaleCommand;
import com.gmail.nossr50.commands.chat.AdminChatCommand;
import com.gmail.nossr50.commands.chat.ChatSpyCommand;
import com.gmail.nossr50.commands.chat.PartyChatCommand;
import com.gmail.nossr50.commands.database.McremoveCommand;
import com.gmail.nossr50.commands.database.PurgeCommand;
import com.gmail.nossr50.commands.database.ShowDatabaseCommand;
import com.gmail.nossr50.commands.experience.AddLevelsCommand;
import com.gmail.nossr50.commands.experience.AddXPCommand;
import com.gmail.nossr50.commands.experience.SkillEditCommand;
import com.gmail.nossr50.commands.experience.SkillResetCommand;
import com.gmail.nossr50.commands.party.PartyCommand;
import com.gmail.nossr50.commands.party.teleport.PtpCommand;
import com.gmail.nossr50.commands.player.*;
import com.gmail.nossr50.commands.server.ReloadPluginCommand;
import com.gmail.nossr50.commands.skills.*;
import com.gmail.nossr50.datatypes.skills.PrimarySkillType;
import com.gmail.nossr50.mcMMO;
import com.gmail.nossr50.util.StringUtils;
import org.bukkit.command.PluginCommand;

import java.util.ArrayList;
import java.util.List;

public final class CommandRegistrationManager {
    private final mcMMO pluginRef;
    private String permissionsMessage = pluginRef.getLocaleManager().getString("mcMMO.NoPermission");

    public CommandRegistrationManager(mcMMO pluginRef) {
        this.pluginRef = pluginRef;
    }

    private void registerSkillCommands() {
        for (PrimarySkillType skill : PrimarySkillType.values()) {
            String commandName = skill.toString().toLowerCase();
            String localizedName = skill.getName().toLowerCase();

            PluginCommand command;

            command = pluginRef.getCommand(commandName);
            command.setDescription(pluginRef.getLocaleManager().getString("Commands.Description.Skill", StringUtils.getCapitalized(localizedName)));
            command.setPermission("mcmmo.commands." + commandName);
            command.setPermissionMessage(permissionsMessage);
            command.setUsage(pluginRef.getLocaleManager().getString("Commands.Usage.0", localizedName));
            command.setUsage(command.getUsage() + "\n" + pluginRef.getLocaleManager().getString("Commands.Usage.2", localizedName, "?", "[" + pluginRef.getLocaleManager().getString("Commands.Usage.Page") + "]"));

            switch (skill) {
                case ACROBATICS:
                    command.setExecutor(new AcrobaticsCommand());
                    break;

                case ALCHEMY:
//                    command.setExecutor(new AlchemyCommand());
                    break;

                case ARCHERY:
                    command.setExecutor(new ArcheryCommand());
                    break;

                case AXES:
                    command.setExecutor(new AxesCommand());
                    break;

                case EXCAVATION:
                    command.setExecutor(new ExcavationCommand());
                    break;

                case FISHING:
                    command.setExecutor(new FishingCommand());
                    break;

                case HERBALISM:
                    command.setExecutor(new HerbalismCommand());
                    break;

                case MINING:
                    command.setExecutor(new MiningCommand());
                    break;

                case REPAIR:
                    command.setExecutor(new RepairCommand());
                    break;

                case SALVAGE:
                    command.setExecutor(new SalvageCommand());
                    break;

                case SMELTING:
                    command.setExecutor(new SmeltingCommand());
                    break;

                case SWORDS:
                    command.setExecutor(new SwordsCommand());
                    break;

                case TAMING:
                    command.setExecutor(new TamingCommand());
                    break;

                case UNARMED:
                    command.setExecutor(new UnarmedCommand());
                    break;

                case WOODCUTTING:
                    command.setExecutor(new WoodcuttingCommand());
                    break;

                default:
                    break;
            }
        }
    }

    private void registerAddlevelsCommand() {
        PluginCommand command = pluginRef.getCommand("addlevels");
        command.setDescription(pluginRef.getLocaleManager().getString("Commands.Description.addlevels"));
        command.setPermission("mcmmo.commands.addlevels;mcmmo.commands.addlevels.others");
        command.setPermissionMessage(permissionsMessage);
        command.setUsage(pluginRef.getLocaleManager().getString("Commands.Usage.3", "addlevels", "[" + pluginRef.getLocaleManager().getString("Commands.Usage.Player") + "]", "<" + pluginRef.getLocaleManager().getString("Commands.Usage.Skill") + ">", "<" + pluginRef.getLocaleManager().getString("Commands.Usage.Level") + ">"));
        command.setExecutor(new AddLevelsCommand());
    }

    private void registerAddxpCommand() {
        PluginCommand command = pluginRef.getCommand("addxp");
        command.setDescription(pluginRef.getLocaleManager().getString("Commands.Description.addxp"));
        command.setPermission("mcmmo.commands.addxp;mcmmo.commands.addxp.others");
        command.setPermissionMessage(permissionsMessage);
        command.setUsage(pluginRef.getLocaleManager().getString("Commands.Usage.3", "addxp", "[" + pluginRef.getLocaleManager().getString("Commands.Usage.Player") + "]", "<" + pluginRef.getLocaleManager().getString("Commands.Usage.Skill") + ">", "<" + pluginRef.getLocaleManager().getString("Commands.Usage.XP") + ">"));
        command.setExecutor(new AddXPCommand());
    }

    private void registerMcgodCommand() {
        PluginCommand command = pluginRef.getCommand("mcgod");
        command.setDescription(pluginRef.getLocaleManager().getString("Commands.Description.mcgod"));
        command.setPermission("mcmmo.commands.mcgod;mcmmo.commands.mcgod.others");
        command.setPermissionMessage(permissionsMessage);
        command.setUsage(pluginRef.getLocaleManager().getString("Commands.Usage.1", "mcgod", "[" + pluginRef.getLocaleManager().getString("Commands.Usage.Player") + "]"));
        command.setExecutor(new GodModeCommand());
    }

    private void registerMmoInfoCommand() {
        PluginCommand command = pluginRef.getCommand("mmoinfo");
        command.setDescription(pluginRef.getLocaleManager().getString("Commands.Description.mmoinfo"));
        command.setPermission("mcmmo.commands.mmoinfo");
        command.setPermissionMessage(permissionsMessage);
        command.setUsage(pluginRef.getLocaleManager().getString("Commands.Usage.1", "mmoinfo", "[" + pluginRef.getLocaleManager().getString("Commands.Usage.SubSkill") + "]"));
        command.setExecutor(new MmoInfoCommand());
    }

    private void registerMcChatSpyCommand() {
        PluginCommand command = pluginRef.getCommand("mcchatspy");
        command.setDescription(pluginRef.getLocaleManager().getString("Commands.Description.mcchatspy"));
        command.setPermission("mcmmo.commands.mcchatspy;mcmmo.commands.mcchatspy.others");
        command.setPermissionMessage(permissionsMessage);
        command.setUsage(pluginRef.getLocaleManager().getString("Commands.Usage.1", "mcchatspy", "[" + pluginRef.getLocaleManager().getString("Commands.Usage.Player") + "]"));
        command.setExecutor(new ChatSpyCommand());
    }

    private void registerMcrefreshCommand() {
        PluginCommand command = pluginRef.getCommand("mcrefresh");
        command.setDescription(pluginRef.getLocaleManager().getString("Commands.Description.mcrefresh"));
        command.setPermission("mcmmo.commands.mcrefresh;mcmmo.commands.mcrefresh.others");
        command.setPermissionMessage(permissionsMessage);
        command.setUsage(pluginRef.getLocaleManager().getString("Commands.Usage.1", "mcrefresh", "[" + pluginRef.getLocaleManager().getString("Commands.Usage.Player") + "]"));
        command.setExecutor(new RefreshCooldownsCommand());
    }

    private void registerMmoeditCommand() {
        PluginCommand command = pluginRef.getCommand("mmoedit");
        command.setDescription(pluginRef.getLocaleManager().getString("Commands.Description.mmoedit"));
        command.setPermission("mcmmo.commands.mmoedit;mcmmo.commands.mmoedit.others");
        command.setPermissionMessage(permissionsMessage);
        command.setUsage(pluginRef.getLocaleManager().getString("Commands.Usage.3", "mmoedit", "[" + pluginRef.getLocaleManager().getString("Commands.Usage.Player") + "]", "<" + pluginRef.getLocaleManager().getString("Commands.Usage.Skill") + ">", "<" + pluginRef.getLocaleManager().getString("Commands.Usage.Level") + ">"));
        command.setExecutor(new SkillEditCommand());
    }

    private void registerMcmmoReloadCommand() {
        PluginCommand command = pluginRef.getCommand("mcmmoreload");
        command.setDescription(pluginRef.getLocaleManager().getString("Commands.Description.mcmmoreload"));
        command.setPermission("mcmmo.commands.reload");
        command.setPermissionMessage(permissionsMessage);
        command.setUsage(pluginRef.getLocaleManager().getString("Commands.Usage.0", "mcmmoreload"));
        command.setExecutor(new ReloadPluginCommand(pluginRef));
    }

    private void registerSkillresetCommand() {
        PluginCommand command = pluginRef.getCommand("skillreset");
        command.setDescription(pluginRef.getLocaleManager().getString("Commands.Description.skillreset"));
        command.setPermission("mcmmo.commands.skillreset;mcmmo.commands.skillreset.others"); // Only need the main ones, not the individual skill ones
        command.setPermissionMessage(permissionsMessage);
        command.setUsage(pluginRef.getLocaleManager().getString("Commands.Usage.2", "skillreset", "[" + pluginRef.getLocaleManager().getString("Commands.Usage.Player") + "]", "<" + pluginRef.getLocaleManager().getString("Commands.Usage.Skill") + ">"));
        command.setExecutor(new SkillResetCommand());
    }

    private void registerXprateCommand() {
        List<String> aliasList = new ArrayList<>();
        aliasList.add("mcxprate");

        PluginCommand command = pluginRef.getCommand("xprate");
        command.setDescription(pluginRef.getLocaleManager().getString("Commands.Description.xprate"));
        command.setPermission("mcmmo.commands.xprate;mcmmo.commands.xprate.reset;mcmmo.commands.xprate.set");
        command.setPermissionMessage(permissionsMessage);
        command.setUsage(pluginRef.getLocaleManager().getString("Commands.Usage.2", "xprate", "<" + pluginRef.getLocaleManager().getString("Commands.Usage.Rate") + ">", "<true|false>"));
        command.setUsage(command.getUsage() + "\n" + pluginRef.getLocaleManager().getString("Commands.Usage.1", "xprate", "reset"));
        command.setAliases(aliasList);
        command.setExecutor(new ExperienceRateCommand());
    }

    private void registerInspectCommand() {
        PluginCommand command = pluginRef.getCommand("inspect");
        command.setDescription(pluginRef.getLocaleManager().getString("Commands.Description.inspect"));
        command.setPermission("mcmmo.commands.inspect;mcmmo.commands.inspect.far;mcmmo.commands.inspect.offline");
        command.setPermissionMessage(permissionsMessage);
        command.setUsage(pluginRef.getLocaleManager().getString("Commands.Usage.1", "inspect", "<" + pluginRef.getLocaleManager().getString("Commands.Usage.Player") + ">"));
        command.setExecutor(new InspectCommand());
    }

    private void registerMccooldownCommand() {
        PluginCommand command = pluginRef.getCommand("mccooldown");
        command.setDescription(pluginRef.getLocaleManager().getString("Commands.Description.mccooldown"));
        command.setPermission("mcmmo.commands.mccooldown");
        command.setPermissionMessage(permissionsMessage);
        command.setUsage(pluginRef.getLocaleManager().getString("Commands.Usage.0", "mccooldowns"));
        command.setExecutor(new CooldownCommand());
    }

    private void registerMcabilityCommand() {
        PluginCommand command = pluginRef.getCommand("mcability");
        command.setDescription(pluginRef.getLocaleManager().getString("Commands.Description.mcability"));
        command.setPermission("mcmmo.commands.mcability;mcmmo.commands.mcability.others");
        command.setPermissionMessage(permissionsMessage);
        command.setUsage(pluginRef.getLocaleManager().getString("Commands.Usage.1", "mcability", "[" + pluginRef.getLocaleManager().getString("Commands.Usage.Player") + "]"));
        command.setExecutor(new AbilityToggleCommand());
    }

    private void registerMcmmoCommand() {
        PluginCommand command = pluginRef.getCommand("mcmmo");
        command.setDescription(pluginRef.getLocaleManager().getString("Commands.Description.mcmmo"));
        command.setPermission("mcmmo.commands.mcmmo.description;mcmmo.commands.mcmmo.help");
        command.setPermissionMessage(permissionsMessage);
        command.setUsage(pluginRef.getLocaleManager().getString("Commands.Usage.0", "mcmmo"));
        command.setUsage(command.getUsage() + "\n" + pluginRef.getLocaleManager().getString("Commands.Usage.1", "mcmmo", "help"));
        command.setExecutor(new McMMOCommand());
    }

    private void registerMcrankCommand() {
        PluginCommand command = pluginRef.getCommand("mcrank");
        command.setDescription(pluginRef.getLocaleManager().getString("Commands.Description.mcrank"));
        command.setPermission("mcmmo.commands.mcrank;mcmmo.commands.mcrank.others;mcmmo.commands.mcrank.others.far;mcmmo.commands.mcrank.others.offline");
        command.setPermissionMessage(permissionsMessage);
        command.setUsage(pluginRef.getLocaleManager().getString("Commands.Usage.1", "mcrank", "[" + pluginRef.getLocaleManager().getString("Commands.Usage.Player") + "]"));
        command.setExecutor(new RankCommand());
    }

    private void registerMcstatsCommand() {
        PluginCommand command = pluginRef.getCommand("mcstats");
        command.setDescription(pluginRef.getLocaleManager().getString("Commands.Description.mcstats"));
        command.setPermission("mcmmo.commands.mcstats");
        command.setPermissionMessage(permissionsMessage);
        command.setUsage(pluginRef.getLocaleManager().getString("Commands.Usage.0", "mcstats"));
        command.setExecutor(new SkillStatsCommand());
    }

    private void registerMctopCommand() {
        PluginCommand command = pluginRef.getCommand("mctop");
        command.setDescription(pluginRef.getLocaleManager().getString("Commands.Description.mctop"));
        command.setPermission("mcmmo.commands.mctop"); // Only need the main one, not the individual skill ones
        command.setPermissionMessage(permissionsMessage);
        command.setUsage(pluginRef.getLocaleManager().getString("Commands.Usage.2", "mctop", "[" + pluginRef.getLocaleManager().getString("Commands.Usage.Skill") + "]", "[" + pluginRef.getLocaleManager().getString("Commands.Usage.Page") + "]"));
        command.setExecutor(new LeaderboardCommand());
    }

    private void registerMcpurgeCommand() {
        PluginCommand command = pluginRef.getCommand("mcpurge");
        command.setDescription(pluginRef.getLocaleManager().getString("Commands.Description.mcpurge", pluginRef.getDatabaseCleaningSettings().getOldUserCutoffMonths()));
        command.setPermission("mcmmo.commands.mcpurge");
        command.setPermissionMessage(permissionsMessage);
        command.setUsage(pluginRef.getLocaleManager().getString("Commands.Usage.0", "mcpurge"));
        command.setExecutor(new PurgeCommand());
    }

    private void registerMcremoveCommand() {
        PluginCommand command = pluginRef.getCommand("mcremove");
        command.setDescription(pluginRef.getLocaleManager().getString("Commands.Description.mcremove"));
        command.setPermission("mcmmo.commands.mcremove");
        command.setPermissionMessage(permissionsMessage);
        command.setUsage(pluginRef.getLocaleManager().getString("Commands.Usage.1", "mcremove", "<" + pluginRef.getLocaleManager().getString("Commands.Usage.Player") + ">"));
        command.setExecutor(new McremoveCommand());
    }

    private void registerMmoshowdbCommand() {
        PluginCommand command = pluginRef.getCommand("mmoshowdb");
        command.setDescription(pluginRef.getLocaleManager().getString("Commands.Description.mmoshowdb"));
        command.setPermission("mcmmo.commands.mmoshowdb");
        command.setPermissionMessage(permissionsMessage);
        command.setUsage(pluginRef.getLocaleManager().getString("Commands.Usage.0", "mmoshowdb"));
        command.setExecutor(new ShowDatabaseCommand());
    }

    private void registerMcconvertCommand() {
        PluginCommand command = pluginRef.getCommand("mcconvert");
        command.setDescription(pluginRef.getLocaleManager().getString("Commands.Description.mcconvert"));
        command.setPermission("mcmmo.commands.mcconvert;mcmmo.commands.mcconvert.experience;mcmmo.commands.mcconvert.database");
        command.setPermissionMessage(permissionsMessage);
        command.setUsage(pluginRef.getLocaleManager().getString("Commands.Usage.2", "mcconvert", "database", "<flatfile|sql>"));
        command.setUsage(command.getUsage() + "\n" + pluginRef.getLocaleManager().getString("Commands.Usage.2", "mcconvert", "experience", "<linear|exponential>"));
        command.setExecutor(new ConvertCommand());
    }

    private void registerAdminChatCommand() {
        PluginCommand command = pluginRef.getCommand("adminchat");
        command.setDescription(pluginRef.getLocaleManager().getString("Commands.Description.adminchat"));
        command.setPermission("mcmmo.chat.adminchat");
        command.setPermissionMessage(permissionsMessage);
        command.setUsage(pluginRef.getLocaleManager().getString("Commands.Usage.0", "adminchat"));
        command.setUsage(command.getUsage() + "\n" + pluginRef.getLocaleManager().getString("Commands.Usage.1", "adminchat", "<on|off>"));
        command.setUsage(command.getUsage() + "\n" + pluginRef.getLocaleManager().getString("Commands.Usage.1", "adminchat", "<" + pluginRef.getLocaleManager().getString("Commands.Usage.Message") + ">"));
        command.setExecutor(new AdminChatCommand());
    }

    private void registerPartyChatCommand() {
        PluginCommand command = pluginRef.getCommand("partychat");
        command.setDescription(pluginRef.getLocaleManager().getString("Commands.Description.partychat"));
        command.setPermission("mcmmo.chat.partychat;mcmmo.commands.party");
        command.setPermissionMessage(permissionsMessage);
        command.setUsage(pluginRef.getLocaleManager().getString("Commands.Usage.0", "partychat"));
        command.setUsage(command.getUsage() + "\n" + pluginRef.getLocaleManager().getString("Commands.Usage.1", "partychat", "<on|off>"));
        command.setUsage(command.getUsage() + "\n" + pluginRef.getLocaleManager().getString("Commands.Usage.1", "partychat", "<" + pluginRef.getLocaleManager().getString("Commands.Usage.Message") + ">"));
        command.setExecutor(new PartyChatCommand());
    }

    private void registerPartyCommand() {
        PluginCommand command = pluginRef.getCommand("party");
        command.setDescription(pluginRef.getLocaleManager().getString("Commands.Description.party"));
        command.setPermission("mcmmo.commands.party;mcmmo.commands.party.accept;mcmmo.commands.party.create;mcmmo.commands.party.disband;" +
                "mcmmo.commands.party.xpshare;mcmmo.commands.party.invite;mcmmo.commands.party.itemshare;mcmmo.commands.party.join;" +
                "mcmmo.commands.party.kick;mcmmo.commands.party.lock;mcmmo.commands.party.owner;mcmmo.commands.party.password;" +
                "mcmmo.commands.party.quit;mcmmo.commands.party.rename;mcmmo.commands.party.unlock");
        command.setPermissionMessage(permissionsMessage);
        command.setExecutor(new PartyCommand());
    }

    private void registerPtpCommand() {
        PluginCommand command = pluginRef.getCommand("ptp");
        command.setDescription(pluginRef.getLocaleManager().getString("Commands.Description.ptp"));
        command.setPermission("mcmmo.commands.ptp"); // Only need the main one, not the individual ones for toggle/accept/acceptall
        command.setPermissionMessage(permissionsMessage);
        command.setUsage(pluginRef.getLocaleManager().getString("Commands.Usage.1", "ptp", "<" + pluginRef.getLocaleManager().getString("Commands.Usage.Player") + ">"));
        command.setUsage(command.getUsage() + "\n" + pluginRef.getLocaleManager().getString("Commands.Usage.1", "ptp", "<toggle|accept|acceptall>"));
        command.setExecutor(new PtpCommand());
    }

    /*private void registerHardcoreCommand() {
        PluginCommand command = mcMMO.p.getCommand("hardcore");
        command.setDescription(pluginRef.getLocaleManager().getString("Commands.Description.hardcore"));
        command.setPermission("mcmmo.commands.hardcore;mcmmo.commands.hardcore.toggle;mcmmo.commands.hardcore.modify");
        command.setPermissionMessage(permissionsMessage);
        command.setUsage(pluginRef.getLocaleManager().getString("Commands.Usage.1", "hardcore", "[on|off]"));
        command.setUsage(command.getUsage() + "\n" + pluginRef.getLocaleManager().getString("Commands.Usage.1", "hardcore", "<" + pluginRef.getLocaleManager().getString("Commands.Usage.Rate") + ">"));
        command.setExecutor(new HardcoreCommand());
    }

    private void registerVampirismCommand() {
        PluginCommand command = mcMMO.p.getCommand("vampirism");
        command.setDescription(pluginRef.getLocaleManager().getString("Commands.Description.vampirism"));
        command.setPermission("mcmmo.commands.vampirism;mcmmo.commands.vampirism.toggle;mcmmo.commands.vampirism.modify");
        command.setPermissionMessage(permissionsMessage);
        command.setUsage(pluginRef.getLocaleManager().getString("Commands.Usage.1", "vampirism", "[on|off]"));
        command.setUsage(command.getUsage() + "\n" + pluginRef.getLocaleManager().getString("Commands.Usage.1", "vampirism", "<" + pluginRef.getLocaleManager().getString("Commands.Usage.Rate") + ">"));
        command.setExecutor(new VampirismCommand());
    }*/

    private void registerMcnotifyCommand() {
        PluginCommand command = pluginRef.getCommand("mcnotify");
        command.setDescription(pluginRef.getLocaleManager().getString("Commands.Description.mcnotify"));
        command.setPermission("mcmmo.commands.mcnotify");
        command.setPermissionMessage(permissionsMessage);
        command.setUsage(pluginRef.getLocaleManager().getString("Commands.Usage.0", "mcnotify"));
        command.setExecutor(new ChatNotificationToggleCommand());
    }

    private void registerMHDCommand() {
        PluginCommand command = pluginRef.getCommand("mhd");
        command.setDescription("Resets all mob health bar settings for all players to the default"); //TODO: Localize
        command.setPermission("mcmmo.commands.mhd");
        command.setPermissionMessage(permissionsMessage);
        command.setUsage(pluginRef.getLocaleManager().getString("Commands.Usage.0", "mhd"));
        command.setExecutor(new ResetUserHealthBarSettingsCommand());
    }

    private void registerMcscoreboardCommand() {
        PluginCommand command = pluginRef.getCommand("mcscoreboard");
        command.setDescription("Change the current mcMMO scoreboard being displayed"); //TODO: Localize
        command.setPermission("mcmmo.commands.mcscoreboard");
        command.setPermissionMessage(permissionsMessage);
        command.setUsage(pluginRef.getLocaleManager().getString("Commands.Usage.1", "mcscoreboard", "<CLEAR | KEEP>"));
        command.setUsage(command.getUsage() + "\n" + pluginRef.getLocaleManager().getString("Commands.Usage.2", "mcscoreboard", "time", "<seconds>"));
        command.setExecutor(new ScoreboardCommand());
    }

    private void registerMcImportCommand() {
        PluginCommand command = pluginRef.getCommand("mcimport");
        command.setDescription("Import mod config files"); //TODO: Localize
        command.setPermission("mcmmo.commands.mcimport");
        command.setPermissionMessage(permissionsMessage);
        command.setUsage(pluginRef.getLocaleManager().getString("Commands.Usage.0", "mcimport"));
        command.setExecutor(new McImportCommand());
    }

    private void registerReloadLocaleCommand() {
        PluginCommand command = pluginRef.getCommand("mcmmoreloadlocale");
        command.setDescription("Reloads locale"); // TODO: Localize
        command.setPermission("mcmmo.commands.reloadlocale");
        command.setPermissionMessage(permissionsMessage);
        command.setUsage(pluginRef.getLocaleManager().getString("Commands.Usage.0", "mcmmoreloadlocale"));
        command.setExecutor(new ReloadLocaleCommand());
    }

    public void registerCommands() {
        // Generic Commands
        registerMmoInfoCommand();
        registerMcImportCommand();
        registerMcabilityCommand();
        registerMcgodCommand();
        registerMcChatSpyCommand();
        registerMcmmoCommand();
        registerMcnotifyCommand();
        registerMcrefreshCommand();
        registerMcscoreboardCommand();
        registerMHDCommand();
        registerXprateCommand();

        // Chat Commands
        registerPartyChatCommand();
        registerAdminChatCommand();

        // Database Commands
        registerMcpurgeCommand();
        registerMcremoveCommand();
        registerMmoshowdbCommand();
        registerMcconvertCommand();

        // Experience Commands
        registerAddlevelsCommand();
        registerAddxpCommand();
        registerMmoeditCommand();
        registerSkillresetCommand();

        // Hardcore Commands
        /*registerHardcoreCommand();
        registerVampirismCommand();*/

        // Party Commands
        registerPartyCommand();
        registerPtpCommand();

        // Player Commands
        registerInspectCommand();
        registerMccooldownCommand();
        registerMcrankCommand();
        registerMcstatsCommand();
        registerMctopCommand();

        // Skill Commands
        registerSkillCommands();

        //Config Commands
        registerMcmmoReloadCommand();
        // Admin commands
        registerReloadLocaleCommand();
    }
}
