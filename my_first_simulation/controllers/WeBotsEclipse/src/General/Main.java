package General;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Properties;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.nio.charset.Charset;
import java.io.File;
import java.util.Scanner;
import Network.Server;

import java.lang.Thread;

public class Main
{
    private static ControllerExecutor supervisorController;

    public static void main(String args[]) throws IOException, FileNotFoundException
    {
        FileInputStream fileInputStream = new FileInputStream(new File("path.txt"));
        InputStreamReader inputStreamReader = new InputStreamReader(fileInputStream, Charset.forName("UTF-8"));

        Properties properties = new Properties();
        properties.load(inputStreamReader);

        String webotsPath = properties.getProperty("webotspath");
        String projectPath = properties.getProperty("projectpath");
        int serverTcpPort = Integer.parseInt(properties.getProperty("tcp_port"));
        int timeStep = Integer.parseInt(properties.getProperty("time_step"));
        int numeroGuardie = Integer.parseInt(properties.getProperty("guardie"));
        int numeroLadri = Integer.parseInt(properties.getProperty("ladri"));
        
        //Inizializzo variabili globali
        SharedVariables.init(projectPath, webotsPath, timeStep, serverTcpPort, numeroGuardie, numeroLadri);
        
        Server server = new Server();
        server.start();
        avviaSupervisorController();
        
        try
        {
			server.join();
		} catch (InterruptedException e)
        {
			e.printStackTrace();
		}
    }

    public static void avviaSupervisorController()
    {
        supervisorController = new ControllerExecutor("SupervisorController", "supervisor");
        try
        {
            supervisorController.start();
            supervisorController.join();
        }
        catch (InterruptedException e)
        {
            e.printStackTrace();
        }
    }
}

