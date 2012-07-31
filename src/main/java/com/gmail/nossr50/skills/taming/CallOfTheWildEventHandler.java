package com.gmail.nossr50.skills.taming;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Ocelot;
import org.bukkit.entity.Player;
import org.bukkit.entity.Tameable;
import org.bukkit.inventory.ItemStack;
import org.bukkit.metadata.FixedMetadataValue;

import com.gmail.nossr50.mcMMO;
import com.gmail.nossr50.locale.LocaleLoader;
import com.gmail.nossr50.util.Misc;

public class CallOfTheWildEventHandler {
    protected Player player;
    protected ItemStack inHand;
    protected EntityType type;
    protected int summonAmount;

    protected CallOfTheWildEventHandler(Player player, EntityType type, int summonAmount) {
        this.player = player;
        this.inHand = player.getItemInHand();
        this.type = type;
        this.summonAmount = summonAmount;
    }

    protected void sendInsufficientAmountMessage() {
        player.sendMessage(LocaleLoader.getString("Skills.NeedMore") + " " + ChatColor.GRAY + Misc.prettyItemString(inHand.getTypeId()));
    }

    protected boolean nearbyEntityExists() {
        boolean entityExists = false;

        for (Entity entity : player.getNearbyEntities(40, 40, 40)) {
            if (entity.getType() == type) {
                entityExists = true;
                break;
            }
        }

        return entityExists;
    }

    protected void sendFailureMessage() {
        if (type == EntityType.OCELOT) {
            player.sendMessage(LocaleLoader.getString("Taming.Summon.Fail.Ocelot"));
        }
        else {
            player.sendMessage(LocaleLoader.getString("Taming.Summon.Fail.Wolf"));
        }
    }

    protected void spawnCreature() {
        LivingEntity entity = (LivingEntity) player.getWorld().spawnEntity(player.getLocation(), type);
        entity.setMetadata("mcmmoSummoned", new FixedMetadataValue(mcMMO.p, true));

        ((Tameable) entity).setOwner(player);

        if (type == EntityType.OCELOT) {
            ((Ocelot) entity).setCatType(Ocelot.Type.getType(1 + Taming.getRandom().nextInt(3)));
        }
        else {
            entity.setHealth(entity.getMaxHealth());
        }
    }

    protected void processResourceCost() {
        int newAmount = inHand.getAmount() - summonAmount;

        if (newAmount == 0) {
            player.setItemInHand(new ItemStack(Material.AIR));
        }
        else {
            player.getItemInHand().setAmount(inHand.getAmount() - summonAmount);
        }
    }

    protected void sendSuccessMessage() {
        player.sendMessage(LocaleLoader.getString("Taming.Summon.Complete"));
    }
}
