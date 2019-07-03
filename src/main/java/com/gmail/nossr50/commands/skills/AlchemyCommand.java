//package com.gmail.nossr50.commands.skills;
//
//import com.gmail.nossr50.datatypes.skills.PrimarySkillType;
//import com.gmail.nossr50.util.TextComponentFactory;
//import net.md_5.bungee.api.chat.TextComponent;
//import org.bukkit.entity.Player;
//
//import java.util.ArrayList;
//import java.util.List;
//
//public class AlchemyCommand extends SkillCommand {
////    private String brewSpeed;
////    private String brewSpeedLucky;
////
////    private int tier;
////    private int ingredientCount;
////    private String ingredientList;
////
////    private boolean canCatalysis;
////    private boolean canConcoctions;
//
//    public AlchemyCommand() {
//        super(PrimarySkillType.ALCHEMY);
//    }
//
////    protected String[] calculateAbilityDisplayValues(Player player) {
////        //TODO: Needed?
////        if (pluginRef.getUserManager().getPlayer(player) == null) {
////            player.sendMessage(pluginRef.getLocaleManager().getString("Profile.PendingLoad"));
////            return new String[]{"DATA NOT LOADED", "DATA NOT LOADED"};
////        }
////
////        AlchemyManager alchemyManager = pluginRef.getUserManager().getPlayer(player).getAlchemyManager();
////        String[] displayValues = new String[2];
////
////        boolean isLucky = Permissions.lucky(player, PrimarySkillType.ALCHEMY);
////
////        displayValues[0] = decimal.format(alchemyManager.calculateBrewSpeed(false)) + "x";
////        displayValues[1] = isLucky ? decimal.format(alchemyManager.calculateBrewSpeed(true)) + "x" : null;
////
////        return displayValues;
////    }
//
//    @Override
//    protected void dataCalculations(Player player, double skillValue) {
//        // ALCHEMY_CATALYSIS
////        if (canCatalysis) {
////            String[] catalysisStrings = calculateAbilityDisplayValues(player);
////            brewSpeed = catalysisStrings[0];
////            brewSpeedLucky = catalysisStrings[1];
////        }
////
////        // ALCHEMY_CONCOCTIONS
////        if (canConcoctions) {
////            AlchemyManager alchemyManager = pluginRef.getUserManager().getPlayer(player).getAlchemyManager();
////            tier = alchemyManager.getTier();
////            ingredientCount = alchemyManager.getIngredients().size();
////            ingredientList = alchemyManager.getIngredientList();
////        }
//    }
//
//    @Override
//    protected void permissionsCheck(Player player) {
////        canCatalysis = canUseSubskill(player, SubSkillType.ALCHEMY_CATALYSIS);
////        canConcoctions = canUseSubskill(player, SubSkillType.ALCHEMY_CONCOCTIONS);
//    }
//
//    @Override
//    protected List<String> statsDisplay(Player player, double skillValue, boolean hasEndurance, boolean isLucky) {
//        List<String> messages = new ArrayList<>();
//
////        if (canCatalysis) {
////            messages.add(getStatMessage(SubSkillType.ALCHEMY_CATALYSIS, brewSpeed)
////                    + (isLucky ? pluginRef.getLocaleManager().getString("Perks.Lucky.Bonus", brewSpeedLucky) : ""));
////        }
////
////        if (canConcoctions) {
////            messages.add(getStatMessage(false, true, SubSkillType.ALCHEMY_CONCOCTIONS, String.valueOf(tier), String.valueOf(RankUtils.getHighestRank(SubSkillType.ALCHEMY_CONCOCTIONS))));
////            messages.add(getStatMessage(true, true, SubSkillType.ALCHEMY_CONCOCTIONS, String.valueOf(ingredientCount), ingredientList));
////
////            //messages.add(pluginRef.getLocaleManager().getString("Alchemy.Concoctions.Rank", tier, RankUtils.getHighestRank(SubSkillType.ALCHEMY_CONCOCTIONS)));
////            //messages.add(pluginRef.getLocaleManager().getString("Alchemy.Concoctions.Ingredients", ingredientCount, ingredientList));
////        }
//
//        return messages;
//    }
//
//    @Override
//    protected List<TextComponent> getTextComponents(Player player) {
//        List<TextComponent> textComponents = new ArrayList<>();
//
//        TextComponentFactory.getSubSkillTextComponents(player, textComponents, PrimarySkillType.ALCHEMY);
//
//        return textComponents;
//    }
//}
