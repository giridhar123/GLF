package SupervisorController;

import Map.Mappa;
import Network.ClientConnectionHandler;
import Network.Client.CTS_PEER_INFO;
import Robot.SupervisorRobot;
import General.ConnectionHandler;
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
        
        while (supervisor.step(SharedVariables.TIME_STEP) != -1);
    }
 }
