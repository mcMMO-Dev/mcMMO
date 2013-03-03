package com.gmail.nossr50.datatypes.skills;

public class Ability {
    protected boolean mode;
    protected boolean informed = true;

    public boolean getMode() {
        return mode;
    }

    public void setMode(boolean mode) {
        this.mode = mode;
    }

    public boolean getInformed() {
        return informed;
    }

    public void setInformed(boolean informed) {
        this.informed = informed;
    }
}
