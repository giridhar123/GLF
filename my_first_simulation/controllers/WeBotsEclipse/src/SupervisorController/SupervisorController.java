package SupervisorController;

import Map.Mappa;
import Network.Client.CTS_PEER_INFO;
import General.ConnectionHandler;
import General.ControllerExecutor;
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
    private static MySupervisor mySupervisor;
    private static SupervisorHandler supervisorHandler;

    public static void main(String[] args) throws InterruptedException
    {	      
    	int timeStep = 16;
    	
    	mySupervisor = new MySupervisor();
        connectToServer();
        
        while (mySupervisor.step(timeStep) != -1);
        
    }
    
    private static void connectToServer()
    {
        supervisorHandler = new SupervisorHandler(mySupervisor);
        supervisorHandler.start();
    }
 }
