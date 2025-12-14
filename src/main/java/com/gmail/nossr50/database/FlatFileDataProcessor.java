package com.gmail.nossr50.database;

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
import static com.gmail.nossr50.database.FlatFileDatabaseManager.DATA_ENTRY_COUNT;
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

import com.gmail.nossr50.database.flatfile.FlatFileDataBuilder;
import com.gmail.nossr50.database.flatfile.FlatFileDataContainer;
import com.gmail.nossr50.database.flatfile.FlatFileDataUtil;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.UUID;
import java.util.logging.Logger;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class FlatFileDataProcessor {
    private final @NotNull List<FlatFileDataContainer> flatFileDataContainers;
    private final @NotNull List<FlatFileDataFlag> flatFileDataFlags;
    private final @NotNull Logger logger;
    private final HashSet<String> names;
    private final HashSet<UUID> uuids;
    private int uniqueProcessingID; //TODO: Not being used, should we use it?
    boolean corruptDataFound;

    public FlatFileDataProcessor(@NotNull Logger logger) {
        this.logger = logger;
        flatFileDataContainers = new ArrayList<>();
        flatFileDataFlags = new ArrayList<>();
        names = new HashSet<>();
        uuids = new HashSet<>();
        uniqueProcessingID = 0;
    }

    public void processData(@NotNull String lineData) {
        assert !lineData.isEmpty();

        //Make sure the data line is "correct"
        if (lineData.charAt(lineData.length() - 1) != ':') {
            // Length checks depend on last rawSplitData being ':'
            // We add it here if it is missing
            lineData = lineData.concat(":");
        }

        //Split the data into an array
        String[] splitDataLine = lineData.split(":");

        FlatFileDataBuilder builder = new FlatFileDataBuilder(splitDataLine, uniqueProcessingID);
        uniqueProcessingID++;
        boolean[] badDataValues = new boolean[DATA_ENTRY_COUNT];
        boolean anyBadData = false;

        //This is the minimum size of the split array needed to be considered proper data
        if (splitDataLine.length < getMinimumSplitDataLength()) {
            //Data is considered junk
            if (!corruptDataFound) {
                logger.severe(
                        "Some corrupt data was found in mcmmo.users and has been repaired, it is possible that some player data has been lost in this process.");
                corruptDataFound = true;
            }

            //Flag as junk (corrupt)
            builder.appendFlag(FlatFileDataFlag.CORRUPTED_OR_UNRECOGNIZABLE);

            //TODO: This block here is probably pointless
            if (splitDataLine.length >= 10
                    //The value here is kind of arbitrary, it shouldn't be too low to avoid false positives, but also we aren't really going to correctly identify when player data has been corrupted or not with 100% accuracy ever
                    && splitDataLine[0] != null && !splitDataLine[0].isEmpty()) {
                if (splitDataLine[0].length() <= 16 && splitDataLine[0].length() >= 3) {
                    logger.severe(
                            "Not enough data found to recover corrupted player data for user: "
                                    + splitDataLine[0]);
                    registerData(builder.appendFlag(FlatFileDataFlag.TOO_INCOMPLETE));
                    return;
                }
            }

            registerData(builder.appendFlag(FlatFileDataFlag.CORRUPTED_OR_UNRECOGNIZABLE));
            return;
        }

        /*
         * Check for duplicate names
         */

        boolean invalidUUID = false;

        String name = splitDataLine[USERNAME_INDEX];
        String strOfUUID = splitDataLine[UUID_INDEX];

        if (name.isEmpty()) {
            reportBadDataLine("No name found for data", "[MISSING NAME]", lineData);
            builder.appendFlag(FlatFileDataFlag.MISSING_NAME);
            anyBadData = true;
            badDataValues[USERNAME_INDEX] = true;
        }

        if (strOfUUID.isEmpty() || strOfUUID.equalsIgnoreCase("NULL")) {
            invalidUUID = true;
            badDataValues[UUID_INDEX] = true;
            reportBadDataLine("Empty/null UUID for user", "Empty/null", lineData);
            builder.appendFlag(FlatFileDataFlag.BAD_UUID_DATA);

            anyBadData = true;
        }

        UUID uuid = null;

        try {
            uuid = UUID.fromString(strOfUUID);
        } catch (IllegalArgumentException e) {
            //UUID does not conform
            invalidUUID = true;
            badDataValues[UUID_INDEX] = true;
            reportBadDataLine("Invalid UUID data found for user", strOfUUID, lineData);
            builder.appendFlag(FlatFileDataFlag.BAD_UUID_DATA);
        }

        //Duplicate UUID is no good, reject them
        if (!invalidUUID && uuid != null && uuids.contains(uuid)) {
            registerData(builder.appendFlag(FlatFileDataFlag.DUPLICATE_UUID));
            return;
        }

        uuids.add(uuid);

        if (names.contains(name)) {
            builder.appendFlag(FlatFileDataFlag.DUPLICATE_NAME);
            anyBadData = true;
            badDataValues[USERNAME_INDEX] = true;
        }

        if (!name.isEmpty()) {
            names.add(name);
        }

        //Make sure the data is up to date schema wise, if it isn't we adjust it to the correct size and flag it for repair
        splitDataLine = isDataSchemaUpToDate(splitDataLine, builder, badDataValues);

        /*
         * After establishing this data has at least an identity we check for bad data
         * Bad Value checks
         */

        //Check each data for bad values
        for (int i = 0; i < DATA_ENTRY_COUNT; i++) {
            if (shouldNotBeEmpty(splitDataLine[i], i)) {

                if (i == OVERHAUL_LAST_LOGIN) {
                    builder.appendFlag(FlatFileDataFlag.LAST_LOGIN_SCHEMA_UPGRADE);
                }

                badDataValues[i] = true;
                anyBadData = true;
                continue;
            }

            boolean isCorrectType = isOfExpectedType(splitDataLine[i], getExpectedValueType(i));

            if (!isCorrectType) {
                anyBadData = true;
                badDataValues[i] = true;
            }
        }

        if (anyBadData) {
            builder.appendFlag(FlatFileDataFlag.BAD_VALUES);
            builder.appendBadDataValues(badDataValues);
        }

        registerData(builder);
    }

    public @NotNull String[] isDataSchemaUpToDate(@NotNull String[] splitDataLine,
            @NotNull FlatFileDataBuilder builder, boolean[] badDataValues) {
        assert splitDataLine.length <= DATA_ENTRY_COUNT; //should NEVER be higher

        if (splitDataLine.length < DATA_ENTRY_COUNT) {
            int oldLength = splitDataLine.length;
            splitDataLine = Arrays.copyOf(splitDataLine, DATA_ENTRY_COUNT);
            int newLength = splitDataLine.length;

            //TODO: Test this
            for (int i = oldLength; i < (newLength - 1); i++) {
                badDataValues[i] = true;
            }

            builder.appendFlag(FlatFileDataFlag.INCOMPLETE);
            builder.setSplitStringData(splitDataLine);
        }
        return splitDataLine;
    }


    public boolean shouldNotBeEmpty(@Nullable String data, int index) {
        if (getExpectedValueType(index) == ExpectedType.IGNORED) {
            return false;
        } else {
            return data == null || data.isEmpty();
        }
    }

    public boolean isOfExpectedType(@NotNull String data, @NotNull ExpectedType expectedType) {
        switch (expectedType) {
            case STRING:
                return true;
            case INTEGER:
                try {
                    Integer.valueOf(data);
                    return true;
                } catch (Exception e) {
                    return false;
                }
            case BOOLEAN:
                return data.equalsIgnoreCase("true") || data.equalsIgnoreCase("false");
            case FLOAT:
                try {
                    Float.valueOf(data);
                    return true;
                } catch (NumberFormatException e) {
                    return false;
                }
            case DOUBLE:
                try {
                    Double.valueOf(data);
                    return true;
                } catch (NumberFormatException e) {
                    return false;
                }
            case UUID:
                try {
                    UUID.fromString(data);
                    return true;
                } catch (IllegalArgumentException e) {
                    return false;
                }
            case OUT_OF_RANGE:
                throw new ArrayIndexOutOfBoundsException(
                        "Value matched type OUT_OF_RANGE, this should never happen.");
            case IGNORED:
            default:
                return true;
        }

    }

    private void reportBadDataLine(String warning, String context, String dataLine) {
        logger.warning("FlatFileDatabaseBuilder Warning: " + warning + " - " + context);
        logger.warning("FlatFileDatabaseBuilder: (Line Data) - " + dataLine);
        logger.warning("mcMMO will repair this data if automatically (if it is possible).");
    }

    private int getMinimumSplitDataLength() {
        return UUID_INDEX + 1;
    }

    private void registerData(@NotNull FlatFileDataBuilder builder) {
        FlatFileDataContainer flatFileDataContainer = builder.build();
        flatFileDataContainers.add(flatFileDataContainer);

        if (flatFileDataContainer.getDataFlags() != null) {
            flatFileDataFlags.addAll(flatFileDataContainer.getDataFlags());
        }
    }

    public static @NotNull ExpectedType getExpectedValueType(int dataIndex)
            throws IndexOutOfBoundsException {
        return switch (dataIndex) {
            case USERNAME_INDEX ->
                    ExpectedType.STRING;
            //Assumption: Used to be used for something, no longer used
            case 2, 3, 23, 33, HEALTHBAR, LEGACY_LAST_LOGIN -> ExpectedType.IGNORED;
            case SKILLS_MINING, SKILLS_REPAIR, SKILLS_UNARMED, SKILLS_HERBALISM, SKILLS_EXCAVATION,
                 SKILLS_ARCHERY,
                 SKILLS_SWORDS, SKILLS_AXES, SKILLS_WOODCUTTING, SKILLS_ACROBATICS, SKILLS_TAMING,
                 SKILLS_FISHING,
                 SKILLS_ALCHEMY, SKILLS_CROSSBOWS, SKILLS_TRIDENTS, SKILLS_MACES, SKILLS_SPEARS,
                 COOLDOWN_BERSERK,
                 COOLDOWN_GIGA_DRILL_BREAKER, COOLDOWN_TREE_FELLER, COOLDOWN_GREEN_TERRA,
                 COOLDOWN_SERRATED_STRIKES,
                 COOLDOWN_SKULL_SPLITTER, COOLDOWN_SUPER_BREAKER, COOLDOWN_BLAST_MINING,
                 SCOREBOARD_TIPS,
                 COOLDOWN_CHIMAERA_WING, COOLDOWN_SUPER_SHOTGUN, COOLDOWN_TRIDENTS,
                 COOLDOWN_ARCHERY, COOLDOWN_MACES, COOLDOWN_SPEARS -> ExpectedType.INTEGER;
            case EXP_MINING, EXP_WOODCUTTING, EXP_REPAIR, EXP_UNARMED, EXP_HERBALISM,
                 EXP_EXCAVATION, EXP_ARCHERY,
                 EXP_SWORDS, EXP_AXES, EXP_ACROBATICS, EXP_TAMING, EXP_FISHING, EXP_ALCHEMY,
                 EXP_CROSSBOWS,
                 EXP_TRIDENTS, EXP_MACES, EXP_SPEARS -> ExpectedType.FLOAT;
            case UUID_INDEX -> ExpectedType.UUID;
            case OVERHAUL_LAST_LOGIN -> ExpectedType.LONG;
            default -> throw new IndexOutOfBoundsException();
        };
    }

    public @NotNull List<FlatFileDataContainer> getFlatFileDataContainers() {
        return flatFileDataContainers;
    }

    public @NotNull List<FlatFileDataFlag> getFlatFileDataFlags() {
        return flatFileDataFlags;
    }

    public int getDataFlagCount() {
        return flatFileDataFlags.size();
    }

    public @NotNull StringBuilder processDataForSave() {
        StringBuilder stringBuilder = new StringBuilder();

        //Fix our data if needed and prepare it to be saved

        for (FlatFileDataContainer dataContainer : flatFileDataContainers) {
            String[] splitData = FlatFileDataUtil.getPreparedSaveDataLine(dataContainer);

            if (splitData == null) {
                continue;
            }

            //We add a trailing : as it is needed for some reason (is it?)
            //TODO: Is the trailing ":" actually necessary?
            String fromSplit = org.apache.commons.lang3.StringUtils.join(splitData, ":") + ":";
            stringBuilder.append(fromSplit).append("\r\n");
        }

        return stringBuilder;
    }

}
