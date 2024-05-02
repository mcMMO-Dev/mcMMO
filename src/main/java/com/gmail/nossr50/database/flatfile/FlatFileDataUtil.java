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
        //TODO: Add UUID recovery? Might not even be worth it.
        return switch (index) {
            case USERNAME_INDEX ->
                    LEGACY_INVALID_OLD_USERNAME; //We'll keep using this value for legacy compatibility reasons (not sure if needed but don't care)
            //Assumption: Used to be for something, no longer used
            //Assumption: Used to be for something, no longer used
            //Assumption: Used to be used for something, no longer used
            //Assumption: Used to be used for something, no longer used
            case 2, 3, 23, 33, LEGACY_LAST_LOGIN, HEALTHBAR -> "IGNORED";
            case SKILLS_MINING, SKILLS_REPAIR, SKILLS_UNARMED, SKILLS_HERBALISM, SKILLS_EXCAVATION, SKILLS_ARCHERY,
                 SKILLS_SWORDS, SKILLS_AXES, SKILLS_WOODCUTTING, SKILLS_ACROBATICS, SKILLS_TAMING, SKILLS_FISHING,
                 SKILLS_ALCHEMY, SKILLS_CROSSBOWS, SKILLS_TRIDENTS, SKILLS_MACES -> String.valueOf(startingLevel);
            case OVERHAUL_LAST_LOGIN -> String.valueOf(-1L);
            case COOLDOWN_BERSERK, COOLDOWN_GIGA_DRILL_BREAKER, COOLDOWN_TREE_FELLER, COOLDOWN_GREEN_TERRA,
                 COOLDOWN_SERRATED_STRIKES, COOLDOWN_SKULL_SPLITTER, COOLDOWN_SUPER_BREAKER, COOLDOWN_BLAST_MINING,
                 COOLDOWN_SUPER_SHOTGUN, COOLDOWN_TRIDENTS, COOLDOWN_ARCHERY, COOLDOWN_MACES, SCOREBOARD_TIPS, COOLDOWN_CHIMAERA_WING,
                 EXP_MINING, EXP_WOODCUTTING, EXP_REPAIR, EXP_UNARMED, EXP_HERBALISM, EXP_EXCAVATION, EXP_ARCHERY,
                 EXP_SWORDS, EXP_AXES, EXP_ACROBATICS, EXP_TAMING, EXP_FISHING, EXP_ALCHEMY, EXP_CROSSBOWS,
                 EXP_TRIDENTS, EXP_MACES -> "0";
            case UUID_INDEX ->
                    throw new IndexOutOfBoundsException(); //TODO: Add UUID recovery? Might not even be worth it.
            default -> throw new IndexOutOfBoundsException();
        };

    }
}
