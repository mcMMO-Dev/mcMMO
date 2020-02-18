package com.gmail.nossr50.commands.admin;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.*;
import com.gmail.nossr50.mcMMO;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

@CommandAlias("nbttools")
@Description("Read or Modify values of NBT on an item in-hand")
public class NBTToolsCommand extends BaseCommand {

    public static final String STYLE_TEXT_1 = "//////////";
    @Dependency
    private mcMMO plugin;

    @Default
    public void onCommand(Player player) {
        player.sendMessage("hi");
    }

    /**
     * Show the NBT tags of an item in hand
     */
    @Subcommand("showtags")
    public void onShowTags(Player player) {
        //Show NBT tags to player
        player.sendMessage(STYLE_TEXT_1 + " NBT TOOLS " + STYLE_TEXT_1);
        player.sendMessage("NBT Analysis: " + player.getInventory().getItemInMainHand().getType().getKey().toString());
        player.sendMessage(STYLE_TEXT_1 + STYLE_TEXT_1);
        plugin.getNbtManager().printNBT(player.getInventory().getItemInMainHand(), player);
        player.sendMessage(ChatColor.GRAY + "NBT Analysis completed!");
    }

    @Subcommand("add")
    public void onAddTags(Player player) {

    }

    @Subcommand("remove")
    public void onRemoveTags(Player player) {

    }
}
