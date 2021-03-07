package LadroController;

import General.SharedVariables;
import Network.ClientConnectionHandler;
import Network.Client.CTS_PEER_INFO;
import Robot.GenericRobot;
import Robot.LadroRobot;

public class LadroController
{
    public static void main(String[] args) throws Exception
    {
        LadroRobot robot = new LadroRobot(GenericRobot.OVEST);
        
        ClientConnectionHandler clientConnectionHandler = new ClientConnectionHandler(CTS_PEER_INFO.LADRO, robot);
        clientConnectionHandler.start();

        while (true)
        {
        	robot.stop();
        	robot.step(SharedVariables.TIME_STEP);
        }
    }
}
