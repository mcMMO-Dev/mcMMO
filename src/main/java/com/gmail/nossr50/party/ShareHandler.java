package com.gmail.nossr50.party;

import java.util.List;

import org.bukkit.entity.Player;

import com.gmail.nossr50.config.Config;
import com.gmail.nossr50.skills.utilities.SkillType;
import com.gmail.nossr50.util.Users;

public final class ShareHandler {
    public enum XpShareMode {
        NONE,
        EQUAL;

        public static XpShareMode getFromString(String string) {
            try {
                return valueOf(string);
            }
            catch (IllegalArgumentException exception) {
                return NONE;
            }
        }
    };

    private static boolean running; // Used to prevent permanent sharing, McMMOPlayer.addXp() uses it

    private ShareHandler() {}

    /**
     * Distribute XP amongst party members.
     *
     * @param xp XP without party sharing
     */
    public static void handleEqualXpShare(int xp, Player player, Party party, SkillType skillType) {
        running = true;
        int newExp = xp;

        if (party.getXpShareMode() == XpShareMode.EQUAL) {
            List<Player> nearMembers = PartyManager.getNearMembers(player, party, Config.getInstance().getPartyShareRange());

            if (nearMembers.size() > 0) {
                newExp = (int) ((xp / (nearMembers.size() + 1)) * Config.getInstance().getPartyShareBonus());
            }

            for (Player member : nearMembers) {
                Users.getPlayer(member).addXp(skillType, newExp);
            }
        }

        running = false;
    }

    public static boolean isRunning() {
        return running;
    }
}