package Robot;

import Map.Mappa;
import Map.Point;
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
	public void onStcSendMapReceived(Mappa mappa)
	{
		this.mappa = new Mappa(mappa.getXSize(), mappa.getYSize());
	}
	
	public void explore()
	{
		if (mappa == null)
			return;
		
		boolean qualcosa = true;
		boolean obstaclesInFront = false;
		
		this.robotPosition = new Point(4,4);
		
		while (qualcosa)
		{
			obstaclesInFront = goStraightOn(clientConnectionHandler);
			if (!obstaclesInFront)
			{
				int row = -1;
				int col = -1;
				
				switch(direction)
			    {
			    case NORD:
			    	row = robotPosition.getX() - 1;
			    	col = robotPosition.getY();
			    	break;
			    case EST:
			    	row = robotPosition.getX();
			    	col = robotPosition.getY() + 1;
			    	break;
			    case SUD:
			    	row = robotPosition.getX() + 1;
			    	col = robotPosition.getY();
			    	break;
			    case OVEST:
			    	row = robotPosition.getX();
			    	col = robotPosition.getY() - 1;
			    	break;
			    }
				
				mappa.setValue(row, col, 1);
				
				turnRight(clientConnectionHandler);
				
				System.out.println(mappa);
			}
		}
	}
}
