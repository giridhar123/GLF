import com.cyberbotics.webots.controller.Motor;
import com.cyberbotics.webots.controller.Robot;
import java.io.*;
import java.net.*;
import java.util.Date;

public class GuardiaController
{  
    private final int TCP_PORT = 6868;

    public static void main(String[] args) throws IOException
    {    
        Robot robot = new Robot();
        int timeStep = 16;
        Motor motorL = robot.getMotor("left wheel motor");
        Motor motorR = robot.getMotor("right wheel motor");

        /* TEST SOCKET START */
        Socket socket = new Socket("127.0.0.1", TCP_PORT);
        OutputStream output = socket.getOutputStream();
        PrintWriter writer = new PrintWriter(output, true);
        writer.println("Ciaooooo sono Guardia controller");
        /* TEST SOCKET END */

        while (robot.step(timeStep) != -1)
        {
            motorL.setPosition(10.0);
            motorR.setPosition(10.0);
        }
    }
}
