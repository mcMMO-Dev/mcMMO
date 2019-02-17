package com.gmail.nossr50.config.mods;

import com.gmail.nossr50.config.ConfigCollections;
import com.gmail.nossr50.datatypes.mods.CustomBlock;
import com.gmail.nossr50.mcMMO;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

public class CustomBlockConfig extends ConfigCollections {
    public List<Material> customExcavationBlocks = new ArrayList<>();
    public List<Material> customHerbalismBlocks = new ArrayList<>();
    public List<Material> customMiningBlocks = new ArrayList<>();
    public List<Material> customOres = new ArrayList<>();
    public List<Material> customLogs = new ArrayList<>();
    public List<Material> customLeaves = new ArrayList<>();
    public List<Material> customAbilityBlocks = new ArrayList<>();
    public HashMap<Material, CustomBlock> customBlockMap = new HashMap<>();
    private boolean needsUpdate = false;

    protected CustomBlockConfig(String fileName) {
        //super(McmmoCore.getDataFolderPath().getPath() + "mods", fileName, false);
        super(mcMMO.p.getDataFolder().getPath() + "mods", fileName, false);
        loadKeys();
    }

    @Override
    protected void loadKeys() {
        loadBlocks("Excavation", customExcavationBlocks);
        loadBlocks("Herbalism", customHerbalismBlocks);
        loadBlocks("Mining", customMiningBlocks);
        loadBlocks("Woodcutting", null);
        loadBlocks("Ability_Blocks", customAbilityBlocks);

        if (needsUpdate) {
            needsUpdate = false;
            backup();
        }
    }

    private void loadBlocks(String skillType, List<Material> blockList) {
        if (needsUpdate) {
            return;
        }

        ConfigurationSection skillSection = config.getConfigurationSection(skillType);

        if (skillSection == null) {
            return;
        }

        Set<String> skillConfigSet = skillSection.getKeys(false);

        for (String blockName : skillConfigSet) {
            if (config.contains(skillType + "." + blockName + ".Drop_Item")) {
                needsUpdate = true;
                return;
            }

            String[] blockInfo = blockName.split("[|]");

            Material blockMaterial = Material.matchMaterial(blockInfo[0]);

            if (blockMaterial == null) {
                plugin.getLogger().warning("Invalid material name. This item will be skipped. - " + blockInfo[0]);
                continue;
            }

            if (blockList != null) {
                blockList.add(blockMaterial);
            }

            if (skillType.equals("Ability_Blocks")) {
                continue;
            }

            int xp = getIntValue(skillType + "." + blockName + ".XP_Gain");
            int smeltingXp = 0;

            if (skillType.equals("Mining") && getBooleanValue(skillType + "." + blockName + ".Is_Ore")) {
                customOres.add(blockMaterial);
                smeltingXp = getIntValue(skillType + "." + blockName + ".Smelting_XP_Gain", xp / 10);
            } else if (skillType.equals("Woodcutting")) {
                if (getBooleanValue(skillType + "." + blockName + ".Is_Log")) {
                    customLogs.add(blockMaterial);
                } else {
                    customLeaves.add(blockMaterial);
                    xp = 0; // Leaves don't grant XP
                }
            }

            customBlockMap.put(blockMaterial, new CustomBlock(xp, getBooleanValue(skillType + "." + blockName + ".Double_Drops_Enabled"), smeltingXp));
        }
    }
}
