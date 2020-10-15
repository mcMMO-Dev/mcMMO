package com.gmail.nossr50.util.player;

import com.gmail.nossr50.config.Config;
import com.gmail.nossr50.datatypes.party.Party;
import com.gmail.nossr50.datatypes.party.PartyFeature;
import org.jetbrains.annotations.NotNull;

public class PartyUtils {
    public static boolean isAllowed(@NotNull Party party, @NotNull PartyFeature partyFeature) {
        return party.getPartyExperienceManager().getLevel() >= Config.getInstance().getPartyFeatureUnlockLevel(partyFeature);
    }
}
