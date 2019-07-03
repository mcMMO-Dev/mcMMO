package com.gmail.nossr50.util;

import com.gmail.nossr50.datatypes.experience.XPGainReason;
import com.gmail.nossr50.datatypes.experience.XPGainSource;
import com.gmail.nossr50.datatypes.interactions.NotificationType;
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
import com.gmail.nossr50.events.skills.McMMOPlayerNotificationEvent;
import com.gmail.nossr50.events.skills.abilities.McMMOPlayerAbilityActivateEvent;
import com.gmail.nossr50.events.skills.abilities.McMMOPlayerAbilityDeactivateEvent;
import com.gmail.nossr50.events.skills.fishing.McMMOPlayerFishingTreasureEvent;
import com.gmail.nossr50.events.skills.fishing.McMMOPlayerMagicHunterEvent;
import com.gmail.nossr50.events.skills.repair.McMMOPlayerRepairCheckEvent;
import com.gmail.nossr50.events.skills.salvage.McMMOPlayerSalvageCheckEvent;
import com.gmail.nossr50.events.skills.secondaryabilities.SubSkillEvent;
import com.gmail.nossr50.events.skills.unarmed.McMMOPlayerDisarmEvent;
import com.gmail.nossr50.mcMMO;
import com.gmail.nossr50.util.skills.CombatUtils;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
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
import org.bukkit.plugin.PluginManager;
import org.bukkit.potion.PotionEffectType;

import java.util.HashMap;
import java.util.Map;

/**
 * This class is meant to help make event related code less boilerplate
 */
public class EventManager {
    
    private mcMMO pluginRef;

    public EventManager(mcMMO pluginRef) {
        this.pluginRef = pluginRef;
    }

    /**
     * Checks to see if damage is from natural sources
     * This cannot be used to determine if damage is from vanilla MC, it just checks to see if the damage is from a complex behaviour in mcMMO such as bleed.
     *
     * @param event this event
     * @return true if damage is NOT from an unnatural mcMMO skill (such as bleed DOTs)
     */
    public boolean isDamageFromMcMMOComplexBehaviour(Event event) {
        if (event instanceof FakeEntityDamageEvent) {
            return true;
        }
        return false;
    }

    /**
     * This little method is just to make the code more readable
     *
     * @param entity target entity
     * @return the associated McMMOPlayer for this entity
     */
    public McMMOPlayer getMcMMOPlayer(Entity entity) {
        return pluginRef.getUserManager().getPlayer((Player) entity);
    }

    /**
     * Checks to see if a Player was damaged in this EntityDamageEvent
     * <p>
     * This method checks for the following things and if they are all true it returns true
     * <p>
     * 1) The player is real and not an NPC
     * 2) The player is not in god mode
     * 3) The damage dealt is above 0 (if a player has Absorption, check if damage and final damage are above 0)
     * 4) The player is loaded into our mcMMO user profiles
     *
     * @param entityDamageEvent
     * @return
     */
    public boolean isRealPlayerDamaged(EntityDamageEvent entityDamageEvent) {
        //Make sure the damage is above 0
        double damage = entityDamageEvent.getDamage();
        double finalDamage = entityDamageEvent.getFinalDamage();

        if (entityDamageEvent.getEntity() instanceof Player) {
            Player player = (Player) entityDamageEvent.getEntity();

            //If a player has Absorption, check both damage and final damage
            if (player.hasPotionEffect(PotionEffectType.ABSORPTION)) {
                if (damage <= 0 && finalDamage <= 0) {
                    return false;
                }
            }
            //Otherwise, do original check - only check final damage
            else {
                if (finalDamage <= 0) {
                    return false;
                }
            }
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

            if (!pluginRef.getUserManager().hasPlayerDataKey(player)) {
                return true;
            }

            McMMOPlayer mcMMOPlayer = pluginRef.getUserManager().getPlayer(player);

            if (mcMMOPlayer == null) {
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

    public McMMOPlayerAbilityActivateEvent callPlayerAbilityActivateEvent(Player player, PrimarySkillType skill) {
        McMMOPlayerAbilityActivateEvent event = new McMMOPlayerAbilityActivateEvent(player, skill);
        pluginRef.getServer().getPluginManager().callEvent(event);

        return event;
    }

    /**
     * Calls a new SubSkillEvent for this SubSkill and then returns it
     *
     * @param player       target player
     * @param subSkillType target subskill
     * @return the event after it has been fired
     */
    @Deprecated
    public SubSkillEvent callSubSkillEvent(Player player, SubSkillType subSkillType) {
        SubSkillEvent event = new SubSkillEvent(player, subSkillType);
        pluginRef.getServer().getPluginManager().callEvent(event);

        return event;
    }

    /**
     * Calls a new SubSkillEvent for this SubSkill and then returns it
     *
     * @param player           target player
     * @param abstractSubSkill target subskill
     * @return the event after it has been fired
     */
    public SubSkillEvent callSubSkillEvent(Player player, AbstractSubSkill abstractSubSkill) {
        SubSkillEvent event = new SubSkillEvent(player, abstractSubSkill);
        pluginRef.getServer().getPluginManager().callEvent(event);

        return event;
    }

    public void callFakeArmSwingEvent(Player player) {
        FakePlayerAnimationEvent event = new FakePlayerAnimationEvent(player);
        pluginRef.getServer().getPluginManager().callEvent(event);

    }

    public boolean tryLevelChangeEvent(Player player, PrimarySkillType skill, int levelsChanged, double xpRemoved, boolean isLevelUp, XPGainReason xpGainReason) {
        McMMOPlayerLevelChangeEvent event = isLevelUp ? new McMMOPlayerLevelUpEvent(player, skill, levelsChanged, xpGainReason) : new McMMOPlayerLevelDownEvent(player, skill, levelsChanged, xpGainReason);
        pluginRef.getServer().getPluginManager().callEvent(event);

        boolean isCancelled = event.isCancelled();

        if (isCancelled) {
            PlayerProfile profile = pluginRef.getUserManager().getPlayer(player).getProfile();

            profile.modifySkill(skill, profile.getSkillLevel(skill) - (isLevelUp ? levelsChanged : -levelsChanged));
            profile.addXp(skill, xpRemoved);
        }

        return isCancelled;
    }

    public boolean tryLevelEditEvent(Player player, PrimarySkillType skill, int levelsChanged, double xpRemoved, boolean isLevelUp, XPGainReason xpGainReason, int oldLevel) {
        McMMOPlayerLevelChangeEvent event = isLevelUp ? new McMMOPlayerLevelUpEvent(player, skill, levelsChanged - oldLevel, xpGainReason) : new McMMOPlayerLevelDownEvent(player, skill, levelsChanged, xpGainReason);
        pluginRef.getServer().getPluginManager().callEvent(event);

        boolean isCancelled = event.isCancelled();

        if (isCancelled) {
            PlayerProfile profile = pluginRef.getUserManager().getPlayer(player).getProfile();

            profile.modifySkill(skill, profile.getSkillLevel(skill) - (isLevelUp ? levelsChanged : -levelsChanged));
            profile.addXp(skill, xpRemoved);
        }

        return isCancelled;
    }

    /**
     * Simulate a block break event.
     *
     * @param block          The block to break
     * @param player         The player breaking the block
     * @param shouldArmSwing true if an armswing event should be fired, false otherwise
     * @return true if the event wasn't cancelled, false otherwise
     */
    public boolean simulateBlockBreak(Block block, Player player, boolean shouldArmSwing) {
        PluginManager pluginManager = pluginRef.getServer().getPluginManager();

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

    public void handlePartyTeleportEvent(Player teleportingPlayer, Player targetPlayer) {
        McMMOPlayer mcMMOPlayer = pluginRef.getUserManager().getPlayer(teleportingPlayer);

        if (mcMMOPlayer == null)
            return;

        McMMOPartyTeleportEvent event = new McMMOPartyTeleportEvent(teleportingPlayer, targetPlayer, mcMMOPlayer.getParty().getName());
        pluginRef.getServer().getPluginManager().callEvent(event);

        if (event.isCancelled()) {
            return;
        }

        teleportingPlayer.teleport(targetPlayer);

        teleportingPlayer.sendMessage(pluginRef.getLocaleManager().getString("Party.Teleport.Player", targetPlayer.getName()));
        targetPlayer.sendMessage(pluginRef.getLocaleManager().getString("Party.Teleport.Target", teleportingPlayer.getName()));

        mcMMOPlayer.getPartyTeleportRecord().actualizeLastUse();
    }

    public boolean handlePartyXpGainEvent(Party party, double xpGained) {
        McMMOPartyXpGainEvent event = new McMMOPartyXpGainEvent(party, xpGained);
        pluginRef.getServer().getPluginManager().callEvent(event);

        boolean isCancelled = event.isCancelled();

        if (!isCancelled) {
            party.addXp(event.getRawXpGained());
        }

        return !isCancelled;
    }

    public boolean handlePartyLevelChangeEvent(Party party, int levelsChanged, double xpRemoved) {
        McMMOPartyLevelUpEvent event = new McMMOPartyLevelUpEvent(party, levelsChanged);
        pluginRef.getServer().getPluginManager().callEvent(event);

        boolean isCancelled = event.isCancelled();

        if (isCancelled) {

            party.setLevel(party.getLevel() + levelsChanged);
            party.addXp(xpRemoved);
        }

        return !isCancelled;
    }

    public boolean handleXpGainEvent(Player player, PrimarySkillType skill, double xpGained, XPGainReason xpGainReason) {
        McMMOPlayerXpGainEvent event = new McMMOPlayerXpGainEvent(player, skill, getSkillLevel(player, skill), xpGained, xpGainReason);
        pluginRef.getServer().getPluginManager().callEvent(event);

        boolean isCancelled = event.isCancelled();

        if (!isCancelled) {
            pluginRef.getUserManager().getPlayer(player).addXp(skill, event.getRawXpGained());
            pluginRef.getUserManager().getPlayer(player).getProfile().registerXpGain(skill, event.getRawXpGained());
        }

        return !isCancelled;
    }

    public boolean handleStatsLossEvent(Player player, HashMap<String, Integer> levelChanged, HashMap<String, Double> experienceChanged) {
        if (pluginRef.getUserManager().getPlayer(player) == null)
            return true;

        McMMOPlayerStatLossEvent event = new McMMOPlayerStatLossEvent(player, levelChanged, experienceChanged);
        pluginRef.getServer().getPluginManager().callEvent(event);

        boolean isCancelled = event.isCancelled();

        if (!isCancelled) {
            levelChanged = event.getLevelChanged();
            experienceChanged = event.getExperienceChanged();
            PlayerProfile playerProfile = pluginRef.getUserManager().getPlayer(player).getProfile();

            for (PrimarySkillType primarySkillType : PrimarySkillType.NON_CHILD_SKILLS) {
                String skillName = primarySkillType.toString();
                int playerSkillLevel = playerProfile.getSkillLevel(primarySkillType);

                playerProfile.modifySkill(primarySkillType, playerSkillLevel - levelChanged.get(skillName));
                playerProfile.removeXp(primarySkillType, experienceChanged.get(skillName));

                if (playerProfile.getSkillXpLevel(primarySkillType) < 0) {
                    playerProfile.setSkillXpLevel(primarySkillType, 0);
                }

                if (playerProfile.getSkillLevel(primarySkillType) < 0) {
                    playerProfile.modifySkill(primarySkillType, 0);
                }
            }
        }

        return !isCancelled;
    }

    public boolean handleVampirismEvent(Player killer, Player victim, HashMap<String, Integer> levelChanged, HashMap<String, Double> experienceChanged) {
        McMMOPlayerVampirismEvent eventKiller = new McMMOPlayerVampirismEvent(killer, false, levelChanged, experienceChanged);
        McMMOPlayerVampirismEvent eventVictim = new McMMOPlayerVampirismEvent(victim, true, levelChanged, experienceChanged);
        pluginRef.getServer().getPluginManager().callEvent(eventKiller);
        pluginRef.getServer().getPluginManager().callEvent(eventVictim);

        boolean isCancelled = eventKiller.isCancelled() || eventVictim.isCancelled();

        if (!isCancelled) {
            HashMap<String, Integer> levelChangedKiller = eventKiller.getLevelChanged();
            HashMap<String, Double> experienceChangedKiller = eventKiller.getExperienceChanged();

            HashMap<String, Integer> levelChangedVictim = eventVictim.getLevelChanged();
            HashMap<String, Double> experienceChangedVictim = eventVictim.getExperienceChanged();

            McMMOPlayer killerPlayer = pluginRef.getUserManager().getPlayer(killer);

            //Not loaded
            if (killerPlayer == null)
                return true;

            //Not loaded
            if (pluginRef.getUserManager().getPlayer(victim) == null)
                return true;

            PlayerProfile victimProfile = pluginRef.getUserManager().getPlayer(victim).getProfile();

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

    public void callAbilityDeactivateEvent(Player player, SuperAbilityType ability) {
        McMMOPlayerAbilityDeactivateEvent event = new McMMOPlayerAbilityDeactivateEvent(player, PrimarySkillType.byAbility(ability));
        pluginRef.getServer().getPluginManager().callEvent(event);

    }

    public McMMOPlayerFishingTreasureEvent callFishingTreasureEvent(Player player, ItemStack treasureDrop, int treasureXp, Map<Enchantment, Integer> enchants) {
        McMMOPlayerFishingTreasureEvent event = enchants.isEmpty() ? new McMMOPlayerFishingTreasureEvent(player, treasureDrop, treasureXp) : new McMMOPlayerMagicHunterEvent(player, treasureDrop, treasureXp, enchants);
        pluginRef.getServer().getPluginManager().callEvent(event);

        return event;
    }

    public void callFakeFishEvent(Player player, FishHook hook) {
        FakePlayerFishEvent event = new FakePlayerFishEvent(player, null, hook, PlayerFishEvent.State.FISHING);
        pluginRef.getServer().getPluginManager().callEvent(event);

    }

    public McMMOPlayerRepairCheckEvent callRepairCheckEvent(Player player, short durability, ItemStack repairMaterial, ItemStack repairedObject) {
        McMMOPlayerRepairCheckEvent event = new McMMOPlayerRepairCheckEvent(player, durability, repairMaterial, repairedObject);
        pluginRef.getServer().getPluginManager().callEvent(event);

        return event;
    }

    public McMMOPlayerPreDeathPenaltyEvent callPreDeathPenaltyEvent(Player player) {
        McMMOPlayerPreDeathPenaltyEvent event = new McMMOPlayerPreDeathPenaltyEvent(player);
        pluginRef.getServer().getPluginManager().callEvent(event);

        return event;
    }

    public McMMOPlayerDisarmEvent callDisarmEvent(Player defender) {
        McMMOPlayerDisarmEvent event = new McMMOPlayerDisarmEvent(defender);
        pluginRef.getServer().getPluginManager().callEvent(event);

        return event;
    }

    public McMMOPlayerSalvageCheckEvent callSalvageCheckEvent(Player player, ItemStack salvageMaterial, ItemStack salvageResults, ItemStack enchantedBook) {
        McMMOPlayerSalvageCheckEvent event = new McMMOPlayerSalvageCheckEvent(player, salvageMaterial, salvageResults, enchantedBook);
        pluginRef.getServer().getPluginManager().callEvent(event);

        return event;
    }

    private int getSkillLevel(Player player, PrimarySkillType primarySkillType) {
        return pluginRef.getUserManager().getPlayer(player).getSkillLevel(primarySkillType);
    }

    /**
     * Creates and calls a McMMOPlayerNotificationEvent
     * @param player target player
     * @param notificationType notification category
     * @param textComponent text component used for the message
     * @return the McMMOPlayerNotificationEvent after its been fired
     */
    public McMMOPlayerNotificationEvent createAndCallNotificationEvent(Player player, NotificationType notificationType, TextComponent textComponent) {
        //Init event
        McMMOPlayerNotificationEvent customEvent = new McMMOPlayerNotificationEvent(notificationType, player, pluginRef.getNotificationManager().getPlayerNotificationSettings(notificationType), textComponent);

        //Call event
        Bukkit.getServer().getPluginManager().callEvent(customEvent);
        return customEvent;
    }

    /**
     * Creates and calls a McMMOPlayerNotificationEvent
     * @param player target player
     * @param notificationType notification category
     * @param message string used for the message
     * @return the McMMOPlayerNotificationEvent after its been fired
     */
    public McMMOPlayerNotificationEvent createAndCallNotificationEvent(Player player, NotificationType notificationType, String message) {
        //Init event
        McMMOPlayerNotificationEvent customEvent = new McMMOPlayerNotificationEvent(notificationType, player, pluginRef.getNotificationManager().getPlayerNotificationSettings(notificationType), message);

        //Call event
        Bukkit.getServer().getPluginManager().callEvent(customEvent);
        return customEvent;
    }

}
