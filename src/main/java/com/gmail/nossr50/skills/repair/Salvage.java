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
				final int salvagedAmount = ItemChecks.getSalvagedAmount(inHand);
				final int itemID = ItemChecks.getSalvagedItemID(inHand);

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
