package com.gmail.nossr50.database.flatfile;

import static com.gmail.nossr50.database.FlatFileDatabaseManager.COOLDOWN_ARCHERY;
import static com.gmail.nossr50.database.FlatFileDatabaseManager.COOLDOWN_BERSERK;
import static com.gmail.nossr50.database.FlatFileDatabaseManager.COOLDOWN_BLAST_MINING;
import static com.gmail.nossr50.database.FlatFileDatabaseManager.COOLDOWN_CHIMAERA_WING;
import static com.gmail.nossr50.database.FlatFileDatabaseManager.COOLDOWN_GIGA_DRILL_BREAKER;
import static com.gmail.nossr50.database.FlatFileDatabaseManager.COOLDOWN_GREEN_TERRA;
import static com.gmail.nossr50.database.FlatFileDatabaseManager.COOLDOWN_MACES;
import static com.gmail.nossr50.database.FlatFileDatabaseManager.COOLDOWN_SERRATED_STRIKES;
import static com.gmail.nossr50.database.FlatFileDatabaseManager.COOLDOWN_SKULL_SPLITTER;
import static com.gmail.nossr50.database.FlatFileDatabaseManager.COOLDOWN_SPEARS;
import static com.gmail.nossr50.database.FlatFileDatabaseManager.COOLDOWN_SUPER_BREAKER;
import static com.gmail.nossr50.database.FlatFileDatabaseManager.COOLDOWN_SUPER_SHOTGUN;
import static com.gmail.nossr50.database.FlatFileDatabaseManager.COOLDOWN_TREE_FELLER;
import static com.gmail.nossr50.database.FlatFileDatabaseManager.COOLDOWN_TRIDENTS;
import static com.gmail.nossr50.database.FlatFileDatabaseManager.EXP_ACROBATICS;
import static com.gmail.nossr50.database.FlatFileDatabaseManager.EXP_ALCHEMY;
import static com.gmail.nossr50.database.FlatFileDatabaseManager.EXP_ARCHERY;
import static com.gmail.nossr50.database.FlatFileDatabaseManager.EXP_AXES;
import static com.gmail.nossr50.database.FlatFileDatabaseManager.EXP_CROSSBOWS;
import static com.gmail.nossr50.database.FlatFileDatabaseManager.EXP_EXCAVATION;
import static com.gmail.nossr50.database.FlatFileDatabaseManager.EXP_FISHING;
import static com.gmail.nossr50.database.FlatFileDatabaseManager.EXP_HERBALISM;
import static com.gmail.nossr50.database.FlatFileDatabaseManager.EXP_MACES;
import static com.gmail.nossr50.database.FlatFileDatabaseManager.EXP_MINING;
import static com.gmail.nossr50.database.FlatFileDatabaseManager.EXP_REPAIR;
import static com.gmail.nossr50.database.FlatFileDatabaseManager.EXP_SPEARS;
import static com.gmail.nossr50.database.FlatFileDatabaseManager.EXP_SWORDS;
import static com.gmail.nossr50.database.FlatFileDatabaseManager.EXP_TAMING;
import static com.gmail.nossr50.database.FlatFileDatabaseManager.EXP_TRIDENTS;
import static com.gmail.nossr50.database.FlatFileDatabaseManager.EXP_UNARMED;
import static com.gmail.nossr50.database.FlatFileDatabaseManager.EXP_WOODCUTTING;
import static com.gmail.nossr50.database.FlatFileDatabaseManager.HEALTHBAR;
import static com.gmail.nossr50.database.FlatFileDatabaseManager.LEGACY_INVALID_OLD_USERNAME;
import static com.gmail.nossr50.database.FlatFileDatabaseManager.LEGACY_LAST_LOGIN;
import static com.gmail.nossr50.database.FlatFileDatabaseManager.OVERHAUL_LAST_LOGIN;
import static com.gmail.nossr50.database.FlatFileDatabaseManager.SCOREBOARD_TIPS;
import static com.gmail.nossr50.database.FlatFileDatabaseManager.SKILLS_ACROBATICS;
import static com.gmail.nossr50.database.FlatFileDatabaseManager.SKILLS_ALCHEMY;
import static com.gmail.nossr50.database.FlatFileDatabaseManager.SKILLS_ARCHERY;
import static com.gmail.nossr50.database.FlatFileDatabaseManager.SKILLS_AXES;
import static com.gmail.nossr50.database.FlatFileDatabaseManager.SKILLS_CROSSBOWS;
import static com.gmail.nossr50.database.FlatFileDatabaseManager.SKILLS_EXCAVATION;
import static com.gmail.nossr50.database.FlatFileDatabaseManager.SKILLS_FISHING;
import static com.gmail.nossr50.database.FlatFileDatabaseManager.SKILLS_HERBALISM;
import static com.gmail.nossr50.database.FlatFileDatabaseManager.SKILLS_MACES;
import static com.gmail.nossr50.database.FlatFileDatabaseManager.SKILLS_MINING;
import static com.gmail.nossr50.database.FlatFileDatabaseManager.SKILLS_REPAIR;
import static com.gmail.nossr50.database.FlatFileDatabaseManager.SKILLS_SPEARS;
import static com.gmail.nossr50.database.FlatFileDatabaseManager.SKILLS_SWORDS;
import static com.gmail.nossr50.database.FlatFileDatabaseManager.SKILLS_TAMING;
import static com.gmail.nossr50.database.FlatFileDatabaseManager.SKILLS_TRIDENTS;
import static com.gmail.nossr50.database.FlatFileDatabaseManager.SKILLS_UNARMED;
import static com.gmail.nossr50.database.FlatFileDatabaseManager.SKILLS_WOODCUTTING;
import static com.gmail.nossr50.database.FlatFileDatabaseManager.USERNAME_INDEX;
import static com.gmail.nossr50.database.FlatFileDatabaseManager.UUID_INDEX;

import com.gmail.nossr50.database.FlatFileDataFlag;
import com.gmail.nossr50.database.FlatFileDatabaseManager;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class FlatFileDataUtil {

    public static @Nullable String[] getPreparedSaveDataLine(
            @NotNull FlatFileDataContainer dataContainer) {
        if (dataContainer.getDataFlags() == null) {
            return dataContainer.getSplitData();
        }

        //Data of this type is not salvageable
        //TODO: Test that we ignore the things we are supposed to ignore
        //TODO: Should we even keep track of the bad data or just not even build data containers for it? Making containers for it is only really useful for debugging.. well I suppose operations are typically async so it shouldn't matter
        if (dataContainer.getDataFlags().contains(FlatFileDataFlag.CORRUPTED_OR_UNRECOGNIZABLE)
                || dataContainer.getDataFlags().contains(FlatFileDataFlag.DUPLICATE_UUID)
                //For now we will not try to fix any issues with UUIDs
                || dataContainer.getDataFlags().contains(FlatFileDataFlag.BAD_UUID_DATA)
                //For now we will not try to fix any issues with UUIDs
                || dataContainer.getDataFlags().contains(FlatFileDataFlag.TOO_INCOMPLETE)) {
            return null;
        }

        String[] splitData;

        /*
         * First fix the bad data values if they exist
         */
        if (dataContainer instanceof BadCategorizedFlatFileData badData) {
            splitData = repairBadData(dataContainer.getSplitData(), badData.getBadDataIndexes());
        } else {
            splitData = dataContainer.getSplitData();
        }

        //Make sure we have as many values as we are supposed to
        assert splitData.length == FlatFileDatabaseManager.DATA_ENTRY_COUNT;
        return splitData;
    }

    public static @NotNull String[] repairBadData(@NotNull String[] splitData,
            boolean[] badDataValues) {
        for (int i = 0; i < FlatFileDatabaseManager.DATA_ENTRY_COUNT; i++) {
            if (badDataValues[i]) {
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
    public static @NotNull String getZeroInitialisedData(int index, int startingLevel)
            throws IndexOutOfBoundsException {
        //TODO: Add UUID recovery? Might not even be worth it.
        return switch (index) {
            //We'll keep using this value for legacy compatibility reasons (not sure if needed but don't care)
            case USERNAME_INDEX ->
                    LEGACY_INVALID_OLD_USERNAME;
            //Assumption: Used to be used for something, no longer used
            case 2, 3, 23, 33, LEGACY_LAST_LOGIN, HEALTHBAR -> "IGNORED";
            case SKILLS_MINING, SKILLS_REPAIR, SKILLS_UNARMED, SKILLS_HERBALISM, SKILLS_EXCAVATION,
                 SKILLS_ARCHERY,
                 SKILLS_SWORDS, SKILLS_AXES, SKILLS_WOODCUTTING, SKILLS_ACROBATICS, SKILLS_TAMING,
                 SKILLS_FISHING,
                 SKILLS_ALCHEMY, SKILLS_CROSSBOWS, SKILLS_TRIDENTS, SKILLS_MACES, SKILLS_SPEARS ->
                    String.valueOf(startingLevel);
            case OVERHAUL_LAST_LOGIN -> String.valueOf(-1L);
            case COOLDOWN_BERSERK, COOLDOWN_GIGA_DRILL_BREAKER, COOLDOWN_TREE_FELLER,
                 COOLDOWN_GREEN_TERRA,
                 COOLDOWN_SERRATED_STRIKES, COOLDOWN_SKULL_SPLITTER, COOLDOWN_SUPER_BREAKER,
                 COOLDOWN_BLAST_MINING,
                 COOLDOWN_SUPER_SHOTGUN, COOLDOWN_TRIDENTS, COOLDOWN_ARCHERY, COOLDOWN_MACES,
                 COOLDOWN_SPEARS, SCOREBOARD_TIPS, COOLDOWN_CHIMAERA_WING,
                 EXP_MINING, EXP_WOODCUTTING, EXP_REPAIR, EXP_UNARMED, EXP_HERBALISM,
                 EXP_EXCAVATION, EXP_ARCHERY,
                 EXP_SWORDS, EXP_AXES, EXP_ACROBATICS, EXP_TAMING, EXP_FISHING, EXP_ALCHEMY,
                 EXP_CROSSBOWS,
                 EXP_TRIDENTS, EXP_MACES, EXP_SPEARS -> "0";
            case UUID_INDEX ->
                    throw new IndexOutOfBoundsException(); //TODO: Add UUID recovery? Might not even be worth it.
            default -> throw new IndexOutOfBoundsException();
        };

    }
}
