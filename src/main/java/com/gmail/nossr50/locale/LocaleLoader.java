package com.gmail.nossr50.locale;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

import org.bukkit.ChatColor;

import com.gmail.nossr50.mcMMO;
import com.gmail.nossr50.config.Config;

public final class LocaleLoader {
    private static final String BUNDLE_ROOT = "com.gmail.nossr50.locale.locale";
    private static List<ResourceBundle> bundles = new ArrayList<ResourceBundle>();
    private static List<ResourceBundle> defaultBundles = new ArrayList<ResourceBundle>();
    private static ResourceBundle bundle = null;
    private static ResourceBundle enBundle = null;

    private LocaleLoader() {};

    public static String getString(String key) {
        return getString(key, (Object[]) null);
    }

    /**
     * Gets the appropriate string from the Locale files.
     *
     * @param key The key to look up the string with
     * @param messageArguments Any arguments to be added to the string
     * @return The properly formatted locale string
     */
    public static String getString(String key, Object... messageArguments) {
        if (bundle == null) {
            initialize();
        }

        for(ResourceBundle customBundle : bundles) {
        	if(customBundle.containsKey(key)) {
        		return getString(key, customBundle, messageArguments);
        	}
        }
        if(bundle.containsKey(key)) {
        	return getString(key, bundle, messageArguments);
        }
        for(ResourceBundle defaultCustomBundle : defaultBundles) {
        	if(defaultCustomBundle.containsKey(key)) {
        		return getString(key, defaultCustomBundle, messageArguments);
        	}
        }
        if(enBundle.containsKey(key)) {
        	return getString(key, enBundle, messageArguments);
        }
        if (!key.contains("Guides")) {
            mcMMO.p.getLogger().warning("Could not find locale string: " + key);
        }

        return '!' + key + '!';
    }

    private static String getString(String key, ResourceBundle bundle, Object... messageArguments) throws MissingResourceException {
        return formatString(bundle.getString(key), messageArguments);
    }

    public static String formatString(String string, Object... messageArguments) {
        if (messageArguments != null) {
            MessageFormat formatter = new MessageFormat("");
            formatter.applyPattern(string);
            string = formatter.format(messageArguments);
        }

        string = addColors(string);

        return string;
    }

    public static Locale getCurrentLocale() {
        if (bundle == null) {
            initialize();
        }
        return bundle.getLocale();
    }

    private static void initialize() {
        if (bundle == null) {
            Locale locale = getLocaleFromConfig();

            bundle = ResourceBundle.getBundle(BUNDLE_ROOT, locale);
            enBundle = ResourceBundle.getBundle(BUNDLE_ROOT, Locale.US);
        }
    }

	public static Locale getLocaleFromConfig() {
		Locale.setDefault(new Locale("en", "US"));
		Locale locale = null;
		String[] myLocale = Config.getInstance().getLocale().split("[-_ ]");

		if (myLocale.length == 1) {
		    locale = new Locale(myLocale[0]);
		}
		else if (myLocale.length >= 2) {
		    locale = new Locale(myLocale[0], myLocale[1]);
		}
		return locale;
	}
    
    public static void addResourceBundle(String bundleRoot, Locale defaultLocale) {
    	Locale locale = getLocaleFromConfig();
    	bundles.add(ResourceBundle.getBundle(bundleRoot, locale));
    	defaultBundles.add(ResourceBundle.getBundle(bundleRoot, defaultLocale));
    }
    
    public static void addResourceBundle(ResourceBundle bundle, ResourceBundle defaultBundle) {
    	if(bundle != null) {
    		bundles.add(bundle);
    	}
    	if(defaultBundle != null) {
    		defaultBundles.add(defaultBundle);
    	}
    }

    private static String addColors(String input) {
        input = input.replaceAll("\\Q[[BLACK]]\\E", ChatColor.BLACK.toString());
        input = input.replaceAll("\\Q[[DARK_BLUE]]\\E", ChatColor.DARK_BLUE.toString());
        input = input.replaceAll("\\Q[[DARK_GREEN]]\\E", ChatColor.DARK_GREEN.toString());
        input = input.replaceAll("\\Q[[DARK_AQUA]]\\E", ChatColor.DARK_AQUA.toString());
        input = input.replaceAll("\\Q[[DARK_RED]]\\E", ChatColor.DARK_RED.toString());
        input = input.replaceAll("\\Q[[DARK_PURPLE]]\\E", ChatColor.DARK_PURPLE.toString());
        input = input.replaceAll("\\Q[[GOLD]]\\E", ChatColor.GOLD.toString());
        input = input.replaceAll("\\Q[[GRAY]]\\E", ChatColor.GRAY.toString());
        input = input.replaceAll("\\Q[[DARK_GRAY]]\\E", ChatColor.DARK_GRAY.toString());
        input = input.replaceAll("\\Q[[BLUE]]\\E", ChatColor.BLUE.toString());
        input = input.replaceAll("\\Q[[GREEN]]\\E", ChatColor.GREEN.toString());
        input = input.replaceAll("\\Q[[AQUA]]\\E", ChatColor.AQUA.toString());
        input = input.replaceAll("\\Q[[RED]]\\E", ChatColor.RED.toString());
        input = input.replaceAll("\\Q[[LIGHT_PURPLE]]\\E", ChatColor.LIGHT_PURPLE.toString());
        input = input.replaceAll("\\Q[[YELLOW]]\\E", ChatColor.YELLOW.toString());
        input = input.replaceAll("\\Q[[WHITE]]\\E", ChatColor.WHITE.toString());
        input = input.replaceAll("\\Q[[BOLD]]\\E", ChatColor.BOLD.toString());
        input = input.replaceAll("\\Q[[UNDERLINE]]\\E", ChatColor.UNDERLINE.toString());
        input = input.replaceAll("\\Q[[ITALIC]]\\E", ChatColor.ITALIC.toString());
        input = input.replaceAll("\\Q[[STRIKE]]\\E", ChatColor.STRIKETHROUGH.toString());
        input = input.replaceAll("\\Q[[MAGIC]]\\E", ChatColor.MAGIC.toString());
        input = input.replaceAll("\\Q[[RESET]]\\E", ChatColor.RESET.toString());

        return input;
    }
}
