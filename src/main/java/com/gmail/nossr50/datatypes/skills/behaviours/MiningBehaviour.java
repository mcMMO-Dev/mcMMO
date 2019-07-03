package com.gmail.nossr50.datatypes.skills.behaviours;

import com.gmail.nossr50.mcMMO;

/**
 * These behaviour classes are a band-aid fix for a larger problem
 * Until the new skill system for mcMMO is finished/implemented, there is no good place to store the hardcoded behaviours for each skill
 * These behaviour classes server this purpose, they act as a bad solution to a bad problem
 * These classes will be removed when the new skill system is in place
 */
@Deprecated
public class MiningBehaviour {

    private final mcMMO pluginRef;

    public MiningBehaviour(mcMMO pluginRef) {
        this.pluginRef = pluginRef;
    }
}
