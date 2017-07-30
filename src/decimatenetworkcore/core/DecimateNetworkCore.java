package decimatenetworkcore.core;

import java.sql.Connection;
import java.sql.DriverManager;

import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

import decimatenetworkcore.punish.PunishmentManager;

public class DecimateNetworkCore extends JavaPlugin {

	private static DecimateNetworkCore instance;
	private String conString = "jdbc:mysql://198.100.26.121/mc_11737?user=mc_11737&password=f5fcae0e7d&autoReconnect=true&wait_timeout=1209600&";
	private Connection connection;

	private PunishmentManager punishmentManager;
	private DataUserManager dataUserManager;

	@Override
	public void onEnable() {
		instance = this;

		this.establishConnection();
		this.punishmentManager = new PunishmentManager();
		this.dataUserManager = new DataUserManager();

		Bukkit.getServer().getPluginManager().registerEvents(punishmentManager, this);
		Bukkit.getServer().getPluginManager().registerEvents(dataUserManager, this);

		this.getCommand("ban").setExecutor(punishmentManager);
		this.getCommand("unban").setExecutor(punishmentManager);
		this.getCommand("mute").setExecutor(punishmentManager);
		this.getCommand("unmute").setExecutor(punishmentManager);
		this.getCommand("pinfo").setExecutor(punishmentManager);
		this.getCommand("history").setExecutor(punishmentManager);

		Bukkit.getServer().getScheduler().scheduleSyncRepeatingTask(instance, new Runnable() {

			@SuppressWarnings("deprecation")
			@Override
			public void run() {
				System.out.println("Pinging database...");
				Bukkit.getServer().getScheduler().runTaskAsynchronously(instance, new PingDatabaseTask());
			}

		}, 20 * 60 * 30, 20 * 60 * 30);
	}

	public Connection getConnection() {
		return this.connection;
	}

	public PunishmentManager getPunishmentManager() {
		return this.punishmentManager;
	}

	public DataUserManager getDataUserManager() {
		return this.dataUserManager;
	}

	private void establishConnection() {
		try {
			Class.forName("com.mysql.jdbc.Driver");
			this.connection = DriverManager.getConnection(conString);
			this.connection.setAutoCommit(true);
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	public static DecimateNetworkCore getInstance() {
		return instance;
	}

}
