package com.gmail.nossr50.commands.skills;

import com.gmail.nossr50.config.AdvancedConfig;
import com.gmail.nossr50.datatypes.skills.PrimarySkillType;
import com.gmail.nossr50.datatypes.skills.SubSkillType;
import com.gmail.nossr50.locale.LocaleLoader;
import com.gmail.nossr50.util.Permissions;
import com.gmail.nossr50.util.TextComponentFactory;
import com.gmail.nossr50.util.player.UserManager;
import com.gmail.nossr50.util.skills.CombatUtils;
import com.gmail.nossr50.util.skills.RankUtils;
import com.gmail.nossr50.util.skills.SkillActivationType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class SwordsCommand extends SkillCommand {
    private String counterChance;
    private String counterChanceLucky;
    private int bleedLength;
    private String bleedChance;
    private String bleedChanceLucky;
    private String serratedStrikesLength;
    private String serratedStrikesLengthEndurance;

    private boolean canCounter;
    private boolean canSerratedStrike;
    private boolean canBleed;

    public SwordsCommand() {
        super(PrimarySkillType.SWORDS);
    }

    @Override
    protected void dataCalculations(Player player, float skillValue) {
        // SWORDS_COUNTER_ATTACK
        if (canCounter) {
            String[] counterStrings = getAbilityDisplayValues(SkillActivationType.RANDOM_LINEAR_100_SCALE_WITH_CAP, player, SubSkillType.SWORDS_COUNTER_ATTACK);
            counterChance = counterStrings[0];
            counterChanceLucky = counterStrings[1];
        }

        // SWORDS_RUPTURE
        if (canBleed) {
            bleedLength = UserManager.getPlayer(player).getSwordsManager().getRuptureBleedTicks();

            String[] bleedStrings = getAbilityDisplayValues(SkillActivationType.RANDOM_LINEAR_100_SCALE_WITH_CAP, player, SubSkillType.SWORDS_RUPTURE);
            bleedChance = bleedStrings[0];
            bleedChanceLucky = bleedStrings[1];
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
        canBleed = canUseSubskill(player, SubSkillType.SWORDS_RUPTURE);
        canCounter = canUseSubskill(player, SubSkillType.SWORDS_COUNTER_ATTACK);
        canSerratedStrike = RankUtils.hasUnlockedSubskill(player, SubSkillType.SWORDS_SERRATED_STRIKES) && Permissions.serratedStrikes(player);
    }

    @Override
    protected List<String> statsDisplay(Player player, float skillValue, boolean hasEndurance, boolean isLucky) {
        List<String> messages = new ArrayList<String>();

        int ruptureTicks = UserManager.getPlayer(player).getSwordsManager().getRuptureBleedTicks();
        double ruptureDamagePlayers =  RankUtils.getRank(player, SubSkillType.SWORDS_RUPTURE) >= 3 ? AdvancedConfig.getInstance().getRuptureDamagePlayer() * 1.5D : AdvancedConfig.getInstance().getRuptureDamagePlayer();
        double ruptureDamageMobs =  RankUtils.getRank(player, SubSkillType.SWORDS_RUPTURE) >= 3 ? AdvancedConfig.getInstance().getRuptureDamageMobs() * 1.5D : AdvancedConfig.getInstance().getRuptureDamageMobs();

        if (canCounter) {
            messages.add(getStatMessage(SubSkillType.SWORDS_COUNTER_ATTACK, counterChance)
                    + (isLucky ? LocaleLoader.getString("Perks.Lucky.Bonus", counterChanceLucky) : ""));
        }

        if (canBleed) {
            messages.add(getStatMessage(SubSkillType.SWORDS_RUPTURE, bleedChance)
                    + (isLucky ? LocaleLoader.getString("Perks.Lucky.Bonus", bleedChanceLucky) : ""));
            messages.add(getStatMessage(true, true, SubSkillType.SWORDS_RUPTURE,
                    String.valueOf(ruptureTicks),
                    String.valueOf(ruptureDamagePlayers),
                    String.valueOf(ruptureDamageMobs)));

            messages.add(LocaleLoader.getString("Swords.Combat.Rupture.Note"));
        }

        if (canSerratedStrike) {
            messages.add(getStatMessage(SubSkillType.SWORDS_SERRATED_STRIKES, serratedStrikesLength)
                    + (hasEndurance ? LocaleLoader.getString("Perks.ActivationTime.Bonus", serratedStrikesLengthEndurance) : ""));
        }

        if(canUseSubskill(player, SubSkillType.SWORDS_STAB))
        {
            messages.add(getStatMessage(SubSkillType.SWORDS_STAB,
                    String.valueOf(UserManager.getPlayer(player).getSwordsManager().getStabDamage())));
        }

        if(canUseSubskill(player, SubSkillType.SWORDS_SWORDS_LIMIT_BREAK)) {
            messages.add(getStatMessage(SubSkillType.SWORDS_SWORDS_LIMIT_BREAK,
                    String.valueOf(CombatUtils.getLimitBreakDamageAgainstQuality(player, SubSkillType.SWORDS_SWORDS_LIMIT_BREAK, 1000))));
        }

        return messages;
    }

    @Override
    protected List<TextComponent> getTextComponents(Player player) {
        List<TextComponent> textComponents = new ArrayList<>();

        TextComponentFactory.getSubSkillTextComponents(player, textComponents, PrimarySkillType.SWORDS);

        return textComponents;
    }
}
