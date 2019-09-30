package com.gmail.nossr50.config.serializers;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Sets;
import com.google.common.reflect.TypeToken;
import ninja.leaping.configurate.ConfigurationNode;
import ninja.leaping.configurate.objectmapping.ObjectMappingException;
import ninja.leaping.configurate.objectmapping.serialize.TypeSerializer;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;

import java.lang.reflect.ParameterizedType;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class SetSerializer implements TypeSerializer<Set<?>> {
    @Nullable
    @Override
    public Set<?> deserialize(@NonNull TypeToken<?> type, @NonNull ConfigurationNode value) throws ObjectMappingException {

        if (!(type.getType() instanceof ParameterizedType)) {
            throw new ObjectMappingException("Raw types are not supported for collections");
        }

        TypeToken<?> entryType = type.resolveType(Set.class.getTypeParameters()[0]);
        TypeSerializer entrySerial = value.getOptions().getSerializers().get(entryType);
        if (entrySerial == null) {
            throw new ObjectMappingException("No applicable type serializer for type " + entryType);
        }

        if (value.hasListChildren()) {
            List<? extends ConfigurationNode> values = value.getChildrenList();
            Set<Object> ret = new HashSet<>(values.size());
            for (ConfigurationNode ent : values) {
                ret.add(entrySerial.deserialize(entryType, ent));
            }
            return ret;
        } else {
            Object unwrappedVal = value.getValue();
            if (unwrappedVal != null) {
                return Sets.newHashSet(deserialize(entryType, value));
            }
        }
        return new HashSet<>();
    }

    @Override
    public void serialize(@NonNull TypeToken<?> type, @Nullable Set<?> obj, @NonNull ConfigurationNode value) throws ObjectMappingException {
        if (!(type.getType() instanceof ParameterizedType)) {
            throw new ObjectMappingException("Raw types are not supported for collections");
        }
        TypeToken<?> entryType = type.resolveType(Set.class.getTypeParameters()[0]);
        TypeSerializer entrySerial = value.getOptions().getSerializers().get(entryType);
        if (entrySerial == null) {
            throw new ObjectMappingException("No applicable type serializer for type " + entryType);
        }
        value.setValue(ImmutableList.of());
        for (Object ent : obj) {
            entrySerial.serialize(entryType, ent, value.getAppendedNode());
        }
    }
}
