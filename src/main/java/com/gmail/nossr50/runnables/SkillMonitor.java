package com.gmail.nossr50.runnables;

import org.bukkit.entity.Player;

import com.gmail.nossr50.McMMO;
import com.gmail.nossr50.datatypes.AbilityType;
import com.gmail.nossr50.datatypes.PlayerProfile;
import com.gmail.nossr50.datatypes.SkillType;
import com.gmail.nossr50.util.Skills;
import com.gmail.nossr50.util.Users;

public class SkillMonitor implements Runnable {
    private final McMMO plugin;

    public SkillMonitor(final McMMO plugin) {
        this.plugin = plugin;
    }

    @Override
    public void run() {
        long curTime = System.currentTimeMillis();

        for (Player player : plugin.getServer().getOnlinePlayers()) {

            PlayerProfile PP = Users.getProfile(player);

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
