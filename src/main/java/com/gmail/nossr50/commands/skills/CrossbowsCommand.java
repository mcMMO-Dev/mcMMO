package com.gmail.nossr50.commands.skills;

import com.gmail.nossr50.datatypes.player.McMMOPlayer;
import com.gmail.nossr50.datatypes.skills.PrimarySkillType;
import com.gmail.nossr50.datatypes.skills.SubSkillType;
import com.gmail.nossr50.locale.LocaleLoader;
import com.gmail.nossr50.skills.archery.Archery;
import com.gmail.nossr50.util.Permissions;
import com.gmail.nossr50.util.player.UserManager;
import com.gmail.nossr50.util.random.ProbabilityUtil;
import com.gmail.nossr50.util.skills.CombatUtils;
import com.gmail.nossr50.util.skills.RankUtils;
import com.gmail.nossr50.util.text.TextComponentFactory;
import net.kyori.adventure.text.Component;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class CrossbowsCommand extends SkillCommand {
    private boolean canSSG;
    private boolean canTrickShot;
    private boolean canPoweredShot;
    private int bounceCount;

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

        canTrickShot = RankUtils.hasUnlockedSubskill(player, SubSkillType.CROSSBOWS_TRICK_SHOT)
                && Permissions.trickShot(player);

        canPoweredShot = RankUtils.hasUnlockedSubskill(player, SubSkillType.CROSSBOWS_POWERED_SHOT)
                && Permissions.poweredShot(player);
    }

    @Override
    protected List<String> statsDisplay(Player player, float skillValue, boolean hasEndurance, boolean isLucky) {
        List<String> messages = new ArrayList<>();

        McMMOPlayer mmoPlayer = UserManager.getPlayer(player);
        if (mmoPlayer == null) {
            return messages;
        }

        if (canPoweredShot) {
            messages.add(getStatMessage(SubSkillType.ARCHERY_SKILL_SHOT, percent.format(mmoPlayer.getCrossbowsManager().getDamageBonusPercent(player))));
        }

        if (canSSG) {
            messages.add("Super Shotgun");
            //TODO: Implement SSG
        }

        if (canTrickShot) {
            messages.add(getStatMessage(SubSkillType.CROSSBOWS_TRICK_SHOT,
                    String.valueOf(mmoPlayer.getCrossbowsManager().getTrickShotMaxBounceCount())));
        }

        if(Permissions.canUseSubSkill(player, SubSkillType.CROSSBOWS_CROSSBOWS_LIMIT_BREAK)) {
            messages.add(getStatMessage(SubSkillType.CROSSBOWS_CROSSBOWS_LIMIT_BREAK,
                    String.valueOf(CombatUtils.getLimitBreakDamageAgainstQuality(player, SubSkillType.CROSSBOWS_CROSSBOWS_LIMIT_BREAK, 1000))));
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
