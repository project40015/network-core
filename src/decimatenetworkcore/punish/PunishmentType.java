package decimatenetworkcore.punish;

public enum PunishmentType {

	MUTE("muted"),
	BAN("banned");
	
	private String action;
	
	PunishmentType(String action){
		this.action = action;
	}
	
	public String getAction(){
		return action;
	}
	
}
