package com.gmail.nossr50.commands;

import com.gmail.nossr50.commands.database.ConvertDatabaseCommand;
import com.gmail.nossr50.commands.experience.ConvertExperienceCommand;
import com.gmail.nossr50.database.DatabaseManagerFactory;
import com.gmail.nossr50.datatypes.database.DatabaseType;
import com.gmail.nossr50.datatypes.experience.FormulaType;
import com.gmail.nossr50.mcMMO;
import com.google.common.collect.ImmutableList;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.util.StringUtil;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class McconvertCommand implements TabExecutor {
    private List<String> FORMULA_TYPES;
    private List<String> DATABASE_TYPES;
    private final List<String> SUBCOMMANDS = ImmutableList.of("database", "experience");
    private CommandExecutor databaseConvertCommand;
    private CommandExecutor experienceConvertCommand;

    private mcMMO pluginRef;

    public McconvertCommand(mcMMO pluginRef) {
        this.pluginRef = pluginRef;
        databaseConvertCommand = new ConvertDatabaseCommand(pluginRef);
        experienceConvertCommand = new ConvertExperienceCommand(pluginRef);
        initTypes();
    }

    private void initTypes() {
        ArrayList<String> formulaTypes = new ArrayList<>();
        ArrayList<String> databaseTypes = new ArrayList<>();

        for (FormulaType type : FormulaType.values()) {
            formulaTypes.add(type.toString());
        }

        for (DatabaseType type : DatabaseType.values()) {
            databaseTypes.add(type.toString());
        }

        // Custom stuff
        databaseTypes.remove(DatabaseType.CUSTOM.toString());

        if (pluginRef.getDatabaseManager().getDatabaseType() == DatabaseType.CUSTOM) {
            databaseTypes.add(DatabaseManagerFactory.getCustomDatabaseManagerClass().getName());
        }

        Collections.sort(formulaTypes);
        Collections.sort(databaseTypes);

        FORMULA_TYPES = ImmutableList.copyOf(formulaTypes);
        DATABASE_TYPES = ImmutableList.copyOf(databaseTypes);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        switch (args.length) {
            case 2:
                if (args[0].equalsIgnoreCase("database") || args[0].equalsIgnoreCase("db")) {
                    return databaseConvertCommand.onCommand(sender, command, label, args);
                } else if (args[0].equalsIgnoreCase("experience") || args[0].equalsIgnoreCase("xp") || args[1].equalsIgnoreCase("exp")) {
                    return experienceConvertCommand.onCommand(sender, command, label, args);
                }

                return false;
            default:
                return false;
        }
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        switch (args.length) {
            case 1:
                return StringUtil.copyPartialMatches(args[0], SUBCOMMANDS, new ArrayList<>(SUBCOMMANDS.size()));
            case 2:
                if (args[1].equalsIgnoreCase("database") || args[1].equalsIgnoreCase("db")) {
                    return StringUtil.copyPartialMatches(args[0], DATABASE_TYPES, new ArrayList<>(DATABASE_TYPES.size()));
                }

                if (args[1].equalsIgnoreCase("experience") || args[1].equalsIgnoreCase("xp") || args[1].equalsIgnoreCase("exp")) {
                    return StringUtil.copyPartialMatches(args[0], FORMULA_TYPES, new ArrayList<>(FORMULA_TYPES.size()));
                }

                return ImmutableList.of();
            default:
                return ImmutableList.of();
        }
    }
}
