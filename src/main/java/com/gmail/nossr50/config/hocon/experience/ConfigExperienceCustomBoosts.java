package com.gmail.nossr50.config.hocon.experience;

import com.gmail.nossr50.datatypes.experience.CustomXPPerk;
import com.gmail.nossr50.datatypes.skills.PrimarySkillType;
import ninja.leaping.configurate.objectmapping.Setting;
import ninja.leaping.configurate.objectmapping.serialize.ConfigSerializable;

import java.util.ArrayList;

@ConfigSerializable
public class ConfigExperienceCustomBoosts {

    private static final ArrayList<CustomXPPerk> CUSTOM_BOOST_SET_DEFAULT;

    static {
        CUSTOM_BOOST_SET_DEFAULT = new ArrayList<>();
        CustomXPPerk customXPPerk = new CustomXPPerk("examplecustomxpperk");
        customXPPerk.setCustomXPValue(PrimarySkillType.MINING, 13.37f);
        customXPPerk.setCustomXPValue(PrimarySkillType.WOODCUTTING, 4.0f);
        CUSTOM_BOOST_SET_DEFAULT.add(customXPPerk);
    }

    @Setting(value = "Custom-Global-XP-Permissions", comment = "You can give custom global xp perks to players by adding 'mcmmo.customperks.xp.<PERK NAME HERE>' to your players" +
            "\nEnter the name of a permission node and the value of the XP boost that permission node should have." +
            "\nPlayers do not benefit from custom xp perks without being assigned positive permission nodes for said xp perks")
    private ArrayList<CustomXPPerk> customXPBoosts = CUSTOM_BOOST_SET_DEFAULT;

    public ArrayList<CustomXPPerk> getCustomXPBoosts() {
        return customXPBoosts;
    }
}
