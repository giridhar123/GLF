package Robot;

import java.util.ArrayList;

import General.AStarSearcher;
import Map.Mappa;
import Map.Point;
import Network.ClientConnectionHandler;
import Network.Packets.ClientToServer.CTS_PEER_INFO;

public class LadroRobot extends GenericRobot implements Client {

	private ClientConnectionHandler clientConnectionHandler;
	
	public LadroRobot(int direction)
	{
		super(direction);
		
		clientConnectionHandler = new ClientConnectionHandler(CTS_PEER_INFO.LADRO, this);
        clientConnectionHandler.start();
	}
	
	private boolean goTo(Point goal)
	{
		AStarSearcher aStarSearcher = new AStarSearcher(mappa);		
		ArrayList<Point> path = aStarSearcher.getPath(robotPosition, goal);
		
		if (path == null)
		{
			System.out.println("No path found");
			return false;
		}
		else
			System.out.println("Path found");
		
		path.add(0, robotPosition);
		ArrayList<Integer> convertedPath = convertToDirections(path);
		
		int value;
		
		for (int i = 0; i < convertedPath.size(); ++i)
		{
			value = convertedPath.get(i);
			int sig = this.direction - value;
			
			if(sig < 0) sig +=4;
			
			if((sig % 4) == 1) turnRight(clientConnectionHandler);
			else if((sig % 4) == 2)
			{
				turnLeft(clientConnectionHandler);
				turnLeft(clientConnectionHandler);
			}
			else if((sig % 4) == 3) turnLeft(clientConnectionHandler); 
		
			goStraightOn(clientConnectionHandler);
		}
		
		return true;
	}
	
	private ArrayList<Integer> convertToDirections(ArrayList<Point> path)
	{
		ArrayList<Integer> newPath = new ArrayList<>();
		
		Point pos1 = path.get(0);
		Point pos2;
		for (int i = 1; i < path.size(); ++i)
		{
			pos2 = path.get(i);
			
			if (pos2.getX() < pos1.getX())
				newPath.add(NORD);
			else if (pos2.getX() > pos1.getX())
				newPath.add(SUD);
			else if (pos2.getY() > pos1.getY())
				newPath.add(EST);
			else if (pos2.getY() < pos1.getY())
				newPath.add(OVEST);
			
			pos1 = pos2;
		}
		
		return newPath;
	}
	
	@Override
	public void onStcSendMapReceived(Mappa mappa)
	{
		System.out.println("LADRO: MAPPA RICEVUTA");
		this.mappa = mappa;
		
		for (int i = 0; i < mappa.getXSize(); ++i)
		{
			for (int j = 0; j < mappa.getYSize(); ++j)
			{
				if (i == 10 && j == 10)
					System.out.print(" b" + mappa.get(i, j) + "b");
				else if (i == 20 && j == 20)
					System.out.print(" a" + mappa.get(i, j) + "a");
				else
					System.out.print(" " + mappa.get(i, j) + " ");
			}
			
			System.out.println("");
		}
		//funzione cerca punto buono
		this.robotPosition = new Point(10, 10);
		Point goal = new Point(20, 20);
		goTo(goal);
	}
}
