package Robot;

import com.cyberbotics.webots.controller.Field;
import com.cyberbotics.webots.controller.Node;
import com.cyberbotics.webots.controller.Supervisor;

import General.SharedVariables;
import Map.StartPosition;
import Map.Mappa;
import Map.Point;
import Network.Client;
import Network.ClientConnectionHandler;
import Network.Packets.ClientToServer.CTS_PEER_INFO;
import Network.Packets.ClientToServer.CTS_WORLD_READY;

/*
 * Classe per la gestione, lo spawn e varie cose tra webots ed eclipse.
 */
public class SupervisorRobot extends Supervisor implements Client {
	
	private ClientConnectionHandler clientConnectionHandler;
	private Mappa mappa;
	private boolean worldSpawned;
	
	public SupervisorRobot()
	{
		clientConnectionHandler = new ClientConnectionHandler(CTS_PEER_INFO.SUPERVISOR, this);
		this.mappa = null;
		this.worldSpawned = false;
	}
	
	@Override
	public void connectToServer()
	{
		clientConnectionHandler.start();
	}
		
	/*
	 * Metodo che spawna le casse e i robot.
	 * Fondamentalmente si prendono le variabili tramite le funzioni della libreria di webots.
	 * Si prende il nodo supervisor e si possono utilizzare le funzioni "speciali" definite nella documentazione come lo spawn di oggetti etc.
	 */
	public void spawnWorld()
	{
		if (mappa == null || worldSpawned)
			return;
		
        final Node viewpoint_node = getFromDef("viewpoint");
        Node robot_node = getFromDef("supervisor");
        robot_node.setVisibility(viewpoint_node, false);
        
        final Node Floor = getFromDef("World");
    	Field pavimento = Floor.getField("floorSize");
    	pavimento.setSFVec2f(mappa.getFloorSize());

    	final Node RootNode = getRoot();
        Field RootChildrenField = RootNode.getField("children");
    	
        StringBuilder spawnString = new StringBuilder(createWoodenBoxsString());
        spawnString.append(createRobotsString());
    	
        //E' la funzione di spawn
    	RootChildrenField.importMFNodeFromString(4, spawnString.toString());
		
    	//Viene inviato il pacchetto al server che definisce che il mondo è pronto
		clientConnectionHandler.sendPacket(new CTS_WORLD_READY());
		worldSpawned = true;
	}

	// Funzione con la quale viene creata la stringa per lo spawn delle casse
	private String createWoodenBoxsString() 
	{
		StringBuilder string = new StringBuilder();
		float TempX, TempZ;
		for(int i=0; i < mappa.getXSize(); i++)
		{
			for(int j=0; j< mappa.getYSize(); j++)
			{
				/*
				 * Sto scorrendo l'intera mappa, se all'interno di questo valore c'Ã¨ un 1
				 * allora viene spawnata una cassa in quel punto
				 */
				TempX = matrixToWorldX(j);
				TempZ = matrixToWorldZ(i);
				if(mappa.get(new Point(i, j)) != 0 )
					string.append("DEF Box Proto2 {translation " + TempX + ",0.05," + TempZ + " size 0.0999,0.0999,0.0999 mass 2 locked TRUE} ");
			}
		}
		return string.toString();
	}
	
	
	// Funzione con la quale viene creata la stringa per lo spawn dei robot
	private String createRobotsString()
	{
		StringBuilder string = new StringBuilder();
		
		String robotName = new String("Guardia");
		String currentName;
		Point position = null;
		float tempX, tempZ;
		
		for (int i = 0; i < SharedVariables.getNumeroGuardie(); ++i)
		{
			currentName = robotName.concat(Integer.toString(i));
			
			position = new StartPosition(currentName, mappa);
			tempX = matrixToWorldX(position.getY());
			tempZ = matrixToWorldZ(position.getX());
			
			string.append("DEF " + currentName + " Guardia { name \"" + currentName + "\", translation " + tempX + ",0.05," + tempZ + " controller \"<extern>\" } ");
		}
		
		robotName = new String("Ladro");
		for (int i = 0; i < SharedVariables.getNumeroLadri(); ++i)
		{
			currentName = robotName.concat(Integer.toString(i));
			
			position = new StartPosition(currentName, mappa);
			tempX = matrixToWorldX(position.getY());
			tempZ = matrixToWorldZ(position.getX());
			
			
			string.append("DEF " + currentName + " Ladro { name \"" + currentName + "\", translation " + tempX + ",0.05," + tempZ + " rotation 0,1,0,3.14 controller \"<extern>\" } ");
		}
		
		return string.toString();
	}

	private float matrixToWorldZ(float point)
    {
    	return ((float) (mappa.getSpiazzamentoY() - ( 0.10 * point)));
    }

	private float matrixToWorldX(float point)
	{	
		return ((float) (mappa.getSpiazzamentoX() - ( 0.10 * point)));
	}
	
	public int step() { return step(SharedVariables.getTimeStep());	}
	
	@Override
	public void onStcSendMapReceived(Mappa mappa)
	{
		this.mappa = mappa;		
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