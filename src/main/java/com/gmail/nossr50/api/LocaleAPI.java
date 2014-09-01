package com.gmail.nossr50.api;

import java.util.ResourceBundle;

import com.gmail.nossr50.locale.LocaleLoader;

public final class LocaleAPI {
	
	public static void addBundles(ResourceBundle bundle, ResourceBundle defaultBundle) {
		LocaleLoader.addResourceBundle(bundle, defaultBundle);
	}
	
	public static void addBundle(ResourceBundle bundle, boolean isDefault) {
		if(isDefault) {
			addBundles(null, bundle);
		}
		else {
			addBundles(bundle, null);
		}
	}

}
