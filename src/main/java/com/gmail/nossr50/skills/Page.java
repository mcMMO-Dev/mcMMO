package com.gmail.nossr50.skills;

import java.util.ArrayList;

import org.bukkit.entity.Player;

import com.gmail.nossr50.locale.LocaleLoader;
import com.gmail.nossr50.util.Misc;

public final class Page {
    private Page() {}

    public static int getTotalPageNumber(String address) {
        String[] addressSplit = LocaleLoader.getString(address).split("\n");

        if (addressSplit.length <= 8) {
            return 1;
        }

        return (addressSplit.length/8)+1;
    }

    public static ArrayList<String> grabPageContents(String header, String address, int pagenum) {
        int pageIndexStart = 0;

        //Determine what string to start at
        if (pagenum > 1) {
            pageIndexStart = 8*(pagenum-1);
        }

        ArrayList<String> allStrings = new ArrayList<String>();
        String split[] = LocaleLoader.getString(address).split("\n");

        allStrings.add(LocaleLoader.getString("Guides.Header", new Object[] {header} ));

        //Add targeted strings
        while (allStrings.size() < 9) {
            if (pageIndexStart+allStrings.size() > split.length) {
                allStrings.add("");
            }
            else {
                allStrings.add(split[pageIndexStart+allStrings.size()-1]);
            }
        }

        allStrings.add("Page "+pagenum+" of "+getTotalPageNumber(address));
        return allStrings;
    }

    public static void clearChat(Player player) {
        for (int x = 0; x < 20; x++) {
            player.sendMessage("");
        }
    }

    public static void grabGuidePageForSkill(SkillType skilltype, Player player, String[] args) {
        String skillName = skilltype.toString();
        String capitalized = Misc.getCapitalized(skillName);
        String localized = SkillTools.localizeSkillName(skilltype);
        player.sendMessage(LocaleLoader.getString("Guides.Available", new Object[] {localized, localized.toLowerCase()} ));

        if (args.length >= 1 && args[0].equals("?")) {
            String address = "Guides." + capitalized;
            int totalPages = Page.getTotalPageNumber(address);

            if (args.length == 1) {
                Page.clearChat(player);

                for (String target : Page.grabPageContents(localized, address, 1)) {
                    player.sendMessage(target);
                }
            }
            else {
                if (!Misc.isInt(args[1])) {
                    player.sendMessage(LocaleLoader.getString("Guides.Page.Invalid"));
                }

                if (Misc.getInt(args[1]) <= totalPages) {
                    Page.clearChat(player);

                    for (String target : Page.grabPageContents(localized, address, Misc.getInt(args[1]))) {
                        player.sendMessage(target);
                    }
                }
                else {
                    player.sendMessage(LocaleLoader.getString("Guides.Page.OutOfRange", new Object[] {totalPages} ));
                }
            }
        }
        else {
            player.sendMessage(LocaleLoader.getString("Guides.Usage", new Object[] {localized.toLowerCase()} ));
        }
    }
}