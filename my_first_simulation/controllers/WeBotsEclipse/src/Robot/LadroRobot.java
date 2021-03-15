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
	
	public void connectToServer()
	{
		clientConnectionHandler.start();
	}
	
	public void hide()
	{
		if (mappa == null || hidden)
			return;
		
		/*for (int i = 0; i < mappa.getXSize(); ++i)
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
		}*/
		
		this.robotPosition = new Point(15, 15);
		
		ArrayList<Point> pts = potentialShelters(mappa);
		Point dest;
		Random r = new Random();
		
		do 
		{
			int index = r.nextInt(pts.size());
			dest = pts.get(index);
			dest.print();
		}
		while(!goTo(dest));
		
		hidden = true;
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
		
		path.add(0, new Point(robotPosition));
		ArrayList<Integer> convertedPath = convertToDirections(path);
		
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
	
	private ArrayList<Point> potentialShelters(Mappa mappa)
	{
		ArrayList<Point> possiblePoints = new ArrayList<Point>();
		
		for(int x = 0; x < mappa.getXSize(); ++x)
		{
			for(int y = 0; y < mappa.getYSize(); ++y)
			{
				if(mappa.get(x, y) == 0) 
				{
					if(checkPoint(mappa, x, y))
						possiblePoints.add(new Point(x,y));
				}
			}
		}
		
		return possiblePoints;
	}
    
    private boolean checkPoint(Mappa mappa,int x,int y) 
	{
		int north, south, east, west;
		
		north = x - 1 >= 0 ? mappa.get(x-1, y) : 0;
		south = x + 1 < mappa.getXSize() ? mappa.get(x+1, y) : 0;
		east = y + 1 < mappa.getYSize() ? mappa.get(x, y+1) : 0;
		west = y - 1 >= 0 ? mappa.get(x, y-1) : 0;
		
		return ((north + south + east + west) == 3);
	}
    
    @Override
	public void onStcSendMapReceived(Mappa mappa)
	{
		this.mappa = mappa;
	}
}
