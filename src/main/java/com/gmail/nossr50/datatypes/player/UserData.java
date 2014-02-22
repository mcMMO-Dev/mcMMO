package com.gmail.nossr50.datatypes.player;

import com.gmail.nossr50.datatypes.MobHealthbarType;
import com.gmail.nossr50.datatypes.skills.AbilityType;
import com.gmail.nossr50.datatypes.skills.SkillType;
import com.gmail.nossr50.mcMMO;
import org.bukkit.entity.Player;

import java.io.Serializable;
import java.util.HashMap;
import java.util.UUID;

public class UserData implements Serializable {
    private static final long serialVersionUID = 1L;

    private UUID playerID;
    private String name;
    private long lastPlayed;
    private HashMap<SkillType, Integer> skillLevels = new HashMap<SkillType, Integer>();
    private HashMap<SkillType, Float> skillXp = new HashMap<SkillType, Float>();
    private HashMap<AbilityType, Integer> abilityData = new HashMap<AbilityType, Integer>();
    private MobHealthbarType mobHealthbarType;

    public UserData(McMMOPlayer mcMMOPlayer) {
        Player player = mcMMOPlayer.getPlayer();

        playerID = player.getUniqueId();
        name = player.getName();
        lastPlayed = player.getLastPlayed();

        for (SkillType skill : SkillType.values()) {
            skillLevels.put(skill, mcMMOPlayer.getSkillLevel(skill));
            skillXp.put(skill, mcMMOPlayer.getSkillXpLevelRaw(skill));
        }

        PlayerProfile profile = mcMMOPlayer.getProfile();

        for (AbilityType ability : AbilityType.values()) {
            abilityData.put(ability, (int) profile.getAbilityDATS(ability));
        }

        mobHealthbarType = profile.getMobHealthbarType();
    }

    public UserData(PlayerProfile profile) {
        name = profile.getPlayerName();

        Player player = mcMMO.p.getServer().getPlayerExact(name);

        if (player == null) {
            playerID = UUID.fromString(name);
            lastPlayed = 0L;
        }
        else {
            playerID = player.getUniqueId();
            lastPlayed = player.getLastPlayed();
        }

        for (SkillType skill : SkillType.values()) {
            skillLevels.put(skill, profile.getSkillLevel(skill));
            skillXp.put(skill, profile.getSkillXpLevelRaw(skill));
        }

        for (AbilityType ability : AbilityType.values()) {
            abilityData.put(ability, (int) profile.getAbilityDATS(ability));
        }

        mobHealthbarType = profile.getMobHealthbarType();
    }

    public String getName() {
        return name;
    }

    public long getLastPlayed() {
        return lastPlayed;
    }

    public void setLastPlayed(long lastPlayed) {
        this.lastPlayed = lastPlayed;
    }

    public HashMap<SkillType, Integer> getSkillLevels() {
        return skillLevels;
    }

    public HashMap<SkillType, Float> getSkillXp() {
        return skillXp;
    }

    public HashMap<AbilityType, Integer> getAbilityData() {
        return abilityData;
    }

    public MobHealthbarType getMobHealthbarType() {
        return mobHealthbarType;
    }
}
