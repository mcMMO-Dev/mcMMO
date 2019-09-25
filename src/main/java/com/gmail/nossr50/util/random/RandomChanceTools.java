package com.gmail.nossr50.util.random;

import com.gmail.nossr50.datatypes.skills.PrimarySkillType;
import com.gmail.nossr50.datatypes.skills.SubSkillType;
import com.gmail.nossr50.datatypes.skills.subskills.AbstractSubSkill;
import com.gmail.nossr50.events.skills.secondaryabilities.SubSkillEvent;
import com.gmail.nossr50.events.skills.secondaryabilities.SubSkillRandomCheckEvent;
import com.gmail.nossr50.mcMMO;
import com.gmail.nossr50.util.skills.SkillActivationType;
import org.bukkit.entity.Player;

import java.text.DecimalFormat;
import java.util.Random;

public class RandomChanceTools {
    
    private final mcMMO pluginRef;
    
    public final DecimalFormat percent;
    public final double LINEAR_CURVE_VAR;

    public RandomChanceTools(mcMMO pluginRef) {
        this.pluginRef = pluginRef;

        percent = new DecimalFormat("##0.00%");
        LINEAR_CURVE_VAR = 100.0D;
    }

    /**
     * This method is the final step in determining if a Sub-Skill / Secondary Skill in mcMMO successfully activates either from chance or otherwise
     * Random skills check for success based on numbers and then fire a cancellable event, if that event is not cancelled they succeed
     * non-RNG skills just fire the cancellable event and succeed if they go uncancelled
     *
     * @param skillActivationType this value represents what kind of activation procedures this sub-skill uses
     * @param subSkillType The identifier for this specific sub-skill
     * @param player The owner of this sub-skill
     * @return returns true if all conditions are met and the event is not cancelled
     */
    public boolean isActivationSuccessful(SkillActivationType skillActivationType, SubSkillType subSkillType, Player player) {
        switch (skillActivationType) {
            case RANDOM_LINEAR_100_SCALE_WITH_CAP:
                return checkRandomChanceExecutionSuccess(player, subSkillType);
            case RANDOM_STATIC_CHANCE:
                return checkRandomStaticChanceExecutionSuccess(player, subSkillType);
            case ALWAYS_FIRES:
                SubSkillEvent event = pluginRef.getEventManager().callSubSkillEvent(player, subSkillType, pluginRef.getSkillTools().getPrimarySkillBySubSkill(subSkillType));
                return !event.isCancelled();
            default:
                return false;
        }
    }

    public double getActivationChance(SkillActivationType skillActivationType, SubSkillType subSkillType, Player player) {
        switch (skillActivationType) {
            case RANDOM_LINEAR_100_SCALE_WITH_CAP:
                return getRandomChanceExecutionSuccess(player, subSkillType, true);
            case RANDOM_STATIC_CHANCE:
                return getRandomStaticChanceExecutionSuccess(player, subSkillType);
            default:
                return 0.1337;
        }
    }

    /**
     * Checks whether or not the random chance succeeds
     *
     * @return true if the random chance succeeds
     */
    public boolean checkRandomChanceExecutionSuccess(Player player, PrimarySkillType primarySkillType, double chance) {
        //Check the odds
        chance *= 100;

        chance = addLuck(player, primarySkillType, chance);

        /*
         * Stuff like treasures can specify a drop chance from 0.05 to 100
         * Because of that we need to use a large int bound and multiply the chance by 100
         */
        return rollDice(chance, 10000);
    }

    public boolean rollDice(double chanceOfSuccess, int bound) {
        Random random = new Random();

        if (chanceOfSuccess > random.nextInt(bound))
            return true;
        else
            return false;
    }

    /**
     * Used for stuff like Excavation, Fishing, etc...
     *
     * @param randomChance
     * @return
     */
    public boolean checkRandomChanceExecutionSuccess(RandomChanceSkillStatic randomChance) {
        double chanceOfSuccess = calculateChanceOfSuccess(randomChance);

        //Check the odds
        return rollDice(chanceOfSuccess, 100);
    }

    public boolean checkRandomChanceExecutionSuccess(RandomChanceSkill randomChance) {
        double chanceOfSuccess = calculateChanceOfSuccess(randomChance);

        //Check the odds
        return rollDice(chanceOfSuccess, 100);
    }


    /*public double getRandomChanceExecutionChance(RandomChanceSkill randomChance)
    {
        double chanceOfSuccess = calculateChanceOfSuccess(randomChance);
        return chanceOfSuccess;
    }*/

    /**
     * Gets the Chance for something to activate
     *
     * @param randomChance
     * @return
     */
    public double getRandomChanceExecutionChance(RandomChanceExecution randomChance) {
        double chanceOfSuccess = getChanceOfSuccess(randomChance.getXPos(), randomChance.getProbabilityCap(), LINEAR_CURVE_VAR);

        return chanceOfSuccess;
    }

    public double getRandomChanceExecutionChance(RandomChanceStatic randomChance) {
        double chanceOfSuccess = getChanceOfSuccess(randomChance.getXPos(), randomChance.getProbabilityCap(), LINEAR_CURVE_VAR);

        chanceOfSuccess = addLuck(randomChance.isLucky(), chanceOfSuccess);

        return chanceOfSuccess;
    }

    /*private double calculateChanceOfSuccess(RandomChancerandomChance) {
        double chanceOfSuccess = getChanceOfSuccess(randomChance.getXPos(), randomChance.getProbabilityCap());
        return chanceOfSuccess;
    }*/

    private double calculateChanceOfSuccess(RandomChanceSkill randomChanceSkill) {
        double skillLevel = randomChanceSkill.getSkillLevel();
        double maximumProbability = randomChanceSkill.getProbabilityCap();
        double maximumBonusLevel = randomChanceSkill.getMaximumBonusLevelCap();

        double chanceOfSuccess;

        if (skillLevel >= maximumBonusLevel) {
            //Chance of success is equal to the maximum probability if the maximum bonus level has been reached
            chanceOfSuccess = maximumProbability;
        } else {
            //Get chance of success
            chanceOfSuccess = getChanceOfSuccess(randomChanceSkill.getXPos(), maximumProbability, maximumBonusLevel);
        }

        //Add Luck
        chanceOfSuccess = addLuck(randomChanceSkill.isLucky(), chanceOfSuccess);

        return chanceOfSuccess;
    }

    private double calculateChanceOfSuccess(RandomChanceSkillStatic randomChance) {
        double chanceOfSuccess = getChanceOfSuccess(randomChance.getXPos(), 100, 100);

        //Add Luck
        chanceOfSuccess = addLuck(randomChance.isLucky(), chanceOfSuccess);

        return chanceOfSuccess;
    }

    /**
     * The formula for RNG success is determined like this
     * maximum probability * ( x / maxlevel )
     *
     * @return the chance of success from 0-100 (100 = guaranteed)
     */
    private int getChanceOfSuccess(double skillLevel, double maxProbability, double maxLevel) {
        //return (int) (x / (y / LINEAR_CURVE_VAR));
        return (int) (maxProbability * (skillLevel / maxLevel));
        // max probability * (weight/maxlevel) = chance of success
    }

    private int getChanceOfSuccess(double x, double y) {
        return (int) (x / (y / LINEAR_CURVE_VAR));
        // max probability * (weight/maxlevel) = chance of success
    }

    public double getRandomChanceExecutionSuccess(Player player, SubSkillType subSkillType, boolean hasCap) {
        RandomChanceSkill rcs = new RandomChanceSkill(pluginRef, player, subSkillType, hasCap);
        return calculateChanceOfSuccess(rcs);
    }

    public double getRandomStaticChanceExecutionSuccess(Player player, SubSkillType subSkillType) {
        try {
            return getRandomChanceExecutionChance(new RandomChanceSkillStatic(pluginRef, getStaticRandomChance(subSkillType), player, subSkillType));
        } catch (InvalidStaticChance invalidStaticChance) {
            //Catch invalid skills
            invalidStaticChance.printStackTrace();
        }

        return 0.1337; //Puts on shades
    }

    public boolean checkRandomChanceExecutionSuccess(Player player, SubSkillType subSkillType) {
        return checkRandomChanceExecutionSuccess(new RandomChanceSkill(pluginRef, player, subSkillType));
    }

    public boolean checkRandomStaticChanceExecutionSuccess(Player player, SubSkillType subSkillType) {
        try {
            return checkRandomChanceExecutionSuccess(new RandomChanceSkillStatic(pluginRef, getStaticRandomChance(subSkillType), player, subSkillType));
        } catch (InvalidStaticChance invalidStaticChance) {
            //Catch invalid skills
            invalidStaticChance.printStackTrace();
        }

        return false;
    }

    /**
     * Grabs activation rolls for Secondary Abilities
     *
     * @param subSkillType The secondary ability to grab properties of
     * @return The activation roll involved in the RNG calculation
     * @throws InvalidStaticChance if the skill has no defined chance this exception will be thrown and you should know you're a naughty boy
     */
    public double getStaticRandomChance(SubSkillType subSkillType) throws InvalidStaticChance {
        return pluginRef.getDynamicSettingsManager().getSkillPropertiesManager().getStaticChanceProperty(subSkillType);
    }

    public boolean sendSkillEvent(Player player, SubSkillType subSkillType, double activationChance) {
        SubSkillRandomCheckEvent event = new SubSkillRandomCheckEvent(player, subSkillType, activationChance, pluginRef.getSkillTools().getPrimarySkillBySubSkill(subSkillType));
        return !event.isCancelled();
    }

    /*public boolean treasureDropSuccessful(Player player, double dropChance, int activationChance) {
        SubSkillRandomCheckEvent event = new SubSkillRandomCheckEvent(player, SubSkillType.EXCAVATION_ARCHAEOLOGY, dropChance / activationChance);
        mcMMO.p.getServer().getPluginManager().callEvent(event);
        return (event.getChance() * activationChance) > (Misc.getRandom().nextDouble() * activationChance) && !event.isCancelled();
    }*/

    public boolean isActivationSuccessful(SkillActivationType skillActivationType, AbstractSubSkill abstractSubSkill, Player player) {
        return isActivationSuccessful(skillActivationType, abstractSubSkill.getSubSkillType(), player);
    }

    public String[] calculateAbilityDisplayValues(SkillActivationType skillActivationType, Player player, SubSkillType subSkillType) {
        double successChance = getActivationChance(skillActivationType, subSkillType, player);
        String[] displayValues = new String[2];

        boolean isLucky = pluginRef.getPermissionTools().lucky(player, subSkillType.getParentSkill(pluginRef));

        displayValues[0] = percent.format(Math.min(successChance, 100.0D) / 100.0D);
        displayValues[1] = isLucky ? percent.format(Math.min(successChance * 1.3333D, 100.0D) / 100.0D) : null;

        return displayValues;
    }

    public String[] calculateAbilityDisplayValuesStatic(Player player, PrimarySkillType primarySkillType, double chance) {
        RandomChanceStatic rcs = new RandomChanceStatic(chance, false);
        double successChance = getRandomChanceExecutionChance(rcs);

        RandomChanceStatic rcs_lucky = new RandomChanceStatic(chance, true);
        double successChance_lucky = getRandomChanceExecutionChance(rcs_lucky);

        String[] displayValues = new String[2];

        boolean isLucky = pluginRef.getPermissionTools().lucky(player, primarySkillType);

        displayValues[0] = percent.format(Math.min(successChance, 100.0D) / 100.0D);
        displayValues[1] = isLucky ? percent.format(Math.min(successChance_lucky, 100.0D) / 100.0D) : null;

        return displayValues;
    }

    public String[] calculateAbilityDisplayValuesCustom(SkillActivationType skillActivationType, Player player, SubSkillType subSkillType, double multiplier) {
        double successChance = getActivationChance(skillActivationType, subSkillType, player);
        successChance *= multiplier; //Currently only used for graceful roll
        String[] displayValues = new String[2];

        //TODO: Account for lucky in this

        boolean isLucky = pluginRef.getPermissionTools().lucky(player, subSkillType.getParentSkill(pluginRef));

        displayValues[0] = percent.format(Math.min(successChance, 100.0D) / 100.0D);
        displayValues[1] = isLucky ? percent.format(Math.min(successChance * 1.3333D, 100.0D) / 100.0D) : null;

        return displayValues;
    }

    public double addLuck(Player player, PrimarySkillType primarySkillType, double chance) {
        if (pluginRef.getPermissionTools().lucky(player, primarySkillType))
            return chance * 1.333D;
        else
            return chance;
    }

    public double addLuck(boolean isLucky, double chance) {
        if (isLucky)
            return chance * 1.333D;
        else
            return chance;
    }
}
