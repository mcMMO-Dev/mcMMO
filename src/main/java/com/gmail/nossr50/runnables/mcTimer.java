package com.gmail.nossr50.runnables;

import org.bukkit.entity.Player;

import com.gmail.nossr50.Users;
import com.gmail.nossr50.mcMMO;
import com.gmail.nossr50.datatypes.AbilityType;
import com.gmail.nossr50.datatypes.PlayerProfile;
import com.gmail.nossr50.datatypes.SkillType;
import com.gmail.nossr50.skills.Skills;

public class mcTimer implements Runnable {
    private final mcMMO plugin;

    public mcTimer(final mcMMO plugin) {
        this.plugin = plugin;
    }

    @Override
    public void run() {
        long curTime = System.currentTimeMillis();

        for (Player player : plugin.getServer().getOnlinePlayers()) {
            if (player == null) { //Is this even possible?
                continue;
            }

            PlayerProfile PP = Users.getProfile(player);

            if (PP == null) { //Is this even possible?
                continue;
            }

            /*
             * MONITOR SKILLS
             */
            for (SkillType skill : SkillType.values()) {
                if (skill.getTool() != null && skill.getAbility() != null) {
                    Skills.monitorSkill(player, PP, curTime, skill);
                }
            }

            /*
             * COOLDOWN MONITORING
             */
            for (AbilityType ability : AbilityType.values()) {
                if (ability.getCooldown() > 0 ) {
                    Skills.watchCooldown(player, PP, curTime, ability);
                }
            }
        }
    }
}
