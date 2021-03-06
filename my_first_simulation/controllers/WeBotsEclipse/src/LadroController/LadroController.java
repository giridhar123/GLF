package LadroController;

import General.SharedVariables;
import Network.ClientConnectionHandler;
import Network.Client.CTS_PEER_INFO;
import Robot.LadroRobot;

public class LadroController
{
    public static void main(String[] args) throws Exception
    {
        LadroRobot robot = new LadroRobot();
        
        ClientConnectionHandler clientConnectionHandler = new ClientConnectionHandler(CTS_PEER_INFO.LADRO, robot);
        clientConnectionHandler.start();

        while (robot.step(SharedVariables.TIME_STEP) != -1);
    }
}
