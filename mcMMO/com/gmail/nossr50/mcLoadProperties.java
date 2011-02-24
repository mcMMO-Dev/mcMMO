package com.gmail.nossr50;

public class mcLoadProperties {
	public static Boolean pvp, eggs, apples, cake, music, diamond, glowstone, slowsand, sulphur, netherrack, bones, coal, clay, anvilmessages;
	public static String mcmmo, mcc, stats, mmoedit, ptp, party, myspawn, setmyspawn, whois, invite, accept, clearmyspawn;
	public static int xpmodifier;
	
	public static void loadMain(){
    	String propertiesFile = mcMMO.maindirectory + "mcmmo.properties";
    	mcProperties properties = new mcProperties(propertiesFile);
    	properties.load();
    	
    	/*
    	 * EXPERIENCE RATE MODIFIER
    	 */
    	xpmodifier = properties.getInteger("xpmodifier", 2);
    	/*
    	 * TOGGLE CLAY
    	 */
    	clay = properties.getBoolean("clay", true);
    	/*
    	 * ANVIL MESSAGES
    	 */
    	anvilmessages = properties.getBoolean("anvilmessages", true);
    	/*
    	 * EXCAVATION LOOT TOGGLES
    	 */
    	glowstone = properties.getBoolean("glowstone", true);
    	pvp = properties.getBoolean("pvp", true);
    	eggs = properties.getBoolean("eggs", true);
    	apples = properties.getBoolean("apples", true);
    	cake = properties.getBoolean("cake", true);
    	music = properties.getBoolean("music", true);
    	diamond = properties.getBoolean("diamond", true);
    	slowsand = properties.getBoolean("slowsand", true);
    	sulphur = properties.getBoolean("sulphur", true);
    	netherrack = properties.getBoolean("netherrack", true);
    	bones = properties.getBoolean("bones", true);
    	/*
    	 * CUSTOM COMMANDS
    	 */
    	mcmmo = properties.getString("mcmmo", "mcmmo");
    	mcc = properties.getString("mcc", "mcc");
    	stats = properties.getString("stats", "stats");
    	mmoedit = properties.getString("mmoedit", "mmoedit");
    	ptp = properties.getString("ptp", "ptp");
    	party = properties.getString("party", "party");
    	myspawn = properties.getString("myspawn", "myspawn");
    	setmyspawn = properties.getString("setmyspawn", "setmyspawn");
    	whois = properties.getString("whois", "whois");
    	invite = properties.getString("invite", "invite");
    	accept = properties.getString("accept", "accept");
    	clearmyspawn = properties.getString("clearmyspawn", "clearmyspawn");
    	properties.save("==McMMO Configuration==\r\nYou can turn off excavation loot tables by turning the option to false\r\nYou can customize mcMMOs command names by modifying them here as well\r\nThis is an early version of the configuration file, eventually you'll be able to customize messages from mcMMO and XP gains");
    	//herp derp
    }
}