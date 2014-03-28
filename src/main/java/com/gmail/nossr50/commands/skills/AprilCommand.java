package com.gmail.nossr50.commands.skills;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;

import com.gmail.nossr50.locale.LocaleLoader;
import com.gmail.nossr50.util.HolidayManager.FakeSkillType;
import com.gmail.nossr50.util.Misc;
import com.gmail.nossr50.util.StringUtils;
import com.gmail.nossr50.util.commands.CommandUtils;

import com.google.common.collect.ImmutableList;

public class AprilCommand implements TabExecutor {
    private String skillName;

    protected DecimalFormat percent = new DecimalFormat("##0.00%");
    protected DecimalFormat decimal = new DecimalFormat("##0.00");

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (CommandUtils.noConsoleUsage(sender)) {
            return true;
        }

        skillName = StringUtils.getCapitalized(label);

        switch (args.length) {
            case 0:
                Player player = (Player) sender;
                FakeSkillType fakeSkillType = FakeSkillType.getByName(skillName);

                float skillValue = Misc.getRandom().nextInt(99);

                player.sendMessage(LocaleLoader.getString("Skills.Header", skillName));
                player.sendMessage(LocaleLoader.getString("Commands.XPGain", getXPGainString(fakeSkillType)));
                player.sendMessage(LocaleLoader.getString("Effects.Level", (int) skillValue, Misc.getRandom().nextInt(1000), 1000 + Misc.getRandom().nextInt(1000)));


                List<String> effectMessages = effectsDisplay(fakeSkillType);

                if (!effectMessages.isEmpty()) {
                    player.sendMessage(LocaleLoader.getString("Skills.Header", LocaleLoader.getString("Effects.Effects")));

                    for (String message : effectMessages) {
                        player.sendMessage(message);
                    }
                }

                List<String> statsMessages = statsDisplay(fakeSkillType);

                if (!statsMessages.isEmpty()) {
                    player.sendMessage(LocaleLoader.getString("Skills.Header", LocaleLoader.getString("Commands.Stats.Self")));

                    for (String message : statsMessages) {
                        player.sendMessage(message);
                    }
                }

                player.sendMessage(LocaleLoader.formatString("[[DARK_AQUA]]Guide for {0} available - type /APRIL FOOLS ! :D", skillName));
                return true;

            default:
                return true;
        }
    }

    private String getXPGainString(FakeSkillType fakeSkillType) {
        switch (fakeSkillType) {
            case MACHO:
                return "Get beaten up";
            case JUMPING:
                return "Kris Kross will make ya Jump Jump";
            case THROWING:
                return "Chuck your items on the floor";
            case WRECKING:
                return "I'M GONNA WRECK IT!";
            case CRAFTING:
                return "Craft apple pies";
            case WALKING:
                return "Walk around the park";
            case SWIMMING:
                return "Like a fish on a bicycle";
            case FALLING:
                return "Faceplant the floor, headbutt the ground";
            case CLIMBING:
                return "Climb the highest mountain";
            case FLYING:
                return "I believe I can fly";
            case DIVING:
                return "Scuba club 4000";
            case PIGGY:
                return "OINK! OINK!";
            default:
                return "Sit and wait?";
        }
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        switch (args.length) {
            case 1:
                return ImmutableList.of("?");
            default:
                return ImmutableList.of();
        }
    }

    private List<String> effectsDisplay(FakeSkillType fakeSkillType) {
        List<String> messages = new ArrayList<String>();

        switch (fakeSkillType) {
            case MACHO:
                messages.add(LocaleLoader.getString("Effects.Template", "Punching bag", "Absorb damage, like a bag of sand"));
                break;
            case JUMPING:
                messages.add(LocaleLoader.getString("Effects.Template", "Jump", "PRESS SPACE TO JUMP"));
                messages.add(LocaleLoader.getString("Effects.Template", "Jump Twice", "PRESS SPACE TWICE TO JUMP TWICE"));
                break;
            case THROWING:
                messages.add(LocaleLoader.getString("Effects.Template", "Drop Item", "Randomly drop items, at random"));
                break;
            case WRECKING:
                messages.add(LocaleLoader.getString("Effects.Template", "Ralphinator", "Smash windows with your fists"));
                break;
            case CRAFTING:
                messages.add(LocaleLoader.getString("Effects.Template", "Crafting", "Chance of successful craft"));
                break;
            case WALKING:
                messages.add(LocaleLoader.getString("Effects.Template", "Walk", "Traveling gracefully by foot"));
                break;
            case SWIMMING:
                messages.add(LocaleLoader.getString("Effects.Template", "Swim", "Just keep swimming, swimming, swimming"));
                break;
            case FALLING:
                messages.add(LocaleLoader.getString("Effects.Template", "Skydiving", "Go jump of a cliff. No, seriously."));
                break;
            case CLIMBING:
                messages.add(LocaleLoader.getString("Effects.Template", "Rock Climber", "Use string to climb mountains faster"));
                break;
            case FLYING:
                messages.add(LocaleLoader.getString("Effects.Template", "Fly", "Throw yourself at the ground and miss"));
                break;
            case DIVING:
                messages.add(LocaleLoader.getString("Effects.Template", "Hold Breath", "Press shift to hold your breath longer"));
                break;
            case PIGGY:
                messages.add(LocaleLoader.getString("Effects.Template", "Carrot Turbo", "Supercharge your pigs with carrots"));
                break;
        }

        return messages;
    }

    private List<String> statsDisplay(FakeSkillType fakeSkillType) {
        List<String> messages = new ArrayList<String>();

        switch (fakeSkillType) {
            case MACHO:
                messages.add(LocaleLoader.formatString("[[RED]]Damage Taken: [[YELLOW]]{0}%", decimal.format(Misc.getRandom().nextInt(77))));
                break;
            case JUMPING:
                messages.add(LocaleLoader.formatString("[[RED]]Double Jump Chance: [[YELLOW]]{0}%", decimal.format(Misc.getRandom().nextInt(27))));
                break;
            case THROWING:
                messages.add(LocaleLoader.formatString("[[RED]]Drop Item Chance: [[YELLOW]]{0}%", decimal.format(Misc.getRandom().nextInt(87))));
                break;
            case WRECKING:
                messages.add(LocaleLoader.formatString("[[RED]]Wrecking Chance: [[YELLOW]]{0}%", decimal.format(Misc.getRandom().nextInt(14))));
                break;
            case CRAFTING:
                messages.add(LocaleLoader.formatString("[[RED]]Crafting Success: [[YELLOW]]{0}%", decimal.format(Misc.getRandom().nextInt(27))));
                break;
            case WALKING:
                messages.add(LocaleLoader.formatString("[[RED]]Walk Chance: [[YELLOW]]{0}%", decimal.format(Misc.getRandom().nextInt(27))));
                break;
            case SWIMMING:
                messages.add(LocaleLoader.formatString("[[RED]]Swim Chance: [[YELLOW]]{0}%", decimal.format(Misc.getRandom().nextInt(27))));
                break;
            case FALLING:
                messages.add(LocaleLoader.formatString("[[RED]]Skydiving Success: [[YELLOW]]{0}%", decimal.format(Misc.getRandom().nextInt(37))));
                break;
            case CLIMBING:
                messages.add(LocaleLoader.formatString("[[RED]]Rock Climber Chance: [[YELLOW]]{0}%", decimal.format(Misc.getRandom().nextInt(27))));
                break;
            case FLYING:
                messages.add(LocaleLoader.formatString("[[RED]]Fly Chance: [[YELLOW]]{0}%", decimal.format(Misc.getRandom().nextInt(27))));
                break;
            case DIVING:
                messages.add(LocaleLoader.formatString("[[RED]]Hold Breath Chance: [[YELLOW]]{0}%", decimal.format(Misc.getRandom().nextInt(27))));
                break;
            case PIGGY:
                messages.add(LocaleLoader.formatString("[[RED]]Carrot Turbo Boost: [[YELLOW]]{0}%", decimal.format(Misc.getRandom().nextInt(80)) + 10));
                break;
        }

        return messages;
    }
}
