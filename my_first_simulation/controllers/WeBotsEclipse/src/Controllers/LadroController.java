package Controllers;

import General.SharedVariables;
import Robot.GenericRobot;
import Robot.LadroRobot;

public class LadroController
{
    public static void main(String[] args) throws Exception
    {
    	System.out.println("LadroController avviato...");
    	
    	init(args);
    	
        LadroRobot robot = new LadroRobot(GenericRobot.NORD);
        
        //ArrayList<Point> pts = potentialShelters(map);  pts contiene i possibili nascondigli
        //estrai un punto da pts
                
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
 