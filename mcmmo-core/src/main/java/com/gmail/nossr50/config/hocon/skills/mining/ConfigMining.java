package com.gmail.nossr50.config.hocon.skills.mining;

import com.gmail.nossr50.config.ConfigConstants;
import ninja.leaping.configurate.objectmapping.Setting;
import ninja.leaping.configurate.objectmapping.serialize.ConfigSerializable;

import java.util.ArrayList;
import java.util.HashSet;

import static org.bukkit.Material.*;

@ConfigSerializable
public class ConfigMining {

    private static final HashSet<String> DEFAULT_BONUS_DROPS;

    static {
        DEFAULT_BONUS_DROPS = new HashSet<>();

        DEFAULT_BONUS_DROPS.add(ANDESITE.getKey().toString());
        DEFAULT_BONUS_DROPS.add(DIORITE.getKey().toString());
        DEFAULT_BONUS_DROPS.add(GRANITE.getKey().toString());
        DEFAULT_BONUS_DROPS.add(COAL_ORE.getKey().toString());
        DEFAULT_BONUS_DROPS.add(COAL.getKey().toString());
        DEFAULT_BONUS_DROPS.add(DIAMOND_ORE.getKey().toString());
        DEFAULT_BONUS_DROPS.add(DIAMOND.getKey().toString());
        DEFAULT_BONUS_DROPS.add(EMERALD_ORE.getKey().toString());
        DEFAULT_BONUS_DROPS.add(EMERALD.getKey().toString());
        DEFAULT_BONUS_DROPS.add(END_STONE.getKey().toString());
        DEFAULT_BONUS_DROPS.add(GLOWSTONE.getKey().toString());
        DEFAULT_BONUS_DROPS.add(GLOWSTONE_DUST.getKey().toString());
        DEFAULT_BONUS_DROPS.add(GOLD_ORE.getKey().toString());
        DEFAULT_BONUS_DROPS.add(IRON_ORE.getKey().toString());
        DEFAULT_BONUS_DROPS.add(IRON_INGOT.getKey().toString());
        DEFAULT_BONUS_DROPS.add(LAPIS_ORE.getKey().toString());
        DEFAULT_BONUS_DROPS.add(LAPIS_LAZULI.getKey().toString());
        DEFAULT_BONUS_DROPS.add(MOSSY_COBBLESTONE.getKey().toString());
        DEFAULT_BONUS_DROPS.add(NETHERRACK.getKey().toString());
        DEFAULT_BONUS_DROPS.add(OBSIDIAN.getKey().toString());
        DEFAULT_BONUS_DROPS.add(NETHER_QUARTZ_ORE.getKey().toString());
        DEFAULT_BONUS_DROPS.add(QUARTZ.getKey().toString());
        DEFAULT_BONUS_DROPS.add(REDSTONE_ORE.getKey().toString());
        DEFAULT_BONUS_DROPS.add(REDSTONE.getKey().toString());
        DEFAULT_BONUS_DROPS.add(SANDSTONE.getKey().toString());
        DEFAULT_BONUS_DROPS.add(STONE.getKey().toString());
        DEFAULT_BONUS_DROPS.add(COBBLESTONE.getKey().toString());
    }

    @Setting(value = "Z-Bonus-Drops", comment = "Bonus drops will be allowed for these blocks." +
            "\nUse Minecraft friendly names for entries, not Bukkit material names.")
    private HashSet<String> bonusDrops = DEFAULT_BONUS_DROPS;

    @Setting(value = ConfigConstants.SUB_SKILL_NODE)
    private ConfigMiningSubskills miningSubskills = new ConfigMiningSubskills();

    public ConfigMiningSubskills getMiningSubskills() {
        return miningSubskills;
    }

    public ConfigMiningBlastMining getBlastMining() {
        return miningSubskills.getBlastMining();
    }

    public ArrayList<String> getDetonators() {
        return getBlastMining().getDetonators();
    }

    public HashSet<String> getBonusDrops() {
        return bonusDrops;
    }


}