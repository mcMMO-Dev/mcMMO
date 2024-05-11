package com.gmail.nossr50.skills.tridents;

import com.gmail.nossr50.datatypes.player.McMMOPlayer;
import com.gmail.nossr50.datatypes.skills.PrimarySkillType;
import com.gmail.nossr50.datatypes.skills.SubSkillType;
import com.gmail.nossr50.datatypes.skills.ToolType;
import com.gmail.nossr50.skills.SkillManager;
import com.gmail.nossr50.util.Permissions;
import com.gmail.nossr50.util.skills.RankUtils;

public class TridentsManager extends SkillManager {
    public TridentsManager(McMMOPlayer mmoPlayer) {
        super(mmoPlayer, PrimarySkillType.TRIDENTS);
    }

    public double impaleDamageBonus() {
        int rank = RankUtils.getRank(getPlayer(), SubSkillType.TRIDENTS_IMPALE);

        if(rank > 1) {
            return (1.0D + (rank * .5D));
        } else if(rank == 1) {
            return 1.0D;
        }

        return 0.0D;
    }
}
