package com.gmail.nossr50.skills.mining;

import org.bukkit.entity.Player;

import com.gmail.nossr50.config.AdvancedConfig;
import com.gmail.nossr50.config.Config;
import com.gmail.nossr50.skills.utilities.SkillTools;
import com.gmail.nossr50.skills.utilities.SkillType;
import com.gmail.nossr50.util.Permissions;
import com.gmail.nossr50.util.Users;

public class BlastMining {
    public static int rank1 = AdvancedConfig.getInstance().getBlastMiningRank1();
    public static int rank2 = AdvancedConfig.getInstance().getBlastMiningRank2();
    public static int rank3 = AdvancedConfig.getInstance().getBlastMiningRank3();
    public static int rank4 = AdvancedConfig.getInstance().getBlastMiningRank4();
    public static int rank5 = AdvancedConfig.getInstance().getBlastMiningRank5();
    public static int rank6 = AdvancedConfig.getInstance().getBlastMiningRank6();
    public static int rank7 = AdvancedConfig.getInstance().getBlastMiningRank7();
    public static int rank8 = AdvancedConfig.getInstance().getBlastMiningRank8();

    public static int detonatorID = Config.getInstance().getDetonatorItemID();

    public final static int MAXIMUM_REMOTE_DETONATION_DISTANCE = 100;

    public static boolean canUseDemolitionsExpertise(Player player) {
        return SkillTools.unlockLevelReached(player, SkillType.MINING, rank4) && Permissions.demolitionsExpertise(player);
    }

    public static int processDemolitionsExpertise(Player player, int damage) {
        int skillLevel = Users.getPlayer(player).getProfile().getSkillLevel(SkillType.MINING);
        int modifiedDamage;

        if (skillLevel >= BlastMining.rank8) {
            modifiedDamage = 0;
        }
        else if (skillLevel >= BlastMining.rank6) {
            modifiedDamage = damage / 4;
        }
        else {
            modifiedDamage = damage / 2;
        }

        return modifiedDamage;
    }
}
