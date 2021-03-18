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
                
        while (robot.step(SharedVariables.getTimeStep()) != -1)
        {
        	Speaker.playSound(speaker , speaker, "sounds/lupin.mp3", 50.0, 1, 0, true);
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