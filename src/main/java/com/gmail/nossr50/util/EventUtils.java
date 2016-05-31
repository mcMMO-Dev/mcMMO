package com.gmail.nossr50.util;

import java.util.HashMap;
import java.util.Map;

import org.bukkit.block.Block;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Fish;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerFishEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.PluginManager;

import com.gmail.nossr50.mcMMO;
import com.gmail.nossr50.datatypes.player.McMMOPlayer;
import com.gmail.nossr50.datatypes.player.PlayerProfile;
import com.gmail.nossr50.datatypes.skills.AbilityType;
import com.gmail.nossr50.datatypes.skills.SecondaryAbility;
import com.gmail.nossr50.datatypes.skills.SkillType;
import com.gmail.nossr50.datatypes.skills.XPGainReason;
import com.gmail.nossr50.events.experience.McMMOPlayerLevelChangeEvent;
import com.gmail.nossr50.events.experience.McMMOPlayerLevelDownEvent;
import com.gmail.nossr50.events.experience.McMMOPlayerLevelUpEvent;
import com.gmail.nossr50.events.experience.McMMOPlayerXpGainEvent;
import com.gmail.nossr50.events.fake.FakeBlockBreakEvent;
import com.gmail.nossr50.events.fake.FakeBlockDamageEvent;
import com.gmail.nossr50.events.fake.FakePlayerAnimationEvent;
import com.gmail.nossr50.events.fake.FakePlayerFishEvent;
import com.gmail.nossr50.events.hardcore.McMMOPlayerPreDeathPenaltyEvent;
import com.gmail.nossr50.events.hardcore.McMMOPlayerStatLossEvent;
import com.gmail.nossr50.events.hardcore.McMMOPlayerVampirismEvent;

import com.gmail.nossr50.events.skills.abilities.McMMOPlayerAbilityActivateEvent;
import com.gmail.nossr50.events.skills.abilities.McMMOPlayerAbilityDeactivateEvent;
import com.gmail.nossr50.events.skills.fishing.McMMOPlayerFishingTreasureEvent;
import com.gmail.nossr50.events.skills.fishing.McMMOPlayerMagicHunterEvent;
import com.gmail.nossr50.events.skills.repair.McMMOPlayerRepairCheckEvent;
import com.gmail.nossr50.events.skills.secondaryabilities.SecondaryAbilityEvent;
import com.gmail.nossr50.events.skills.unarmed.McMMOPlayerDisarmEvent;
import com.gmail.nossr50.locale.LocaleLoader;
import com.gmail.nossr50.util.player.UserManager;

public class EventUtils {
    public static McMMOPlayerAbilityActivateEvent callPlayerAbilityActivateEvent(Player player, SkillType skill) {
        McMMOPlayerAbilityActivateEvent event = new McMMOPlayerAbilityActivateEvent(player, skill);
        mcMMO.p.getServer().getPluginManager().callEvent(event);

        return event;
    }

    public static SecondaryAbilityEvent callSecondaryAbilityEvent(Player player, SecondaryAbility secondaryAbility) {
        SecondaryAbilityEvent event = new SecondaryAbilityEvent(player, secondaryAbility);
        mcMMO.p.getServer().getPluginManager().callEvent(event);

        return event;
    }

    public static FakePlayerAnimationEvent callFakeArmSwingEvent(Player player) {
        FakePlayerAnimationEvent event = new FakePlayerAnimationEvent(player);
        mcMMO.p.getServer().getPluginManager().callEvent(event);

        return event;
    }

    public static boolean handleLevelChangeEvent(Player player, SkillType skill, int levelsChanged, float xpRemoved, boolean isLevelUp, XPGainReason xpGainReason) {
        McMMOPlayerLevelChangeEvent event = isLevelUp ? new McMMOPlayerLevelUpEvent(player, skill, levelsChanged, xpGainReason) : new McMMOPlayerLevelDownEvent(player, skill, levelsChanged, xpGainReason);
        mcMMO.p.getServer().getPluginManager().callEvent(event);

        boolean isCancelled = event.isCancelled();

        if (isCancelled) {
            PlayerProfile profile = UserManager.getPlayer(player).getProfile();

            profile.modifySkill(skill, profile.getSkillLevel(skill) - (isLevelUp ? levelsChanged : -levelsChanged));
            profile.addXp(skill, xpRemoved);
        }

        return !isCancelled;
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

 

    public static boolean handleXpGainEvent(Player player, SkillType skill, float xpGained, XPGainReason xpGainReason) {
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
        McMMOPlayerStatLossEvent event = new McMMOPlayerStatLossEvent(player, levelChanged, experienceChanged);
        mcMMO.p.getServer().getPluginManager().callEvent(event);

        boolean isCancelled = event.isCancelled();

        if (!isCancelled) {
            levelChanged = event.getLevelChanged();
            experienceChanged = event.getExperienceChanged();
            PlayerProfile playerProfile = UserManager.getPlayer(player).getProfile();

            for (SkillType skillType : SkillType.NON_CHILD_SKILLS) {
                String skillName = skillType.toString();
                int playerSkillLevel = playerProfile.getSkillLevel(skillType);

                playerProfile.modifySkill(skillType, playerSkillLevel - levelChanged.get(skillName));
                playerProfile.removeXp(skillType, experienceChanged.get(skillName));

                if (playerProfile.getSkillXpLevel(skillType) < 0) {
                    playerProfile.setSkillXpLevel(skillType, 0);
                }

                if (playerProfile.getSkillLevel(skillType) < 0) {
                    playerProfile.modifySkill(skillType, 0);
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
            PlayerProfile victimProfile = UserManager.getPlayer(victim).getProfile();

            for (SkillType skillType : SkillType.NON_CHILD_SKILLS) {
                String skillName = skillType.toString();
                int victimSkillLevel = victimProfile.getSkillLevel(skillType);

                killerPlayer.addLevels(skillType, levelChangedKiller.get(skillName));
                killerPlayer.beginUnsharedXpGain(skillType, experienceChangedKiller.get(skillName), XPGainReason.VAMPIRISM);

                victimProfile.modifySkill(skillType, victimSkillLevel - levelChangedVictim.get(skillName));
                victimProfile.removeXp(skillType, experienceChangedVictim.get(skillName));

                if (victimProfile.getSkillXpLevel(skillType) < 0) {
                    victimProfile.setSkillXpLevel(skillType, 0);
                }

                if (victimProfile.getSkillLevel(skillType) < 0) {
                    victimProfile.modifySkill(skillType, 0);
                }
            }
        }

        return !isCancelled;
    }

    public static McMMOPlayerAbilityDeactivateEvent callAbilityDeactivateEvent(Player player, AbilityType ability) {
        McMMOPlayerAbilityDeactivateEvent event = new McMMOPlayerAbilityDeactivateEvent(player, SkillType.byAbility(ability));
        mcMMO.p.getServer().getPluginManager().callEvent(event);

        return event;
    }

    public static McMMOPlayerFishingTreasureEvent callFishingTreasureEvent(Player player, ItemStack treasureDrop, int treasureXp, Map<Enchantment, Integer> enchants) {
        McMMOPlayerFishingTreasureEvent event = enchants.isEmpty() ? new McMMOPlayerFishingTreasureEvent(player, treasureDrop, treasureXp) : new McMMOPlayerMagicHunterEvent(player, treasureDrop, treasureXp, enchants);
        mcMMO.p.getServer().getPluginManager().callEvent(event);

        return event;
    }

    public static FakePlayerFishEvent callFakeFishEvent(Player player, Fish hook) {
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
}
