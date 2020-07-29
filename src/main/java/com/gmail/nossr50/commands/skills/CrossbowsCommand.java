package com.gmail.nossr50.commands.skills;

import com.gmail.nossr50.datatypes.skills.PrimarySkillType;
import com.gmail.nossr50.datatypes.skills.SubSkillType;
import com.gmail.nossr50.skills.crossbows.CrossbowManager;
import com.gmail.nossr50.util.TextComponentFactory;
import com.gmail.nossr50.util.player.UserManager;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.entity.Player;

import java.util.ArrayList;
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
        List<String> messages = new ArrayList<>();

        CrossbowManager crossbowManager = UserManager.getPlayer(player).getCrossbowManager();

        if(canUseSubskill(player, SubSkillType.CROSSBOWS_SUPER_SHOTGUN)) {
            String additionalArrowCount = String.valueOf(crossbowManager.getSuperShotgunAdditionalArrowCount());
            messages.add(getStatMessage(SubSkillType.CROSSBOWS_CROSSBOWS_LIMIT_BREAK, additionalArrowCount));
        }

        return messages;
    }

    @Override
    protected List<TextComponent> getTextComponents(Player player) {
        List<TextComponent> textComponents = new ArrayList<>();

        TextComponentFactory.getSubSkillTextComponents(player, textComponents, PrimarySkillType.AXES);

        return textComponents;
    }
}
