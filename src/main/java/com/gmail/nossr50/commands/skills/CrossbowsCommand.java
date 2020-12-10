package com.gmail.nossr50.commands.skills;

import com.gmail.nossr50.datatypes.skills.PrimarySkillType;
import com.gmail.nossr50.datatypes.skills.SubSkillType;
import com.gmail.nossr50.skills.crossbows.CrossbowManager;
import com.gmail.nossr50.util.TextComponentFactory;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class CrossbowsCommand extends SkillCommand {
    public CrossbowsCommand() {
        super(PrimarySkillType.CROSSBOWS);
    }

    @Override
    protected void dataCalculations(@NotNull McMMOPlayer mmoPlayer, float skillValue) {

    }

    @Override
    protected void permissionsCheck(@NotNull McMMOPlayer mmoPlayer) {

    }

    @Override
    protected @NotNull List<String> statsDisplay(@NotNull McMMOPlayer mmoPlayer, float skillValue, boolean hasEndurance, boolean isLucky) {
        List<String> messages = new ArrayList<>();

        CrossbowManager crossbowManager = mcMMO.getUserManager().getPlayer(player).getCrossbowManager();

        if(canUseSubskill(player, SubSkillType.CROSSBOWS_SUPER_SHOTGUN)) {
            String additionalArrowCount = String.valueOf(crossbowManager.getSuperShotgunAdditionalArrowCount());
            messages.add(getStatMessage(SubSkillType.CROSSBOWS_SUPER_SHOTGUN, additionalArrowCount));
        }

        return messages;
    }

    @Override
    protected List<TextComponent> getTextComponents(Player player) {
        List<TextComponent> textComponents = new ArrayList<>();

        TextComponentFactory.getSubSkillTextComponents(player, textComponents, PrimarySkillType.CROSSBOWS);

        return textComponents;
    }
}
