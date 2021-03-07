package GuardiaController;

import General.SharedVariables;
import Network.ClientConnectionHandler;
import Network.Client.CTS_PEER_INFO;
import Robot.GenericRobot;
import Robot.GuardiaRobot;

public class GuardiaController
{  
    public static void main(String[] args) throws Exception
    {
        GuardiaRobot robot = new GuardiaRobot(GenericRobot.EST);

        ClientConnectionHandler clientConnectionHandler = new ClientConnectionHandler(CTS_PEER_INFO.GUARDIA, robot);
        clientConnectionHandler.start();
        
        while (true)
        {
        	robot.stop();
        	robot.step(SharedVariables.TIME_STEP);
        }
    }
}
