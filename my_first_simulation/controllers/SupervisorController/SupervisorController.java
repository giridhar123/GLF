import com.cyberbotics.webots.controller.Supervisor;
import com.cyberbotics.webots.controller.Node;
import com.cyberbotics.webots.controller.Field;
import com.cyberbotics.webots.controller.Motor;
import com.cyberbotics.webots.controller.Robot;

public class SupervisorController
{
    public static void main(String[] args)
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
        String SpawnEPuck = "DEF prova E-puck { controller \"MyController\", translation 0,1.5,0} " ;
        String SpawnBox = "DEF prova2 WoodenBox {translation 0,1.5,0 size 0.1,0.1,0.1 mass 2} " ;
        
        RootChildenField.importMFNodeFromString(4,SpawnBox);

        Motor motor = supervisor.getMotor("left wheel motor");
        //  DistanceSensor ds = robot.getDistanceSensor("dsname");
        //  ds.enable(timeStep);
        
        // Main loop:
        // - perform simulation steps until Webots is stopping the controller
        int count = 0;
        
        while (supervisor.step(TIME_STEP) != -1) {
            
            //motor.setPosition(10.0);
            System.out.println(count++);
        }

        //RootChildenField.importMFNodeFromString(4,SpawnEPuck);
    }
}
