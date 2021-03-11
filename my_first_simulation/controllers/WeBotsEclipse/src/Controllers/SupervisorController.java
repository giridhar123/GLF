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
    	SupervisorRobot supervisor = new SupervisorRobot();
    	
    	supervisor.connectToServer();
        
    	supervisor.connectToServer();
    	
        while (supervisor.step(SharedVariables.TIME_STEP) != -1);
    }
 }
