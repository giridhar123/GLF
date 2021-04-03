package General;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Properties;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.nio.charset.Charset;
import java.io.File;
import Network.Server;

public class Main
{
    private static ControllerExecutor supervisorController;

    public static void main(String args[]) throws IOException, FileNotFoundException
    {
        FileInputStream fileInputStream = new FileInputStream(new File("Config.txt"));
        InputStreamReader inputStreamReader = new InputStreamReader(fileInputStream, Charset.forName("UTF-8"));

        Properties properties = new Properties();
        properties.load(inputStreamReader);

        String webotsPath = properties.getProperty("webotsPath");
        String projectPath = properties.getProperty("projectPath");
        int serverTcpPort = Integer.parseInt(properties.getProperty("serverTcpPort"));
        int timeStep = Integer.parseInt(properties.getProperty("timeStep"));
        int numeroGuardie = Integer.parseInt(properties.getProperty("numeroGuardie"));
        int numeroLadri = Integer.parseInt(properties.getProperty("numeroLadri"));
        int dimMappaInternaX  = Integer.parseInt(properties.getProperty("dimMappaInternaX"));
		int dimMappaInternaY = Integer.parseInt(properties.getProperty("dimMappaInternaY"));
		int dimSpawnX = Integer.parseInt(properties.getProperty("dimSpawnX"));
		int dimSpawnGate = Integer.parseInt(properties.getProperty("dimSpawnGate"));
		String difficolta = properties.getProperty("difficolta");

        //Inizializzo variabili globali
        SharedVariables.init(projectPath, webotsPath, timeStep, serverTcpPort, numeroGuardie, numeroLadri, dimMappaInternaX, dimMappaInternaY, dimSpawnX, dimSpawnGate, difficolta);
        
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

