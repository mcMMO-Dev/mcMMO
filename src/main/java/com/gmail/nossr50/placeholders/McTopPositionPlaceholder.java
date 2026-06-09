package com.gmail.nossr50.placeholders;

import com.gmail.nossr50.datatypes.database.PlayerStat;
import com.gmail.nossr50.datatypes.skills.PrimarySkillType;
import org.bukkit.entity.Player;

import java.util.List;

/**
 * Placeholder for getting the skill level/score at a specific position in the leaderboard
 * Usage: %mcmmo_mctop_<skill>:<position>% or %mcmmo_mctop_overall:<position>%
 */
public class McTopPositionPlaceholder implements Placeholder {
    private final PapiExpansion papiExpansion;
    private final PrimarySkillType skill;

    public McTopPositionPlaceholder(PapiExpansion papiExpansion, PrimarySkillType skill) {
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

        final int statsPerPage = 10; // Adjust if your leaderboard uses a different page size
        List<PlayerStat> leaderboard = papiExpansion.getLeaderboard(skill, position);
        
        if (leaderboard == null || leaderboard.isEmpty()) {
            return "";
        }
        int pageIndex = (position - 1) % statsPerPage;
        if (pageIndex >= leaderboard.size()) {
            return "";
        }
        PlayerStat stat = leaderboard.get(pageIndex);
        return String.valueOf(stat.value());
    }

    @Override
    public String getName() {
        if (skill == null) {
            return "mctop_overall";
        }
        return "mctop_" + skill.toString().toLowerCase();
    }
}
