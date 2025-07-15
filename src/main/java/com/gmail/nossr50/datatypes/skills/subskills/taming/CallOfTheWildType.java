package com.gmail.nossr50.datatypes.skills.subskills.taming;

import com.gmail.nossr50.util.text.StringUtils;
import org.bukkit.entity.EntityType;

public enum CallOfTheWildType {
    WOLF,
    CAT,
    HORSE;

    //TODO: This is a hacky fix to make the COTW code in 2.1 more bearable, this will be removed upon the rework planned for COTW
    public String getConfigEntityTypeEntry() {

        switch (this) {
            case CAT:
                return StringUtils.getPrettyEntityTypeString(
                        EntityType.OCELOT); //Even though cats will be summoned in 1.14, we specify Ocelot here. This will be gone in 2.2
            case WOLF:
                return StringUtils.getPrettyEntityTypeString(EntityType.WOLF);
            case HORSE:
                return StringUtils.getPrettyEntityTypeString(EntityType.HORSE);
        }

        return null;
    }

}
