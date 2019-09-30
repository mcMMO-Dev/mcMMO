package com.gmail.nossr50.config.hocon.serializers;

import com.gmail.nossr50.api.exceptions.InvalidSkillException;
import com.gmail.nossr50.datatypes.experience.CustomXPPerk;
import com.gmail.nossr50.datatypes.skills.PrimarySkillType;
import com.google.common.reflect.TypeToken;
import ninja.leaping.configurate.ConfigurationNode;
import ninja.leaping.configurate.objectmapping.ObjectMappingException;
import ninja.leaping.configurate.objectmapping.serialize.TypeSerializer;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;

import java.util.HashMap;
import java.util.Map;

public class CustomXPPerkSerializer implements TypeSerializer<CustomXPPerk> {

    private static final String PERK_NAME = "perk-name";
    private static final String XP_BOOST_NODE_ROOT = "XP-Boosts";

    @Nullable
    @Override
    public CustomXPPerk deserialize(@NonNull TypeToken<?> type, @NonNull ConfigurationNode value) throws ObjectMappingException {
        String perkName = value.getNode(PERK_NAME).getValue(TypeToken.of(String.class));

        Map<PrimarySkillType, Double> map = value.getNode(XP_BOOST_NODE_ROOT).getValue(new TypeToken<Map<PrimarySkillType, Double>>(){});
        HashMap<PrimarySkillType, Double> xpBoostHashMap = new HashMap<>(map);

        CustomXPPerk customXPPerk = new CustomXPPerk(perkName);
        customXPPerk.setCustomXPMultiplierMap(xpBoostHashMap);

        return customXPPerk;
    }

    @Override
    public void serialize(@NonNull TypeToken<?> type, @Nullable CustomXPPerk obj, @NonNull ConfigurationNode value) throws ObjectMappingException {
        String name = obj.getPerkName();

        HashMap<PrimarySkillType, Double> xpBoostMap = new HashMap<>();

        value.getNode(PERK_NAME).setValue(name);

        for (PrimarySkillType primarySkillType : PrimarySkillType.values()) {
            Double xpMultValue = obj.getXPMultiplierValue(primarySkillType);

            //Ignore default values
            if (xpMultValue == 1.0)
                continue;

            xpBoostMap.put(primarySkillType, obj.getXPMultiplierValue(primarySkillType));
        }

        value.getNode(XP_BOOST_NODE_ROOT).setValue(xpBoostMap);
    }

    private PrimarySkillType matchIgnoreCase(String string) throws InvalidSkillException {
        for (PrimarySkillType primarySkillType : PrimarySkillType.values()) {
            if (string.equalsIgnoreCase(primarySkillType.toString()))
                return primarySkillType;
        }

        throw new InvalidSkillException(string);
    }
}
