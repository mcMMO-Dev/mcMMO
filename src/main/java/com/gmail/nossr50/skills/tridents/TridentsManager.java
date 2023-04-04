package com.gmail.nossr50.skills.tridents;

import com.gmail.nossr50.datatypes.player.McMMOPlayer;
import com.gmail.nossr50.datatypes.skills.PrimarySkillType;
import com.gmail.nossr50.skills.SkillManager;

public class TridentsManager extends SkillManager {
    public TridentsManager(McMMOPlayer mmoPlayer) {
        super(mmoPlayer, PrimarySkillType.TRIDENTS);
    }
}
