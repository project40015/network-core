package decimatenetworkcore.punish;

import java.sql.PreparedStatement;

import org.bukkit.scheduler.BukkitRunnable;

import decimatenetworkcore.core.DecimateNetworkCore;

public class PushPunishmentDataTask extends BukkitRunnable {

	private Punishment punishment;

	public PushPunishmentDataTask(Punishment punishment) {
		this.punishment = punishment;
	}

	public void run() {
		try {
			PreparedStatement s = DecimateNetworkCore.getInstance().getConnection().prepareStatement(
					"UPDATE `NetworkPunishments` SET `type` = ?, `reason` = ?, `applied` = ?, `expiration` = ?, `punisherUuid` = ?, `punishedUuid` = ?, `reverted` = ? WHERE `id` = ?");
			s.setInt(8, punishment.getId());
			s.setString(1, punishment.getType().toString());
			s.setString(2, punishment.getReason());
			s.setLong(3, punishment.getApplied());
			s.setLong(4, punishment.getExpiration());
			s.setString(5, punishment.getPunisherUUID());
			s.setString(6, punishment.getPunishedUUID());
			s.setBoolean(7, punishment.isReverted());
			s.executeUpdate();
			s.close();

		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

}
