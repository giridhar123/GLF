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

        String webotsPath = properties.getProperty("webotspath");
        String projectPath = properties.getProperty("projectpath");
        int serverTcpPort = Integer.parseInt(properties.getProperty("tcp_port"));
        int timeStep = Integer.parseInt(properties.getProperty("time_step"));
        int numeroGuardie = Integer.parseInt(properties.getProperty("guardie"));
        int numeroLadri = Integer.parseInt(properties.getProperty("ladri"));
        int DimMapX  = Integer.parseInt(properties.getProperty("DimMapX"));
		int DimMapY = Integer.parseInt(properties.getProperty("DimMapY"));
		int xDimSpawn = Integer.parseInt(properties.getProperty("xDimSpawn"));
		int SpawnPort = Integer.parseInt(properties.getProperty("SpawnPort"));
		double WeBotsXYMap = Double.parseDouble(properties.getProperty("WeBotsXYMap"));
		double WeBotsTile = Double.parseDouble(properties.getProperty("WeBotsTile"));
		String difficolta = properties.getProperty("difficolta");

        //Inizializzo variabili globali
        SharedVariables.init(projectPath, webotsPath, timeStep, serverTcpPort, numeroGuardie, numeroLadri,DimMapX, DimMapY, xDimSpawn, SpawnPort, WeBotsXYMap,WeBotsTile,difficolta);
        
        if (!SharedVariables.isInitialized())
        	return;
        
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

