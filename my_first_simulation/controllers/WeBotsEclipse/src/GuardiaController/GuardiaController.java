package GuardiaController;

import com.cyberbotics.webots.controller.Motor;
import com.cyberbotics.webots.controller.Robot;
import java.net.*;
import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousSocketChannel;
import java.util.concurrent.Future;
import Network.Client.CTS_PEER_INFO;
import Robot.GuardiaRobot;

public class GuardiaController
{  
    private static final int TCP_PORT = 6868;

    public static void main(String[] args) throws Exception
    {
        GuardiaRobot robot = new GuardiaRobot();
        int timeStep = 16;

        /* TEST SOCKET START */
        CTS_PEER_INFO packet = new CTS_PEER_INFO(CTS_PEER_INFO.LADRO);
        ByteBuffer buffer = packet.encode();
        
        AsynchronousSocketChannel client = AsynchronousSocketChannel.open();
        InetSocketAddress hostAddress = new InetSocketAddress("localhost", TCP_PORT);
        Future<Void> future = client.connect(hostAddress);
        future.get();
        Future<Integer> writeResult = client.write(buffer);
        /* TEST SOCKET END */

        System.out.println("1");
        robot.goStraightOn();
        System.out.println("2");
        robot.goStraightOn();
        System.out.println("3");
        robot.goStraightOn();
        
        while (robot.step(timeStep) != -1)
        {
            
        }
    }
}
