package com.gmail.nossr50.commands.skills;

import com.gmail.nossr50.datatypes.skills.PrimarySkillType;
import com.gmail.nossr50.datatypes.skills.SubSkillType;
import com.gmail.nossr50.locale.LocaleLoader;
import com.gmail.nossr50.skills.archery.Archery;
import com.gmail.nossr50.util.skills.CombatUtils;
import com.gmail.nossr50.util.skills.SkillActivationType;
import com.gmail.nossr50.util.text.TextComponentFactory;
import net.kyori.adventure.text.Component;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class ArcheryCommand extends SkillCommand {
    private String skillShotBonus;
    private String dazeChance;
    private String dazeChanceLucky;
    private String retrieveChance;
    private String retrieveChanceLucky;

    private boolean canSkillShot;
    private boolean canDaze;
    private boolean canRetrieve;

    public ArcheryCommand() {
        super(PrimarySkillType.ARCHERY);
    }

    @Override
    protected void dataCalculations(Player player, float skillValue) {
        // ARCHERY_ARROW_RETRIEVAL
        if (canRetrieve) {
            String[] retrieveStrings = getAbilityDisplayValues(SkillActivationType.RANDOM_LINEAR_100_SCALE_WITH_CAP, player, SubSkillType.ARCHERY_ARROW_RETRIEVAL);
            retrieveChance = retrieveStrings[0];
            retrieveChanceLucky = retrieveStrings[1];
        }
        
        // ARCHERY_DAZE
        if (canDaze) {
            String[] dazeStrings = getAbilityDisplayValues(SkillActivationType.RANDOM_LINEAR_100_SCALE_WITH_CAP, player, SubSkillType.ARCHERY_DAZE);
            dazeChance = dazeStrings[0];
            dazeChanceLucky = dazeStrings[1];
        }
        
        // SKILL SHOT
        if (canSkillShot) {
            skillShotBonus = percent.format(Archery.getDamageBonusPercent(player));
        }
    }

    @Override
    protected void permissionsCheck(Player player) {
        canSkillShot = canUseSubskill(player, SubSkillType.ARCHERY_SKILL_SHOT);
        canDaze = canUseSubskill(player, SubSkillType.ARCHERY_DAZE);
        canRetrieve = canUseSubskill(player, SubSkillType.ARCHERY_ARROW_RETRIEVAL);
    }

    @Override
    protected List<String> statsDisplay(Player player, float skillValue, boolean hasEndurance, boolean isLucky) {
        List<String> messages = new ArrayList<>();

        if (canRetrieve) {
            messages.add(getStatMessage(SubSkillType.ARCHERY_ARROW_RETRIEVAL, retrieveChance)
                    + (isLucky ? LocaleLoader.getString("Perks.Lucky.Bonus", retrieveChanceLucky) : ""));
        }
        
        if (canDaze) {
            messages.add(getStatMessage(SubSkillType.ARCHERY_DAZE, dazeChance)
                    + (isLucky ? LocaleLoader.getString("Perks.Lucky.Bonus", dazeChanceLucky) : ""));
        }
        
        if (canSkillShot) {
            messages.add(getStatMessage(SubSkillType.ARCHERY_SKILL_SHOT, skillShotBonus));
        }

        if(canUseSubskill(player, SubSkillType.ARCHERY_ARCHERY_LIMIT_BREAK)) {
            messages.add(getStatMessage(SubSkillType.ARCHERY_ARCHERY_LIMIT_BREAK,
                String.valueOf(CombatUtils.getLimitBreakDamageAgainstQuality(player, SubSkillType.ARCHERY_ARCHERY_LIMIT_BREAK, 1000))));
        }

        return messages;
    }

    @Override
    protected List<Component> getTextComponents(Player player) {
        List<Component> textComponents = new ArrayList<>();

        TextComponentFactory.getSubSkillTextComponents(player, textComponents, PrimarySkillType.ARCHERY);

        return textComponents;
    }
}
