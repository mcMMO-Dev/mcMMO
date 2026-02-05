package com.gmail.nossr50.commands.skills;


import static com.gmail.nossr50.datatypes.skills.SubSkillType.SPEARS_MOMENTUM;
import static com.gmail.nossr50.datatypes.skills.SubSkillType.SPEARS_SPEARS_LIMIT_BREAK;
import static com.gmail.nossr50.datatypes.skills.SubSkillType.SPEARS_SPEAR_MASTERY;
import static com.gmail.nossr50.util.skills.SkillUtils.canUseSubskill;
import static com.gmail.nossr50.util.text.TextComponentFactory.appendSubSkillTextComponents;

import com.gmail.nossr50.datatypes.skills.PrimarySkillType;
import com.gmail.nossr50.locale.LocaleLoader;
import com.gmail.nossr50.mcMMO;
import com.gmail.nossr50.skills.spears.SpearsManager;
import com.gmail.nossr50.util.player.UserManager;
import com.gmail.nossr50.util.skills.CombatUtils;
import com.gmail.nossr50.util.skills.RankUtils;
import com.gmail.nossr50.util.skills.SkillUtils;
import java.util.ArrayList;
import java.util.List;
import net.kyori.adventure.text.Component;
import org.bukkit.entity.Player;

public class SpearsCommand extends SkillCommand {

    public SpearsCommand() {
        super(PrimarySkillType.SPEARS);
    }

    String momentumChanceToApply, momentumChanceToApplyLucky, momentumDuration;

    @Override
    protected void dataCalculations(Player player, float skillValue) {
        if (SkillUtils.canUseSubskill(player, SPEARS_MOMENTUM)) {
            int momentumRank = RankUtils.getRank(player, SPEARS_MOMENTUM);
            momentumDuration = String.valueOf(
                    SpearsManager.getMomentumTickDuration(momentumRank) / 20.0D);
            momentumChanceToApply =
                    mcMMO.p.getAdvancedConfig().getMomentumChanceToApplyOnHit(momentumRank) + "%";
            momentumChanceToApplyLucky = String.valueOf(
                    mcMMO.p.getAdvancedConfig().getMomentumChanceToApplyOnHit(momentumRank) * 1.33);
        }
    }

    @Override
    protected void permissionsCheck(Player player) {
    }

    @Override
    protected List<String> statsDisplay(Player player, float skillValue, boolean hasEndurance,
            boolean isLucky) {
        final SpearsManager spearsManager = UserManager.getPlayer(player).getSpearsManager();
        final double spearMasteryBonusDmg = spearsManager.getSpearMasteryBonusDamage();

        List<String> messages = new ArrayList<>();

        if (canUseSubskill(player, SPEARS_SPEARS_LIMIT_BREAK)) {
            messages.add(getStatMessage(SPEARS_SPEARS_LIMIT_BREAK,
                    String.valueOf(CombatUtils.getLimitBreakDamageAgainstQuality(player,
                            SPEARS_SPEARS_LIMIT_BREAK, 1000))));
        }

        if (canUseSubskill(player, SPEARS_SPEAR_MASTERY)) {
            messages.add(getStatMessage(SPEARS_SPEAR_MASTERY,
                    String.valueOf(spearMasteryBonusDmg)));
        }

        if (SkillUtils.canUseSubskill(player, SPEARS_MOMENTUM)) {
            messages.add(getStatMessage(SPEARS_MOMENTUM, momentumChanceToApply)
                    + (isLucky ? LocaleLoader.getString("Perks.Lucky.Bonus",
                    momentumChanceToApplyLucky) : ""));
            messages.add(getStatMessage(true, true, SPEARS_MOMENTUM, momentumDuration));
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
