package com.gmail.nossr50;

public class mcLoadProperties {
	public static Boolean pvp, eggs, apples, cake, music, diamond, glowstone, slowsand, sulphur, netherrack, bones, coal;
	
	public static void loadMain(){
    	String propertiesFile = mcMMO.maindirectory + "mcmmo.properties";
    	mcProperties properties = new mcProperties(propertiesFile);
    	properties.load();
    	
    	//Grab properties stuff here
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
    	properties.save("==McMMO Configuration==");
    	//herp derp
    }
}