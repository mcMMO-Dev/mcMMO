package com.gmail.nossr50.locale;

import java.text.MessageFormat;
import java.util.Locale;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

import org.bukkit.ChatColor;

import com.gmail.nossr50.config.LoadProperties;

public class mcLocale 
{
	private static final String BUNDLE_NAME = "com.gmail.nossr50.locale.locale"; //$NON-NLS-1$

	private static ResourceBundle RESOURCE_BUNDLE = null;

	public static String getString(String key) 
	{
		return getString(key, null);
	}

	public static String getString(String key, Object[] messageArguments) 
	{
		try {
			if (RESOURCE_BUNDLE == null) 
			{
				String myLocale = LoadProperties.locale.toLowerCase();
				try {
					//attempt to get the locale denoted
					RESOURCE_BUNDLE = ResourceBundle.getBundle(BUNDLE_NAME, new Locale(myLocale));
				} catch (MissingResourceException e) {
					//System.out.println("Failed to load locale specified by mcmmo.properties '"+myLocale+"', defaulting to en_us");
					RESOURCE_BUNDLE = ResourceBundle.getBundle(BUNDLE_NAME, new Locale("en_us"));
				}
			}
			
			String output = RESOURCE_BUNDLE.getString(key);

			if (messageArguments != null) 
			{
				MessageFormat formatter = new MessageFormat("");
				formatter.applyPattern(output);
				output = formatter.format(messageArguments);
			}
			
			output = addColors(output);
			
			return output;
		} catch (MissingResourceException e) {
			return '!' + key + '!';
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
		
		return input;
	}
}
