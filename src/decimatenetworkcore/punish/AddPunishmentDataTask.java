package decimatenetworkcore.punish;

import java.sql.PreparedStatement;
import java.sql.ResultSet;

import org.bukkit.scheduler.BukkitRunnable;

import decimatenetworkcore.core.DecimateNetworkCore;


public class AddPunishmentDataTask extends BukkitRunnable {

	private Punishment punishment;
	
	public AddPunishmentDataTask(Punishment punishment){
		this.punishment = punishment;
	}
	
	public void run(){
		try{
			PreparedStatement statement = DecimateNetworkCore.getInstance().getConnection().prepareStatement(
					"SELECT * FROM `NetworkPunishments` WHERE `id` = ?"
			);
			statement.setInt(1, punishment.getId());
			ResultSet resultSet = statement.executeQuery();
			boolean isLoaded = resultSet.next();
			if(!isLoaded){
				PreparedStatement ps = DecimateNetworkCore.getInstance().getConnection().prepareStatement(
						"INSERT INTO `NetworkPunishments` (`id`, `type`, `reason`, `applied`, `expiration`, `punisherUuid`, `punishedUuid`, `reverted`) VALUES (?, ?, ?, ?, ?, ?, ?, ?)"
				);

				ps.setInt(1, punishment.getId());
				ps.setString(2, punishment.getType().toString());
				ps.setString(3, punishment.getReason());
				ps.setLong(4, punishment.getApplied());
				ps.setLong(5, punishment.getExpiration());
				ps.setString(6, punishment.getPunisherUUID());
				ps.setString(7, punishment.getPunishedUUID());
				ps.setBoolean(8, punishment.isReverted());
				ps.executeUpdate();
				ps.close();
			}
			resultSet.close();
			statement.close();
		}catch(Exception ex){
			ex.printStackTrace();
		}
	}
	
}
