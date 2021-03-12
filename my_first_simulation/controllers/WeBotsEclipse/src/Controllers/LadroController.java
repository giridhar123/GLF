package Controllers;

import java.util.ArrayList;

import General.SharedVariables;
import Map.Point;
import Robot.GenericRobot;
import Robot.LadroRobot;

public class LadroController
{
	
    public static void main(String[] args) throws Exception
    {
        LadroRobot robot = new LadroRobot(GenericRobot.SUD);
        
        //ArrayList<Point> pts = potentialShelters(map);  pts contiene i possibili nascondigli
        //estrai un punto da pts
                
        robot.connectToServer();

        while (robot.step(SharedVariables.TIME_STEP) != -1)
        {
        	robot.hide();
        }
    }
    
    
}
 