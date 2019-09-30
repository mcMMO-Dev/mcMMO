package com.gmail.nossr50.config.language;

import ninja.leaping.configurate.objectmapping.Setting;
import ninja.leaping.configurate.objectmapping.serialize.ConfigSerializable;

@ConfigSerializable
public class ConfigLanguage {

    public static final String TARGET_LANGUAGE_DEFAULT = "en_US";
    public static final String AVAILABLE_LANGUAGE_LIST = "cs_CZ, cy, da, de, en_US, es, fi, fr, hu_HU, it, ja_JP, ko," +
            "\n nl, pl, pt_BR, ru, sv, th_TH, zh_CN, zh_TW";

    @Setting(value = "Language", comment = "Which language mcMMO will use." +
            "\nThe default language for mcMMO will be used for languages that do not have complete translations." +
            "\nIf you'd wish to contribute to available languages please submit a pull request on our github" +
            "\nhttps://github.com/mcMMO-Dev/mcMMO" +
            "\nAvailable Languages: " + AVAILABLE_LANGUAGE_LIST +
            "\n\nDefault value: " + TARGET_LANGUAGE_DEFAULT)
    private String targetLanguage = TARGET_LANGUAGE_DEFAULT;

    public String getTargetLanguage() {
        return targetLanguage;
    }
}
