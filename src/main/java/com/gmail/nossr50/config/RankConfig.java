package com.gmail.nossr50.config;

import com.gmail.nossr50.config.hocon.skills.ranks.SkillRankProperty;
import com.gmail.nossr50.datatypes.skills.SubSkillType;
import com.gmail.nossr50.datatypes.skills.subskills.AbstractSubSkill;
import com.gmail.nossr50.mcMMO;
import ninja.leaping.configurate.commented.CommentedConfigurationNode;
import ninja.leaping.configurate.objectmapping.serialize.ConfigSerializable;

import java.util.ArrayList;
import java.util.List;

@ConfigSerializable
public class RankConfig extends ConfigValidated {

    //private static RankConfig instance;

    public RankConfig() {
        //super(McmmoCore.getDataFolderPath().getAbsoluteFile(),"skillranks.yml", true);
        super("skillranks", mcMMO.p.getDataFolder().getAbsoluteFile(), ConfigConstants.RELATIVE_PATH_CONFIG_DIR, true, true, true, true);
        //this.instance = this;
    }

    /**
     * This grabs an instance of this config class from the Config Manager
     * This method is deprecated and will be removed in the future
     *
     * @return the instance of this config
     * @see mcMMO#getConfigManager()
     * @deprecated Please use mcMMO.getConfigManager() to grab a specific config instead
     */
    @Deprecated
    public static RankConfig getInstance() {
        return mcMMO.getConfigManager().getRankConfig();
    }

    /**
     * The version of this config
     *
     * @return
     */
    @Override
    public double getConfigVersion() {
        return 1;
    }

    @Override
    public List<String> validateKeys() {
        List<String> reason = new ArrayList<>();

        /*
         * In the future this method will check keys for all skills, but for now it only checks overhauled skills
         */
        checkKeys(reason);

        return reason;
    }



    /**
     * Checks for valid keys for subskill ranks
     */
    private void checkKeys(List<String> reasons) {
        //For now we will only check ranks of stuff I've overhauled
        for (SubSkillType subSkillType : SubSkillType.values()) {
            //Keeping track of the rank requirements and making sure there are no logical errors
            int curRank = 0;
            int prevRank = 0;

            for (int x = 0; x < subSkillType.getNumRanks(); x++) {
                if (curRank > 0)
                    prevRank = curRank;

                curRank = getSubSkillUnlockLevel(subSkillType, x);

                if (prevRank > curRank) {
                    //We're going to allow this but we're going to warn them
                    mcMMO.p.getLogger().severe("You have the ranks for the subskill " + subSkillType.toString() + " in skillranks config set up poorly, sequential ranks should have ascending requirements");
                }
            }
        }
    }
}
