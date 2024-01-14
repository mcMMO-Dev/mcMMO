package com.gmail.nossr50.commands.skills;

import com.gmail.nossr50.datatypes.player.McMMOPlayer;
import com.gmail.nossr50.datatypes.skills.PrimarySkillType;
import com.gmail.nossr50.datatypes.skills.SubSkillType;
import com.gmail.nossr50.util.player.UserManager;
import com.gmail.nossr50.util.skills.CombatUtils;
import com.gmail.nossr50.util.skills.SkillUtils;
import com.gmail.nossr50.util.text.TextComponentFactory;
import net.kyori.adventure.text.Component;
import org.bukkit.ChatColor;
import org.bukkit.entity.Cat;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

import static com.gmail.nossr50.datatypes.skills.SubSkillType.*;

public class TridentsCommand extends SkillCommand {


    public TridentsCommand() {
        super(PrimarySkillType.TRIDENTS);
    }

    @Override
    protected void dataCalculations(Player player, float skillValue) {}

    @Override
    protected void permissionsCheck(Player player) {}

    @Override
    protected List<String> statsDisplay(Player player, float skillValue, boolean hasEndurance, boolean isLucky) {
        List<String> messages = new ArrayList<>();
        McMMOPlayer mmoPlayer = UserManager.getPlayer(player);
        if (mmoPlayer == null) {
            return messages;
        }

        messages.add(ChatColor.DARK_AQUA + "Reminder: " + ChatColor.GOLD + "This is a BETA version of mcMMO, please report any bugs you find!");
        messages.add(ChatColor.GOLD + "Tridents is a " + ChatColor.RED + "WIP" +ChatColor.GOLD + " skill that is still being developed, please leave feedback in our discord!");

//        if (SkillUtils.canUseSubskill(player, TRIDENTS_SUPER)) {
//            messages.add("Tridents Super Ability");
//            //TODO: Implement Tridents Super
//        }

        if(SkillUtils.canUseSubskill(player, TRIDENTS_TRIDENTS_LIMIT_BREAK)) {
            messages.add(getStatMessage(TRIDENTS_TRIDENTS_LIMIT_BREAK,
                    String.valueOf(CombatUtils.getLimitBreakDamageAgainstQuality(player, TRIDENTS_TRIDENTS_LIMIT_BREAK, 1000))));
        }

        if(SkillUtils.canUseSubskill(player, TRIDENTS_IMPALE)) {
            messages.add(getStatMessage(TRIDENTS_IMPALE,
                    String.valueOf(mmoPlayer.getTridentsManager().impaleDamageBonus())));
        }

        return messages;
    }

    @Override
    protected List<Component> getTextComponents(Player player) {
        List<Component> textComponents = new ArrayList<>();

        TextComponentFactory.getSubSkillTextComponents(player, textComponents, PrimarySkillType.TRIDENTS);

        return textComponents;
    }
}
