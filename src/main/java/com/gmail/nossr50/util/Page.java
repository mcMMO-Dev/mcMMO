package com.gmail.nossr50.util;

import java.util.ArrayList;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import com.gmail.nossr50.datatypes.SkillType;
import com.gmail.nossr50.locale.LocaleLoader;

public class Page {

    public static int getTotalPageNumber(String address)
    {
        String[] addressSplit = LocaleLoader.getString(address).split("\n");
        if(addressSplit.length <= 8)
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
        String split[] = LocaleLoader.getString(address).split("\n");

        allStrings.add(ChatColor.GOLD+"-="+ChatColor.GREEN+header+ChatColor.GOLD+"=-"); //So stylish

        //Add targeted strings

        while(allStrings.size() < 9)
        {
            if(pageIndexStart+allStrings.size() > split.length)
                allStrings.add("");
            else
                allStrings.add(split[pageIndexStart+allStrings.size()-1]);
        }

        allStrings.add("Page "+pagenum+" of "+getTotalPageNumber(address));
        return allStrings;
    }

    public static void clearChat(Player player)
    {
        for(int x = 0; x < 20; x++)
        {
            player.sendMessage("");
        }
    }

    public static void grabGuidePageForSkill(SkillType skilltype, Player player, String[] args)
    {
        String capitalized = Misc.getCapitalized(skilltype.toString());
        player.sendMessage(ChatColor.DARK_AQUA+"Guide for "+capitalized+" available type /"+skilltype.toString().toLowerCase()+" ? [Page#]"); //TODO: Needs more locale.
        if (args.length >= 1)
        {
            if(args[0].equals("?"))
            {

                String address = "Guides."+capitalized;

                if(args.length > 1 && Misc.isInt(args[1]) && Misc.getInt(args[1]) <= Page.getTotalPageNumber(address))
                {
                    Page.clearChat(player);
                    for(String target : Page.grabPageContents(capitalized+" Guide", address, Misc.getInt(args[1])))
                    {
                        player.sendMessage(target);
                    }
                } else if(args.length == 1)
                {
                    Page.clearChat(player);
                    for(String target : Page.grabPageContents(capitalized+" Guide", address, 1))
                    {
                        player.sendMessage(target);
                    }
                }
                else if(args.length > 1 && Misc.getInt(args[1]) > Page.getTotalPageNumber(address))
                {
                    player.sendMessage("That page doesn't exist, there are only "+Page.getTotalPageNumber(address)+" total pages"); //TODO: Needs more locale.
                }
                else if(args.length > 1 && !Misc.isInt(args[1]))
                {
                    player.sendMessage("Not a valid page number!"); //TODO: Needs more locale.
                }
            }
            else
            {
                player.sendMessage(ChatColor.RED + "Usage is /"+skilltype.toString().toLowerCase()+" ? [page]"); //TODO: Needs more locale.
            }
        }
    }
}