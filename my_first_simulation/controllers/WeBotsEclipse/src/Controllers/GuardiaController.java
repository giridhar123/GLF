package Controllers;

import General.SharedVariables;
import Robot.GenericRobot;
import Robot.GuardiaRobot;

public class GuardiaController
{  
    public static void main(String[] args) throws Exception
    {
        GuardiaRobot robot = new GuardiaRobot(GenericRobot.EST);
        robot.connectToServer();
    	
        while (robot.myStep(SharedVariables.TIME_STEP, null) != -1)
        {
        	//robot.explore();
        	//robot.goStraightOn(null, 3);
        }
    }
}
