package com.gmail.nossr50.config;

import java.util.ArrayList;
import java.util.List;

import com.gmail.nossr50.mcMMO;
import com.gmail.nossr50.skills.repair.Repairable;

public class RepairConfig extends ConfigLoader {
    private final String fileName;
    private List<Repairable> repairables;

    public RepairConfig(mcMMO plugin, String fileName) {
        super(plugin, fileName);
        this.fileName = fileName;
    }

    @Override
    protected void load() {
        if(plugin.isInJar(fileName)) addDefaults();
        loadKeys();
    }

    @Override
    protected void loadKeys() {
        // TODO Auto-generated method stub
    }

    protected List<Repairable> getLoadedRepairables() {
        if(repairables == null) return new ArrayList<Repairable>();
        return repairables;
    }
}
