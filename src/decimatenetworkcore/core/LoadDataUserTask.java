package decimatenetworkcore.core;

import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import org.bukkit.scheduler.BukkitRunnable;

public class LoadDataUserTask extends BukkitRunnable {

	private String name;
	private String uuid;
	private String ip;
	
	public LoadDataUserTask(String name, String uuid, String ip){
		this.name = name;
		this.uuid = uuid;
		this.ip = ip;
	}
	
	public void run() {
		try {
			PreparedStatement statement = DecimateNetworkCore.getInstance().getConnection()
					.prepareStatement("SELECT * FROM `DataUser` where `uuid` = ?");
			statement.setString(1, uuid);
			ResultSet resultSet = statement.executeQuery();
			if(resultSet.next()){
				DataUser dataUser = new DataUser(uuid);
				dataUser.setName(name);
				dataUser.setFirstJoined(resultSet.getDate("first_joined"));
				dataUser.setLastOnline(new Date(System.currentTimeMillis()));
				dataUser.setPlaytime(resultSet.getLong("playtime"));
				dataUser.setDonations(resultSet.getDouble("total_donations"));
				dataUser.setMobsKilled(resultSet.getInt("mobs_killed"));
				dataUser.setBlocksBroken(resultSet.getInt("blocks_broken"));
				dataUser.setBlocksPlaced(resultSet.getInt("blocks_placed"));
				dataUser.setPlayersKilled(resultSet.getInt("players_killed"));
				dataUser.setDeaths(resultSet.getInt("deaths"));
				dataUser.setFac1ActiveTrail(resultSet.getString("fac1_active_trail"));
				dataUser.setFac1TNT(resultSet.getInt("fac1_tnt_bank"));
				dataUser.setFac1Rank(resultSet.getString("fac1_rank"));
				dataUser.setFirstJoinIP(resultSet.getString("first_join_ip"));
				DecimateNetworkCore.getInstance().getDataUserManager().addDataUser(dataUser);
			}else{
				DataUser dataUser = new DataUser(uuid);
				dataUser.setName(name);
				dataUser.setFirstJoined(new Date(System.currentTimeMillis()));
				dataUser.setLastOnline(new Date(System.currentTimeMillis()));
				dataUser.setPlaytime(0);
				dataUser.setDonations(0);
				dataUser.setMobsKilled(0);
				dataUser.setBlocksBroken(0);
				dataUser.setBlocksPlaced(0);
				dataUser.setPlayersKilled(0);
				dataUser.setDeaths(0);
				dataUser.setFac1ActiveTrail("");
				dataUser.setFac1Rank("DEFAULT");
				dataUser.setFac1TNT(0);
				dataUser.setFirstJoinIP(ip);
				DecimateNetworkCore.getInstance().getDataUserManager().addDataUser(dataUser);
				PreparedStatement ps = DecimateNetworkCore.getInstance().getConnection().prepareStatement(
						"INSERT INTO `DataUser` (`name`, `uuid`, `first_joined`, `last_joined`, `playtime`, `total_donations`, `mobs_killed`, `blocks_broken`, `blocks_placed`, `players_killed`, `deaths`, `fac1_active_trail`, `fac1_tnt_bank`, `fac1_rank`, `first_join_ip`) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)");
				ps.setString(1, name);
				ps.setString(2, uuid);
				ps.setDate(3, new Date(System.currentTimeMillis()));
				ps.setDate(4, new Date(System.currentTimeMillis()));
				ps.setLong(5, 0);
				ps.setDouble(6, 0);
				ps.setInt(7, 0);
				ps.setInt(8, 0);
				ps.setInt(9, 0);
				ps.setInt(10, 0);
				ps.setInt(11, 0);
				ps.setString(12, "");
				ps.setInt(13, 0);
				ps.setString(14, "DEFAULT");
				ps.setString(15, ip);
				ps.execute();
			}
			resultSet.close();
			statement.close();
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}
	
}
