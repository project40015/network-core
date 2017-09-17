package decimatenetworkcore.core;

import java.sql.Date;
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

	private String name;
	private String uuid;
	private Date first_joined;
	private Date last_joined;
	private long playtime;
	private double total_donations;
	private int mobs_killed;
	private int blocks_broken;
	private int blocks_placed;
	private int players_killed;
	private int deaths;
	private String fac1_active_trail;
	private String fac1_rank;
	private String first_join_ip;
	
	private long start;
	
	private List<Punishment> punishments = new ArrayList<>();

	public DataUser(String uuid) {
		this.uuid = uuid;
		start = System.currentTimeMillis();
		loadPunishments();
	}

	public boolean isPunished(PunishmentType type) {
		for (Punishment punishment : punishments) {
			if (punishment.isActive() && type.equals(punishment.getType())) {
				return true;
			}
		}
		return false;
	}

	public void loadPunishment(Punishment punishment) {
		this.punishments.add(punishment);
		OfflinePlayer offp = Bukkit.getServer().getOfflinePlayer(UUID.fromString(uuid));
		if (offp.isOnline()) {
			Player player = (Player) offp;
			player.sendMessage(ChatColor.GRAY + "You have been " + ChatColor.RED.toString() + ChatColor.BOLD
					+ punishment.getType().getAction() + ChatColor.GRAY + " for " + ChatColor.RED
					+ punishment.getReason() + ChatColor.GRAY + " for " + ChatColor.RED
					+ punishment.getRemainingTimeString() + ChatColor.GRAY + "!");
			if (punishment.getType().equals(PunishmentType.BAN)) {
				Bukkit.getServer().getScheduler().runTask(DecimateNetworkCore.getInstance(), new Runnable() {

					@Override
					public void run() {
						player.kickPlayer(ChatColor.RED + "You are banned for " + punishment.getReason()
								+ "!\n Expires: " + ChatColor.YELLOW + punishment.getRemainingTimeString() + "\n"
								+ ChatColor.RED + "Appeal: " + ChatColor.YELLOW.toString() + ChatColor.UNDERLINE
								+ "http://decimatepvp.com/forums/forums/ban-appeals.13/");
					}
				});

			} else if (punishment.getType().equals(PunishmentType.IPBAN)) {
				Bukkit.getServer().getScheduler().runTask(DecimateNetworkCore.getInstance(), new Runnable() {

					@Override
					public void run() {
						player.kickPlayer(ChatColor.RED + "You are blacklisted for " + punishment.getReason()
								+ "\n"
								+ ChatColor.RED + "Appeal: " + ChatColor.YELLOW.toString() + ChatColor.UNDERLINE
								+ "http://decimatepvp.com/forums/forums/ban-appeals.13/");
					}
				});

			}
		}
	}

	public Punishment getActivePunishment(PunishmentType type) {
		for (Punishment punishment : punishments) {
			if (punishment.isActive() && type.equals(punishment.getType())) {
				return punishment;
			}
		}
		return null;
	}

	public String getName(){
		return name;
	}
	
	public void setName(String name){
		this.name = name;
	}
	
	public String getUUID() {
		return uuid;
	}
	
	public Date getFirstJoined(){
		return this.first_joined;
	}
	
	public void setFirstJoined(Date date){
		this.first_joined = date;
	}
	
	public Date getLastOnline(){
		return this.last_joined;
	}
	
	public void setLastOnline(Date date){
		this.last_joined = date;
	}
	
	public long getPlaytime(){
		return playtime + (System.currentTimeMillis() - start);
	}
	
	public void setPlaytime(long playtime){
		this.playtime = playtime;
	}
	
	public double getTotalDonations(){
		return total_donations;
	}
	
	public void setDonations(double donations){
		this.total_donations = donations;
	}
	
	public int getMobsKilled(){
		return mobs_killed;
	}
	
	public void setMobsKilled(int killed){
		this.mobs_killed = killed;
	}
	
	public int getBlocksBroken(){
		return blocks_broken;
	}
	
	public void setBlocksBroken(int blocks_broken){
		this.blocks_broken = blocks_broken;
	}
	
	public int getBlocksPlaced(){
		return blocks_placed;
	}
	
	public void setBlocksPlaced(int placed){
		this.blocks_placed = placed;
	}
	
	public int getPlayersKilled(){
		return players_killed;
	}
	
	public void setPlayersKilled(int killed){
		this.players_killed = killed;
	}
	
	public int getDeaths(){
		return deaths;
	}
	
	public void setDeaths(int deaths){
		this.deaths = deaths;
	}
	
	public String getFac1ActiveTrail(){
		return this.fac1_active_trail;
	}
	
	public void setFac1ActiveTrail(String trail){
		this.fac1_active_trail = trail;
	}
	
	public String getFac1Rank(){
		return this.fac1_rank;
	}
	
	public void setFac1Rank(String rank){
		this.fac1_rank = rank;
	}
	
	public String getFirstJoinIP(){
		return this.first_join_ip;
	}
	
	public void setFirstJoinIP(String ip){
		this.first_join_ip = ip;
	}

	private void loadPunishments() {
		this.punishments = DecimateNetworkCore.getInstance().getPunishmentManager().getPunishments(uuid);
	}

	public List<Punishment> getPunishments() {
		return punishments;
	}

}
