package com.gmail.nossr50.config.tiers;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;

import org.bukkit.configuration.ConfigurationSection;

import com.gmail.nossr50.config.ConfigLoader;

public class RankLoader extends ConfigLoader {

    private List<FishingRank> fishingRanks;
    private List<SmeltingRank> smeltingRanks;
    private List<BlastMiningRank> blastMiningRanks;
    private List<RepairRank> repairRanks;
    private static RankLoader instance;

    private RankLoader() {
        super("ranks.yml");
        fishingRanks = new ArrayList<FishingRank>();
        smeltingRanks = new ArrayList<SmeltingRank>();
        blastMiningRanks = new ArrayList<BlastMiningRank>();
        repairRanks = new ArrayList<RepairRank>();
        loadKeys();
    }

    public static RankLoader getInstance() {
        if (instance == null) {
            instance = new RankLoader();
        }

        return instance;
    }

    @Override
    protected void loadKeys() {
        ConfigurationSection section = config.getConfigurationSection("Ranks.Fishing");
        RankComparator rankComparator = new RankComparator();
        for (String rank : section.getKeys(false)) {
            fishingRanks.add(new FishingRank(section.getConfigurationSection(rank)));
        }
        Collections.sort(fishingRanks, rankComparator);

        section = config.getConfigurationSection("Ranks.BlastMining");
        for (String rank : section.getKeys(false)) {
            blastMiningRanks.add(new BlastMiningRank(section.getConfigurationSection(rank)));
        }
        Collections.sort(blastMiningRanks, rankComparator);

        section = config.getConfigurationSection("Ranks.Repair");
        for (String rank : section.getKeys(false)) {
            repairRanks.add(new RepairRank(section.getConfigurationSection(rank)));
        }
        Collections.sort(repairRanks, rankComparator);

        section = config.getConfigurationSection("Ranks.Smelting");
        for (String rank : section.getKeys(false)) {
            smeltingRanks.add(new SmeltingRank(section.getConfigurationSection(rank)));
        }
        Collections.sort(smeltingRanks, rankComparator);
    }

    public FishingRank getFishingRank(int level) {
        Iterator<FishingRank> it = fishingRanks.iterator();
        while (it.hasNext()) {
            FishingRank rank = it.next();
            if (rank.getLevel() > level) {
                return rank;
            }
        }
        return null;
    }

    public RepairRank getRepairRank(int level) {
        Iterator<RepairRank> it = repairRanks.iterator();
        while (it.hasNext()) {
            RepairRank rank = it.next();
            if (rank.getLevel() > level) {
                return rank;
            }
        }
        return null;
    }

    public SmeltingRank getSmeltingRank(int level) {
        Iterator<SmeltingRank> it = smeltingRanks.iterator();
        while (it.hasNext()) {
            SmeltingRank rank = it.next();
            if (rank.getLevel() > level) {
                return rank;
            }
        }
        return null;
    }

    public BlastMiningRank getBlastMiningRank(int level) {
        Iterator<BlastMiningRank> it = blastMiningRanks.iterator();
        while (it.hasNext()) {
            BlastMiningRank rank = it.next();
            if (rank.getLevel() > level) {
                return rank;
            }
        }
        return null;
    }

    private class RankComparator implements Comparator<Rank> {
        @Override
        public int compare(Rank o1, Rank o2) {
            return Integer.valueOf(o1.getLevel()).compareTo(Integer.valueOf(o2.getLevel()));
        }
        
    }
}
