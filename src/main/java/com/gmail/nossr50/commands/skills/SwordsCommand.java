package com.gmail.nossr50.commands.skills;

import com.gmail.nossr50.datatypes.skills.PrimarySkillType;
import com.gmail.nossr50.datatypes.skills.SubSkillType;
import com.gmail.nossr50.locale.LocaleLoader;
import com.gmail.nossr50.mcMMO;
import com.gmail.nossr50.util.Permissions;
import com.gmail.nossr50.util.random.ProbabilityUtil;
import com.gmail.nossr50.util.skills.CombatUtils;
import com.gmail.nossr50.util.skills.RankUtils;
import com.gmail.nossr50.util.skills.SkillUtils;
import com.gmail.nossr50.util.text.TextComponentFactory;
import java.util.ArrayList;
import java.util.List;
import net.kyori.adventure.text.Component;
import org.bukkit.entity.Player;

public class SwordsCommand extends SkillCommand {
    private String counterChance;
    private String counterChanceLucky;
    private String serratedStrikesLength;
    private String serratedStrikesLengthEndurance;

    private String rupturePureTickDamageAgainstPlayers, rupturePureTickDamageAgainstMobs,
            ruptureLengthSecondsAgainstPlayers, ruptureLengthSecondsAgainstMobs,
            ruptureChanceToApply, ruptureChanceToApplyLucky;

    private boolean canCounter;
    private boolean canSerratedStrike;
    private boolean canRupture;

    public SwordsCommand() {
        super(PrimarySkillType.SWORDS);
    }

    @Override
    protected void dataCalculations(Player player, float skillValue) {
        // SWORDS_COUNTER_ATTACK
        if (canCounter) {
            String[] counterStrings = ProbabilityUtil.getRNGDisplayValues(mmoPlayer,
                    SubSkillType.SWORDS_COUNTER_ATTACK);
            counterChance = counterStrings[0];
            counterChanceLucky = counterStrings[1];
        }

        // SWORDS_RUPTURE
        if (canRupture) {
            int ruptureRank = RankUtils.getRank(player, SubSkillType.SWORDS_RUPTURE);
            ruptureLengthSecondsAgainstPlayers = String.valueOf(
                    mcMMO.p.getAdvancedConfig().getRuptureDurationSeconds(true));
            ruptureLengthSecondsAgainstMobs = String.valueOf(
                    mcMMO.p.getAdvancedConfig().getRuptureDurationSeconds(false));

            rupturePureTickDamageAgainstPlayers = String.valueOf(
                    mcMMO.p.getAdvancedConfig().getRuptureTickDamage(true, ruptureRank));
            rupturePureTickDamageAgainstMobs = String.valueOf(
                    mcMMO.p.getAdvancedConfig().getRuptureTickDamage(false, ruptureRank));

            ruptureChanceToApply =
                    mcMMO.p.getAdvancedConfig().getRuptureChanceToApplyOnHit(ruptureRank) + "%";
            ruptureChanceToApplyLucky = String.valueOf(
                    mcMMO.p.getAdvancedConfig().getRuptureChanceToApplyOnHit(ruptureRank) * 1.33);
        }

        // SERRATED STRIKES
        if (canSerratedStrike) {
            String[] serratedStrikesStrings = calculateLengthDisplayValues(player, skillValue);
            serratedStrikesLength = serratedStrikesStrings[0];
            serratedStrikesLengthEndurance = serratedStrikesStrings[1];
        }
    }

    @Override
    protected void permissionsCheck(Player player) {
        canRupture = SkillUtils.canUseSubskill(player, SubSkillType.SWORDS_RUPTURE);
        canCounter = SkillUtils.canUseSubskill(player, SubSkillType.SWORDS_COUNTER_ATTACK);
        canSerratedStrike =
                RankUtils.hasUnlockedSubskill(player, SubSkillType.SWORDS_SERRATED_STRIKES)
                        && Permissions.serratedStrikes(player);
    }

    @Override
    protected List<String> statsDisplay(Player player, float skillValue, boolean hasEndurance,
            boolean isLucky) {
        List<String> messages = new ArrayList<>();

        if (canCounter) {
            messages.add(getStatMessage(SubSkillType.SWORDS_COUNTER_ATTACK, counterChance)
                    + (isLucky ? LocaleLoader.getString("Perks.Lucky.Bonus", counterChanceLucky)
                    : ""));
        }

        if (canRupture) {
            messages.add(getStatMessage(SubSkillType.SWORDS_RUPTURE, ruptureChanceToApply)
                    + (isLucky ? LocaleLoader.getString("Perks.Lucky.Bonus",
                    ruptureChanceToApplyLucky) : ""));
            messages.add(getStatMessage(true, true, SubSkillType.SWORDS_RUPTURE,
                    ruptureLengthSecondsAgainstPlayers,
                    ruptureLengthSecondsAgainstMobs));

            messages.add(LocaleLoader.getString("Swords.SubSkill.Rupture.Stat.TickDamage",
                    rupturePureTickDamageAgainstPlayers, rupturePureTickDamageAgainstMobs));

            messages.add(LocaleLoader.getString("Swords.Combat.Rupture.Note.Update.One"));
        }

        if (canSerratedStrike) {
            messages.add(getStatMessage(SubSkillType.SWORDS_SERRATED_STRIKES, serratedStrikesLength)
                    + (hasEndurance ? LocaleLoader.getString("Perks.ActivationTime.Bonus",
                    serratedStrikesLengthEndurance) : ""));
        }

        if (SkillUtils.canUseSubskill(player, SubSkillType.SWORDS_STAB)) {
            messages.add(getStatMessage(SubSkillType.SWORDS_STAB,
                    String.valueOf(mmoPlayer.getSwordsManager().getStabDamage())));
        }

        if (SkillUtils.canUseSubskill(player, SubSkillType.SWORDS_SWORDS_LIMIT_BREAK)) {
            messages.add(getStatMessage(SubSkillType.SWORDS_SWORDS_LIMIT_BREAK,
                    String.valueOf(CombatUtils.getLimitBreakDamageAgainstQuality(player,
                            SubSkillType.SWORDS_SWORDS_LIMIT_BREAK, 1000))));
        }

        return messages;
    }

    @Override
    protected List<Component> getTextComponents(Player player) {
        List<Component> textComponents = new ArrayList<>();

        TextComponentFactory.appendSubSkillTextComponents(player, textComponents,
                PrimarySkillType.SWORDS);

        return textComponents;
    }
}
