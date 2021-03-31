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
		
	public void spawnWorld()
	{
		if (mappa == null || worldSpawned)
			return;
		
        final Node viewpoint_node = getFromDef("viewpoint");
        Node robot_node = getFromDef("supervisor");
        robot_node.setVisibility(viewpoint_node, false);
        
        final Node Floor = getFromDef("World");
    	Field pavimento = Floor.getField("floorSize");
    	pavimento.setSFVec2f(mappa.getWeBotsXYMap());

    	final Node RootNode = getRoot();
        Field RootChildrenField = RootNode.getField("children");
    	
        StringBuilder spawnString = new StringBuilder(createWoodenBoxsString());
        spawnString.append(createRobotsString());
    	
    	RootChildrenField.importMFNodeFromString(4, spawnString.toString());
		
    	    	
    	spostaRobot("Guardia");
    	spostaRobot("Ladro");
		
		CTS_WORLD_READY cts_world_ready = new CTS_WORLD_READY();
		clientConnectionHandler.sendPacket(cts_world_ready);
		
		
		worldSpawned = true;
	}
	
	private void spostaRobot(String robotName)
	{
		int n = robotName.compareTo(new String("Guardia")) == 0 ? SharedVariables.getNumeroGuardie() : SharedVariables.getNumeroLadri();
		
		String currentName = null;
		Node robotNode = null;
		
		int x, y;
		Point position;
		for (int i = 0; i < n; ++i)
		{
			currentName = robotName.concat(Integer.toString(i));

			robotNode = getFromDef(currentName);
			if (robotNode != null)
			{
				position = new StartPosition(currentName, mappa);
				x = position.getX();
				y = position.getY();
				
				Field posizione = robotNode.getField("translation");
				double pos[] = posizione.getSFVec3f();
				
				double newPosition[] = new double[3];
				newPosition[0] = MatrixToWorldX((float) y, mappa.getWeBotsTile());
				newPosition[1] = pos[1];
				newPosition[2] = MatrixToWorldZ((float) x, mappa.getWeBotsTile());
				posizione.setSFVec3f(newPosition);
			}
		}
	}

	private String createWoodenBoxsString() 
	{
		StringBuilder string = new StringBuilder();
		float TempX, TempZ;
		for(int i=0; i < mappa.getXSize(); i++)
		{
			for(int j=0; j< mappa.getYSize(); j++)
			{
				//Sto scorrendo l'array, se all'interno di questo valore c'ï¿½ 1 allora faccio lo spawn su quel punto di posizione x,y
				TempX = MatrixToWorldX(j,mappa.getWeBotsTile());
				TempZ = MatrixToWorldZ(i,mappa.getWeBotsTile());
				if(mappa.get(new Point(i, j)) != 0 )
					string.append("DEF Box Proto2 {translation " + TempX + ",0.05," + TempZ + " size 0.0999,0.0999,0.0999 mass 2 locked TRUE} ");
			}
		}
		return string.toString();
	}
	
	private String createRobotsString()
	{
		StringBuilder string = new StringBuilder();
		
		String name = new String("Guardia");
		String currentName;
		for (int i = 0; i < SharedVariables.getNumeroGuardie(); ++i)
		{
			currentName = name.concat(Integer.toString(i));
			string.append("DEF " + currentName + " Guardia { name \"" + currentName + "\", controller \"<extern>\" } ");
		}
		
		name = new String("Ladro");
		for (int i = 0; i < SharedVariables.getNumeroLadri(); ++i)
		{
			currentName = name.concat(Integer.toString(i));
			string.append("DEF " + currentName + " Ladro { name \"" + currentName + "\", rotation 0,1,0,3.14 controller \"<extern>\" } ");
		}
		
		return string.toString();
	}

	private static float MatrixToWorldZ(float point, double WeBotsTile)
    {
    	point = (float) (WeBotsTile - ( 0.10 * point)); //4,95 e' la posizione 0 vedendola ad occhio, posso modificarla liberamente per un'implementazione futura in modo da rendere la mappa come schifo la voglio io
    	return point;
    }

	private static float MatrixToWorldX(float point, double WeBotsTile)
	{	
		point = (float) (WeBotsTile - ( 0.10 * point)); //4,95 e'la posizione 0 vedendola ad occhio, posso modificarla liberamente per un'implementazione futura in modo da rendere la mappa come schifo la voglio io
		return point;
	}
	
	@Override
	public void onStcSendMapReceived(Mappa mappa) {
		this.mappa = mappa;		
	}

	public int step() { return step(SharedVariables.getTimeStep());	}

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