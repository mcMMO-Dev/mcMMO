package com.gmail.nossr50.skills.tridents;

import com.gmail.nossr50.datatypes.player.McMMOPlayer;
import com.gmail.nossr50.datatypes.skills.PrimarySkillType;
import com.gmail.nossr50.datatypes.skills.ToolType;
import com.gmail.nossr50.skills.SkillManager;
import com.gmail.nossr50.util.Permissions;

public class TridentsManager extends SkillManager {
    public TridentsManager(McMMOPlayer mmoPlayer) {
        super(mmoPlayer, PrimarySkillType.TRIDENTS);
    }

    /**
     * Checks if the player can activate the Super Ability for Tridents
     * @return true if the player can activate the Super Ability, false otherwise
     */
    public boolean canActivateAbility() {
        return mmoPlayer.getToolPreparationMode(ToolType.TRIDENTS) && Permissions.tridentsSuper(getPlayer());
    }
}
