package Robot;

import Map.Mappa;

public interface Client {
	public abstract void onStcSendMapReceived(Mappa mappa);
}
