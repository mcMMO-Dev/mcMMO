//package com.gmail.nossr50.commands.skills;
//
//import com.gmail.nossr50.datatypes.player.McMMOPlayer;
//import com.gmail.nossr50.datatypes.skills.PrimarySkillType;
//import com.gmail.nossr50.util.player.UserManager;
//import com.gmail.nossr50.util.skills.CombatUtils;
//import com.gmail.nossr50.util.skills.SkillUtils;
//import com.gmail.nossr50.util.text.TextComponentFactory;
//import net.kyori.adventure.text.Component;
//import org.bukkit.ChatColor;
//import org.bukkit.entity.Player;
//
//import java.util.ArrayList;
//import java.util.List;
//
//import static com.gmail.nossr50.datatypes.skills.SubSkillType.MACES_MACES_LIMIT_BREAK;
//
//public class MacesCommand extends SkillCommand {
//
//    public MacesCommand() {
//        super(PrimarySkillType.MACES);
//    }
//
//    @Override
//    protected void dataCalculations(Player player, float skillValue) {}
//
//    @Override
//    protected void permissionsCheck(Player player) {}
//
//    @Override
//    protected List<String> statsDisplay(Player player, float skillValue, boolean hasEndurance, boolean isLucky) {
//        List<String> messages = new ArrayList<>();
//        McMMOPlayer mmoPlayer = UserManager.getPlayer(player);
//        if (mmoPlayer == null) {
//            return messages;
//        }
//
//        if(SkillUtils.canUseSubskill(player, MACES_MACES_LIMIT_BREAK)) {
//            messages.add(getStatMessage(MACES_MACES_LIMIT_BREAK,
//                    String.valueOf(CombatUtils.getLimitBreakDamageAgainstQuality(player, MACES_MACES_LIMIT_BREAK, 1000))));
//        }
//
//        messages.add(ChatColor.GRAY + "The Maces skill is a work in progress and is still being developed," +
//                " feedback would be appreciated in the mcMMO discord server.");
//        return messages;
//    }
//
//    @Override
//    protected List<Component> getTextComponents(Player player) {
//        List<Component> textComponents = new ArrayList<>();
//
//        TextComponentFactory.getSubSkillTextComponents(player, textComponents, PrimarySkillType.MACES);
//
//        return textComponents;
//    }
//}
