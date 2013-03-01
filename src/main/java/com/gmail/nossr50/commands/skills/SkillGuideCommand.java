package com.gmail.nossr50.commands.skills;

import java.util.ArrayList;

import org.bukkit.entity.Player;

import com.gmail.nossr50.datatypes.skills.SkillType;
import com.gmail.nossr50.locale.LocaleLoader;
import com.gmail.nossr50.util.StringUtils;
import com.gmail.nossr50.util.skills.SkillUtils;

public final class SkillGuideCommand {
    private SkillGuideCommand() {}

    public static int getTotalPageNumber(String address) {
        String[] addressSplit = LocaleLoader.getString(address).split("\n");

        if (addressSplit.length <= 8) {
            return 1;
        }

        return (addressSplit.length / 8) + 1;
    }

    public static ArrayList<String> grabPageContents(String header, String address, int pagenum) {
        int pageIndexStart = 0;

        // Determine what string to start at
        if (pagenum > 1) {
            pageIndexStart = 8 * (pagenum - 1);
        }

        ArrayList<String> allStrings = new ArrayList<String>();
        String split[] = LocaleLoader.getString(address).split("\n");

        allStrings.add(LocaleLoader.getString("Guides.Header", header));

        // Add targeted strings
        while (allStrings.size() < 9) {
            if (pageIndexStart + allStrings.size() > split.length) {
                allStrings.add("");
            }
            else {
                allStrings.add(split[pageIndexStart + allStrings.size() - 1]);
            }
        }

        allStrings.add("Page " + pagenum + " of " + getTotalPageNumber(address));
        return allStrings;
    }

    public static void clearChat(Player player) {
        player.sendMessage("\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n"); // Dear god why?
    }

    public static boolean grabGuidePageForSkill(SkillType skilltype, Player player, String[] args) {
        String skillName = skilltype.toString();
        String capitalized = StringUtils.getCapitalized(skillName);
        String localized = SkillUtils.getSkillName(skilltype);
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

                SkillGuideCommand.clearChat(player);

                for (String target : SkillGuideCommand.grabPageContents(localized, address, 1)) {
                    player.sendMessage(target);
                }

                return true;

            case 2:
                int totalPages = SkillGuideCommand.getTotalPageNumber(address);

                if (!StringUtils.isInt(args[1])) {
                    player.sendMessage(LocaleLoader.getString("Guides.Page.Invalid"));
                    return true;
                }

                if (Integer.parseInt(args[1]) > totalPages) {
                    player.sendMessage(LocaleLoader.getString("Guides.Page.OutOfRange", totalPages));
                    return true;
                }

                SkillGuideCommand.clearChat(player);

                for (String target : SkillGuideCommand.grabPageContents(localized, address, Integer.parseInt(args[1]))) {
                    player.sendMessage(target);
                }

                return true;

            default:
                return false;
        }
    }
}
