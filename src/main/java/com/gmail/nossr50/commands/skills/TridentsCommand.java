package com.gmail.nossr50.commands.skills;

import static com.gmail.nossr50.datatypes.skills.SubSkillType.TRIDENTS_IMPALE;
import static com.gmail.nossr50.datatypes.skills.SubSkillType.TRIDENTS_TRIDENTS_LIMIT_BREAK;

import com.gmail.nossr50.datatypes.skills.PrimarySkillType;
import com.gmail.nossr50.util.skills.CombatUtils;
import com.gmail.nossr50.util.skills.SkillUtils;
import com.gmail.nossr50.util.text.TextComponentFactory;
import java.util.ArrayList;
import java.util.List;
import net.kyori.adventure.text.Component;
import org.bukkit.entity.Player;

public class TridentsCommand extends SkillCommand {


    public TridentsCommand() {
        super(PrimarySkillType.TRIDENTS);
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

        if (SkillUtils.canUseSubskill(player, TRIDENTS_TRIDENTS_LIMIT_BREAK)) {
            messages.add(getStatMessage(TRIDENTS_TRIDENTS_LIMIT_BREAK,
                    String.valueOf(CombatUtils.getLimitBreakDamageAgainstQuality(player,
                            TRIDENTS_TRIDENTS_LIMIT_BREAK, 1000))));
        }

        if (SkillUtils.canUseSubskill(player, TRIDENTS_IMPALE)) {
            messages.add(getStatMessage(TRIDENTS_IMPALE,
                    String.valueOf(mmoPlayer.getTridentsManager().impaleDamageBonus())));
        }

        return messages;
    }

    @Override
    protected List<Component> getTextComponents(Player player) {
        List<Component> textComponents = new ArrayList<>();

        TextComponentFactory.appendSubSkillTextComponents(player, textComponents,
                PrimarySkillType.TRIDENTS);

        return textComponents;
    }
}
