package com.gmail.nossr50.commands.hardcore;

import com.gmail.nossr50.mcMMO;
import com.gmail.nossr50.config.Config;
import com.gmail.nossr50.locale.LocaleLoader;
import com.gmail.nossr50.util.Permissions;

public class HardcoreCommand extends HardcoreModeCommand {
    @Override
    protected void disable() {
        Config.getInstance().setHardcoreEnabled(false);
        mcMMO.p.getServer().broadcastMessage(LocaleLoader.getString("Hardcore.Disabled"));
    }

    @Override
    protected void enable() {
        Config.getInstance().setHardcoreEnabled(true);
        mcMMO.p.getServer().broadcastMessage(LocaleLoader.getString("Hardcore.Enabled"));
    }

    @Override
    protected boolean checkTogglePermissions() {
        return Permissions.hardcoreToggle(sender);
    }

    @Override
    protected boolean checkModifyPermissions() {
        return Permissions.hardcoreModify(sender);
    }

    @Override
    protected boolean checkEnabled() {
        return Config.getInstance().getHardcoreEnabled();
    }

    @Override
    protected void modify() {
        Config.getInstance().setHardcoreDeathStatPenaltyPercentage(newPercent);
        sender.sendMessage(LocaleLoader.getString("Hardcore.PercentageChanged", percent.format(newPercent / 100D)));
    }
}