package decimatenetworkcore.punish;

import java.sql.PreparedStatement;
import java.sql.ResultSet;

import org.bukkit.scheduler.BukkitRunnable;

import decimatenetworkcore.core.DecimateNetworkCore;

public class LoadPunishmentDataTask extends BukkitRunnable {

	public void run() {
		try {
			PreparedStatement statement = DecimateNetworkCore.getInstance().getConnection()
					.prepareStatement("SELECT * FROM `NetworkPunishments`");
			ResultSet resultSet = statement.executeQuery();
			int i = 0;
			while (resultSet.next()) {
				i++;
				int id = resultSet.getInt("id");
				PunishmentType type = PunishmentType.valueOf(resultSet.getString("type"));
				String reason = resultSet.getString("reason");
				long applied = resultSet.getLong("applied");
				long expiration = resultSet.getLong("expiration");
				String punisherUuid = resultSet.getString("punisherUuid");
				String punishedUuid = resultSet.getString("punishedUuid");
				boolean reverted = resultSet.getBoolean("reverted");
				DecimateNetworkCore.getInstance().getPunishmentManager().loadPunishment(
						new Punishment(id, type, reason, applied, expiration, punisherUuid, punishedUuid, reverted));
			}
			System.out.println("Loaded in " + i + " punishments.");
			resultSet.close();
			statement.close();
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

}
