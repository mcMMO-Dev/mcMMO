package com.gmail.nossr50.util.input;

import com.gmail.nossr50.config.AdvancedConfig;
import com.gmail.nossr50.config.Config;
import com.gmail.nossr50.datatypes.interactions.NotificationType;
import com.neetgames.mcmmo.player.OnlineMMOPlayer;
import com.gmail.nossr50.datatypes.player.PersistentPlayerData;
import com.gmail.nossr50.datatypes.skills.AbilityToolType;
import com.gmail.nossr50.datatypes.skills.PrimarySkillType;
import com.gmail.nossr50.datatypes.skills.SuperAbilityType;
import com.gmail.nossr50.mcMMO;
import com.gmail.nossr50.runnables.skills.AbilityDisableTask;
import com.gmail.nossr50.runnables.skills.ToolLowerTask;
import com.gmail.nossr50.util.EventUtils;
import com.gmail.nossr50.util.Misc;
import com.gmail.nossr50.util.player.NotificationManager;
import com.gmail.nossr50.util.skills.PerksUtils;
import com.gmail.nossr50.util.skills.RankUtils;
import com.gmail.nossr50.util.skills.SkillUtils;
import com.gmail.nossr50.util.sounds.SoundManager;
import com.gmail.nossr50.util.sounds.SoundType;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;

public class SuperAbilityManager {

    private final OnlineMMOPlayer mmoPlayer;
    private final Player player;

    private final Map<SuperAbilityType, Boolean> superAbilityState = new HashMap<>();
    private final Map<SuperAbilityType, Boolean> abilityInformed = new HashMap<>();

    private boolean abilityActivationPermission = true;

    private final Map<AbilityToolType, Boolean> toolMode = new HashMap<>();
    private final PersistentPlayerData persistentPlayerData;

    public SuperAbilityManager(@NotNull OnlineMMOPlayer mmoPlayer, @NotNull PersistentPlayerData persistentPlayerData) {
        this.mmoPlayer = mmoPlayer;
        this.persistentPlayerData = persistentPlayerData;
        this.player = mmoPlayer.getPlayer();

        for (SuperAbilityType superAbilityType : SuperAbilityType.values()) {
            superAbilityState.put(superAbilityType, false);
            abilityInformed.put(superAbilityType, true); // This is intended
        }

        for (AbilityToolType abilityToolType : AbilityToolType.values()) {
            toolMode.put(abilityToolType, false);
        }
    }

    public void processAbilityActivation(PrimarySkillType skill) {
        Player player = mmoPlayer.getPlayer();

        if (!skill.getPermissions(player)) {
            return;
        }

        if (Config.getInstance().getAbilitiesOnlyActivateWhenSneaking() && !player.isSneaking()) {
            return;
        }

        ItemStack inHand = player.getInventory().getItemInMainHand();

        if (mcMMO.getModManager().isCustomTool(inHand) && !mcMMO.getModManager().getTool(inHand).isAbilityEnabled()) {
            return;
        }

        if (!getAbilityActivationPermission()) {
            return;
        }

        //Don't activate 2 abilities at once
        for (SuperAbilityType superAbilityType : SuperAbilityType.values()) {
            if (getAbilityMode(superAbilityType)) {
                return;
            }
        }

        SuperAbilityType ability = skill.getSuperAbilityType();

        /*
         * Woodcutting & Axes need to be treated differently.
         * Basically the tool always needs to ready and we check to see if the cooldown is over when the user takes action
         */

        /*
         * Woodcutting & Axes need to be treated differently.
         * Basically the tool always needs to ready and we check to see if the cooldown is over when the user takes action
         */
        //TODO: Convert this later
        if (mmoPlayer.getAbilityActivationProcessor().isHoldingTool() && !isAbilityToolPrimed(tool)) {
            if (skill != PrimarySkillType.WOODCUTTING && skill != PrimarySkillType.AXES) {
                int timeRemaining = calculateTimeRemaining(ability);

                if (isAbilityOnCooldown(ability)) {
                    NotificationManager.sendPlayerInformation(player, NotificationType.ABILITY_COOLDOWN, "Skills.TooTired", String.valueOf(timeRemaining));
                    return;
                }
            }

            if (Config.getInstance().getAbilityMessagesEnabled()) {
                /*
                 *
                 * IF THE TOOL IS AN AXE
                 *
                 */
                if(tool == ToolType.AXE) {
                    processAxeToolMessages();
                } else {
                    NotificationManager.sendPlayerInformation(player, NotificationType.TOOL, tool.getRaiseTool());
                }

                //Send Sound
                SoundManager.sendSound(player, player.getLocation(), SoundType.TOOL_READY);
            }

            setToolPreparationMode(tool, true);
            new ToolLowerTask(this, tool).runTaskLater(mcMMO.p, 4 * Misc.TICK_CONVERSION_FACTOR);
        }



        //TODO: Older code below
        if (mmoPlayer.getAbilityActivationProcessor().isHoldingTool() && !isAbilityToolPrimed(tool)) {
            if (skill != PrimarySkillType.WOODCUTTING && skill != PrimarySkillType.AXES) {
                int timeRemaining = calculateTimeRemaining(ability);

                if (!getAbilityMode(ability) && timeRemaining > 0) {
                    NotificationManager.sendPlayerInformation(player, NotificationType.ABILITY_COOLDOWN, "Skills.TooTired", String.valueOf(timeRemaining));
                    return;
                }
            }

            if (Config.getInstance().getAbilityMessagesEnabled()) {
                NotificationManager.sendPlayerInformation(player, NotificationType.TOOL, tool.getRaiseToolLocaleKey());
                SoundManager.sendSound(player, player.getLocation(), SoundType.TOOL_READY);
            }

            setAbilityToolPrime(tool, true);
            new ToolLowerTask(mmoPlayer, tool).runTaskLater(mcMMO.p, 4 * Misc.TICK_CONVERSION_FACTOR);
        }
    }

    /**
     * Check to see if an ability can be activated.
     *
     * @param primarySkillType The primarySkillType the ability is based on
     */
    public void checkAbilityActivation(PrimarySkillType primarySkillType) {
        AbilityToolType tool = primarySkillType.getTool();
        SuperAbilityType ability = primarySkillType.getSuperAbilityType();

        if (getAbilityMode(ability) || !ability.getPermissions(player)) {
            return;
        }

        //TODO: This is hacky and temporary solution until skills are move to the new system
        //Potential problems with this include skills with two super abilities (ie mining)
        if(!primarySkillType.isSuperAbilityUnlocked(player))
        {
            int diff = RankUtils.getSuperAbilityUnlockRequirement(primarySkillType.getSuperAbilityType()) - mmoPlayer.getSkillLevel(primarySkillType);

            //Inform the player they are not yet skilled enough
            NotificationManager.sendPlayerInformation(player, NotificationType.ABILITY_COOLDOWN, "Skills.AbilityGateRequirementFail", String.valueOf(diff), primarySkillType.getName());
            return;
        }

        int timeRemaining = calculateTimeRemaining(ability);

        if (timeRemaining > 0) {
            /*
             * Axes and Woodcutting are odd because they share the same tool.
             * We show them the too tired message when they take action.
             */
            if (primarySkillType == PrimarySkillType.WOODCUTTING || primarySkillType == PrimarySkillType.AXES) {
                NotificationManager.sendPlayerInformation(player, NotificationType.ABILITY_COOLDOWN, "Skills.TooTired", String.valueOf(timeRemaining));
                //SoundManager.sendSound(player, player.getLocation(), SoundType.TIRED);
            }

            return;
        }

        if (EventUtils.callPlayerAbilityActivateEvent(player, primarySkillType).isCancelled()) {
            return;
        }

        //These values change depending on whether or not the server is in retro mode
        int abilityLengthVar = AdvancedConfig.getInstance().getAbilityLength();
        int abilityLengthCap = AdvancedConfig.getInstance().getAbilityLengthCap();

        int ticks;

        //Ability cap of 0 or below means no cap
        if(abilityLengthCap > 0)
        {
            ticks = PerksUtils.handleActivationPerks(player, 2 + (Math.min(abilityLengthCap, mmoPlayer.getSkillLevel(primarySkillType)) / abilityLengthVar), ability.getMaxLength());
        } else {
            ticks = PerksUtils.handleActivationPerks(player, 2 + (mmoPlayer.getSkillLevel(primarySkillType) / abilityLengthVar), ability.getMaxLength());
        }

        if (mmoPlayer.hasSkillChatNotifications()) {
            NotificationManager.sendPlayerInformation(player, NotificationType.SUPER_ABILITY, ability.getAbilityOn());
            //player.sendMessage(ability.getAbilityOn());
        }

        if (AdvancedConfig.getInstance().sendAbilityNotificationToOtherPlayers()) {
            SkillUtils.sendSkillMessage(player, NotificationType.SUPER_ABILITY_ALERT_OTHERS, ability.getAbilityPlayer());
        }

        //Sounds
        SoundManager.worldSendSound(player.getWorld(), player.getLocation(), SoundType.ABILITY_ACTIVATED_GENERIC);

        // Enable the ability
        mmoPlayer.getPersistentPlayerData().setAbilityDATS(ability, System.currentTimeMillis() + (ticks * Misc.TIME_CONVERSION_FACTOR));
        setAbilityMode(ability, true);

        if (ability == SuperAbilityType.SUPER_BREAKER || ability == SuperAbilityType.GIGA_DRILL_BREAKER) {
            SkillUtils.handleAbilitySpeedIncrease(player);
        }

        setAbilityToolPrime(tool, false);
        new AbilityDisableTask(mmoPlayer, ability).runTaskLater(mcMMO.p, ticks * Misc.TICK_CONVERSION_FACTOR);
    }

    /*
     * Abilities
     */

    /**
     * Reset the mode of all abilities.
     */
    public void disableSuperAbilities() {
        for (SuperAbilityType ability : SuperAbilityType.values()) {
            // Correctly disable and handle any special deactivate code
            new AbilityDisableTask(mmoPlayer, ability).run();
        }
    }

    public void resetCooldowns() {
        this.persistentPlayerData.resetCooldowns();
    }

    /**
     * Get the mode of an ability.
     *
     * @param ability The ability to check
     * @return true if the ability is enabled, false otherwise
     */
    public boolean getAbilityMode(SuperAbilityType ability) {
        return superAbilityState.get(ability);
    }

    /**
     * Set the mode of an ability.
     *
     * @param ability The ability to check
     * @param isActive True if the ability is active, false otherwise
     */
    public void setAbilityMode(SuperAbilityType ability, boolean isActive) {
        superAbilityState.put(ability, isActive);
    }

    /**
     * Get the informed state of an ability
     *
     * @param ability The ability to check
     * @return true if the ability is informed, false otherwise
     */
    public boolean getAbilityInformed(SuperAbilityType ability) {
        return abilityInformed.get(ability);
    }

    /**
     * Set the informed state of an ability.
     *
     * @param ability The ability to check
     * @param isInformed True if the ability is informed, false otherwise
     */
    public void setAbilityInformed(SuperAbilityType ability, boolean isInformed) {
        abilityInformed.put(ability, isInformed);
    }

    /**
     * Whether or not a tool is primed
     *
     * @param abilityToolType ability tool to check
     * @return true if the abilityToolType is primed, false otherwise
     */
    public boolean isAbilityToolPrimed(AbilityToolType abilityToolType) {
        return toolMode.get(abilityToolType);
    }

    public boolean getAbilityActivationPermission() {
        return abilityActivationPermission;
    }

    public void toggleAbilityActivationPermission() {
        abilityActivationPermission = !abilityActivationPermission;
    }

    /*
     * Tools
     */

    /**
     * Reset the prep modes of all tools.
     */
    public void unprimeAllAbilityTools() {
        for (AbilityToolType abilityToolType : AbilityToolType.values()) {
            setAbilityToolPrime(abilityToolType, false);
        }
    }

    /**
     * Set the current prep mode of a abilityToolType.
     *
     * @param abilityToolType Tool to set the mode for
     * @param isPrepared true if the abilityToolType should be prepped, false otherwise
     */
    public void setAbilityToolPrime(AbilityToolType abilityToolType, boolean isPrepared) {
        toolMode.put(abilityToolType, isPrepared);
    }

    /**
     * Calculate the time remaining until the superAbilityType's cooldown expires.
     *
     * @param superAbilityType SuperAbilityType whose cooldown to check
     *
     * @return the number of seconds remaining before the cooldown expires
     */
    public int calculateTimeRemaining(SuperAbilityType superAbilityType) {
        long deactivatedTimestamp = mmoPlayer.getAbilityDATS(superAbilityType) * Misc.TIME_CONVERSION_FACTOR;
        return (int) (((deactivatedTimestamp + (PerksUtils.handleCooldownPerks(mmoPlayer.getPlayer(), superAbilityType.getCooldown()) * Misc.TIME_CONVERSION_FACTOR)) - System.currentTimeMillis()) / Misc.TIME_CONVERSION_FACTOR);
    }

}
