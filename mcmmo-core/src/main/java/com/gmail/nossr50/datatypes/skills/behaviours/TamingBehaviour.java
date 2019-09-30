package com.gmail.nossr50.datatypes.skills.behaviours;

import com.gmail.nossr50.datatypes.skills.SubSkillType;
import com.gmail.nossr50.datatypes.skills.subskills.taming.CallOfTheWildType;
import com.gmail.nossr50.datatypes.skills.subskills.taming.TamingSummon;
import com.gmail.nossr50.mcMMO;
import org.bukkit.EntityEffect;
import org.bukkit.entity.*;

import java.util.HashMap;

/**
 * These behaviour classes are a band-aid fix for a larger problem
 * Until the new skill system for mcMMO is finished/implemented, there is no good place to store the hardcoded behaviours for each skill
 * These behaviour classes server this purpose, they act as a bad solution to a bad problem
 * These classes will be removed when the new skill system is in place
 */
@Deprecated
public class TamingBehaviour {

    private final mcMMO pluginRef;
    private HashMap<CallOfTheWildType, TamingSummon> cotwSummonDataProperties;

    public TamingBehaviour(mcMMO pluginRef) {
        this.pluginRef = pluginRef;
    }

    public TamingSummon getSummon(CallOfTheWildType callOfTheWildType) {
        return pluginRef.getConfigManager().getConfigTaming().getCallOfTheWild().getCOTWSummon(callOfTheWildType);
    }

    public double getFastFoodServiceActivationChance() {
        return pluginRef.getDynamicSettingsManager().getSkillStaticChance(SubSkillType.TAMING_FAST_FOOD_SERVICE);
    }

    public int getGoreBleedTicks() {
        return pluginRef.getConfigManager().getConfigTaming().getGore().getGoreBleedTicks();
    }

    public double getGoreModifier() {
        return pluginRef.getConfigManager().getConfigTaming().getGore().getGoreMofifier();
    }

    public double getSharpenedClawsBonusDamage(boolean PVE) {
        if(PVE) {
            return pluginRef.getConfigManager().getConfigTaming().getSharpenedClaws().getBonusDamage().getPVEModifier();
        } else {
            return pluginRef.getConfigManager().getConfigTaming().getSharpenedClaws().getBonusDamage().getPVEModifier();
        }
    }

    public double getShockProofModifier() {
        return pluginRef.getConfigManager().getConfigTaming().getShockProof().getShockProofModifier();
    }

    public double getThickFurModifier() {
        return pluginRef.getConfigManager().getConfigTaming().getThickFur().getThickFurDamageModifier();
    }

    public boolean canPreventDamage(Tameable pet, AnimalTamer owner) {
        return pet.isTamed() && owner instanceof Player && pet instanceof Wolf;
    }

    public double processThickFur(Wolf wolf, double damage) {
        wolf.playEffect(EntityEffect.WOLF_SHAKE);
        return damage / getThickFurModifier();
    }

    public void processThickFurFire(Wolf wolf) {
        wolf.playEffect(EntityEffect.WOLF_SMOKE);
        wolf.setFireTicks(0);
    }

    public double processShockProof(Wolf wolf, double damage) {
        wolf.playEffect(EntityEffect.WOLF_SHAKE);
        return damage / getShockProofModifier();
    }

    public void processHolyHound(Wolf wolf, double damage) {
        double modifiedHealth = Math.min(wolf.getHealth() + damage, wolf.getMaxHealth());

        wolf.setHealth(modifiedHealth);
        wolf.playEffect(EntityEffect.WOLF_HEARTS);
    }

    public String getCallOfTheWildFailureMessage(EntityType type) {
        switch (type) {
            case OCELOT:
                return "Taming.Summon.Fail.Ocelot";

            case WOLF:
                return "Taming.Summon.Fail.Wolf";

            case HORSE:
                return "Taming.Summon.Fail.Horse";

            default:
                return "";
        }
    }



}
