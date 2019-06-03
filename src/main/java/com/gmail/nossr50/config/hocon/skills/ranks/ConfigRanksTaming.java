package com.gmail.nossr50.config.hocon.skills.ranks;

import ninja.leaping.configurate.objectmapping.Setting;
import ninja.leaping.configurate.objectmapping.serialize.ConfigSerializable;

@ConfigSerializable
public class ConfigRanksTaming {

    @Setting(value = "Beast-Lore")
    private SkillRankProperty beastLore = new SkillRankProperty(2);

    @Setting(value = "Gore")
    private SkillRankProperty gore = new SkillRankProperty(15);

    @Setting(value = "Call-Of-The-Wild")
    private SkillRankProperty callOfTheWild = new SkillRankProperty(5);

    @Setting(value = "Pummel")
    private SkillRankProperty pummel = new SkillRankProperty(20);

    @Setting(value = "FastFoodService")
    private SkillRankProperty fastFoodService = new SkillRankProperty(20);

    @Setting(value = "Environmentally-Aware")
    private SkillRankProperty environmentallyAware = new SkillRankProperty(10);

    @Setting(value = "Thick-Fur")
    private SkillRankProperty thickFur = new SkillRankProperty(25);

    @Setting(value = "Holy-Hound")
    private SkillRankProperty holyHound = new SkillRankProperty(35);

    @Setting(value = "Shock-Proof")
    private SkillRankProperty shockProof = new SkillRankProperty(50);

    @Setting(value = "Sharpened-Claws")
    private SkillRankProperty sharpenedClaws = new SkillRankProperty(75);

    public SkillRankProperty getBeastLore() {
        return beastLore;
    }

    public SkillRankProperty getGore() {
        return gore;
    }

    public SkillRankProperty getCallOfTheWild() {
        return callOfTheWild;
    }

    public SkillRankProperty getPummel() {
        return pummel;
    }

    public SkillRankProperty getFastFoodService() {
        return fastFoodService;
    }

    public SkillRankProperty getEnvironmentallyAware() {
        return environmentallyAware;
    }

    public SkillRankProperty getThickFur() {
        return thickFur;
    }

    public SkillRankProperty getHolyHound() {
        return holyHound;
    }

    public SkillRankProperty getShockProof() {
        return shockProof;
    }

    public SkillRankProperty getSharpenedClaws() {
        return sharpenedClaws;
    }
}
