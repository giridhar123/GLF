import java.io.IOException;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.Properties;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.FileNotFoundException;
import java.nio.charset.Charset;
import java.io.File;
import java.util.Scanner;

public class General
{
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

        Server server = new Server();
        server.start();

        printMenu();
        Scanner scanner = new Scanner(System.in);
        int value = scanner.nextInt();
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
        System.out.println("Digita 1 per avviare i controller");
        System.out.println("Digita 2 per compilare tutti i controller");
    }

    public static void compilaTutto()
    {
        System.out.println("HEY");
    }

    public static void eseguiTutto()
    {
        guardiaController = new ControllerExecutor("GuardiaController", "guardia", webotsPath, projectPath);
        ladroController = new ControllerExecutor("LadroController", "ladro", webotsPath, projectPath);
        guardiaController.start();
        ladroController.start();
    }
}