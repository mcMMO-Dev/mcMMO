package com.gmail.nossr50.skills.acrobatics;

import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageEvent;

import com.gmail.nossr50.datatypes.McMMOPlayer;
import com.gmail.nossr50.locale.LocaleLoader;
import com.gmail.nossr50.skills.utilities.SkillTools;
import com.gmail.nossr50.skills.utilities.SkillType;
import com.gmail.nossr50.util.Misc;
import com.gmail.nossr50.util.ParticleEffectUtils;

public class DodgeEventHandler extends AcrobaticsEventHandler {
    protected DodgeEventHandler(AcrobaticsManager manager, EntityDamageEvent event) {
        super(manager, event);

        calculateSkillModifier();
        calculateModifiedDamage();
    }

    @Override
    protected void calculateSkillModifier() {
        this.skillModifier = SkillTools.skillCheck(manager.getSkillLevel(), Acrobatics.dodgeMaxBonusLevel);
    }

    @Override
    protected void calculateModifiedDamage() {
        int modifiedDamage = damage / 2;

        if (modifiedDamage <= 0) {
            modifiedDamage = 1;
        }

        this.modifiedDamage = modifiedDamage;
    }

    @Override
    protected void modifyEventDamage() {
        event.setDamage(modifiedDamage);
    }

    @Override
    protected void sendAbilityMessage() {
        McMMOPlayer mcMMOPlayer = manager.getMcMMOPlayer();
        Player dodgingPlayer = mcMMOPlayer.getPlayer();

        ParticleEffectUtils.playDodgeEffect(dodgingPlayer);

        if (mcMMOPlayer.getProfile().useChatNotifications()) {
            dodgingPlayer.sendMessage(LocaleLoader.getString("Acrobatics.Combat.Proc"));
        }
    }

    @Override
    protected void processXpGain(int xp) {
        McMMOPlayer mcMMOPlayer = manager.getMcMMOPlayer();

        if (System.currentTimeMillis() >= mcMMOPlayer.getProfile().getRespawnATS() + Misc.PLAYER_RESPAWN_COOLDOWN_SECONDS) {
            manager.getMcMMOPlayer().beginXpGain(SkillType.ACROBATICS, xp);
        }
    }
}
