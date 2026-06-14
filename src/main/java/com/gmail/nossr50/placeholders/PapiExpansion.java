package com.gmail.nossr50.placeholders;

import com.gmail.nossr50.api.ExperienceAPI;
import com.gmail.nossr50.config.experience.ExperienceConfig;
import com.gmail.nossr50.datatypes.party.Party;
import com.gmail.nossr50.datatypes.player.McMMOPlayer;
import com.gmail.nossr50.datatypes.skills.PrimarySkillType;
import com.gmail.nossr50.mcMMO;
import com.gmail.nossr50.util.Permissions;
import com.gmail.nossr50.util.player.UserManager;
import com.gmail.nossr50.util.skills.SkillTools;
import com.gmail.nossr50.util.text.StringUtils;
import java.util.Map;
import java.util.TreeMap;
import me.clip.placeholderapi.PlaceholderAPIPlugin;
import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jspecify.annotations.NonNull;

/**
 * PlaceholderAPI expansion entrypoint for mcMMO placeholders.
 */
public class PapiExpansion extends PlaceholderExpansion {
    private final Map<String, Placeholder> placeholders
            = new TreeMap<>(String.CASE_INSENSITIVE_ORDER);
    /**
     * Shared cache backing leaderboard-by-position placeholders.
     */
    private final LeaderboardPlaceholderCache leaderboardPlaceholderCache;

    public PapiExpansion() {
        final int maxTrackedRank = mcMMO.p.getGeneralConfig().getPapiLeaderboardMaxTrackedRank();
        final long refreshIntervalTicks =
                20L * mcMMO.p.getGeneralConfig().getPapiLeaderboardRefreshIntervalSeconds();

        this.leaderboardPlaceholderCache = new LeaderboardPlaceholderCache(
                mcMMO.p,
                maxTrackedRank,
                refreshIntervalTicks
        );
        init();
        leaderboardPlaceholderCache.start();
    }

    @Override
    public @NonNull String getIdentifier() {
        return "mcmmo";
    }

    @Override
    public @NonNull String getAuthor() {
        return "mcMMO Dev Team";
    }

    @Override
    public @NonNull String getVersion() {
        return "1.1,0";
    }

    @Override
    public boolean persist() {
        return true;
    }

    @Override
    public String getRequiredPlugin() {
        return "mcMMO";
    }

    @Override
    @Nullable
    public String onPlaceholderRequest(final Player player, @NotNull final String params) {
        String token;
        String data = null;
        int dataPosition = params.indexOf(":");

        if (dataPosition != -1) {
            token = params.substring(0, dataPosition);
            data = params.substring(dataPosition + 1);
        } else {
            token = params;
        }

        Placeholder placeholder = placeholders.get(token);

        if (placeholder != null) {
            return placeholder.process(player, data);
        } else {
            return null;
        }
    }

    public Integer getSkillLevel(PrimarySkillType skill, Player player) {
        final McMMOPlayer user = UserManager.getPlayer(player);
        if (user == null) {
            return null;
        }
        return user.getSkillLevel(skill);
    }

    public Integer getExpNeeded(PrimarySkillType skill, Player player) {
        final McMMOPlayer user = UserManager.getPlayer(player);
        if (user == null) {
            return null;
        }
        return user.getXpToLevel(skill);
    }

    public Integer getExp(PrimarySkillType skill, Player player) {
        final McMMOPlayer user = UserManager.getPlayer(player);
        if (user == null) {
            return null;
        }

        return user.getSkillXpLevel(skill);
    }


    public Integer getExpRemaining(PrimarySkillType skill, Player player) {
        final McMMOPlayer user = UserManager.getPlayer(player);
        if (user == null) {
            return null;
        }
        int current = user.getSkillXpLevel(skill);
        int needed = user.getXpToLevel(skill);

        return needed - current;
    }

    public Integer getRank(PrimarySkillType skill, Player player) {
        try {
            return ExperienceAPI.getPlayerRankSkill(player.getUniqueId(),
                    StringUtils.getCapitalized(skill.toString()));
        } catch (Exception ex) {
            return null;
        }
    }

    public Integer getPowerLevel(Player player) {
        final McMMOPlayer user = UserManager.getPlayer(player);
        if (user == null) {
            return null;
        }
        return user.getPowerLevel();
    }

    public Integer getPowerCap(Player player) {
        return mcMMO.p.getGeneralConfig().getPowerLevelCap();
    }

    public String getPartyName(Player player) {
        final McMMOPlayer user = UserManager.getPlayer(player);
        if (user == null) {
            return null;
        }
        final Party party = user.getParty();

        return (party == null) ? null : party.getName();
    }

    public String getPartyLeader(Player player) {
        final McMMOPlayer user = UserManager.getPlayer(player);
        if (user == null) {
            return null;
        }
        final Party party = user.getParty();
        return (party == null) ? null : party.getLeader().getPlayerName();
    }

    public Integer getPartySize(Player player) {
        final McMMOPlayer user = UserManager.getPlayer(player);
        if (user == null) {
            return null;
        }
        final Party party = user.getParty();
        return (party == null) ? null : party.getMembers().size();
    }

    public String getXpRate(Player player) {
        return String.valueOf(ExperienceConfig.getInstance().getExperienceGainsGlobalMultiplier());
    }

    public String getSkillXpRate(PrimarySkillType skill, Player player) {
        final McMMOPlayer user = UserManager.getPlayer(player);
        if (user == null) {
            return null;
        }

        double modifier = 1.0F;

        if (Permissions.customXpBoost(player, skill)) {
            modifier = ExperienceConfig.getInstance().getCustomXpPerkBoost();
        } else if (Permissions.quadrupleXp(player, skill)) {
            modifier = 4;
        } else if (Permissions.tripleXp(player, skill)) {
            modifier = 3;
        } else if (Permissions.doubleAndOneHalfXp(player, skill)) {
            modifier = 2.5;
        } else if (Permissions.doubleXp(player, skill)) {
            modifier = 2;
        } else if (Permissions.oneAndOneHalfXp(player, skill)) {
            modifier = 1.5;
        } else if (Permissions.oneAndOneTenthXp(player, skill)) {
            modifier = 1.1;
        }

        return String.valueOf(modifier);
    }

    public String isExpEventActive(Player player) {
        return mcMMO.p.isXPEventEnabled() ? PlaceholderAPIPlugin.booleanTrue()
                : PlaceholderAPIPlugin.booleanFalse();
    }

    public void shutdown() {
        // Called from plugin disable to stop periodic refresh tasks before global task cancellation.
        leaderboardPlaceholderCache.shutdown();
    }

    /**
     * Registers one placeholder handler in the expansion token map.
     */
    public void registerPlaceholder(Placeholder placeholder) {
        final Placeholder registered = placeholders.get(placeholder.getName());
        if (registered != null) {
            throw new IllegalStateException(
                    "Placeholder " + placeholder.getName() + " is already registered!");
        }

        placeholders.put(placeholder.getName(), placeholder);
    }

    /**
     * Performs one-time placeholder registration for all supported tokens.
     */
    protected void init() {
        for (PrimarySkillType skill : PrimarySkillType.values()) {
            // %mcmmo_level_<skillname>%
            registerPlaceholder(new SkillLevelPlaceholder(this, skill));

            //%mcmmo_xp_needed_<skillname>%
            registerPlaceholder(new SkillExpNeededPlaceholder(this, skill));

            //%mcmmo_xp_<skillname>%
            registerPlaceholder(new SkillExpPlaceholder(this, skill));

            //%mcmmo_xp_remaining_<skillname>%
            registerPlaceholder(new SkillExpRemainingPlaceholder(this, skill));

            //%mcmmo_rank_<skillname>%
            registerPlaceholder(new SkillRankPlaceholder(this, skill));

            //%mcmmo_xprate_<skillname>%
            registerPlaceholder(new SkillXpRatePlaceholder(this, skill));
        }

        for (PrimarySkillType skill : SkillTools.NON_CHILD_SKILLS) {
            // %mcmmo_mctop_<skillname>:<position>%
            registerPlaceholder(new McTopPositionPlaceholder(skill, leaderboardPlaceholderCache));

            // %mcmmo_mctop_name_<skillname>:<position>%
            registerPlaceholder(new McTopNamePlaceholder(skill, leaderboardPlaceholderCache));
        }

        //%mcmmo_power_level%
        registerPlaceholder(new PowerLevelPlaceholder(this));

        // %mcmmo_power_level_cap%
        registerPlaceholder(new PowerLevelCapPlaceholder(this));

        // %mcmmo_in_party%
        registerPlaceholder(new PartyIsMemberPlaceholder(this));

        /// %mcmmo_party_name%
        registerPlaceholder(new PartyNamePlaceholder(this));

        // %mcmmo_is_party_leader%
        registerPlaceholder(new PartyIsLeaderPlaceholder(this));

        // %mcmmo_party_leader%
        registerPlaceholder(new PartyLeaderPlaceholder(this));

        // %mcmmo_party_size%
        registerPlaceholder(new PartySizePlaceholder(this));

        // %mcmmo_is_xp_event_active%
        registerPlaceholder(new XpEventActivePlaceholder(this));
        // %mcmmo_xprate%
        registerPlaceholder(new XpRatePlaceholder(this));

        // %mcmmo_mctop_overall:<position>%
        registerPlaceholder(new McTopPositionPlaceholder(null, leaderboardPlaceholderCache));

        // %mcmmo_mctop_name_overall:<position>%
        registerPlaceholder(new McTopNamePlaceholder(null, leaderboardPlaceholderCache));

        // %mcmmo_mctop_all:<position>%
        registerPlaceholder(new McTopPositionPlaceholder(null, "all", leaderboardPlaceholderCache));

        // %mcmmo_mctop_name_all:<position>%
        registerPlaceholder(new McTopNamePlaceholder(null, "all", leaderboardPlaceholderCache));

        // %mcmmo_mctop_powerlevel:<position>%
        registerPlaceholder(new McTopPositionPlaceholder(null, "powerlevel", leaderboardPlaceholderCache));

        // %mcmmo_mctop_name_powerlevel:<position>%
        registerPlaceholder(new McTopNamePlaceholder(null, "powerlevel", leaderboardPlaceholderCache));
    }

}
