package decimatenetworkcore.punish;

public class Punishment {

	private int id;
	private PunishmentType type;
	private String reason;
	private long applied;
	private long expiration;
	private String punisherUuid;
	private String punishedUuid;
	private boolean reverted;
	
	public Punishment(int id, PunishmentType type, String reason, long applied, long expiration, String punisherUuid, String punishedUuid, boolean reverted){
		this.id = id;
		this.type = type;
		this.reason = reason;
		this.applied = applied;
		this.expiration = expiration;
		this.punisherUuid = punisherUuid;
		this.punishedUuid = punishedUuid;
		this.reverted = reverted;
	}
	
	public boolean isActive(){
		if(!reverted && expiration == -1){
			return true;
		}
		return !reverted && expiration > System.currentTimeMillis();
	}
	
	public int getId(){
		return id;
	}
	
	public PunishmentType getType(){
		return type;
	}
	
	public String getReason(){
		return reason;
	}
	
	public long getApplied(){
		return applied;
	}
	
	public long getExpiration(){
		return expiration;
	}
	
	public String getPunisherUUID(){
		return punisherUuid;
	}
	
	public String getPunishedUUID(){
		return punishedUuid;
	}
	
	public boolean isReverted(){
		return reverted;
	}
	
	public void revert(){
		this.reverted = true;
	}
	
	public String getRemainingTimeString(){
		return getTimeString(System.currentTimeMillis(), true);
	}
	
	private String getTimeString(long n, boolean activeCare){
		if(!isActive() && activeCare){
			return "EXPIRED";
		}
		if(expiration == -1){
			return "forever";
		}
		long time = expiration - n;

		int seconds = (int) ((time / 1000) % 60);
		int minutes = (int) ((time / (1000 * 60)) % 60);
		int hours = (int) ((time / (1000 * 60 * 60)) % 24);
		int days = (int) ((time / (1000 * 60 * 60 * 24)));

		return (days != 0 ? days + "d " : "") + (hours != 0 ? hours + "h " : "") + (minutes != 0 ? minutes + "m " : "") + (seconds != 0 ? seconds + "s" : "");
	}
	
	public String getTotalTimeString(){
		return getTimeString(applied, false);
	}
	
}
