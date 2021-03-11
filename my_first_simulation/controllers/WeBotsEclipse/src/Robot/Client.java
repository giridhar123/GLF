package Robot;

import Map.Mappa;

public interface Client {
	
	public abstract void connectToServer();
	public abstract void onStcSendMapReceived(Mappa mappa);
}
