package com.gmail.nossr50.commands.skills;

import com.gmail.nossr50.datatypes.skills.PrimarySkillType;
import com.gmail.nossr50.datatypes.skills.SubSkillType;
import com.gmail.nossr50.util.Permissions;
import com.gmail.nossr50.util.skills.RankUtils;
import com.gmail.nossr50.util.text.TextComponentFactory;
import net.kyori.adventure.text.Component;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class CrossbowsCommand extends SkillCommand {
    private boolean canSSG;

    public CrossbowsCommand() {
        super(PrimarySkillType.CROSSBOWS);
    }

    @Override
    protected void dataCalculations(Player player, float skillValue) {
        // TODO: Implement data calculations
    }

    @Override
    protected void permissionsCheck(Player player) {
        canSSG = RankUtils.hasUnlockedSubskill(player, SubSkillType.CROSSBOWS_SUPER_SHOTGUN)
                && Permissions.superShotgun(player);
    }

    @Override
    protected List<String> statsDisplay(Player player, float skillValue, boolean hasEndurance, boolean isLucky) {
        List<String> messages = new ArrayList<>();

        if (canSSG) {
            //TODO: Implement SSG
        }

        return messages;
    }

    @Override
    protected List<Component> getTextComponents(Player player) {
        List<Component> textComponents = new ArrayList<>();

        TextComponentFactory.getSubSkillTextComponents(player, textComponents, PrimarySkillType.CROSSBOWS);

        return textComponents;
    }
}
