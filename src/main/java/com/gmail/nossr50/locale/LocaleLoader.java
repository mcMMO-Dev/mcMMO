package com.gmail.nossr50.locale;

import com.gmail.nossr50.mcMMO;
import com.gmail.nossr50.util.text.TextUtils;
import net.kyori.adventure.text.TextComponent;
import org.bukkit.ChatColor;
import org.jetbrains.annotations.NotNull;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.MessageFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.logging.Level;

public final class LocaleLoader {
    private static final String BUNDLE_ROOT = "com.gmail.nossr50.locale.locale";
    private static final String OVERRIDE_FILE_NAME = "locale_override.properties";
    private static Map<String, String> bundleCache = new HashMap<>();
    private static ResourceBundle bundle = null;
    private static ResourceBundle filesystemBundle = null;
    private static ResourceBundle enBundle = null;

    private LocaleLoader() {}

    public static String getString(String key) {
        return getString(key, (Object[]) null);
    }

    /**
     * Gets the appropriate string from the Locale files.
     *
     * @param key              The key to look up the string with
     * @param messageArguments Any arguments to be added to the string
     *
     * @return The properly formatted locale string
     */
    public static String getString(String key, Object... messageArguments) {
        if (bundle == null) {
            initialize();
        }

        String rawMessage = bundleCache.computeIfAbsent(key, LocaleLoader::getRawString);
        return formatString(rawMessage, messageArguments);
    }

    //TODO: Remove this hacky crap with something better later

    /**
     * Gets the appropriate TextComponent representation of a formatted string from the Locale files.
     *
     * @param key              The key to look up the string with
     * @param messageArguments Any arguments to be added to the text component
     *
     * @return The properly formatted text component
     */
    public static @NotNull TextComponent getTextComponent(@NotNull String key, Object... messageArguments) {
        if (bundle == null) {
            initialize();
        }

        String rawMessage = bundleCache.computeIfAbsent(key, LocaleLoader::getRawString);
        return formatComponent(rawMessage, messageArguments);
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
            } catch (MissingResourceException ignored) {
            }
        }

        try {
            return bundle.getString(key);
        } catch (MissingResourceException ignored) {
        }

        try {
            return enBundle.getString(key);
        } catch (MissingResourceException ignored) {
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

    public static @NotNull TextComponent formatComponent(@NotNull String string, Object... messageArguments) {
        if (messageArguments != null) {
            MessageFormat formatter = new MessageFormat("");
            formatter.applyPattern(string.replace("'", "''"));
            string = formatter.format(messageArguments);
        }

        return TextUtils.colorizeText(string);
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

            String[] myLocale = mcMMO.p.getGeneralConfig().getLocale().split("[-_ ]");

            if (myLocale.length == 1) {
                locale = new Locale(myLocale[0]);
            } else if (myLocale.length >= 2) {
                locale = new Locale(myLocale[0], myLocale[1]);
            }

            if (locale == null) {
                throw new IllegalStateException("Failed to parse locale string '" + mcMMO.p.getGeneralConfig().getLocale() + "'");
            }

            Path localePath = Paths.get(mcMMO.getLocalesDirectory() + "locale_" + locale.toString() + ".properties");
            Path overridePath = Paths.get(mcMMO.getLocalesDirectory() + OVERRIDE_FILE_NAME);
            File overrideFile = overridePath.toFile();

            if (Files.exists(localePath) && Files.isRegularFile(localePath)) {

                File oldOverrideFile = localePath.toFile();

                try {
                    //Copy the file
                    com.google.common.io.Files.copy(oldOverrideFile, overrideFile);
                    //Remove the old file now
                    oldOverrideFile.delete();

                    //Insert our helpful text
                    StringBuilder stringBuilder = new StringBuilder();

                    try(BufferedReader bufferedReader = new BufferedReader(new FileReader(overrideFile.getPath()))) {
                        // Open the file
                        String line;
                        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("MM/dd/yyyy HH:mm");
                        LocalDateTime localDateTime = LocalDateTime.now();
                        stringBuilder.append("# mcMMO Locale Override File created on ").append(localDateTime.format(dateTimeFormatter)).append("\r\n"); //Empty file
                        stringBuilder.append(getLocaleHelpTextWithoutExamples()); //Add our helpful text
                        while ((line = bufferedReader.readLine()) != null) {
                            stringBuilder.append(line).append("\r\n");
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                    try(FileWriter fileWriter = new FileWriter(overrideFile.getPath())) {
                        fileWriter.write(stringBuilder.toString());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            //Use the new locale file
            if (Files.exists(overridePath) && Files.isRegularFile(overridePath)) {
                try (Reader localeReader = Files.newBufferedReader(overridePath)) {
                    mcMMO.p.getLogger().log(Level.INFO, "Loading locale from {0}", overridePath);
                    filesystemBundle = new PropertyResourceBundle(localeReader);
                } catch (IOException e) {
                    mcMMO.p.getLogger().log(Level.WARNING, "Failed to load locale from " + overridePath, e);
                }
            } else {
                //Create a blank file and fill it in with some helpful text
                try (BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(overrideFile, true))) {
                    // Open the file to write the player
                    DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("MM/dd/yyyy HH:mm");
                    LocalDateTime localDateTime = LocalDateTime.now();
                    bufferedWriter.append("# mcMMO Locale Override File created on ").append(localDateTime.format(dateTimeFormatter)).append("\r\n"); //Empty file
                    String localeExplanation = getLocaleHelpText();
                    bufferedWriter.append(localeExplanation);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            bundle = ResourceBundle.getBundle(BUNDLE_ROOT, locale);
        }

        enBundle = ResourceBundle.getBundle(BUNDLE_ROOT, Locale.US);
    }

    @NotNull
    private static String getLocaleHelpText() {
        String localeExplanation =
                        "# -- Are you looking to change the language of mcMMO but without editing it yourself? --\n" +
                        "\n" +
                        "# mcMMO has quite a few built in translations, you can choose which translation by editing config.yml with the appropriate locale code. The setting is `General.Locale` in config.yml\n" +
                        "# Odds are, if you speak a popular language on earth we already have a translation for it.\n" +
                        "# However our translations are done by the community, and update infrequently. (Please help us out <3)\n" +
                        "# We would love more people to help update our locales, submit any updated translation file to our GitHub or email it to me at business@neetgames.com\n" +
                        "# For a list of built in translations, view this link: https://github.com/mcMMO-Dev/mcMMO/tree/master/src/main/resources/locale\n" +
                        "\n" +
                        "\n" +
                        "# -- Using a built in translation -- \n" +
                        "# Assuming you read the above section, edit config.yml's General.Locale from en_US to the locale code that we support (see the above link), then reboot your server\n" +
                        "\n" +
                        "\n" +
                        "# -- Do you want to change the text in mcMMO? Including adding colors? ( Locale Override ) -- \n" +
                        "# First, a brief explanation.\n" +
                        "# Locales are the language files used by mcMMO, they also contain color codes and most of the styling used by mcMMO.\n" +
                        "# You can customize a locale outside of the JAR in version 2.1.51 and up.\n" +
                        "#\n" +
                        "# Locales can be overridden by editing this file\n" +
                        "# You can find the up to date current locale files here https://github.com/mcMMO-Dev/mcMMO/tree/master/src/main/resources/locale\n" +
                        "# The master file is en_US, if a translation is missing entries (as they often are) it will pull from the en_US file https://github.com/mcMMO-Dev/mcMMO/blob/master/src/main/resources/locale/locale_en_US.properties\n" +
                        "#\n" +
                        "# To override a locale, add entries to this file and copy ** only ** the strings you want to replace, otherwise you will not see any updated strings when mcMMO updates and will have to manually change them and read patch notes carefully.\n" +
                        "# If you wish to replace every line in some way, feel free to copy the entire contents of this file, just be advised that you will need to be on top of locale updates in mcMMO and follow our changelog closely.\n" +
                        "\n" +
                        "\n" +
                        "# WARNING: Locales only support ASCII and UTF16 characters at the moment, so you'll need to run special characters through a UTF16 converter (google it) to get them to work. This will be fixed in the future!\n" +
                        "# FIND KEYS HERE: On our github repo (en_US is our master file and has ALL the keys) -> https://github.com/mcMMO-Dev/mcMMO/tree/master/src/main/resources/locale\n" +
                        "# WARNING: Some keys in our master file are unused, make gratuitous use of Ctrl+F\n" +
                        "# HOW TO APPLY: You can either restart the server for these changes to take effect or run /mcreloadlocale.\n" +
                        "# -- Add Keys Below --\n" +
                        getExamples();
        return localeExplanation;
    }

    @NotNull
    private static String getExamples() {
        return "This.Is.An.Example.Put.Locale.Keys.Here.One=&aExample text using hex color codes\n" +
                "This.Is.An.Example.Put.Locale.Keys.Here.Two=[[DARK_AQUA]]Example text using our own color codes\n" +
                "This.Is.An.Example.Put.Locale.Keys.Here.Three=Example text with no colors\n";
    }

    @NotNull
    private static String getLocaleHelpTextWithoutExamples() {
        String localeExplanation =
                        "# -- Are you looking to change the language of mcMMO but without editing it yourself? --\n" +
                        "\n" +
                        "# mcMMO has quite a few built in translations, you can choose which translation by editing config.yml with the appropriate locale code. The setting is `General.Locale` in config.yml\n" +
                        "# Odds are, if you speak a popular language on earth we already have a translation for it.\n" +
                        "# However our translations are done by the community, and update infrequently. (Please help us out <3)\n" +
                        "# We would love more people to help update our locales, submit any updated translation file to our GitHub or email it to me at business@neetgames.com\n" +
                        "# For a list of built in translations, view this link: https://github.com/mcMMO-Dev/mcMMO/tree/master/src/main/resources/locale\n" +
                        "\n" +
                        "\n" +
                        "# -- Using a built in translation -- \n" +
                        "# Assuming you read the above section, edit config.yml's General.Locale from en_US to the locale code that we support (see the above link), then reboot your server\n" +
                        "\n" +
                        "\n" +
                        "# -- Do you want to change the text in mcMMO? Including adding colors? ( Locale Override ) -- \n" +
                        "# First, a brief explanation.\n" +
                        "# Locales are the language files used by mcMMO, they also contain color codes and most of the styling used by mcMMO.\n" +
                        "# You can customize a locale outside of the JAR in version 2.1.51 and up.\n" +
                        "#\n" +
                        "# Locales can be overridden by editing this file\n" +
                        "# You can find the up to date current locale files here https://github.com/mcMMO-Dev/mcMMO/tree/master/src/main/resources/locale\n" +
                        "# The master file is en_US, if a translation is missing entries (as they often are) it will pull from the en_US file https://github.com/mcMMO-Dev/mcMMO/blob/master/src/main/resources/locale/locale_en_US.properties\n" +
                        "#\n" +
                        "# To override a locale, add entries to this file and copy ** only ** the strings you want to replace, otherwise you will not see any updated strings when mcMMO updates and will have to manually change them and read patch notes carefully.\n" +
                        "# If you wish to replace every line in some way, feel free to copy the entire contents of this file, just be advised that you will need to be on top of locale updates in mcMMO and follow our changelog closely.\n" +
                        "\n" +
                        "\n" +
                        "# WARNING: Locales only support ASCII and UTF16 characters at the moment, so you'll need to run special characters through a UTF16 converter (google it) to get them to work. This will be fixed in the future!\n" +
                        "# FIND KEYS HERE: On our github repo (en_US is our master file and has ALL the keys) -> https://github.com/mcMMO-Dev/mcMMO/tree/master/src/main/resources/locale\n" +
                        "# WARNING: Some keys in our master file are unused, make gratuitous use of Ctrl+F\n" +
                        "# HOW TO APPLY: You can either restart the server for these changes to take effect or run /mcreloadlocale.\n" +
                        "# -- Add Keys Below --\n";
        return localeExplanation;
    }

    public static String addColors(String input) {
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
