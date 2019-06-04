package com.gmail.nossr50.config.hocon;

import com.gmail.nossr50.config.hocon.skills.ranks.SkillRankProperty;
import com.gmail.nossr50.mcMMO;
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
        HashMap<Integer, Integer> standardMap;
        HashMap<Integer, Integer> retroMap;

        try {
            standardMap = (HashMap<Integer, Integer>) value.getNode(STANDARD_RANK_UNLOCK_LEVEL_REQUIREMENTS).getValue(new TypeToken<Map<?, ?>>() {});
            retroMap = (HashMap<Integer, Integer>) value.getNode(RETRO_RANK_UNLOCK_LEVEL_REQUIREMENTS).getValue(new TypeToken<Map<?, ?>>() {});
        } catch (ObjectMappingException e) {
            mcMMO.p.getLogger().severe("Unable to deserialize rank property information from the config, make sure the ranks are correctly set in the config. You can delete the rank config to generate a new one if problems persist.");
            throw e;
        }

        SkillRankProperty skillRankProperty = new SkillRankProperty();
        skillRankProperty.setStandardRanks(standardMap);
        skillRankProperty.setRetroRanks(retroMap);

        return skillRankProperty;
    }

    @Override
    public void serialize(@NonNull TypeToken<?> type, @Nullable SkillRankProperty obj, @NonNull ConfigurationNode value) throws ObjectMappingException {
        value.getNode(STANDARD_RANK_UNLOCK_LEVEL_REQUIREMENTS).setValue(obj.getStandardRanks());
        value.getNode(RETRO_RANK_UNLOCK_LEVEL_REQUIREMENTS).setValue(obj.getStandardRanks());
    }
}
