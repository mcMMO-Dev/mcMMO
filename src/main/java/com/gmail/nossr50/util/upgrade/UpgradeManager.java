package com.gmail.nossr50.util.upgrade;

import com.gmail.nossr50.config.BukkitConfig;
import com.gmail.nossr50.datatypes.database.UpgradeType;
import com.gmail.nossr50.mcMMO;
import com.gmail.nossr50.util.LogUtils;
import java.util.Arrays;
import java.util.EnumSet;
import java.util.Set;

public class UpgradeManager extends BukkitConfig {
    private final Set<UpgradeType> setNeededUpgrades;

    public UpgradeManager() {
        super("upgrades_overhaul.yml"); //overhaul is added so we don't have any issues with classic

        setNeededUpgrades = EnumSet.allOf(UpgradeType.class);

        loadKeys();
    }

    /**
     * Check if the given {@link UpgradeType} is necessary.
     *
     * @param type Upgrade type to check
     * @return true if plugin data needs to have the given upgrade
     */
    public boolean shouldUpgrade(final UpgradeType type) {
        return setNeededUpgrades.contains(type);
    }

    /**
     * Set the given {@link UpgradeType} as completed. Does nothing if the upgrade was applied
     * previously.
     *
     * @param type Upgrade type to set as complete
     */
    public void setUpgradeCompleted(final UpgradeType type) {
        if (!setNeededUpgrades.remove(type)) {
            return;
        }

        LogUtils.debug(mcMMO.p.getLogger(),
                "Saving upgrade status for type " + type.toString() + "...");

        config.set("Upgrades_Finished." + type, true);

        try {
            config.save(getFile());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void loadKeys() {
        for (UpgradeType type : UpgradeType.values()) {
            if (config.getBoolean("Upgrades_Finished." + type.toString())) {
                setNeededUpgrades.remove(type);
            }
        }

        LogUtils.debug(mcMMO.p.getLogger(), "Needed upgrades: " + Arrays.toString(
                setNeededUpgrades.toArray(new UpgradeType[setNeededUpgrades.size()])));
    }
}
