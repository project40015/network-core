package decimatenetworkcore.core;

import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import org.bukkit.Bukkit;
import org.bukkit.scheduler.BukkitRunnable;

public class PushDataUserTask extends BukkitRunnable {

	private DataUser user;
	private String server;
	
	private String uuid = "", rank = "";
	
	public PushDataUserTask(DataUser user){
		this.user = user;
		this.server = "none";
	}
	
	public PushDataUserTask(String uuid, String rank){
		this.uuid = uuid;
		this.rank = rank;
	}
	
	public PushDataUserTask(DataUser user, String server){
		this(user);
		this.server = server;
	}

	@Override
	public void run() {
		try {
			if(!uuid.equals("") && !rank.equals("")){
				PreparedStatement s = DecimateNetworkCore.getInstance().getConnection().prepareStatement(
						"UPDATE `DataUser` SET `fac1_rank` = ? WHERE `uuid` = ?");
				s.setString(2, uuid);
				s.setString(1, rank);
				s.execute();
				return;
			}
			PreparedStatement s = DecimateNetworkCore.getInstance().getConnection().prepareStatement(
					"UPDATE `DataUser` SET `name` = ?, `last_joined` = ?, `playtime` = ?, `total_donations` = ?, `mobs_killed` = ?, `blocks_broken` = ?, `blocks_placed` = ?, `players_killed` = ?, `deaths` = ?, `fac1_active_trail` = ?, `fac1_tnt_bank` = ?, `fac1_rank` = ? WHERE `uuid` = ?");
			s.setString(13, user.getUUID());
			s.setString(1, user.getName());
			s.setDate(2, new Date(System.currentTimeMillis()));
			s.setLong(3, user.getPlaytime());
			s.setDouble(4, user.getTotalDonations());
			s.setInt(5, user.getMobsKilled());
			s.setInt(6, user.getBlocksBroken());
			s.setInt(7, user.getBlocksPlaced());
			s.setInt(8, user.getPlayersKilled());
			s.setInt(9, user.getDeaths());
			s.setString(10, user.getFac1ActiveTrail());
			s.setInt(11, user.getFac1TNT());
			s.setString(12, user.getFac1Rank());
			s.execute();
			if(!this.server.equals("none")){
				Bukkit.getServer().getPluginManager().callEvent(new DataSyncEvent(user.getUUID(), server));
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
}
