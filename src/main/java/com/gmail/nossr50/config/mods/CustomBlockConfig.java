package com.gmail.nossr50.config.mods;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.material.MaterialData;

import com.gmail.nossr50.config.ConfigLoader;
import com.gmail.nossr50.datatypes.mods.CustomBlock;

public class CustomBlockConfig extends ConfigLoader {
    private boolean needsUpdate = false;

    public List<MaterialData> customExcavationBlocks  = new ArrayList<MaterialData>();
    public List<MaterialData> customHerbalismBlocks   = new ArrayList<MaterialData>();
    public List<MaterialData> customMiningBlocks      = new ArrayList<MaterialData>();
    public List<MaterialData> customOres              = new ArrayList<MaterialData>();
    public List<MaterialData> customLogs              = new ArrayList<MaterialData>();
    public List<MaterialData> customLeaves            = new ArrayList<MaterialData>();
    public List<MaterialData> customAbilityBlocks     = new ArrayList<MaterialData>();

    public HashMap<MaterialData, CustomBlock> customBlockMap = new HashMap<MaterialData, CustomBlock>();

    protected CustomBlockConfig(String fileName) {
        super("mods", fileName);
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

    private void loadBlocks(String skillType, List<MaterialData> blockList) {
        if (needsUpdate) {
            return;
        }

        ConfigurationSection skillSection = config.getConfigurationSection(skillType);

        if (skillSection == null) {
            return;
        }

        Set<String> skillConfigSet = skillSection.getKeys(false);

        for (String blockName : skillConfigSet) {
            if (config.contains(skillType + "." + blockName + "." + ".Drop_Item")) {
                needsUpdate = true;
                return;
            }

            String[] blockInfo = blockName.split("[|]");

            Material blockMaterial = Material.matchMaterial(blockInfo[0]);

            if (blockMaterial == null) {
                plugin.getLogger().warning("Invalid material name. This item will be skipped. - " + blockInfo[0]);
                continue;
            }

            byte blockData = (blockInfo.length == 2) ? Byte.valueOf(blockInfo[1]) : 0;
            MaterialData blockMaterialData = new MaterialData(blockMaterial, blockData);

            if (blockList != null) {
                blockList.add(blockMaterialData);
            }

            if (skillType.equals("Ability_Blocks")) {
                continue;
            }

            int xp = config.getInt(skillType + "." + blockName + ".XP_Gain");
            int smeltingXp = 0;

            if (skillType.equals("Mining") && config.getBoolean(skillType + "." + blockName + ".Is_Ore")) {
                customOres.add(blockMaterialData);
                smeltingXp = config.getInt(skillType + "." + blockName + ".Smelting_XP_Gain", xp / 10);
            }
            else if (skillType.equals("Woodcutting")) {
                if (config.getBoolean(skillType + "." + blockName + ".Is_Log")) {
                    customLogs.add(blockMaterialData);
                }
                else {
                    customLeaves.add(blockMaterialData);
                    xp = 0; // Leaves don't grant XP
                }
            }

            customBlockMap.put(blockMaterialData, new CustomBlock(xp, config.getBoolean(skillType + "." + blockName + ".Double_Drops_Enabled"), smeltingXp));
        }
    }
}
