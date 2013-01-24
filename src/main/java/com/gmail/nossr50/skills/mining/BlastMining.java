package com.gmail.nossr50.skills.mining;

import com.gmail.nossr50.config.AdvancedConfig;
import com.gmail.nossr50.config.Config;

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
}
