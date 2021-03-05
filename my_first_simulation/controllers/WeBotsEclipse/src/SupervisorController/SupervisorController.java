package SupervisorController;

import Map.Mappa;
import Network.Client.CTS_PEER_INFO;
import General.ConnectionHandler;
import General.ControllerExecutor;

import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousSocketChannel;
import java.util.Vector;
import java.util.concurrent.Future;

import com.cyberbotics.webots.controller.Node;
import com.cyberbotics.webots.controller.Field;
import com.cyberbotics.webots.controller.Motor;
import com.cyberbotics.webots.controller.Robot;
import com.cyberbotics.webots.controller.Supervisor;

@SuppressWarnings("unused")
public class SupervisorController
{
    private static Mappa map;
    private static MySupervisor mySupervisor;
    private static final int TCP_PORT = 6868;
    private static SupervisorHandler supervisorHandler;

    public static void main(String[] args) throws InterruptedException
    {	
    	// Creazione dell'oggetto su webots
        final int TIME_STEP = 16;
        mySupervisor = new MySupervisor();
        
        final Node robot_node = mySupervisor.getFromDef("supervisor");
        final Node viewpoint_node = mySupervisor.getFromDef("viewpoint");
        
        final Node Floor = mySupervisor.getFromDef("World");//
        if(Floor != null)
        {
        		// da capire come modificare il campo di gioco
        }

        if (robot_node == null) {
            System.err.println("No DEF MY_ROBOT node found in the current world file");
            System.exit(1);
        }
        if (viewpoint_node == null) {
            System.err.println("No DEF ViewPoint node found in the current world file");
            System.exit(1);
        }

        robot_node.setVisibility(viewpoint_node, false);
        
        
        connectToServer();
        
        /* T E S T I N G */ 
        //SpawnTest(RootChildenField, mappa);
        
        while (mySupervisor.step(TIME_STEP) != -1) {
            //Thread.sleep(5);
        }
    }
    
    private static void connectToServer()
    {
        supervisorHandler = new SupervisorHandler(mySupervisor);
        supervisorHandler.start();
    }
    
    private static void SpawnTest(Field RootChildenField, Mappa mappa)
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
				 // Sto scorrendo l'array, se all'interno di questo valore c'� 1 allora faccio lo spawn su quel punto di posizione x,y
				 	TempX = MatrixToWorldX((float) i);
				 	TempY = MatrixToWorldY((float) j);
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
          
          //Seconda L
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
          int count = 0;
    }

	private static float MatrixToWorldY(float point) {
	
		point = (float) (4.95 - ( 0.10 * point)); //4,95 � la posizione 0 vedendola ad occhio, posso modificarla liberamente per un'implementazione futura in modo da rendere la mappa come schifo la voglio io
		return point;
	}
	private static float MatrixToWorldX(float point) {
		
		point = (float) (4.95 - ( 0.10 * point)); //4,95 � la posizione 0 vedendola ad occhio, posso modificarla liberamente per un'implementazione futura in modo da rendere la mappa come schifo la voglio io
		return point;
	}
 }
