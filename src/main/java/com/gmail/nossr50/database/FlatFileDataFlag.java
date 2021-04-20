package com.gmail.nossr50.database;

public enum FlatFileDataFlag {
    INCOMPLETE,
    BAD_VALUES,
    LAST_LOGIN_SCHEMA_UPGRADE,
    MISSING_NAME,
    DUPLICATE_NAME,
    DUPLICATE_UUID,
    BAD_UUID_DATA, //Can be because it is missing, null, or corrupted or some other reason
    TOO_INCOMPLETE,
    CORRUPTED_OR_UNRECOGNIZABLE,
}
