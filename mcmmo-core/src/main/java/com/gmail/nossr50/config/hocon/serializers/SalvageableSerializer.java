package com.gmail.nossr50.config.hocon.serializers;

import com.gmail.nossr50.skills.salvage.salvageables.Salvageable;
import com.google.common.reflect.TypeToken;
import ninja.leaping.configurate.ConfigurationNode;
import ninja.leaping.configurate.objectmapping.ObjectMappingException;
import ninja.leaping.configurate.objectmapping.serialize.TypeSerializer;
import ninja.leaping.configurate.util.EnumLookup;

import java.util.Optional;

public class SalvageableSerializer implements TypeSerializer<Salvageable> {
    public static final String ITEM_NODE_NAME = "Item";
    public static final String ITEM_RETURNED_BY_SALVAGE = "Item-Returned-By-Salvage";
    public static final String MAXIMUM_QUANTITY_RETURNED = "Maximum-Quantity-Returned";
    public static final String OVERRIDE_LEVEL_REQUIREMENT = "Override-Level-Requirement";

    /*
         TypeTokens are obtained in two ways

            For Raw basic classes:

                TypeToken<String> stringTok = TypeToken.of(String.class);
                TypeToken<Integer> intTok = TypeToken.of(Integer.class);

            For Generics:

                TypeToken<List<String>> stringListTok = new TypeToken<List<String>>() {};

            Wildcard example:

                TypeToken<Map<?, ?>> wildMapTok = new TypeToken<Map<?, ?>>() {};

         */


    @Override
    public Salvageable deserialize(TypeToken<?> type, ConfigurationNode value) throws ObjectMappingException {

        /*
        CONFIG_REPAIRABLES_DEFAULTS.add(new Repairable(WOODEN_SWORD, OAK_PLANKS, 1, 0, .25D));
         */

        /* Repairable(Material itemMaterial, Material repairMaterial, int minimumQuantity, int minimumLevel, double xpMultiplier) */

        String item = value.getNode(ITEM_NODE_NAME).getValue(TypeToken.of(String.class));
//        String itemReturnedBySalvage = value.getNode(ITEM_RETURNED_BY_SALVAGE).getValue(new TypeToken<String>() {});
        String itemReturnedBySalvage = value.getNode(ITEM_RETURNED_BY_SALVAGE).getValue(TypeToken.of(String.class));
        int maximumQuantityReturned = value.getNode(MAXIMUM_QUANTITY_RETURNED).getValue(TypeToken.of(Integer.class));
        int minimumLevel = value.getNode(OVERRIDE_LEVEL_REQUIREMENT).getValue(TypeToken.of(Integer.class));

        return new Salvageable(item, itemReturnedBySalvage, minimumLevel, maximumQuantityReturned);
    }

    @Override
    public void serialize(TypeToken<?> type, Salvageable obj, ConfigurationNode value) {

        value.getNode(ITEM_NODE_NAME).setValue(obj.getItemMaterial().getKey().toString());
        value.getNode(ITEM_RETURNED_BY_SALVAGE).setValue(obj.getSalvagedItemMaterial().getKey().toString());
        value.getNode(MAXIMUM_QUANTITY_RETURNED).setValue(obj.getMaximumQuantity());
        value.getNode(OVERRIDE_LEVEL_REQUIREMENT).setValue(obj.getMinimumLevel());
    }

    private Enum getEnum(String enumConstant, TypeToken<?> type) throws ObjectMappingException {
        //noinspection RedundantCast
        Optional<Enum> ret = (Optional) EnumLookup.lookupEnum(type.getRawType().asSubclass(Enum.class),
                enumConstant); // XXX: intellij says this cast is optional but it isnt

        if (!ret.isPresent()) {
            throw new ObjectMappingException("Invalid enum constant provided for " + enumConstant + ": " +
                    "Expected a value of enum " + type + ", got " + enumConstant);
        }

        return ret.get();
    }

}
