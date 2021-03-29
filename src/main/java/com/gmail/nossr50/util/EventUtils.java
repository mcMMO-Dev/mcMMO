package com.gmail.nossr50.util;

import com.gmail.nossr50.config.Config;
import com.gmail.nossr50.datatypes.experience.XPGainReason;
import com.gmail.nossr50.datatypes.experience.XPGainSource;
import com.gmail.nossr50.datatypes.party.Party;
import com.gmail.nossr50.datatypes.player.McMMOPlayer;
import com.gmail.nossr50.datatypes.player.PlayerProfile;
import com.gmail.nossr50.datatypes.skills.PrimarySkillType;
import com.gmail.nossr50.datatypes.skills.SubSkillType;
import com.gmail.nossr50.datatypes.skills.SuperAbilityType;
import com.gmail.nossr50.datatypes.skills.subskills.AbstractSubSkill;
import com.gmail.nossr50.events.experience.McMMOPlayerLevelChangeEvent;
import com.gmail.nossr50.events.experience.McMMOPlayerLevelDownEvent;
import com.gmail.nossr50.events.experience.McMMOPlayerLevelUpEvent;
import com.gmail.nossr50.events.experience.McMMOPlayerXpGainEvent;
import com.gmail.nossr50.events.fake.*;
import com.gmail.nossr50.events.hardcore.McMMOPlayerPreDeathPenaltyEvent;
import com.gmail.nossr50.events.hardcore.McMMOPlayerStatLossEvent;
import com.gmail.nossr50.events.hardcore.McMMOPlayerVampirismEvent;
import com.gmail.nossr50.events.party.McMMOPartyLevelUpEvent;
import com.gmail.nossr50.events.party.McMMOPartyTeleportEvent;
import com.gmail.nossr50.events.party.McMMOPartyXpGainEvent;
import com.gmail.nossr50.events.players.McMMOPlayerProfileLoadEvent;
import com.gmail.nossr50.events.skills.abilities.McMMOPlayerAbilityActivateEvent;
import com.gmail.nossr50.events.skills.abilities.McMMOPlayerAbilityDeactivateEvent;
import com.gmail.nossr50.events.skills.fishing.McMMOPlayerFishingTreasureEvent;
import com.gmail.nossr50.events.skills.fishing.McMMOPlayerMagicHunterEvent;
import com.gmail.nossr50.events.skills.repair.McMMOPlayerRepairCheckEvent;
import com.gmail.nossr50.events.skills.salvage.McMMOPlayerSalvageCheckEvent;
import com.gmail.nossr50.events.skills.secondaryabilities.SubSkillBlockEvent;
import com.gmail.nossr50.events.skills.secondaryabilities.SubSkillEvent;
import com.gmail.nossr50.events.skills.unarmed.McMMOPlayerDisarmEvent;
import com.gmail.nossr50.locale.LocaleLoader;
import com.gmail.nossr50.mcMMO;
import com.gmail.nossr50.util.player.NotificationManager;
import com.gmail.nossr50.util.player.UserManager;
import com.gmail.nossr50.util.skills.CombatUtils;
import org.bukkit.block.Block;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Entity;
import org.bukkit.entity.FishHook;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.PlayerFishEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginManager;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;

/**
 * This class is meant to help make event related code less boilerplate
 */
public final class EventUtils {

    /**
     * This is a static utility class, therefore we don't want any instances of
     * this class. Making the constructor private prevents accidents like that.
     */
    private EventUtils() {}

    /*
     * Quality of Life methods
     */

    /**
     * This is a simple check to see if an {@link Event} is fake or not.
     * {@link FakeEvent FakeEvents} should not be processed like normally and maybe even 
     * be ignored by other {@link Plugin plugins} completely.
     * 
     * @param event The {@link Event} in question
     * @return Whether this {@link Event} has been faked by mcMMO and should not be processed normally.
     */
    public static boolean isFakeEvent(@NotNull Event event) {
        return event instanceof FakeEvent;
    }

    /**
     * Checks to see if damage is from natural sources
     * This cannot be used to determine if damage is from vanilla MC, it just checks to see if the damage is from a complex behaviour in mcMMO such as bleed.
     *
     * @param event this event
     * @return true if damage is NOT from an unnatural mcMMO skill (such as bleed DOTs)
     */
    public static boolean isDamageFromMcMMOComplexBehaviour(@NotNull Event event) {
        return event instanceof FakeEntityDamageEvent;
    }

    /**
     * This little method is just to make the code more readable
     * 
     * @param entity target entity
     * @return the associated McMMOPlayer for this entity
     */
    public static McMMOPlayer getMcMMOPlayer(@NotNull Entity entity)
    {
        return UserManager.getPlayer((Player)entity);
    }

    /**
     * Checks to see if a Player was damaged in this EntityDamageEvent
     *
     * This method checks for the following things and if they are all true it returns true
     *
     * 1) The player is real and not an NPC
     * 2) The player is not in god mode
     * 3) The damage dealt is above 0
     * 4) The player is loaded into our mcMMO user profiles
     *
     * @param entityDamageEvent
     * @return
     */
    public static boolean isRealPlayerDamaged(@NotNull EntityDamageEvent entityDamageEvent)
    {
        //Make sure the damage is above 0
        double damage = entityDamageEvent.getFinalDamage();

        if (damage <= 0) {
            return false;
        }

        Entity entity = entityDamageEvent.getEntity();

        //Check to make sure the entity is not an NPC
        if(Misc.isNPCEntityExcludingVillagers(entity))
            return false;

        if (!entity.isValid() || !(entity instanceof LivingEntity)) {
            return false;
        }

        LivingEntity livingEntity = (LivingEntity) entity;

        if (CombatUtils.isInvincible(livingEntity, damage)) {
            return false;
        }

        if (livingEntity instanceof Player) {
            Player player = (Player) entity;

            if (!UserManager.hasPlayerDataKey(player)) {
                return true;
            }

            McMMOPlayer mcMMOPlayer = UserManager.getPlayer(player);

            if(mcMMOPlayer == null)
            {
                return true;
            }

            /* Check for invincibility */
            if (mcMMOPlayer.getGodMode()) {
                entityDamageEvent.setCancelled(true);
                return false;
            }

            return true;
        } else {
            return false;
        }
    }

    /*
     * Others
     */

    public static @NotNull McMMOPlayerAbilityActivateEvent callPlayerAbilityActivateEvent(@NotNull Player player, @NotNull PrimarySkillType skill) {
        McMMOPlayerAbilityActivateEvent event = new McMMOPlayerAbilityActivateEvent(player, skill);
        mcMMO.p.getServer().getPluginManager().callEvent(event);

        return event;
    }

    public static @NotNull McMMOPlayerProfileLoadEvent callPlayerProfileLoadEvent(@NotNull Player player, @NotNull PlayerProfile profile){
        McMMOPlayerProfileLoadEvent event = new McMMOPlayerProfileLoadEvent(player, profile);
        mcMMO.p.getServer().getPluginManager().callEvent(event);

        return event;
    }

    /**
     * Calls a new SubSkillEvent for this SubSkill and then returns it
     * @param player target player
     * @param subSkillType target subskill
     * @return the event after it has been fired
     */
    @Deprecated
    public static @NotNull SubSkillEvent callSubSkillEvent(Player player, SubSkillType subSkillType) {
        SubSkillEvent event = new SubSkillEvent(player, subSkillType);
        mcMMO.p.getServer().getPluginManager().callEvent(event);

        return event;
    }

    /**
     * Calls a new SubSkillBlockEvent for this SubSkill and its related block and then returns it
     * @param player target player
     * @param subSkillType target subskill
     * @param block associated block
     * @return the event after it has been fired
     */
    @Deprecated
    public static @NotNull SubSkillBlockEvent callSubSkillBlockEvent(@NotNull Player player, @NotNull SubSkillType subSkillType, @NotNull Block block) {
        SubSkillBlockEvent event = new SubSkillBlockEvent(player, subSkillType, block);
        mcMMO.p.getServer().getPluginManager().callEvent(event);

        return event;
    }

    /**
     * Calls a new SubSkillEvent for this SubSkill and then returns it
     * @param player target player
     * @param abstractSubSkill target subskill
     * @return the event after it has been fired
     */
    public static SubSkillEvent callSubSkillEvent(Player player, AbstractSubSkill abstractSubSkill) {
        SubSkillEvent event = new SubSkillEvent(player, abstractSubSkill);
        mcMMO.p.getServer().getPluginManager().callEvent(event);

        return event;
    }

    public static FakePlayerAnimationEvent callFakeArmSwingEvent(Player player) {
        FakePlayerAnimationEvent event = new FakePlayerAnimationEvent(player);
        mcMMO.p.getServer().getPluginManager().callEvent(event);

        return event;
    }

    public static boolean tryLevelChangeEvent(Player player, PrimarySkillType skill, int levelsChanged, float xpRemoved, boolean isLevelUp, XPGainReason xpGainReason) {
        McMMOPlayerLevelChangeEvent event = isLevelUp ? new McMMOPlayerLevelUpEvent(player, skill, levelsChanged, xpGainReason) : new McMMOPlayerLevelDownEvent(player, skill, levelsChanged, xpGainReason);
        mcMMO.p.getServer().getPluginManager().callEvent(event);

        boolean isCancelled = event.isCancelled();

        if (isCancelled) {
            PlayerProfile profile = UserManager.getPlayer(player).getProfile();

            profile.modifySkill(skill, profile.getSkillLevel(skill) - (isLevelUp ? levelsChanged : -levelsChanged));
            profile.addXp(skill, xpRemoved);
        }

        return isCancelled;
    }

    public static boolean tryLevelChangeEvent(@NotNull McMMOPlayer mmoPlayer, PrimarySkillType skill, int levelsChanged, float xpRemoved, boolean isLevelUp, XPGainReason xpGainReason) {
        McMMOPlayerLevelChangeEvent event = isLevelUp ? new McMMOPlayerLevelUpEvent(mmoPlayer.getPlayer(), skill, levelsChanged, xpGainReason) : new McMMOPlayerLevelDownEvent(mmoPlayer.getPlayer(), skill, levelsChanged, xpGainReason);
        mcMMO.p.getServer().getPluginManager().callEvent(event);

        boolean isCancelled = event.isCancelled();

        if (isCancelled) {
            mmoPlayer.modifySkill(skill, mmoPlayer.getSkillLevel(skill) - (isLevelUp ? levelsChanged : -levelsChanged));
            mmoPlayer.addXp(skill, xpRemoved);
        } else {
            if (isLevelUp) {
                NotificationManager.processLevelUpBroadcasting(mmoPlayer, skill, mmoPlayer.getSkillLevel(skill));
                NotificationManager.processPowerLevelUpBroadcasting(mmoPlayer, mmoPlayer.getPowerLevel());

            }
        }

        return isCancelled;
    }

    public static boolean tryLevelEditEvent(Player player, PrimarySkillType skill, int levelsChanged, float xpRemoved, boolean isLevelUp, XPGainReason xpGainReason, int oldLevel) {
        McMMOPlayerLevelChangeEvent event = isLevelUp ? new McMMOPlayerLevelUpEvent(player, skill, levelsChanged - oldLevel, xpGainReason) : new McMMOPlayerLevelDownEvent(player, skill, levelsChanged, xpGainReason);
        mcMMO.p.getServer().getPluginManager().callEvent(event);

        boolean isCancelled = event.isCancelled();

        if (isCancelled) {
            PlayerProfile profile = UserManager.getPlayer(player).getProfile();

            profile.modifySkill(skill, profile.getSkillLevel(skill) - (isLevelUp ? levelsChanged : -levelsChanged));
            profile.addXp(skill, xpRemoved);
        }

        return isCancelled;
    }

    public static boolean tryLevelEditEvent(@NotNull McMMOPlayer mmoPlayer, PrimarySkillType skill, int levelsChanged, float xpRemoved, boolean isLevelUp, XPGainReason xpGainReason, int oldLevel) {
        McMMOPlayerLevelChangeEvent event = isLevelUp ? new McMMOPlayerLevelUpEvent(mmoPlayer.getPlayer(), skill, levelsChanged - oldLevel, xpGainReason) : new McMMOPlayerLevelDownEvent(mmoPlayer.getPlayer(), skill, levelsChanged, xpGainReason);
        mcMMO.p.getServer().getPluginManager().callEvent(event);

        boolean isCancelled = event.isCancelled();

        if (isCancelled) {
            mmoPlayer.modifySkill(skill, mmoPlayer.getSkillLevel(skill) - (isLevelUp ? levelsChanged : -levelsChanged));
            mmoPlayer.addXp(skill, xpRemoved);
        } else {
            if (isLevelUp) {
                NotificationManager.processLevelUpBroadcasting(mmoPlayer, skill, mmoPlayer.getSkillLevel(skill));
                NotificationManager.processPowerLevelUpBroadcasting(mmoPlayer, mmoPlayer.getPowerLevel());
            }
        }

        return isCancelled;
    }

    /**
     * Simulate a block break event.
     *
     * @param block The block to break
     * @param player The player breaking the block
     * @param shouldArmSwing true if an armswing event should be fired, false otherwise
     * @return true if the event wasn't cancelled, false otherwise
     */
    public static boolean simulateBlockBreak(Block block, Player player, boolean shouldArmSwing) {
        PluginManager pluginManager = mcMMO.p.getServer().getPluginManager();

        // Support for NoCheat
        if (shouldArmSwing) {
            callFakeArmSwingEvent(player);
        }

        FakeBlockDamageEvent damageEvent = new FakeBlockDamageEvent(player, block, player.getInventory().getItemInMainHand(), true);
        pluginManager.callEvent(damageEvent);

        FakeBlockBreakEvent breakEvent = new FakeBlockBreakEvent(block, player);
        pluginManager.callEvent(breakEvent);

        return !damageEvent.isCancelled() && !breakEvent.isCancelled();
    }

    public static void handlePartyTeleportEvent(Player teleportingPlayer, Player targetPlayer) {
        McMMOPlayer mcMMOPlayer = UserManager.getPlayer(teleportingPlayer);

        if(mcMMOPlayer == null)
            return;

        McMMOPartyTeleportEvent event = new McMMOPartyTeleportEvent(teleportingPlayer, targetPlayer, mcMMOPlayer.getParty().getName());
        mcMMO.p.getServer().getPluginManager().callEvent(event);

        if (event.isCancelled()) {
            return;
        }

        teleportingPlayer.teleport(targetPlayer);

        teleportingPlayer.sendMessage(LocaleLoader.getString("Party.Teleport.Player", targetPlayer.getName()));
        targetPlayer.sendMessage(LocaleLoader.getString("Party.Teleport.Target", teleportingPlayer.getName()));

        mcMMOPlayer.getPartyTeleportRecord().actualizeLastUse();
    }

    public static boolean handlePartyXpGainEvent(Party party, float xpGained) {
        McMMOPartyXpGainEvent event = new McMMOPartyXpGainEvent(party, xpGained);
        mcMMO.p.getServer().getPluginManager().callEvent(event);

        boolean isCancelled = event.isCancelled();

        if (!isCancelled) {
            party.addXp(event.getRawXpGained());
        }

        return !isCancelled;
    }

    public static boolean handlePartyLevelChangeEvent(Party party, int levelsChanged, float xpRemoved) {
        McMMOPartyLevelUpEvent event = new McMMOPartyLevelUpEvent(party, levelsChanged);
        mcMMO.p.getServer().getPluginManager().callEvent(event);

        boolean isCancelled = event.isCancelled();

        if (isCancelled) {

            party.setLevel(party.getLevel() + levelsChanged);
            party.addXp(xpRemoved);
        }

        return !isCancelled;
    }

    public static boolean handleXpGainEvent(Player player, PrimarySkillType skill, float xpGained, XPGainReason xpGainReason) {
        McMMOPlayerXpGainEvent event = new McMMOPlayerXpGainEvent(player, skill, xpGained, xpGainReason);
        mcMMO.p.getServer().getPluginManager().callEvent(event);

        boolean isCancelled = event.isCancelled();

        if (!isCancelled) {
            UserManager.getPlayer(player).addXp(skill, event.getRawXpGained());
            UserManager.getPlayer(player).getProfile().registerXpGain(skill, event.getRawXpGained());
        }

        return !isCancelled;
    }

    public static boolean handleStatsLossEvent(Player player, HashMap<String, Integer> levelChanged, HashMap<String, Float> experienceChanged) {
        if(UserManager.getPlayer(player) == null)
            return true;

        McMMOPlayerStatLossEvent event = new McMMOPlayerStatLossEvent(player, levelChanged, experienceChanged);
        mcMMO.p.getServer().getPluginManager().callEvent(event);

        boolean isCancelled = event.isCancelled();

        if (!isCancelled) {
            levelChanged = event.getLevelChanged();
            experienceChanged = event.getExperienceChanged();
            PlayerProfile playerProfile = UserManager.getPlayer(player).getProfile();

            for (PrimarySkillType primarySkillType : PrimarySkillType.NON_CHILD_SKILLS) {
                String skillName = primarySkillType.toString();
                int playerSkillLevel = playerProfile.getSkillLevel(primarySkillType);
                int threshold = Config.getInstance().getHardcoreDeathStatPenaltyLevelThreshold();
                if(playerSkillLevel > threshold) {
                    playerProfile.modifySkill(primarySkillType, Math.max(threshold, playerSkillLevel - levelChanged.get(skillName)));
                    playerProfile.removeXp(primarySkillType, experienceChanged.get(skillName));

                    if (playerProfile.getSkillXpLevel(primarySkillType) < 0) {
                        playerProfile.setSkillXpLevel(primarySkillType, 0);
                    }

                    if (playerProfile.getSkillLevel(primarySkillType) < 0) {
                        playerProfile.modifySkill(primarySkillType, 0);
                    }
                }
            }
        }

        return !isCancelled;
    }

    public static boolean handleVampirismEvent(Player killer, Player victim, HashMap<String, Integer> levelChanged, HashMap<String, Float> experienceChanged) {
        McMMOPlayerVampirismEvent eventKiller = new McMMOPlayerVampirismEvent(killer, false, levelChanged, experienceChanged);
        McMMOPlayerVampirismEvent eventVictim = new McMMOPlayerVampirismEvent(victim, true, levelChanged, experienceChanged);
        mcMMO.p.getServer().getPluginManager().callEvent(eventKiller);
        mcMMO.p.getServer().getPluginManager().callEvent(eventVictim);

        boolean isCancelled = eventKiller.isCancelled() || eventVictim.isCancelled();

        if (!isCancelled) {
            HashMap<String, Integer> levelChangedKiller = eventKiller.getLevelChanged();
            HashMap<String, Float> experienceChangedKiller = eventKiller.getExperienceChanged();

            HashMap<String, Integer> levelChangedVictim = eventVictim.getLevelChanged();
            HashMap<String, Float> experienceChangedVictim = eventVictim.getExperienceChanged();

            McMMOPlayer killerPlayer = UserManager.getPlayer(killer);

            //Not loaded
            if(killerPlayer == null)
                return true;

            //Not loaded
            if(UserManager.getPlayer(victim) == null)
                return true;

            PlayerProfile victimProfile = UserManager.getPlayer(victim).getProfile();

            for (PrimarySkillType primarySkillType : PrimarySkillType.NON_CHILD_SKILLS) {
                String skillName = primarySkillType.toString();
                int victimSkillLevel = victimProfile.getSkillLevel(primarySkillType);

                killerPlayer.addLevels(primarySkillType, levelChangedKiller.get(skillName));
                killerPlayer.beginUnsharedXpGain(primarySkillType, experienceChangedKiller.get(skillName), XPGainReason.VAMPIRISM, XPGainSource.VAMPIRISM);

                victimProfile.modifySkill(primarySkillType, victimSkillLevel - levelChangedVictim.get(skillName));
                victimProfile.removeXp(primarySkillType, experienceChangedVictim.get(skillName));

                if (victimProfile.getSkillXpLevel(primarySkillType) < 0) {
                    victimProfile.setSkillXpLevel(primarySkillType, 0);
                }

                if (victimProfile.getSkillLevel(primarySkillType) < 0) {
                    victimProfile.modifySkill(primarySkillType, 0);
                }
            }
        }

        return !isCancelled;
    }

    public static McMMOPlayerAbilityDeactivateEvent callAbilityDeactivateEvent(Player player, SuperAbilityType ability) {
        McMMOPlayerAbilityDeactivateEvent event = new McMMOPlayerAbilityDeactivateEvent(player, PrimarySkillType.byAbility(ability));
        mcMMO.p.getServer().getPluginManager().callEvent(event);

        return event;
    }

    public static McMMOPlayerFishingTreasureEvent callFishingTreasureEvent(Player player, ItemStack treasureDrop, int treasureXp, Map<Enchantment, Integer> enchants) {
        McMMOPlayerFishingTreasureEvent event = enchants.isEmpty() ? new McMMOPlayerFishingTreasureEvent(player, treasureDrop, treasureXp) : new McMMOPlayerMagicHunterEvent(player, treasureDrop, treasureXp, enchants);
        mcMMO.p.getServer().getPluginManager().callEvent(event);

        return event;
    }

    public static FakePlayerFishEvent callFakeFishEvent(Player player, FishHook hook) {
        FakePlayerFishEvent event = new FakePlayerFishEvent(player, null, hook, PlayerFishEvent.State.FISHING);
        mcMMO.p.getServer().getPluginManager().callEvent(event);

        return event;
    }

    public static McMMOPlayerRepairCheckEvent callRepairCheckEvent(Player player, short durability, ItemStack repairMaterial, ItemStack repairedObject) {
        McMMOPlayerRepairCheckEvent event = new McMMOPlayerRepairCheckEvent(player, durability, repairMaterial, repairedObject);
        mcMMO.p.getServer().getPluginManager().callEvent(event);

        return event;
    }

    public static McMMOPlayerPreDeathPenaltyEvent callPreDeathPenaltyEvent(Player player) {
        McMMOPlayerPreDeathPenaltyEvent event = new McMMOPlayerPreDeathPenaltyEvent(player);
        mcMMO.p.getServer().getPluginManager().callEvent(event);

        return event;
    }

    public static McMMOPlayerDisarmEvent callDisarmEvent(Player defender) {
        McMMOPlayerDisarmEvent event = new McMMOPlayerDisarmEvent(defender);
        mcMMO.p.getServer().getPluginManager().callEvent(event);

        return event;
    }

    public static McMMOPlayerSalvageCheckEvent callSalvageCheckEvent(Player player, ItemStack salvageMaterial, ItemStack salvageResults, ItemStack enchantedBook) {
        McMMOPlayerSalvageCheckEvent event = new McMMOPlayerSalvageCheckEvent(player, salvageMaterial, salvageResults, enchantedBook);
        mcMMO.p.getServer().getPluginManager().callEvent(event);

        return event;
    }


}
