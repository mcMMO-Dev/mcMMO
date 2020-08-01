package com.gmail.nossr50.util.input;

import com.gmail.nossr50.config.Config;
import com.gmail.nossr50.datatypes.player.McMMOPlayer;
import com.gmail.nossr50.datatypes.skills.PrimarySkillType;
import com.gmail.nossr50.datatypes.skills.subskills.taming.CallOfTheWildType;
import com.gmail.nossr50.events.fake.FakePlayerAnimationEvent;
import com.gmail.nossr50.mcMMO;
import com.gmail.nossr50.skills.herbalism.HerbalismManager;
import com.gmail.nossr50.skills.mining.MiningManager;
import com.gmail.nossr50.skills.taming.TamingManager;
import com.gmail.nossr50.util.BlockUtils;
import com.gmail.nossr50.util.ChimaeraWing;
import com.gmail.nossr50.util.EventUtils;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

public class AbilityActivationProcessor {
    private final McMMOPlayer mmoPlayer;
    private final Player player;

    public AbilityActivationProcessor(McMMOPlayer mmoPlayer) {
        this.mmoPlayer = mmoPlayer;
        this.player = mmoPlayer.getPlayer();
    }

    /**
     * Checks to see if the player is holding a tool
     */
    public boolean isHoldingTool() {
        return mcMMO.getMaterialMapStore().isTool(player.getInventory().getItemInMainHand().getType());
    }


    public void processAbilityAndToolActivations(PlayerInteractEvent event) {
        switch(event.getAction()) {
            case LEFT_CLICK_BLOCK:
                processLeftClickBlock();
                break;
            case RIGHT_CLICK_BLOCK:
                processRightClickBlock(event);
                break;
            case LEFT_CLICK_AIR:
                processLeftClickAir();
                break;
            case RIGHT_CLICK_AIR:
                processRightClickAir();
                break;
            case PHYSICAL:
                break;
        }

    }

    private void processRightClickBlock(PlayerInteractEvent playerInteractEvent) {
        /*
            Right clicked on block
         */
        if(player.getInventory().getItemInOffHand().getType() != Material.AIR && !player.isInsideVehicle() && !player.isSneaking()) {
            return;
        }

        Block block = playerInteractEvent.getClickedBlock();

        if(block == null)
            return;

        BlockState blockState = block.getState();

        if(playerInteractEvent.getClickedBlock() != null) {
            if (BlockUtils.canActivateTools(playerInteractEvent.getClickedBlock())) {
                if (Config.getInstance().getAbilitiesEnabled()) {
                    if (BlockUtils.canActivateHerbalism(playerInteractEvent.getClickedBlock())) {
                        getSuperAbilityManager().processAbilityActivation(PrimarySkillType.HERBALISM);
                    }

                    getSuperAbilityManager().processAbilityActivation(PrimarySkillType.AXES);
                    getSuperAbilityManager().processAbilityActivation(PrimarySkillType.EXCAVATION);
                    getSuperAbilityManager().processAbilityActivation(PrimarySkillType.MINING);
                    getSuperAbilityManager().processAbilityActivation(PrimarySkillType.SWORDS);
                    getSuperAbilityManager().processAbilityActivation(PrimarySkillType.UNARMED);
                    getSuperAbilityManager().processAbilityActivation(PrimarySkillType.WOODCUTTING);
                }

                //TODO: Move this
                ChimaeraWing.activationCheck(player);
            }
        }

        /* GREEN THUMB CHECK */
        HerbalismManager herbalismManager = mmoPlayer.getHerbalismManager();

        if (getHeldItem().getType() == Material.BONE_MEAL) {
            switch (blockState.getType()) {
                case BEETROOTS:
                case CARROT:
                case COCOA:
                case WHEAT:
                case NETHER_WART_BLOCK:
                case POTATO:
                    mcMMO.getPlaceStore().setFalse(blockState);
            }
        }

        FakePlayerAnimationEvent fakeSwing = new FakePlayerAnimationEvent(playerInteractEvent.getPlayer()); //PlayerAnimationEvent compat
        if (herbalismManager.canGreenThumbBlock(blockState)) {
            Bukkit.getPluginManager().callEvent(fakeSwing);
            player.getInventory().setItemInMainHand(new ItemStack(Material.WHEAT_SEEDS, getHeldItem().getAmount() - 1));
            if (herbalismManager.processGreenThumbBlocks(blockState) && EventUtils.simulateBlockBreak(block, player, false)) {
                blockState.update(true);
            }
        }

        /* SHROOM THUMB CHECK */
        else if (herbalismManager.canUseShroomThumb(blockState)) {
            Bukkit.getPluginManager().callEvent(fakeSwing);
            playerInteractEvent.setCancelled(true);
            if (herbalismManager.processShroomThumb(blockState) && EventUtils.simulateBlockBreak(block, player, false)) {
                blockState.update(true);
            }
        }
    }

    private void processRightClickAir() {
        if(player.getInventory().getItemInOffHand().getType() != Material.AIR && !player.isInsideVehicle() && !player.isSneaking()) {
            return;
        }

        /* ACTIVATION CHECKS */
        if (Config.getInstance().getAbilitiesEnabled()) {
            getSuperAbilityManager().processAbilityActivation(PrimarySkillType.AXES);
            getSuperAbilityManager().processAbilityActivation(PrimarySkillType.EXCAVATION);
            getSuperAbilityManager().processAbilityActivation(PrimarySkillType.HERBALISM);
            getSuperAbilityManager().processAbilityActivation(PrimarySkillType.MINING);
            getSuperAbilityManager().processAbilityActivation(PrimarySkillType.SWORDS);
            getSuperAbilityManager().processAbilityActivation(PrimarySkillType.UNARMED);
            getSuperAbilityManager().processAbilityActivation(PrimarySkillType.WOODCUTTING);
        }

        /* ITEM CHECKS */
        ChimaeraWing.activationCheck(player);

        //TODO: This is strange, why is this needed?
        //TODO: This is strange, why is this needed?
        //TODO: This is strange, why is this needed?
        //TODO: This is strange, why is this needed?
        //TODO: This is strange, why is this needed?
        /* BLAST MINING CHECK */
        MiningManager miningManager = mmoPlayer.getMiningManager();
        if (miningManager.canDetonate()) {
            miningManager.remoteDetonation();
        }
    }

    private void processLeftClickBlock() {
        processCallOfTheWildActivation();
    }

    private void processLeftClickAir() {
        processCallOfTheWildActivation();
    }

    private void processCallOfTheWildActivation() {
        if (!player.isSneaking()) {
            return;
        }

        /* CALL OF THE WILD CHECKS */
        Material type = getHeldItem().getType();
        TamingManager tamingManager = mmoPlayer.getTamingManager();

        if (type == Config.getInstance().getTamingCOTWMaterial(CallOfTheWildType.WOLF.getConfigEntityTypeEntry())) {
            tamingManager.summonWolf();
        }
        else if (type == Config.getInstance().getTamingCOTWMaterial(CallOfTheWildType.CAT.getConfigEntityTypeEntry())) {
            tamingManager.summonOcelot();
        }
        else if (type == Config.getInstance().getTamingCOTWMaterial(CallOfTheWildType.HORSE.getConfigEntityTypeEntry())) {
            tamingManager.summonHorse();
        }
    }

    private SuperAbilityManager getSuperAbilityManager() {
        return mmoPlayer.getSuperAbilityManager();
    }

    private ItemStack getHeldItem() {
        return player.getInventory().getItemInMainHand();
    }

}
