package com.gmail.nossr50.party;

import java.util.List;

import org.bukkit.entity.Player;

import com.gmail.nossr50.config.Config;
import com.gmail.nossr50.datatypes.McMMOPlayer;
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
            catch (NullPointerException exception) {
                return NONE;
            }
        }
    };

    private ShareHandler() {}

    /**
     * Distribute Xp amongst party members.
     *
     * @param xp Xp without party sharing
     * @param mcMMOPlayer Player initiating the Xp gain
     * @param skillType Skill being used
     * @return True is the xp has been shared
     */
    public static boolean handleXpShare(int xp, McMMOPlayer mcMMOPlayer, SkillType skillType) {
        Party party = mcMMOPlayer.getParty();

        switch (party.getXpShareMode()) {
        case EQUAL:
            Player player = mcMMOPlayer.getPlayer();
            List<Player> nearMembers = PartyManager.getNearMembers(player, party, Config.getInstance().getPartyShareRange());

            if (nearMembers.isEmpty()) {
                return false;
            }

            double partySize = nearMembers.size() + 1;
            double splitXp = xp / partySize * Config.getInstance().getPartyShareBonus();
            int roundedXp = (int) Math.ceil(splitXp);

            for (Player member : nearMembers) {
                Users.getPlayer(member).beginUnsharedXpGain(skillType, roundedXp);
            }

            mcMMOPlayer.beginUnsharedXpGain(skillType, roundedXp);

            return true;
        case NONE:
        default:
            return false;
        }
    }
}

