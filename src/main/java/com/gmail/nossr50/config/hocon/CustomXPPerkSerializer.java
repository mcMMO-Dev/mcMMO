package com.gmail.nossr50.config.hocon;

import com.gmail.nossr50.api.exceptions.InvalidSkillException;
import com.gmail.nossr50.datatypes.experience.CustomXPPerk;
import com.gmail.nossr50.datatypes.skills.PrimarySkillType;
import com.gmail.nossr50.mcMMO;
import com.google.common.reflect.TypeResolver;
import com.google.common.reflect.TypeToken;
import ninja.leaping.configurate.ConfigurationNode;
import ninja.leaping.configurate.objectmapping.ObjectMappingException;
import ninja.leaping.configurate.objectmapping.serialize.TypeSerializer;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;

public class CustomXPPerkSerializer implements TypeSerializer<CustomXPPerk> {

    @Nullable
    @Override
    public CustomXPPerk deserialize(@NonNull TypeToken<?> type, @NonNull ConfigurationNode value) throws ObjectMappingException {
        String perkName = value.getNode("name").getValue(TypeToken.of(String.class));
        CustomXPPerk customXPPerk = new CustomXPPerk(perkName);

        //See if any children nodes match skills by name
        for(ConfigurationNode configurationNode : value.getChildrenList())
        {
            try {
                PrimarySkillType primarySkillType = matchIgnoreCase(configurationNode.getValue(TypeToken.of(String.class)));
                float boostValue = configurationNode.getNode("XP-Multiplier").getValue(TypeToken.of(Float.class));
                customXPPerk.setCustomXPValue(primarySkillType, boostValue);
            } catch (InvalidSkillException e) {
                mcMMO.p.getLogger().info("Custom XP perk has a skill defined that was not found, did you misspell it?");
                e.printStackTrace();
            } catch (ObjectMappingException e) {
                e.printStackTrace();
            }
        }

        return customXPPerk;
    }

    @Override
    public void serialize(@NonNull TypeToken<?> type, @Nullable CustomXPPerk obj, @NonNull ConfigurationNode value) throws ObjectMappingException {

    }

    private PrimarySkillType matchIgnoreCase(String string) throws InvalidSkillException
    {
        for(PrimarySkillType primarySkillType : PrimarySkillType.values())
        {
            if(string.equalsIgnoreCase(primarySkillType.toString()))
                return primarySkillType;
        }

        throw new InvalidSkillException();
    }

    /*
        CustomXPPerk customXPPerk = new CustomXPPerk("examplecustomxpperk");
        customXPPerk.setCustomXPValue(PrimarySkillType.MINING, 13.37f);
        customXPPerk.setCustomXPValue(PrimarySkillType.WOODCUTTING, 4.0f);
     */
}
