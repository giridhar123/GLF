package Cleaner;

import java.util.Vector;

import com.cyberbotics.webots.controller.Field;
import com.cyberbotics.webots.controller.Node;
import com.cyberbotics.webots.controller.Supervisor;

import Map.Mappa;
import Map.Point;

public class MySupervisor extends Supervisor {
		
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

        // Inizializzazione Scenario
       
        
    	Field pavimento = Floor.getField("floorSize");
    	System.out.println(pavimento.getSFVec2f()[0]);
    	pavimento.setSFVec2f(mappa.getWeBotsXYMap());

    	Spawn(mappa, RootChildrenField);
	
	}
	
	
	private static void Spawn(Mappa mappa, Field RootChildrenField )
	{
		float TempX, TempY;

	
	for(int i=0; i < mappa.getXSize() ; i++)
	  {
		 for(int j=0; j< mappa.getYSize(); j++)
		 {
			 // Sto scorrendo l'array, se all'interno di questo valore c'� 1 allora faccio lo spawn su quel punto di posizione x,y
			 	TempX = MatrixToWorldX((float) i,mappa.getWeBotsTile());
			 	TempY = MatrixToWorldY((float) j,mappa.getWeBotsTile());

			if(mappa.get(new Point(i, j)) != 0 )
			 	System.out.println(mappa.get(new Point(i, j)));
			 {
				 String SpawnBox = "DEF L1 Proto1 {translation "+TempX+",0.05,"+TempY+" size 0.099,0.099,0.099 mass 2 locked TRUE} " ;
				 RootChildrenField.importMFNodeFromString(4,SpawnBox);
			 }
		}
	  }
	}

	private static float MatrixToWorldY(float point, double WeBotsTile) {
		point = (float) (WeBotsTile - ( 0.10 * point)); //4,95 e' la posizione 0 vedendola ad occhio, posso modificarla liberamente per un'implementazione futura in modo da rendere la mappa come schifo la voglio io
		return point;
	}
	private static float MatrixToWorldX(float point, double WeBotsTile) {
		
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
				 // Sto scorrendo l'array, se all'interno di questo valore c'� 1 allora faccio lo spawn su quel punto di posizione x,y
				 	TempX = MatrixToWorldX((float) i, mappa.getWeBotsTile());
				 	TempY = MatrixToWorldY((float) j, mappa.getWeBotsTile());
				 if(mappa.get(new Point(i, j)) == 1 )
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
	          int count = 0;
    }
    
    
 }