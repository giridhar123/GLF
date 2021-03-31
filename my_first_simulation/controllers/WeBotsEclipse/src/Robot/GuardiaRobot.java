package Robot;

import java.util.ArrayList;
import java.util.Random;

import com.cyberbotics.webots.controller.Camera;
import com.cyberbotics.webots.controller.CameraRecognitionObject;
import General.AStarSearcher;
import General.SharedVariables;
import Map.Mappa;
import Map.Point;
import Map.StartPosition;
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
	private boolean ready;
	private Point oldPosition;
	
	private int numeroGuardia;
	private Camera camera;
	
	private Point goal;
	private ArrayList<Integer> correctedPath;
	
	private SireneThread sireneThread;
	private AStarSearcher aStarSearcher;

	public GuardiaRobot(int direction)
	{
		super(direction);
		
		openSet = new ArrayList<>();
		ladriFound = 0;
		ready = false;
		
		numeroGuardia = Integer.valueOf(String.valueOf(getName().charAt(getName().length() - 1)));
		
		camera = new Camera("camera");
		camera.enable(SharedVariables.getTimeStep());
		
		goal = null;
		correctedPath = null;
		
		sireneThread = new SireneThread(this);
		sireneThread.start();
		
		clientConnectionHandler = new ClientConnectionHandler(CTS_PEER_INFO.GUARDIA, this);
		
	}
	
	@Override
	public void connectToServer()
	{
		clientConnectionHandler.start();
	}
	
	private void explore()
	{
		if (mappa == null || ladriFound == SharedVariables.getNumeroLadri())
			return;
		
		openSet.remove(new Point(robotPosition));
		
		Random r = new Random();
		
		while (openSet.size() > 0)
		{	
			goal = openSet.get(r.nextInt(openSet.size()));
			if (!updatePath(goal))
				continue;
			
			clientConnectionHandler.sendPacket(new CTS_GOING_TO(new Point(goal)));
			
			for (int i = 0; i < correctedPath.size(); ++i)
			{
				/*
				 * Se c'è un'altra guardia nella "5-adiacenza" (3 celle di fronte e le 2 laterali)
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
				openSet.remove(new Point(robotPosition));
				clientConnectionHandler.sendPacket(new CTS_GOING_TO(new Point(robotPosition)));
				checkLadro();
				checkLateral();
				
				
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
			
				if (obstaclesInFront)
				{		
					putObstaclesInFront();
					
					//Cerco un nuovo path per lo stesso punto
					i = updatePath(goal) ? -1 : correctedPath.size();
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
	
	/*
	 * Aggiunge un ostacolo nella mappa e invia un pacchetto per segnalarlo alle altre guardie
	 */
	
	private void updateMapAndSendPacket(Point punto)
	{
		if (mappa.get(punto) != Mappa.EMPTY)
			return;
		
		mappa.setValue(punto, 1);
		clientConnectionHandler.sendPacket(new CTS_OBSTACLE_IN_MAP(new Point(new Point(punto))));
	}

	/*
	 * Tramite la telecamera, verifica se è inquadrato un ladro
	 */
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
	
	/* 
	 * Trasforma un path di punti in un path di direzioni,
	 * se non c'è un path segnala il goal come irragiungibile
	 */
	
	private boolean updatePath(Point goal)
	{
		ArrayList<Point> path = aStarSearcher.getPath(robotPosition, goal);
		
		if (path == null)
		{
			updateMapAndSendPacket(goal);
			openSet.remove(new Point(goal));
			return false;
		}
		
		correctedPath = AStarSearcher.pathToRobotDirections(path);
		return true;
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
		ArrayList<Point> path = null;
		for (int i = 0; i < 5; ++i)
		{
			if (mappa.get(punti[i]) == Mappa.GUARDIA)
			{	
				path = aStarSearcher.getPath(robotPosition, punti[i]);
				if (path != null && path.size() <= 3)
				{
					guardiaFound = true;
					break;
				}
			}
		}
		
		if (guardiaFound)
		{
			Point temp = null;
			Random r = new Random();
			
			do
			{
				int count = 0;
				do 
				{
					temp = openSet.get(r.nextInt(openSet.size()));
					count++;
					if(count == (openSet.size() / 2))
					{
						System.out.println(getName() + ": Vabbe' meglio se aspetto!");
						//Wait 7,5 secs
						step(7500);
						return false;
					}
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
		if (!ready)
		{
			step(25 * 1000);
			return;
		}

		//La prima guardia parte 10 secondi dopo che parte l'ultimo ladro 
		step(numeroGuardia * SharedVariables.getTimeStep() * 1000);
		
		explore();
	}
	
	@Override
	public void onStcSendMapReceived(Mappa mappa)
	{
		this.mappa = new Mappa(mappa.getxAmpiezzaSpawn(), mappa.getXDimInterna(), mappa.getYDimInterna(), mappa.getDimSpawnGate());
		
		this.robotPosition = new StartPosition(this);
		
		oldPosition = new Point(robotPosition);
		aStarSearcher = new AStarSearcher(mappa);
		
		//Aggiungo tutta la mappa nell'openset
		for (int i = this.mappa.getxAmpiezzaSpawn(); i < this.mappa.getxAmpiezzaSpawn() + this.mappa.getXDimInterna() - 1; i++)
			for (int j = 0; j < this.mappa.getYSize() - 1; ++j)
				openSet.add(new Point(i, j));
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

	@Override
	public void onStcStartGuardie() 
	{
		ready = true;
	}
}
