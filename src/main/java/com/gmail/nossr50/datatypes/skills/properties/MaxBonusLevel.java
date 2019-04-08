package com.gmail.nossr50.datatypes.skills.properties;

import com.gmail.nossr50.datatypes.skills.SubSkillType;
import com.gmail.nossr50.mcMMO;

public class MaxBonusLevel extends AbstractScalingProperty {
    public MaxBonusLevel(SubSkillType subSkillType) {
        super(subSkillType);
    }

    /**
     * Returns the appropriate value for this scaling property whether it is Standard or Retro
     *
     * @return the value used in scaling calculations for this ScalingProperty
     */
    @Override
    public double getValue() {
        if(mcMMO.getConfigManager().isRetroMode())
        {

        } else {

        }
    }
}
