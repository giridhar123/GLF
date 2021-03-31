package Robot;

import java.util.ArrayList;
import java.util.Random;

import General.AStarSearcher;
import General.SharedVariables;
import Map.Mappa;
import Map.Point;
import Map.StartPosition;
import Network.Client;
import Network.ClientConnectionHandler;
import Network.Packets.ClientToServer.CTS_GOING_TO;
import Network.Packets.ClientToServer.CTS_LADRO_HIDDEN;
import Network.Packets.ClientToServer.CTS_PEER_INFO;

public class LadroRobot extends GenericRobot implements Client {

	private ClientConnectionHandler clientConnectionHandler;
	private boolean hidden;
	private ArrayList<Point> possiblePoints;
	
	public LadroRobot(int direction)
	{
		super(direction);
		
		hidden = false;
		possiblePoints = null;
		
		clientConnectionHandler = new ClientConnectionHandler(CTS_PEER_INFO.LADRO, this);
	}
	
	@Override
	public void connectToServer()
	{
		clientConnectionHandler.start();
	}
	
	private void hide()
	{
		if (mappa == null || possiblePoints == null || hidden)
			return;
		
		Point dest;
		Random r = new Random();
			
		int index = -1;
		
		goStraightOn();
		
		do 
		{
			index = r.nextInt(possiblePoints.size());
			dest = new Point(possiblePoints.get(index));
			possiblePoints.remove(dest);
		}
		while(!goTo(dest));
		
		hidden = true;
		clientConnectionHandler.sendPacket(new CTS_LADRO_HIDDEN());
	}
	
	private boolean goTo(Point goal)
	{
		AStarSearcher aStarSearcher = new AStarSearcher(mappa);		
		ArrayList<Point> path = aStarSearcher.getPath(robotPosition, goal);
		
		if (path == null)
			return false;
		
		clientConnectionHandler.sendPacket(new CTS_GOING_TO(new Point(goal)));
		
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
		
		this.robotPosition = new StartPosition(this);
		
		possiblePoints = getPotentialsPoints();
	}
    
    @Override
    public void work ()
    {
    	if (!hidden)
    	{
	    	String id = String.valueOf(getName().charAt(getName().length() - 1));
	    	
			try 
			{
				step((5 + SharedVariables.getTimeStep())*Integer.valueOf(id)*1000);
			}
			catch (NumberFormatException e)
			{
				e.printStackTrace();
			}
			
	    	hide();
    	}
    	else
    		step(100 * 1000);
    }

	@Override
	public void onCtsObstacleInMapReceived(Point point)
	{
		/*
		 * DO NOTHING
		 */
	}

	@Override
	public void onCtsGoingToReceived(Point point)
	{
		possiblePoints.remove(point);
	}

	@Override
	public void onPosizioneIncrementata()
	{
		/*
		 * DO NOTHING
		 */		
	}

	@Override
	public void onCtsNewGuardiaPosReceived(Point before, Point after)
	{
		/*
		 * DO NOTHING
		 */
	}

	@Override
	public void onCtsGoalChangedReceived(Point old, Point New)
	{
		/*
		 * DO NOTHING
		 */		
	}

	@Override
	public void onCtsLadroFound(Point punto)
	{
		/*
		 * DO NOTHING
		 */		
	}

	@Override
	public void onStcStartGuardie() 
	{
		/*
		 * DO NOTHING
		 */	
	}
}
