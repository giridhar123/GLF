import com.cyberbotics.webots.controller.Supervisor;
import com.cyberbotics.webots.controller.Node;
import com.cyberbotics.webots.controller.Field;

public class MyControllerJava {

  public static void main(String[] args) {

    final int TIME_STEP = 32;

    final Supervisor supervisor = new Supervisor();

    // do this once only
    final Node robot_node = supervisor.getFromDef("prova");
    if (robot_node == null) {
      System.err.println("No DEF MY_ROBOT node found in the current world file");
      System.exit(1);
    }

    /* Cerca un nuovo controller */
    Node RootNode = supervisor.getRoot();
    Field RootChildenField = RootNode.getField("children");
    String SpawnEPuck = "DEF prova E-puck { controller \"MyController\", translation 0,1.5,0} " ;
    String SpawnBox = "DEF prova2 WoodenBox {translation 0,1.5,0 size 0.1,0.1,0.1 mass 2} " ;

    //RootChildenField.importMFNodeFromString(4,SpawnEPuck);
    RootChildenField.importMFNodeFromString(4,SpawnBox);

    
  }
}




