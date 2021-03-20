package Robot;

import java.util.ArrayList;
import java.util.Random;

import General.AStarSearcher;
import Map.Mappa;
import Map.Point;
import Network.Client;
import Network.ClientConnectionHandler;
import Network.Packets.ClientToServer.CTS_PEER_INFO;

public class LadroRobot extends GenericRobot implements Client {

	private ClientConnectionHandler clientConnectionHandler;
	private boolean hidden;
	
	public LadroRobot(int direction)
	{
		super(direction);
		
		hidden = false;		
		clientConnectionHandler = new ClientConnectionHandler(CTS_PEER_INFO.LADRO, this);
	}
	
	@Override
	public void connectToServer()
	{
		clientConnectionHandler.start();
	}
	
	public void hide()
	{
		if (mappa == null || hidden)
			return;
		
		this.robotPosition = new Point(15, 15);
		
		ArrayList<Point> pts = getPotentialsPoints();
		Point dest;
		Random r = new Random();
		
		int index = -1;
		do 
		{
			index = r.nextInt(pts.size());
			dest = pts.get(index);
			System.out.println("Ladro: Provo ad andare in: " + dest);
		}
		while(!goTo(dest));
		
		hidden = true;
	}
	
	private boolean goTo(Point goal)
	{
		AStarSearcher aStarSearcher = new AStarSearcher(mappa);		
		ArrayList<Point> path = aStarSearcher.getPath(robotPosition, goal);
		
		if (path == null)
			return false;
		
		ArrayList<Integer> convertedPath = AStarSearcher.pathToRobotDirections(path);
		
		int value, count;
		count = 0;
		
		for (int i = 0; i < convertedPath.size(); ++i)
		{
			value = convertedPath.get(i);
			
			if (value != this.direction)
			{
				goStraightOn(count);
				changeDirectionTo(value);
				count = 1;
			}
			else
				++count;
		}
		
		goStraightOn(count);
		
		return true;
	}
	
	private ArrayList<Point> getPotentialsPoints()
	{
		if (mappa == null)
			return null;
		
		ArrayList<Point> possiblePoints = new ArrayList<Point>();
		Point punto = null;
		
		for(int x = 0; x < mappa.getXSize(); ++x)
		{
			for(int y = 0; y < mappa.getYSize(); ++y)
			{
				punto = new Point(x, y);
				if(mappa.get(punto) == 0) 
				{
					if(checkPoint(new Point(x, y)))
						possiblePoints.add(new Point(x,y));
				}
			}
		}
		
		return possiblePoints;
	}
    
    private boolean checkPoint(Point punto) 
	{
    	if (mappa == null)
    		return false;
    	
    	int x = punto.getX();
    	int y = punto.getY();
    	
		int north, south, east, west;
		
		north = x - 1 >= 0 ? mappa.get(new Point(x - 1, y)) : 0;
		south = x + 1 < mappa.getXSize() ? mappa.get(new Point(x + 1, y)) : 0;
		east = y + 1 < mappa.getYSize() ? mappa.get(new Point(x, y + 1)) : 0;
		west = y - 1 >= 0 ? mappa.get(new Point(x, y - 1)) : 0;
		
		return ((north + south + east + west) == 3);
	}
    
    @Override
	public void onStcSendMapReceived(Mappa mappa)
	{
		this.mappa = mappa;
	}
}
