package GuardiaController;

import com.cyberbotics.webots.controller.Motor;
import com.cyberbotics.webots.controller.Robot;
import java.net.*;
import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousSocketChannel;
import java.util.concurrent.Future;

import Network.Opcodes;
import Network.Client.CTS_PROVA;

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
        short size = 45;
        CTS_PROVA packet = new CTS_PROVA(Opcodes.CTS_PROVA, size);
        ByteBuffer buffer = packet.encode();
        System.out.println("il buffer contiene" + (short) buffer.getShort());
        buffer.position(0);
        
        AsynchronousSocketChannel client = AsynchronousSocketChannel.open();
        InetSocketAddress hostAddress = new InetSocketAddress("localhost", TCP_PORT);
        Future<Void> future = client.connect(hostAddress);
        future.get();
        Future<Integer> writeResult = client.write(buffer);
        System.out.println("Inviato" + writeResult.get().shortValue());
        /* TEST SOCKET END */

        while (robot.step(timeStep) != -1)
        {
            motorL.setPosition(10.0);
            motorR.setPosition(10.0);
        }
    }
}
