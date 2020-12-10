package com.gmail.nossr50.commands.skills;

import com.gmail.nossr50.datatypes.player.McMMOPlayer;
import com.gmail.nossr50.datatypes.skills.PrimarySkillType;
import com.gmail.nossr50.datatypes.skills.SubSkillType;
import com.gmail.nossr50.locale.LocaleLoader;
import com.gmail.nossr50.skills.salvage.Salvage;
import com.gmail.nossr50.skills.salvage.SalvageManager;
import com.gmail.nossr50.util.skills.RankUtils;
import com.gmail.nossr50.util.text.TextComponentFactory;
import net.kyori.adventure.text.Component;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class SalvageCommand extends SkillCommand {
    private boolean canScrapCollector;
    private boolean canArcaneSalvage;

    public SalvageCommand() {
        super(PrimarySkillType.SALVAGE);
    }

    @Override
    protected void dataCalculations(Player player, float skillValue) {
        // TODO Auto-generated method stub

    }

    @Override
    protected void permissionsCheck(@NotNull McMMOPlayer mmoPlayer) {
        canScrapCollector = canUseSubskill(mmoPlayer, SubSkillType.SALVAGE_SCRAP_COLLECTOR);
        canArcaneSalvage = canUseSubskill(mmoPlayer, SubSkillType.SALVAGE_ARCANE_SALVAGE);
    }

    @Override
    protected @NotNull List<String> statsDisplay(@NotNull McMMOPlayer mmoPlayer, float skillValue, boolean hasEndurance, boolean isLucky) {
        List<String> messages = new ArrayList<>();
        SalvageManager salvageManager = mmoPlayer.getSalvageManager();

        if (canScrapCollector) {
            messages.add(getStatMessage(false, true,
                    SubSkillType.SALVAGE_SCRAP_COLLECTOR,
                    String.valueOf(RankUtils.getRank(mmoPlayer, SubSkillType.SALVAGE_SCRAP_COLLECTOR)),
                    RankUtils.getHighestRankStr(SubSkillType.SALVAGE_SCRAP_COLLECTOR)));
        }

        if (canArcaneSalvage) {
            messages.add(getStatMessage(false, true, SubSkillType.SALVAGE_ARCANE_SALVAGE,
                    String.valueOf(salvageManager.getArcaneSalvageRank()),
                    String.valueOf(RankUtils.getHighestRank(SubSkillType.SALVAGE_ARCANE_SALVAGE))));

            if (Salvage.arcaneSalvageEnchantLoss) {
                messages.add(LocaleLoader.getString("Ability.Generic.Template", LocaleLoader.getString("Salvage.Arcane.ExtractFull"), percent.format(salvageManager.getExtractFullEnchantChance() / 100)));
            }

            if (Salvage.arcaneSalvageDowngrades) {
                messages.add(LocaleLoader.getString("Ability.Generic.Template", LocaleLoader.getString("Salvage.Arcane.ExtractPartial"), percent.format(salvageManager.getExtractPartialEnchantChance() / 100)));
            }
        }

        return messages;
    }

    @Override
    protected @NotNull List<Component> getTextComponents(@NotNull McMMOPlayer mmoPlayer) {
        List<Component> textComponents = new ArrayList<>();

        TextComponentFactory.getSubSkillTextComponents(mmoPlayer, textComponents, PrimarySkillType.SALVAGE);

        return textComponents;
    }
}
