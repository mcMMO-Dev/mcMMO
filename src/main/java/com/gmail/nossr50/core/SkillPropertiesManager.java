package com.gmail.nossr50.core;

import com.gmail.nossr50.config.ConfigConstants;
import com.gmail.nossr50.datatypes.skills.PrimarySkillType;
import com.gmail.nossr50.datatypes.skills.SubSkillType;
import com.gmail.nossr50.datatypes.skills.properties.MaxBonusLevel;
import com.gmail.nossr50.mcMMO;
import com.gmail.nossr50.util.random.InvalidStaticChance;
import com.google.common.reflect.TypeToken;
import ninja.leaping.configurate.commented.CommentedConfigurationNode;
import ninja.leaping.configurate.objectmapping.ObjectMappingException;

import java.util.HashMap;
import java.util.Iterator;

/**
 * Hacky way to do this until I rewrite the skill system fully
 */
public class SkillPropertiesManager {

    private HashMap<SubSkillType, Double> maxChanceMap;
    private HashMap<SubSkillType, Double> staticActivationChanceMap;
    private HashMap<SubSkillType, Integer> maxBonusLevelMap;
    private HashMap<SubSkillType, Double> maxBonusPercentage;

    public SkillPropertiesManager() {
        maxChanceMap = new HashMap<>();
        maxBonusLevelMap = new HashMap<>();
        maxBonusPercentage = new HashMap<>();
        staticActivationChanceMap = new HashMap<>();
    }

    public void registerMaxBonusLevel(SubSkillType subSkillType, MaxBonusLevel maxBonusLevel) {
        maxBonusLevelMap.put(subSkillType, mcMMO.isRetroModeEnabled() ? maxBonusLevel.getRetroScaleValue() : maxBonusLevel.getStandardScaleValue());
    }

    public void registerMaxChance(SubSkillType subSkillType, double maxChance) {
        maxChanceMap.put(subSkillType, maxChance);
    }

    public double getMaxChance(SubSkillType subSkillType) {
        return maxChanceMap.get(subSkillType);
    }

    public double getMaxBonusLevel(SubSkillType subSkillType) {
        return maxBonusLevelMap.get(subSkillType);
    }

    public void fillRegisters() {
        fillRNGRegisters();
    }

    /**
     * Goes over each of our skill configs and grabs any properties it can find
     */
    private void fillRNGRegisters() {

        //The path to a subskill's properties will always be like this
        //Skill Config Root Node -> Sub-Skill -> Hocon-Friendly-Name (of the subskill) -> PropertyFieldName (camelCase of the interface type)
        for(PrimarySkillType primarySkillType : PrimarySkillType.values()) {
            CommentedConfigurationNode rootNode = mcMMO.getConfigManager().getSkillConfigLoader(primarySkillType).getRootNode();

            //Attempt to grab node
            CommentedConfigurationNode subSkillCategoryNode = getNodeIfReal(rootNode, ConfigConstants.SUB_SKILL_NODE);

            //Check if the root node has a node matching the name "Sub-Skill"
            if(subSkillCategoryNode != null) {

                //Check all the "children" of this skill, this will need to be rewritten in the future
                for (SubSkillType subSkillType : primarySkillType.getSkillAbilities()) {

                    //HOCON friendly subskill name
                    String hoconFriendlySubskillName = subSkillType.getHoconFriendlyConfigName();

                    //Attempt to grab node
                    CommentedConfigurationNode subSkillNode = getNodeIfReal(subSkillCategoryNode, hoconFriendlySubskillName);

                    //Check if the Sub-Skill node has a child matching this subskill name
                    if (subSkillNode != null) {

                        //Check for all the various mcMMO skill properties
                        for(Iterator<? extends CommentedConfigurationNode> it = subSkillNode.getChildrenList().iterator(); it.hasNext();) {

                            CommentedConfigurationNode childNode = it.next();

                            Object lastObjectInPath = childNode.getPath()[childNode.getPath().length - 1];
                            String nodeName = lastObjectInPath.toString();

                            switch(nodeName) {
                                case ConfigConstants.MAX_BONUS_LEVEL_FIELD_NAME:
                                    attemptRegisterMaxBonusLevel(subSkillType, childNode);
                                    break;
                                case ConfigConstants.MAX_CHANCE_FIELD_NAME:
                                    attemptRegisterMaxChance(subSkillType, childNode);
                                    break;
                                case ConfigConstants.STATIC_ACTIVATION_FIELD_NAME:
                                    attemptRegisterStaticChance(subSkillType, childNode);
                                    break;
                                case ConfigConstants.MAX_BONUS_PERCENTAGE_FIELD_NAME:
                                    attemptRegisterMaxBonusPercentage(subSkillType, childNode);
                                    break;
                            }

                        }
                    }
                }
            }
        }
    }

    private CommentedConfigurationNode getNodeIfReal(CommentedConfigurationNode configurationNode, String path) {
        if(doesNodeExist(configurationNode.getNode(path)))
            return configurationNode.getNode(path);
        else
            return null;
    }

    private boolean doesNodeExist(CommentedConfigurationNode configurationNode) {
        return configurationNode.getValue() != null;
    }

    private void attemptRegisterMaxBonusLevel(SubSkillType subSkillType, CommentedConfigurationNode childNode) {
        try {
            MaxBonusLevel maxBonusLevel = childNode.getValue(TypeToken.of(MaxBonusLevel.class));
            registerMaxBonusLevel(subSkillType, maxBonusLevel);
            mcMMO.p.getLogger().info("Registered MaxBonusLevel for "+subSkillType.toString());
        } catch (ObjectMappingException e) {
            //This time a silent exception is fine
        }
    }

    private void attemptRegisterMaxChance(SubSkillType subSkillType, CommentedConfigurationNode childNode) {
        try {
            Double maxChance = childNode.getValue(TypeToken.of(Double.class));
            registerMaxChance(subSkillType, maxChance);
            mcMMO.p.getLogger().info("Registered MaxChance for "+subSkillType.toString());
        } catch (ObjectMappingException e) {
            //This time a silent exception is fine
        }
    }

    private void attemptRegisterStaticChance(SubSkillType subSkillType, CommentedConfigurationNode childNode) {

    }

    private void attemptRegisterMaxBonusPercentage(SubSkillType subSkillType, CommentedConfigurationNode childNode) {

    }

    public double getStaticChanceProperty(SubSkillType subSkillType) throws InvalidStaticChance {
        if(staticActivationChanceMap.get(subSkillType) == null)
            throw new InvalidStaticChance();

        return staticActivationChanceMap.get(subSkillType);
    }

}
