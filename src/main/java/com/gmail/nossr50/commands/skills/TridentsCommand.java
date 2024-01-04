package com.gmail.nossr50.commands.skills;

import com.gmail.nossr50.datatypes.skills.PrimarySkillType;
import com.gmail.nossr50.datatypes.skills.SubSkillType;
import com.gmail.nossr50.util.skills.CombatUtils;
import com.gmail.nossr50.util.skills.SkillUtils;
import com.gmail.nossr50.util.text.TextComponentFactory;
import net.kyori.adventure.text.Component;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class TridentsCommand extends SkillCommand {


    public TridentsCommand() {
        super(PrimarySkillType.TRIDENTS);
    }

    @Override
    protected void dataCalculations(Player player, float skillValue) {}

    @Override
    protected void permissionsCheck(Player player) {}

    @Override
    protected List<String> statsDisplay(Player player, float skillValue, boolean hasEndurance, boolean isLucky) {
        List<String> messages = new ArrayList<>();

        if (SkillUtils.canUseSubskill(player, SubSkillType.TRIDENTS_SUPER)) {
            messages.add("Tridents Super Ability");
            //TODO: Implement Tridents Super
        }

        if(SkillUtils.canUseSubskill(player, SubSkillType.TRIDENTS_TRIDENTS_LIMIT_BREAK)) {
            messages.add(getStatMessage(SubSkillType.TRIDENTS_TRIDENTS_LIMIT_BREAK,
                    String.valueOf(CombatUtils.getLimitBreakDamageAgainstQuality(player, SubSkillType.TRIDENTS_TRIDENTS_LIMIT_BREAK, 1000))));
        }

        return messages;
    }

    @Override
    protected List<Component> getTextComponents(Player player) {
        List<Component> textComponents = new ArrayList<>();

        TextComponentFactory.getSubSkillTextComponents(player, textComponents, PrimarySkillType.TRIDENTS);

        return textComponents;
    }
}
