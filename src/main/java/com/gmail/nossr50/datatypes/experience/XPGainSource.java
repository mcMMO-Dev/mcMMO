package com.gmail.nossr50.datatypes.experience;

public enum XPGainSource {
    /** From direct sources, either your own actions or actions done to you. */
    SELF,
    /** From Vampirism Kills */
    VAMPIRISM, //From Vampirism kills
    /** From Smelting, Brewing, etc... */
    PASSIVE, //Smelting, Brewing, etc...
    /** From shared XP from party members */
    PARTY_MEMBERS, //From other members of a party
    /** From commands or API */
    COMMAND,
    /** Uncategorized, Other Plugins, etc... */
    CUSTOM, //Outside Sources
}
