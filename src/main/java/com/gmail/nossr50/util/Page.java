package com.gmail.nossr50.util;

import java.util.ArrayList;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import com.gmail.nossr50.locale.mcLocale;

public class Page {
    public static ArrayList<String> grabPageContents(String header, String address, int pagenum)
    {
        int pageIndexStart = 0;
        
        //Determine what string to start at
        if(pagenum > 1)
        {
            pageIndexStart = 8*(pagenum-1);
        }
        
        ArrayList<String> allStrings = new ArrayList<String>();
        String[] addressSplit = mcLocale.getString(address).split("\n");
        
        allStrings.add(ChatColor.GOLD+"-="+ChatColor.GREEN+header+ChatColor.GOLD+"=-"); //So stylish
        
        //Add targeted strings
        while(allStrings.size() < 9)
        {
            allStrings.add(addressSplit[pageIndexStart+allStrings.size()-1]);
        }
        
        int totalPages = 1;
        
        if(addressSplit.length >= 8)
        {
            totalPages = (addressSplit.length/8)+1;
        }
        
        allStrings.add("Page "+pagenum+" of "+totalPages);
        return allStrings;
    }
    
    public static void clearChat(Player player)
    {
        //Extremely ugly way of making the screen clear up
        int x = 0;
        while(x < 20)
        {
            player.sendMessage("");
            x++;
        }
    }
}
