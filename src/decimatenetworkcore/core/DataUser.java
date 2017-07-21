package decimatenetworkcore.core;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

import decimatenetworkcore.punish.Punishment;
import decimatenetworkcore.punish.PunishmentType;

public class DataUser {

	private String uuid;
	private List<Punishment> punishments = new ArrayList<>();
	
	public DataUser(String uuid){
		this.uuid = uuid;
		loadPunishments();
	}
	
	public boolean isPunished(PunishmentType type){
		for(Punishment punishment : punishments){
			if(punishment.isActive() && type.equals(punishment.getType())){
				return true;
			}
		}
		return false;
	}
	
	public void loadPunishment(Punishment punishment){
		this.punishments.add(punishment);
		OfflinePlayer offp = Bukkit.getServer().getOfflinePlayer(UUID.fromString(uuid));
		if(offp.isOnline()){
			Player player = (Player) offp;
			player.sendMessage(ChatColor.GRAY + "You have been " + ChatColor.RED.toString() + ChatColor.BOLD + punishment.getType().getAction() + ChatColor.GRAY + " for "
					+ ChatColor.RED + punishment.getReason() + ChatColor.GRAY + " for " + ChatColor.RED + punishment.getRemainingTimeString() + ChatColor.GRAY + "!");
			if(punishment.getType().equals(PunishmentType.BAN)){
				Bukkit.getServer().getScheduler().runTask(DecimateNetworkCore.getInstance(), new Runnable(){

					@Override
					public void run() {
						player.kickPlayer(ChatColor.RED + "You are banned for " + punishment.getReason() + "!\n Expires: " + ChatColor.YELLOW + punishment.getRemainingTimeString()
						+ "\n" + ChatColor.RED + "Appeal: " + ChatColor.YELLOW.toString() + ChatColor.UNDERLINE + "http://decimatepvp.com/forums/forums/ban-appeals.13/");						
					}
				});

			}
		}
	}
	
	public Punishment getActivePunishment(PunishmentType type){
		for(Punishment punishment : punishments){
			if(punishment.isActive() && type.equals(punishment.getType())){
				return punishment;
			}
		}
		return null;
	}
	
	public String getUUID(){
		return uuid;
	}
	
	private void loadPunishments(){
		this.punishments = DecimateNetworkCore.getInstance().getPunishmentManager().getPunishments(uuid);
	}
	
	public List<Punishment> getPunishments(){
		return punishments;
	}
	
}
