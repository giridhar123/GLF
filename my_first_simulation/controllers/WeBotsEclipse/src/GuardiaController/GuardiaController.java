package GuardiaController;

import General.SharedVariables;
import Network.ClientConnectionHandler;
import Network.Client.CTS_PEER_INFO;
import Robot.GuardiaRobot;

public class GuardiaController
{  
    public static void main(String[] args) throws Exception
    {
        GuardiaRobot robot = new GuardiaRobot();

        ClientConnectionHandler clientConnectionHandler = new ClientConnectionHandler(CTS_PEER_INFO.GUARDIA);
        clientConnectionHandler.start();

        robot.turnRight();
        
        while (robot.step(SharedVariables.TIME_STEP) != -1);
    }
}
