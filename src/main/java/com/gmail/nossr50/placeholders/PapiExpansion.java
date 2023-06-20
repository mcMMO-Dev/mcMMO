package com.gmail.nossr50.placeholders;

import com.gmail.nossr50.api.ExperienceAPI;
import com.gmail.nossr50.config.experience.ExperienceConfig;
import com.gmail.nossr50.datatypes.party.Party;
import com.gmail.nossr50.datatypes.player.McMMOPlayer;
import com.gmail.nossr50.datatypes.skills.PrimarySkillType;
import com.gmail.nossr50.mcMMO;
import com.gmail.nossr50.util.Permissions;
import com.gmail.nossr50.util.player.UserManager;
import com.gmail.nossr50.util.text.StringUtils;
import me.clip.placeholderapi.PlaceholderAPIPlugin;
import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;


public class PapiExpansion extends PlaceholderExpansion {

    public PapiExpansion() {
    }

    @Override
    public String getIdentifier() {
        return "mcmmo";
    }

    @Override
    public String getAuthor() {
        return "mcMMO Dev Team";
    }

    @Override
    public String getVersion() {
        return mcMMO.p.getDescription().getVersion();
    }

    @Override
    public String getRequiredPlugin() {
        return "mcMMO";
    }

    @Override
    @Nullable
    public String onPlaceholderRequest(final Player player, @NotNull final String params) {
      
      // Non player-specific placeholders
      if (params.equalsIgnoreCase("is_xp_event_active")) {
        return mcMMO.p.isXPEventEnabled() ? PlaceholderAPIPlugin.booleanTrue() : PlaceholderAPIPlugin.booleanFalse();
      }else if (params.equalsIgnoreCase("xprate")) {
        return String.valueOf(ExperienceConfig.getInstance().getExperienceGainsGlobalMultiplier());
      }else if (params.equalsIgnoreCase("power_level_cap")) {
        return mcMMO.p.getGeneralConfig().getPowerLevelCap()+"";
      }
      
      final McMMOPlayer user = UserManager.getPlayer(player);
      if (user == null) return null;
      
      if (params.startsWith("level_")) {
        PrimarySkillType skill = PrimarySkillType.valueOf(params.substring(6).toUpperCase());
        return skill == null ? null : user.getSkillLevel(skill)+"";
      }else if (params.startsWith("xp_needed_")) {
        PrimarySkillType skill = PrimarySkillType.valueOf(params.substring(10).toUpperCase());
        return skill == null ? null : user.getXpToLevel(skill)+"";
      }else if (params.startsWith("xp_remaining_")) {
        PrimarySkillType skill = PrimarySkillType.valueOf(params.substring(13).toUpperCase());
        return skill == null ? null : (user.getXpToLevel(skill) - user.getSkillXpLevel(skill))+"";
      }else if (params.startsWith("xp_")) {
        PrimarySkillType skill = PrimarySkillType.valueOf(params.substring(3).toUpperCase());
        return skill == null ? null : user.getSkillXpLevel(skill)+"";
      }else if (params.startsWith("rank_")) {
        try {
          return ExperienceAPI.getPlayerRankSkill(player.getUniqueId(), StringUtils.getCapitalized(params.substring(5)))+"";
        } catch (Exception ex) {
          return null;
        }
      }else if (params.startsWith("xprate_")) {
        PrimarySkillType skill = PrimarySkillType.valueOf(params.substring(7).toUpperCase());
        if (skill == null) return null;
        double modifier = 1.0F;
        if (Permissions.customXpBoost(player, skill))
            modifier = ExperienceConfig.getInstance().getCustomXpPerkBoost();
        else if (Permissions.quadrupleXp(player, skill))
            modifier = 4;
        else if (Permissions.tripleXp(player, skill))
            modifier = 3;
        else if (Permissions.doubleAndOneHalfXp(player, skill))
            modifier = 2.5;
        else if (Permissions.doubleXp(player, skill))
            modifier = 2;
        else if (Permissions.oneAndOneHalfXp(player, skill))
            modifier = 1.5;
        else if (Permissions.oneAndOneTenthXp(player, skill))
            modifier = 1.1;
        return String.valueOf(modifier);
      }else if (params.equalsIgnoreCase("power_level")) {
        return user.getPowerLevel()+"";
      }
      
      
      //Party placeholders
      final Party party = user.getParty();
      
      if (params.equalsIgnoreCase("in_party")) {
        return (party==null) ? PlaceholderAPIPlugin.booleanFalse() : PlaceholderAPIPlugin.booleanTrue();
      }else if (params.equalsIgnoreCase("party_name")) {
        return (party == null) ? "" : party.getName();
      }else if (params.equalsIgnoreCase("is_party_leader")) {
        if (party == null) return "";
        return party.getLeader().getPlayerName().equals(player.getName()) ? PlaceholderAPIPlugin.booleanTrue() : PlaceholderAPIPlugin.booleanFalse();
      }else if (params.equalsIgnoreCase("party_leader")) {
        return (party == null) ? "" : party.getLeader().getPlayerName();
      }else if (params.equalsIgnoreCase("party_size")) {
        return (party == null) ? "" : party.getMembers().size()+"";
      }
      
      return null;
    }

}
