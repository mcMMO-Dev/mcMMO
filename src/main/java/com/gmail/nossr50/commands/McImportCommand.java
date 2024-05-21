package com.gmail.nossr50.commands;

import com.gmail.nossr50.datatypes.skills.ModConfigType;
import com.gmail.nossr50.mcMMO;
import com.gmail.nossr50.util.Misc;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;

public class McImportCommand implements CommandExecutor {
    int fileAmount;

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, String[] args) {
        if (args.length == 0) {
            importModConfig();
            return true;
        }
        return false;
    }

    public boolean importModConfig() {
        String importFilePath = mcMMO.getModDirectory() + File.separator + "import";
        File importFile = new File(importFilePath, "import.log");
        mcMMO.p.getLogger().info("Starting import of mod materials...");
        fileAmount = 0;

        HashMap<ModConfigType, ArrayList<String>> materialNames = new HashMap<>();

        BufferedReader in = null;

        try {
            // Open the file
            in = new BufferedReader(new FileReader(importFile));

            String line;
            String materialName;
            String modName;

            // While not at the end of the file
            while ((line = in.readLine()) != null) {
                String[] split1 = line.split("material ");

                if (split1.length != 2) {
                    continue;
                }

                String[] split2 = split1[1].split(" with");

                if (split2.length != 2) {
                    continue;
                }

                materialName = split2[0];

                // Categorise each material under a mod config type
                ModConfigType type = ModConfigType.getModConfigType(materialName);

                if (!materialNames.containsKey(type)) {
                    materialNames.put(type, new ArrayList<>());
                }

                materialNames.get(type).add(materialName);
            }
        }
        catch (FileNotFoundException e) {
            mcMMO.p.getLogger().warning("Could not find " + importFile.getAbsolutePath() + " ! (No such file or directory)");
            mcMMO.p.getLogger().warning("Copy and paste latest.log to " + importFile.getParentFile().getAbsolutePath() + " and rename it to import.log");
            return false;
        }
        catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        finally {
            tryClose(in);
        }

        createOutput(materialNames);

        mcMMO.p.getLogger().info("Import finished! Created " + fileAmount + " files!");
        return true;
    }

    private void createOutput(HashMap<ModConfigType, ArrayList<String>> materialNames) {
        for (ModConfigType modConfigType : materialNames.keySet()) {
            HashMap<String, ArrayList<String>> materialNamesType = new HashMap<>();

            for (String materialName : materialNames.get(modConfigType)) {
                String modName = Misc.getModName(materialName);

                if (!materialNamesType.containsKey(modName)) {
                    materialNamesType.put(modName, new ArrayList<>());
                }

                materialNamesType.get(modName).add(materialName);
            }

            createOutput(modConfigType, materialNamesType);
        }

    }

    private void tryClose(Closeable c) {
        if (c == null) {
            return;
        }
        try {
            c.close();
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void createOutput(ModConfigType modConfigType, HashMap<String, ArrayList<String>> materialNames) {
        File outputFilePath = new File(mcMMO.getModDirectory() + File.separator + "output");
        if (!outputFilePath.exists() && !outputFilePath.mkdirs()) {
            mcMMO.p.getLogger().severe("Could not create output directory! " + outputFilePath.getAbsolutePath());
        }

        FileWriter out = null;
        String type = modConfigType.name().toLowerCase(Locale.ENGLISH);

        for (String modName : materialNames.keySet()) {
            File outputFile = new File(outputFilePath, modName + "." + type + ".yml");
            mcMMO.p.getLogger().info("Creating " + outputFile.getName());
            try {
                if (outputFile.exists() && !outputFile.delete()) {
                    mcMMO.p.getLogger().severe("Not able to delete old output file! " + outputFile.getAbsolutePath());
                }

                if (!outputFile.createNewFile()) {
                    mcMMO.p.getLogger().severe("Could not create output file! " + outputFile.getAbsolutePath());
                    continue;
                }

                StringBuilder writer = new StringBuilder();
                HashMap<String, ArrayList<String>> configSections = getConfigSections(modConfigType, modName, materialNames);

                if (configSections == null) {
                    mcMMO.p.getLogger().severe("Something went wrong!! type is " + type);
                    return;
                }

                // Write the file, go through each skill and write all the materials
                for (String configSection : configSections.keySet()) {
                    if (configSection.equals("UNIDENTIFIED")) {
                        writer.append("# This isn't a valid config section and all materials in this category need to be").append("\r\n");
                        writer.append("# copy and pasted to a valid section of this config file.").append("\r\n");
                    }
                    writer.append(configSection).append(":").append("\r\n");

                    for (String line : configSections.get(configSection)) {
                        writer.append(line).append("\r\n");
                    }

                    writer.append("\r\n");
                }

                out = new FileWriter(outputFile);
                out.write(writer.toString());
            } catch (Exception e) {
                e.printStackTrace();
                return;
            } finally {
                tryClose(out);
                fileAmount++;
            }
        }
    }

    private HashMap<String, ArrayList<String>> getConfigSections(ModConfigType type, String modName, HashMap<String, ArrayList<String>> materialNames) {
        switch (type) {
            case BLOCKS:
                return getConfigSectionsBlocks(modName, materialNames);
            case TOOLS:
                return getConfigSectionsTools(modName, materialNames);
            case ARMOR:
                return getConfigSectionsArmor(modName, materialNames);
            case UNKNOWN:
                return getConfigSectionsUnknown(modName, materialNames);
        }

        return null;
    }

    private HashMap<String, ArrayList<String>> getConfigSectionsBlocks(String modName, HashMap<String, ArrayList<String>> materialNames) {
        HashMap<String, ArrayList<String>> configSections = new HashMap<>();

        // Go through all the materials and categorise them under a skill
        for (String materialName : materialNames.get(modName)) {
            String skillName = "UNIDENTIFIED";
            if (materialName.contains("ORE")) {
                skillName = "Mining";
            } else if (materialName.contains("LOG") || materialName.contains("LEAVES")) {
                skillName = "Woodcutting";
            } else if (materialName.contains("GRASS") || materialName.contains("SHORT_GRASS") || materialName.contains("FLOWER") || materialName.contains("CROP")) {
                skillName = "Herbalism";
            } else if (materialName.contains("DIRT") || materialName.contains("SAND")) {
                skillName = "Excavation";
            }

            if (!configSections.containsKey(skillName)) {
                configSections.put(skillName, new ArrayList<>());
            }

            ArrayList<String> skillContents = configSections.get(skillName);
            skillContents.add("    " + materialName + "|0:");
            skillContents.add("    " + "    " + "XP_Gain: 99");
            skillContents.add("    " + "    " + "Double_Drops_Enabled: true");

            if (skillName.equals("Mining")) {
                skillContents.add("    " + "    " + "Smelting_XP_Gain: 9");
            } else if (skillName.equals("Woodcutting")) {
                skillContents.add("    " + "    " + "Is_Log: " + materialName.contains("LOG"));
            }
        }

        return configSections;
    }

    private HashMap<String, ArrayList<String>> getConfigSectionsTools(String modName, HashMap<String, ArrayList<String>> materialNames) {
        HashMap<String, ArrayList<String>> configSections = new HashMap<>();

        // Go through all the materials and categorise them under a tool type
        for (String materialName : materialNames.get(modName)) {
            String toolType = "UNIDENTIFIED";
            if (materialName.contains("PICKAXE")) {
                toolType = "Pickaxes";
            } else if (materialName.contains("AXE")) {
                toolType = "Axes";
            } else if (materialName.contains("BOW")) {
                toolType = "Bows";
            } else if (materialName.contains("HOE")) {
                toolType = "Hoes";
            } else if (materialName.contains("SHOVEL") || materialName.contains("SPADE")) {
                toolType = "Shovels";
            } else if (materialName.contains("SWORD")) {
                toolType = "Swords";
            }

            if (!configSections.containsKey(toolType)) {
                configSections.put(toolType, new ArrayList<>());
            }

            ArrayList<String> skillContents = configSections.get(toolType);
            skillContents.add("    " + materialName + ":");
            skillContents.add("    " + "    " + "XP_Modifier: 1.0");
            skillContents.add("    " + "    " + "Tier: 1");
            skillContents.add("    " + "    " + "Ability_Enabled: true");
            addRepairableLines(materialName, skillContents);
        }

        return configSections;
    }

    private HashMap<String, ArrayList<String>> getConfigSectionsArmor(String modName, HashMap<String, ArrayList<String>> materialNames) {
        HashMap<String, ArrayList<String>> configSections = new HashMap<>();

        // Go through all the materials and categorise them under an armor type
        for (String materialName : materialNames.get(modName)) {
            String toolType = "UNIDENTIFIED";
            if (materialName.contains("BOOT") || materialName.contains("SHOE")) {
                toolType = "Boots";
            } else if (materialName.contains("CHESTPLATE") || materialName.contains("CHEST")) {
                toolType = "Chestplates";
            } else if (materialName.contains("HELM") || materialName.contains("HAT")) {
                toolType = "Helmets";
            } else if (materialName.contains("LEGGINGS") || materialName.contains("LEGS") || materialName.contains("PANTS")) {
                toolType = "Leggings";
            }

            if (!configSections.containsKey(toolType)) {
                configSections.put(toolType, new ArrayList<>());
            }

            ArrayList<String> skillContents = configSections.get(toolType);
            skillContents.add("    " + materialName + ":");
            addRepairableLines(materialName, skillContents);
        }

        return configSections;
    }

    private void addRepairableLines(String materialName, ArrayList<String> skillContents) {
        skillContents.add("    " + "    " + "Repairable: true");
        skillContents.add("    " + "    " + "Repair_Material: REPAIR_MATERIAL_NAME");
        skillContents.add("    " + "    " + "Repair_Material_Data_Value: 0");
        skillContents.add("    " + "    " + "Repair_Material_Quantity: 9");
        skillContents.add("    " + "    " + "Repair_Material_Pretty_Name: Repair Item Name");
        skillContents.add("    " + "    " + "Repair_MinimumLevel: 0");
        skillContents.add("    " + "    " + "Repair_XpMultiplier: 1.0");

        Material material = Material.matchMaterial(materialName);
        short durability = (material == null) ? (short) 9999 : material.getMaxDurability();
        skillContents.add("    " + "    " + "Durability: " + ((durability > 0) ? durability : (short) 9999));
    }

    private HashMap<String, ArrayList<String>> getConfigSectionsUnknown(String modName, HashMap<String, ArrayList<String>> materialNames) {
        HashMap<String, ArrayList<String>> configSections = new HashMap<>();

        // Go through all the materials and print them
        for (String materialName : materialNames.get(modName)) {
            String configKey = "UNIDENTIFIED";

            if (!configSections.containsKey(configKey)) {
                configSections.put(configKey, new ArrayList<>());
            }

            ArrayList<String> skillContents = configSections.get(configKey);
            skillContents.add("    " + materialName);
        }

        return configSections;
    }
}
