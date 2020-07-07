package com.gmail.nossr50.commands.skills;

import com.gmail.nossr50.datatypes.skills.PrimarySkillType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.entity.Player;

import java.util.List;

public class CrossbowsCommand extends SkillCommand {
    public CrossbowsCommand() {
        super(PrimarySkillType.CROSSBOWS);
    }

    @Override
    protected void dataCalculations(Player player, float skillValue) {

    }

    @Override
    protected void permissionsCheck(Player player) {

    }

    @Override
    protected List<String> statsDisplay(Player player, float skillValue, boolean hasEndurance, boolean isLucky) {
        return null;
    }

    @Override
    protected List<TextComponent> getTextComponents(Player player) {
        return null;
    }
}
