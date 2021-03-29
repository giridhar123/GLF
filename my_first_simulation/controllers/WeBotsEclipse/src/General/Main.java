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
        int DimMapX  = Integer.parseInt(properties.getProperty("DimMapX"));
		int DimMapY = Integer.parseInt(properties.getProperty("DimMapY"));
		int xDimSpawn = Integer.parseInt(properties.getProperty("xDimSpawn"));
		int SpawnPort = Integer.parseInt(properties.getProperty("SpawnPort"));
		double WeBotsXYMap = Double.parseDouble(properties.getProperty("WeBotsXYMap"));
		double WeBotsTile = Double.parseDouble(properties.getProperty("WeBotsTile"));
		String difficolta = properties.getProperty("difficolta");

        //Inizializzo variabili globali
        SharedVariables.init(projectPath, webotsPath, timeStep, serverTcpPort, numeroGuardie, numeroLadri,DimMapX, DimMapY, xDimSpawn, SpawnPort, WeBotsXYMap,WeBotsTile,difficolta);
        
        Server server = new Server();
        server.start();

        printMenu();
        Scanner scanner = new Scanner(System.in);
        int value = 2 ;	//scanner.nextInt();
        scanner.close();

        switch (value)
        {
            case 1:
                compilaTutto();
                break;
            case 2:
                eseguiTutto();
                break;
        }
    }

    public static void printMenu()
    {
        System.out.println("Digita 1 per compilare i controller");
        
        System.out.println("Digita 2 per avviare tutti i controller");
    }

    public static void compilaTutto()
    {
        System.out.println("HEY");
    }

    public static void eseguiTutto()
    {
    	//Cleaner cleaner = new Cleaner();

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

    public static void RestartAll()
    {
    	/*
    	 * L'idea � quella di utilizzare questa funzione per resettare il mondo, per� deve essere effettuata ogniqualvolta venga riavviato il main.
    	 * Purtroppo il codice va avanti prima che webots si "stabilizzi" per riagganciare i controllori.
    	 * Ho provato ad usare syncronized ma non � andato, c'� da capire come fare.
    	 * 
    	 * com.cyberbotics.webots.controller.Node; // restartController
    	 * com.cyberbotics.webots.controller.Supervisor; // worldReset
    	 */
    }

}

