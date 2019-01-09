package com.gmail.nossr50.commands.skills;

import com.gmail.nossr50.datatypes.json.McMMOUrl;
import com.gmail.nossr50.util.StringUtils;

public enum McMMOWebLinks {
    WEBSITE,
    DISCORD,
    PATREON,
    SPIGOT,
    HELP_TRANSLATE,
    WIKI;

    public String getUrl()
    {
        return McMMOUrl.getUrl(this);
    }

    public String getNiceTitle()
    {
        return StringUtils.getCapitalized(toString());
    }
}
