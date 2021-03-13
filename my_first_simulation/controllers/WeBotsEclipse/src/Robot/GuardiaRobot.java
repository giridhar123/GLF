package Robot;

import java.util.Random;

import Map.Mappa;
import Map.Point;
import Network.Client;
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
		System.out.println("Mappa ricevuta");
		this.mappa = new Mappa(mappa.getXSize(), mappa.getYSize());
	}
	
	public void explore()
	{
		if (mappa == null)
			return;
		
		boolean qualcosa = true;
		
		this.robotPosition = new Point(4,4);
		Random r = new Random();
		
		while (qualcosa)
		{
			if (!goStraightOn())
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
				
				if (r.nextDouble() < 0.5)
					turnRight();
				else
					turnLeft();
				
				System.out.println(mappa);
			}
		}
	}
}
