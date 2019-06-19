package com.gmail.nossr50.config.hocon.serializers;

import com.gmail.nossr50.datatypes.permissions.PermissionWrapper;
import com.gmail.nossr50.skills.repair.RepairTransaction;
import com.gmail.nossr50.skills.repair.repairables.Repairable;
import com.gmail.nossr50.skills.repair.repairables.RepairableBuilder;
import com.gmail.nossr50.util.nbt.RawNBT;
import com.google.common.reflect.TypeToken;
import ninja.leaping.configurate.ConfigurationNode;
import ninja.leaping.configurate.ValueType;
import ninja.leaping.configurate.objectmapping.ObjectMappingException;
import ninja.leaping.configurate.objectmapping.serialize.TypeSerializer;
import ninja.leaping.configurate.util.EnumLookup;
import org.bukkit.inventory.ItemStack;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

public class RepairableSerializer implements TypeSerializer<Repairable> {
    private static final String ITEM = "Item";
    private static final String BASE_XP = "XP-Per-Repair";
    private static final String REPAIR_TRANSACTION = "Repair-Transaction";
    private static final String STRICT_MATCH_ITEM = "Strict-Match-Item";
    private static final String STRICT_MATCHING_REPAIR_TRANSACTION = "Strict-Matching-Repair-Transaction";
    private static final String REPAIR_COUNT = "Repair-Count";
    private static final String NBT = "NBT";
    private static final String PERMISSION = "Permission";
    private static final String MINIMUM_LEVEL = "Minimum-Level";

    @Override
    public Repairable deserialize(TypeToken<?> type, ConfigurationNode value) throws ObjectMappingException {
        ItemStack itemStack = value.getNode(ITEM).getValue(TypeToken.of(ItemStack.class));
        RepairableBuilder builder = new RepairableBuilder(itemStack)
                .repairTransaction(value.getNode(REPAIR_TRANSACTION).getValue(TypeToken.of(RepairTransaction.class)))
                .strictMatchingItem(value.getNode(STRICT_MATCH_ITEM).getValue(TypeToken.of(Boolean.class)))
                .strictMatchingRepairTransaction(value.getNode(STRICT_MATCHING_REPAIR_TRANSACTION).getValue(TypeToken.of(Boolean.class)))
                .baseXP(value.getNode(BASE_XP).getValue(TypeToken.of(Integer.class)))
                .repairCount(value.getNode(REPAIR_COUNT).getValue(TypeToken.of(Integer.class)));

        if(value.getNode(MINIMUM_LEVEL).getValueType() != ValueType.NULL) {
            builder = builder.minLevel(value.getNode(MINIMUM_LEVEL).getValue(TypeToken.of(Integer.class)));
        }

//        if(value.getNode(NBT).getValueType() != ValueType.NULL) {
//            builder = builder.rawNBT(value.getNode(NBT).getValue(TypeToken.of(RawNBT.class)));
//        }

        if(value.getNode(PERMISSION).getValueType() != ValueType.NULL) {
            builder = builder.permissionWrapper(value.getNode(PERMISSION).getValue(TypeToken.of(PermissionWrapper.class)));
        }

        return builder.build();
    }

    @Override
    public void serialize(TypeToken<?> type, Repairable obj, ConfigurationNode value) {
        value.getNode(ITEM).setValue(obj.getItem());
        value.getNode(REPAIR_TRANSACTION).setValue(obj.getRepairTransaction());
        value.getNode(STRICT_MATCH_ITEM).setValue(obj.isStrictMatchingItem());
        value.getNode(STRICT_MATCHING_REPAIR_TRANSACTION).setValue(obj.isStrictMatchingRepairTransaction());
        value.getNode(BASE_XP).setValue(obj.getBaseXP());
        value.getNode(REPAIR_COUNT).setValue(obj.getRepairCount());

        if(obj.getMinimumLevel() > 0)
            value.getNode(MINIMUM_LEVEL).setValue(obj.getMinimumLevel());

//        if(obj.hasNBT())
//            value.getNode(NBT).setValue(obj.getRawNBT());

        if(obj.hasPermission())
            value.getNode(PERMISSION).setValue(obj.getPermissionWrapper());
    }

}
