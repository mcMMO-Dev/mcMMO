package com.gmail.nossr50.util;

import com.gmail.nossr50.mcMMO;
import org.bukkit.Registry;
import org.bukkit.attribute.Attribute;

public class AttributeMapper {
    private final mcMMO pluginRef;
    private static final String GENERIC_JUMP_STRENGTH = "generic.jump_strength";
    private static final String HORSE_JUMP_STRENGTH = "horse.jump_strength";
    private final Attribute horseJumpStrength;

    public AttributeMapper(mcMMO pluginRef) {
        this.pluginRef = pluginRef;
        this.horseJumpStrength = initHorseJumpStrength();
    }

    private Attribute initHorseJumpStrength() {
        // TODO: Use modern matching?
//        if (Registry.ATTRIBUTE.match(GENERIC_JUMP_STRENGTH) != null) {
//            return Registry.ATTRIBUTE.match(GENERIC_JUMP_STRENGTH);
//        }
//
//        if (Registry.ATTRIBUTE.match(HORSE_JUMP_STRENGTH) != null) {
//            return Registry.ATTRIBUTE.match(HORSE_JUMP_STRENGTH);
//        }

        for (Attribute attr : Registry.ATTRIBUTE) {
            if (attr.getKey().getKey().equalsIgnoreCase(HORSE_JUMP_STRENGTH)
                    || attr.getKey().getKey().equalsIgnoreCase(GENERIC_JUMP_STRENGTH)
                    || attr.name().equalsIgnoreCase(HORSE_JUMP_STRENGTH)
                    || attr.name().equalsIgnoreCase(GENERIC_JUMP_STRENGTH)) {
                return attr;
            }
        }

        pluginRef.getLogger().severe("Unable to find the Generic Jump Strength or Horse Jump Strength attribute, " +
                "mcMMO will not function properly.");
        throw new IllegalStateException("Unable to find the Generic Jump Strength or Horse Jump Strength attribute");
    }

    public Attribute getHorseJumpStrength() {
        return horseJumpStrength;
    }
}
