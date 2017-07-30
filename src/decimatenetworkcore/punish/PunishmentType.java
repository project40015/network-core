package decimatenetworkcore.punish;

import org.bukkit.Material;

public enum PunishmentType {

	MUTE("muted", Material.BOOK), BAN("banned", Material.IRON_SWORD);

	private String action;
	private Material block;

	PunishmentType(String action, Material block) {
		this.action = action;
		this.block = block;
	}

	public String getAction() {
		return action;
	}

	public Material getBlock() {
		return block;
	}

}
