package com.gmail.nossr50.commands.skills;

import com.gmail.nossr50.datatypes.skills.PrimarySkillType;
import com.gmail.nossr50.datatypes.skills.SubSkillType;
import com.gmail.nossr50.mcMMO;
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

    public SwordsCommand(mcMMO pluginRef) {
        super(PrimarySkillType.SWORDS, pluginRef);
    }

    @Override
    protected void dataCalculations(Player player, double skillValue) {
        // SWORDS_COUNTER_ATTACK
        if (canCounter) {
            String[] counterStrings = getAbilityDisplayValues(player, SubSkillType.SWORDS_COUNTER_ATTACK);
            counterChance = counterStrings[0];
            counterChanceLucky = counterStrings[1];
        }

        // SWORDS_RUPTURE
        if (canBleed) {
            bleedLength = pluginRef.getUserManager().getPlayer(player).getSwordsManager().getRuptureBleedTicks();

            String[] bleedStrings = getAbilityDisplayValues(player, SubSkillType.SWORDS_RUPTURE);
            bleedChance = bleedStrings[0];
            bleedChanceLucky = bleedStrings[1];
        }

        // SERRATED STRIKES
        if (canSerratedStrike) {
            String[] serratedStrikesStrings = formatLengthDisplayValues(player, skillValue);
            serratedStrikesLength = serratedStrikesStrings[0];
            serratedStrikesLengthEndurance = serratedStrikesStrings[1];
        }
    }

    @Override
    protected void permissionsCheck(Player player) {
        canBleed = canUseSubskill(player, SubSkillType.SWORDS_RUPTURE);
        canCounter = canUseSubskill(player, SubSkillType.SWORDS_COUNTER_ATTACK);
        canSerratedStrike = pluginRef.getRankTools().hasUnlockedSubskill(player, SubSkillType.SWORDS_SERRATED_STRIKES) && pluginRef.getPermissionTools().serratedStrikes(player);
    }

    @Override
    protected List<String> statsDisplay(Player player, double skillValue, boolean hasEndurance, boolean isLucky) {
        List<String> messages = new ArrayList<>();

        int ruptureTicks = pluginRef.getUserManager().getPlayer(player).getSwordsManager().getRuptureBleedTicks();
        double ruptureDamagePlayer = pluginRef.getConfigManager().getConfigSwords().getRuptureDamagePlayer();
        double pveRupture = pluginRef.getConfigManager().getConfigSwords().getRuptureDamageMobs();

        double pvpDamageRupture = pluginRef.getRankTools().getRank(player, SubSkillType.SWORDS_RUPTURE) >= 3 ? ruptureDamagePlayer * 1.5D : ruptureDamagePlayer;
        double ruptureDamageMobs = pluginRef.getRankTools().getRank(player, SubSkillType.SWORDS_RUPTURE) >= 3 ? pveRupture * 1.5D : pveRupture;

        if (canCounter) {
            messages.add(getStatMessage(SubSkillType.SWORDS_COUNTER_ATTACK, counterChance)
                    + (isLucky ? pluginRef.getLocaleManager().getString("Perks.Lucky.Bonus", counterChanceLucky) : ""));
        }

        if (canBleed) {
            messages.add(getStatMessage(SubSkillType.SWORDS_RUPTURE, bleedChance)
                    + (isLucky ? pluginRef.getLocaleManager().getString("Perks.Lucky.Bonus", bleedChanceLucky) : ""));
            messages.add(getStatMessage(true, true, SubSkillType.SWORDS_RUPTURE,
                    String.valueOf(ruptureTicks),
                    String.valueOf(pvpDamageRupture),
                    String.valueOf(ruptureDamageMobs)));

            messages.add(pluginRef.getLocaleManager().getString("Swords.Combat.Rupture.Note"));
        }

        if (canSerratedStrike) {
            messages.add(getStatMessage(SubSkillType.SWORDS_SERRATED_STRIKES, serratedStrikesLength)
                    + (hasEndurance ? pluginRef.getLocaleManager().getString("Perks.ActivationTime.Bonus", serratedStrikesLengthEndurance) : ""));
        }

        if (canUseSubskill(player, SubSkillType.SWORDS_STAB)) {
            messages.add(getStatMessage(SubSkillType.SWORDS_STAB,
                    String.valueOf(pluginRef.getUserManager().getPlayer(player).getSwordsManager().getStabDamage())));
        }

        if (canUseSubskill(player, SubSkillType.SWORDS_SWORDS_LIMIT_BREAK)) {
            messages.add(getStatMessage(SubSkillType.SWORDS_SWORDS_LIMIT_BREAK,
<<<<<<< HEAD
                    String.valueOf(pluginRef.getCombatTools().getLimitBreakDamage(player, SubSkillType.SWORDS_SWORDS_LIMIT_BREAK))));
=======
                    String.valueOf(CombatUtils.getLimitBreakDamageAgainstQuality(player, SubSkillType.SWORDS_SWORDS_LIMIT_BREAK, 1000))));
>>>>>>> 308e3a4b1f46e9e3de28d6d540dd055a540ed4d5
        }

        return messages;
    }

    @Override
    protected List<TextComponent> getTextComponents(Player player) {
        List<TextComponent> textComponents = new ArrayList<>();

        pluginRef.getTextComponentFactory().getSubSkillTextComponents(player, textComponents, PrimarySkillType.SWORDS);

        return textComponents;
    }
}
