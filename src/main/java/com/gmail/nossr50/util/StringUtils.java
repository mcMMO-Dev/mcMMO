package com.gmail.nossr50.util;

import org.bukkit.CropState;
import org.bukkit.GrassSpecies;
import org.bukkit.Material;
import org.bukkit.NetherWartsState;
import org.bukkit.TreeSpecies;
import org.bukkit.entity.EntityType;
import org.bukkit.material.CocoaPlant;
import org.bukkit.material.Crops;
import org.bukkit.material.LongGrass;
import org.bukkit.material.MaterialData;
import org.bukkit.material.NetherWarts;
import org.bukkit.material.Sapling;
import org.bukkit.material.Tree;
import org.bukkit.material.CocoaPlant.CocoaPlantSize;

import com.gmail.nossr50.datatypes.party.PartyFeature;
import com.gmail.nossr50.datatypes.skills.AbilityType;
import com.gmail.nossr50.datatypes.skills.SecondaryAbility;

public class StringUtils {

    /**
     * Gets a capitalized version of the target string.
     *
     * @param target
     *            String to capitalize
     * @return the capitalized string
     */
    public static String getCapitalized(String target) {
        return target.substring(0, 1).toUpperCase() + target.substring(1).toLowerCase();
    }

    public static String getPrettyItemString(Material material) {
        return createPrettyEnumString(material.toString());
    }

    public static String getPrettyEntityTypeString(EntityType entity) {
        return createPrettyEnumString(entity.toString());
    }

    public static String getPrettyAbilityString(AbilityType ability) {
        return createPrettyEnumString(ability.toString());
    }

    public static String getPrettyTreeSpeciesString(TreeSpecies species) {
        return createPrettyEnumString(species.toString());
    }
    
    public static String getWildcardConfigMaterialDataString(MaterialData data) {
        return StringUtils.getPrettyItemString(data.getItemType()).replace(" ", "_") + "|*";
    }

    public static String getFriendlyConfigMaterialDataString(MaterialData data) {
        switch (data.getItemType()) {
            case LOG :
            case LOG_2 : {
                TreeSpecies species = TreeSpecies.GENERIC;
                if (data instanceof Tree) {
                    Tree tree = (Tree) data;
                    species = tree.getSpecies();
                }
                return createPrettyEnumString(species.name()).replace(" ", "_");
            }
            case LONG_GRASS : {
                LongGrass grass = (LongGrass) data;
                GrassSpecies species = grass.getSpecies();
                switch (species) {
                    case DEAD :
                        return "Dead_Bush";

                    case FERN_LIKE :
                        return "Small_Fern";

                    case NORMAL :
                        return "Small_Grass";
                }
                break;
            }
            case RED_ROSE : {
                switch (data.getData()) {
                    case 0x0 :
                        return "Poppy";

                    case 0x1 :
                        return "Blue_Orchid";

                    case 0x2 :
                        return "Allium";

                    case 0x3 :
                        return "Azure_Bluet";

                    case 0x4 :
                        return "Red_Tulip";

                    case 0x5 :
                        return "Orange_Tulip";

                    case 0x6 :
                        return "White_Tulip";

                    case 0x7 :
                        return "Pink_Tulip";

                    case 0x8 :
                        return "Oxeye_Daisy";

                    default :
                        return getExplicitConfigMaterialDataString(data);
                }
            }
            case DOUBLE_PLANT : {
                switch (data.getData()) {
                    case 0x0 :
                        return "Sunflower";

                    case 0x1 :
                        return "Lilac";

                    case 0x2 :
                        return "Tall_Grass";

                    case 0x3 :
                        return "Tall_Fern";

                    case 0x4 :
                        return "Rose_Bush";

                    case 0x5 :
                        return "Peony";

                    default :
                        return getExplicitConfigMaterialDataString(data);
                }
            }
            case RAW_FISH : {
                switch (data.getData()) {
                    case 0x0 :
                        return "Raw_Fish";

                    case 0x1 :
                        return "Raw_Salmon";

                    case 0x2 :
                        return "Clownfish";

                    case 0x3 :
                        return "Pufferfish";

                    default :
                        return getExplicitConfigMaterialDataString(data);
                }
            }
            case COOKED_FISH : {
                switch (data.getData()) {
                    case 0x0 :
                        return "Cooked_Fish";

                    case 0x1 :
                        return "Cooked_Salmon";

                    default :
                        return getExplicitConfigMaterialDataString(data);
                }
            }
            case DIRT : {
                switch (data.getData()) {
                    case 0x0 :
                        return "Dirt";

                    case 0x1 :
                        return "Coarse_Dirt";

                    case 0x2 :
                        return "Podzol";

                    default :
                        return getExplicitConfigMaterialDataString(data);
                }
            }
            case SAND : {
                switch (data.getData()) {
                    case 0x0 :
                        return "Sand";

                    case 0x1 :
                        return "Red_Sand";

                    default :
                        return getExplicitConfigMaterialDataString(data);
                }
            }
            case QUARTZ_BLOCK : {
                switch (data.getData()) {
                    case 0x0 :
                        return "Quartz_Block";

                    case 0x1 :
                        return "Chiseled_Quartz_Block";

                    case 0x2 :
                    case 0x3 :
                    case 0x4 :
                        return "Quartz_Pillar";

                    default :
                        return getExplicitConfigMaterialDataString(data);
                }
            }
            case SPONGE : {
                switch (data.getData()) {
                    case 0x0 :
                        return "Sponge";

                    case 0x1 :
                        return "Wet_Sponge";

                    default :
                        return getExplicitConfigMaterialDataString(data);
                }
            }
            case PRISMARINE : {
                switch (data.getData()) {
                    case 0x0 :
                        return "Prismarine";

                    case 0x1 :
                        return "Prismarine_Brick";

                    case 0x2 :
                        return "Dark_Prismarine";

                    default :
                        return getExplicitConfigMaterialDataString(data);
                }
            }
            case STONE : {
                switch (data.getData()) {
                    case 0x0 :
                        return "Stone";

                    case 0x1 :
                        return "Granite";

                    case 0x2 :
                        return "Polished_Granite";

                    case 0x3 :
                        return "Diorite";

                    case 0x4 :
                        return "Polished_Diorite";

                    case 0x5 :
                        return "Andesite";

                    case 0x6 :
                        return "Polished_Andesite";

                    default :
                        return getExplicitConfigMaterialDataString(data);
                }
            }
            case WOOD : {
                switch (data.getData()) {
                    case 0x0 :
                        return "Oak_Planks";

                    case 0x1 :
                        return "Spruce_Planks";

                    case 0x2 :
                        return "Birch_Planks";

                    case 0x3 :
                        return "Jungle_Planks";

                    case 0x4 :
                        return "Acacia_Planks";

                    case 0x5 :
                        return "Dark_Oak_Planks";

                    default :
                        return getExplicitConfigMaterialDataString(data);
                }
            }
            case GLOWING_REDSTONE_ORE :
                return getPrettyItemString(Material.REDSTONE_ORE).replace(" ", "_");

            case BEETROOT_BLOCK :
            case CARROT :
            case POTATO :
            case CROPS : {
                if (((Crops) data).getState() == CropState.RIPE) {
                    return getPrettyItemString(data.getItemType()).replace(" ", "_") + "_Ripe";
                }
                return getPrettyItemString(data.getItemType()).replace(" ", "_") + "_Ungrown";
            }
            case NETHER_WARTS : {
                if (((NetherWarts) data).getState() == NetherWartsState.RIPE) {
                    return getPrettyItemString(data.getItemType()).replace(" ", "_") + "_Ripe";
                }
                return getPrettyItemString(data.getItemType()).replace(" ", "_") + "_Ungrown";
            }
            case COCOA : {
                if (((CocoaPlant) data).getSize() == CocoaPlantSize.LARGE) {
                    return getPrettyItemString(data.getItemType()).replace(" ", "_") + "_Ripe";
                }
                return getPrettyItemString(data.getItemType()).replace(" ", "_") + "_Ungrown";
            }
            case SAPLING:
            {
                TreeSpecies species = TreeSpecies.GENERIC;
                if (data instanceof Sapling) {
                    Sapling sapling = (Sapling) data;
                    species = sapling.getSpecies();
                }
                return createPrettyEnumString(species.name()).replace(" ", "_") + "_Sapling";
            }
            case SMOOTH_BRICK :
            case WOOL :
            case INK_SACK :
            case STAINED_CLAY :
            case STAINED_GLASS :
            case FLOWER_POT :
            case MONSTER_EGGS :
                return getExplicitConfigMaterialDataString(data);
            default :
                break;
        }
        return getPrettyItemString(data.getItemType()).replace(" ", "_");
    }

    public static String getExplicitConfigMaterialDataString(MaterialData data) {
        return StringUtils.getPrettyItemString(data.getItemType()).replace(" ", "_") + "|" + data.getData();
    }

    public static String getPrettySecondaryAbilityString(SecondaryAbility secondaryAbility) {
        switch (secondaryAbility) {
            case HERBALISM_DOUBLE_DROPS :
            case MINING_DOUBLE_DROPS :
            case WOODCUTTING_DOUBLE_DROPS :
                return "Double Drops";
            case FISHING_TREASURE_HUNTER :
            case EXCAVATION_TREASURE_HUNTER :
                return "Treasure Hunter";
            case GREEN_THUMB_BLOCK :
            case GREEN_THUMB_PLANT :
                return "Green Thumb";
            default :
                return createPrettyEnumString(secondaryAbility.toString());
        }
    }

    public static String getPrettyPartyFeatureString(PartyFeature partyFeature) {
        return createPrettyEnumString(partyFeature.toString());
    }

    private static String createPrettyEnumString(String baseString) {
        String[] substrings = baseString.split("_");
        String prettyString = "";
        int size = 1;

        for (String string : substrings) {
            prettyString = prettyString.concat(getCapitalized(string));

            if (size < substrings.length) {
                prettyString = prettyString.concat(" ");
            }

            size++;
        }

        return prettyString;
    }

    /**
     * Determine if a string represents an Integer
     *
     * @param string
     *            String to check
     * @return true if the string is an Integer, false otherwise
     */
    public static boolean isInt(String string) {
        try {
            Integer.parseInt(string);
            return true;
        } catch (NumberFormatException nFE) {
            return false;
        }
    }

    /**
     * Determine if a string represents a Double
     *
     * @param string
     *            String to check
     * @return true if the string is a Double, false otherwise
     */
    public static boolean isDouble(String string) {
        try {
            Double.parseDouble(string);
            return true;
        } catch (NumberFormatException nFE) {
            return false;
        }
    }

}
