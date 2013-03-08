package com.gmail.nossr50.database.queuemanager;

import java.util.Map;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;

import com.gmail.nossr50.mcMMO;
import com.gmail.nossr50.database.DatabaseManager;
import com.gmail.nossr50.locale.LocaleLoader;
import com.gmail.nossr50.util.skills.SkillUtils;
import com.gmail.nossr50.datatypes.skills.SkillType;

public class McRankAsync implements Queueable {
    private final String playerName;
    private final CommandSender sender;
    private String player;

    public McRankAsync(String playerName, CommandSender sender) {
        this.playerName = playerName;
        this.sender = sender;
        this.player = sender.getName();
    }

    @Override
    public void run() {
        final Map<String, Integer> skills = DatabaseManager.readSQLRank(playerName);

        Bukkit.getScheduler().scheduleSyncDelayedTask(mcMMO.p, new Runnable() {
            @Override
            public void run() {
                sender.sendMessage(LocaleLoader.getString("Commands.mcrank.Heading"));
                sender.sendMessage(LocaleLoader.getString("Commands.mcrank.Player", playerName));

                for (SkillType skillType : SkillType.values()) {
                    if (skillType.isChildSkill()) {
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

        }, 1L);
    }
<<<<<<< HEAD

    @Override
    public String getPlayer() {
        return player;
=======
    
    @Override
<<<<<<< HEAD
    public boolean equals(Object obj) {
        if (obj instanceof String) {
            return ((String)obj).equalsIgnoreCase(player);
        }
        return false;
>>>>>>> One per player
=======
    public String getPlayer() {
        return player;
>>>>>>> String wrapper
    }
}
