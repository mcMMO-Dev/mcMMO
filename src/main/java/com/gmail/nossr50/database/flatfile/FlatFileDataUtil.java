package com.gmail.nossr50.database.flatfile;

import com.gmail.nossr50.database.FlatFileDataFlag;
import com.gmail.nossr50.database.FlatFileDatabaseManager;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import static com.gmail.nossr50.database.FlatFileDatabaseManager.*;

public class FlatFileDataUtil {

    public static @Nullable String[] getPreparedSaveDataLine(@NotNull FlatFileDataContainer dataContainer) {
        if(dataContainer.getDataFlags() == null) {
            return dataContainer.getSplitData();
        }

        //Data of this type is not salvageable
        //TODO: Test that we ignore the things we are supposed to ignore
        //TODO: Should we even keep track of the bad data or just not even build data containers for it? Making containers for it is only really useful for debugging.. well I suppose operations are typically async so it shouldn't matter
        if(dataContainer.getDataFlags().contains(FlatFileDataFlag.CORRUPTED_OR_UNRECOGNIZABLE)
                || dataContainer.getDataFlags().contains(FlatFileDataFlag.DUPLICATE_UUID) //For now we will not try to fix any issues with UUIDs
                || dataContainer.getDataFlags().contains(FlatFileDataFlag.BAD_UUID_DATA) //For now we will not try to fix any issues with UUIDs
                || dataContainer.getDataFlags().contains(FlatFileDataFlag.TOO_INCOMPLETE)) {
            return null;
        }

        String[] splitData;

        /*
         * First fix the bad data values if they exist
         */
        if(dataContainer instanceof BadCategorizedFlatFileData badData) {
            splitData = repairBadData(dataContainer.getSplitData(), badData.getBadDataIndexes());
        } else {
            splitData = dataContainer.getSplitData();
        }

        //Make sure we have as many values as we are supposed to
        assert splitData.length == FlatFileDatabaseManager.DATA_ENTRY_COUNT;
        return splitData;
    }

    public static @NotNull String[] repairBadData(@NotNull String[] splitData, boolean[] badDataValues) {
        for(int i = 0; i < FlatFileDatabaseManager.DATA_ENTRY_COUNT; i++) {
            if(badDataValues[i]) {
                //This data value was marked as bad so we zero initialize it
                splitData[i] = getZeroInitialisedData(i, 0);
            }
        }

        return splitData;
    }

    /**
     * @param index "zero" Initialization will depend on what the index is for
     * @return the "zero" initialized data corresponding to the index
     */
    public static @NotNull String getZeroInitialisedData(int index, int startingLevel) throws IndexOutOfBoundsException {
        switch(index) {
            case USERNAME_INDEX:
                return LEGACY_INVALID_OLD_USERNAME; //We'll keep using this value for legacy compatibility reasons (not sure if needed but don't care)
            case 2: //Assumption: Used to be for something, no longer used
            case 3: //Assumption: Used to be for something, no longer used
            case 23: //Assumption: Used to be used for something, no longer used
            case 33: //Assumption: Used to be used for something, no longer used
            case LEGACY_LAST_LOGIN:
            case HEALTHBAR:
                return "IGNORED";
            case SKILLS_MINING:
            case SKILLS_REPAIR:
            case SKILLS_UNARMED:
            case SKILLS_HERBALISM:
            case SKILLS_EXCAVATION:
            case SKILLS_ARCHERY:
            case SKILLS_SWORDS:
            case SKILLS_AXES:
            case SKILLS_WOODCUTTING:
            case SKILLS_ACROBATICS:
            case SKILLS_TAMING:
            case SKILLS_FISHING:
            case SKILLS_ALCHEMY:
            case SKILLS_CROSSBOWS:
            case SKILLS_TRIDENTS:
                return String.valueOf(startingLevel);
            case OVERHAUL_LAST_LOGIN:
                return String.valueOf(-1L);
            case COOLDOWN_BERSERK:
            case COOLDOWN_GIGA_DRILL_BREAKER:
            case COOLDOWN_TREE_FELLER:
            case COOLDOWN_GREEN_TERRA:
            case COOLDOWN_SERRATED_STRIKES:
            case COOLDOWN_SKULL_SPLITTER:
            case COOLDOWN_SUPER_BREAKER:
            case COOLDOWN_BLAST_MINING:
            case COOLDOWN_SUPER_SHOTGUN:
            case COOLDOWN_TRIDENTS:
            case COOLDOWN_ARCHERY:
            case SCOREBOARD_TIPS:
            case COOLDOWN_CHIMAERA_WING:
            case EXP_MINING:
            case EXP_WOODCUTTING:
            case EXP_REPAIR:
            case EXP_UNARMED:
            case EXP_HERBALISM:
            case EXP_EXCAVATION:
            case EXP_ARCHERY:
            case EXP_SWORDS:
            case EXP_AXES:
            case EXP_ACROBATICS:
            case EXP_TAMING:
            case EXP_FISHING:
            case EXP_ALCHEMY:
            case EXP_CROSSBOWS:
            case EXP_TRIDENTS:
                return "0";
            case UUID_INDEX:
                throw new IndexOutOfBoundsException(); //TODO: Add UUID recovery? Might not even be worth it.
        }

        throw new IndexOutOfBoundsException();
    }
}
