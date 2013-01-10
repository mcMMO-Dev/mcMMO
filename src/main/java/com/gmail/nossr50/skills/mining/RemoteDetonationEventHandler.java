package com.gmail.nossr50.skills.mining;

import java.util.HashSet;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.entity.TNTPrimed;
import org.bukkit.event.player.PlayerInteractEvent;

import com.gmail.nossr50.mcMMO;
import com.gmail.nossr50.datatypes.AbilityType;
import com.gmail.nossr50.datatypes.PlayerProfile;
import com.gmail.nossr50.locale.LocaleLoader;
import com.gmail.nossr50.util.Misc;
import com.gmail.nossr50.util.Skills;

public class RemoteDetonationEventHandler {
    private Player player;
    private PlayerProfile profile;

    private PlayerInteractEvent event;
    protected Block block;

    private HashSet<Byte> transparentBlocks = new HashSet<Byte>();

    public RemoteDetonationEventHandler(MiningManager manager, PlayerInteractEvent event) {
        this.player = manager.getPlayer();
        this.profile = manager.getProfile();

        this.event = event;
        this.block = event.getClickedBlock();
    }

    protected void targetTNT() {
        if (block == null || block.getType() != Material.TNT) {
            generateTransparentBlockList();
            block = player.getTargetBlock(transparentBlocks, BlastMining.MAXIMUM_REMOTE_DETONATION_DISTANCE);
        }
        else {
            event.setCancelled(true); // This is the only way I know to avoid the original TNT to be triggered (in case the player is close to it)
        }
    }

    protected boolean cooldownOver() {
        if (!Skills.cooldownOver(profile.getSkillDATS(AbilityType.BLAST_MINING) * Misc.TIME_CONVERSION_FACTOR, AbilityType.BLAST_MINING.getCooldown(), player)) {
            player.sendMessage(LocaleLoader.getString("Skills.TooTired") + ChatColor.YELLOW + " (" + Skills.calculateTimeLeft(profile.getSkillDATS(AbilityType.BLAST_MINING) * Misc.TIME_CONVERSION_FACTOR, AbilityType.BLAST_MINING.getCooldown(), player) + "s)");

            return false;
        }

        return true;
    }

    protected void sendMessages() {
        Misc.sendSkillMessage(player, AbilityType.BLAST_MINING.getAbilityPlayer(player));
        player.sendMessage(LocaleLoader.getString("Mining.Blast.Boom"));
    }

    protected void handleDetonation() {
        TNTPrimed tnt = player.getWorld().spawn(block.getLocation(), TNTPrimed.class);
        mcMMO.p.addToTNTTracker(tnt.getEntityId(), player.getName());
        tnt.setFuseTicks(0);

        block.setType(Material.AIR);
    }

    protected void setProfileData() {
        profile.setSkillDATS(AbilityType.BLAST_MINING, System.currentTimeMillis());
        profile.setAbilityInformed(AbilityType.BLAST_MINING, false);
    }

    private void generateTransparentBlockList() {
        transparentBlocks.add((byte) Material.AIR.getId());
        transparentBlocks.add((byte) Material.SAPLING.getId());
        transparentBlocks.add((byte) Material.POWERED_RAIL.getId());
        transparentBlocks.add((byte) Material.DETECTOR_RAIL.getId());
        transparentBlocks.add((byte) Material.LONG_GRASS.getId());
        transparentBlocks.add((byte) Material.DEAD_BUSH.getId());
        transparentBlocks.add((byte) Material.YELLOW_FLOWER.getId());
        transparentBlocks.add((byte) Material.RED_ROSE.getId());
        transparentBlocks.add((byte) Material.BROWN_MUSHROOM.getId());
        transparentBlocks.add((byte) Material.RED_MUSHROOM.getId());
        transparentBlocks.add((byte) Material.TORCH.getId());
        transparentBlocks.add((byte) Material.FIRE.getId());
        transparentBlocks.add((byte) Material.REDSTONE_WIRE.getId());
        transparentBlocks.add((byte) Material.CROPS.getId());
        transparentBlocks.add((byte) Material.LADDER.getId());
        transparentBlocks.add((byte) Material.RAILS.getId());
        transparentBlocks.add((byte) Material.LEVER.getId());
        transparentBlocks.add((byte) Material.REDSTONE_TORCH_OFF.getId());
        transparentBlocks.add((byte) Material.REDSTONE_TORCH_ON.getId());
        transparentBlocks.add((byte) Material.STONE_BUTTON.getId());
        transparentBlocks.add((byte) Material.SNOW.getId());
        transparentBlocks.add((byte) Material.SUGAR_CANE_BLOCK.getId());
        transparentBlocks.add((byte) Material.PORTAL.getId());
        transparentBlocks.add((byte) Material.DIODE_BLOCK_OFF.getId());
        transparentBlocks.add((byte) Material.DIODE_BLOCK_ON.getId());
        transparentBlocks.add((byte) Material.PUMPKIN_STEM.getId());
        transparentBlocks.add((byte) Material.MELON_STEM.getId());
        transparentBlocks.add((byte) Material.VINE.getId());
        transparentBlocks.add((byte) Material.WATER_LILY.getId());
        transparentBlocks.add((byte) Material.NETHER_WARTS.getId());
        transparentBlocks.add((byte) Material.ENDER_PORTAL.getId());
        transparentBlocks.add((byte) Material.COCOA.getId());
        transparentBlocks.add((byte) Material.TRIPWIRE_HOOK.getId());
        transparentBlocks.add((byte) Material.TRIPWIRE.getId());
        transparentBlocks.add((byte) Material.FLOWER_POT.getId());
        transparentBlocks.add((byte) Material.CARROT.getId());
        transparentBlocks.add((byte) Material.POTATO.getId());
        transparentBlocks.add((byte) Material.WOOD_BUTTON.getId());
        transparentBlocks.add((byte) Material.SKULL.getId());
    }
}
