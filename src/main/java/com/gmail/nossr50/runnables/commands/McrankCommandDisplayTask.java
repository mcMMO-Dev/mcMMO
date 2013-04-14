package com.gmail.nossr50.runnables.commands;

import java.util.Map;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import com.gmail.nossr50.mcMMO;
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
        Player player = mcMMO.p.getServer().getPlayer(playerName);
        Integer rank;

        sender.sendMessage(LocaleLoader.getString("Commands.mcrank.Heading"));
        sender.sendMessage(LocaleLoader.getString("Commands.mcrank.Player", playerName));

        for (SkillType skill : SkillType.values()) {
            if (skill.isChildSkill() || (player != null && !Permissions.skillEnabled(player, skill))) {
                continue;
            }

            rank = skills.get(skill.name());
            sender.sendMessage(LocaleLoader.getString("Commands.mcrank.Skill", SkillUtils.getSkillName(skill), (rank == null ? LocaleLoader.getString("Commands.mcrank.Unranked") : rank)));
        }

        rank = skills.get("ALL");
        sender.sendMessage(LocaleLoader.getString("Commands.mcrank.Overall", (rank == null ? LocaleLoader.getString("Commands.mcrank.Unranked") : rank)));
    }
}
