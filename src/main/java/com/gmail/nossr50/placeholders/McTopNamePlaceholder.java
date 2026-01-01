package com.gmail.nossr50.placeholders;

import com.gmail.nossr50.datatypes.database.PlayerStat;
import com.gmail.nossr50.datatypes.skills.PrimarySkillType;
import org.bukkit.entity.Player;

import java.util.List;

/**
 * Placeholder for getting the player name at a specific position in the leaderboard
 * Usage: %mcmmo_mctop_name_<skill>:<position>% or %mcmmo_mctop_name_overall:<position>%
 */
public class McTopNamePlaceholder implements Placeholder {
    private final PapiExpansion papiExpansion;
    private final PrimarySkillType skill;

    public McTopNamePlaceholder(PapiExpansion papiExpansion, PrimarySkillType skill) {
        this.papiExpansion = papiExpansion;
        this.skill = skill;
    }

    @Override
    public String process(Player player, String params) {
        if (params == null || params.isEmpty()) {
            return "";
        }

        int position;
        try {
            position = Integer.parseInt(params);
        } catch (NumberFormatException e) {
            return "";
        }

        if (position < 1) {
            return "";
        }

        List<PlayerStat> leaderboard = papiExpansion.getLeaderboard(skill, position);
        
        if (leaderboard == null || leaderboard.isEmpty() || position > leaderboard.size()) {
            return "";
        }

        PlayerStat stat = leaderboard.get(position - 1);
        return stat.playerName();
    }

    @Override
    public String getName() {
        if (skill == null) {
            return "mctop_name_overall";
        }
        return "mctop_name_" + skill.toString().toLowerCase();
    }
}
