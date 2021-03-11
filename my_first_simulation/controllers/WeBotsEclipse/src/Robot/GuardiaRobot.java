package Robot;

import Map.Mappa;
import Network.ClientConnectionHandler;
import Network.Packets.ClientToServer.CTS_PEER_INFO;

public class GuardiaRobot extends GenericRobot implements Client {
	
	private ClientConnectionHandler clientConnectionHandler;

	public GuardiaRobot(int direction)
	{
		super(direction);
		
		clientConnectionHandler = new ClientConnectionHandler(CTS_PEER_INFO.GUARDIA, this);
	}
	
	public void connectToServer()
	{
		clientConnectionHandler.start();
	}
	
	@Override
	public void onStcSendMapReceived(Mappa mappa) {
		//Do Nothing
	}

}
