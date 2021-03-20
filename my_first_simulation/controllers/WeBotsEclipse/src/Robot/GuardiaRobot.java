package Robot;

import java.util.ArrayList;
import java.util.Random;

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
		
		/*
		FRONTAL_OBSTACLE_TRESHOLD = 75;
		frontalSensors.setTreshold(FRONTAL_OBSTACLE_TRESHOLD);
		*/
		
		clientConnectionHandler = new ClientConnectionHandler(CTS_PEER_INFO.GUARDIA, this);
	}
	
	@Override
	public void connectToServer()
	{
		clientConnectionHandler.start();
	}
	
	@Override
	public void onStcSendMapReceived(Mappa mappa)
	{
		//System.out.println("Guardia: Mappa ricevuta");
		this.mappa = new Mappa(mappa.getXSize(), mappa.getYSize());
		
		for (int i = 1; i < mappa.getXSize()-1; i++)
		{
			for (int j = 1; j < mappa.getYSize()-1; ++j)
			{
				openSet.add(new Point(i, j));
			}
		}
	}
	
	public void explore()
	{
		if (mappa == null || ladroFound)
			return;
	
		this.robotPosition = new Point(4,4);
		closedSet.add(new Point(robotPosition));
		
		Point goal = null;
		AStarSearcher aStarSearcher = new AStarSearcher(mappa);
		ArrayList<Point> path = null;
		ArrayList<Integer> correctedPath = null;
		Random r = new Random();
		
		while (openSet.size() != closedSet.size())
		{	
			//CameraCheck();
			//SirenOn();
			
			goal = openSet.get(r.nextInt(openSet.size()));
			
			if (closedSet.contains(goal))
			{
				System.out.println(goal + " già esaminato");
				continue;
			}
			
			path = aStarSearcher.getPath(robotPosition, goal);
			if (path == null)
			{
				System.out.println("Non c'è un path per " + goal);
				updateMapAndSendPacket(goal);
				closedSet.add(new Point(goal));
				continue;
			}
			
			System.out.println("C'è un path per " + goal);
			
			correctedPath = AStarSearcher.pathToRobotDirections(path);
			for (int i = 0; i < correctedPath.size(); ++i)
			{
				//CameraCheck();
				//SirenOn();
				int value = correctedPath.get(i);
				
				checkLateral();
				changeDirectionTo(value);
				checkLateral();
				
				boolean obstaclesInFront = !goStraightOn();
				closedSet.add(new Point(robotPosition));
				checkLateral();
			
				if (obstaclesInFront)
				{		
					putObstaclesInFront();
					
					//Cerco un nuovo path per lo stesso punto
					path = aStarSearcher.getPath(robotPosition, goal);
					if (path == null)
					{
						System.out.println("Non ho più un path per " + goal);
						updateMapAndSendPacket(goal);
						closedSet.add(new Point(goal));
						i = correctedPath.size();
					}			
					else
					{
						System.out.println("Aggiorno il path per " + goal);
						correctedPath = AStarSearcher.pathToRobotDirections(path);
						i = -1;
					}
				}
				System.out.println(mappa);
			}
		}
		
		ladroFound = true;
	}
	
	private void putObstaclesInFront()
	{	
		/*		
		if (frontalSensors.getLeftValue() < FRONTAL_OBSTACLE_TRESHOLD && frontalSensors.getRightValue() < FRONTAL_OBSTACLE_TRESHOLD)
			return false;
		*/
		
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
	}
	
	private void checkLateral()
	{
		int x = robotPosition.getX();
		int y = robotPosition.getY();
		Point punto = null;
		
		if (lateralSensors.getLeftValue() > 85)
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
		if (lateralSensors.getRightValue() > 85)
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
	{if (mappa.get(punto) == 1)
			return;
		
		//System.out.println("1");
		mappa.setValue(punto, 1);
		//System.out.println("2 Guardia invio pacchetto con punto: " + punto);
		clientConnectionHandler.sendPacket(new CTS_UPDATE_MAP_POINT(punto));
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
