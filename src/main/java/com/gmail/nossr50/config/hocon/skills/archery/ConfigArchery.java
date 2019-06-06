package com.gmail.nossr50.config.hocon.skills.archery;

import ninja.leaping.configurate.objectmapping.Setting;
import ninja.leaping.configurate.objectmapping.serialize.ConfigSerializable;

@ConfigSerializable
public class ConfigArchery {

    /* ARCHERY */

//
//    public double getDazeBonusDamage() {
//        return getDoubleValue(SKILLS, ARCHERY, DAZE, BONUS_DAMAGE);
//    }
//
//    public double getForceMultiplier() {
//        return getDoubleValue(SKILLS, ARCHERY, FORCE_MULTIPLIER);
//    }

    /*
        Archery:
        SkillShot:
            # RankDamageMultiplier: The current rank of this subskill is multiplied by this value to determine the bonus damage, rank 20 would result in 200% damage increase with a value of 10.0 for RankDamageMultiplier
            # RankDamageMultiplier is a percentage
            RankDamageMultiplier: 10.0
            # MaxDamage: After adding bonus damage, the total damage dealt by the player will not exceed this number
            # You should be careful to not set this too low
            MaxDamage: 9.0
        Daze:
            # ChanceMax: Maximum chance of causing daze to opponents when on <MaxBonusLevel> or higher
            # MaxBonusLevel: Maximum bonus level of Daze, when a player reaches this level his chance of causing a daze will be <ChanceMax>
            # Modifier: Extra damage for arrows that cause a daze (2 damage = 1 heart)
            ChanceMax: 50.0
            MaxBonusLevel:
                Standard: 100
                RetroMode: 1000
            BonusDamage: 4.0
     */


    @Setting(value = "Daze")
    private ConfigArcheryDaze daze = new ConfigArcheryDaze();

    @Setting(value = "Skill-Shot")
    private ConfigArcherySkillShot skillShot = new ConfigArcherySkillShot();

    public ConfigArcheryDaze getDaze() {
        return daze;
    }

    public ConfigArcherySkillShot getSkillShot() {
        return skillShot;
    }
}