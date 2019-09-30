package com.gmail.nossr50.config.hocon.serializers;

import com.gmail.nossr50.config.hocon.skills.ranks.SkillRankProperty;
import com.google.common.reflect.TypeToken;
import ninja.leaping.configurate.ConfigurationNode;
import ninja.leaping.configurate.objectmapping.ObjectMappingException;
import ninja.leaping.configurate.objectmapping.serialize.TypeSerializer;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;

import java.util.HashMap;
import java.util.Map;

public class SkillRankPropertySerializer implements TypeSerializer<SkillRankProperty> {

    private static final String STANDARD_RANK_UNLOCK_LEVEL_REQUIREMENTS = "Standard-Rank-Unlock-Level-Requirements";
    private static final String RETRO_RANK_UNLOCK_LEVEL_REQUIREMENTS = "Retro-Rank-Unlock-Level-Requirements";

    @Nullable
    @Override
    public SkillRankProperty deserialize(@NonNull TypeToken<?> type, @NonNull ConfigurationNode value) throws ObjectMappingException {
        HashMap<Integer, Integer> standardHashMap;
        HashMap<Integer, Integer> retroHashMap;

        try {
            Map<? extends Integer, ? extends Integer> standardMap = value.getNode(STANDARD_RANK_UNLOCK_LEVEL_REQUIREMENTS).getValue(new TypeToken<Map<? extends Integer, ? extends Integer>>() {});
            Map<? extends Integer, ? extends Integer> retroMap = value.getNode(RETRO_RANK_UNLOCK_LEVEL_REQUIREMENTS).getValue(new TypeToken<Map<? extends Integer, ? extends Integer>>() {});

            standardHashMap = new HashMap<>(standardMap);
            retroHashMap = new HashMap<>(retroMap);

        } catch (ObjectMappingException e) {
            System.out.println("[mcMMO Deserializer Debug] Unable to deserialize rank property information from the config, make sure the ranks are correctly set in the config. You can delete the rank config to generate a new one if problems persist.");
            throw e;
        }

        SkillRankProperty skillRankProperty = new SkillRankProperty();
        skillRankProperty.setStandardRanks(standardHashMap);
        skillRankProperty.setRetroRanks(retroHashMap);

        return skillRankProperty;
    }

    @Override
    public void serialize(@NonNull TypeToken<?> type, @Nullable SkillRankProperty obj, @NonNull ConfigurationNode value) throws ObjectMappingException {
        value.getNode(STANDARD_RANK_UNLOCK_LEVEL_REQUIREMENTS).setValue(obj.getStandardRanks());
        value.getNode(RETRO_RANK_UNLOCK_LEVEL_REQUIREMENTS).setValue(obj.getRetroRanks());
    }
}
