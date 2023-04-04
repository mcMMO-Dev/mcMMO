package com.gmail.nossr50.skills.crossbows;

import com.gmail.nossr50.datatypes.player.McMMOPlayer;
import com.gmail.nossr50.datatypes.skills.PrimarySkillType;
import com.gmail.nossr50.skills.SkillManager;

public class CrossbowsManager extends SkillManager {
    public CrossbowsManager(McMMOPlayer mmoPlayer) {
        super(mmoPlayer, PrimarySkillType.CROSSBOWS);
    }
}
