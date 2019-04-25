package com.gmail.nossr50.config.hocon.superabilities;

import ninja.leaping.configurate.objectmapping.Setting;
import ninja.leaping.configurate.objectmapping.serialize.ConfigSerializable;

@ConfigSerializable
public class ConfigSectionTreeFeller {

    public static final int TREE_FELLER_LIMIT_DEFAULT = 500;

    @Setting(value = "Tree-Size-Limit", comment = "Trees over this many blocks in size will not activate" +
            "\nLower this number to improve performance." +
            "\nDefault value: " + TREE_FELLER_LIMIT_DEFAULT)
    private int treeFellerLimit = TREE_FELLER_LIMIT_DEFAULT;

    public int getTreeFellerLimit() {
        return treeFellerLimit;
    }
}