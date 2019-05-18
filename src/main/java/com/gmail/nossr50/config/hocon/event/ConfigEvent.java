package com.gmail.nossr50.config.hocon.event;

import ninja.leaping.configurate.objectmapping.Setting;
import ninja.leaping.configurate.objectmapping.serialize.ConfigSerializable;

@ConfigSerializable
public class ConfigEvent {

    @Setting(value = "XP-Rate-Event", comment = "Settings relating to XP rate events")
    public ConfigEventExperienceRate xpRate = new ConfigEventExperienceRate();

    public ConfigEventExperienceRate getXpRate() {
        return xpRate;
    }

    public boolean isShowXPRateInfoOnPlayerJoin() {
        return xpRate.isShowXPRateInfoOnPlayerJoin();
    }

    public boolean isBroadcastXPRateEventMessages() {
        return xpRate.isBroadcastXPRateEventMessages();
    }
}
