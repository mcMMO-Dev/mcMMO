package com.gmail.nossr50.datatypes.skills.properties;

public interface ScalingProperty extends SkillProperty {
    /**
     * Returns the appropriate value for this scaling property whether it is Standard or Retro
     * @return the value used in scaling calculations for this ScalingProperty
     */
    double getValue();
}
