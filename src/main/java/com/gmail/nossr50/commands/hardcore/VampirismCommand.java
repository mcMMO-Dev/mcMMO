package com.gmail.nossr50.commands.hardcore;

import com.gmail.nossr50.mcMMO;
import com.gmail.nossr50.config.Config;
import com.gmail.nossr50.locale.LocaleLoader;
import com.gmail.nossr50.util.Permissions;

public class VampirismCommand extends HardcoreModeCommand {
    @Override
    protected boolean checkTogglePermissions() {
        return Permissions.vampirismToggle(sender);
    }

    @Override
    protected boolean checkModifyPermissions() {
        return Permissions.vampirismModify(sender);
    }

    @Override
    protected boolean checkEnabled() {
        return Config.getInstance().getHardcoreVampirismEnabled();
    }

    @Override
    protected void enable() {
        Config.getInstance().setHardcoreVampirismEnabled(true);
        mcMMO.p.getServer().broadcastMessage(LocaleLoader.getString("Vampirism.Enabled"));
    }

    @Override
    protected void disable() {
        Config.getInstance().setHardcoreVampirismEnabled(false);
        mcMMO.p.getServer().broadcastMessage(LocaleLoader.getString("Vampirism.Disabled"));
    }

    @Override
    protected void modify() {
        Config.getInstance().setHardcoreVampirismStatLeechPercentage(newPercent);
        sender.sendMessage(LocaleLoader.getString("Vampirism.PercentageChanged", percent.format(newPercent / 100D)));
    }
}