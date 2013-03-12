package com.gmail.nossr50.commands.skills;

import java.util.ArrayList;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import com.gmail.nossr50.datatypes.skills.SkillType;
import com.gmail.nossr50.locale.LocaleLoader;
import com.gmail.nossr50.util.StringUtils;
import com.gmail.nossr50.util.skills.SkillUtils;

public class SkillGuideCommand implements CommandExecutor {
    private String header;
    private String[] guide;

    private String invalidPage;

    public SkillGuideCommand(SkillType skillType) {
        header = LocaleLoader.getString("Guides.Header", SkillUtils.getSkillName(skillType));
        guide = LocaleLoader.getString("Guides." + StringUtils.getCapitalized(skillType.toString())).split("\n");

        invalidPage = LocaleLoader.getString("Guides.Page.Invalid");
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        switch (args.length) {
            case 1:
                if (!args[0].equals("?")) {
                    return false;
                }

                sendGuide(sender, 1);
                return true;

            case 2:
                int totalPages = getTotalPageNumber();

                if (!StringUtils.isInt(args[1])) {
                    sender.sendMessage(invalidPage);
                    return true;
                }

                int pageNumber = Integer.parseInt(args[1]);

                if (pageNumber > totalPages || pageNumber <= 0) {
                    sender.sendMessage(LocaleLoader.getString("Guides.Page.OutOfRange", totalPages));
                    return true;
                }

                sendGuide(sender, pageNumber);
                return true;

            default:
                return false;
        }
    }

    private int getTotalPageNumber() {
        return (int) Math.ceil(guide.length / 8.0);
    }

    private void sendGuide(CommandSender sender, int pageNumber) {
        for (String target : grabPageContents(pageNumber)) {
            sender.sendMessage(target);
        }
    }

    private ArrayList<String> grabPageContents(int pagenum) {
        int pageIndexStart = 8 * (pagenum - 1); // Determine what string to start at
        ArrayList<String> allStrings = new ArrayList<String>();

        allStrings.add(header);

        // Add targeted strings
        while (allStrings.size() < 9) {
            if (pageIndexStart + allStrings.size() > guide.length) {
                allStrings.add("");
            }
            else {
                allStrings.add(guide[pageIndexStart + (allStrings.size() - 1)]);
            }
        }

        allStrings.add("Page " + pagenum + " of " + getTotalPageNumber());
        return allStrings;
    }
}
