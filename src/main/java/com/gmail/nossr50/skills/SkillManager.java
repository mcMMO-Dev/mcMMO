package com.gmail.nossr50.skills;

import org.bukkit.entity.Player;

import com.gmail.nossr50.datatypes.PlayerProfile;
import com.gmail.nossr50.datatypes.SkillType;
import com.gmail.nossr50.util.Users;

public abstract class SkillManager {
    protected Player player;
    protected PlayerProfile profile;
    protected int skillLevel;

    public SkillManager(Player player, SkillType skill) {
        this.player = player;
        this.profile = Users.getProfile(player);
        this.skillLevel = profile.getSkillLevel(skill);
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
}
