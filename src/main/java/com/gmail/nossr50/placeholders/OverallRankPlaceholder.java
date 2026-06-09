package com.gmail.nossr50.placeholders;

import com.gmail.nossr50.api.ExperienceAPI;
import org.bukkit.entity.Player;

/**
 * Placeholder for getting the overall power level rank
 * Usage: %mcmmo_rank_overall%
 */
public class OverallRankPlaceholder implements Placeholder {

    public OverallRankPlaceholder(PapiExpansion papiExpansion) {
        // The papiExpansion parameter is intentionally unused in this class.
        // It is kept for consistency with other Placeholder implementations.
        // If future functionality requires access to papiExpansion, it can be stored as a field.
    }

    @Override
    public String process(Player player, String params) {
        try {
            int rank = ExperienceAPI.getPlayerRankOverall(player.getUniqueId());
            return String.valueOf(rank);
        } catch (Exception ex) {
            return "";
        }
    }

    @Override
    public String getName() {
        return "rank_overall";
    }
}
