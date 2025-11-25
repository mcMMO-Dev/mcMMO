package com.gmail.nossr50.commands.skills;


import static com.gmail.nossr50.datatypes.skills.SubSkillType.SPEARS_SPEARS_LIMIT_BREAK;
import static com.gmail.nossr50.util.text.TextComponentFactory.appendSubSkillTextComponents;

import com.gmail.nossr50.datatypes.skills.PrimarySkillType;
import com.gmail.nossr50.util.skills.CombatUtils;
import com.gmail.nossr50.util.skills.SkillUtils;
import java.util.ArrayList;
import java.util.List;
import net.kyori.adventure.text.Component;
import org.bukkit.entity.Player;

public class SpearsCommand extends SkillCommand {


    public SpearsCommand() {
        super(PrimarySkillType.SPEARS);
    }

    @Override
    protected void dataCalculations(Player player, float skillValue) {
    }

    @Override
    protected void permissionsCheck(Player player) {
    }

    @Override
    protected List<String> statsDisplay(Player player, float skillValue, boolean hasEndurance,
            boolean isLucky) {
        List<String> messages = new ArrayList<>();

        if (SkillUtils.canUseSubskill(player, SPEARS_SPEARS_LIMIT_BREAK)) {
            messages.add(getStatMessage(SPEARS_SPEARS_LIMIT_BREAK,
                    String.valueOf(CombatUtils.getLimitBreakDamageAgainstQuality(player,
                            SPEARS_SPEARS_LIMIT_BREAK, 1000))));
        }

        return messages;
    }

    @Override
    protected List<Component> getTextComponents(Player player) {
        List<Component> textComponents = new ArrayList<>();

        appendSubSkillTextComponents(player, textComponents, PrimarySkillType.SPEARS);

        return textComponents;
    }
}
