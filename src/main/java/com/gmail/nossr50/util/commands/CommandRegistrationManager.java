package com.gmail.nossr50.util.commands;

import com.gmail.nossr50.commands.McLevelUpSoundCommand;
import com.gmail.nossr50.commands.McabilityCommand;
import com.gmail.nossr50.commands.McconvertCommand;
import com.gmail.nossr50.commands.McgodCommand;
import com.gmail.nossr50.commands.McmmoCommand;
import com.gmail.nossr50.commands.McnotifyCommand;
import com.gmail.nossr50.commands.McrefreshCommand;
import com.gmail.nossr50.commands.McscoreboardCommand;
import com.gmail.nossr50.commands.XprateCommand;
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
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.PluginCommand;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public final class CommandRegistrationManager {
    private CommandRegistrationManager() {
    }

    /**
     * Everything needed to wire one plugin.yml command declaration to its executor. Description
     * and usage lines are suppliers so the table can be built without touching the locale or a
     * running plugin instance.
     */
    private record CommandSpec(@NotNull String name, @NotNull Supplier<String> description,
            @Nullable String permission, @NotNull Supplier<List<String>> usageLines,
            @NotNull Supplier<? extends CommandExecutor> executor, @NotNull List<String> aliases,
            boolean requiresParty) {
    }

    private static @NotNull CommandSpec spec(@NotNull String name, @Nullable String permission,
            @NotNull Supplier<List<String>> usageLines,
            @NotNull Supplier<? extends CommandExecutor> executor) {
        return spec(name, () -> LocaleLoader.getString("Commands.Description." + name),
                permission, usageLines, executor);
    }

    private static @NotNull CommandSpec spec(@NotNull String name,
            @NotNull Supplier<String> description, @Nullable String permission,
            @NotNull Supplier<List<String>> usageLines,
            @NotNull Supplier<? extends CommandExecutor> executor) {
        return new CommandSpec(name, description, permission, usageLines, executor, List.of(),
                false);
    }

    private static final List<CommandSpec> COMMAND_SPECS = List.of(
            // Generic Commands
            spec("mmoxpbar", null, () -> List.of(
                    LocaleLoader.getString("Commands.Usage.1", "mmoxpbar", "<reset | disable>"),
                    LocaleLoader.getString("Commands.Usage.2", "mmoxpbar",
                            "<show | hide | disable>", "<skillname>")),
                    XPBarCommand::new),
            spec("mmoinfo", "mcmmo.commands.mmoinfo", () -> List.of(
                    LocaleLoader.getString("Commands.Usage.1", "mmoinfo",
                            "[" + LocaleLoader.getString("Commands.Usage.SubSkill") + "]")),
                    MmoInfoCommand::new),
            // No permission required on mmodebug to save support headaches
            spec("mmodebug", null, () -> List.of(
                    LocaleLoader.getString("Commands.Usage.0", "mmodebug")),
                    PlayerDebugCommand::new),
            spec("mcability", "mcmmo.commands.mcability;mcmmo.commands.mcability.others",
                    () -> List.of(LocaleLoader.getString("Commands.Usage.1", "mcability",
                            "[" + LocaleLoader.getString("Commands.Usage.Player") + "]")),
                    McabilityCommand::new),
            spec("mcgod", "mcmmo.commands.mcgod;mcmmo.commands.mcgod.others", () -> List.of(
                    LocaleLoader.getString("Commands.Usage.1", "mcgod",
                            "[" + LocaleLoader.getString("Commands.Usage.Player") + "]")),
                    McgodCommand::new),
            spec("mcchatspy", "mcmmo.commands.mcchatspy;mcmmo.commands.mcchatspy.others",
                    () -> List.of(LocaleLoader.getString("Commands.Usage.1", "mcchatspy",
                            "[" + LocaleLoader.getString("Commands.Usage.Player") + "]")),
                    McChatSpy::new),
            spec("mcmmo", "mcmmo.commands.mcmmo.description;mcmmo.commands.mcmmo.help",
                    () -> List.of(LocaleLoader.getString("Commands.Usage.0", "mcmmo"),
                            LocaleLoader.getString("Commands.Usage.1", "mcmmo", "help")),
                    McmmoCommand::new),
            spec("mcnotify", "mcmmo.commands.mcnotify", () -> List.of(
                    LocaleLoader.getString("Commands.Usage.0", "mcnotify")),
                    McnotifyCommand::new),
            spec("mclevelupsound", "mcmmo.commands.mclevelupsound", () -> List.of(
                    LocaleLoader.getString("Commands.Usage.0", "mclevelupsound")),
                    McLevelUpSoundCommand::new),
            spec("mcrefresh", "mcmmo.commands.mcrefresh;mcmmo.commands.mcrefresh.others",
                    () -> List.of(LocaleLoader.getString("Commands.Usage.1", "mcrefresh",
                            "[" + LocaleLoader.getString("Commands.Usage.Player") + "]")),
                    McrefreshCommand::new),
            spec("mcscoreboard",
                    () -> "Change the current mcMMO scoreboard being displayed", //TODO: Localize
                    "mcmmo.commands.mcscoreboard", () -> List.of(
                            LocaleLoader.getString("Commands.Usage.1", "mcscoreboard",
                                    "<CLEAR | KEEP>"),
                            LocaleLoader.getString("Commands.Usage.2", "mcscoreboard", "time",
                                    "<seconds>")),
                    McscoreboardCommand::new),
            new CommandSpec("xprate",
                    () -> LocaleLoader.getString("Commands.Description.xprate"),
                    "mcmmo.commands.xprate;mcmmo.commands.xprate.reset;"
                            + "mcmmo.commands.xprate.set;mcmmo.commands.xprate.show",
                    () -> List.of(LocaleLoader.getString("Commands.Usage.3", "xprate",
                                    "[" + LocaleLoader.getString("Commands.Usage.Skill")
                                            + "|all]",
                                    "<" + LocaleLoader.getString("Commands.Usage.Rate") + ">",
                                    "[true|false]"),
                            LocaleLoader.getString("Commands.Usage.1", "xprate", "reset"),
                            LocaleLoader.getString("Commands.Usage.0", "xprate")),
                    XprateCommand::new, List.of("mcxprate"), false),

            // Database Commands
            spec("mcpurge", () -> LocaleLoader.getString("Commands.Description.mcpurge",
                    mcMMO.p.getGeneralConfig().getOldUsersCutoff()), "mcmmo.commands.mcpurge",
                    () -> List.of(LocaleLoader.getString("Commands.Usage.0", "mcpurge")),
                    McpurgeCommand::new),
            spec("mcremove", "mcmmo.commands.mcremove", () -> List.of(
                    LocaleLoader.getString("Commands.Usage.1", "mcremove",
                            "<" + LocaleLoader.getString("Commands.Usage.Player") + ">")),
                    McremoveCommand::new),
            spec("mmoshowdb", "mcmmo.commands.mmoshowdb", () -> List.of(
                    LocaleLoader.getString("Commands.Usage.0", "mmoshowdb")),
                    MmoshowdbCommand::new),
            spec("mcconvert",
                    "mcmmo.commands.mcconvert;mcmmo.commands.mcconvert.experience;"
                            + "mcmmo.commands.mcconvert.database",
                    () -> List.of(LocaleLoader.getString("Commands.Usage.2", "mcconvert",
                                    "database", "<flatfile|sql>"),
                            LocaleLoader.getString("Commands.Usage.2", "mcconvert", "experience",
                                    "<linear|exponential>")),
                    McconvertCommand::new),

            // Experience Commands
            spec("addlevels", "mcmmo.commands.addlevels;mcmmo.commands.addlevels.others",
                    () -> List.of(LocaleLoader.getString("Commands.Usage.3.XP", "addlevels",
                            "[" + LocaleLoader.getString("Commands.Usage.Player") + "]",
                            "<" + LocaleLoader.getString("Commands.Usage.Skill") + ">",
                            "<" + LocaleLoader.getString("Commands.Usage.Level") + ">")),
                    AddlevelsCommand::new),
            spec("addxp", "mcmmo.commands.addxp;mcmmo.commands.addxp.others", () -> List.of(
                    LocaleLoader.getString("Commands.Usage.3.XP", "addxp",
                            "[" + LocaleLoader.getString("Commands.Usage.Player") + "]",
                            "<" + LocaleLoader.getString("Commands.Usage.Skill") + ">",
                            "<" + LocaleLoader.getString("Commands.Usage.XP") + ">")),
                    AddxpCommand::new),
            spec("mmoedit", "mcmmo.commands.mmoedit;mcmmo.commands.mmoedit.others",
                    () -> List.of(LocaleLoader.getString("Commands.Usage.3.XP", "mmoedit",
                            "[" + LocaleLoader.getString("Commands.Usage.Player") + "]",
                            "<" + LocaleLoader.getString("Commands.Usage.Skill") + ">",
                            "<" + LocaleLoader.getString("Commands.Usage.Level") + ">")),
                    MmoeditCommand::new),
            // Only the main permission nodes are needed here, not the per-skill ones
            spec("skillreset", "mcmmo.commands.skillreset;mcmmo.commands.skillreset.others",
                    () -> List.of(LocaleLoader.getString("Commands.Usage.2", "skillreset",
                            "[" + LocaleLoader.getString("Commands.Usage.Player") + "]",
                            "<" + LocaleLoader.getString("Commands.Usage.Skill") + ">")),
                    SkillresetCommand::new),

            // Party Commands (only registered while the party system is enabled)
            new CommandSpec("party", () -> LocaleLoader.getString("Commands.Description.party"),
                    "mcmmo.commands.party;mcmmo.commands.party.accept;mcmmo.commands.party.create;"
                            + "mcmmo.commands.party.disband;mcmmo.commands.party.xpshare;"
                            + "mcmmo.commands.party.invite;mcmmo.commands.party.itemshare;"
                            + "mcmmo.commands.party.join;mcmmo.commands.party.kick;"
                            + "mcmmo.commands.party.lock;mcmmo.commands.party.owner;"
                            + "mcmmo.commands.party.password;mcmmo.commands.party.quit;"
                            + "mcmmo.commands.party.rename;mcmmo.commands.party.unlock",
                    List::of, PartyCommand::new, List.of(), true),
            // Only the main ptp permission node is needed, not the toggle/accept/acceptall ones
            new CommandSpec("ptp", () -> LocaleLoader.getString("Commands.Description.ptp"),
                    "mcmmo.commands.ptp",
                    () -> List.of(LocaleLoader.getString("Commands.Usage.1", "ptp",
                                    "<" + LocaleLoader.getString("Commands.Usage.Player") + ">"),
                            LocaleLoader.getString("Commands.Usage.1", "ptp",
                                    "<toggle|accept|acceptall>")),
                    PtpCommand::new, List.of(), true),

            // Player Commands
            spec("inspect",
                    "mcmmo.commands.inspect;mcmmo.commands.inspect.far;"
                            + "mcmmo.commands.inspect.offline",
                    () -> List.of(LocaleLoader.getString("Commands.Usage.1", "inspect",
                            "<" + LocaleLoader.getString("Commands.Usage.Player") + ">")),
                    InspectCommand::new),
            spec("mccooldown", "mcmmo.commands.mccooldown", () -> List.of(
                    LocaleLoader.getString("Commands.Usage.0", "mccooldowns")),
                    MccooldownCommand::new),
            spec("mcrank",
                    "mcmmo.commands.mcrank;mcmmo.commands.mcrank.others;"
                            + "mcmmo.commands.mcrank.others.far;"
                            + "mcmmo.commands.mcrank.others.offline",
                    () -> List.of(LocaleLoader.getString("Commands.Usage.1", "mcrank",
                            "[" + LocaleLoader.getString("Commands.Usage.Player") + "]")),
                    McRankCommand::new),
            spec("mcstats", "mcmmo.commands.mcstats", () -> List.of(
                    LocaleLoader.getString("Commands.Usage.0", "mcstats")),
                    McstatsCommand::new),
            // Only the main mctop permission node is needed, not the per-skill ones
            spec("mctop", "mcmmo.commands.mctop", () -> List.of(
                    LocaleLoader.getString("Commands.Usage.2", "mctop",
                            "[" + LocaleLoader.getString("Commands.Usage.Skill") + "]",
                            "[" + LocaleLoader.getString("Commands.Usage.Page") + "]")),
                    McTopCommand::new),

            // Admin commands
            spec("mcmmoreloadlocale", () -> "Reloads locale", // TODO: Localize
                    "mcmmo.commands.reloadlocale", () -> List.of(
                            LocaleLoader.getString("Commands.Usage.0", "mcmmoreloadlocale")),
                    McmmoReloadLocaleCommand::new)
    );

    /**
     * Command names wired by the spec table. Package-private so the registration coverage test
     * can compare them against the plugin.yml declarations.
     */
    static @NotNull Set<String> specCommandNames() {
        return COMMAND_SPECS.stream().map(CommandSpec::name)
                .collect(Collectors.toUnmodifiableSet());
    }

    /**
     * Command names wired by the skill command loop. Package-private for the registration
     * coverage test.
     */
    static @NotNull Set<String> skillCommandNames() {
        return Arrays.stream(PrimarySkillType.values())
                .map(skill -> skill.toString().toLowerCase(Locale.ENGLISH))
                .collect(Collectors.toUnmodifiableSet());
    }

    private static void applySpec(@NotNull CommandSpec spec, @NotNull String permissionMessage) {
        final PluginCommand command = mcMMO.p.getCommand(spec.name());
        if (command == null) {
            mcMMO.p.getLogger().severe("Command not found: " + spec.name());
            return;
        }

        command.setDescription(spec.description().get());
        command.setPermission(spec.permission());
        command.setPermissionMessage(permissionMessage);

        final List<String> usageLines = spec.usageLines().get();
        if (!usageLines.isEmpty()) {
            command.setUsage(String.join("\n", usageLines));
        }

        if (!spec.aliases().isEmpty()) {
            command.setAliases(spec.aliases());
        }

        command.setExecutor(spec.executor().get());
    }

    private static void registerSkillCommands(@NotNull String permissionMessage) {
        for (PrimarySkillType primarySkillType : PrimarySkillType.values()) {
            if (primarySkillType == PrimarySkillType.SPEARS
                    && !mcMMO.getMinecraftGameVersion().isAtLeast(1, 21, 11)) {
                continue;
            }

            final String commandName = primarySkillType.toString().toLowerCase(Locale.ENGLISH);
            final String localizedName = mcMMO.p.getSkillTools()
                    .getHeaderBannerSkillName(primarySkillType).toLowerCase(Locale.ENGLISH);

            final PluginCommand command = mcMMO.p.getCommand(commandName);
            if (command == null) {
                mcMMO.p.getLogger().severe("Command not found: " + commandName);
                continue;
            }

            command.setDescription(LocaleLoader.getString("Commands.Description.Skill",
                    StringUtils.getCapitalized(localizedName)));
            command.setPermission("mcmmo.commands." + commandName);
            command.setPermissionMessage(permissionMessage);
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

    public static void registerCommands() {
        final String permissionMessage = LocaleLoader.getString("mcMMO.NoPermission");
        final boolean partyEnabled = mcMMO.p.getPartyConfig().isPartyEnabled();

        for (CommandSpec spec : COMMAND_SPECS) {
            if (spec.requiresParty() && !partyEnabled) {
                continue;
            }
            applySpec(spec, permissionMessage);
        }

        // Skill Commands
        registerSkillCommands(permissionMessage);
    }
}
