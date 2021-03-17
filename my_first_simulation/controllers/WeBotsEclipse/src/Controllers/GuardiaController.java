package Controllers;

import General.SharedVariables;
import Robot.GenericRobot;
import Robot.GuardiaRobot;


public class GuardiaController
{  
    public static void main(String[] args) throws Exception
    {
    	System.out.println("GuardiaController avviato...");
    	
    	init(args);
        GuardiaRobot robot = new GuardiaRobot(GenericRobot.SUD);
       
        robot.connectToServer();
                
        while (robot.step(SharedVariables.getTimeStep()) != -1)
        {
        	robot.explore();
        }
    }
    
    public static void init(String[] args)
    {
    	String webotsPath = args[0];
    	String projectPath = args[1];
    	int serverTcpPort = Integer.parseInt(args[2]);
    	int timeStep = Integer.parseInt(args[3]);
    	
    	SharedVariables.init(projectPath, webotsPath, timeStep, serverTcpPort);
    }
}