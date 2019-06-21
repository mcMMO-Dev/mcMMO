package com.gmail.nossr50.config.hocon.serializers;

import com.gmail.nossr50.datatypes.items.CustomItemTarget;
import com.gmail.nossr50.datatypes.permissions.PermissionWrapper;
import com.gmail.nossr50.skills.repair.RepairTransaction;
import com.gmail.nossr50.skills.repair.repairables.Repairable;
import com.gmail.nossr50.skills.repair.repairables.RepairableBuilder;
import com.google.common.reflect.TypeToken;
import ninja.leaping.configurate.ConfigurationNode;
import ninja.leaping.configurate.ValueType;
import ninja.leaping.configurate.commented.CommentedConfigurationNode;
import ninja.leaping.configurate.commented.SimpleCommentedConfigurationNode;
import ninja.leaping.configurate.objectmapping.ObjectMappingException;
import ninja.leaping.configurate.objectmapping.serialize.TypeSerializer;
import org.bukkit.inventory.ItemStack;

public class RepairableSerializer implements TypeSerializer<Repairable> {
    public static final String REPAIRABLE_ITEM = "Repairable-Item";
    public static final String MAXIMUM_DURABILITY = "Maximum-Durability";
    public static final String ITEMS_REQUIRED_TO_REPAIR = "Items-Required-To-Repair";
    public static final String SKILL_LEVEL_REQUIRED_TO_REPAIR = "Skill-Level-Required-To-Repair";
    public static final String BASE_XP_REWARD = "Base-XP-Reward";
    public static final String BASE_REPAIR_COUNT = "Base-Repair-Count";
    public static final String REQUIRED_PERMISSION_NODE = "Required-Permission-Node";

    @Override
    public Repairable deserialize(TypeToken<?> type, ConfigurationNode value) throws ObjectMappingException {
        /* Necessary fields */
        CustomItemTarget customItemTarget = value.getNode(REPAIRABLE_ITEM).getValue(TypeToken.of(CustomItemTarget.class));
        Short maximumDurability = value.getNode(MAXIMUM_DURABILITY).getValue(TypeToken.of(Short.class));
        RepairTransaction repairTransaction = value.getNode(ITEMS_REQUIRED_TO_REPAIR).getValue(TypeToken.of(RepairTransaction.class));

        RepairableBuilder repairableBuilder = new RepairableBuilder(customItemTarget, maximumDurability, repairTransaction);

        if(value.getNode(SKILL_LEVEL_REQUIRED_TO_REPAIR).getValueType() != ValueType.NULL) {
            repairableBuilder.addMinLevel(value.getNode(SKILL_LEVEL_REQUIRED_TO_REPAIR).getInt());
        }

        if(value.getNode(BASE_XP_REWARD).getValueType() != ValueType.NULL) {
            repairableBuilder.setBaseXP(value.getNode(BASE_XP_REWARD).getInt());
        }

        if(value.getNode(BASE_REPAIR_COUNT).getValueType() != ValueType.NULL) {
            repairableBuilder.setRepairCount(value.getNode(BASE_REPAIR_COUNT).getInt());
        }

        if(value.getNode(REQUIRED_PERMISSION_NODE).getValueType() != ValueType.NULL) {
            repairableBuilder.addPermissionWrapper(value.getNode(REQUIRED_PERMISSION_NODE).getValue(TypeToken.of(PermissionWrapper.class)));
        }

        return repairableBuilder.build();
    }

    @Override
    public void serialize(TypeToken<?> type, Repairable obj, ConfigurationNode value) {
        value.getNode(REPAIRABLE_ITEM).setValue(obj.getCustomItemTarget());
        value.getNode(MAXIMUM_DURABILITY).setValue(obj.getMaximumDurability());
        value.getNode(ITEMS_REQUIRED_TO_REPAIR).setValue(obj.getRepairTransaction());

        if(obj.getMinimumLevel() > 0) {
            value.getNode(SKILL_LEVEL_REQUIRED_TO_REPAIR).setValue(obj.getMinimumLevel());
        }

        if(obj.getBaseXP() != 0) {
            value.getNode(BASE_XP_REWARD).setValue(obj.getBaseXP());
            SerializerUtil.addCommentIfCompatible(value.getNode(BASE_XP_REWARD), "The minimum amount of XP to reward a player when they repair this item.");
        }

        if(obj.getRepairCount() != 0) {
            value.getNode(BASE_REPAIR_COUNT).setValue(obj.getRepairCount());
            SerializerUtil.addCommentIfCompatible(value.getNode(BASE_REPAIR_COUNT), "How many times it should take a player to repair this item from fully damaged to brand new without any skill in Repair." +
                    "\nThis value is used in calculating how much damage to remove from an item, typically you want this to be at least equal to the number of mats used to craft the item.");
        }

        if(obj.getPermissionWrapper() != null) {
            value.getNode(REQUIRED_PERMISSION_NODE).setValue(obj.getPermissionWrapper());
            SerializerUtil.addCommentIfCompatible(value.getNode(REQUIRED_PERMISSION_NODE), "A custom permission node required to repair this item." +
                    "\nThis setting is optional.");
        }
    }

}
