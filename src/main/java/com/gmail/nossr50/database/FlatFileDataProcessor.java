package com.gmail.nossr50.database;

import com.gmail.nossr50.database.flatfile.FlatFileDataBuilder;
import com.gmail.nossr50.database.flatfile.FlatFileDataContainer;
import com.gmail.nossr50.database.flatfile.FlatFileDataUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.logging.Logger;

import static com.gmail.nossr50.database.FlatFileDatabaseManager.*;

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
        if(lineData.charAt(lineData.length() - 1) != ':') {
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
        if(splitDataLine.length < getMinimumSplitDataLength()) {
            //Data is considered junk
            if(!corruptDataFound) {
                logger.severe("Some corrupt data was found in mcmmo.users and has been repaired, it is possible that some player data has been lost in this process.");
                corruptDataFound = true;
            }

            //Flag as junk (corrupt)
            builder.appendFlag(FlatFileDataFlag.CORRUPTED_OR_UNRECOGNIZABLE);

            //TODO: This block here is probably pointless
            if(splitDataLine.length >= 10 //The value here is kind of arbitrary, it shouldn't be too low to avoid false positives, but also we aren't really going to correctly identify when player data has been corrupted or not with 100% accuracy ever
                    && splitDataLine[0] != null && !splitDataLine[0].isEmpty()) {
                if(splitDataLine[0].length() <= 16 && splitDataLine[0].length() >= 3) {
                    logger.severe("Not enough data found to recover corrupted player data for user: "+splitDataLine[0]);
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

        if(name.isEmpty()) {
            reportBadDataLine("No name found for data", "[MISSING NAME]", lineData);
            builder.appendFlag(FlatFileDataFlag.MISSING_NAME);
            anyBadData = true;
            badDataValues[USERNAME_INDEX] = true;
        }

        if(strOfUUID.isEmpty() || strOfUUID.equalsIgnoreCase("NULL")) {
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
        if(!invalidUUID && uuid != null && uuids.contains(uuid)) {
            registerData(builder.appendFlag(FlatFileDataFlag.DUPLICATE_UUID));
            return;
        }

        uuids.add(uuid);

        if(names.contains(name)) {
            builder.appendFlag(FlatFileDataFlag.DUPLICATE_NAME);
            anyBadData = true;
            badDataValues[USERNAME_INDEX] = true;
        }

        if(!name.isEmpty())
            names.add(name);

        //Make sure the data is up to date schema wise, if it isn't we adjust it to the correct size and flag it for repair
        splitDataLine = isDataSchemaUpToDate(splitDataLine, builder, badDataValues);

        /*
         * After establishing this data has at least an identity we check for bad data
         * Bad Value checks
         */

        //Check each data for bad values
        for(int i = 0; i < DATA_ENTRY_COUNT; i++) {
            if(shouldNotBeEmpty(splitDataLine[i], i)) {

                if(i == OVERHAUL_LAST_LOGIN) {
                    builder.appendFlag(FlatFileDataFlag.LAST_LOGIN_SCHEMA_UPGRADE);
                }

                badDataValues[i] = true;
                anyBadData = true;
                continue;
            }

            boolean isCorrectType = isOfExpectedType(splitDataLine[i], getExpectedValueType(i));

            if(!isCorrectType) {
                anyBadData = true;
                badDataValues[i] = true;
            }
        }

        if(anyBadData) {
            builder.appendFlag(FlatFileDataFlag.BAD_VALUES);
            builder.appendBadDataValues(badDataValues);
        }

        registerData(builder);
    }

    public @NotNull String[] isDataSchemaUpToDate(@NotNull String[] splitDataLine, @NotNull FlatFileDataBuilder builder, boolean[] badDataValues) {
        assert splitDataLine.length <= DATA_ENTRY_COUNT; //should NEVER be higher

        if(splitDataLine.length < DATA_ENTRY_COUNT) {
            int oldLength = splitDataLine.length;
            splitDataLine = Arrays.copyOf(splitDataLine, DATA_ENTRY_COUNT);
            int newLength = splitDataLine.length;

            //TODO: Test this
            for(int i = oldLength; i < (newLength - 1); i++){
                badDataValues[i] = true;
            }

            builder.appendFlag(FlatFileDataFlag.INCOMPLETE);
            builder.setSplitStringData(splitDataLine);
        }
        return splitDataLine;
    }


    public boolean shouldNotBeEmpty(@Nullable String data, int index) {
        if(getExpectedValueType(index) == ExpectedType.IGNORED) {
            return false;
        } else {
            return data == null || data.isEmpty();
        }
    }

    public boolean isOfExpectedType(@NotNull String data, @NotNull ExpectedType expectedType) {
        switch(expectedType) {
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
                throw new ArrayIndexOutOfBoundsException("Value matched type OUT_OF_RANGE, this should never happen.");
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

        if(flatFileDataContainer.getDataFlags() != null)
            flatFileDataFlags.addAll(flatFileDataContainer.getDataFlags());
    }

    public static @NotNull ExpectedType getExpectedValueType(int dataIndex) throws IndexOutOfBoundsException {
        switch(dataIndex) {
            case USERNAME_INDEX:
                return ExpectedType.STRING;
            case 2: //Assumption: Used to be for something, no longer used
            case 3: //Assumption: Used to be for something, no longer used
            case 23: //Assumption: Used to be used for something, no longer used
            case 33: //Assumption: Used to be used for something, no longer used
            case HEALTHBAR:
            case LEGACY_LAST_LOGIN:
                return ExpectedType.IGNORED;
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
            case COOLDOWN_BERSERK:
            case COOLDOWN_GIGA_DRILL_BREAKER:
            case COOLDOWN_TREE_FELLER:
            case COOLDOWN_GREEN_TERRA:
            case COOLDOWN_SERRATED_STRIKES:
            case COOLDOWN_SKULL_SPLITTER:
            case COOLDOWN_SUPER_BREAKER:
            case COOLDOWN_BLAST_MINING:
            case SCOREBOARD_TIPS:
            case COOLDOWN_CHIMAERA_WING:
            case COOLDOWN_SUPER_SHOTGUN:
            case COOLDOWN_TRIDENTS:
            case COOLDOWN_ARCHERY:
                return ExpectedType.INTEGER;
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
                return ExpectedType.FLOAT;
            case UUID_INDEX:
                return ExpectedType.UUID;
            case OVERHAUL_LAST_LOGIN:
                return ExpectedType.LONG;
        }

        throw new IndexOutOfBoundsException();
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

        for(FlatFileDataContainer dataContainer : flatFileDataContainers) {
            String[] splitData = FlatFileDataUtil.getPreparedSaveDataLine(dataContainer);

            if(splitData == null)
                continue;

            //We add a trailing : as it is needed for some reason (is it?)
            //TODO: Is the trailing ":" actually necessary?
            String fromSplit = org.apache.commons.lang.StringUtils.join(splitData, ":") + ":";
            stringBuilder.append(fromSplit).append("\r\n");
        }

        return stringBuilder;
    }

}
