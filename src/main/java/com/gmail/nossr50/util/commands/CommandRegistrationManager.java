package com.gmail.nossr50.util.commands;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.command.PluginCommand;

import com.gmail.nossr50.mcMMO;
import com.gmail.nossr50.commands.McabilityCommand;
import com.gmail.nossr50.commands.McgodCommand;
import com.gmail.nossr50.commands.McmmoCommand;
import com.gmail.nossr50.commands.McnotifyCommand;
import com.gmail.nossr50.commands.McrefreshCommand;
import com.gmail.nossr50.commands.XprateCommand;
import com.gmail.nossr50.commands.chat.AdminChatCommand;
import com.gmail.nossr50.commands.chat.PartyChatCommand;
import com.gmail.nossr50.commands.database.McpurgeCommand;
import com.gmail.nossr50.commands.database.McremoveCommand;
import com.gmail.nossr50.commands.database.MmoupdateCommand;
import com.gmail.nossr50.commands.experience.AddlevelsCommand;
import com.gmail.nossr50.commands.experience.AddxpCommand;
import com.gmail.nossr50.commands.experience.MmoeditCommand;
import com.gmail.nossr50.commands.experience.SkillresetCommand;
import com.gmail.nossr50.commands.hardcore.HardcoreCommand;
import com.gmail.nossr50.commands.hardcore.VampirismCommand;
import com.gmail.nossr50.commands.party.PartyCommand;
import com.gmail.nossr50.commands.party.teleport.PtpCommand;
import com.gmail.nossr50.commands.player.InspectCommand;
import com.gmail.nossr50.commands.player.McrankCommand;
import com.gmail.nossr50.commands.player.McstatsCommand;
import com.gmail.nossr50.commands.player.MctopCommand;
import com.gmail.nossr50.commands.skills.AcrobaticsCommand;
import com.gmail.nossr50.commands.skills.ArcheryCommand;
import com.gmail.nossr50.commands.skills.AxesCommand;
import com.gmail.nossr50.commands.skills.ExcavationCommand;
import com.gmail.nossr50.commands.skills.FishingCommand;
import com.gmail.nossr50.commands.skills.HerbalismCommand;
import com.gmail.nossr50.commands.skills.MiningCommand;
import com.gmail.nossr50.commands.skills.RepairCommand;
import com.gmail.nossr50.commands.skills.SmeltingCommand;
import com.gmail.nossr50.commands.skills.SwordsCommand;
import com.gmail.nossr50.commands.skills.TamingCommand;
import com.gmail.nossr50.commands.skills.UnarmedCommand;
import com.gmail.nossr50.commands.skills.WoodcuttingCommand;
import com.gmail.nossr50.commands.spout.MchudCommand;
import com.gmail.nossr50.commands.spout.XplockCommand;
import com.gmail.nossr50.config.Config;
import com.gmail.nossr50.datatypes.skills.SkillType;
import com.gmail.nossr50.locale.LocaleLoader;
import com.gmail.nossr50.util.StringUtils;
import com.gmail.nossr50.util.skills.SkillUtils;

public final class CommandRegistrationManager {
    private CommandRegistrationManager() {};

    private static String permissionsMessage = LocaleLoader.getString("mcMMO.NoPermission");

    public static void registerSkillCommands() {
        for (SkillType skill : SkillType.values()) {
            String commandName = skill.toString().toLowerCase();
            String localizedName = SkillUtils.getSkillName(skill).toLowerCase();

            PluginCommand command;

            command = mcMMO.p.getCommand(commandName);
            command.setDescription(LocaleLoader.getString("Commands.Description.Skill", StringUtils.getCapitalized(localizedName)));
            command.setPermission("mcmmo.commands." + commandName);
            command.setPermissionMessage(permissionsMessage);
            command.setUsage(LocaleLoader.getString("Commands.Usage.0", localizedName));
            command.setUsage(command.getUsage() + "\n" + LocaleLoader.getString("Commands.Usage.2", localizedName, "?", "[" + LocaleLoader.getString("Commands.Usage.Page") + "]"));

            switch (skill) {
                case ACROBATICS:
                    command.setExecutor(new AcrobaticsCommand());
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

    public static void registerAddlevelsCommand() {
        PluginCommand command = mcMMO.p.getCommand("addlevels");
        command.setDescription(LocaleLoader.getString("Commands.Description.addlevels"));
        command.setPermission("mcmmo.commands.addlevels;mcmmo.commands.addlevels.others");
        command.setPermissionMessage(permissionsMessage);
        command.setUsage(LocaleLoader.getString("Commands.Usage.3", "addlevels", "[" + LocaleLoader.getString("Commands.Usage.Player") + "]", "<" + LocaleLoader.getString("Commands.Usage.Skill") + ">", "<" + LocaleLoader.getString("Commands.Usage.Level") + ">"));
        command.setExecutor(new AddlevelsCommand());
    }

    public static void registerAddxpCommand() {
        PluginCommand command = mcMMO.p.getCommand("addxp");
        command.setDescription(LocaleLoader.getString("Commands.Description.addxp"));
        command.setPermission("mcmmo.commands.addxp;mcmmo.commands.addxp.others");
        command.setPermissionMessage(permissionsMessage);
        command.setUsage(LocaleLoader.getString("Commands.Usage.3", "addxp", "[" + LocaleLoader.getString("Commands.Usage.Player") + "]", "<" + LocaleLoader.getString("Commands.Usage.Skill") + ">", "<" + LocaleLoader.getString("Commands.Usage.XP") + ">"));
        command.setExecutor(new AddxpCommand());
    }

    public static void registerMcgodCommand() {
        PluginCommand command = mcMMO.p.getCommand("mcgod");
        command.setDescription(LocaleLoader.getString("Commands.Description.mcgod"));
        command.setPermission("mcmmo.commands.mcgod;mcmmo.commands.mcgod.others");
        command.setPermissionMessage(permissionsMessage);
        command.setUsage(LocaleLoader.getString("Commands.Usage.1", "mcgod", "[" + LocaleLoader.getString("Commands.Usage.Player") + "]"));
        command.setExecutor(new McgodCommand());
    }

    public static void registerMcrefreshCommand() {
        PluginCommand command = mcMMO.p.getCommand("mcrefresh");
        command.setDescription(LocaleLoader.getString("Commands.Description.mcrefresh"));
        command.setPermission("mcmmo.commands.mcrefresh;mcmmo.commands.mcrefresh.others");
        command.setPermissionMessage(permissionsMessage);
        command.setUsage(LocaleLoader.getString("Commands.Usage.1", "mcrefresh", "[" + LocaleLoader.getString("Commands.Usage.Player") + "]"));
        command.setExecutor(new McrefreshCommand());
    }

    public static void registerMmoeditCommand() {
        PluginCommand command = mcMMO.p.getCommand("mmoedit");
        command.setDescription(LocaleLoader.getString("Commands.Description.mmoedit"));
        command.setPermission("mcmmo.commands.mmoedit;mcmmo.commands.mmoedit.others");
        command.setPermissionMessage(permissionsMessage);
        command.setUsage(LocaleLoader.getString("Commands.Usage.3", "mmoedit", "[" + LocaleLoader.getString("Commands.Usage.Player") + "]", "<" + LocaleLoader.getString("Commands.Usage.Skill") + ">", "<" + LocaleLoader.getString("Commands.Usage.Level") + ">"));
        command.setExecutor(new MmoeditCommand());
    }

    public static void registerSkillresetCommand() {
        PluginCommand command = mcMMO.p.getCommand("skillreset");
        command.setDescription(LocaleLoader.getString("Commands.Description.skillreset"));
        command.setPermission("mcmmo.commands.skillreset;mcmmo.commands.skillreset.others"); // Only need the main ones, not the individual skill ones
        command.setPermissionMessage(permissionsMessage);
        command.setUsage(LocaleLoader.getString("Commands.Usage.2", "skillreset", "[" + LocaleLoader.getString("Commands.Usage.Player") + "]", "<" + LocaleLoader.getString("Commands.Usage.Skill") + ">"));
        command.setExecutor(new SkillresetCommand());
    }

    public static void registerXprateCommand() {
        List<String> aliasList = new ArrayList<String>();
        aliasList.add("mcxprate");

        PluginCommand command = mcMMO.p.getCommand("xprate");
        command.setDescription(LocaleLoader.getString("Commands.Description.xprate"));
        command.setPermission("mcmmo.commands.xprate;mcmmo.commands.xprate.reset;mcmmo.commands.xprate.set");
        command.setPermissionMessage(permissionsMessage);
        command.setUsage(LocaleLoader.getString("Commands.Usage.2", "xprate", "<" + LocaleLoader.getString("Commands.Usage.Rate") + ">", "<true|false>"));
        command.setUsage(command.getUsage() + "\n" + LocaleLoader.getString("Commands.Usage.1", "xprate", "reset"));
        command.setAliases(aliasList);
        command.setExecutor(new XprateCommand());
    }

    public static void registerInspectCommand() {
        PluginCommand command = mcMMO.p.getCommand("inspect");
        command.setDescription(LocaleLoader.getString("Commands.Description.inspect"));
        command.setPermission("mcmmo.commands.inspect;mcmmo.commands.inspect.far;mcmmo.commands.inspect.offline");
        command.setPermissionMessage(permissionsMessage);
        command.setUsage(LocaleLoader.getString("Commands.Usage.1", "inspect", "<" + LocaleLoader.getString("Commands.Usage.Player") + ">"));
        command.setExecutor(new InspectCommand());
    }

    public static void registerMcabilityCommand() {
        PluginCommand command = mcMMO.p.getCommand("mcability");
        command.setDescription(LocaleLoader.getString("Commands.Description.mcability"));
        command.setPermission("mcmmo.commands.mcability;mcmmo.commands.mcability.others");
        command.setPermissionMessage(permissionsMessage);
        command.setUsage(LocaleLoader.getString("Commands.Usage.1", "mcability", "[" + LocaleLoader.getString("Commands.Usage.Player") + "]"));
        command.setExecutor(new McabilityCommand());
    }

    public static void registerMcmmoCommand() {
        PluginCommand command = mcMMO.p.getCommand("mcmmo");
        command.setDescription(LocaleLoader.getString("Commands.Description.mcmmo"));
        command.setPermission("mcmmo.commands.mcmmo.description;mcmmo.commands.mcmmo.help");
        command.setPermissionMessage(permissionsMessage);
        command.setUsage(LocaleLoader.getString("Commands.Usage.0", "mcmmo"));
        command.setUsage(command.getUsage() + "\n" + LocaleLoader.getString("Commands.Usage.1", "mcmmo", "help"));
        command.setExecutor(new McmmoCommand());
    }

    public static void registerMcrankCommand() {
        PluginCommand command = mcMMO.p.getCommand("mcrank");
        command.setDescription(LocaleLoader.getString("Commands.Description.mcrank"));
        command.setPermission("mcmmo.commands.mcrank;mcmmo.commands.mcrank.others;mcmmo.commands.mcrank.others.far;mcmmo.commands.mcrank.others.offline");
        command.setPermissionMessage(permissionsMessage);
        command.setUsage(LocaleLoader.getString("Commands.Usage.1", "mcrank", "[" + LocaleLoader.getString("Commands.Usage.Player") + "]"));
        command.setExecutor(new McrankCommand());
    }

    public static void registerMcstatsCommand() {
        PluginCommand command = mcMMO.p.getCommand("mcstats");
        command.setDescription(LocaleLoader.getString("Commands.Description.mcstats"));
        command.setPermission("mcmmo.commands.mcstats");
        command.setPermissionMessage(permissionsMessage);
        command.setUsage(LocaleLoader.getString("Commands.Usage.0", "mcstats"));
        command.setExecutor(new McstatsCommand());
    }

    public static void registerMctopCommand() {
        PluginCommand command = mcMMO.p.getCommand("mctop");
        command.setDescription(LocaleLoader.getString("Commands.Description.mctop"));
        command.setPermission("mcmmo.commands.mctop"); // Only need the main one, not the individual skill ones
        command.setPermissionMessage(permissionsMessage);
        command.setUsage(LocaleLoader.getString("Commands.Usage.2", "mctop", "[" + LocaleLoader.getString("Commands.Usage.Skill") + "]", "[" + LocaleLoader.getString("Commands.Usage.Page") + "]"));
        command.setExecutor(new MctopCommand());
    }

    public static void registerMcpurgeCommand() {
        PluginCommand command = mcMMO.p.getCommand("mcpurge");
        command.setDescription(LocaleLoader.getString("Commands.Description.mcpurge", Config.getInstance().getOldUsersCutoff()));
        command.setPermissionMessage(permissionsMessage);
        command.setUsage(LocaleLoader.getString("Commands.Usage.0", "mcpurge"));
        command.setExecutor(new McpurgeCommand());
    }

    public static void registerMcremoveCommand() {
        PluginCommand command = mcMMO.p.getCommand("mcremove");
        command.setDescription(LocaleLoader.getString("Commands.Description.mcremove"));
        command.setPermissionMessage(permissionsMessage);
        command.setUsage(LocaleLoader.getString("Commands.Usage.1", "mcremove", "<" + LocaleLoader.getString("Commands.Usage.Player") + ">"));
        command.setExecutor(new McremoveCommand());
    }

    public static void registerMmoupdateCommand() {
        PluginCommand command = mcMMO.p.getCommand("mmoupdate");
        command.setDescription(LocaleLoader.getString("Commands.Description.mmoupdate"));
        command.setPermissionMessage(permissionsMessage);
        command.setUsage(LocaleLoader.getString("Commands.Usage.0", "mmoupdate"));
        command.setExecutor(new MmoupdateCommand());
    }

    public static void registerAdminChatCommand() {
        PluginCommand command = mcMMO.p.getCommand("adminchat");
        command.setDescription(LocaleLoader.getString("Commands.Description.adminchat"));
        command.setPermission("mcmmo.chat.adminchat");
        command.setPermissionMessage(permissionsMessage);
        command.setUsage(LocaleLoader.getString("Commands.Usage.0", "adminchat"));
        command.setUsage(command.getUsage() + "\n" + LocaleLoader.getString("Commands.Usage.1", "adminchat", "<on|off>"));
        command.setUsage(command.getUsage() + "\n" + LocaleLoader.getString("Commands.Usage.1", "adminchat", "<" + LocaleLoader.getString("Commands.Usage.Message") + ">"));
        command.setExecutor(new AdminChatCommand());
    }

    public static void registerPartyChatCommand() {
        PluginCommand command = mcMMO.p.getCommand("partychat");
        command.setDescription(LocaleLoader.getString("Commands.Description.partychat"));
        command.setPermission("mcmmo.chat.partychat;mcmmo.commands.party");
        command.setPermissionMessage(permissionsMessage);
        command.setUsage(LocaleLoader.getString("Commands.Usage.0", "partychat"));
        command.setUsage(command.getUsage() + "\n" + LocaleLoader.getString("Commands.Usage.1", "partychat", "<on|off>"));
        command.setUsage(command.getUsage() + "\n" + LocaleLoader.getString("Commands.Usage.1", "partychat", "<" + LocaleLoader.getString("Commands.Usage.Message") + ">"));
        command.setExecutor(new PartyChatCommand());
    }

    public static void registerMchudCommand() {
        PluginCommand command = mcMMO.p.getCommand("mchud");
        command.setDescription(LocaleLoader.getString("Commands.Description.mchud"));
        command.setPermission("mcmmo.commands.mchud");
        command.setPermissionMessage(permissionsMessage);
        command.setUsage(LocaleLoader.getString("Commands.Usage.1", "mchud", "<DISABLED | STANDARD | SMALL | RETRO>"));
        command.setExecutor(new MchudCommand());
    }

    public static void registerXplockCommand() {
        PluginCommand command = mcMMO.p.getCommand("xplock");
        command.setDescription(LocaleLoader.getString("Commands.Description.xplock"));
        command.setPermission("mcmmo.commands.xplock");
        command.setPermissionMessage(permissionsMessage);
        command.setUsage(LocaleLoader.getString("Commands.Usage.0", "xplock"));
        command.setUsage(command.getUsage() + "\n" + LocaleLoader.getString("Commands.Usage.1", "xplock", "<on|off>"));
        command.setUsage(command.getUsage() + "\n" + LocaleLoader.getString("Commands.Usage.1", "xplock", "<" + LocaleLoader.getString("Commands.Usage.Skill") + ">"));
        command.setExecutor(new XplockCommand());
    }

    public static void registerPartyCommand() {
        PluginCommand command = mcMMO.p.getCommand("party");
        command.setDescription(LocaleLoader.getString("Commands.Description.party"));
        command.setPermission("mcmmo.commands.party;mcmmo.commands.party.accept;mcmmo.commands.party.create;mcmmo.commands.party.disband;" +
                              "mcmmo.commands.party.expshare;mcmmo.commands.party.invite;mcmmo.commands.party.itemshare;mcmmo.commands.party.join;" +
                              "mcmmo.commands.party.kick;mcmmo.commands.party.lock;mcmmo.commands.party.owner;mcmmo.commands.party.password;" +
                              "mcmmo.commands.party.quit;mcmmo.commands.party.rename;mcmmo.commands.party.unlock");
        command.setPermissionMessage(permissionsMessage);
        command.setExecutor(new PartyCommand());
    }

    public static void registerPtpCommand() {
        PluginCommand command = mcMMO.p.getCommand("ptp");
        command.setDescription(LocaleLoader.getString("Commands.Description.ptp"));
        command.setPermission("mcmmo.commands.ptp"); // Only need the main one, not the individual ones for toggle/accept/acceptall
        command.setPermissionMessage(permissionsMessage);
        command.setUsage(LocaleLoader.getString("Commands.Usage.1", "ptp", "<" + LocaleLoader.getString("Commands.Usage.Player") + ">"));
        command.setUsage(command.getUsage() + "\n" + LocaleLoader.getString("Commands.Usage.1", "ptp", "<toggle|accept|acceptall>"));
        command.setExecutor(new PtpCommand());
    }

    public static void registerHardcoreCommand() {
        PluginCommand command = mcMMO.p.getCommand("hardcore");
        command.setDescription(LocaleLoader.getString("Commands.Description.hardcore"));
        command.setPermission("mcmmo.commands.hardcore;mcmmo.commands.hardcore.toggle;mcmmo.commands.hardcore.modify");
        command.setPermissionMessage(permissionsMessage);
        command.setUsage(LocaleLoader.getString("Commands.Usage.1", "hardcore", "[on|off]"));
        command.setUsage(command.getUsage() + "\n" + LocaleLoader.getString("Commands.Usage.1", "hardcore", "<" + LocaleLoader.getString("Commands.Usage.Rate") + ">"));
        command.setExecutor(new HardcoreCommand());
    }

    public static void registerVampirismCommand() {
        PluginCommand command = mcMMO.p.getCommand("vampirism");
        command.setDescription(LocaleLoader.getString("Commands.Description.vampirism"));
        command.setPermission("mcmmo.commands.vampirism;mcmmo.commands.vampirism.toggle;mcmmo.commands.vampirism.modify");
        command.setPermissionMessage(permissionsMessage);
        command.setUsage(LocaleLoader.getString("Commands.Usage.1", "vampirism", "[on|off]"));
        command.setUsage(command.getUsage() + "\n" + LocaleLoader.getString("Commands.Usage.1", "vampirism", "<" + LocaleLoader.getString("Commands.Usage.Rate") + ">"));
        command.setExecutor(new VampirismCommand());
    }

    public static void registerMcnotifyCommand() {
        PluginCommand command = mcMMO.p.getCommand("mcnotify");
        command.setDescription(LocaleLoader.getString("Commands.Description.mcnotify"));
        command.setPermission("mcmmo.commands.mcnotify");
        command.setPermissionMessage(permissionsMessage);
        command.setUsage(LocaleLoader.getString("Commands.Usage.0", "mcnotify"));
        command.setExecutor(new McnotifyCommand());
    }
}
