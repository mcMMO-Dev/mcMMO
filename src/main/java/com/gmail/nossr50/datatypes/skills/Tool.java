package com.gmail.nossr50.datatypes.skills;

import com.gmail.nossr50.util.Misc;

public class Tool {
    private boolean preparationMode = true;
    private long preparationATS;

    public boolean getPreparationMode() {
        return preparationMode;
    }

    public void setPreparationMode(boolean preparationMode) {
        this.preparationMode = preparationMode;
    }

    public long getPreparationATS() {
        return preparationATS;
    }

    public void setPreparationATS(long toolPreparationATS) {
        int startTime = (int) (toolPreparationATS / Misc.TIME_CONVERSION_FACTOR);

        preparationATS = startTime;
    }
}
