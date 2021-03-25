package Robot;

import java.util.ArrayList;
import java.util.Random;

import com.cyberbotics.webots.controller.Camera;
import com.cyberbotics.webots.controller.CameraRecognitionObject;
import com.cyberbotics.webots.controller.LED;

import General.AStarSearcher;
import General.SharedVariables;
import Map.Mappa;
import Map.Point;
import Network.Client;
import Network.ClientConnectionHandler;
import Network.Packets.ClientToServer.CTS_PEER_INFO;
import Network.Packets.ClientToServer.CTS_GOAL_CHANGED;
import Network.Packets.ClientToServer.CTS_GOING_TO;
import Network.Packets.ClientToServer.CTS_NEW_GUARDIA_POS;
import Network.Packets.ClientToServer.CTS_OBSTACLE_IN_MAP;

public class GuardiaRobot extends GenericRobot implements Client {
	
	private ClientConnectionHandler clientConnectionHandler;
	private ArrayList<Point> openSet;
	private boolean ladroFound;
	private Point oldPosition;

	public GuardiaRobot(int direction)
	{
		super(direction);
		
		openSet = new ArrayList<>();
		ladroFound = false;
		
		/*
		FRONTAL_OBSTACLE_TRESHOLD = 75;-
		frontalSensors.setTreshold(FRONTAL_OBSTACLE_TRESHOLD);
		*/
		
		clientConnectionHandler = new ClientConnectionHandler(CTS_PEER_INFO.GUARDIA, this);
		
		oldPosition = new Point(robotPosition);
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
		this.mappa = new Mappa(mappa.getxAmpiezzaSpawn(), mappa.getXDimInterna(), mappa.getYDimInterna(), mappa.getDimSpawnGate());
		
		//Aggiungo tutta la mappa nell'openset
		for (int i = this.mappa.getxAmpiezzaSpawn(); i < this.mappa.getxAmpiezzaSpawn() + this.mappa.getXDimInterna() - 1; i++)
			for (int j = 0; j < this.mappa.getYSize() - 1; ++j)
				openSet.add(new Point(i, j));
		
		System.out.println(getName() + ": size is:" + openSet.size());
	}
	
	private void explore()
	{
		if (mappa == null || ladroFound)
			return;
		
		openSet.remove(new Point(robotPosition));
		
		Point goal = null;
		AStarSearcher aStarSearcher = new AStarSearcher(mappa);
		ArrayList<Point> path = null;
		ArrayList<Integer> correctedPath = null;
		Random r = new Random();
		
		while (openSet.size() > 0)
		{	
			//CameraCheck();
			//SirenOn();
			
			goal = openSet.get(r.nextInt(openSet.size()));
			path = aStarSearcher.getPath(robotPosition, goal);
			
			if (path == null)
			{
				//System.out.println(getName() + ":Non c'e un path per " + goal);
				updateMapAndSendPacket(goal);
				openSet.remove(new Point(goal));
				continue;
			}
			
			//System.out.println(getName() + ": C'e un path per " + goal);
			clientConnectionHandler.sendPacket(new CTS_GOING_TO(goal));
			
			correctedPath = AStarSearcher.pathToRobotDirections(path);
			for (int i = 0; i < correctedPath.size(); ++i)
			{
				
				if(check8Neighbours())
				{
					Point temp = null;
					do
					{
						do 
						{
							temp = openSet.get(r.nextInt(openSet.size()));
						}
						while(!isOpposite(temp));
		
						path = aStarSearcher.getPath(robotPosition, temp);
						
						if (path == null)
						{
							updateMapAndSendPacket(temp);
							openSet.remove(new Point(temp));
						}
					}
					while(path == null);
					System.out.println(getName() + ": Guardia vicina trovata, cambio path da " + goal + " a " + temp);
					clientConnectionHandler.sendPacket(new CTS_GOAL_CHANGED(goal, temp));
					openSet.add(new Point(goal));
					goal = new Point(temp);
					correctedPath = AStarSearcher.pathToRobotDirections(path);
					i = 0;
				}
				
				int value = correctedPath.get(i);
				
				checkLateral();
				changeDirectionTo(value);
				checkLateral();
				
				boolean obstaclesInFront = !goStraightOn();
				openSet.remove(new Point(robotPosition));
				checkLateral();
			
				if (obstaclesInFront)
				{		
					putObstaclesInFront();
					
					//Cerco un nuovo path per lo stesso punto
					path = aStarSearcher.getPath(robotPosition, goal);
					if (path == null)
					{
						//System.out.println(getName() + ": Non ho piu un path per " + goal);
						updateMapAndSendPacket(goal);
						openSet.remove(new Point(goal));
						i = correctedPath.size();
					}			
					else
					{
						//System.out.println(getName() + ": Aggiorno il path per " + goal);
						correctedPath = AStarSearcher.pathToRobotDirections(path);
						i = -1;
					}
				}
				
				ladroFound = CameraCheckTest(); // dobbiamo mettere una condizione per la quale se trova il ladro di fare qualcosa.
				//SirenOn();
			//	System.out.println(getName() + "\n" + mappa);
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
	{if (mappa.get(punto) == 1 || mappa.get(punto) == Mappa.GUARDIA)
			return;
		
		//System.out.println("1");
		mappa.setValue(punto, 1);
		//System.out.println("2 Guardia invio pacchetto con punto: " + punto);
		clientConnectionHandler.sendPacket(new CTS_OBSTACLE_IN_MAP(punto));
		//System.out.println("3");
	}

	private boolean CameraCheckTest()
	{
		//Funzione di test
		Camera camera = new Camera("camera");    
        camera.enable(150);
        camera.recognitionEnable(150);
        CameraRecognitionObject[] CCC = camera.getCameraRecognitionObjects();
        
        for (int i = 0; i < camera.getRecognitionNumberOfObjects(); ++i)
        {
        	if(	CCC[i].getModel().equalsIgnoreCase("ladro") )
        	{
        		//System.out.println("Ho trovato un ladro! � distante "+ CCC[i].getPosition()[0] + CCC[i].getPosition()[1] + CCC[i].getPosition()[2]);
        	}
        	else
        	{
        		//System.out.println("Ho trovato qualcos'altro ! � distante  " + CCC[i].getPosition()[0]);
        		//System.out.println( CCC[i].getPosition()[1] );
        		//System.out.println( CCC[i].getPosition()[2] );
        		//System.out.println( "\n");
        	}
        }  
        
       return false;
	}
	
	private boolean CameraCheck()
	{
		//Questa � la funzione finale una volta capita la condizione.
		Camera camera = new Camera("camera");    
        camera.enable(150);
        camera.recognitionEnable(150);
        CameraRecognitionObject[] CRO = camera.getCameraRecognitionObjects();
        double distance ;
        double limiteRobot = 0.001 ;
        double limiteCassa = 0.001 ;

        for (int i = 0; i < camera.getRecognitionNumberOfObjects(); ++i)
        {
    		distance = CRO[i].getPosition()[0] ; 

        	if(	CRO[i].getModel().equalsIgnoreCase("ladro") )
        	{
        			if(distance < limiteRobot )
        				return true ;
        	}
        	else
        	{
            	if(	CRO[i].getModel().equalsIgnoreCase("wooden box") )
            	{
            		if(distance < limiteCassa )
        			{
        				// Tutte le sistemate che dovete fare
        			}
            	}
        	}
        }
        return false;
	}
	
	private void SirenOn()
	{
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
	
	private boolean check8Neighbours() 
	{
		switch (direction) 
		{
		case NORD: return (mappa.get(new Point(robotPosition.getX(), robotPosition.getY() + 1)) == Mappa.GUARDIA ||
				mappa.get(new Point(robotPosition.getX() - 1, robotPosition.getY())) == Mappa.GUARDIA ||
				mappa.get(new Point(robotPosition.getX(), robotPosition.getY() - 1)) == Mappa.GUARDIA ||
				mappa.get(new Point(robotPosition.getX() - 1, robotPosition.getY() - 1)) == Mappa.GUARDIA ||
				mappa.get(new Point(robotPosition.getX() - 1, robotPosition.getY() + 1)) == Mappa.GUARDIA);
		
		case EST: return (mappa.get(new Point(robotPosition.getX() + 1, robotPosition.getY())) == Mappa.GUARDIA ||
				mappa.get(new Point(robotPosition.getX(), robotPosition.getY() + 1)) == Mappa.GUARDIA ||
				mappa.get(new Point(robotPosition.getX() - 1, robotPosition.getY())) == Mappa.GUARDIA ||
				mappa.get(new Point(robotPosition.getX() + 1, robotPosition.getY() + 1)) == Mappa.GUARDIA ||
				mappa.get(new Point(robotPosition.getX() - 1, robotPosition.getY() + 1)) == Mappa.GUARDIA);
		
		case SUD: return (mappa.get(new Point(robotPosition.getX() + 1, robotPosition.getY())) == Mappa.GUARDIA ||
				mappa.get(new Point(robotPosition.getX(), robotPosition.getY() + 1)) == Mappa.GUARDIA ||
				mappa.get(new Point(robotPosition.getX(), robotPosition.getY() - 1)) == Mappa.GUARDIA ||
				mappa.get(new Point(robotPosition.getX() + 1, robotPosition.getY() + 1)) == Mappa.GUARDIA ||
				mappa.get(new Point(robotPosition.getX() + 1, robotPosition.getY() - 1)) == Mappa.GUARDIA);
		
		case OVEST: return (mappa.get(new Point(robotPosition.getX() + 1, robotPosition.getY())) == Mappa.GUARDIA ||
				mappa.get(new Point(robotPosition.getX() - 1, robotPosition.getY())) == Mappa.GUARDIA ||
				mappa.get(new Point(robotPosition.getX(), robotPosition.getY() - 1)) == Mappa.GUARDIA ||
				mappa.get(new Point(robotPosition.getX() - 1, robotPosition.getY() - 1)) == Mappa.GUARDIA ||
				mappa.get(new Point(robotPosition.getX() + 1, robotPosition.getY() - 1)) == Mappa.GUARDIA);
		}
		
		return false;
		
	}

	private boolean isOpposite(Point temp)
	{
		switch (direction) 
		{
		case NORD: return temp.getX() > robotPosition.getX();
		case EST: return temp.getY() < robotPosition.getY();
		case SUD: return temp.getX() < robotPosition.getX();
		case OVEST: return temp.getY() > robotPosition.getY();
		}
		
		return false;
	}
	
	@Override
	public void work () 
	{
		String id = String.valueOf(getName().charAt(getName().length() - 1));
		
		try 
		{
			//La prima guardia parte 10 secondi dopo che parte l'ultimo ladro 
			step(10000 + ((SharedVariables.getNumeroLadri() + Integer.valueOf(id))* SharedVariables.getTimeStep() * 1000));
		}
		catch (NumberFormatException e) 
		{
			e.printStackTrace();
		}
		
		explore();
	}

	@Override
	public void onCtsObstacleInMapReceived(Point point) {
		//System.out.println(getName() + " ho ricevuto ostacolo in " + point);
		mappa.setValue(point, Mappa.FULL);
		openSet.remove(point);
	}

	@Override
	public void onCtsGoingToReceived(Point point) {
		//System.out.println(getName() + " un'altra guardia vuole andare in " + point);
		openSet.remove(point);		
	}

	@Override
	public void onPosizioneIncrementata() {
		clientConnectionHandler.sendPacket(new CTS_NEW_GUARDIA_POS(oldPosition, robotPosition));
		oldPosition = new Point(robotPosition);
	}

	@Override
	public void onCtsNewGuardiaPosReceived(Point before, Point after) {
		mappa.setValue(before, Mappa.EMPTY);
		mappa.setValue(after, Mappa.GUARDIA);		
	}

	@Override
	public void onCtsGoalChangedReceived(Point old, Point New) {
		openSet.add(new Point(old));
		openSet.remove(new Point(New));
	}
}
