package com.gmail.nossr50.commands.admin;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.*;
import com.gmail.nossr50.mcMMO;
import net.kyori.text.TextComponent;
import net.kyori.text.adapter.bukkit.TextAdapter;
import net.kyori.text.format.TextColor;
import net.kyori.text.serializer.gson.GsonComponentSerializer;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

@CommandAlias("nbttools")
@Description("Read or Modify values of NBT on an item in-hand")
public class NBTToolsCommand extends BaseCommand {

    public static final String STYLE_TEXT_1 = "//////////";
    @Dependency
    private mcMMO plugin;

    @Default
    @CommandPermission("mcmmo.commands.nbttools")
    public void onCommand(Player player) {
        //TODO: Add some help messages
        player.sendMessage("hi");
    }

    /**
     * Show the NBT tags of an item in hand
     */
    @Subcommand("tags show")
    public void onShowTags(Player player) {
        final TextComponent textComponent = TextComponent.builder()
                .content(plugin.getLocaleManager().getString("mcMMO.Template.Prefix"))
                .append("NBT Tools")
                .color(TextColor.GOLD)
                .append(" - ")
                .append("Showing NBT Tags (")
                .append(player.getInventory().getItemInMainHand().getType().getKey().toString())
                .color(TextColor.GREEN)
                .append(")")
                .color(TextColor.GOLD)
                .append(TextComponent.newline())
                .build();

        String json = GsonComponentSerializer.INSTANCE.serialize(textComponent);
        TextAdapter.sendMessage(player, textComponent);

        //Show NBT tags to player
        player.sendMessage(STYLE_TEXT_1 + " NBT TOOLS " + STYLE_TEXT_1);
        player.sendMessage("NBT Analysis: " + player.getInventory().getItemInMainHand().getType().getKey().toString());
        player.sendMessage(STYLE_TEXT_1 + STYLE_TEXT_1);
        plugin.getNbtManager().printNBT(player.getInventory().getItemInMainHand(), player);
        player.sendMessage(ChatColor.GRAY + "NBT Analysis completed!");
    }

    @Subcommand("tags set")
    public void onAddTags(Player player, String[] args) {
        if(args.length == 0) {
            player.sendMessage("No arguments provided!");
        } else if(args.length == 1) {
            player.sendMessage("Not enough arguments provided!");
        } else {
            player.sendMessage("Modifying NBT on item in hand...");
            String targetTag = args[0];

            //Check for the tag

        }
    }

    @Subcommand("tags remove")
    public void onRemoveTags(Player player, String[] args) {
        if(args.length == 0) {
            player.sendMessage("No arguments provided!");
        } else if(args.length == 1) {
            player.sendMessage("Not enough arguments provided!");
        } else {
            player.sendMessage("Modifying NBT on item in hand...");
            String targetTag = args[0];

            //Check for the tag

        }
    }

    @Subcommand("tags clear")
    public void onClearTags(Player player) {
        //Clear all NBT tags that fall under "tag" on the item

        player.sendMessage("Clearing NBT on item...");
    }
}
