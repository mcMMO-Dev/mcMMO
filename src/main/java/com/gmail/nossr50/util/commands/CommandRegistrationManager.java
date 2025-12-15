package com.gmail.nossr50.util.commands;

import com.gmail.nossr50.commands.McabilityCommand;
import com.gmail.nossr50.commands.McconvertCommand;
import com.gmail.nossr50.commands.McgodCommand;
import com.gmail.nossr50.commands.McmmoCommand;
import com.gmail.nossr50.commands.McnotifyCommand;
import com.gmail.nossr50.commands.McrefreshCommand;
import com.gmail.nossr50.commands.McscoreboardCommand;
import com.gmail.nossr50.commands.XprateCommand;
import com.gmail.nossr50.commands.admin.CompatibilityCommand;
import com.gmail.nossr50.commands.admin.McmmoReloadLocaleCommand;
import com.gmail.nossr50.commands.admin.PlayerDebugCommand;
import com.gmail.nossr50.commands.chat.McChatSpy;
import com.gmail.nossr50.commands.database.McpurgeCommand;
import com.gmail.nossr50.commands.database.McremoveCommand;
import com.gmail.nossr50.commands.database.MmoshowdbCommand;
import com.gmail.nossr50.commands.experience.AddlevelsCommand;
import com.gmail.nossr50.commands.experience.AddxpCommand;
import com.gmail.nossr50.commands.experience.MmoeditCommand;
import com.gmail.nossr50.commands.experience.SkillresetCommand;
import com.gmail.nossr50.commands.party.PartyCommand;
import com.gmail.nossr50.commands.party.teleport.PtpCommand;
import com.gmail.nossr50.commands.player.InspectCommand;
import com.gmail.nossr50.commands.player.McRankCommand;
import com.gmail.nossr50.commands.player.McTopCommand;
import com.gmail.nossr50.commands.player.MccooldownCommand;
import com.gmail.nossr50.commands.player.McstatsCommand;
import com.gmail.nossr50.commands.player.XPBarCommand;
import com.gmail.nossr50.commands.skills.AcrobaticsCommand;
import com.gmail.nossr50.commands.skills.AlchemyCommand;
import com.gmail.nossr50.commands.skills.ArcheryCommand;
import com.gmail.nossr50.commands.skills.AxesCommand;
import com.gmail.nossr50.commands.skills.CrossbowsCommand;
import com.gmail.nossr50.commands.skills.ExcavationCommand;
import com.gmail.nossr50.commands.skills.FishingCommand;
import com.gmail.nossr50.commands.skills.HerbalismCommand;
import com.gmail.nossr50.commands.skills.MacesCommand;
import com.gmail.nossr50.commands.skills.MiningCommand;
import com.gmail.nossr50.commands.skills.MmoInfoCommand;
import com.gmail.nossr50.commands.skills.RepairCommand;
import com.gmail.nossr50.commands.skills.SalvageCommand;
import com.gmail.nossr50.commands.skills.SmeltingCommand;
import com.gmail.nossr50.commands.skills.SpearsCommand;
import com.gmail.nossr50.commands.skills.SwordsCommand;
import com.gmail.nossr50.commands.skills.TamingCommand;
import com.gmail.nossr50.commands.skills.TridentsCommand;
import com.gmail.nossr50.commands.skills.UnarmedCommand;
import com.gmail.nossr50.commands.skills.WoodcuttingCommand;
import com.gmail.nossr50.datatypes.skills.PrimarySkillType;
import com.gmail.nossr50.locale.LocaleLoader;
import com.gmail.nossr50.mcMMO;
import com.gmail.nossr50.util.text.StringUtils;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import org.bukkit.command.PluginCommand;

public final class CommandRegistrationManager {
    private CommandRegistrationManager() {
    }

    private static final String permissionsMessage = LocaleLoader.getString("mcMMO.NoPermission");

    private static void registerSkillCommands() {
        for (PrimarySkillType primarySkillType : PrimarySkillType.values()) {
            if (primarySkillType == PrimarySkillType.SPEARS
                    && !mcMMO.getCompatibilityManager().getMinecraftGameVersion()
                    .isAtLeast(1, 21, 11)) {
                continue;
            }

            if (primarySkillType == PrimarySkillType.MACES
                    && !mcMMO.getCompatibilityManager().getMinecraftGameVersion()
                    .isAtLeast(1, 21, 0)) {
                continue;
            }

            final String commandName = primarySkillType.toString().toLowerCase(Locale.ENGLISH);
            final String localizedName = mcMMO.p.getSkillTools()
                    .getLocalizedSkillName(primarySkillType).toLowerCase(Locale.ENGLISH);

            final PluginCommand command = mcMMO.p.getCommand(commandName);
            if (command == null) {
                mcMMO.p.getLogger().severe("Command not found: " + commandName);
                continue;
            }

            command.setDescription(LocaleLoader.getString("Commands.Description.Skill",
                    StringUtils.getCapitalized(localizedName)));
            command.setPermission("mcmmo.commands." + commandName);
            command.setPermissionMessage(permissionsMessage);
            command.setUsage(LocaleLoader.getString("Commands.Usage.0", commandName));
            command.setUsage(command.getUsage() + "\n" + LocaleLoader.getString("Commands.Usage.2",
                    commandName, "?", "[" + LocaleLoader.getString("Commands.Usage.Page") + "]"));

            switch (primarySkillType) {
                case ACROBATICS -> command.setExecutor(new AcrobaticsCommand());
                case ALCHEMY -> command.setExecutor(new AlchemyCommand());
                case ARCHERY -> command.setExecutor(new ArcheryCommand());
                case AXES -> command.setExecutor(new AxesCommand());
                case CROSSBOWS -> command.setExecutor(new CrossbowsCommand());
                case EXCAVATION -> command.setExecutor(new ExcavationCommand());
                case FISHING -> command.setExecutor(new FishingCommand());
                case HERBALISM -> command.setExecutor(new HerbalismCommand());
                case MACES -> command.setExecutor(new MacesCommand());
                case MINING -> command.setExecutor(new MiningCommand());
                case REPAIR -> command.setExecutor(new RepairCommand());
                case SALVAGE -> command.setExecutor(new SalvageCommand());
                case SMELTING -> command.setExecutor(new SmeltingCommand());
                case SPEARS -> command.setExecutor(new SpearsCommand());
                case SWORDS -> command.setExecutor(new SwordsCommand());
                case TAMING -> command.setExecutor(new TamingCommand());
                case TRIDENTS -> command.setExecutor(new TridentsCommand());
                case UNARMED -> command.setExecutor(new UnarmedCommand());
                case WOODCUTTING -> command.setExecutor(new WoodcuttingCommand());
                default -> throw new IllegalStateException("Unexpected value: " + primarySkillType);
            }
        }
    }

    private static void registerAddlevelsCommand() {
        PluginCommand command = mcMMO.p.getCommand("addlevels");
        command.setDescription(LocaleLoader.getString("Commands.Description.addlevels"));
        command.setPermission("mcmmo.commands.addlevels;mcmmo.commands.addlevels.others");
        command.setPermissionMessage(permissionsMessage);
        command.setUsage(LocaleLoader.getString("Commands.Usage.3.XP", "addlevels",
                "[" + LocaleLoader.getString("Commands.Usage.Player") + "]",
                "<" + LocaleLoader.getString("Commands.Usage.Skill") + ">",
                "<" + LocaleLoader.getString("Commands.Usage.Level") + ">"));
        command.setExecutor(new AddlevelsCommand());
    }

    private static void registerAddxpCommand() {
        PluginCommand command = mcMMO.p.getCommand("addxp");
        command.setDescription(LocaleLoader.getString("Commands.Description.addxp"));
        command.setPermission("mcmmo.commands.addxp;mcmmo.commands.addxp.others");
        command.setPermissionMessage(permissionsMessage);
        command.setUsage(LocaleLoader.getString("Commands.Usage.3.XP", "addxp",
                "[" + LocaleLoader.getString("Commands.Usage.Player") + "]",
                "<" + LocaleLoader.getString("Commands.Usage.Skill") + ">",
                "<" + LocaleLoader.getString("Commands.Usage.XP") + ">"));
        command.setExecutor(new AddxpCommand());
    }

    private static void registerMcgodCommand() {
        PluginCommand command = mcMMO.p.getCommand("mcgod");
        command.setDescription(LocaleLoader.getString("Commands.Description.mcgod"));
        command.setPermission("mcmmo.commands.mcgod;mcmmo.commands.mcgod.others");
        command.setPermissionMessage(permissionsMessage);
        command.setUsage(LocaleLoader.getString("Commands.Usage.1", "mcgod",
                "[" + LocaleLoader.getString("Commands.Usage.Player") + "]"));
        command.setExecutor(new McgodCommand());
    }

    private static void registerMmoInfoCommand() {
        PluginCommand command = mcMMO.p.getCommand("mmoinfo");
        command.setDescription(LocaleLoader.getString("Commands.Description.mmoinfo"));
        command.setPermission("mcmmo.commands.mmoinfo");
        command.setPermissionMessage(permissionsMessage);
        command.setUsage(LocaleLoader.getString("Commands.Usage.1", "mmoinfo",
                "[" + LocaleLoader.getString("Commands.Usage.SubSkill") + "]"));
        command.setExecutor(new MmoInfoCommand());
    }

    private static void registerMmoDebugCommand() {
        PluginCommand command = mcMMO.p.getCommand("mmodebug");
        command.setDescription(LocaleLoader.getString("Commands.Description.mmodebug"));
        command.setPermission(null); //No perm required to save support headaches
        command.setPermissionMessage(permissionsMessage);
        command.setUsage(LocaleLoader.getString("Commands.Usage.0", "mmodebug"));
        command.setExecutor(new PlayerDebugCommand());
    }

    private static void registerMcChatSpyCommand() {
        PluginCommand command = mcMMO.p.getCommand("mcchatspy");
        command.setDescription(LocaleLoader.getString("Commands.Description.mcchatspy"));
        command.setPermission("mcmmo.commands.mcchatspy;mcmmo.commands.mcchatspy.others");
        command.setPermissionMessage(permissionsMessage);
        command.setUsage(LocaleLoader.getString("Commands.Usage.1", "mcchatspy",
                "[" + LocaleLoader.getString("Commands.Usage.Player") + "]"));
        command.setExecutor(new McChatSpy());
    }

    private static void registerMcrefreshCommand() {
        PluginCommand command = mcMMO.p.getCommand("mcrefresh");
        command.setDescription(LocaleLoader.getString("Commands.Description.mcrefresh"));
        command.setPermission("mcmmo.commands.mcrefresh;mcmmo.commands.mcrefresh.others");
        command.setPermissionMessage(permissionsMessage);
        command.setUsage(LocaleLoader.getString("Commands.Usage.1", "mcrefresh",
                "[" + LocaleLoader.getString("Commands.Usage.Player") + "]"));
        command.setExecutor(new McrefreshCommand());
    }

    private static void registerMmoeditCommand() {
        PluginCommand command = mcMMO.p.getCommand("mmoedit");
        command.setDescription(LocaleLoader.getString("Commands.Description.mmoedit"));
        command.setPermission("mcmmo.commands.mmoedit;mcmmo.commands.mmoedit.others");
        command.setPermissionMessage(permissionsMessage);
        command.setUsage(LocaleLoader.getString("Commands.Usage.3.XP", "mmoedit",
                "[" + LocaleLoader.getString("Commands.Usage.Player") + "]",
                "<" + LocaleLoader.getString("Commands.Usage.Skill") + ">",
                "<" + LocaleLoader.getString("Commands.Usage.Level") + ">"));
        command.setExecutor(new MmoeditCommand());
    }

    private static void registerSkillresetCommand() {
        PluginCommand command = mcMMO.p.getCommand("skillreset");
        command.setDescription(LocaleLoader.getString("Commands.Description.skillreset"));
        command.setPermission(
                "mcmmo.commands.skillreset;mcmmo.commands.skillreset.others"); // Only need the main ones, not the individual skill ones
        command.setPermissionMessage(permissionsMessage);
        command.setUsage(LocaleLoader.getString("Commands.Usage.2", "skillreset",
                "[" + LocaleLoader.getString("Commands.Usage.Player") + "]",
                "<" + LocaleLoader.getString("Commands.Usage.Skill") + ">"));
        command.setExecutor(new SkillresetCommand());
    }

    private static void registerXprateCommand() {
        List<String> aliasList = new ArrayList<>();
        aliasList.add("mcxprate");

        PluginCommand command = mcMMO.p.getCommand("xprate");
        command.setDescription(LocaleLoader.getString("Commands.Description.xprate"));
        command.setPermission(
                "mcmmo.commands.xprate;mcmmo.commands.xprate.reset;mcmmo.commands.xprate.set");
        command.setPermissionMessage(permissionsMessage);
        command.setUsage(LocaleLoader.getString("Commands.Usage.2", "xprate",
                "<" + LocaleLoader.getString("Commands.Usage.Rate") + ">", "<true|false>"));
        command.setUsage(
                command.getUsage() + "\n" + LocaleLoader.getString("Commands.Usage.1", "xprate",
                        "reset"));
        command.setAliases(aliasList);
        command.setExecutor(new XprateCommand());
    }

    private static void registerInspectCommand() {
        PluginCommand command = mcMMO.p.getCommand("inspect");
        command.setDescription(LocaleLoader.getString("Commands.Description.inspect"));
        command.setPermission(
                "mcmmo.commands.inspect;mcmmo.commands.inspect.far;mcmmo.commands.inspect.offline");
        command.setPermissionMessage(permissionsMessage);
        command.setUsage(LocaleLoader.getString("Commands.Usage.1", "inspect",
                "<" + LocaleLoader.getString("Commands.Usage.Player") + ">"));
        command.setExecutor(new InspectCommand());
    }

    private static void registerMccooldownCommand() {
        PluginCommand command = mcMMO.p.getCommand("mccooldown");
        command.setDescription(LocaleLoader.getString("Commands.Description.mccooldown"));
        command.setPermission("mcmmo.commands.mccooldown");
        command.setPermissionMessage(permissionsMessage);
        command.setUsage(LocaleLoader.getString("Commands.Usage.0", "mccooldowns"));
        command.setExecutor(new MccooldownCommand());
    }

    private static void registerMcabilityCommand() {
        PluginCommand command = mcMMO.p.getCommand("mcability");
        command.setDescription(LocaleLoader.getString("Commands.Description.mcability"));
        command.setPermission("mcmmo.commands.mcability;mcmmo.commands.mcability.others");
        command.setPermissionMessage(permissionsMessage);
        command.setUsage(LocaleLoader.getString("Commands.Usage.1", "mcability",
                "[" + LocaleLoader.getString("Commands.Usage.Player") + "]"));
        command.setExecutor(new McabilityCommand());
    }

    private static void registerMcmmoCommand() {
        PluginCommand command = mcMMO.p.getCommand("mcmmo");
        command.setDescription(LocaleLoader.getString("Commands.Description.mcmmo"));
        command.setPermission("mcmmo.commands.mcmmo.description;mcmmo.commands.mcmmo.help");
        command.setPermissionMessage(permissionsMessage);
        command.setUsage(LocaleLoader.getString("Commands.Usage.0", "mcmmo"));
        command.setUsage(
                command.getUsage() + "\n" + LocaleLoader.getString("Commands.Usage.1", "mcmmo",
                        "help"));
        command.setExecutor(new McmmoCommand());
    }

    private static void registerMcrankCommand() {
        PluginCommand command = mcMMO.p.getCommand("mcrank");
        command.setDescription(LocaleLoader.getString("Commands.Description.mcrank"));
        command.setPermission(
                "mcmmo.commands.mcrank;mcmmo.commands.mcrank.others;mcmmo.commands.mcrank.others.far;mcmmo.commands.mcrank.others.offline");
        command.setPermissionMessage(permissionsMessage);
        command.setUsage(LocaleLoader.getString("Commands.Usage.1", "mcrank",
                "[" + LocaleLoader.getString("Commands.Usage.Player") + "]"));
        command.setExecutor(new McRankCommand());
    }

    private static void registerMcstatsCommand() {
        PluginCommand command = mcMMO.p.getCommand("mcstats");
        command.setDescription(LocaleLoader.getString("Commands.Description.mcstats"));
        command.setPermission("mcmmo.commands.mcstats");
        command.setPermissionMessage(permissionsMessage);
        command.setUsage(LocaleLoader.getString("Commands.Usage.0", "mcstats"));
        command.setExecutor(new McstatsCommand());
    }

    private static void registerMctopCommand() {
        PluginCommand command = mcMMO.p.getCommand("mctop");
        command.setDescription(LocaleLoader.getString("Commands.Description.mctop"));
        command.setPermission(
                "mcmmo.commands.mctop"); // Only need the main one, not the individual skill ones
        command.setPermissionMessage(permissionsMessage);
        command.setUsage(LocaleLoader.getString("Commands.Usage.2", "mctop",
                "[" + LocaleLoader.getString("Commands.Usage.Skill") + "]",
                "[" + LocaleLoader.getString("Commands.Usage.Page") + "]"));
        command.setExecutor(new McTopCommand());
    }

    private static void registerMcpurgeCommand() {
        PluginCommand command = mcMMO.p.getCommand("mcpurge");
        command.setDescription(LocaleLoader.getString("Commands.Description.mcpurge",
                mcMMO.p.getGeneralConfig().getOldUsersCutoff()));
        command.setPermission("mcmmo.commands.mcpurge");
        command.setPermissionMessage(permissionsMessage);
        command.setUsage(LocaleLoader.getString("Commands.Usage.0", "mcpurge"));
        command.setExecutor(new McpurgeCommand());
    }

    private static void registerMcremoveCommand() {
        PluginCommand command = mcMMO.p.getCommand("mcremove");
        command.setDescription(LocaleLoader.getString("Commands.Description.mcremove"));
        command.setPermission("mcmmo.commands.mcremove");
        command.setPermissionMessage(permissionsMessage);
        command.setUsage(LocaleLoader.getString("Commands.Usage.1", "mcremove",
                "<" + LocaleLoader.getString("Commands.Usage.Player") + ">"));
        command.setExecutor(new McremoveCommand());
    }

    private static void registerMmoshowdbCommand() {
        PluginCommand command = mcMMO.p.getCommand("mmoshowdb");
        command.setDescription(LocaleLoader.getString("Commands.Description.mmoshowdb"));
        command.setPermission("mcmmo.commands.mmoshowdb");
        command.setPermissionMessage(permissionsMessage);
        command.setUsage(LocaleLoader.getString("Commands.Usage.0", "mmoshowdb"));
        command.setExecutor(new MmoshowdbCommand());
    }

    private static void registerMcconvertCommand() {
        PluginCommand command = mcMMO.p.getCommand("mcconvert");
        command.setDescription(LocaleLoader.getString("Commands.Description.mcconvert"));
        command.setPermission(
                "mcmmo.commands.mcconvert;mcmmo.commands.mcconvert.experience;mcmmo.commands.mcconvert.database");
        command.setPermissionMessage(permissionsMessage);
        command.setUsage(LocaleLoader.getString("Commands.Usage.2", "mcconvert", "database",
                "<flatfile|sql>"));
        command.setUsage(
                command.getUsage() + "\n" + LocaleLoader.getString("Commands.Usage.2", "mcconvert",
                        "experience", "<linear|exponential>"));
        command.setExecutor(new McconvertCommand());
    }

    private static void registerPartyCommand() {
        PluginCommand command = mcMMO.p.getCommand("party");
        command.setDescription(LocaleLoader.getString("Commands.Description.party"));
        command.setPermission(
                "mcmmo.commands.party;mcmmo.commands.party.accept;mcmmo.commands.party.create;mcmmo.commands.party.disband;"
                        +
                        "mcmmo.commands.party.xpshare;mcmmo.commands.party.invite;mcmmo.commands.party.itemshare;mcmmo.commands.party.join;"
                        +
                        "mcmmo.commands.party.kick;mcmmo.commands.party.lock;mcmmo.commands.party.owner;mcmmo.commands.party.password;"
                        +
                        "mcmmo.commands.party.quit;mcmmo.commands.party.rename;mcmmo.commands.party.unlock");
        command.setPermissionMessage(permissionsMessage);
        command.setExecutor(new PartyCommand());
    }

    private static void registerPtpCommand() {
        PluginCommand command = mcMMO.p.getCommand("ptp");
        command.setDescription(LocaleLoader.getString("Commands.Description.ptp"));
        command.setPermission(
                "mcmmo.commands.ptp"); // Only need the main one, not the individual ones for toggle/accept/acceptall
        command.setPermissionMessage(permissionsMessage);
        command.setUsage(LocaleLoader.getString("Commands.Usage.1", "ptp",
                "<" + LocaleLoader.getString("Commands.Usage.Player") + ">"));
        command.setUsage(
                command.getUsage() + "\n" + LocaleLoader.getString("Commands.Usage.1", "ptp",
                        "<toggle|accept|acceptall>"));
        command.setExecutor(new PtpCommand());
    }

    private static void registerMcnotifyCommand() {
        PluginCommand command = mcMMO.p.getCommand("mcnotify");
        command.setDescription(LocaleLoader.getString("Commands.Description.mcnotify"));
        command.setPermission("mcmmo.commands.mcnotify");
        command.setPermissionMessage(permissionsMessage);
        command.setUsage(LocaleLoader.getString("Commands.Usage.0", "mcnotify"));
        command.setExecutor(new McnotifyCommand());
    }

    private static void registerMcscoreboardCommand() {
        PluginCommand command = mcMMO.p.getCommand("mcscoreboard");
        command.setDescription(
                "Change the current mcMMO scoreboard being displayed"); //TODO: Localize
        command.setPermission("mcmmo.commands.mcscoreboard");
        command.setPermissionMessage(permissionsMessage);
        command.setUsage(
                LocaleLoader.getString("Commands.Usage.1", "mcscoreboard", "<CLEAR | KEEP>"));
        command.setUsage(command.getUsage() + "\n" + LocaleLoader.getString("Commands.Usage.2",
                "mcscoreboard", "time", "<seconds>"));
        command.setExecutor(new McscoreboardCommand());
    }

    private static void registerReloadLocaleCommand() {
        PluginCommand command = mcMMO.p.getCommand("mcmmoreloadlocale");
        command.setDescription("Reloads locale"); // TODO: Localize
        command.setPermission("mcmmo.commands.reloadlocale");
        command.setPermissionMessage(permissionsMessage);
        command.setUsage(LocaleLoader.getString("Commands.Usage.0", "mcmmoreloadlocale"));
        command.setExecutor(new McmmoReloadLocaleCommand());
    }

    private static void registerCompatibilityCommand() {
        PluginCommand command = mcMMO.p.getCommand("mmocompat"); //TODO: Localize
        command.setDescription(LocaleLoader.getString("Commands.Description.mmocompat"));
        command.setUsage(LocaleLoader.getString("Commands.Usage.0", "mmocompat"));
        command.setExecutor(new CompatibilityCommand());
    }

    private static void registerXPBarCommand() {
        PluginCommand command = mcMMO.p.getCommand("mmoxpbar"); //TODO: Localize
        command.setDescription(LocaleLoader.getString("Commands.Description.mmoxpbar"));
        command.setUsage(
                LocaleLoader.getString("Commands.Usage.1", "mmoxpbar", "<reset | disable>"));
        command.setUsage(
                command.getUsage() + "\n" + LocaleLoader.getString("Commands.Usage.2", "mmoxpbar",
                        "<show | hide | disable>", "<skillname>"));
        command.setExecutor(new XPBarCommand());
    }

    public static void registerCommands() {
        // Generic Commands
        registerXPBarCommand();
        registerMmoInfoCommand();
        registerMmoDebugCommand();
        registerMcabilityCommand();
        registerMcgodCommand();
        registerMcChatSpyCommand();
        registerMcmmoCommand();
        registerMcnotifyCommand();
        registerMcrefreshCommand();
        registerMcscoreboardCommand();
        registerXprateCommand();

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

        // Party Commands
        if (mcMMO.p.getPartyConfig().isPartyEnabled()) {
            registerPartyCommand();
            registerPtpCommand();
        }

        // Player Commands
        registerInspectCommand();
        registerMccooldownCommand();
        registerMcrankCommand();
        registerMcstatsCommand();
        registerMctopCommand();

        // Skill Commands
        registerSkillCommands();

        // Admin commands
        registerReloadLocaleCommand();

        // Misc
        registerCompatibilityCommand();
    }
}
