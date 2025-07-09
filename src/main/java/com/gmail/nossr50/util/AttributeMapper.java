package com.gmail.nossr50.util;

import com.gmail.nossr50.mcMMO;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Optional;
import java.util.stream.Stream;
import org.bukkit.attribute.Attribute;

public class AttributeMapper {

    public static final String ATTRIBUTE = "ATTRIBUTE";
    public static final String ORG_BUKKIT_REGISTRY = "org.bukkit.Registry";

    // Prevent instantiation
    private AttributeMapper() {
    }

    // Define constants for attribute keys and their legacy counterparts
    private static final String MAX_HEALTH_1_21_3_STR = "max_health";
    private static final String MAX_HEALTH_1_18_2_STR = "generic.max_health";
    public static final Attribute MAPPED_MAX_HEALTH;

    private static final String JUMP_STRENGTH_1_23_1 = "jump_strength";
    private static final String JUMP_STRENGTH_1_21_1 = "generic.jump_strength";
    private static final String JUMP_STR_1_18_2 = "horse.jump_strength";
    public static final Attribute MAPPED_JUMP_STRENGTH;

    public static final Attribute MAPPED_MOVEMENT_SPEED;
    private static final String MOVEMENT_SPEED_1_18_2 = "generic.movement_speed";
    private static final String MOVEMENT_SPEED_1_21_1 = "generic.movement_speed";
    private static final String MOVEMENT_SPEED_1_21_3 = "movement_speed";

    // Add other attributes similarly...
    // For brevity, only key attributes are shown

    static {
        MAPPED_MAX_HEALTH = findAttribute(MAX_HEALTH_1_21_3_STR, MAX_HEALTH_1_18_2_STR);
        MAPPED_JUMP_STRENGTH = findAttribute(JUMP_STRENGTH_1_23_1, JUMP_STRENGTH_1_21_1,
                JUMP_STR_1_18_2);
        MAPPED_MOVEMENT_SPEED = findAttribute(MOVEMENT_SPEED_1_18_2, MOVEMENT_SPEED_1_21_1,
                MOVEMENT_SPEED_1_21_3);
    }

    private static Attribute findAttribute(String... keys) {
        Stream<?> attributeStream;
        try {
            // Try to get Registry.ATTRIBUTE using reflection
            Class<?> registryClass = Class.forName(ORG_BUKKIT_REGISTRY);
            Field attributeField = registryClass.getField(ATTRIBUTE);
            Object attributeRegistry = attributeField.get(null);

            // Get the stream() method of the attribute registry
            Method streamMethod = attributeRegistry.getClass().getMethod("stream");
            attributeStream = (Stream<?>) streamMethod.invoke(attributeRegistry);
        } catch (ClassNotFoundException | NoSuchFieldException | IllegalAccessException |
                 NoSuchMethodException |
                 InvocationTargetException e) {
            // Fallback to older versions where Attribute is an enum
            Object[] enumConstants = Attribute.class.getEnumConstants();
            attributeStream = Arrays.stream(enumConstants);
        }

        Optional<?> optionalAttribute = attributeStream
                .filter(attr -> {
                    try {
                        String attrKey = null;
                        String attrName = null;

                        // Try to get attr.getKey().getKey()
                        Method getKeyMethod = attr.getClass().getMethod("getKey");
                        Object namespacedKey = getKeyMethod.invoke(attr);

                        if (namespacedKey != null) {
                            Method getKeyStringMethod = namespacedKey.getClass()
                                    .getMethod("getKey");
                            attrKey = (String) getKeyStringMethod.invoke(namespacedKey);
                        }

                        // Try to get attr.name()
                        Method nameMethod;
                        try {
                            nameMethod = attr.getClass().getMethod("name");
                            attrName = (String) nameMethod.invoke(attr);
                        } catch (NoSuchMethodException e) {
                            // name() method doesn't exist in newer versions
                            attrName = null;
                        }

                        // Compare with provided keys
                        for (String key : keys) {
                            if ((attrKey != null && attrKey.equalsIgnoreCase(key)) ||
                                    (attrName != null && attrName.equalsIgnoreCase(key))) {
                                return true;
                            }
                        }
                    } catch (Exception e) {
                        mcMMO.p.getLogger()
                                .severe("Unable to find the attribute with possible keys: "
                                        + Arrays.toString(keys)
                                        + ", mcMMO will not function properly.");
                        throw new RuntimeException(e);
                    }
                    return false;
                })
                .findFirst();

        if (optionalAttribute.isPresent()) {
            return (Attribute) optionalAttribute.get();
        } else {
            mcMMO.p.getLogger().severe("Unable to find the attribute with possible keys: "
                    + Arrays.toString(keys) + ", mcMMO will not function properly.");
            throw new IllegalStateException("Unable to find the attribute with possible keys: "
                    + Arrays.toString(keys));
        }
    }

    /*
    For easy reference...
    List of 1.18 Attributes by name...
        GENERIC_MAX_HEALTH("generic.max_health"),
        GENERIC_FOLLOW_RANGE("generic.follow_range"),
        GENERIC_KNOCKBACK_RESISTANCE("generic.knockback_resistance"),
        GENERIC_MOVEMENT_SPEED("generic.movement_speed"),
        GENERIC_FLYING_SPEED("generic.flying_speed"),
        GENERIC_ATTACK_DAMAGE("generic.attack_damage"),
        GENERIC_ATTACK_KNOCKBACK("generic.attack_knockback"),
        GENERIC_ATTACK_SPEED("generic.attack_speed"),
        GENERIC_ARMOR("generic.armor"),
        GENERIC_ARMOR_TOUGHNESS("generic.armor_toughness"),
        GENERIC_LUCK("generic.luck"),
        HORSE_JUMP_STRENGTH("horse.jump_strength"),
        ZOMBIE_SPAWN_REINFORCEMENTS("zombie.spawn_reinforcements");
    List of 1.21.1 Attributes by name...
        GENERIC_MAX_HEALTH("generic.max_health"),
        GENERIC_FOLLOW_RANGE("generic.follow_range"),
        GENERIC_KNOCKBACK_RESISTANCE("generic.knockback_resistance"),
        GENERIC_MOVEMENT_SPEED("generic.movement_speed"),
        GENERIC_FLYING_SPEED("generic.flying_speed"),
        GENERIC_ATTACK_DAMAGE("generic.attack_damage"),
        GENERIC_ATTACK_KNOCKBACK("generic.attack_knockback"),
        GENERIC_ATTACK_SPEED("generic.attack_speed"),
        GENERIC_ARMOR("generic.armor"),
        GENERIC_ARMOR_TOUGHNESS("generic.armor_toughness"),
        GENERIC_FALL_DAMAGE_MULTIPLIER("generic.fall_damage_multiplier"),
        GENERIC_LUCK("generic.luck"),
        GENERIC_MAX_ABSORPTION("generic.max_absorption"),
        GENERIC_SAFE_FALL_DISTANCE("generic.safe_fall_distance"),
        GENERIC_SCALE("generic.scale"),
        GENERIC_STEP_HEIGHT("generic.step_height"),
        GENERIC_GRAVITY("generic.gravity"),
        GENERIC_JUMP_STRENGTH("generic.jump_strength"),
        GENERIC_EXPLOSION_KNOCKBACK_RESISTANCE("generic.explosion_knockback_resistance"),
        GENERIC_MOVEMENT_EFFICIENCY("generic.movement_efficiency"),
        GENERIC_OXYGEN_BONUS("generic.oxygen_bonus"),
        GENERIC_WATER_MOVEMENT_EFFICIENCY("generic.water_movement_efficiency"),
        PLAYER_BLOCK_INTERACTION_RANGE("player.block_interaction_range"),
        PLAYER_ENTITY_INTERACTION_RANGE("player.entity_interaction_range"),
        PLAYER_BLOCK_BREAK_SPEED("player.block_break_speed"),
        PLAYER_MINING_EFFICIENCY("player.mining_efficiency"),
        PLAYER_SNEAKING_SPEED("player.sneaking_speed"),
        PLAYER_SUBMERGED_MINING_SPEED("player.submerged_mining_speed"),
        PLAYER_SWEEPING_DAMAGE_RATIO("player.sweeping_damage_ratio"),
        ZOMBIE_SPAWN_REINFORCEMENTS("zombie.spawn_reinforcements");
    List of 1.21.3 Attributes...
        Attribute MAX_HEALTH = getAttribute("max_health");
        Attribute FOLLOW_RANGE = getAttribute("follow_range");
        Attribute KNOCKBACK_RESISTANCE = getAttribute("knockback_resistance");
        Attribute MOVEMENT_SPEED = getAttribute("movement_speed");
        Attribute FLYING_SPEED = getAttribute("flying_speed");
        Attribute ATTACK_DAMAGE = getAttribute("attack_damage");
        Attribute ATTACK_KNOCKBACK = getAttribute("attack_knockback");
        Attribute ATTACK_SPEED = getAttribute("attack_speed");
        Attribute ARMOR = getAttribute("armor");
        Attribute ARMOR_TOUGHNESS = getAttribute("armor_toughness");
        Attribute FALL_DAMAGE_MULTIPLIER = getAttribute("fall_damage_multiplier");
        Attribute LUCK = getAttribute("luck");
        Attribute MAX_ABSORPTION = getAttribute("max_absorption");
        Attribute SAFE_FALL_DISTANCE = getAttribute("safe_fall_distance");
        Attribute SCALE = getAttribute("scale");
        Attribute STEP_HEIGHT = getAttribute("step_height");
        Attribute GRAVITY = getAttribute("gravity");
        Attribute JUMP_STRENGTH = getAttribute("jump_strength");
        Attribute BURNING_TIME = getAttribute("burning_time");
        Attribute EXPLOSION_KNOCKBACK_RESISTANCE = getAttribute("explosion_knockback_resistance");
        Attribute MOVEMENT_EFFICIENCY = getAttribute("movement_efficiency");
        Attribute OXYGEN_BONUS = getAttribute("oxygen_bonus");
        Attribute WATER_MOVEMENT_EFFICIENCY = getAttribute("water_movement_efficiency");
        Attribute TEMPT_RANGE = getAttribute("tempt_range");
        Attribute BLOCK_INTERACTION_RANGE = getAttribute("block_interaction_range");
        Attribute ENTITY_INTERACTION_RANGE = getAttribute("entity_interaction_range");
        Attribute BLOCK_BREAK_SPEED = getAttribute("block_break_speed");
        Attribute MINING_EFFICIENCY = getAttribute("mining_efficiency");
        Attribute SNEAKING_SPEED = getAttribute("sneaking_speed");
        Attribute SUBMERGED_MINING_SPEED = getAttribute("submerged_mining_speed");
        Attribute SWEEPING_DAMAGE_RATIO = getAttribute("sweeping_damage_ratio");
        Attribute SPAWN_REINFORCEMENTS = getAttribute("spawn_reinforcements");
    */
}
