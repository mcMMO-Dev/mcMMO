package com.gmail.nossr50.party;

import java.util.List;

import org.bukkit.entity.Player;

import com.gmail.nossr50.config.Config;
import com.gmail.nossr50.skills.SkillTools;
import com.gmail.nossr50.skills.SkillType;
import com.gmail.nossr50.util.Users;

public class ShareHandler {

    public static boolean expShareEnabled = Config.getInstance().getExpShareEnabled();
    public static boolean itemShareEnabled = Config.getInstance().getItemShareEnabled();
    public static double partyShareRange = Config.getInstance().getPartyShareRange();
    public static double partyShareBonus = Config.getInstance().getPartyShareBonus();

    protected enum PartyShareType {
        NO_SHARE,
        RANDOM,
        EQUAL,
    };

    public static double checkXpSharing(int oldExp, Player player, Party party) {
        int newExp = oldExp;

        if (party.getExpShareMode() == null) {
            party.setExpShareMode("NO_SHARE");
        }

        if (party.getExpShareMode().equals("NO_SHARE")) {
            return newExp;
        }
        else if (party.getExpShareMode().equals("EQUAL")) {
            newExp = (int) calculateSharedExp(oldExp, player, party);
        }

        return newExp;
    }

    /**
     * Calculate the party XP.
     *
     * @param int XP without party sharing
     * @return the party shared XP
     */
    public static double calculateSharedExp(int oldExp, Player player, Party party) {
        int newExp = oldExp;
        List<Player> nearMembers = PartyManager.getNearMembers(player, party, partyShareRange);

        if (nearMembers.size() > 0) {
            newExp = (int) ((oldExp / (nearMembers.size() + 1)) * partyShareBonus);
        }

        return newExp;
    }


    /**
     * Distribute XP amongst party members.
     *
     * @param int XP without party sharing
     * @return the party share experience
     */
    public static void handleEqualExpShare(int xp, Player player, Party party, SkillType skillType) {
        List<Player> nearMembers = PartyManager.getNearMembers(player, party, partyShareRange);

        for (Player member : nearMembers) {
            if (nearMembers.size() > 0) {
                Users.getPlayer(member).addXP(skillType, xp);

                SkillTools.xpCheckSkill(skillType, member, Users.getProfile(member));
            }
        }
    }
}