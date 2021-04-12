package com.gmail.nossr50.database;

import com.gmail.nossr50.database.flatfile.CategorizedFlatFileData;
import com.gmail.nossr50.database.flatfile.CategorizedFlatFileDataBuilder;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.util.*;
import java.util.logging.Logger;

import static com.gmail.nossr50.database.FlatFileDatabaseManager.*;

public class FlatFileDataProcessor {
    public static final String INVALID_OLD_USERNAME = "_INVALID_OLD_USERNAME_";
    private @NotNull List<CategorizedFlatFileData> categorizedDataList;
    private @NotNull List<FlatFileDataFlag> flatFileDataFlags;
    private final @NotNull File userFile;
    private final @NotNull Logger logger;
    private final HashSet<String> names;
    private final HashSet<UUID> uuids;
    private int uniqueProcessingID;
    boolean corruptDataFound;

    public FlatFileDataProcessor(@NotNull File userFile, @NotNull Logger logger) {
        this.userFile = userFile;
        this.logger = logger;
        categorizedDataList = new ArrayList<>();
        flatFileDataFlags = new ArrayList<>();
        names = new HashSet<>();
        uuids = new HashSet<>();
        uniqueProcessingID = 0;
    }

    public void processData(@NotNull String lineData) {
        CategorizedFlatFileDataBuilder builder = new CategorizedFlatFileDataBuilder(lineData, uniqueProcessingID);
        uniqueProcessingID++;

        /*
         * Is the line empty?
         */
        if (lineData.isEmpty()) {
            registerData(builder.appendFlag(FlatFileDataFlag.EMPTY_LINE));
            return;
        }

        //Make sure the data line is "correct"
        if(lineData.charAt(lineData.length() - 1) != ':') {
            // Length checks depend on last rawSplitData being ':'
            // We add it here if it is missing
            lineData = lineData.concat(":");
        }

        //Split the data into an array
        String[] splitDataLine = lineData.split(":");

        //This is the minimum size of the split array needed to be considered proper data
        if(splitDataLine.length < getMinimumSplitDataLength()) {
            //Data is considered junk
            if(!corruptDataFound) {
                logger.severe("Some corrupt data was found in mcmmo.users and has been repaired, it is possible that some player data has been lost in this process.");
                corruptDataFound = true;
            }

            //Flag as junk (corrupt)
            builder.appendFlag(FlatFileDataFlag.JUNK);

            //TODO: This block here is probably pointless
            if(splitDataLine.length >= 10 //The value here is kind of arbitrary, it shouldn't be too low to avoid false positives, but also we aren't really going to correctly identify when player data has been corrupted or not with 100% accuracy ever
                    && splitDataLine[0] != null && !splitDataLine[0].isEmpty()) {
                if(splitDataLine[0].length() <= 16 && splitDataLine[0].length() >= 3) {
                    logger.severe("Not enough data found to recover corrupted player data for user: "+splitDataLine[0]);
                    registerData(builder.appendFlag(FlatFileDataFlag.TOO_INCOMPLETE));
                    return;
                }
            }

            registerData(builder.appendFlag(FlatFileDataFlag.JUNK));
            return;
        }

        /*
         * Check for duplicate names
         */

        boolean nameIsDupe = false;
        boolean invalidUUID = false;

        String name = splitDataLine[USERNAME_INDEX];
        String strOfUUID = splitDataLine[UUID_INDEX];

        if(name.isEmpty()) {
            reportBadDataLine("No name found for data", "[MISSING NAME]", lineData);
            builder.appendFlag(FlatFileDataFlag.MISSING_NAME);
        }

        if(strOfUUID.isEmpty() || strOfUUID.equalsIgnoreCase("NULL")) {
            invalidUUID = true;
            reportBadDataLine("Empty/null UUID for user", "Empty/null", lineData);
            builder.appendFlag(FlatFileDataFlag.MISSING_OR_NULL_UUID);
        }

        UUID uuid = null;

        try {
            uuid = UUID.fromString(strOfUUID);
        } catch (IllegalArgumentException e) {
            invalidUUID = true;
            //UUID does not conform

            reportBadDataLine("Invalid UUID data found for user", strOfUUID, lineData);
            e.printStackTrace();
        }

        //Duplicate UUID is no good, reject them
        if(uuid != null && uuids.contains(uuid)) {
            registerData(builder.appendFlag(FlatFileDataFlag.DUPLICATE_UUID));
            return;
        }

        uuids.add(uuid);


        if(names.contains(name)) {
            //Duplicate entry
            nameIsDupe = true;

            //We can accept them if they are a duped name if they have a unique UUID
            if(invalidUUID) {
                //Reject the data
                reportBadDataLine("Duplicate user found and due to a missing UUID their data had to be discarded", name, lineData);

                registerData(builder.appendFlag(FlatFileDataFlag.DUPLICATE_NAME_NOT_FIXABLE));
                return;
            } else {
                builder.appendFlag(FlatFileDataFlag.DUPLICATE_NAME_FIXABLE);
            }
        }

        if(!name.isEmpty())
            names.add(name);

        //Make sure the data is up to date schema wise
        if(splitDataLine.length < DATA_ENTRY_COUNT) {
            splitDataLine = Arrays.copyOf(splitDataLine, DATA_ENTRY_COUNT+1);
            lineData = org.apache.commons.lang.StringUtils.join(splitDataLine, ":") + ":";
            builder.appendFlag(FlatFileDataFlag.INCOMPLETE);
            builder.setStringDataRepresentation(lineData);
        }

        /*
         * After establishing this data has at least an identity we check for bad data
         * Bad Value checks
         */

        //Check each data for bad values
        boolean[] badDataValues = new boolean[DATA_ENTRY_COUNT];
        boolean anyBadData = false;

        for(int i = 0; i < DATA_ENTRY_COUNT; i++) {
            if(shouldNotBeEmpty(splitDataLine[i], i)) {
                badDataValues[i] = true;
                anyBadData = true;
                reportBadDataLine("Data is empty when it should not be at index", "[index=" + i + "]", lineData);
                continue;
            }

            boolean isCorrectType = isOfExpectedType(splitDataLine[i], getExpectedValueType(i));

            if(!isCorrectType) {
                reportBadDataLine("Data is not of correct type", splitDataLine[i], lineData);
                anyBadData = true;
                badDataValues[i] = true;
            }
        }

        if(anyBadData) {
            builder.appendFlag(FlatFileDataFlag.BAD_VALUES);
        }

        registerData(builder);
    }

    public boolean shouldNotBeEmpty(String data, int index) {
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
        logger.severe("FlatFileDatabaseBuilder Warning: " + warning + " - " + context);
        logger.severe("FlatFileDatabaseBuilder: (Line Data) - " + dataLine);
    }

    private int getMinimumSplitDataLength() {
        return UUID_INDEX + 1;
    }

    private void registerData(@NotNull CategorizedFlatFileDataBuilder builder) {
        CategorizedFlatFileData categorizedFlatFileData = builder.build();
        categorizedDataList.add(categorizedFlatFileData);
        flatFileDataFlags.addAll(categorizedFlatFileData.getDataFlags());
    }

    public @NotNull ExpectedType getExpectedValueType(int dataIndex) {
        switch(dataIndex) {
            case USERNAME_INDEX:
                return ExpectedType.STRING;
            case 2: //Assumption: Used to be for something, no longer used
            case 3: //Assumption: Used to be for something, no longer used
            case 23: //Assumption: Used to be used for something, no longer used
            case 33: //Assumption: Used to be used for something, no longer used
            case HEALTHBAR:
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
            case LAST_LOGIN:
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
                return ExpectedType.FLOAT;
            case UUID_INDEX:
                return ExpectedType.UUID;
            default:
                return ExpectedType.OUT_OF_RANGE;
        }
    }

    public @NotNull List<CategorizedFlatFileData> getCategorizedDataList() {
        return categorizedDataList;
    }

    public @NotNull List<FlatFileDataFlag> getFlatFileDataFlags() {
        return flatFileDataFlags;
    }

    public int getDataFlagCount() {
        return flatFileDataFlags.size();
    }
}
