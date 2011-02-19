package com.gmail.nossr50;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Properties;

public class mcProperties extends Properties{
//private static volatile mcProperties instance;
private String fileName;
public mcProperties(String file) {
	this.fileName = file;
}
public void load() {
	File file = new File(this.fileName);
	if(file.exists()) {
		try  {
			load(new FileInputStream(this.fileName));
		} catch (IOException ex) {
			
		}
	}
}
public void save(String start){
	try{
		store(new FileOutputStream(this.fileName), start);
	} catch (IOException ex) {
		
	}
}
public int getInteger(String key, int value){
	if(containsKey(key)){
		return Integer.parseInt(getProperty(key));
	}
	put(key, String.valueOf(value));
	return value;
}
public String getString(String key, String value){
	if(containsKey(key)){
		return getProperty(key);
	}
	put(key, value);
	return value;
}
public Boolean getBoolean(String key, boolean value) {
	if (containsKey(key)) {
		String boolString = getProperty(key);
		return (boolString.length() > 0)
				&& (boolString.toLowerCase().charAt(0) == 't');
	}
	put(key, value ? "true" : "false");
	return value;
}
public double getDouble(String key, double value) {
	if (containsKey(key)) {
		return Double.parseDouble(getProperty(key));
	}

	put(key, String.valueOf(value));
	return value;
}

}
