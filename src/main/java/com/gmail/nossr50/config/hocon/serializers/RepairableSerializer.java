package com.gmail.nossr50.config.hocon.serializers;

import com.gmail.nossr50.skills.repair.RepairTransaction;
import com.gmail.nossr50.skills.repair.repairables.Repairable;
import com.gmail.nossr50.util.nbt.RawNBT;
import com.google.common.reflect.TypeToken;
import ninja.leaping.configurate.ConfigurationNode;
import ninja.leaping.configurate.objectmapping.ObjectMappingException;
import ninja.leaping.configurate.objectmapping.serialize.TypeSerializer;
import ninja.leaping.configurate.util.EnumLookup;
import org.bukkit.inventory.ItemStack;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

public class RepairableSerializer implements TypeSerializer<Repairable> {
    private static final String ITEM = "Item";
    private static final String ITEMS_USED_TO_REPAIR = "Repair-Transaction-Cost";
    private static final String OVERRIDE_LEVEL_REQUIREMENT = "Level-Requirement";
    private static final String BASE_XP = "XP-Per-Repair";
    private static final String FULL_REPAIR_TRANSACTIONS = "Repair-Count";
    private static final String STRICT_MATCHING = "Use-Strict-Matching";
    private static final String RAW_NBT = "Raw-NBT";

    @Override
    public Repairable deserialize(TypeToken<?> type, ConfigurationNode value) throws ObjectMappingException {
        String itemMaterial = value.getNode(ITEM).getValue(TypeToken.of(String.class));
        RepairTransaction repairTransaction = value.getNode(ITEMS_USED_TO_REPAIR).getValue(TypeToken.of(RepairTransaction.class));
        Integer minimumLevel = value.getNode(OVERRIDE_LEVEL_REQUIREMENT).getValue(TypeToken.of(Integer.class));
        Integer baseXP = value.getNode(BASE_XP).getValue(TypeToken.of(Integer.class));
        Integer minRepairs = value.getNode(FULL_REPAIR_TRANSACTIONS).getValue(TypeToken.of(Integer.class));
        Boolean strictMatching = value.getNode(STRICT_MATCHING).getValue(TypeToken.of(Boolean.class));
        String rawNBT = value.getNode(RAW_NBT).getValue(TypeToken.of(String.class));

        return new Repairable(itemMaterial, repairTransaction, minimumLevel, minRepairs, baseXP, strictMatching, new RawNBT(rawNBT));
    }

    @Override
    public void serialize(TypeToken<?> type, Repairable obj, ConfigurationNode value) {
        value.getNode(ITEM).setValue(obj.getItemMaterial().getKey().toString());
        value.getNode(ITEMS_USED_TO_REPAIR).setValue(obj.getRepairTransaction());
        value.getNode(BASE_XP).setValue(obj.getBaseXP());
        value.getNode(FULL_REPAIR_TRANSACTIONS).setValue(obj.getRepairCount());
        value.getNode(STRICT_MATCHING).setValue(obj.useStrictMatching());
        value.getNode(RAW_NBT).setValue(obj.getRawNBT().getNbtContents());
    }

}
