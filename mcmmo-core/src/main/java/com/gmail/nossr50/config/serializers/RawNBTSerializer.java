//package com.gmail.nossr50.config.serializers;
//
//import com.gmail.nossr50.util.nbt.RawNBT;
//import com.google.common.reflect.TypeToken;
//import ninja.leaping.configurate.ConfigurationNode;
//import ninja.leaping.configurate.objectmapping.ObjectMappingException;
//import ninja.leaping.configurate.objectmapping.serialize.TypeSerializer;
//import org.checkerframework.checker.nullness.qual.NonNull;
//import org.checkerframework.checker.nullness.qual.Nullable;
//
//public class RawNBTSerializer implements TypeSerializer<RawNBT> {
//
//    private static final String NBT = "NBT";
//
//    @Nullable
//    @Override
//    public RawNBT deserialize(@NonNull TypeToken<?> type, @NonNull ConfigurationNode value) throws ObjectMappingException {
//        String nbtString = value.getNode(NBT).getValue(TypeToken.of(String.class));
//        return new RawNBT(nbtString);
//    }
//
//    @Override
//    public void serialize(@NonNull TypeToken<?> type, @Nullable RawNBT obj, @NonNull ConfigurationNode value) throws ObjectMappingException {
//        value.getNode(NBT).setValue(obj.getNbtContents());
//    }
//
//}
