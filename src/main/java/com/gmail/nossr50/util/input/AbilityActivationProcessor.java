package com.gmail.nossr50.util.input;

import com.gmail.nossr50.config.Config;
import com.gmail.nossr50.datatypes.interactions.NotificationType;
import com.gmail.nossr50.util.Misc;
import com.neetgames.mcmmo.player.OnlineMMOPlayer;
import com.gmail.nossr50.datatypes.skills.PrimarySkillType;
import com.gmail.nossr50.datatypes.skills.SubSkillType;
import com.gmail.nossr50.datatypes.skills.SuperAbilityType;
import com.gmail.nossr50.datatypes.skills.subskills.taming.CallOfTheWildType;
import com.gmail.nossr50.events.fake.FakePlayerAnimationEvent;
import com.gmail.nossr50.locale.LocaleLoader;
import com.gmail.nossr50.mcMMO;
import com.gmail.nossr50.skills.herbalism.HerbalismManager;
import com.gmail.nossr50.skills.mining.MiningManager;
import com.gmail.nossr50.skills.taming.TamingManager;
import com.gmail.nossr50.util.BlockUtils;
import com.gmail.nossr50.util.ChimaeraWing;
import com.gmail.nossr50.util.EventUtils;
import com.gmail.nossr50.util.player.NotificationManager;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

public class AbilityActivationProcessor {
    private final @NotNull OnlineMMOPlayer mmoPlayer;
    private final @NotNull Player player;

    public AbilityActivationProcessor(@NotNull OnlineMMOPlayer mmoPlayer) {
        this.mmoPlayer = mmoPlayer;
        this.player = Misc.adaptPlayer(mmoPlayer);
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
        HerbalismManager herbalismManager = ((McMMOPlayer) (mmoPlayer)).getHerbalismManager();

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
            player.getInventory().getItemInMainHand().setAmount(player.getInventory().getItemInMainHand().getAmount() - 1);
            player.updateInventory();
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
        MiningManager miningManager = ((McMMOPlayer) (mmoPlayer)).getMiningManager();
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
        TamingManager tamingManager = ((McMMOPlayer) (mmoPlayer)).getTamingManager();

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

    public void processAxeToolMessages() {
        Block rayCast = player.getTargetBlock(null, 100);

        /*
         * IF BOTH TREE FELLER & SKULL SPLITTER ARE ON CD
         */
        if(isAbilityOnCooldown(SuperAbilityType.TREE_FELLER) && isAbilityOnCooldown(SuperAbilityType.SKULL_SPLITTER)) {
            tooTiredMultiple(PrimarySkillType.WOODCUTTING, SubSkillType.WOODCUTTING_TREE_FELLER, SuperAbilityType.TREE_FELLER, SubSkillType.AXES_SKULL_SPLITTER, SuperAbilityType.SKULL_SPLITTER);
            /*
             * IF TREE FELLER IS ON CD
             * AND PLAYER IS LOOKING AT TREE
             */
        } else if(isAbilityOnCooldown(SuperAbilityType.TREE_FELLER)
                && BlockUtils.isPartOfTree(rayCast)) {
            raiseToolWithCooldowns(SubSkillType.WOODCUTTING_TREE_FELLER, SuperAbilityType.TREE_FELLER);

            /*
             * IF SKULL SPLITTER IS ON CD
             */
        } else if(isAbilityOnCooldown(SuperAbilityType.SKULL_SPLITTER)) {
            raiseToolWithCooldowns(SubSkillType.AXES_SKULL_SPLITTER, SuperAbilityType.SKULL_SPLITTER);
        } else {
            NotificationManager.sendPlayerInformation(player, NotificationType.TOOL, ToolType.AXE.getRaiseTool());
        }
    }

    private void tooTiredMultiple(PrimarySkillType primarySkillType, SubSkillType aSubSkill, SuperAbilityType aSuperAbility, SubSkillType bSubSkill, SuperAbilityType bSuperAbility) {
        String aSuperAbilityCD = LocaleLoader.getString("Skills.TooTired.Named", aSubSkill.getLocaleName(), String.valueOf(calculateTimeRemaining(aSuperAbility)));
        String bSuperAbilityCD = LocaleLoader.getString("Skills.TooTired.Named", bSubSkill.getLocaleName(), String.valueOf(calculateTimeRemaining(bSuperAbility)));
        String allCDStr = aSuperAbilityCD + ", " + bSuperAbilityCD;

        NotificationManager.sendPlayerInformation(player, NotificationType.TOOL, "Skills.TooTired.Extra",
                primarySkillType.getName(),
                allCDStr);
    }

    private void raiseToolWithCooldowns(SubSkillType subSkillType, SuperAbilityType superAbilityType) {
        NotificationManager.sendPlayerInformation(player, NotificationType.TOOL,
                "Axes.Ability.Ready.Extra",
                subSkillType.getLocaleName(),
                String.valueOf(calculateTimeRemaining(superAbilityType)));
    }

    public boolean isAbilityOnCooldown(SuperAbilityType ability) {
        return !getAbilityMode(ability) && calculateTimeRemaining(ability) > 0;
    }

    private SuperSkillManagerImpl getSuperAbilityManager() {
        return mmoPlayer.getSuperAbilityManager();
    }

    private ItemStack getHeldItem() {
        return player.getInventory().getItemInMainHand();
    }

}
