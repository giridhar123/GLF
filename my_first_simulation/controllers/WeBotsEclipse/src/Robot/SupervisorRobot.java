package Robot;

import java.util.Vector;
import com.cyberbotics.webots.controller.Field;
import com.cyberbotics.webots.controller.Node;
import com.cyberbotics.webots.controller.Supervisor;
import Map.Mappa;
import Network.ClientConnectionHandler;
import Network.Packet;
import Network.Packets.ClientToServer.CTS_PEER_INFO;
import Network.Packets.ClientToServer.CTS_WORLD_READY;

public class SupervisorRobot extends Supervisor implements Client {
	
	private ClientConnectionHandler clientConnectionHandler;
	
	public SupervisorRobot()
	{
		clientConnectionHandler = new ClientConnectionHandler(CTS_PEER_INFO.SUPERVISOR, this);
	}
	
	public void connectToServer()
	{
		clientConnectionHandler.start();
	}
		
	public void spawnaMappa(Mappa mappa)
	{	
		// Node
		final Node RootNode = getRoot();
        final Node Floor = getFromDef("World");
        final Node robot_node = getFromDef("supervisor");
        final Node viewpoint_node = getFromDef("viewpoint");
       
        // Field
        Field RootChildrenField = RootNode.getField("children");
        
        robot_node.setVisibility(viewpoint_node, false);
        
        // Controlli 
        /*
        if (robot_node == null) 
        {
            System.err.println("No DEF MY_ROBOT node found in the current world file");
            System.exit(1);
        }
        if (viewpoint_node == null) 
        {
        	System.err.println("No DEF ViewPoint node found in the current world file");
            System.exit(1);
        }
		*/
        // Inizializzazione Scenario
      
        
    	Field pavimento = Floor.getField("floorSize");
    	System.out.println(pavimento.getSFVec2f()[0]);
    	pavimento.setSFVec2f(mappa.getWeBotsXYMap());

    	Spawn(mappa, RootChildrenField);
	
	}
	
	
	private void SpawnAMMERDA(Mappa mappa, Field RootChildrenField )
	{
		float TempX, TempY;
		
		int[][] matrice= new int[21][21];
		int x,y;
		
		int min = 0;
		int max = matrice[1].length-1;
		
		for(int i=0; i < matrice[0].length ; i++)
			{	
				for(int j=0; j < matrice[1].length; j++)
				{
					if(i==min || i==max || j==min || j==max )
					{
						matrice[i][j] = 1 ;
					}
					else
					{
						matrice[i][j] = 0 ;
					}
					
				}
			}
		
		for(int i=0; i<100; i++)
		{
			x=(int) (Math.random()*21);
			y=(int) (Math.random()*21);
			
			if ((x == 10 && y == 10) || (matrice[x][y] == 1))
				i--;
			
			matrice[x][y]=1;
		}

		
		String spawnBox = "";		
		for(int i=0; i < 21; i++)
		{
			for(int j=0; j < 21; j++)
			{
				// Sto scorrendo l'array, se all'interno di questo valore c'ï¿½ 1 allora faccio lo spawn su quel punto di posizione x,y
				TempX = MatrixToWorldX((float) j,mappa.getWeBotsTile());
				TempY = MatrixToWorldZ((float) i,mappa.getWeBotsTile());
				if(matrice[i][j] == 1)
				{
					spawnBox += "DEF L1 Proto1 {translation "+TempX+",0.05,"+TempY+" size 0.099,0.099,0.099 mass 2 locked TRUE} ";
				}
			}
		}
		
		RootChildrenField.importMFNodeFromString(4, spawnBox);
	
		final Node robot_node = getFromDef("Ladro");
		if (robot_node != null)
		{
			System.out.println("Sposto il robot...");
			Field posizione = robot_node.getField("translation");
			double pos[] = posizione.getSFVec3f();
			
			double newPosition[] = new double[3];
			newPosition[0] = MatrixToWorldX((float) 10, mappa.getWeBotsTile());
			newPosition[1] = pos[1];
			newPosition[2] = MatrixToWorldZ((float) 10, mappa.getWeBotsTile());
			posizione.setSFVec3f(newPosition);
			System.out.println("Robot spostato");

			for(int i=0; i < 20; i++)
			{
				for(int j=0; j< 20; j++)
				{
					// Sto scorrendo l'array, se all'interno di questo valore c'ï¿½ 1 allora faccio lo spawn su quel punto di posizione x,y
				 	TempX = MatrixToWorldX((float) j,mappa.getWeBotsTile());
				 	TempY = MatrixToWorldZ((float) i,mappa.getWeBotsTile());
	
					 if(matrice[i][j] == 1)
					 {
						 String SpawnBox = "DEF L1 Proto1 {translation "+TempX+",0.05,"+TempY+" size 0.099,0.099,0.099 mass 2 locked TRUE} " ;
						 RootChildrenField.importMFNodeFromString(4,SpawnBox);
					 }

				}
		
				Packet worldReady = new CTS_WORLD_READY();
				clientConnectionHandler.sendPacket(worldReady);
			}
		}
	}

	private void Spawn(Mappa mappa, Field RootChildrenField) 
	{
		String SpawnBox = "";
		float TempX, TempZ;
		
		for(int i=0; i < mappa.getXSize(); i++)
		{
			for(int j=0; j< mappa.getYSize(); j++)
			{
				//Sto scorrendo l'array, se all'interno di questo valore c'ï¿½ 1 allora faccio lo spawn su quel punto di posizione x,y
				TempX = MatrixToWorldX((float) j,mappa.getWeBotsTile());
				TempZ = MatrixToWorldZ((float) i,mappa.getWeBotsTile());
			
				if(mappa.get(i, j) != 0 )
					SpawnBox += "DEF L1 Proto1 {translation "+TempX+",0.05,"+TempZ+" size 0.099,0.099,0.099 mass 2 locked TRUE}" ;
			}
		}
		RootChildrenField.importMFNodeFromString(4,SpawnBox);
		
		Node robot_node = getFromDef("Ladro");
		if (robot_node != null)
		{
			System.out.println("Sposto il ladro...");
			Field posizione = robot_node.getField("translation");
			double pos[] = posizione.getSFVec3f();
			
			double newPosition[] = new double[3];
			newPosition[0] = MatrixToWorldX((float) 15, mappa.getWeBotsTile());
			newPosition[1] = pos[1];
			newPosition[2] = MatrixToWorldZ((float) 15, mappa.getWeBotsTile());
			posizione.setSFVec3f(newPosition);
			System.out.println("Ladro spostato");
		}
		
		robot_node = getFromDef("Guardia");
		if (robot_node != null)
		{
			System.out.println("Sposto la guardia...");
			Field posizione = robot_node.getField("translation");
			double pos[] = posizione.getSFVec3f();
			
			double newPosition[] = new double[3];
			newPosition[0] = MatrixToWorldX((float) 4, mappa.getWeBotsTile());
			newPosition[1] = pos[1];
			newPosition[2] = MatrixToWorldZ((float) 4, mappa.getWeBotsTile());
			posizione.setSFVec3f(newPosition);
			System.out.println("Guardia spostata");
		}
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
	
	private static void RestartNode(Node robot_node)
	{
        robot_node.restartController();
	}
	
	// Da cancellare
    private static void SpawnTestBrutto(Field RootChildenField, Mappa mappa)
    {
    	//String SpawnEPuck = "DEF prova E-puck { controller \"MyController\", translation 0,1.5,0} " ;
    	//String SpawnBox2 = "DEF prova2 WoodenBox {translation 0,1.5,0 size 0.1,0.1,0.1 mass 2} " ;
    	
    	 float TempX;
    	 float TempY;
    	 
    	 int i=0;
    	 int j=0;
    	 
    	 for(i=0; i<100; i++)
		  {
			 for(j=0; j<100; j++)
			 {
				 // Sto scorrendo l'array, se all'interno di questo valore c'ï¿½ 1 allora faccio lo spawn su quel punto di posizione x,y
				 	TempX = MatrixToWorldX((float) j, mappa.getWeBotsTile());
				 	TempY = MatrixToWorldZ((float) i, mappa.getWeBotsTile());
				 if(mappa.get(i, j) == 1 )
				 {
		    	  String SpawnBox = "DEF L1 Proto1 {translation "+TempX+",0.05,"+TempY+" size 0.1,0.1,0.1 mass 2} " ;
		          RootChildenField.importMFNodeFromString(4,SpawnBox);
				 }
			}
			 
		  }
 
    	  String SpawnBox1 = "DEF L1 Proto1 {translation 0,0.05,0 size 0.1,0.1,0.1 mass 2} " ;
          String SpawnBox2 = "DEF L1 Proto1 {translation 0.1,0.05,0 size 0.1,0.1,0.1 mass 2} " ;
          String SpawnBox3 = "DEF L1 Proto1 {translation 0.2,0.05,0 size 0.1,0.1,0.1 mass 2} " ;
          String SpawnBox4 = "DEF L1 Proto1 {translation 0.3,0.05,0 size 0.1,0.1,0.1 mass 2} " ;
          String SpawnBox5 = "DEF L1 Proto1 {translation 0.3,0.05,-0.1 size 0.1,0.1,0.1 mass 2} " ;
          
          Vector<String> v = new Vector<String>(6);
          	v.add(SpawnBox1) ;
          	v.add(SpawnBox2) ;
          	v.add(SpawnBox3) ;
          	v.add(SpawnBox4) ;
          	v.add(SpawnBox5) ;
   
          for(i=0 ; i<5 ; i++)
          	RootChildenField.importMFNodeFromString(4,v.get(i));
          v.clear();
          
          // Seconda L
	           SpawnBox1 = "DEF L2 Proto1 {translation 0,0.05,-0.60 size 0.1,0.1,0.1 mass 2} " ;
	           SpawnBox2 = "DEF L2 Proto1 {translation 0.1,0.05,-0.60 size 0.1,0.1,0.1 mass 2} " ;
	           SpawnBox3 = "DEF L2 Proto1 {translation 0.2,0.05,-0.60 size 0.1,0.1,0.1 mass 2} " ;
	           SpawnBox4 = "DEF L2 Proto1 {translation 0.3,0.05,-0.60 size 0.1,0.1,0.1 mass 2} " ;
	           SpawnBox5 = "DEF L2 Proto1 {translation 0.3,0.05,-0.70 size 0.1,0.1,0.1 mass 2} " ;
	           
	  	     	v.add(SpawnBox1) ;
	  	     	v.add(SpawnBox2) ;
	  	     	v.add(SpawnBox3) ;
	  	     	v.add(SpawnBox4) ;
	  	     	v.add(SpawnBox5) ;
       	
	  	      for(i=0 ; i<5 ; i++)
	           	RootChildenField.importMFNodeFromString(4,v.get(i));
	  	      v.clear();
  	   
	  	        String Ball = "DEF O Ball1 {translation 0.25,0.035,-0.35 mass 2 radius 0.10} " ;
	           	RootChildenField.importMFNodeFromString(4,Ball);
    }

	@Override
	public void onStcSendMapReceived(Mappa mappa) {
		spawnaMappa(mappa);
		CTS_WORLD_READY cts_world_ready = new CTS_WORLD_READY();
		clientConnectionHandler.sendPacket(cts_world_ready);
	}
 }