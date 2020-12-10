package com.gmail.nossr50.commands.skills;

import com.gmail.nossr50.datatypes.skills.PrimarySkillType;
import com.gmail.nossr50.util.TextComponentFactory;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class TridentsCommand extends SkillCommand {
    public TridentsCommand() {
        super(PrimarySkillType.TRIDENTS);
    }

    @Override
    protected void dataCalculations(@NotNull McMMOPlayer mmoPlayer, float skillValue) {

    }

    @Override
    protected void permissionsCheck(@NotNull McMMOPlayer mmoPlayer) {

    }

    @Override
    protected @NotNull List<String> statsDisplay(@NotNull McMMOPlayer mmoPlayer, float skillValue, boolean hasEndurance, boolean isLucky) {
        return null;
    }

    @Override
    protected List<TextComponent> getTextComponents(Player player) {
        List<TextComponent> textComponents = new ArrayList<>();

        TextComponentFactory.getSubSkillTextComponents(player, textComponents, PrimarySkillType.TRIDENTS);

        return textComponents;
    }
}
