package Controllers;

import com.cyberbotics.webots.controller.Camera;
import com.cyberbotics.webots.controller.CameraRecognitionObject;
import General.SharedVariables;
import Robot.GenericRobot;
import Robot.GuardiaRobot;
import com.cyberbotics.webots.controller.LED;


public class GuardiaController
{  
    public static void main(String[] args) throws Exception
    {
        GuardiaRobot robot = new GuardiaRobot(GenericRobot.SUD);
       
        robot.connectToServer();
                
        while (robot.step(SharedVariables.TIME_STEP) != -1)
        {

         robot.explore();
        }
    }
}

/*

    if (led_counter[0] == 0) {
      random_color = (int)(10 * ((float)rand() / RAND_MAX));
      wb_led_set(led[0], random_color);

       We will rechange the color in a random amount of time. 
      led_counter[0] = (int)(4 * ((float)rand() / RAND_MAX)) + 4;
      if (random_color == 0)
        printf("LED 0 is turned off\n");
      else
        printf("LED 0 uses color %d\n", random_color);
    } else
      led_counter[0]--;

    
     * For led 1 (left hand side), which is a monochromatic gradual LED,
     * we increase and decrease its value, making it glow
     
    wb_led_set(led[1], led_counter[1]);
    led_counter[1] += led1_increase;
    if (led_counter[1] > 255) {
      led_counter[1] = 255;
      led1_increase = -10;
    } else if (led_counter[1] < 0) {
      led_counter[1] = 0;
      led1_increase = 10;
    }
    
     * For led 2 (back side), which is a RGB gradual LED, we set a
     * random value each 1024 ms.
     
    if (led_counter[2] == 0)
      wb_led_set(led[2], (int)(0xffffff * ((float)rand() / RAND_MAX)));
    led_counter[2]++;
    if (led_counter[2] == 16)
      led_counter[2] = 0;


  return 0;
}

*/
