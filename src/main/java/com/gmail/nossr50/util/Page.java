package com.gmail.nossr50.util;

import java.util.ArrayList;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import com.gmail.nossr50.m;
import com.gmail.nossr50.datatypes.SkillType;
import com.gmail.nossr50.locale.mcLocale;

public class Page {
    
    public static int getTotalPageNumber(String address)
    {
        String[] addressSplit = mcLocale.getString(address).split("\n");
        if(addressSplit.length < 8)
            return 1;
        else
            return (addressSplit.length/8)+1;
    }
    
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
            if(pageIndexStart+allStrings.size() > addressSplit.length)
                allStrings.add("");
            else
                allStrings.add(addressSplit[pageIndexStart+allStrings.size()-1]);
        }
        
        allStrings.add("Page "+pagenum+" of "+getTotalPageNumber(address));
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
    
    public static void grabGuidePageForSkill(SkillType skilltype, Player player, String[] args)
    {
        if (args.length >= 1)
        {
            if(args[0].equals("?"))
            {
                String capitalized = m.getCapitalized(skilltype.toString());
                String address = "Guides."+capitalized;
                
                Page.clearChat(player);
                if(args.length > 1 && m.isInt(args[1]) && m.getInt(args[1]) <= Page.getTotalPageNumber(address))
                {
                    for(String target : Page.grabPageContents(capitalized+" Guide", address, m.getInt(args[1])))
                    {
                        player.sendMessage(target);
                    }
                } else if(args.length > 1 && m.getInt(args[1]) > Page.getTotalPageNumber(address)) 
                {
                    player.sendMessage("That page doesn't exist, there are only "+Page.getTotalPageNumber(address)+" total pages");
                }
                else if(args.length > 1 && !m.isInt(args[1]))
                {
                    player.sendMessage("Not a valid page number!");
                }
            }
            else
            {
                player.sendMessage(ChatColor.RED + "Usage is /"+skilltype.toString().toLowerCase()+" ? [page]");
            }
        }
    }
}