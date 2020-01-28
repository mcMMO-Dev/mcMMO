package com.gmail.nossr50.config.serializers;

import com.gmail.nossr50.config.skills.ranks.SkillRankProperty;
import com.google.common.reflect.TypeToken;
import ninja.leaping.configurate.ConfigurationNode;
import ninja.leaping.configurate.objectmapping.ObjectMappingException;
import ninja.leaping.configurate.objectmapping.serialize.TypeSerializer;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;

import java.util.HashMap;
import java.util.Map;

public class SkillRankPropertySerializer implements TypeSerializer<SkillRankProperty> {

    private static final String RANK_UNLOCK_LEVEL_REQUIREMENTS = "Standard-Rank-Unlock-Level-Requirements";

    @Nullable
    @Override
    public SkillRankProperty deserialize(@NonNull TypeToken<?> type, @NonNull ConfigurationNode value) throws ObjectMappingException {
        HashMap<Integer, Integer> standardHashMap;

        try {
            Map<? extends Integer, ? extends Integer> standardMap = value.getNode(RANK_UNLOCK_LEVEL_REQUIREMENTS).getValue(new TypeToken<Map<? extends Integer, ? extends Integer>>() {});

            standardHashMap = new HashMap<>(standardMap);

        } catch (ObjectMappingException e) {
            System.out.println("[mcMMO Deserializer Debug] Unable to deserialize rank property information from the config, make sure the ranks are correctly set in the config. You can delete the rank config to generate a new one if problems persist.");
            throw e;
        }

        SkillRankProperty skillRankProperty = new SkillRankProperty();
        skillRankProperty.setRanks(standardHashMap);

        return skillRankProperty;
    }

    @Override
    public void serialize(@NonNull TypeToken<?> type, @Nullable SkillRankProperty obj, @NonNull ConfigurationNode value) throws ObjectMappingException {
        value.getNode(RANK_UNLOCK_LEVEL_REQUIREMENTS).setValue(obj.getRanks());
    }
}
