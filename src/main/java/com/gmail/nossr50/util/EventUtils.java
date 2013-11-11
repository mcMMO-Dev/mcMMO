package com.gmail.nossr50.util;

import java.util.Map;

import org.bukkit.block.Block;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Fish;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerFishEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.PluginManager;

import com.gmail.nossr50.mcMMO;
import com.gmail.nossr50.datatypes.party.Party;
import com.gmail.nossr50.datatypes.player.McMMOPlayer;
import com.gmail.nossr50.datatypes.player.PlayerProfile;
import com.gmail.nossr50.datatypes.skills.AbilityType;
import com.gmail.nossr50.datatypes.skills.SkillType;
import com.gmail.nossr50.events.experience.McMMOPlayerLevelChangeEvent;
import com.gmail.nossr50.events.experience.McMMOPlayerLevelDownEvent;
import com.gmail.nossr50.events.experience.McMMOPlayerLevelUpEvent;
import com.gmail.nossr50.events.experience.McMMOPlayerXpGainEvent;
import com.gmail.nossr50.events.fake.FakeBlockBreakEvent;
import com.gmail.nossr50.events.fake.FakeBlockDamageEvent;
import com.gmail.nossr50.events.fake.FakePlayerAnimationEvent;
import com.gmail.nossr50.events.fake.FakePlayerFishEvent;
import com.gmail.nossr50.events.hardcore.McMMOPlayerDeathPenaltyEvent;
import com.gmail.nossr50.events.party.McMMOPartyLevelUpEvent;
import com.gmail.nossr50.events.party.McMMOPartyTeleportEvent;
import com.gmail.nossr50.events.party.McMMOPartyXpGainEvent;
import com.gmail.nossr50.events.skills.abilities.McMMOPlayerAbilityActivateEvent;
import com.gmail.nossr50.events.skills.abilities.McMMOPlayerAbilityDeactivateEvent;
import com.gmail.nossr50.events.skills.fishing.McMMOPlayerFishingTreasureEvent;
import com.gmail.nossr50.events.skills.fishing.McMMOPlayerMagicHunterEvent;
import com.gmail.nossr50.events.skills.repair.McMMOPlayerRepairCheckEvent;
import com.gmail.nossr50.events.skills.unarmed.McMMOPlayerDisarmEvent;
import com.gmail.nossr50.locale.LocaleLoader;
import com.gmail.nossr50.util.player.UserManager;

public class EventUtils {
    public static McMMOPlayerAbilityActivateEvent callPlayerAbilityActivateEvent(Player player, SkillType skill) {
        McMMOPlayerAbilityActivateEvent event = new McMMOPlayerAbilityActivateEvent(player, skill);
        mcMMO.p.getServer().getPluginManager().callEvent(event);

        return event;
    }

    public static FakePlayerAnimationEvent callFakeArmSwingEvent(Player player) {
        FakePlayerAnimationEvent event = new FakePlayerAnimationEvent(player);
        mcMMO.p.getServer().getPluginManager().callEvent(event);

        return event;
    }

    public static boolean handleLevelChangeEvent(Player player, SkillType skill, int levelsChanged, float xpRemoved, boolean isLevelUp) {
        McMMOPlayerLevelChangeEvent event = isLevelUp ? new McMMOPlayerLevelUpEvent(player, skill, levelsChanged) : new McMMOPlayerLevelDownEvent(player, skill, levelsChanged);
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

        FakeBlockDamageEvent damageEvent = new FakeBlockDamageEvent(player, block, player.getItemInHand(), true);
        pluginManager.callEvent(damageEvent);

        FakeBlockBreakEvent breakEvent = new FakeBlockBreakEvent(block, player);
        pluginManager.callEvent(breakEvent);

        return !damageEvent.isCancelled() && !breakEvent.isCancelled();
    }

    public static void handlePartyTeleportEvent(Player teleportingPlayer, Player targetPlayer) {
        McMMOPlayer mcMMOPlayer = UserManager.getPlayer(teleportingPlayer);

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

    public static boolean handleXpGainEvent(Player player, SkillType skill, float xpGained) {
        McMMOPlayerXpGainEvent event = new McMMOPlayerXpGainEvent(player, skill, xpGained);
        mcMMO.p.getServer().getPluginManager().callEvent(event);

        boolean isCancelled = event.isCancelled();

        if (!isCancelled) {
            UserManager.getPlayer(player).addXp(skill, event.getRawXpGained());
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

    public static McMMOPlayerDeathPenaltyEvent callDeathPenaltyEvent(Player player) {
        McMMOPlayerDeathPenaltyEvent event = new McMMOPlayerDeathPenaltyEvent(player);
        mcMMO.p.getServer().getPluginManager().callEvent(event);

        return event;
    }

    public static McMMOPlayerDisarmEvent callDisarmEvent(Player defender) {
        McMMOPlayerDisarmEvent event = new McMMOPlayerDisarmEvent(defender);
        mcMMO.p.getServer().getPluginManager().callEvent(event);

        return event;
    }
}
