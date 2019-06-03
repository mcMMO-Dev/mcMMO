package com.gmail.nossr50.datatypes.skills.properties;

import com.gmail.nossr50.datatypes.skills.SubSkillType;

public abstract class AbstractScalingProperty implements ScalingProperty {
    public SubSkillType subSkillType;

    public AbstractScalingProperty(SubSkillType subSkillType) {
        super();
        this.subSkillType = subSkillType;
    }

    @Override
    public String toString() {
        return "AbstractScalingProperty{" +
                "subSkillType=" + subSkillType +
                '}';
    }
}
