package Controllers;

import com.cyberbotics.webots.controller.Camera;
import com.cyberbotics.webots.controller.CameraRecognitionObject;
import General.SharedVariables;
import Robot.GenericRobot;
import Robot.GuardiaRobot;
import com.cyberbotics.webots.controller.LED;


public class GuardiaController
{  
    public static void main(String[] args) throws Exception
    {
        GuardiaRobot robot = new GuardiaRobot(GenericRobot.SUD);
       
        robot.connectToServer();
                
        while (robot.step(SharedVariables.TIME_STEP) != -1)
        {

         robot.explore();
        }
    }
}