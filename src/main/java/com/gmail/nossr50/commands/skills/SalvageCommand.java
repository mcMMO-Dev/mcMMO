package com.gmail.nossr50.commands.skills;

import com.gmail.nossr50.datatypes.skills.PrimarySkillType;
import com.gmail.nossr50.datatypes.skills.SubSkillType;
import com.gmail.nossr50.mcMMO;
import com.gmail.nossr50.skills.salvage.Salvage;
import com.gmail.nossr50.skills.salvage.SalvageManager;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class SalvageCommand extends SkillCommand {
    private boolean canScrapCollector;
    private boolean canArcaneSalvage;

    public SalvageCommand(mcMMO pluginRef) {
        super(PrimarySkillType.SALVAGE, pluginRef);
    }

    @Override
    protected void dataCalculations(Player player, double skillValue) {
        // TODO Auto-generated method stub

    }

    @Override
    protected void permissionsCheck(Player player) {
        canScrapCollector = canUseSubskill(player, SubSkillType.SALVAGE_SCRAP_COLLECTOR);
        canArcaneSalvage = canUseSubskill(player, SubSkillType.SALVAGE_ARCANE_SALVAGE);
    }

    @Override
    protected List<String> statsDisplay(Player player, double skillValue, boolean hasEndurance, boolean isLucky) {
        List<String> messages = new ArrayList<>();
        SalvageManager salvageManager = pluginRef.getUserManager().getPlayer(player).getSalvageManager();

        if (canScrapCollector) {
            messages.add(getStatMessage(false, true,
                    SubSkillType.SALVAGE_SCRAP_COLLECTOR,
                    String.valueOf(pluginRef.getRankTools().getRank(player, SubSkillType.SALVAGE_SCRAP_COLLECTOR)),
                    pluginRef.getRankTools().getHighestRankStr(SubSkillType.SALVAGE_SCRAP_COLLECTOR)));
        }

        if (canArcaneSalvage) {
            messages.add(getStatMessage(false, true, SubSkillType.SALVAGE_ARCANE_SALVAGE,
                    String.valueOf(salvageManager.getArcaneSalvageRank()),
                    String.valueOf(pluginRef.getRankTools().getHighestRank(SubSkillType.SALVAGE_ARCANE_SALVAGE))));

            if (Salvage.arcaneSalvageEnchantLoss) {
                messages.add(pluginRef.getLocaleManager().getString("Ability.Generic.Template", pluginRef.getLocaleManager().getString("Salvage.Arcane.ExtractFull"), percent.format(salvageManager.getExtractFullEnchantChance() / 100)));
            }

            if (Salvage.arcaneSalvageDowngrades) {
                messages.add(pluginRef.getLocaleManager().getString("Ability.Generic.Template", pluginRef.getLocaleManager().getString("Salvage.Arcane.ExtractPartial"), percent.format(salvageManager.getExtractPartialEnchantChance() / 100)));
            }
        }

        return messages;
    }

    @Override
    protected List<TextComponent> getTextComponents(Player player) {
        List<TextComponent> textComponents = new ArrayList<>();

        pluginRef.getTextComponentFactory().getSubSkillTextComponents(player, textComponents, PrimarySkillType.SALVAGE);

        return textComponents;
    }
}
