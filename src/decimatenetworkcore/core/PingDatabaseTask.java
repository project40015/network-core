package decimatenetworkcore.core;

import java.sql.SQLException;

import org.bukkit.scheduler.BukkitRunnable;

public class PingDatabaseTask extends BukkitRunnable {

	@Override
	public void run() {
		try {
			DecimateNetworkCore.getInstance().getConnection()
					.prepareStatement("/* ping */ SELECT 1 FROM `NetworkPunishments`");
			DecimateNetworkCore.getInstance().getConnection()
			.prepareStatement("/* ping */ SELECT 1 FROM `DataUser`");
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

}
