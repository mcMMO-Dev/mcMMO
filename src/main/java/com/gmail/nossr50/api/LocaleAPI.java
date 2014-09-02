package com.gmail.nossr50.api;

import java.util.Locale;
import java.util.ResourceBundle;

import com.gmail.nossr50.locale.LocaleLoader;

public final class LocaleAPI {
	
	/**
	 * Adds a default resource localization bundle and a localized one
	 * @param bundle the bundle with priority
	 * @param defaultBundle the default bundle to fall back on
	 */
	public static void addBundles(ResourceBundle bundle, ResourceBundle defaultBundle) {
		LocaleLoader.addResourceBundle(bundle, defaultBundle);
	}
	
	/**
	 * Adds a localization bundle to the locale loader
	 * @param bundle the localization bundle to add
	 * @param isDefault whether the bundle is a default bundle (has lower priority)
	 */
	public static void addBundle(ResourceBundle bundle, boolean isDefault) {
		if(isDefault) {
			addBundles(null, bundle);
		}
		else {
			addBundles(bundle, null);
		}
	}
	
	/**
	 * Gets the current locale to create a resource bundle for
	 * @return the current locale
	 */
	public static Locale getCurrentLocale() {
		return LocaleLoader.getCurrentLocale();
	}

}
