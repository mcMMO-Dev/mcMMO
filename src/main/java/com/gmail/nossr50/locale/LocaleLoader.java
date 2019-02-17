package com.gmail.nossr50.locale;

import com.gmail.nossr50.config.MainConfig;
import com.gmail.nossr50.mcMMO;
import org.bukkit.ChatColor;

import java.text.MessageFormat;
import java.util.Locale;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

public final class LocaleLoader {
    private static final String BUNDLE_ROOT = "com.gmail.nossr50.locale.locale";
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

        try {
            return getString(key, bundle, messageArguments);
        }
        catch (MissingResourceException ex) {
            try {
                return getString(key, enBundle, messageArguments);
            }
            catch (MissingResourceException ex2) {
                if (!key.contains("Guides")) {
                    mcMMO.p.getLogger().warning("Could not find locale string: " + key);
                }

                return '!' + key + '!';
            }
        }
    }

    private static String getString(String key, ResourceBundle bundle, Object... messageArguments) throws MissingResourceException {
        return formatString(bundle.getString(key), messageArguments);
    }

    public static String formatString(String string, Object... messageArguments) {
        if (messageArguments != null) {
            MessageFormat formatter = new MessageFormat("");
            formatter.applyPattern(string.replace("'", "''"));
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
            Locale.setDefault(new Locale("en", "US"));
            Locale locale = null;
            String[] myLocale = MainConfig.getInstance().getLocale().split("[-_ ]");

            if (myLocale.length == 1) {
                locale = new Locale(myLocale[0]);
            }
            else if (myLocale.length >= 2) {
                locale = new Locale(myLocale[0], myLocale[1]);
            }

            bundle = ResourceBundle.getBundle(BUNDLE_ROOT, locale);
            enBundle = ResourceBundle.getBundle(BUNDLE_ROOT, Locale.US);
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

        input = input.replaceAll("\\Q&0\\E", ChatColor.BLACK.toString());
        input = input.replaceAll("\\Q&1\\E", ChatColor.DARK_BLUE.toString());
        input = input.replaceAll("\\Q&2\\E", ChatColor.DARK_GREEN.toString());
        input = input.replaceAll("\\Q&3\\E", ChatColor.DARK_AQUA.toString());
        input = input.replaceAll("\\Q&4\\E", ChatColor.DARK_RED.toString());
        input = input.replaceAll("\\Q&5\\E", ChatColor.DARK_PURPLE.toString());
        input = input.replaceAll("\\Q&6\\E", ChatColor.GOLD.toString());
        input = input.replaceAll("\\Q&7\\E", ChatColor.GRAY.toString());
        input = input.replaceAll("\\Q&8\\E", ChatColor.DARK_GRAY.toString());
        input = input.replaceAll("\\Q&9\\E", ChatColor.BLUE.toString());
        input = input.replaceAll("\\Q&a\\E", ChatColor.GREEN.toString());
        input = input.replaceAll("\\Q&b\\E", ChatColor.AQUA.toString());
        input = input.replaceAll("\\Q&c\\E", ChatColor.RED.toString());
        input = input.replaceAll("\\Q&d\\E", ChatColor.LIGHT_PURPLE.toString());
        input = input.replaceAll("\\Q&e\\E", ChatColor.YELLOW.toString());
        input = input.replaceAll("\\Q&f\\E", ChatColor.WHITE.toString());
        input = input.replaceAll("\\Q&l\\E", ChatColor.BOLD.toString());
        input = input.replaceAll("\\Q&n\\E", ChatColor.UNDERLINE.toString());
        input = input.replaceAll("\\Q&o\\E", ChatColor.ITALIC.toString());
        input = input.replaceAll("\\Q&m\\E", ChatColor.STRIKETHROUGH.toString());
        input = input.replaceAll("\\Q&?\\E", ChatColor.MAGIC.toString());
        input = input.replaceAll("\\Q&r\\E", ChatColor.RESET.toString());

        return input;
    }
}
