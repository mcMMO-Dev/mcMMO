package com.gmail.nossr50.locale;

import com.gmail.nossr50.config.Config;
import com.gmail.nossr50.mcMMO;
import org.bukkit.ChatColor;

import java.io.IOException;
import java.io.Reader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.MessageFormat;
import java.util.*;
import java.util.logging.Level;

public final class LocaleLoader {
    private static final String BUNDLE_ROOT = "com.gmail.nossr50.locale.locale";
    private static Map<String, String> bundleCache = new HashMap<>();
    private static ResourceBundle bundle = null;
    private static ResourceBundle filesystemBundle = null;
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

        String rawMessage = bundleCache.computeIfAbsent(key, LocaleLoader::getRawString);
        return formatString(rawMessage, messageArguments);
    }

    /**
     * Reloads locale
     */
    public static void reloadLocale() {
        bundle = null;
        filesystemBundle = null;
        enBundle = null;
        bundleCache = new HashMap<>(); // Cheaper to replace than clear()
        initialize();
    }

    private static String getRawString(String key) {
        if (filesystemBundle != null) {
            try {
                return filesystemBundle.getString(key);
            }
            catch (MissingResourceException ignored) {}
        }

        try {
            return bundle.getString(key);
        }
        catch (MissingResourceException ignored) {}

        try {
            return enBundle.getString(key);
        }
        catch (MissingResourceException ignored) {
            if (!key.contains("Guides")) {
                mcMMO.p.getLogger().warning("Could not find locale string: " + key);
            }

            return '!' + key + '!';
        }
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
            String[] myLocale = Config.getInstance().getLocale().split("[-_ ]");

            if (myLocale.length == 1) {
                locale = new Locale(myLocale[0]);
            }
            else if (myLocale.length >= 2) {
                locale = new Locale(myLocale[0], myLocale[1]);
            }

            if (locale == null) {
                throw new IllegalStateException("Failed to parse locale string '" + Config.getInstance().getLocale() + "'");
            }

            Path localePath = Paths.get(mcMMO.getLocalesDirectory() + "locale_" + locale.toString() + ".properties");
            if (Files.exists(localePath) && Files.isRegularFile(localePath)) {
                try (Reader localeReader = Files.newBufferedReader(localePath)) {
                    mcMMO.p.getLogger().log(Level.INFO, "Loading locale from {0}", localePath);
                    filesystemBundle = new PropertyResourceBundle(localeReader);
                } catch (IOException e) {
                    mcMMO.p.getLogger().log(Level.WARNING, "Failed to load locale from " + localePath, e);
                }
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
