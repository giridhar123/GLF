package GuardiaController;

import com.cyberbotics.webots.controller.Motor;
import com.cyberbotics.webots.controller.Robot;
import java.net.*;
import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousSocketChannel;
import java.util.concurrent.Future;
import Network.Client.CTS_PEER_INFO;

public class GuardiaController
{  
    private static final int TCP_PORT = 6868;

    public static void main(String[] args) throws Exception
    {
        Robot robot = new Robot();
        int timeStep = 16;
        Motor motorL = robot.getMotor("left wheel motor");
        Motor motorR = robot.getMotor("right wheel motor");

        /* TEST SOCKET START */
        CTS_PEER_INFO packet = new CTS_PEER_INFO(CTS_PEER_INFO.LADRO);
        ByteBuffer buffer = packet.encode();
        
        AsynchronousSocketChannel client = AsynchronousSocketChannel.open();
        InetSocketAddress hostAddress = new InetSocketAddress("localhost", TCP_PORT);
        Future<Void> future = client.connect(hostAddress);
        future.get();
        Future<Integer> writeResult = client.write(buffer);
        /* TEST SOCKET END */

        while (robot.step(timeStep) != -1)
        {
            motorL.setPosition(10.0);
            motorR.setPosition(10.0);
        }
    }
}
