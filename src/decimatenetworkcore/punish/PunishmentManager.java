package decimatenetworkcore.punish;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import decimate.WarSocket.UserPunishmentEvent;
import decimate.WarSocket.UserRevertPunishmentEvent;
import decimate.WarSocket.WarSocket;
import decimatenetworkcore.core.DataUser;
import decimatenetworkcore.core.DecimateNetworkCore;

public class PunishmentManager implements Listener, CommandExecutor {

	private List<Punishment> punishments = new ArrayList<>();
	private List<Integer> executedIds = new ArrayList<>();
	
	public PunishmentManager(){
		loadPunishments();
	}
	
	public List<Punishment> getPunishments(){
		return punishments;
	}
	
	public List<Punishment> getPunishments(String uuid){
		List<Punishment> result = new ArrayList<Punishment>();
		for(Punishment punishment : punishments){
			if(punishment.getPunishedUUID().equals(uuid)){
				result.add(punishment);
			}
		}
		return result;
	}
	
	public void loadPunishment(Punishment punishment){
		this.punishments.add(punishment);
	}
	
	@SuppressWarnings("deprecation")
	private void loadPunishments(){
		Bukkit.getServer().getScheduler().runTaskAsynchronously(DecimateNetworkCore.getInstance(), new LoadPunishmentDataTask());
	}
	
	public Punishment getPunishment(int id){
		return this.punishments.get(id);
	}
	
	public void punish(PunishmentType type, String reason, long expiration, String punisherUuid, String punishedUuid){
		this.executedIds.add(punishments.size());
		WarSocket.getInstance().emitUserPunishment(punishments.size(), type.toString(), reason, System.currentTimeMillis(), expiration, punisherUuid, punishedUuid);
	}
	
	@SuppressWarnings("deprecation")
	public void revert(PunishmentType type, String uuid){
		for(Punishment punishment : this.getPunishments(uuid)){
			if(punishment.isActive() && punishment.getType().equals(type)){
				punishment.revert();
				Bukkit.getServer().getScheduler().runTaskAsynchronously(DecimateNetworkCore.getInstance(), new PushPunishmentDataTask(punishment));
				WarSocket.getInstance().emitUserRevertPunishment(punishment.getId());
			}
		}
	}
	
	@EventHandler
	public void onRevert(UserRevertPunishmentEvent event){
		this.getPunishment(event.getId()).revert();
	}
	
	@SuppressWarnings("deprecation")
	@EventHandler
	public void onPunish(UserPunishmentEvent event){
		// Changed the apply and expiration as servers have different system times.
		Punishment punishment = new Punishment(event.getId(), PunishmentType.valueOf(event.getType()), event.getReason(), System.currentTimeMillis(),
				event.getExpiration() == -1 ? -1 : System.currentTimeMillis() + event.getExpiration() - event.getApplied(), event.getPunisherUUID(), event.getPunishedUUID(), false);
		this.punishments.add(punishment);
		DataUser du = DecimateNetworkCore.getInstance().getDataUserManager().getDataUser(punishment.getPunishedUUID());
		if(du != null){
			du.loadPunishment(punishment);
		}
		if(this.executedIds.contains(punishment.getId())){
			Bukkit.getServer().getScheduler().runTaskAsynchronously(DecimateNetworkCore.getInstance(), new AddPunishmentDataTask(punishment));
		}
	}
	
	private long getTimeFromMicroString(String string){
		if(string.equalsIgnoreCase("forever") || string.equalsIgnoreCase("infinity")){
			return -1;
		}
		char m = string.charAt(string.length() - 1);
		String numb = string.substring(0, string.length()-1);
		try{
			int n = Integer.valueOf(numb);
			if(n <= 0){
				return 0;
			}
			if(m == 'w' || m == 'W'){
				return (long) n*1000*60*60*24*7;
			}
			if(m == 'd' || m == 'D'){
				return (long) n*1000*60*60*24;
			}
			if(m == 'h' || m == 'H'){
				return (long) n*1000*60*60;
			}
			if(m == 'm' || m == 'M'){
				return (long) n*1000*60;
			}
			if(m == 's' || m == 'S'){
				return (long) n*1000;
			}
		}catch(Exception ex){ }
		return -2;
	}
	
	private String concatStrings(String[] strings, int start){
		String result = "";
		for(int i = start; i < strings.length; i++){
			result += strings[i] + " ";
		}
		return result.trim();
	}

	@SuppressWarnings("deprecation")
	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
//		if(sender instanceof Player){
//			Player player = (Player) sender;
		CommandSender player = sender;
			
			// /mute _Ug 14d fly hacking
			if(command.getName().equalsIgnoreCase("pinfo")){
				if(!player.hasPermission("Decimate.punishment.info")){
					player.sendMessage(ChatColor.RED + "You do not have permission to use this command.");
					return false;
				}
				if(args.length >= 1){
					try{
						int n = Integer.parseInt(args[0]);
						if(n >= 0){
							if(this.punishments.size() > n){
								Punishment punishment = this.punishments.get(n);
								if(punishment.getId() != n){
									boolean nu = true;
									for(Punishment p : this.punishments){
										if(p.getId() == n){
											punishment = p;
											nu = false;
											break;
										}
									}
									if(nu){
										player.sendMessage(ChatColor.RED + "No such punishment exists.");
										return false;
									}
								}
								player.sendMessage(ChatColor.GRAY + "Punishment ID: " + ChatColor.YELLOW + "#" + n);
								player.sendMessage(ChatColor.GRAY + "Type: " + ChatColor.YELLOW + punishment.getType().toString());
								player.sendMessage(ChatColor.GRAY + "Reason: " + ChatColor.YELLOW + punishment.getReason());
								player.sendMessage(ChatColor.GRAY + "Length: " + ChatColor.YELLOW + punishment.getTotalTimeString());
								player.sendMessage(ChatColor.GRAY + "Time Remaining: " + ChatColor.YELLOW + punishment.getRemainingTimeString());
								player.sendMessage(ChatColor.GRAY + "Punished Player: " + ChatColor.YELLOW + punishment.getPunishedUUID());
								player.sendMessage(ChatColor.GRAY + "Staff: " + ChatColor.YELLOW + punishment.getPunisherUUID());
								player.sendMessage(ChatColor.GRAY + "Reverted: " + ChatColor.YELLOW + punishment.isReverted());
							}else{
								player.sendMessage(ChatColor.RED + "No such punishment exists.");
							}
						}else{
							player.sendMessage(ChatColor.RED + "Invalid number.");
						}
					}catch(Exception ex){
						player.sendMessage(ChatColor.RED + "Invalid number.");
					}
				}
			}else if(command.getName().equalsIgnoreCase("mute")){
				if(player.hasPermission("Decimatepvp.mute.apply")){
					if(args.length >= 3){
						OfflinePlayer offp = Bukkit.getServer().getOfflinePlayer(args[0]);
						if(offp.isOp()){
							player.sendMessage(ChatColor.RED + "You may not mute this player.");
							if(offp.isOnline()){
								offp.getPlayer().sendMessage(ChatColor.RED.toString() + ChatColor.BOLD + "WARNING " + ChatColor.GRAY + player.getName() + " tried to mute you.");
							}
							return false;
						}
						if(offp.hasPlayedBefore() || offp.isOnline()){
							long l = getTimeFromMicroString(args[1]);
							if(l != -2){
								String reason = concatStrings(args, 2);
								this.punish(PunishmentType.MUTE, reason, l == -1 ? -1 : System.currentTimeMillis() + l, sender instanceof Player ? ((Player)sender).getUniqueId().toString() : "CONSOLE", offp.getUniqueId().toString());
								player.sendMessage(ChatColor.GREEN + "Successfully muted " + offp.getName() + "!");
								Bukkit.broadcastMessage("");
								Bukkit.broadcastMessage(ChatColor.RED + offp.getName() + ChatColor.GRAY + " was muted by " + ChatColor.RED + player.getName() + ChatColor.GRAY + " for " +
								ChatColor.RED + getTime(l) + ChatColor.GRAY + " for " + ChatColor.RED + reason + ChatColor.GRAY + "!");
								Bukkit.broadcastMessage("");
							}else{
								player.sendMessage(ChatColor.RED + "Invalid time string. Contact another staff member if you need help.");
							}
						}else{
							player.sendMessage(ChatColor.RED + "This player has never played before.");
						}
					}else{
						player.sendMessage(ChatColor.RED + "Invalid syntax. Try: " + ChatColor.YELLOW + "/mute (player) (time) (reason)");
					}
				}else{
					player.sendMessage(ChatColor.RED + "You do not have permission to use this command.");
				}
			}else
			if(command.getName().equalsIgnoreCase("ban")){
				if(player.hasPermission("Decimatepvp.ban.apply")){
					if(args.length >= 3){
						OfflinePlayer offp = Bukkit.getServer().getOfflinePlayer(args[0]);
						if(offp.isOp()){
							player.sendMessage(ChatColor.RED + "You may not ban this player.");
							if(offp.isOnline()){
								offp.getPlayer().sendMessage(ChatColor.RED.toString() + ChatColor.BOLD + "WARNING " + ChatColor.GRAY + player.getName() + " tried to ban you.");
							}
							return false;
						}
						if(offp.hasPlayedBefore() || offp.isOnline()){
							long l = getTimeFromMicroString(args[1]);
							if(l != -2){
								String reason = concatStrings(args, 2);
								this.punish(PunishmentType.BAN, reason, l == -1 ? -1 : System.currentTimeMillis() + l, sender instanceof Player ? ((Player)sender).getUniqueId().toString() : "CONSOLE", offp.getUniqueId().toString());
								player.sendMessage(ChatColor.GREEN + "Successfully banned " + offp.getName() + "!");
								Bukkit.broadcastMessage("");
								Bukkit.broadcastMessage(ChatColor.RED + offp.getName() + ChatColor.GRAY + " was banned by " + ChatColor.RED + player.getName() + ChatColor.GRAY + " for " +
								ChatColor.RED + getTime(l) + ChatColor.GRAY + " for " + ChatColor.RED + reason + ChatColor.GRAY + "!");
								Bukkit.broadcastMessage("");
							}else{
								player.sendMessage(ChatColor.RED + "Invalid time string. Contact another staff member if you need help.");
							}
						}else{
							player.sendMessage(ChatColor.RED + "This player has never played before.");
						}
					}else{
						player.sendMessage(ChatColor.RED + "Invalid syntax. Try: " + ChatColor.YELLOW + "/ban (player) (time) (reason)");
					}
				}else{
					player.sendMessage(ChatColor.RED + "You do not have permission to use this command.");
				}
			}else
			if(command.getName().equalsIgnoreCase("unmute")){
				if(player.hasPermission("Decimatepvp.mute.pardon")){
					if(args.length >= 1){
						OfflinePlayer offp = Bukkit.getServer().getOfflinePlayer(args[0]);
						if(offp.hasPlayedBefore() || offp.isOnline()){
							this.revert(PunishmentType.MUTE, offp.getUniqueId().toString());
							player.sendMessage(ChatColor.GREEN + "Sucessfully lifted " + offp.getName() + "'s mutes.");
						}else{
							player.sendMessage(ChatColor.RED + "This player has never played before.");
						}
					}else{
						player.sendMessage(ChatColor.RED + "Invalid syntax. Try: " + ChatColor.YELLOW + "/unmute (player)");
					}
				}else{
					player.sendMessage(ChatColor.RED + "You do not have permission to use this command.");
				}
			}else
			if(command.getName().equalsIgnoreCase("unban")){
				if(player.hasPermission("Decimatepvp.ban.pardon")){
					if(args.length >= 1){
						OfflinePlayer offp = Bukkit.getServer().getOfflinePlayer(args[0]);
						if(offp.hasPlayedBefore()){
							this.revert(PunishmentType.BAN, offp.getUniqueId().toString());
							player.sendMessage(ChatColor.GREEN + "Sucessfully lifted " + offp.getName() + "'s bans.");
						}else{
							player.sendMessage(ChatColor.RED + "This player has never played before.");
						}
					}else{
						player.sendMessage(ChatColor.RED + "Invalid syntax. Try: " + ChatColor.YELLOW + "/unban (player)");
					}
				}else{
					player.sendMessage(ChatColor.RED + "You do not have permission to use this command.");
				}
			}
//		}
		return false;
	}
	
	public String getTime(long time){
		if(time == -1){
			return "forever";
		}

		int seconds = (int) ((time / 1000) % 60);
		int minutes = (int) ((time / (1000 * 60)) % 60);
		int hours = (int) ((time / (1000 * 60 * 60)) % 24);
		int days = (int) ((time / (1000 * 60 * 60 * 24)));

		return ((days != 0 ? days + "d " : "") + (hours != 0 ? hours + "h " : "") + (minutes != 0 ? minutes + "m " : "") + (seconds != 0 ? seconds + "s" : "")).trim();
	}
	
}
