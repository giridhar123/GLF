package Robot;

import java.util.ArrayList;

import com.cyberbotics.webots.controller.Camera;
import com.cyberbotics.webots.controller.CameraRecognitionObject;
import com.cyberbotics.webots.controller.LED;

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
		//System.out.println("Guardia: Mappa ricevuta");
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
			CameraCheck();
			SirenOn();
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
				updateMapAndSendPacket(goal);
				closedSet.add(new Point(goal));
				continue;
			}
			
			System.out.println("C'è un path per " + goal);
			
			correctedPath = AStarSearcher.pathToRobotDirections(path);
			for (int i = 0; i < correctedPath.size(); ++i)
			{
				CameraCheck();
				SirenOn();
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
					
					updateMapAndSendPacket(new Point(row, col));
					
					//Cerco un nuovo path per lo stesso punto
					aStarSearcher = new AStarSearcher(mappa);
					path = aStarSearcher.getPath(robotPosition, goal);
					if (path == null)
					{
						//System.out.println("Non ho più un path per " + goal);
						updateMapAndSendPacket(goal);
						closedSet.add(new Point(goal));
						i = correctedPath.size();
					}			
					else
					{
						//System.out.println("Aggiorno il path per " + goal);
						correctedPath = AStarSearcher.pathToRobotDirections(path);
						i = -1;
					}
				}
				System.out.println(mappa);
			}
		}
		
		ladroFound = true;
	}
	
	private boolean obstaclesInFront()
	{			
		if (frontalSensors.getLeftValue() < FRONTAL_OBSTACLE_TRESHOLD && frontalSensors.getRightValue() < FRONTAL_OBSTACLE_TRESHOLD)
			return false;
		
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
		
		//System.out.println("HO visto un coso davanti");
		updateMapAndSendPacket(punto);
		
		return true;
	}
	
	private void checkLateral()
	{
		int x = robotPosition.getX();
		int y = robotPosition.getY();
		Point punto = null;
		
		if (leftSensor.getValue() > 85)
		{
			switch(direction)
		    {
		    case NORD:
		    	punto = new Point(x, y - 1);
		    	break;
		    case EST:
		    	punto = new Point(x - 1, y);
		    	break;
		    case SUD:
		    	punto = new Point(x, y + 1);
		    	break;
		    case OVEST:
		    	punto = new Point(x + 1, y);
		    	break;
		    }
			
			updateMapAndSendPacket(punto);
		}
		if (rightSensor.getValue() > 85)
		{
			switch(direction)
		    {
		    case NORD:
		    	punto = new Point(x, y + 1);
		    	break;
		    case EST:
		    	punto = new Point(x + 1, y);
		    	break;
		    case SUD:
		    	punto = new Point(x, y - 1);
		    	break;
		    case OVEST:
		    	punto = new Point(x - 1, y);
		    	break;
		    }
			
			updateMapAndSendPacket(punto);
		}
	}
	
	private void updateMapAndSendPacket(Point punto)
	{
		if(mappa.get(punto.getX(), punto.getY()) == 1) return;
		
		//System.out.println("1");
		mappa.setValue(punto.getX(), punto.getY(), 1);
		//System.out.println("2 : " + punto.getX() + " " + punto.getY());
		clientConnectionHandler.sendPacket(new CTS_UPDATE_MAP_POINT(punto.getX(), punto.getY()));
		//System.out.println("3");
	}

	private void CameraCheck()
	{
		Camera camera = new Camera("camera");    
        camera.enable(150);
        camera.recognitionEnable(150);
        CameraRecognitionObject[] CCC = camera.getCameraRecognitionObjects();
        
        for (int i = 0; i < camera.getRecognitionNumberOfObjects(); ++i)
        {
        	System.out.println(CCC[i].getModel());  // Wooden box oppure Ladro
        }   
	}
	
	private void SirenOn()
	{
		LED led0 = getLED("led_0"); // ignoto lol
        LED led1 = getLED("led_1"); //rosso
        LED led2 = getLED("led_2"); //blu
        
        if(led1.get() == 0) // se rosso è spento
        {
        	led1.set(255);
        	led2.set(0); // accendi rosso
        }
        else
        {
        	led1.set(0);
        	led2.set(255); // accendi rosso
        }        
	}

}
