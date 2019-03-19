package com.gmail.nossr50.config.hocon.notifications;

import ninja.leaping.configurate.objectmapping.Setting;
import ninja.leaping.configurate.objectmapping.serialize.ConfigSerializable;

@ConfigSerializable
public class ConfigNotifications {

    public static final boolean SUPER_ABILITY_TOOL_NOTIFICATION_DEFAULT = true;

    @Setting(value = "Super-Ability-Tool-Raising-Lowering-Notification",
            comment = "Notifies the player when they go into the tool readying state for super abilities.")
    private boolean superAbilityToolMessage = SUPER_ABILITY_TOOL_NOTIFICATION_DEFAULT;

    public boolean isSuperAbilityToolMessage() {
        return superAbilityToolMessage;
    }
}
