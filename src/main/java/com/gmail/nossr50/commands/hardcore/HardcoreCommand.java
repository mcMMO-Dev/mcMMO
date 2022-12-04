//package com.gmail.nossr50.commands.hardcore;
//
//import com.gmail.nossr50.config.Config;
//import com.gmail.nossr50.datatypes.skills.PrimarySkillType;
//import com.gmail.nossr50.locale.LocaleLoader;
//import com.gmail.nossr50.mcMMO;
//import com.gmail.nossr50.util.Permissions;
//import org.bukkit.command.CommandSender;
//
//public class HardcoreCommand extends HardcoreModeCommand {
//    @Override
//    protected boolean checkTogglePermissions(CommandSender sender) {
//        return Permissions.hardcoreToggle(sender);
//    }
//
//    @Override
//    protected boolean checkModifyPermissions(CommandSender sender) {
//        return Permissions.hardcoreModify(sender);
//    }
//
//    @Override
//    protected boolean checkEnabled(PrimarySkillType skill) {
//        if (skill == null) {
//            for (PrimarySkillType primarySkillType : PrimarySkillType.values()) {
//                if (!primarySkillType.getHardcoreStatLossEnabled()) {
//                    return false;
//                }
//            }
//
//            return true;
//        }
//
//        return skill.getHardcoreStatLossEnabled();
//    }
//
//    @Override
//    protected void enable(PrimarySkillType skill) {
//        toggle(true, skill);
//    }
//
//    @Override
//    protected void disable(PrimarySkillType skill) {
//        toggle(false, skill);
//    }
//
//    @Override
//    protected void modify(CommandSender sender, double newPercentage) {
//        Config.getInstance().setHardcoreDeathStatPenaltyPercentage(newPercentage);
//        sender.sendMessage(LocaleLoader.getString("Hardcore.DeathStatLoss.PercentageChanged", percent.format(newPercentage / 100.0D)));
//    }
//
//    private void toggle(boolean enable, PrimarySkillType skill) {
//        if (skill == null) {
//            for (PrimarySkillType primarySkillType : SkillTools.NON_CHILD_SKILLS) {
//                primarySkillType.setHardcoreStatLossEnabled(enable);
//            }
//        }
//        else {
//            skill.setHardcoreStatLossEnabled(enable);
//        }
//
//        mcMMO.p.getServer().broadcastMessage(LocaleLoader.getString("Hardcore.Mode." + (enable ? "Enabled" : "Disabled"), LocaleLoader.getString("Hardcore.DeathStatLoss.Name"), (skill == null ? "all skills" : mcMMO.p.getSkillTools().getLocalizedSkillName(skill))));
//    }
//}