package com.gmail.nossr50.config.hocon;

import com.gmail.nossr50.skills.repair.repairables.Repairable;
import com.google.common.reflect.TypeToken;
import ninja.leaping.configurate.ConfigurationNode;
import ninja.leaping.configurate.objectmapping.ObjectMappingException;
import ninja.leaping.configurate.objectmapping.serialize.TypeSerializer;
import ninja.leaping.configurate.util.EnumLookup;

import java.util.List;
import java.util.Optional;

public class RepairableSerializer implements TypeSerializer<Repairable> {

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
    public Repairable deserialize(TypeToken<?> type, ConfigurationNode value) throws ObjectMappingException {

        /*
        CONFIG_REPAIRABLES_DEFAULTS.add(new Repairable(WOODEN_SWORD, OAK_PLANKS, 1, 0, .25D));
         */

        /* Repairable(Material itemMaterial, Material repairMaterial, int minimumQuantity, int minimumLevel, double xpMultiplier) */

        String item = value.getNode("Item").getValue(TypeToken.of(String.class));
        List<String> repairItems = value.getNode("Items-Used-To-Repair").getValue(new TypeToken<List<String>>() {});


        /*String itemConstant = HOCONUtil.deserializeENUMName(value.getNode("Item").getString());
        String repairConstant = HOCONUtil.deserializeENUMName(value.getNode("Item-Used-To-Repair").getString());

        Material item = (Material) getEnum(itemConstant, TypeToken.of(Material.class));
        Material repairItem = (Material) getEnum(repairConstant, TypeToken.of(Material.class));*/

        int minimumQuantity = value.getNode("Minimum-Quantity-Used-To-Repair").getValue(TypeToken.of(Integer.class));
        int minimumLevel = value.getNode("Override-Level-Requirement").getValue(TypeToken.of(Integer.class));
        double xpMultiplier = value.getNode("XP-Multiplier").getValue(TypeToken.of(Double.class));

        return new Repairable(item, repairItems, minimumQuantity, minimumLevel, xpMultiplier);
    }

    @Override
    public void serialize(TypeToken<?> type, Repairable obj, ConfigurationNode value) {

        /*value.getNode("Item").setValue(HOCONUtil.serializeENUMName(obj.getItemMaterial().getKey().getKey()));
        value.getNode("Item-Used-To-Repair").setValue(HOCONUtil.serializeENUMName(obj.getRepairMaterials().getKey().getKey()));*/
        value.getNode("Item").setValue(obj.getItemMaterial().getKey().toString());
        value.getNode("Items-Used-To-Repair").setValue(obj.getRepairMaterialsRegistryKeys());
        value.getNode("Minimum-Quantity-Used-To-Repair").setValue(obj.getMinimumQuantity());
        value.getNode("Skill-Level-Required-To-Repair").setValue(obj.getMinimumLevel());
        value.getNode("XP-Multiplier").setValue(obj.getXpMultiplier());
    }

    private Enum getEnum(String enumConstant, TypeToken<?> type) throws ObjectMappingException
    {
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
