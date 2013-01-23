package com.gmail.nossr50.skills;

import org.bukkit.entity.Player;

import com.gmail.nossr50.datatypes.PlayerProfile;
import com.gmail.nossr50.util.Misc;
import com.gmail.nossr50.util.Permissions;
import com.gmail.nossr50.util.Users;

public abstract class SkillManager {
    protected Player player;
    protected PlayerProfile profile;
    protected int skillLevel;
    protected int activationChance;

    public SkillManager(Player player, SkillType skill) {
        this.player = player;
        this.profile = Users.getProfile(player);
        this.skillLevel = profile.getSkillLevel(skill);
        this.activationChance = Misc.calculateActivationChance(Permissions.lucky(player, skill));
    }
    
    public SkillManager(Player player, SkillType child, SkillType parent1, SkillType parent2) {
        this.player = player;
        this.profile = Users.getProfile(player);
        this.skillLevel = (profile.getSkillLevel(parent1) / 4) + (profile.getSkillLevel(parent2) / 4); //TODO: Make this cleaner somehow
        this.activationChance = Misc.calculateActivationChance(Permissions.lucky(player, child));
    }

    public Player getPlayer() {
        return player;
    }

    public PlayerProfile getProfile() {
        return profile;
    }

    public int getSkillLevel() {
        return skillLevel;
    }

    public int getActivationChance() {
        return activationChance;
    }
}
