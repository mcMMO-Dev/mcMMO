package com.gmail.nossr50.config.hocon;

import com.google.common.reflect.TypeToken;
import ninja.leaping.configurate.ConfigurationNode;
import ninja.leaping.configurate.objectmapping.ObjectMappingException;
import ninja.leaping.configurate.objectmapping.serialize.TypeSerializer;
import ninja.leaping.configurate.util.EnumLookup;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;

import java.util.Optional;

public class CustomEnumValueSerializer implements TypeSerializer<Enum> {
    @Override
    @SuppressWarnings("unchecked") // i continue to hate generics
    public Enum deserialize(TypeToken<?> type, ConfigurationNode value) throws ObjectMappingException {
        String enumConstant = HOCONUtil.deserializeENUMName(value.getString());
        if (enumConstant == null) {
            throw new ObjectMappingException("No value present in node " + value);
        }

        Optional<Enum> ret = (Optional) EnumLookup.lookupEnum(type.getRawType().asSubclass(Enum.class),
                enumConstant); // XXX: intellij says this cast is optional but it isnt
        if (!ret.isPresent()) {
            throw new ObjectMappingException("Invalid enum constant provided for " + value.getKey() + ": " +
                    "Expected a value of enum " + type + ", got " + enumConstant);
        }
        return ret.get();
    }

    @Override
    public void serialize(@NonNull TypeToken<?> type, @Nullable Enum obj, @NonNull ConfigurationNode value) throws ObjectMappingException {
        value.setValue(HOCONUtil.serializeENUMName(obj.name()));
    }
}
