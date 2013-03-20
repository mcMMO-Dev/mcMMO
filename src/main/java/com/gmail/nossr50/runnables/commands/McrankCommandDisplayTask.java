package com.gmail.nossr50.runnables.commands;

import java.util.Map;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import com.gmail.nossr50.datatypes.skills.SkillType;
import com.gmail.nossr50.locale.LocaleLoader;
import com.gmail.nossr50.util.Permissions;
import com.gmail.nossr50.util.skills.SkillUtils;

public class McrankCommandDisplayTask extends BukkitRunnable {
    private final Map<String, Integer> skills;
    private final CommandSender sender;
    private final String playerName;

    public McrankCommandDisplayTask(Map<String, Integer> skills, CommandSender sender, String playerName) {
        this.skills = skills;
        this.sender = sender;
        this.playerName = playerName;
    }

    @Override
    public void run() {
        sender.sendMessage(LocaleLoader.getString("Commands.mcrank.Heading"));
        sender.sendMessage(LocaleLoader.getString("Commands.mcrank.Player", playerName));

        for (SkillType skillType : SkillType.values()) {
            if ((sender instanceof Player && !Permissions.skillEnabled(sender, skillType)) || skillType.isChildSkill()) {
                continue;
            }

            if (skills.get(skillType.name()) == null) {
                sender.sendMessage(LocaleLoader.getString("Commands.mcrank.Skill", SkillUtils.getSkillName(skillType), LocaleLoader.getString("Commands.mcrank.Unranked")));
            }
            else {
                sender.sendMessage(LocaleLoader.getString("Commands.mcrank.Skill", SkillUtils.getSkillName(skillType), skills.get(skillType.name())));
            }
        }

        if (skills.get("ALL") == null) {
            sender.sendMessage(LocaleLoader.getString("Commands.mcrank.Overall", LocaleLoader.getString("Commands.mcrank.Unranked")));
        }
        else {
            sender.sendMessage(LocaleLoader.getString("Commands.mcrank.Overall", skills.get("ALL")));
        }
    }
}
