package com.gmail.nossr50.config.hocon;

import com.gmail.nossr50.skills.repair.repairables.SimpleRepairable;
import com.google.common.reflect.TypeToken;
import ninja.leaping.configurate.ConfigurationNode;
import ninja.leaping.configurate.objectmapping.ObjectMappingException;
import ninja.leaping.configurate.objectmapping.serialize.TypeSerializer;
import org.bukkit.Material;

public class RepairableSerializer implements TypeSerializer<SimpleRepairable> {

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
    public SimpleRepairable deserialize(TypeToken<?> type, ConfigurationNode value) throws ObjectMappingException {

        /*
        CONFIG_REPAIRABLES_DEFAULTS.add(new SimpleRepairable(WOODEN_SWORD, OAK_PLANKS, 1, 0, .25D));
         */

        /* SimpleRepairable(Material itemMaterial, Material repairMaterial, int minimumQuantity, int minimumLevel, double xpMultiplier) */

        Material item = value.getNode("Item").getValue(TypeToken.of(Material.class));
        Material repairItem = value.getNode("Item-Used-To-Repair").getValue(TypeToken.of(Material.class));
        int minimumQuantity = value.getNode("Minimum-Quantity-Used-To-Repair").getValue(TypeToken.of(Integer.class));
        int minimumLevel = value.getNode("Skill-Level-Required-To-Repair").getValue(TypeToken.of(Integer.class));
        double xpMultiplier = value.getNode("XP-Multiplier").getValue(TypeToken.of(Double.class));

        return new SimpleRepairable(item, repairItem, minimumQuantity, minimumLevel, xpMultiplier);
    }

    @Override
    public void serialize(TypeToken<?> type, SimpleRepairable obj, ConfigurationNode value) throws ObjectMappingException {

        value.getNode("Item").setValue(obj.getItemMaterial());
        value.getNode("Item-Used-To-Repair").setValue(obj.getRepairMaterial());
        value.getNode("Minimum-Quantity-Used-To-Repair").setValue(obj.getMinimumQuantity());
        value.getNode("Skill-Level-Required-To-Repair").setValue(obj.getMinimumLevel());
        value.getNode("XP-Multiplier").setValue(obj.getXpMultiplier());

    }
}
