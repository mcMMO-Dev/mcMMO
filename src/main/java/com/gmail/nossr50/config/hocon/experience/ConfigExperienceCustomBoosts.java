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

        CustomXPPerk exampleA = new CustomXPPerk("example-beneficial-xpperk");
        exampleA.setCustomXPValue(PrimarySkillType.MINING, 13.37);
        exampleA.setCustomXPValue(PrimarySkillType.EXCAVATION, 4.20);

        CustomXPPerk exampleB = new CustomXPPerk("example-detrimental-xpperk");
        exampleB.setCustomXPValue(PrimarySkillType.WOODCUTTING, 0.01);
        exampleB.setCustomXPValue(PrimarySkillType.UNARMED, 0.02);
        exampleB.setCustomXPValue(PrimarySkillType.SWORDS, 0.03);

        CUSTOM_BOOST_SET_DEFAULT.add(exampleA);
        CUSTOM_BOOST_SET_DEFAULT.add(exampleB);
    }

    @Setting(value = "Custom-Global-XP-Permissions", comment = "You can give custom global xp perks to players by adding 'mcmmo.customperks.xp.<PERK NAME HERE>' to your players" +
            "\nEnter the name of a permission node and the value of the XP boost that permission node should have." +
            "\nPlayers do not benefit from custom xp perks without being assigned positive permission nodes for said xp perks")
    private ArrayList<CustomXPPerk> customXPBoosts = CUSTOM_BOOST_SET_DEFAULT;

    public ArrayList<CustomXPPerk> getCustomXPBoosts() {
        return customXPBoosts;
    }
}
