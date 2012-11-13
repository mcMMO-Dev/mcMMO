package com.gmail.nossr50.skills.repair;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.getspout.spoutapi.SpoutManager;
import org.getspout.spoutapi.player.SpoutPlayer;

import com.gmail.nossr50.mcMMO;
import com.gmail.nossr50.config.Config;
import com.gmail.nossr50.datatypes.PlayerProfile;
import com.gmail.nossr50.datatypes.SkillType;
import com.gmail.nossr50.locale.LocaleLoader;
import com.gmail.nossr50.util.ItemChecks;
import com.gmail.nossr50.util.Permissions;
import com.gmail.nossr50.util.Users;

public class Salvage {

	private static Config configInstance = Config.getInstance();
	private static Permissions permInstance = Permissions.getInstance();

	public static void handleSalvage(final Player player, final Location location, final ItemStack inHand) {
		if (!permInstance.salvage(player) || !configInstance.getSalvageEnabled()) {
			return;
		}

		final PlayerProfile profile = Users.getProfile(player);
		final int skillLevel = profile.getSkillLevel(SkillType.REPAIR);
		final int unlockLevel = configInstance.getSalvageUnlockLevel();

		if (skillLevel >= unlockLevel) {
			final World world = player.getWorld();
			final float currentdura = inHand.getDurability();

			if (currentdura == 0) {
				final int salvagedAmount = getSalvagedAmount(inHand);
				final int itemID = getSalvagedItemID(inHand);

				player.setItemInHand(new ItemStack(0));
				location.setY(location.getY() + 1);
				world.dropItem(location, new ItemStack(itemID, salvagedAmount));
                player.sendMessage(LocaleLoader.getString("Repair.Skills.SalvageSuccess"));
			} else {
                player.sendMessage(LocaleLoader.getString("Repair.Skills.NotFullDurability"));
			}
		} else {
            player.sendMessage(LocaleLoader.getString("Repair.Skills.AdeptSalvage"));
		}
	}

	public static int getSalvagedItemID(final ItemStack inHand) {
		int salvagedItem = 0;
		switch (inHand.getType()) {
		case DIAMOND_PICKAXE:
		case DIAMOND_SPADE:
		case DIAMOND_AXE:
		case DIAMOND_SWORD:
		case DIAMOND_HOE:
		case DIAMOND_HELMET:
		case DIAMOND_CHESTPLATE:
		case DIAMOND_LEGGINGS:
		case DIAMOND_BOOTS:
			salvagedItem = 264;
			break;
		case GOLD_PICKAXE:
		case GOLD_SPADE:
		case GOLD_AXE:
		case GOLD_SWORD:
		case GOLD_HOE:
		case GOLD_HELMET:
		case GOLD_CHESTPLATE:
		case GOLD_LEGGINGS:
		case GOLD_BOOTS:
			salvagedItem = 266;
			break;
		case IRON_PICKAXE:
		case IRON_SPADE:
		case IRON_AXE:
		case IRON_SWORD:
		case IRON_HOE:
		case IRON_HELMET:
		case IRON_CHESTPLATE:
		case IRON_LEGGINGS:
		case IRON_BOOTS:
			salvagedItem = 265;
			break;
		case STONE_PICKAXE:
		case STONE_SPADE:
		case STONE_AXE:
		case STONE_SWORD:
		case STONE_HOE:
			salvagedItem = 4;
			break;
		case WOOD_PICKAXE:
		case WOOD_SPADE:
		case WOOD_AXE:
		case WOOD_SWORD:
		case WOOD_HOE:
			salvagedItem = 5;
			break;
		case LEATHER_HELMET:
		case LEATHER_CHESTPLATE:
		case LEATHER_LEGGINGS:
		case LEATHER_BOOTS:
			salvagedItem = 334;
			break;
		default:
			break;
		}
		return salvagedItem;
	}

	public static int getSalvagedAmount(final ItemStack inHand) {
		int salvagedAmount = 0;
		switch (inHand.getType()) {
		case DIAMOND_PICKAXE:
		case GOLD_PICKAXE:
		case IRON_PICKAXE:
		case STONE_PICKAXE:
		case WOOD_PICKAXE:
		case DIAMOND_AXE:
		case GOLD_AXE:
		case IRON_AXE:
		case STONE_AXE:
		case WOOD_AXE:
			salvagedAmount = 3;
			break;
		case DIAMOND_SPADE:
		case GOLD_SPADE:
		case IRON_SPADE:
		case STONE_SPADE:
		case WOOD_SPADE:
			salvagedAmount = 1;
			break;
		case DIAMOND_SWORD:
		case GOLD_SWORD:
		case IRON_SWORD:
		case STONE_SWORD:
		case WOOD_SWORD:
		case DIAMOND_HOE:
		case GOLD_HOE:
		case IRON_HOE:
		case STONE_HOE:
		case WOOD_HOE:
			salvagedAmount = 2;
			break;
		case DIAMOND_HELMET:
		case GOLD_HELMET:
		case IRON_HELMET:
		case LEATHER_HELMET:
			salvagedAmount = 5;
			break;
		case DIAMOND_CHESTPLATE:
		case GOLD_CHESTPLATE:
		case IRON_CHESTPLATE:
		case LEATHER_CHESTPLATE:
			salvagedAmount = 8;
			break;
		case DIAMOND_LEGGINGS:
		case GOLD_LEGGINGS:
		case IRON_LEGGINGS:
		case LEATHER_LEGGINGS:
			salvagedAmount = 7;
			break;
		case DIAMOND_BOOTS:
		case GOLD_BOOTS:
		case IRON_BOOTS:
		case LEATHER_BOOTS:
			salvagedAmount = 4;
			break;
		default:
			break;
		}
		return salvagedAmount;
	}

	/**
	 * Handles notifications for placing an anvil.
	 * 
	 * @param player The player placing the anvil
	 * @param anvilID The item ID of the anvil block
	 */
	public static void placedAnvilCheck(final Player player, final int anvilID) {
		final PlayerProfile profile = Users.getProfile(player);

		if (!profile.getPlacedSalvageAnvil()) {
			if (mcMMO.spoutEnabled) {
				final SpoutPlayer spoutPlayer = SpoutManager.getPlayer(player);

				if (spoutPlayer.isSpoutCraftEnabled()) {
					spoutPlayer.sendNotification("[mcMMO] Anvil Placed", "Right click to salvage!", Material.getMaterial(anvilID));
				}
			} else {
                player.sendMessage(LocaleLoader.getString("Repair.Listener.Anvil2"));
			}

			profile.togglePlacedSalvageAnvil();
		}
	}

	/**
	 * Checks if the item is salvageable.
	 * 
	 * @param is Item to check
	 * @return true if the item is salvageable, false otherwise
	 */
	public static boolean isSalvageable(final ItemStack is) {
		if (configInstance.getSalvageTools() && ItemChecks.isTool(is)) {
			return true;
		}
		if (configInstance.getSalvageArmor() && ItemChecks.isArmor(is)) {
			return true;
		}
		return false;
	}
}
