package Robot;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Random;
import java.util.Set;

import General.AStarSearcher;
import Map.Mappa;
import Map.Point;
import Network.Client;
import Network.ClientConnectionHandler;
import Network.Packets.ClientToServer.CTS_PEER_INFO;
import Network.Packets.ClientToServer.CTS_UPDATE_MAP_POINT;

public class GuardiaRobot extends GenericRobot implements Client {
	
	private ClientConnectionHandler clientConnectionHandler;
	private ArrayList<Point> openSet;
	private ArrayList<Point> closedSet;
	private boolean ladroFound;

	public GuardiaRobot(int direction)
	{
		super(direction);
		
		openSet = new ArrayList<>();
		closedSet = new ArrayList<>();
		ladroFound = false;
		
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
		
		for (int i = 0; i < mappa.getXSize(); i++)
		{
			for (int j = 0; j < mappa.getYSize(); ++j)
			{
				openSet.add(new Point(i, j));
			}
		}
	}
	
	public void explore()
	{
		if (mappa == null || ladroFound)
			return;
		
		int row, col;
		row = col = -1;
	
		this.robotPosition = new Point(4,4);
		closedSet.add(new Point(robotPosition));
		
		Point goal = null;
		AStarSearcher aStarSearcher = null;
		ArrayList<Point> path = null;
		ArrayList<Integer> correctedPath = null;
		
		for (int j = 0; j < openSet.size(); ++j)
		{	
			goal = openSet.get(j);
			
			if (closedSet.contains(goal))
			{
				//System.out.println(goal + " già esaminato");
				continue;
			}
			
			aStarSearcher = new AStarSearcher(mappa);
			path = aStarSearcher.getPath(robotPosition, goal);
			if (path == null)
			{
				//System.out.println("Non c'è un path per " + goal);
				mappa.setValue(goal.getX(), goal.getY(), 1);
				clientConnectionHandler.sendPacket(new CTS_UPDATE_MAP_POINT(goal));
				closedSet.add(new Point(goal));
				continue;
			}
			
			//System.out.println("C'è un path per " + goal);
			
			path.add(0, new Point(robotPosition));
			correctedPath = convertToDirections(path);
			for (int i = 0; i < correctedPath.size(); ++i)
			{
				int value = correctedPath.get(i);
				
				checkLateral();
				changeDirectionTo(value);
				checkLateral();
				
				goStraightOn();
				closedSet.add(new Point(robotPosition));
				checkLateral();
			
				if (obstaclesInFront())
				{	
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
			    	default:
			    		break;
				    }
					
					mappa.setValue(row, col, 1);
					clientConnectionHandler.sendPacket(new CTS_UPDATE_MAP_POINT(goal));
					
					//Cerco un nuovo path per lo stesso punto
					aStarSearcher = new AStarSearcher(mappa);
					path = aStarSearcher.getPath(robotPosition, goal);
					if (path == null)
					{
						//System.out.println("Non ho più un path per " + goal);
						mappa.setValue(goal.getX(), goal.getY(), 1);
						clientConnectionHandler.sendPacket(new CTS_UPDATE_MAP_POINT(goal));
						closedSet.add(new Point(goal));
						i = correctedPath.size();
					}			
					else
					{
						//System.out.println("Aggiorno il path per " + goal);
						path.add(0, new Point(robotPosition));
						correctedPath = convertToDirections(path);
						i = -1;
					}
				}
				//System.out.println(mappa);
			}
		}
		
		ladroFound = true;
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
	
	private boolean obstaclesInFront()
	{
		
		Point punto = null;
		switch(direction)
		{
		case NORD:
	    	punto = new Point(robotPosition.getX() - 1, robotPosition.getY());
	    	break;
	    case EST:
	    	punto = new Point(robotPosition.getX(), robotPosition.getY() + 1);
	    	break;
	    case SUD:
	    	punto = new Point(robotPosition.getX() + 1, robotPosition.getY());
	    	break;
	    case OVEST:
	    	punto = new Point(robotPosition.getX(), robotPosition.getY() - 1);
	    	break;
		}
		
		/*
		if (mappa.get(punto.getX(), punto.getY()) == 1)
			return true;
		else
		{
			
			*/
			if (frontalSensors.getLeftValue() > 80 && frontalSensors.getRightValue() > 80)
			{
				//System.out.println("HO visto un coso davanti");
				mappa.setValue(punto.getX(), punto.getY(), 1);
				clientConnectionHandler.sendPacket(new CTS_UPDATE_MAP_POINT(punto));
				return true;
			}
			return false;
		//}		
	}
	
	private void checkLateral()
	{
		int row, col;
		row = col = -1;
		
		if (leftSensor.getValue() > 85)
		{
			switch(direction)
		    {
		    case NORD:
		    	row = robotPosition.getX();
		    	col = robotPosition.getY() - 1;
		    	break;
		    case EST:
		    	row = robotPosition.getX() - 1;
		    	col = robotPosition.getY();
		    	break;
		    case SUD:
		    	row = robotPosition.getX();
		    	col = robotPosition.getY() + 1;
		    	break;
		    case OVEST:
		    	row = robotPosition.getX() + 1;
		    	col = robotPosition.getY();
		    	break;
		    }
			
			mappa.setValue(row, col, 1);
			clientConnectionHandler.sendPacket(new CTS_UPDATE_MAP_POINT(row, col));
		}
		if (rightSensor.getValue() > 85)
		{
			switch(direction)
		    {
		    case NORD:
		    	row = robotPosition.getX();
		    	col = robotPosition.getY() + 1;
		    	break;
		    case EST:
		    	row = robotPosition.getX() + 1;
		    	col = robotPosition.getY();
		    	break;
		    case SUD:
		    	row = robotPosition.getX();
		    	col = robotPosition.getY() - 1;
		    	break;
		    case OVEST:
		    	row = robotPosition.getX() - 1;
		    	col = robotPosition.getY();
		    	break;
		    }
			
			mappa.setValue(row, col, 1);
			clientConnectionHandler.sendPacket(new CTS_UPDATE_MAP_POINT(row, col));
		}
	}
}
