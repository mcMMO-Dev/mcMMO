package com.gmail.nossr50.commands;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.command.PluginCommand;

import com.gmail.nossr50.mcMMO;
import com.gmail.nossr50.locale.LocaleLoader;
import com.gmail.nossr50.skills.acrobatics.AcrobaticsCommand;
import com.gmail.nossr50.skills.archery.ArcheryCommand;
import com.gmail.nossr50.skills.axes.AxesCommand;
import com.gmail.nossr50.skills.excavation.ExcavationCommand;
import com.gmail.nossr50.skills.fishing.FishingCommand;
import com.gmail.nossr50.skills.herbalism.HerbalismCommand;
import com.gmail.nossr50.skills.mining.MiningCommand;
import com.gmail.nossr50.skills.repair.RepairCommand;
import com.gmail.nossr50.skills.smelting.SmeltingCommand;
import com.gmail.nossr50.skills.swords.SwordsCommand;
import com.gmail.nossr50.skills.taming.TamingCommand;
import com.gmail.nossr50.skills.unarmed.UnarmedCommand;
import com.gmail.nossr50.skills.utilities.SkillType;
import com.gmail.nossr50.skills.woodcutting.WoodcuttingCommand;
import com.gmail.nossr50.util.Misc;

public final class CommandRegistrationHelper {
    private CommandRegistrationHelper() {};

    public static void registerSkillCommands() {
        for (SkillType skill : SkillType.values()) {
            if (skill != SkillType.ALL) {
                String commandName = skill.toString().toLowerCase();
                String localizedName = LocaleLoader.getString(Misc.getCapitalized(commandName) + ".SkillName").toLowerCase();

                List<String> aliasList = new ArrayList<String>();
                aliasList.add(localizedName);

                PluginCommand command;

                // Make us play nice with Essentials
                if (skill == SkillType.REPAIR && mcMMO.p.getServer().getPluginManager().isPluginEnabled("Essentials")) {
                    command = mcMMO.p.getCommand("mcrepair");
                }
                else {
                    command = mcMMO.p.getCommand(commandName);
                }

                command.setAliases(aliasList);
                command.setDescription(LocaleLoader.getString("Commands.Description.Skill", new Object[] { Misc.getCapitalized(localizedName) }));
                command.setPermission("mcmmo.skills." + commandName);
                command.setPermissionMessage(LocaleLoader.getString("mcMMO.NoPermission"));

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
    }
}
