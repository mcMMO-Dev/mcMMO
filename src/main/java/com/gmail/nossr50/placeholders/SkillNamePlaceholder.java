package com.gmail.nossr50.placeholders;

import com.gmail.nossr50.datatypes.skills.PrimarySkillType;
import com.gmail.nossr50.mcMMO;
import java.util.Locale;
import org.bukkit.entity.Player;

/**
 * Localized skill name placeholders. The plain form returns the skill name used in messages
 * (the nicely capitalized Overhaul.Name locale entries, like "Mining"); the header form
 * returns the stylized header name (the SkillName locale entries, FULL CAPS in the English
 * locales). Both follow the server's locale and any locale overrides.
 */
public class SkillNamePlaceholder implements Placeholder {
    private final PrimarySkillType skillType;
    private final boolean headerName;

    private SkillNamePlaceholder(PrimarySkillType skillType, boolean headerName) {
        this.skillType = skillType;
        this.headerName = headerName;
    }

    /**
     * %mcmmo_skillname_&lt;skillname&gt;% - the localized skill name used in messages
     */
    public static SkillNamePlaceholder localized(PrimarySkillType skillType) {
        return new SkillNamePlaceholder(skillType, false);
    }

    /**
     * %mcmmo_skillname_header_&lt;skillname&gt;% - the localized header/banner skill name
     */
    public static SkillNamePlaceholder header(PrimarySkillType skillType) {
        return new SkillNamePlaceholder(skillType, true);
    }

    @Override
    public String process(Player p, String params) {
        return headerName
                ? mcMMO.p.getSkillTools().getHeaderBannerSkillName(skillType)
                : mcMMO.p.getSkillTools().getLocalizedSkillName(skillType);
    }

    @Override
    public String getName() {
        return (headerName ? "skillname_header_" : "skillname_")
                + skillType.toString().toLowerCase(Locale.ENGLISH);
    }
}
