package com.gmail.nossr50.commands.skills;

import static com.gmail.nossr50.datatypes.skills.SubSkillType.MACES_CRIPPLE;
import static com.gmail.nossr50.datatypes.skills.SubSkillType.MACES_MACES_LIMIT_BREAK;

import com.gmail.nossr50.datatypes.skills.PrimarySkillType;
import com.gmail.nossr50.datatypes.skills.SubSkillType;
import com.gmail.nossr50.locale.LocaleLoader;
import com.gmail.nossr50.mcMMO;
import com.gmail.nossr50.skills.maces.MacesManager;
import com.gmail.nossr50.util.skills.CombatUtils;
import com.gmail.nossr50.util.skills.RankUtils;
import com.gmail.nossr50.util.skills.SkillUtils;
import com.gmail.nossr50.util.text.TextComponentFactory;
import java.util.ArrayList;
import java.util.List;
import net.kyori.adventure.text.Component;
import org.bukkit.entity.Player;

public class MacesCommand extends SkillCommand {

    public MacesCommand() {
        super(PrimarySkillType.MACES);
    }

    String crippleChanceToApply, crippleChanceToApplyLucky, crippleLengthAgainstPlayers,
            crippleLengthAgainstMobs;

    @Override
    protected void dataCalculations(Player player, float skillValue) {
        if (SkillUtils.canUseSubskill(player, MACES_CRIPPLE)) {
            int crippleRank = RankUtils.getRank(player, MACES_CRIPPLE);
            crippleLengthAgainstPlayers = String.valueOf(
                    MacesManager.getCrippleTickDuration(true) / 20.0D);
            crippleLengthAgainstMobs = String.valueOf(
                    MacesManager.getCrippleTickDuration(false) / 20.0D);
            crippleChanceToApply =
                    mcMMO.p.getAdvancedConfig().getCrippleChanceToApplyOnHit(crippleRank) + "%";
            crippleChanceToApplyLucky = String.valueOf(
                    mcMMO.p.getAdvancedConfig().getCrippleChanceToApplyOnHit(crippleRank) * 1.33);
        }
    }

    @Override
    protected void permissionsCheck(Player player) {
    }

    @Override
    protected List<String> statsDisplay(Player player, float skillValue, boolean hasEndurance,
            boolean isLucky) {
        final List<String> messages = new ArrayList<>();

        if (SkillUtils.canUseSubskill(player, MACES_MACES_LIMIT_BREAK)) {
            messages.add(getStatMessage(MACES_MACES_LIMIT_BREAK,
                    String.valueOf(CombatUtils.getLimitBreakDamageAgainstQuality(player,
                            MACES_MACES_LIMIT_BREAK, 1000))));
        }

        if (SkillUtils.canUseSubskill(player, MACES_CRIPPLE)) {
            messages.add(getStatMessage(MACES_CRIPPLE, crippleChanceToApply)
                    + (isLucky ? LocaleLoader.getString("Perks.Lucky.Bonus",
                    crippleChanceToApplyLucky) : ""));
            messages.add(getStatMessage(true, true, MACES_CRIPPLE,
                    crippleLengthAgainstPlayers,
                    crippleLengthAgainstMobs));
        }

        if (SkillUtils.canUseSubskill(player, SubSkillType.MACES_CRUSH)) {
            messages.add(getStatMessage(SubSkillType.MACES_CRUSH,
                    String.valueOf(mmoPlayer.getMacesManager().getCrushDamage())));
        }

        return messages;
    }

    @Override
    protected List<Component> getTextComponents(Player player) {
        List<Component> textComponents = new ArrayList<>();

        TextComponentFactory.appendSubSkillTextComponents(player, textComponents,
                PrimarySkillType.MACES);

        return textComponents;
    }
}
