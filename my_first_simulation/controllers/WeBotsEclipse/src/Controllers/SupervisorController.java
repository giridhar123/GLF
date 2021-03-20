package Controllers;

import Map.Mappa;
import Network.ClientConnectionHandler;
import Network.Packets.ClientToServer.CTS_PEER_INFO;
import Robot.SupervisorRobot;
import Network.ServerConnectionHandler;
import General.ControllerExecutor;
import General.SharedVariables;

import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousSocketChannel;
import java.util.Vector;
import java.util.concurrent.Future;
import com.cyberbotics.webots.controller.Node;
import com.cyberbotics.webots.controller.Field;

@SuppressWarnings("unused")

public class SupervisorController
{
    private static SupervisorRobot supervisor;

    public static void main(String[] args) throws InterruptedException
    {
    	System.out.println("SupervisorController avviato...");
    	
    	init(args);
    	
    	SupervisorRobot supervisor = new SupervisorRobot();
        
    	supervisor.connectToServer();
    	
        while (supervisor.step() != -1);
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
