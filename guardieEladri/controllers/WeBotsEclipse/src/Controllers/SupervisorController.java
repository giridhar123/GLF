package Controllers;

import Robot.SupervisorRobot;
import General.SharedVariables;

/*
 * Controller relativo al robot di tipo supervisor
 */

public class SupervisorController
{

    public static void main(String[] args) throws InterruptedException
    {
    	System.out.println("SupervisorController avviato...");

    	init(args);
    	
    	SupervisorRobot supervisor = new SupervisorRobot();
        
    	supervisor.connectToServer();
    	
        while (supervisor.step() != -1)
        {
        	supervisor.spawnWorld();
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
