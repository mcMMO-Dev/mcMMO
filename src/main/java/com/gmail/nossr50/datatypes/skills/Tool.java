package com.gmail.nossr50.datatypes.skills;

import java.util.HashMap;
import java.util.Map;

import com.gmail.nossr50.util.Misc;

public class Tool {
    private static Map<ToolType, Tool> tools = new HashMap<ToolType, Tool>();
    private boolean preparationMode = true;
    private long preparationATS;

    private Tool(ToolType toolType) {
        tools.put(toolType, this);
    }

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

    public static Tool getTool(ToolType toolType) {
        Tool tool = tools.get(toolType);

        if (tool == null) {
            tool = new Tool(toolType);
        }

        return tool;
    }
}
