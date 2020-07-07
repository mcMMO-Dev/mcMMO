package com.gmail.nossr50.skills.crossbows;

import com.gmail.nossr50.datatypes.player.McMMOPlayer;
import com.gmail.nossr50.datatypes.skills.PrimarySkillType;
import com.gmail.nossr50.skills.SkillManager;

public class CrossbowManager extends SkillManager {
    public CrossbowManager(McMMOPlayer mcMMOPlayer) {
        super(mcMMOPlayer, PrimarySkillType.CROSSBOWS);
    }
}
