package com.gmail.nossr50.datatypes.skills.interfaces;

/**
 * Localized interface represents skills which have localizations Skills with localizations will use
 * their localization names/descriptions when being printed
 */
public interface Localized {
    /**
     * The translated name for this locale
     *
     * @return the translated name for this locale
     */
    String getLocaleName();

    /**
     * The translated name for this subskill description
     *
     * @return
     */
    String getLocaleDescription();
}
