// File:          LadroController.java
// Date:
// Description:
// Author:
// Modifications:

// You may need to add other webots classes such as
//  import com.cyberbotics.webots.controller.DistanceSensor;
import com.cyberbotics.webots.controller.Motor;
import com.cyberbotics.webots.controller.Robot;

// Here is the main class of your controller.
// This class defines how to initialize and how to run your controller.
public class LadroController {
  public static void main(String[] args) {

    System.out.println("1");
    Robot robot = new Robot();
    System.out.println("2");
    int timeStep = 16;
    System.out.println("3");
    Motor motorL = robot.getMotor("left wheel motor");
    System.out.println("4");
    Motor motorR = robot.getMotor("right wheel motor");
    System.out.println("5");

    if (robot == null)
      System.out.println("Robot null");
      
    
      motorL.setPosition(-10.0);
      motorR.setPosition(-10.0);
    while (robot.step(timeStep) != -1) {
        System.out.println("LOL");
    }
    System.out.println("6");
  }
}
