package com.gmail.nossr50.datatypes.database;

public enum DatabaseType {
    FLATFILE,
    SQL,
    CUSTOM;

    public static DatabaseType getDatabaseType(String typeName) {
        for (DatabaseType type : values()) {
            if (type.name().equalsIgnoreCase(typeName)) {
                return type;
            }
        }

        if (typeName.equalsIgnoreCase("file")) {
            return FLATFILE;
        } else if (typeName.equalsIgnoreCase("mysql")) {
            return SQL;
        }

        return CUSTOM;
    }
}
