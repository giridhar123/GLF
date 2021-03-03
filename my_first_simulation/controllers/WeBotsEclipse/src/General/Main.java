package General;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Properties;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.nio.charset.Charset;
import java.io.File;
import java.util.Scanner;
import java.lang.Thread;

public class Main
{
    private static ControllerExecutor supervisorController;
    private static ControllerExecutor guardiaController;
    private static ControllerExecutor ladroController;
    private static String webotsPath;
    private static String projectPath;

    public static void main(String args[]) throws IOException, FileNotFoundException
    {
    	
        FileInputStream fileInputStream = new FileInputStream(new File("path.txt"));
        InputStreamReader inputStreamReader = new InputStreamReader(fileInputStream, Charset.forName("UTF-8"));

        Properties properties = new Properties();
        properties.load(inputStreamReader);

        webotsPath = properties.getProperty("webotspath");
        projectPath = properties.getProperty("projectpath");

        //Server server = new Server();
        //server.start();

        printMenu();
        Scanner scanner = new Scanner(System.in);
        int value = 2;//scanner.nextInt();
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
        guardiaController = new ControllerExecutor("GuardiaController", "guardia", webotsPath, projectPath);
        ladroController = new ControllerExecutor("LadroController", "ladro", webotsPath, projectPath);
        supervisorController = new ControllerExecutor("SupervisorController", "supervisor", webotsPath, projectPath);
        try
        {
            guardiaController.start();
        
            while (!(guardiaController.ready))
                Thread.sleep(5);        
            
            ladroController.start();
            while (!(ladroController.ready))
                Thread.sleep(5);

            supervisorController.start();
        }
        catch (InterruptedException e)
        {
            e.printStackTrace();
        }
    }
}