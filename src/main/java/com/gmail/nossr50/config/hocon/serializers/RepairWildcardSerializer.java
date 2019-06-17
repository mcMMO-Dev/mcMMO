package com.gmail.nossr50.config.hocon.serializers;

import com.gmail.nossr50.config.hocon.skills.repair.RepairWildcard;
import com.google.common.reflect.TypeToken;
import ninja.leaping.configurate.ConfigurationNode;
import ninja.leaping.configurate.ValueType;
import ninja.leaping.configurate.objectmapping.ObjectMappingException;
import ninja.leaping.configurate.objectmapping.serialize.TypeSerializer;
import org.bukkit.inventory.ItemStack;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;

import java.util.ArrayList;
import java.util.Set;

public class RepairWildcardSerializer implements TypeSerializer<RepairWildcard> {

    private static final String WILDCARD_IDENTIFIER_NAME = "Wildcard-Identifier-Name";
    private static final String MATCHING_ITEMS = "Matching-Items";

    @Nullable
    @Override
    public RepairWildcard deserialize(@NonNull TypeToken<?> type, @NonNull ConfigurationNode value) throws ObjectMappingException {

        String wildCardName = value.getNode(WILDCARD_IDENTIFIER_NAME).getValue(TypeToken.of(String.class));

        if(value.getNode(WILDCARD_IDENTIFIER_NAME).getNode(MATCHING_ITEMS).getValueType() != ValueType.NULL) {
            Set<ItemStack> matchCandidates = value.getNode(WILDCARD_IDENTIFIER_NAME).getNode(MATCHING_ITEMS).getValue(new TypeToken<Set<ItemStack>>() {});

            return new RepairWildcard(wildCardName, matchCandidates);
        }

        return null;
    }

    @Override
    public void serialize(@NonNull TypeToken<?> type, @Nullable RepairWildcard obj, @NonNull ConfigurationNode value) throws ObjectMappingException {
        value.getNode(WILDCARD_IDENTIFIER_NAME).setValue(obj.getWildcardName());
        value.getNode(WILDCARD_IDENTIFIER_NAME).getNode(MATCHING_ITEMS).setValue(obj.getMatchingItems());
    }

}
