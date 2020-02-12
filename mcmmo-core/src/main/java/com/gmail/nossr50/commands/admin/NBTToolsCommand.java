package com.gmail.nossr50.commands.admin;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.Default;
import co.aikar.commands.annotation.Dependency;
import co.aikar.commands.annotation.Description;
import com.gmail.nossr50.mcMMO;
import org.bukkit.entity.Player;

@CommandAlias("nbttools")
@Description("Read or Modify values of NBT on an item in-hand")
public class NBTToolsCommand extends BaseCommand {

    @Dependency
    private mcMMO pluginRef;

    @Default
    public void onCommand(Player player) {
        player.sendMessage("hi");
    }
}
