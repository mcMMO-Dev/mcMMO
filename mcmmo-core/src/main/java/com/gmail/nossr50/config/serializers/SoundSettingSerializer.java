package com.gmail.nossr50.config.serializers;

import com.gmail.nossr50.config.sound.SoundSetting;
import com.google.common.reflect.TypeToken;
import ninja.leaping.configurate.ConfigurationNode;
import ninja.leaping.configurate.ValueType;
import ninja.leaping.configurate.objectmapping.ObjectMappingException;
import ninja.leaping.configurate.objectmapping.serialize.TypeSerializer;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;

public class SoundSettingSerializer implements TypeSerializer<SoundSetting> {

    private static final String VOLUME_NODE = "Volume";
    private static final String PITCH_NODE = "Pitch";
    public static final String ENABLED_NODE = "Enabled";

    @Nullable
    @Override
    public SoundSetting deserialize(@NonNull TypeToken<?> type, @NonNull ConfigurationNode value) throws ObjectMappingException {
        float volume = 1.0f;
        float pitch = 1.0f;
        boolean enabled = true;

        if(value.getNode(ENABLED_NODE).getValueType() != ValueType.NULL) {
            enabled = value.getNode(ENABLED_NODE).getValue(TypeToken.of(Boolean.class));
        }

        if(value.getNode(VOLUME_NODE).getValueType() != ValueType.NULL) {
            volume = (float) value.getNode(VOLUME_NODE).getValue(TypeToken.of(Double.class)).doubleValue();
        }

        if(value.getNode(PITCH_NODE).getValueType() != ValueType.NULL) {
            volume = (float) value.getNode(PITCH_NODE).getValue(TypeToken.of(Double.class)).doubleValue();
        }

        SoundSetting soundSetting = new SoundSetting(enabled, volume, pitch);
        return soundSetting;
    }

    @Override
    public void serialize(@NonNull TypeToken<?> type, @Nullable SoundSetting obj, @NonNull ConfigurationNode value) throws ObjectMappingException {
        value.getNode(ENABLED_NODE).setValue(obj.isEnabled());
        value.getNode(VOLUME_NODE).setValue(obj.getVolume());
        value.getNode(PITCH_NODE).setValue(obj.getPitch());
    }

}
