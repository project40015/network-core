package decimatenetworkcore.history;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;

import decimatenetworkcore.core.DecimateNetworkCore;
import decimatenetworkcore.punish.Punishment;

public class History {

	private String uuid;
	private Player viewer;

	public History(String uuid, Player viewer) {
		this.uuid = uuid;
		this.viewer = viewer;
		view();
	}

	private void view() {
		Inventory inventory = Bukkit.getServer().createInventory(viewer, 54, ChatColor.GRAY + "Punishment History");
		for (Punishment punishment : DecimateNetworkCore.getInstance().getPunishmentManager().getPunishments(uuid)) {
			inventory.addItem(punishment.toItemStack());
		}
		viewer.openInventory(inventory);
	}

	public String getUUID() {
		return uuid;
	}

	public Player getViewer() {
		return viewer;
	}

}
