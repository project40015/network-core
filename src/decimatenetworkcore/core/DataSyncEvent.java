package decimatenetworkcore.core;

import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class DataSyncEvent extends Event {

    private static final HandlerList handlers = new HandlerList();
    private String uuid;
    private String server;
    
    public DataSyncEvent(String uuid, String server){
    	this.uuid = uuid;
    	this.server = server;
    }
    
    public String getServer(){
    	return server;
    }
    
    public String getUuid(){
    	return uuid;
    }
    
    @Override
    public HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }

}
