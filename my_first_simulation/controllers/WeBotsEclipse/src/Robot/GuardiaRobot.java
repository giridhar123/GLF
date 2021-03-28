package Robot;

import java.util.ArrayList;
import java.util.Random;

import com.cyberbotics.webots.controller.Camera;
import com.cyberbotics.webots.controller.CameraRecognitionObject;
import General.AStarSearcher;
import General.SharedVariables;
import Map.Mappa;
import Map.Point;
import Network.Client;
import Network.ClientConnectionHandler;
import Network.Packets.ClientToServer.CTS_PEER_INFO;
import Network.Packets.ClientToServer.CTS_GOAL_CHANGED;
import Network.Packets.ClientToServer.CTS_GOING_TO;
import Network.Packets.ClientToServer.CTS_LADRO_FOUND;
import Network.Packets.ClientToServer.CTS_NEW_GUARDIA_POS;
import Network.Packets.ClientToServer.CTS_OBSTACLE_IN_MAP;

public class GuardiaRobot extends GenericRobot implements Client {
	
	private ClientConnectionHandler clientConnectionHandler;
	private ArrayList<Point> openSet;
	private int ladriFound;
	private Point oldPosition;
	
	private int numeroGuardia;
	private Camera camera;
	
	private Point goal;
	private ArrayList<Integer> correctedPath;
	
	private SireneThread sireneThread;

	public GuardiaRobot(int direction)
	{
		super(direction);
		
		openSet = new ArrayList<>();
		ladriFound = 0;
		
		numeroGuardia = Integer.valueOf(String.valueOf(getName().charAt(getName().length() - 1)));
		
		camera = new Camera("camera");
		camera.enable(SharedVariables.getTimeStep());
		
		goal = null;
		correctedPath = null;
		
		sireneThread = new SireneThread(this);
		sireneThread.start();
		
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
		this.mappa = new Mappa(mappa.getxAmpiezzaSpawn(), mappa.getXDimInterna(), mappa.getYDimInterna(), mappa.getDimSpawnGate());
		
		//Aggiungo tutta la mappa nell'openset
		for (int i = this.mappa.getxAmpiezzaSpawn(); i < this.mappa.getxAmpiezzaSpawn() + this.mappa.getXDimInterna() - 1; i++)
			for (int j = 0; j < this.mappa.getYSize() - 1; ++j)
				openSet.add(new Point(i, j));
	}
	
	private void explore()
	{
		if (mappa == null || ladriFound == SharedVariables.getNumeroLadri())
			return;
		
		openSet.remove(new Point(robotPosition));
		
		AStarSearcher aStarSearcher = new AStarSearcher(mappa);
		ArrayList<Point> path = null;
		Random r = new Random();
		
		while (openSet.size() > 0)
		{	
			goal = openSet.get(r.nextInt(openSet.size()));
			path = aStarSearcher.getPath(robotPosition, goal);
			
			if (path == null)
			{
				updateMapAndSendPacket(goal);
				openSet.remove(new Point(goal));
				continue;
			}
			
			clientConnectionHandler.sendPacket(new CTS_GOING_TO(new Point(goal)));
			
			correctedPath = AStarSearcher.pathToRobotDirections(path);
			for (int i = 0; i < correctedPath.size(); ++i)
			{
				/*
				 * Se c'è un'altra guardia nella 5-adiacenza (3 celle di fronte e le 2 laterali)
				 * Cerco un nuovo path nella direzione opposta del robot
				 */
				
				if(check5Neighbours())
					i = 0;
				
				int value = correctedPath.get(i);
				
				checkLateral();
				changeDirectionTo(value);
				checkLateral();
				
				checkLadro(); // dobbiamo mettere una condizione per la quale se trova il ladro di fare qualcosa.
				boolean obstaclesInFront = !goStraightOn();
				checkLadro();
				
				if(check5Neighbours())
				{
					i = -1;
					continue;
				}
				
				if (ladriFound == SharedVariables.getNumeroLadri())
				{
					System.out.println(mappa);
					return;
				}
				
				openSet.remove(new Point(robotPosition));
				clientConnectionHandler.sendPacket(new CTS_GOING_TO(new Point(robotPosition)));
				checkLateral();
			
				if (obstaclesInFront)
				{		
					putObstaclesInFront();
					
					//Cerco un nuovo path per lo stesso punto
					path = aStarSearcher.getPath(robotPosition, goal);
					if (path == null)
					{
						updateMapAndSendPacket(goal);
						openSet.remove(new Point(goal));
						i = correctedPath.size();
					}			
					else
					{
						correctedPath = AStarSearcher.pathToRobotDirections(path);
						i = -1;
					}
				}
				}
		}

		System.out.println(mappa);
	}
	
	private void putObstaclesInFront()
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
	{
		if (mappa.get(punto) == Mappa.FULL ||
			mappa.get(punto) == Mappa.GUARDIA ||
			mappa.get(punto) == Mappa.LADRO)
			return;
		
		mappa.setValue(punto, 1);
		clientConnectionHandler.sendPacket(new CTS_OBSTACLE_IN_MAP(new Point(new Point(punto))));
	}

	private boolean checkLadro()
	{
        camera.recognitionEnable(SharedVariables.getTimeStep());
        step();
        CameraRecognitionObject[] CCC = camera.getCameraRecognitionObjects();
        
        boolean ladroFound = false;
        Point punto = null;
        
        for (int i = 0; i < camera.getRecognitionNumberOfObjects(); ++i)
        {
        	if(CCC[i].getModel().equalsIgnoreCase("ladro"))
        	{   
        		
    			int distanzaLaterale = (int) Math.round(CCC[i].getPosition()[0] * 10);
    			distanzaLaterale = Math.abs(distanzaLaterale);
        		int distanzaFrontale = (int) Math.round(CCC[i].getPosition()[2] * 10);
        		distanzaFrontale = Math.abs(distanzaFrontale);
        		
        		if(CCC[i].getPosition()[0] <= -0.01)
	    		{
        			//Sinistra
        			ladroFound = true;
        			switch (direction)
        			{
        			case NORD:
        				punto = new Point(robotPosition.getX() - distanzaFrontale, robotPosition.getY() - distanzaLaterale);
        				break;
        			case SUD:
        				punto = new Point(robotPosition.getX() + distanzaFrontale, robotPosition.getY() + distanzaLaterale);
        				break;
        			case EST:
        				punto = new Point(robotPosition.getX() - distanzaLaterale, robotPosition.getY() + distanzaFrontale);
        				break;
        			case OVEST:
        				punto = new Point(robotPosition.getX() + distanzaLaterale, robotPosition.getY() - distanzaFrontale);
        				break;
        			}
	    		}
	    		else if(CCC[i].getPosition()[0] >= 0.01)
	    		{
	    			//Destra
	    			ladroFound = true;
	    			switch (direction)
        			{
        			case NORD:
        				punto = new Point(robotPosition.getX() - distanzaFrontale, robotPosition.getY() + distanzaLaterale);
        				break;
        			case SUD:
        				punto = new Point(robotPosition.getX() + distanzaFrontale, robotPosition.getY() - distanzaLaterale);
        				break;
        			case EST:
        				punto = new Point(robotPosition.getX() + distanzaLaterale, robotPosition.getY() + distanzaFrontale);
        				break;
        			case OVEST:
        				punto = new Point(robotPosition.getX() - distanzaLaterale, robotPosition.getY() - distanzaFrontale);
        				break;
        			}
	    		}
	    		else 
	    		{
	    			//Di fronte
	    			ladroFound = true;
	    			switch (direction)
        			{
        			case NORD:
        				punto = new Point(robotPosition.getX() - distanzaFrontale, robotPosition.getY());
        				break;
        			case SUD:
        				punto = new Point(robotPosition.getX() + distanzaFrontale, robotPosition.getY());
        				break;
        			case EST:
        				punto = new Point(robotPosition.getX(), robotPosition.getY() + distanzaFrontale);
        				break;
        			case OVEST:
        				punto = new Point(robotPosition.getX(), robotPosition.getY() - distanzaFrontale);
        				break;
        			}
	    		}
        	}
        }
        
        camera.recognitionDisable();
        
        if (ladroFound)
        {        	
        	if (mappa.get(punto) != Mappa.LADRO)
        	{
            	this.ladriFound += 1;
            	mappa.setValue(punto, Mappa.LADRO);
            	clientConnectionHandler.sendPacket(new CTS_LADRO_FOUND(new Point(punto)));
        	}
        }
        
        return ladroFound;
	}
	
	private boolean check5Neighbours() 
	{
		Point punti[] = new Point[5];
		switch (direction) 
		{
		case NORD:
			punti[0] = new Point(robotPosition.getX(), robotPosition.getY() + 1);
			punti[1] = new Point(robotPosition.getX() - 1, robotPosition.getY());
			punti[2] = new Point(robotPosition.getX(), robotPosition.getY() - 1);
			punti[3] = new Point(robotPosition.getX() - 1, robotPosition.getY() - 1);
			punti[4] = new Point(robotPosition.getX() - 1, robotPosition.getY() + 1);
			break;		
		case EST:
			punti[0] = new Point(robotPosition.getX() + 1, robotPosition.getY());
			punti[1] = new Point(robotPosition.getX(), robotPosition.getY() + 1);
			punti[2] = new Point(robotPosition.getX() - 1, robotPosition.getY());
			punti[3] = new Point(robotPosition.getX() + 1, robotPosition.getY() + 1);
			punti[4] = new Point(robotPosition.getX() - 1, robotPosition.getY() + 1);
			break;		
		case SUD:
			punti[0] = new Point(robotPosition.getX() + 1, robotPosition.getY());
			punti[1] = new Point(robotPosition.getX(), robotPosition.getY() + 1);
			punti[2] = new Point(robotPosition.getX(), robotPosition.getY() - 1);
			punti[3] = new Point(robotPosition.getX() + 1, robotPosition.getY() + 1);
			punti[4] = new Point(robotPosition.getX() + 1, robotPosition.getY() - 1);
			break;		
		case OVEST: 
			punti[0] = new Point(robotPosition.getX() + 1, robotPosition.getY());
			punti[1] = new Point(robotPosition.getX() - 1, robotPosition.getY());
			punti[2] = new Point(robotPosition.getX(), robotPosition.getY() - 1);
			punti[3] = new Point(robotPosition.getX() - 1, robotPosition.getY() - 1);
			punti[4] = new Point(robotPosition.getX() + 1, robotPosition.getY() - 1);
			break;
		}
		
		boolean guardiaFound = false;
		for (int i = 0; i < 5; ++i)
		{
			if (mappa.get(punti[i]) == Mappa.GUARDIA)
				guardiaFound = true;
		}
		
		if (guardiaFound)
		{
			Point temp = null;
			Random r = new Random();
			ArrayList<Point> path = null;
			AStarSearcher aStarSearcher = new AStarSearcher(mappa);
			
			do
			{
				do 
				{
					temp = openSet.get(r.nextInt(openSet.size()));
				}
				while(!isOpposite(temp));

				step(3500 * numeroGuardia);
				path = aStarSearcher.getPath(robotPosition, temp);
				
				if (path == null)
				{
					updateMapAndSendPacket(temp);
					openSet.remove(new Point(temp));
				}
			}
			while(path == null);
			clientConnectionHandler.sendPacket(new CTS_GOAL_CHANGED(new Point(goal), new Point(temp)));
			openSet.add(new Point(goal));
			goal = new Point(temp);
			correctedPath = AStarSearcher.pathToRobotDirections(path);
		}
		
		return guardiaFound;
	}

	private boolean isOpposite(Point temp)
	{
		switch (direction) 
		{
		case NORD:
			return temp.getX() > robotPosition.getX();
		case EST:
			return temp.getY() < robotPosition.getY();
		case SUD:
			return temp.getX() < robotPosition.getX();
		case OVEST:
			return temp.getY() > robotPosition.getY();
		default:
			return false;
		}
	}
	
	@Override
	public void work () 
	{		
		try 
		{
			//La prima guardia parte 10 secondi dopo che parte l'ultimo ladro 
			step(10000 + ((SharedVariables.getNumeroLadri() + numeroGuardia)* SharedVariables.getTimeStep() * 1000));
		}
		catch (NumberFormatException e) 
		{
			e.printStackTrace();
		}
		
		explore();
	}

	@Override
	public void onCtsObstacleInMapReceived(Point point)
	{
		mappa.setValue(point, Mappa.FULL);
		openSet.remove(point);
	}

	@Override
	public void onCtsGoingToReceived(Point point)
	{
		openSet.remove(point);		
	}

	@Override
	public void onPosizioneIncrementata()
	{
		clientConnectionHandler.sendPacket(new CTS_NEW_GUARDIA_POS(new Point(oldPosition), new Point(robotPosition)));
		oldPosition = new Point(robotPosition);
	}

	@Override
	public void onCtsNewGuardiaPosReceived(Point before, Point after)
	{
		mappa.setValue(before, Mappa.EMPTY);
		mappa.setValue(after, Mappa.GUARDIA);		
	}

	@Override
	public void onCtsGoalChangedReceived(Point old, Point New)
	{
		openSet.add(new Point(old));
		openSet.remove(new Point(New));
	}

	@Override
	public void onCtsLadroFound(Point punto)
	{
		if (mappa.get(punto) != Mappa.LADRO)
		{
			++ladriFound;
			mappa.setValue(punto, Mappa.LADRO);
		}
	}
}
