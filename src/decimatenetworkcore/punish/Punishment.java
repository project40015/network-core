package decimatenetworkcore.punish;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import decimatenetworkcore.history.EnchantGlow;

public class Punishment {

	private int id;
	private PunishmentType type;
	private String reason;
	private long applied;
	private long expiration;
	private String punisherUuid;
	private String punishedUuid;
	private boolean reverted;
	private ItemStack itemStack;
	private SimpleDateFormat sdf;
	private Date resultDate;

	public Punishment(int id, PunishmentType type, String reason, long applied, long expiration, String punisherUuid,
			String punishedUuid, boolean reverted) {
		this.id = id;
		this.type = type;
		this.reason = reason;
		this.applied = applied;
		this.expiration = expiration;
		this.punisherUuid = punisherUuid;
		this.punishedUuid = punishedUuid;
		this.reverted = reverted;
		this.resultDate = new Date(applied);
		this.sdf = new SimpleDateFormat("MMM dd,yyyy HH:mm");
	}

	public ItemStack toItemStack() {
		this.itemStack = new ItemStack(type.getBlock());
		ItemMeta im = this.itemStack.getItemMeta();
		im.setDisplayName(ChatColor.RED + type.toString());

		List<String> lore = new ArrayList<>();

		lore.add(ChatColor.GRAY + "Punishment ID: " + ChatColor.YELLOW + "#" + id);
		lore.add(ChatColor.GRAY + "Type: " + ChatColor.YELLOW + type.toString());
		lore.add(ChatColor.GRAY + "Reason: " + ChatColor.YELLOW + reason);
		lore.add(ChatColor.GRAY + "Length: " + ChatColor.YELLOW + getTotalTimeString());
		lore.add(ChatColor.GRAY + "Time Remaining: " + ChatColor.YELLOW + getRemainingTimeString());
		lore.add(ChatColor.GRAY + "Staff: " + ChatColor.YELLOW + punisherUuid);
		lore.add(ChatColor.GRAY + "Applied: " + ChatColor.YELLOW + sdf.format(resultDate));
		lore.add(ChatColor.GRAY + "Reverted: " + ChatColor.YELLOW + reverted);

		im.setLore(lore);
		itemStack.setItemMeta(im);
		if (isActive()) {
			EnchantGlow.addGlow(itemStack);
		}
		return this.itemStack;
	}

	public boolean isActive() {
		if (reverted) {
			return false;
		}
		if (expiration == -1) {
			return true;
		}
		return expiration > System.currentTimeMillis();
	}

	public int getId() {
		return id;
	}

	public PunishmentType getType() {
		return type;
	}

	public String getReason() {
		return reason;
	}

	public long getApplied() {
		return applied;
	}

	public long getExpiration() {
		return expiration;
	}

	public String getPunisherUUID() {
		return punisherUuid;
	}

	public String getPunishedUUID() {
		return punishedUuid;
	}

	public boolean isReverted() {
		return reverted;
	}

	public void revert() {
		this.reverted = true;
	}

	public String getRemainingTimeString() {
		return getTimeString(System.currentTimeMillis(), true);
	}

	private String getTimeString(long n, boolean activeCare) {
		if (!isActive() && activeCare) {
			return "EXPIRED";
		}
		if (expiration == -1) {
			return "forever";
		}
		long time = expiration - n;

		int seconds = (int) ((time / 1000) % 60);
		int minutes = (int) ((time / (1000 * 60)) % 60);
		int hours = (int) ((time / (1000 * 60 * 60)) % 24);
		int days = (int) ((time / (1000 * 60 * 60 * 24)));

		return (days != 0 ? days + "d " : "") + (hours != 0 ? hours + "h " : "") + (minutes != 0 ? minutes + "m " : "")
				+ (seconds != 0 ? seconds + "s" : "");
	}

	public String getTotalTimeString() {
		return getTimeString(applied, false);
	}

}
