package SupervisorController;

import com.cyberbotics.webots.controller.Supervisor;
import com.cyberbotics.webots.controller.Node;

import java.util.Vector;

import com.cyberbotics.webots.controller.Field;
import com.cyberbotics.webots.controller.Motor;
import com.cyberbotics.webots.controller.Robot;

@SuppressWarnings("unused")
public class SupervisorController
{
    public static void main(String[] args) throws InterruptedException
    {
        final int TIME_STEP = 16;

        final Supervisor supervisor = new Supervisor();

        // do this once only
        final Node robot_node = supervisor.getFromDef("supervisor");
        final Node viewpoint_node = supervisor.getFromDef("viewpoint");
        if (robot_node == null) {
            System.err.println("No DEF MY_ROBOT node found in the current world file");
            System.exit(1);
        }
        if (viewpoint_node == null) {
            System.err.println("No DEF ViewPoint node found in the current world file");
            System.exit(1);
        }

        robot_node.setVisibility(viewpoint_node, false);
        
        /* Cerca un nuovo controller */
        Node RootNode = supervisor.getRoot();
        Field RootChildenField = RootNode.getField("children");
        	//String SpawnEPuck = "DEF prova E-puck { controller \"MyController\", translation 0,1.5,0} " ;
        	//String SpawnBox2 = "DEF prova2 WoodenBox {translation 0,1.5,0 size 0.1,0.1,0.1 mass 2} " ;
        
       //Prima L
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
 
        for(int i=0 ; i<5 ; i++)
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
     	
	      for(int i=0 ; i<5 ; i++)
         	RootChildenField.importMFNodeFromString(4,v.get(i));
	      v.clear();
	   
	        String Ball = "DEF O Ball1 {translation 0.25,0.035,-0.35 mass 2 radius 0.10} " ;
         	RootChildenField.importMFNodeFromString(4,Ball);


	      
        int count = 0;
        while (supervisor.step(TIME_STEP) != -1) {
            
 
            Thread.sleep(5);
        }
    }
}
