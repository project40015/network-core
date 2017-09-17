package decimatenetworkcore.core;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent.Result;
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import decimatenetworkcore.punish.Punishment;
import decimatenetworkcore.punish.PunishmentType;

public class DataUserManager implements Listener {

	private List<DataUser> users = new ArrayList<>();

	public DataUserManager() {
		loadOnline();
	}

//	@EventHandler
//	public void onJoin(PlayerJoinEvent event) {
//		DataUser du = this.getDataUser(event.getPlayer().getUniqueId().toString());
//		if(du != null){
//			du.setName(event.getPlayer().getName());
//		}
//	}
	
	@SuppressWarnings("deprecation")
	public void pushAll(){
		for(DataUser du : users){
			Bukkit.getScheduler().runTaskAsynchronously(DecimateNetworkCore.getInstance(), new PushDataUserTask(du));
		}
	}
	
	public void addDataUser(DataUser user){
		users.add(user);
	}

	@SuppressWarnings("deprecation")
	public void setRank(String uuid, String rank){
		Bukkit.getScheduler().runTaskAsynchronously(DecimateNetworkCore.getInstance(), new PushDataUserTask(uuid, rank));
	}
	
	@SuppressWarnings("deprecation")
	@EventHandler
	public void onLeave(PlayerQuitEvent event) {
		DataUser du = getDataUser(event.getPlayer().getUniqueId().toString());
		if(du != null){
			Bukkit.getScheduler().runTaskAsynchronously(DecimateNetworkCore.getInstance(), new PushDataUserTask(du));
			this.users.remove(du);
		}
	}
	
	@SuppressWarnings("deprecation")
	public void syncUser(String uuid, String server){
		DataUser du = getDataUser(uuid);
		if(du != null){
			Bukkit.getScheduler().runTaskAsynchronously(DecimateNetworkCore.getInstance(), new PushDataUserTask(du, server));
		}
	}

	@EventHandler
	public void onChat(AsyncPlayerChatEvent event) {
		if (this.getDataUser(event.getPlayer().getUniqueId().toString()).isPunished(PunishmentType.MUTE)) {
			Punishment punishment = this.getDataUser(event.getPlayer().getUniqueId().toString())
					.getActivePunishment(PunishmentType.MUTE);
			event.getPlayer()
					.sendMessage(ChatColor.RED + "You are currently muted for " + ChatColor.YELLOW
							+ punishment.getRemainingTimeString() + ChatColor.RED + " for " + ChatColor.YELLOW
							+ punishment.getReason() + ChatColor.RED + "! If you believe this to be a mistake,"
							+ " please appeal here: " + ChatColor.YELLOW.toString()
							+ "http://tinyurl.com/dappeal" + ChatColor.RED
							+ ". Punishment ID: " + ChatColor.YELLOW + "#" + punishment.getId());
			event.setCancelled(true);
		}
	}

	@SuppressWarnings("deprecation")
	@EventHandler
	public void onLogin(PlayerLoginEvent event){
		if(event.getResult().equals(PlayerLoginEvent.Result.ALLOWED)){
			Bukkit.getScheduler().runTaskAsynchronously(DecimateNetworkCore.getInstance(), new LoadDataUserTask(event.getPlayer().getName(), event.getPlayer().getUniqueId().toString(), event.getHostname()));
		}
	}
	
	@EventHandler
	public void onJoin(AsyncPlayerPreLoginEvent event) {
		for (Punishment punishment : DecimateNetworkCore.getInstance().getPunishmentManager()
				.getPunishments(event.getUniqueId().toString(), event.getAddress().getHostAddress())) {
			if (punishment.isActive() && punishment.getType().equals(PunishmentType.BAN)) {
				event.disallow(Result.KICK_BANNED,
						ChatColor.RED + "You are banned for " + punishment.getReason() + "!\n Expires: "
								+ ChatColor.YELLOW + punishment.getRemainingTimeString() + "\n" + ChatColor.RED
								+ "Appeal: " + ChatColor.YELLOW.toString() + ChatColor.UNDERLINE
								+ "http://tinyurl.com/dappeal");
				return;
			}
			if (punishment.isActive() && punishment.getType().equals(PunishmentType.IPBAN)) {
				event.disallow(Result.KICK_BANNED,
						ChatColor.RED + "You are " + ChatColor.DARK_GRAY + "blacklisted" + ChatColor.RED + " for " + punishment.getReason()
						+ "\n"
						+ ChatColor.RED + "Appeal: " + ChatColor.YELLOW.toString() + ChatColor.UNDERLINE
						+ "http://tinyurl.com/dappeal");
				return;
			}
		}
 	}

	public DataUser getDataUser(String uuid) {
		for (DataUser user : users) {
			if (user.getUUID().equals(uuid)) {
				return user;
			}
		}
		return null;
	}

	private void loadOnline() {
		for (Player player : Bukkit.getServer().getOnlinePlayers()) {
			users.add(new DataUser(player.getUniqueId().toString()));
		}
	}

	public List<DataUser> getDataUsers() {
		return users;
	}

}
