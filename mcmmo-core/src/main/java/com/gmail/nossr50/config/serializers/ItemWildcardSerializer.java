//package com.gmail.nossr50.config.serializers;
//
//import com.gmail.nossr50.datatypes.items.ItemMatch;
//import com.gmail.nossr50.datatypes.items.ItemWildcards;
//import com.google.common.reflect.TypeToken;
//import ninja.leaping.configurate.ConfigurationNode;
//import ninja.leaping.configurate.ValueType;
//import ninja.leaping.configurate.objectmapping.ObjectMappingException;
//import ninja.leaping.configurate.objectmapping.serialize.TypeSerializer;
//import org.checkerframework.checker.nullness.qual.NonNull;
//import org.checkerframework.checker.nullness.qual.Nullable;
//
//import java.util.HashSet;
//import java.util.Set;
//
//public class ItemWildcardSerializer implements TypeSerializer<ItemWildcards> {
//
//    private static final String WILDCARD_IDENTIFIER_NAME = "Wildcard-Identifier-Name";
//    private static final String MATCHING_ITEMS = "Matching-Items";
//
//    @Nullable
//    @Override
//    public ItemWildcards deserialize(@NonNull TypeToken<?> type, @NonNull ConfigurationNode value) throws ObjectMappingException {
//
//        String wildCardName = value.getNode(WILDCARD_IDENTIFIER_NAME).getValue(TypeToken.of(String.class));
//
//        if(value.getNode(WILDCARD_IDENTIFIER_NAME).getNode(MATCHING_ITEMS).getValueType() != ValueType.NULL) {
//            Set<ItemMatch> matchCandidates = value.getNode(WILDCARD_IDENTIFIER_NAME).getNode(MATCHING_ITEMS).getValue(new TypeToken<Set<ItemMatch>>() {});
//
//            return new ItemWildcards(wildCardName, new HashSet<>(matchCandidates));
//        }
//
//        return null;
//    }
//
//    @Override
//    public void serialize(@NonNull TypeToken<?> type, @Nullable ItemWildcards obj, @NonNull ConfigurationNode value) throws ObjectMappingException {
//        value.getNode(WILDCARD_IDENTIFIER_NAME).setValue(obj.getWildcardName());
//        value.getNode(WILDCARD_IDENTIFIER_NAME).getNode(MATCHING_ITEMS).setValue(obj.getItemTargets());
//    }
//
//}
