package com.gmail.nossr50.datatypes.skills.behaviours;

import com.gmail.nossr50.mcMMO;

/**
 * These behaviour classes are a band-aid fix for a larger problem
 * Until the new skill system for mcMMO is finished/implemented, there is no good place to store the hardcoded behaviours for each skill
 * These behaviour classes server this purpose, they act as a bad solution to a bad problem
 * These classes will be removed when the new skill system is in place
 */
@Deprecated
public class SkillBehaviourManager {
    private final mcMMO pluginRef;

    private final AcrobaticsBehaviour acrobaticsBehaviour;
    private final AlchemyBehaviour alchemyBehaviour;
    private final ArcheryBehaviour archeryBehaviour;
    private final AxesBehaviour axesBehaviour;
    private final ExcavationBehaviour excavationBehaviour;
    private final FishingBehaviour fishingBehaviour;
    private final HerbalismBehaviour herbalismBehaviour;
    private final MiningBehaviour miningBehaviour;
    private final RepairBehaviour repairBehaviour;
    private final SalvageBehaviour salvageBehaviour;
    private final SmeltingBehaviour smeltingBehaviour;
    private final SwordsBehaviour swordsBehaviour;
    private final TamingBehaviour tamingBehaviour;
    private final UnarmedBehaviour unarmedBehaviour;
    private final WoodcuttingBehaviour woodcuttingBehaviour;

    public SkillBehaviourManager(mcMMO pluginRef) {
        this.pluginRef = pluginRef;

        acrobaticsBehaviour = new AcrobaticsBehaviour(pluginRef);
        alchemyBehaviour = new AlchemyBehaviour(pluginRef);
        archeryBehaviour = new ArcheryBehaviour(pluginRef);
        axesBehaviour = new AxesBehaviour(pluginRef);
        excavationBehaviour = new ExcavationBehaviour(pluginRef);
        fishingBehaviour = new FishingBehaviour(pluginRef);
        herbalismBehaviour = new HerbalismBehaviour(pluginRef);
        miningBehaviour = new MiningBehaviour(pluginRef);
        repairBehaviour = new RepairBehaviour(pluginRef);
        salvageBehaviour = new SalvageBehaviour(pluginRef);
        smeltingBehaviour = new SmeltingBehaviour(pluginRef);
        swordsBehaviour = new SwordsBehaviour(pluginRef);
        tamingBehaviour = new TamingBehaviour(pluginRef);
        unarmedBehaviour = new UnarmedBehaviour(pluginRef);
        woodcuttingBehaviour = new WoodcuttingBehaviour(pluginRef);
    }

    public AcrobaticsBehaviour getAcrobaticsBehaviour() {
        return acrobaticsBehaviour;
    }

    public AlchemyBehaviour getAlchemyBehaviour() {
        return alchemyBehaviour;
    }

    public ArcheryBehaviour getArcheryBehaviour() {
        return archeryBehaviour;
    }

    public AxesBehaviour getAxesBehaviour() {
        return axesBehaviour;
    }

    public ExcavationBehaviour getExcavationBehaviour() {
        return excavationBehaviour;
    }

    public FishingBehaviour getFishingBehaviour() {
        return fishingBehaviour;
    }

    public HerbalismBehaviour getHerbalismBehaviour() {
        return herbalismBehaviour;
    }

    public MiningBehaviour getMiningBehaviour() {
        return miningBehaviour;
    }

    public RepairBehaviour getRepairBehaviour() {
        return repairBehaviour;
    }

    public SalvageBehaviour getSalvageBehaviour() {
        return salvageBehaviour;
    }

    public SmeltingBehaviour getSmeltingBehaviour() {
        return smeltingBehaviour;
    }

    public SwordsBehaviour getSwordsBehaviour() {
        return swordsBehaviour;
    }

    public TamingBehaviour getTamingBehaviour() {
        return tamingBehaviour;
    }

    public UnarmedBehaviour getUnarmedBehaviour() {
        return unarmedBehaviour;
    }

    public WoodcuttingBehaviour getWoodcuttingBehaviour() {
        return woodcuttingBehaviour;
    }
}
