package com.gmail.nossr50.database;

public enum FlatFileDataFlag {
    INCOMPLETE,
    BAD_VALUES,
    MISSING_NAME,
    DUPLICATE_NAME,
    DUPLICATE_UUID,
    BAD_UUID_DATA, //Can be because it is missing, null, or just not compatible data
    TOO_INCOMPLETE,
    JUNK,
    EMPTY_LINE,
}
