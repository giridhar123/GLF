package Controllers;

import General.SharedVariables;
import Robot.GenericRobot;
import Robot.LadroRobot;

/*
 * Controller relativo al robot di tipo ladro
 */

public class LadroController
{
    public static void main(String[] args) throws Exception
    {
    	System.out.println("LadroController avviato...");
    	
    	init(args);
    	
        LadroRobot robot = new LadroRobot(GenericRobot.NORD);
                
        robot.connectToServer();

        while (robot.step() != -1)
        {
        	robot.work();
        }
    }
    
    public static void init(String[] args)
    {
    	String webotsPath = args[0];
    	String projectPath = args[1];
    	int serverTcpPort = Integer.parseInt(args[2]);
    	int timeStep = Integer.parseInt(args[3]);
    	int numeroGuardie = Integer.parseInt(args[4]);
    	int numeroLadri = Integer.parseInt(args[5]);
    	
    	SharedVariables.init(projectPath, webotsPath, timeStep, serverTcpPort, numeroGuardie, numeroLadri);
    }
}
 