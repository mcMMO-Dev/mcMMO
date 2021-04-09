package com.gmail.nossr50.database;

public enum FlatFileDataFlag {
    INCOMPLETE,
    BAD_VALUES,
    MISSING_NAME,
    DUPLICATE_NAME_FIXABLE,
    DUPLICATE_NAME_NOT_FIXABLE,
    DUPLICATE_UUID,
    MISSING_OR_NULL_UUID,
    TOO_INCOMPLETE,
    JUNK,
    EMPTY,
}
