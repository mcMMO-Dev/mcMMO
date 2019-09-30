package com.gmail.nossr50.config.serializers;

import com.gmail.nossr50.config.notifications.PlayerNotificationSettings;
import com.google.common.reflect.TypeToken;
import ninja.leaping.configurate.ConfigurationNode;
import ninja.leaping.configurate.objectmapping.ObjectMappingException;
import ninja.leaping.configurate.objectmapping.serialize.TypeSerializer;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;

public class PlayerNotificationSerializer implements TypeSerializer<PlayerNotificationSettings> {
    private static final String ENABLED_NODE = "Enabled";
    private static final String SEND_TO_CHAT_NODE = "Send-To-Chat";
    private static final String SEND_TO_ACTION_BAR_NODE = "Send-To-Action-Bar";

    @Nullable
    @Override
    public PlayerNotificationSettings deserialize(@NonNull TypeToken<?> type, @NonNull ConfigurationNode value) throws ObjectMappingException {
        Boolean enabled = value.getNode(ENABLED_NODE).getValue(TypeToken.of(Boolean.class));
        Boolean sendTochat = value.getNode(SEND_TO_CHAT_NODE).getValue(TypeToken.of(Boolean.class));
        Boolean sendToActionBar = value.getNode(SEND_TO_ACTION_BAR_NODE).getValue(TypeToken.of(Boolean.class));

        PlayerNotificationSettings playerNotificationSettings = new PlayerNotificationSettings(enabled, sendTochat, sendToActionBar);
        return playerNotificationSettings;
    }

    @Override
    public void serialize(@NonNull TypeToken<?> type, @Nullable PlayerNotificationSettings obj, @NonNull ConfigurationNode value) throws ObjectMappingException {
        value.getNode(ENABLED_NODE).setValue(obj.isEnabled());
        value.getNode(SEND_TO_CHAT_NODE).setValue(obj.isSendToChat());
        value.getNode(SEND_TO_ACTION_BAR_NODE).setValue(obj.isSendToActionBar());
    }

}
