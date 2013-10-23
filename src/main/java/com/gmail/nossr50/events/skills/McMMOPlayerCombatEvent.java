package com.gmail.nossr50.events.skills;

import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

import com.gmail.nossr50.datatypes.skills.SkillType;
import com.gmail.nossr50.events.fake.FakeEntityDamageByEntityEvent;
import com.gmail.nossr50.util.player.UserManager;

public abstract class McMMOPlayerCombatEvent extends FakeEntityDamageByEntityEvent {
    private Player player;
    private SkillType skill;
    private int skillLevel;

    public McMMOPlayerCombatEvent(Player player, Entity damager, Entity damagee, DamageCause cause, double damage, SkillType skill) {
        super(damager, damagee, cause, damage);
        this.player = player;
        this.skill = skill;
        skillLevel = UserManager.getPlayer(player).getProfile().getSkillLevel(skill);
    }

    public Player getPlayer() {
        return player;
    }

    public SkillType getSkill() {
        return skill;
    }

    public int getSkillLevel() {
        return skillLevel;
    }
}
