package com.gmail.nossr50.commands.skills;

import com.gmail.nossr50.datatypes.skills.PrimarySkillType;
import com.gmail.nossr50.locale.LocaleLoader;
import com.gmail.nossr50.util.StringUtils;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import java.util.ArrayList;
import java.util.Arrays;

public class SkillGuideCommand implements CommandExecutor {
    private String header;
    private ArrayList<String> guide;

    private String invalidPage = LocaleLoader.getString("Guides.Page.Invalid");

    public SkillGuideCommand(PrimarySkillType skill) {
        header = LocaleLoader.getString("Guides.Header", skill.getName());
        guide = getGuide(skill);
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
        return (int) Math.ceil(guide.size() / 8.0);
    }

    private void sendGuide(CommandSender sender, int pageNumber) {
        for (String target : grabPageContents(pageNumber)) {
            sender.sendMessage(target);
        }
    }

    private ArrayList<String> grabPageContents(int page) {
        int pageIndexStart = 8 * (page - 1); // Determine what string to start at
        ArrayList<String> allStrings = new ArrayList<String>();

        allStrings.add(header);

        // Add targeted strings
        while (allStrings.size() < 9) {
            if (pageIndexStart + allStrings.size() > guide.size()) {
                allStrings.add("");
            }
            else {
                allStrings.add(guide.get(pageIndexStart + (allStrings.size() - 1)));
            }
        }

        allStrings.add("Page " + page + " of " + getTotalPageNumber());
        return allStrings;
    }

    private ArrayList<String> getGuide(PrimarySkillType skill) {
        ArrayList<String> guide = new ArrayList<String>();

        for (int i = 0; i < 10; i++) {
            String[] section = LocaleLoader.getString("Guides." + StringUtils.getCapitalized(skill.toString()) + ".Section." + i).split("\n");

            if (section[0].startsWith("!")) {
                break;
            }

            guide.addAll(Arrays.asList(section));

            if (section.length < 8) {
                for (int blankLine = 8 - section.length; blankLine > 0; blankLine--) {
                    guide.add("");
                }

            }
        }

        return guide;
    }
}
