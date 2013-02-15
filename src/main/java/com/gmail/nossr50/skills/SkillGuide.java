package com.gmail.nossr50.skills;

import java.util.ArrayList;

import org.bukkit.entity.Player;

import com.gmail.nossr50.locale.LocaleLoader;
import com.gmail.nossr50.skills.utilities.SkillTools;
import com.gmail.nossr50.skills.utilities.SkillType;
import com.gmail.nossr50.util.StringUtils;

public final class SkillGuide {
    private SkillGuide() {}

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

        allStrings.add(LocaleLoader.getString("Guides.Header", header));

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
        player.sendMessage("\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n");
    }

    public static boolean grabGuidePageForSkill(SkillType skilltype, Player player, String[] args) {
        String skillName = skilltype.toString();
        String capitalized = StringUtils.getCapitalized(skillName);
        String localized = SkillTools.getSkillName(skilltype);
        player.sendMessage(LocaleLoader.getString("Guides.Available", localized, localized.toLowerCase()));

        String address = "Guides." + capitalized;

        switch (args.length) {
        case 0:
            // We have to specify this, otherwise we get the usage string every time we call /skillname...
            return true;

        case 1:
            if (!args[0].equals("?")) {
                return false;
            }

            SkillGuide.clearChat(player);

            for (String target : SkillGuide.grabPageContents(localized, address, 1)) {
                player.sendMessage(target);
            }

            return true;

        case 2:
            int totalPages = SkillGuide.getTotalPageNumber(address);

            if (!StringUtils.isInt(args[1])) {
                player.sendMessage(LocaleLoader.getString("Guides.Page.Invalid"));
                return true;
            }

            if (Integer.parseInt(args[1]) > totalPages) {
                player.sendMessage(LocaleLoader.getString("Guides.Page.OutOfRange", totalPages));
                return true;
            }

            SkillGuide.clearChat(player);

            for (String target : SkillGuide.grabPageContents(localized, address, Integer.parseInt(args[1]))) {
                player.sendMessage(target);
            }

            return true;

        default:
            return false;
        }
    }
}