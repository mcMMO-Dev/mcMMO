package com.gmail.nossr50.party;

import com.gmail.nossr50.config.Config;
import com.gmail.nossr50.datatypes.skills.PrimarySkillType;
import com.gmail.nossr50.mcMMO;
import com.neetgames.mcmmo.experience.XPGainReason;
import com.neetgames.mcmmo.experience.XPGainSource;
import com.neetgames.mcmmo.party.Party;
import com.neetgames.mcmmo.player.OnlineMMOPlayer;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public final class ShareHandler {
    private ShareHandler() {}

    /**
     * Distribute Xp amongst party members.
     *
     * @param xp Xp without party sharing
     * @param mmoPlayer Player initiating the Xp gain
     * @param primarySkillType Skill being used
     * @return True is the xp has been shared
     */
    public static boolean handleXpShare(float xp, @NotNull OnlineMMOPlayer mmoPlayer, @NotNull Party party, @NotNull PrimarySkillType primarySkillType, @NotNull XPGainReason xpGainReason) {

        if (party.getPartyExperienceManager().getXpShareMode() != ShareMode.EQUAL) {
            return false;
        }

        List<Player> nearMembers = mcMMO.getPartyManager().getNearVisibleMembers(mmoPlayer);

        if (nearMembers.isEmpty()) {
            return false;
        }

        nearMembers.add(mmoPlayer.getPlayer());

        int partySize = nearMembers.size();
        double shareBonus = Math.min(Config.getInstance().getPartyShareBonusBase() + (partySize * Config.getInstance().getPartyShareBonusIncrease()), Config.getInstance().getPartyShareBonusCap());
        float splitXp = (float) (xp / partySize * shareBonus);

        for (Player otherMember : nearMembers) {
            OnlineMMOPlayer partyMember = UserManager.queryPlayer(otherMember);

            //Profile not loaded
            if(partyMember == null) {
                continue;
            }

            partyMember.getExperienceHandler().beginUnsharedXpGain(primarySkillType, splitXp, xpGainReason, XPGainSource.PARTY_MEMBERS);
        }

        return true;
    }

    public static XPGainReason getSharedXpGainReason(XPGainReason xpGainReason) {
        if (xpGainReason == XPGainReason.PVE) {
            return XPGainReason.SHARED_PVE;
        }
        else if (xpGainReason == XPGainReason.PVP) {
            return XPGainReason.SHARED_PVP;
        }
        else {
            return xpGainReason;
        }
    }
}
