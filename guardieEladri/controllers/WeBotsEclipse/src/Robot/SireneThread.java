package Robot;

import com.cyberbotics.webots.controller.LED;
/*
 * Classe per avviare le sirene in modo autonomo.
 */
public class SireneThread extends Thread{
	
	private LED led1, led2;
	private String robotName;
	
	public SireneThread(GuardiaRobot guardiaRobot)
	{
		led1 = guardiaRobot.getLED("led_1"); //rosso
		led2 = guardiaRobot.getLED("led_2"); //blu
		robotName = guardiaRobot.getName();
	}
	
	public void run()
	{
		while (true)
		{
	        if(led1.get() == 0) // se rosso è spento
	        {
	        	led1.set(255); // accendi rosso
	        	led2.set(0); // spegni blu
	        }
	        else
	        {
	        	led1.set(0); // spegni rosso
	        	led2.set(255); // accendi blu
	        }  
	        
	        try
	        {
				sleep(500);
			} catch (InterruptedException e)
	        {
				System.out.println(robotName + ": Si è verificato un errore nel thread che gestisce le sirene della guardia.");
			}
		}
	}
}
