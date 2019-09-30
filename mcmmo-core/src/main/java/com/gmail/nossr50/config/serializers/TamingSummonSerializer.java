package com.gmail.nossr50.config.serializers;

import com.gmail.nossr50.datatypes.skills.subskills.taming.CallOfTheWildType;
import com.gmail.nossr50.datatypes.skills.subskills.taming.TamingSummon;
import com.google.common.collect.ImmutableList;
import com.google.common.reflect.TypeToken;
import ninja.leaping.configurate.ConfigurationNode;
import ninja.leaping.configurate.objectmapping.ObjectMappingException;
import ninja.leaping.configurate.objectmapping.serialize.TypeSerializer;
import org.bukkit.Material;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;

public class TamingSummonSerializer implements TypeSerializer<TamingSummon> {

    private static final String ITEM_MATERIAL = "Item-Id";
    private static final String AMOUNT_REQUIRED = "Item-Amount-Required";
    private static final String ENTITIES_SUMMONED = "Entities-Summoned";
    private static final String SUMMON_LIFESPAN_SECONDS = "Summon-Lifespan-Seconds";
    private static final String SUMMON_LIMIT = "Summon-Limit";
    private static final String CALL_OF_THE_WILD_TYPE = "Call-Of-The-Wild-Type";

    @Nullable
    @Override
    public TamingSummon deserialize(@NonNull TypeToken<?> type, @NonNull ConfigurationNode value) throws ObjectMappingException {
        String itemMaterialStr = value.getNode(ITEM_MATERIAL).getValue(TypeToken.of(String.class));
        //TODO: Make platform independent instead of Bukkit dependent
        Material itemMaterial = Material.matchMaterial(itemMaterialStr);
        int amountRequired = value.getNode(AMOUNT_REQUIRED).getValue(TypeToken.of(Integer.class));
        int entitiesSummoned = value.getNode(ENTITIES_SUMMONED).getValue(TypeToken.of(Integer.class));
        int summonLifespanSeconds = value.getNode(SUMMON_LIFESPAN_SECONDS).getValue(TypeToken.of(Integer.class));
        int summonLimit = value.getNode(SUMMON_LIMIT).getValue(TypeToken.of(Integer.class));
        CallOfTheWildType callOfTheWildType = value.getNode(CALL_OF_THE_WILD_TYPE).getValue(new TypeToken<CallOfTheWildType>() {});

        TamingSummon tamingSummon = new TamingSummon(callOfTheWildType, itemMaterial, amountRequired, entitiesSummoned, summonLifespanSeconds, summonLimit);
        return tamingSummon;
    }

    @Override
    public void serialize(@NonNull TypeToken<?> type, @Nullable TamingSummon obj, @NonNull ConfigurationNode value) throws ObjectMappingException {
        value.getNode(ITEM_MATERIAL).setValue(obj.getItemType().getKey().toString());
        value.getNode(AMOUNT_REQUIRED).setValue(obj.getItemAmountRequired());
        value.getNode(ENTITIES_SUMMONED).setValue(obj.getEntitiesSummoned());
        value.getNode(SUMMON_LIFESPAN_SECONDS).setValue(obj.getSummonLifespan());
        value.getNode(SUMMON_LIMIT).setValue(obj.getSummonCap());

        /*
         In order to append our ENUM directly we need to do this as Configurate seems to have no idea what serializer to use on its own accord
         */

        ConfigurationNode cotwNode = value.getNode(CALL_OF_THE_WILD_TYPE);
        TypeToken<?> entryType = type.resolveType(CallOfTheWildType.class);
        TypeSerializer entrySerial = cotwNode.getOptions().getSerializers().get(entryType);

        if (entrySerial == null) {
            throw new ObjectMappingException("No applicable type serializer for type " + entryType);
        }

        cotwNode.setValue(ImmutableList.of());
        entrySerial.serialize(entryType, obj.getCallOfTheWildType(), cotwNode.getAppendedNode());
    }
}
