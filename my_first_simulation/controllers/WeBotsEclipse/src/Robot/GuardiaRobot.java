package Robot;

import Map.Mappa;

public class GuardiaRobot extends GenericRobot implements Client {

	public GuardiaRobot(int direction)
	{
		super(direction);
	}
	
	@Override
	public void onStcSendMapReceived(Mappa mappa) {
		//Do Nothing
	}

}
