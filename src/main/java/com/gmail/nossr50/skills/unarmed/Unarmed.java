package com.gmail.nossr50.skills.unarmed;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;

import com.gmail.nossr50.config.AdvancedConfig;
import com.gmail.nossr50.config.Config;
import com.gmail.nossr50.util.Misc;

public class Unarmed {
    public static int ironArmMaxBonusDamage = AdvancedConfig.getInstance().getIronArmMaxBonus();
    public static int ironArmIncreaseLevel = AdvancedConfig.getInstance().getIronArmIncreaseLevel();

    public static double disarmMaxChance = AdvancedConfig.getInstance().getDisarmChanceMax() ;
    public static int disarmMaxBonusLevel = AdvancedConfig.getInstance().getDisarmMaxBonusLevel();

    public static double deflectMaxChance = AdvancedConfig.getInstance().getDeflectChanceMax();
    public static int deflectMaxBonusLevel = AdvancedConfig.getInstance().getDeflectMaxBonusLevel();

    public static double ironGripMaxChance = AdvancedConfig.getInstance().getIronGripChanceMax();
    public static int ironGripMaxBonusLevel = AdvancedConfig.getInstance().getIronGripMaxBonusLevel();

    public static boolean blockCrackerSmoothBrick = Config.getInstance().getUnarmedBlockCrackerSmoothbrickToCracked();

    public static boolean pvpEnabled = Config.getInstance().getUnarmedPVP();
    public static boolean pveEnabled = Config.getInstance().getUnarmedPVE();

    public static double berserkDamageModifier = 1.5;

    public static void blockCracker(Player player, Block block) {
        if (Misc.blockBreakSimulate(block, player, false)) {
            Material type = block.getType();

            switch (type) {
                case SMOOTH_BRICK:
                    if (blockCrackerSmoothBrick && block.getData() == 0x0) {
                        block.setData((byte) 0x2);
                    }
                    return;

                default:
                    return;
            }
        }
    }
}