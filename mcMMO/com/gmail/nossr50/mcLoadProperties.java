package com.gmail.nossr50;

public class mcLoadProperties {
	public static Boolean pvp, eggs, apples, myspawnclearsinventory, cake, music, diamond, glowstone, slowsand, sulphur, netherrack, bones, coal, clay, anvilmessages;
	public static String mcmmo, mcc, mcgod, stats, mmoedit, ptp, party, myspawn, setmyspawn, whois, invite, accept, clearmyspawn;
	public static int repairdiamondlevel, globalxpmodifier, miningxpmodifier, repairxpmodifier, woodcuttingxpmodifier, unarmedxpmodifier, herbalismxpmodifier, excavationxpmodifier, archeryxpmodifier, swordsxpmodifier, axesxpmodifier, acrobaticsxpmodifier;
	
	public static void loadMain(){
    	String propertiesFile = mcMMO.maindirectory + "mcmmo.properties";
    	mcProperties properties = new mcProperties(propertiesFile);
    	properties.load();
    	
    	repairdiamondlevel = properties.getInteger("repairdiamondlevel", 50);
    	/*
    	 * EXPERIENCE RATE MODIFIER
    	 */
    	globalxpmodifier = properties.getInteger("globalxpmodifier", 1);
    	miningxpmodifier = properties.getInteger("miningxpmodifier", 2);
    	repairxpmodifier = properties.getInteger("repairxpmodifier", 2);
    	woodcuttingxpmodifier = properties.getInteger("woodcuttingxpmodifier", 2);
    	unarmedxpmodifier = properties.getInteger("unarmedxpmodifier", 2);
    	herbalismxpmodifier = properties.getInteger("herbalismxpmodifier", 2);
    	excavationxpmodifier = properties.getInteger("excavationxpmodifier", 2);
    	archeryxpmodifier = properties.getInteger("archeryxpmodifier", 2);
    	swordsxpmodifier = properties.getInteger("swordsxpmodifier", 2);
    	axesxpmodifier = properties.getInteger("axesxpmodifier", 2);
    	acrobaticsxpmodifier = properties.getInteger("acrobaticsxpmodifier", 2);
    	/*
    	 * TOGGLE CLAY
    	 */
    	clay = properties.getBoolean("graveltoclay", true);
    	/*
    	 * ANVIL MESSAGES
    	 */
    	anvilmessages = properties.getBoolean("anvilmessages", true);
    	/*
    	 * EXCAVATION LOOT TOGGLES
    	 */
    	myspawnclearsinventory = properties.getBoolean("myspawnclearsinventory", true);
    	glowstone = properties.getBoolean("canexcavateglowstone", true);
    	pvp = properties.getBoolean("pvp", true);
    	eggs = properties.getBoolean("canexcavateeggs", true);
    	apples = properties.getBoolean("canexcavateapples", true);
    	cake = properties.getBoolean("canexcavatecake", true);
    	music = properties.getBoolean("canexcavatemusic", true);
    	diamond = properties.getBoolean("canexcavatediamond", true);
    	slowsand = properties.getBoolean("canexcavateslowsand", true);
    	sulphur = properties.getBoolean("canexcavatesulphur", true);
    	netherrack = properties.getBoolean("canexcavatenetherrack", true);
    	bones = properties.getBoolean("canexcavatebones", true);
    	/*
    	 * CUSTOM COMMANDS
    	 */
    	mcmmo = properties.getString("/mcmmo", "mcmmo");
    	mcc = properties.getString("/mcc", "mcc");
    	mcgod = properties.getString("/mcgod", "mcgod");
    	stats = properties.getString("/stats", "stats");
    	mmoedit = properties.getString("/mmoedit", "mmoedit");
    	ptp = properties.getString("/ptp", "ptp");
    	party = properties.getString("/party", "party");
    	myspawn = properties.getString("/myspawn", "myspawn");
    	setmyspawn = properties.getString("/setmyspawn", "setmyspawn");
    	whois = properties.getString("/whois", "whois");
    	invite = properties.getString("/invite", "invite");
    	accept = properties.getString("/accept", "accept");
    	clearmyspawn = properties.getString("/clearmyspawn", "clearmyspawn");
    	properties.save("==McMMO Configuration==\r\nYou can turn off excavation loot tables by turning the option to false\r\nYou can customize mcMMOs command names by modifying them here as well\r\nThis is an early version of the configuration file, eventually you'll be able to customize messages from mcMMO and XP gains");
    	//herp derp
    }
}