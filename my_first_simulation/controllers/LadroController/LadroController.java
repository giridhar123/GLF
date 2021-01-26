import com.cyberbotics.webots.controller.Motor;
import com.cyberbotics.webots.controller.Robot;

public class LadroController
{
    public static void main(String[] args)
    {
        Robot robot = new Robot();
        int timeStep = 16;
        Motor motorL = robot.getMotor("left wheel motor");
        Motor motorR = robot.getMotor("right wheel motor");

        while (robot.step(timeStep) != -1) {
            motorL.setPosition(-10.0);
            motorR.setPosition(-10.0);
        }
    }
}
