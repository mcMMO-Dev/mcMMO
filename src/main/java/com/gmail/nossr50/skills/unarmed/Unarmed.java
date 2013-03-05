package com.gmail.nossr50.skills.unarmed;

import org.bukkit.Material;
import org.bukkit.block.BlockState;
import org.bukkit.entity.Player;

import com.gmail.nossr50.config.AdvancedConfig;
import com.gmail.nossr50.config.Config;
import com.gmail.nossr50.util.skills.SkillUtils;

public class Unarmed {
    public static int ironArmMaxBonusDamage = AdvancedConfig.getInstance().getIronArmMaxBonus();
    public static int ironArmIncreaseLevel  = AdvancedConfig.getInstance().getIronArmIncreaseLevel();

    public static int    disarmMaxBonusLevel = AdvancedConfig.getInstance().getDisarmMaxBonusLevel();
    public static double disarmMaxChance     = AdvancedConfig.getInstance().getDisarmChanceMax();

    public static int    deflectMaxBonusLevel = AdvancedConfig.getInstance().getDeflectMaxBonusLevel();
    public static double deflectMaxChance     = AdvancedConfig.getInstance().getDeflectChanceMax();

    public static int    ironGripMaxBonusLevel = AdvancedConfig.getInstance().getIronGripMaxBonusLevel();
    public static double ironGripMaxChance     = AdvancedConfig.getInstance().getIronGripChanceMax();

    public static boolean blockCrackerSmoothBrick = Config.getInstance().getUnarmedBlockCrackerSmoothbrickToCracked();

    public static double berserkDamageModifier = 1.5;

    public static boolean blockCracker(Player player, BlockState blockState) {
        if (SkillUtils.blockBreakSimulate(blockState.getBlock(), player, false)) {
            Material type = blockState.getType();

            switch (type) {
                case SMOOTH_BRICK:
                    if (blockCrackerSmoothBrick && blockState.getRawData() == (byte) 0x0) {
                        blockState.setRawData((byte) 0x2);
                    }
                    return true;

                default:
                    return false;
            }
        }

        return false;
    }
}
