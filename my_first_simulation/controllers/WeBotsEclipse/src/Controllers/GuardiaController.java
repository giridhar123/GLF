package Controllers;

import com.cyberbotics.webots.controller.Speaker;

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
        Speaker speaker = robot.getSpeaker("speaker");
        speaker.setEngine("pico");
        speaker.setLanguage("it-IT");
       
        robot.connectToServer();
                
        while (robot.step() != -1)
        {
        	Speaker.playSound(speaker , speaker, "sounds/lupin.wav", 50.0, 1, 0, true);
        	robot.explore();
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