package com.gmail.nossr50.util;

import java.util.List;

import org.bukkit.Material;
import org.bukkit.TreeSpecies;
import org.bukkit.entity.EntityType;

import com.gmail.nossr50.datatypes.party.PartyFeature;
import com.gmail.nossr50.datatypes.skills.AbilityType;
import com.gmail.nossr50.datatypes.skills.SecondaryAbility;

public class StringUtils {
    /**
     * Gets a capitalized version of the target string.
     *
     * @param target String to capitalize
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

    public static String getPrettySecondaryAbilityString(SecondaryAbility secondaryAbility) {
//        switch (secondaryAbility) {
//            case herbalismDoubleDrops:
//            case miningDoubleDrops:
//            case woodcuttingDoubleDrops:
//                return "Double Drops";
//            case fishingTreasureHunter:
//            case excavationTreasureHunter:
//                return "Treasure Hunter";
//            case greenThumbBlock:
//            case greenThumbPlant:
//                return "Green Thumb";
//            default:
                return createPrettyEnumString(secondaryAbility.toString());
//        }
    }

    public static String getPrettyPartyFeatureString(PartyFeature partyFeature) {
        return createPrettyEnumString(partyFeature.toString());
    }

    private static String createPrettyEnumString(String baseString) {
        return createPrettyStringWithSpacer(baseString, " ");
    }
    
    public static String createPrettyStringWithSpacer(String baseString, String spacer) {
        String[] substrings = baseString.split("_");
        String prettyString = "";
        int size = 1;

        for (String string : substrings) {
            prettyString = prettyString.concat(getCapitalized(string));

            if (size < substrings.length) {
                prettyString = prettyString.concat(spacer);
            }

            size++;
        }

        return prettyString;
    }
    
    public static String createStringForPermission(String baseString) {
    	return baseString.replace("_", "").toLowerCase();
    }

    /**
     * Determine if a string represents an Integer
     *
     * @param string String to check
     * @return true if the string is an Integer, false otherwise
     */
    public static boolean isInt(String string) {
        try {
            Integer.parseInt(string);
            return true;
        }
        catch (NumberFormatException nFE) {
            return false;
        }
    }

    /**
     * Determine if a string represents a Double
     *
     * @param string String to check
     * @return true if the string is a Double, false otherwise
     */
    public static boolean isDouble(String string) {
        try {
            Double.parseDouble(string);
            return true;
        }
        catch (NumberFormatException nFE) {
            return false;
        }
    }

	public static String createStringFromListWithNoPrefixBeforeFirst(List<String> list, String prefix, String suffix) {
		String toRet = "";
		toRet = org.apache.commons.lang.StringUtils.join(list.toArray(), suffix + prefix) + suffix;
		/*Iterator<String> itr = list.iterator();
		if(itr.hasNext()) {
			toRet += itr.next() + suffix;
		}
		while(itr.hasNext()) {
			toRet += prefix + itr.next() + suffix;
		}*/
		return toRet;
	}

	public static String createStringFromList(List<String> list, String prefix, String suffix) {
		String toRet = "";
		toRet = prefix + createStringFromListWithNoPrefixBeforeFirst(list, prefix, suffix);
		/*Iterator<String> itr = list.iterator();
		while(itr.hasNext()) {
			toRet += prefix + itr.next() + suffix;
		}*/
		return toRet;
	}

}
