package com.gmail.nossr50.util;

import com.gmail.nossr50.api.FakeBlockBreakEventType;
import com.gmail.nossr50.api.TreeFellerBlockBreakEvent;
import com.gmail.nossr50.datatypes.experience.XPGainReason;
import com.gmail.nossr50.datatypes.experience.XPGainSource;
import com.gmail.nossr50.datatypes.party.Party;
import com.gmail.nossr50.datatypes.player.McMMOPlayer;
import com.gmail.nossr50.datatypes.player.PlayerProfile;
import com.gmail.nossr50.datatypes.skills.PrimarySkillType;
import com.gmail.nossr50.datatypes.skills.SubSkillType;
import com.gmail.nossr50.datatypes.skills.SuperAbilityType;
import com.gmail.nossr50.events.experience.McMMOPlayerLevelChangeEvent;
import com.gmail.nossr50.events.experience.McMMOPlayerLevelDownEvent;
import com.gmail.nossr50.events.experience.McMMOPlayerLevelUpEvent;
import com.gmail.nossr50.events.experience.McMMOPlayerXpGainEvent;
import com.gmail.nossr50.events.fake.FakeBlockBreakEvent;
import com.gmail.nossr50.events.fake.FakeBlockDamageEvent;
import com.gmail.nossr50.events.fake.FakeEvent;
import com.gmail.nossr50.events.fake.FakePlayerFishEvent;
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
import com.gmail.nossr50.util.skills.SkillTools;
import org.bukkit.block.Block;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Entity;
import org.bukkit.entity.FishHook;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.PlayerFishEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginManager;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;

import static java.util.Objects.requireNonNull;

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
     * This little method is just to make the code more readable
     * 
     * @param entity target entity
     * @return the associated McMMOPlayer for this entity
     */
    public static McMMOPlayer getMcMMOPlayer(@NotNull Entity entity) {
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
    public static boolean isRealPlayerDamaged(@NotNull EntityDamageEvent entityDamageEvent) {
        //Make sure the damage is above 0
        double damage = entityDamageEvent.getFinalDamage();

        if (damage <= 0) {
            return false;
        }

        Entity entity = entityDamageEvent.getEntity();

        //Check to make sure the entity is not an NPC
        if (Misc.isNPCEntityExcludingVillagers(entity))
            return false;

        if (!entity.isValid() || !(entity instanceof LivingEntity livingEntity)) {
            return false;
        }

        if (CombatUtils.isInvincible(livingEntity, damage)) {
            return false;
        }

        if (livingEntity instanceof Player) {
            Player player = (Player) entity;

            if (!UserManager.hasPlayerDataKey(player)) {
                return true;
            }

            McMMOPlayer mcMMOPlayer = UserManager.getPlayer(player);

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

    @Deprecated(forRemoval = true, since = "2.2.010")
    public static @NotNull McMMOPlayerAbilityActivateEvent callPlayerAbilityActivateEvent(@NotNull Player player,
                                                                                          @NotNull PrimarySkillType skill) {
        return callPlayerAbilityActivateEvent(requireNonNull(UserManager.getPlayer(player)), skill);
    }

    public static @NotNull McMMOPlayerAbilityActivateEvent callPlayerAbilityActivateEvent(@NotNull McMMOPlayer mmoPlayer,
                                                                                          @NotNull PrimarySkillType skill) {
        requireNonNull(mmoPlayer, "mmoPlayer cannot be null");
        requireNonNull(skill, "skill cannot be null");
        McMMOPlayerAbilityActivateEvent event = new McMMOPlayerAbilityActivateEvent(mmoPlayer, skill);
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
    @Deprecated(forRemoval = true, since = "2.2.010")
    public static @NotNull SubSkillEvent callSubSkillEvent(@NotNull Player player, @NotNull SubSkillType subSkillType) {
        return callSubSkillEvent(requireNonNull(UserManager.getPlayer(player)), subSkillType);
    }

    /**
     * Calls a new SubSkillEvent for this SubSkill and then returns it
     * @param mmoPlayer target mmoPlayer
     * @param subSkillType target subskill
     * @return the event after it has been fired
     */
    public static @NotNull SubSkillEvent callSubSkillEvent(@NotNull McMMOPlayer mmoPlayer, @NotNull SubSkillType subSkillType) {
        requireNonNull(mmoPlayer, "mmoPlayer cannot be null");
        requireNonNull(subSkillType, "subSkillType cannot be null");
        final SubSkillEvent event = new SubSkillEvent(mmoPlayer, subSkillType);
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
    public static @NotNull SubSkillBlockEvent callSubSkillBlockEvent(@NotNull Player player, @NotNull SubSkillType subSkillType, @NotNull Block block) {
        SubSkillBlockEvent event = new SubSkillBlockEvent(player, subSkillType, block);
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
     * @param block          The block to break
     * @param player         The player breaking the block
     * @param shouldArmSwing ignored (here for API compatibility)
     * @return true if the event wasn't cancelled, false otherwise
     * {@code @Deprecated} use {@link #simulateBlockBreak(Block, Player, FakeBlockBreakEventType)} instead
     */
    public static boolean simulateBlockBreak(Block block, Player player, boolean shouldArmSwing) {
        return simulateBlockBreak(block, player);
    }

    /**
     * Simulate a block break event.
     *
     * @param block The block to break
     * @param player The player breaking the block
     * @return true if the event wasn't cancelled, false otherwise
     * {@code @Deprecated} use {@link #simulateBlockBreak(Block, Player, FakeBlockBreakEventType)} instead
     */
    public static boolean simulateBlockBreak(Block block, Player player) {
        return simulateBlockBreak(block, player, FakeBlockBreakEventType.FAKE);
    }

    /**
     * Simulate a block break event.
     *
     * @param block  The block to break
     * @param player The player breaking the block
     * @param eventType The type of event to signal to other plugins
     * @return true if the event wasn't cancelled, false otherwise
     */
    public static boolean simulateBlockBreak(Block block, Player player, FakeBlockBreakEventType eventType) {
        PluginManager pluginManager = mcMMO.p.getServer().getPluginManager();

        FakeBlockDamageEvent damageEvent = new FakeBlockDamageEvent(player, block, player.getInventory().getItemInMainHand(), true);
        pluginManager.callEvent(damageEvent);

        BlockBreakEvent fakeBlockBreakEvent = null;

        switch (eventType) {
            case FAKE -> fakeBlockBreakEvent = new FakeBlockBreakEvent(block, player);
            case TREE_FELLER -> fakeBlockBreakEvent = new TreeFellerBlockBreakEvent(block, player);
        }
        pluginManager.callEvent(fakeBlockBreakEvent);
        return !damageEvent.isCancelled() && !fakeBlockBreakEvent.isCancelled();
    }

    public static void handlePartyTeleportEvent(Player teleportingPlayer, Player targetPlayer) {
        McMMOPlayer mcMMOPlayer = UserManager.getPlayer(teleportingPlayer);

        if (mcMMOPlayer == null)
            return;

        McMMOPartyTeleportEvent event = new McMMOPartyTeleportEvent(teleportingPlayer, targetPlayer, mcMMOPlayer.getParty().getName());
        mcMMO.p.getServer().getPluginManager().callEvent(event);

        if (event.isCancelled()) {
            return;
        }

//        teleportingPlayer.teleport(targetPlayer);
        mcMMO.p.getFoliaLib().getScheduler().teleportAsync(teleportingPlayer, targetPlayer.getLocation());

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
        McMMOPlayer mmoPlayer = UserManager.getPlayer(player);
        if (mmoPlayer == null)
            return true;

        McMMOPlayerXpGainEvent event = new McMMOPlayerXpGainEvent(player, skill, xpGained, xpGainReason);
        mcMMO.p.getServer().getPluginManager().callEvent(event);

        boolean isCancelled = event.isCancelled();

        if (!isCancelled) {
            mmoPlayer.addXp(skill, event.getRawXpGained());
            mmoPlayer.getProfile().registerXpGain(skill, event.getRawXpGained());
        }

        return !isCancelled;
    }

    public static boolean handleStatsLossEvent(Player player, HashMap<String, Integer> levelChanged, HashMap<String, Float> experienceChanged) {
        if (UserManager.getPlayer(player) == null)
            return true;

        McMMOPlayerStatLossEvent event = new McMMOPlayerStatLossEvent(player, levelChanged, experienceChanged);
        mcMMO.p.getServer().getPluginManager().callEvent(event);

        boolean isCancelled = event.isCancelled();

        if (!isCancelled) {
            levelChanged = event.getLevelChanged();
            experienceChanged = event.getExperienceChanged();
            PlayerProfile playerProfile = UserManager.getPlayer(player).getProfile();

            for (PrimarySkillType primarySkillType : SkillTools.NON_CHILD_SKILLS) {
                String skillName = primarySkillType.toString();
                int playerSkillLevel = playerProfile.getSkillLevel(primarySkillType);
                int threshold = mcMMO.p.getGeneralConfig().getHardcoreDeathStatPenaltyLevelThreshold();
                if (playerSkillLevel > threshold) {
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
            if (killerPlayer == null)
                return true;

            //Not loaded
            if (UserManager.getPlayer(victim) == null)
                return true;

            PlayerProfile victimProfile = UserManager.getPlayer(victim).getProfile();

            for (PrimarySkillType primarySkillType : SkillTools.NON_CHILD_SKILLS) {
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

    @Deprecated(forRemoval = true, since = "2.2.010")
    public static McMMOPlayerAbilityDeactivateEvent callAbilityDeactivateEvent(Player player, SuperAbilityType ability) {
        return callAbilityDeactivateEvent(requireNonNull(UserManager.getPlayer(player)), ability);
    }

    public static McMMOPlayerAbilityDeactivateEvent callAbilityDeactivateEvent(@NotNull McMMOPlayer mmoPlayer, @NotNull SuperAbilityType ability) {
        final McMMOPlayerAbilityDeactivateEvent event = new McMMOPlayerAbilityDeactivateEvent(mmoPlayer, mcMMO.p.getSkillTools().getPrimarySkillBySuperAbility(ability));
        mcMMO.p.getServer().getPluginManager().callEvent(event);

        return event;
    }

    @Deprecated(forRemoval = true, since = "2.2.010")
    public static McMMOPlayerFishingTreasureEvent callFishingTreasureEvent(Player player, ItemStack treasureDrop, int treasureXp, Map<Enchantment, Integer> enchants) {
        return callFishingTreasureEvent(requireNonNull(UserManager.getPlayer(player)), treasureDrop, treasureXp, enchants);
    }

    public static McMMOPlayerFishingTreasureEvent callFishingTreasureEvent(McMMOPlayer mmoPlayer, ItemStack treasureDrop, int treasureXp, Map<Enchantment, Integer> enchants) {
        final McMMOPlayerFishingTreasureEvent event = enchants.isEmpty() ? new McMMOPlayerFishingTreasureEvent(mmoPlayer, treasureDrop, treasureXp) : new McMMOPlayerMagicHunterEvent(mmoPlayer, treasureDrop, treasureXp, enchants);
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
