package Robot;

import com.cyberbotics.webots.controller.LED;

public class SireneThread extends Thread{
	
	LED led1, led2;
	
	public SireneThread(GuardiaRobot guardiaRobot)
	{
		led1 = guardiaRobot.getLED("led_1"); //rosso
		led2 = guardiaRobot.getLED("led_2"); //blu
	}
	
	public void run()
	{
		while (true)
		{
	        if(led1.get() == 0) // se rosso è spento
	        {
	        	led1.set(255);
	        	led2.set(0); // accendi rosso
	        }
	        else
	        {
	        	led1.set(0);
	        	led2.set(255); // accendi rosso
	        }  
	        
	        try
	        {
				sleep(500);
			} catch (InterruptedException e)
	        {
				e.printStackTrace();
			}
		}
	}
}
