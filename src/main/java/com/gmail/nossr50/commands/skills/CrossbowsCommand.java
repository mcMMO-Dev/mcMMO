package com.gmail.nossr50.commands.skills;

import static com.gmail.nossr50.datatypes.skills.SubSkillType.CROSSBOWS_CROSSBOWS_LIMIT_BREAK;
import static com.gmail.nossr50.datatypes.skills.SubSkillType.CROSSBOWS_POWERED_SHOT;
import static com.gmail.nossr50.datatypes.skills.SubSkillType.CROSSBOWS_TRICK_SHOT;

import com.gmail.nossr50.datatypes.skills.PrimarySkillType;
import com.gmail.nossr50.util.Permissions;
import com.gmail.nossr50.util.skills.CombatUtils;
import com.gmail.nossr50.util.skills.RankUtils;
import com.gmail.nossr50.util.text.TextComponentFactory;
import java.util.ArrayList;
import java.util.List;
import net.kyori.adventure.text.Component;
import org.bukkit.entity.Player;

public class CrossbowsCommand extends SkillCommand {
    private boolean canTrickShot;
    private boolean canPoweredShot;

    public CrossbowsCommand() {
        super(PrimarySkillType.CROSSBOWS);
    }

    @Override
    protected void dataCalculations(Player player, float skillValue) {
        // TODO: Implement data calculations
    }

    @Override
    protected void permissionsCheck(Player player) {
        canTrickShot = RankUtils.hasUnlockedSubskill(player, CROSSBOWS_TRICK_SHOT)
                && Permissions.trickShot(player);

        canPoweredShot = RankUtils.hasUnlockedSubskill(player, CROSSBOWS_POWERED_SHOT)
                && Permissions.poweredShot(player);
    }

    @Override
    protected List<String> statsDisplay(Player player, float skillValue, boolean hasEndurance,
            boolean isLucky) {
        List<String> messages = new ArrayList<>();

        if (mmoPlayer == null) {
            return messages;
        }

        if (canPoweredShot) {
            messages.add(getStatMessage(CROSSBOWS_POWERED_SHOT,
                    percent.format(mmoPlayer.getCrossbowsManager().getDamageBonusPercent(player))));
        }

        if (canTrickShot) {
            messages.add(getStatMessage(CROSSBOWS_TRICK_SHOT,
                    String.valueOf(mmoPlayer.getCrossbowsManager().getTrickShotMaxBounceCount())));
        }

        if (Permissions.canUseSubSkill(player, CROSSBOWS_CROSSBOWS_LIMIT_BREAK)) {
            messages.add(getStatMessage(CROSSBOWS_CROSSBOWS_LIMIT_BREAK,
                    String.valueOf(CombatUtils.getLimitBreakDamageAgainstQuality(player,
                            CROSSBOWS_CROSSBOWS_LIMIT_BREAK, 1000))));
        }

        return messages;
    }

    @Override
    protected List<Component> getTextComponents(Player player) {
        List<Component> textComponents = new ArrayList<>();

        TextComponentFactory.appendSubSkillTextComponents(player, textComponents,
                PrimarySkillType.CROSSBOWS);

        return textComponents;
    }
}
