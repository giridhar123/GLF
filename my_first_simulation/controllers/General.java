import java.io.IOException;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.Properties;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.FileNotFoundException;
import java.nio.charset.Charset;
import java.io.File;

public class General
{
    public static void main(String args[]) throws IOException, FileNotFoundException
    {
        FileInputStream fileInputStream = new FileInputStream(new File("path.txt"));
        InputStreamReader inputStreamReader = new InputStreamReader(fileInputStream, Charset.forName("UTF-8"));

        Properties properties = new Properties();
        properties.load(inputStreamReader);

        String webotsPath = properties.getProperty("webotspath");
        String projectPath = properties.getProperty("projectpath");

        ControllerExecutor guardiaController = new ControllerExecutor("GuardiaController", "guardia", webotsPath, projectPath);
        ControllerExecutor ladroController = new ControllerExecutor("LadroController", "ladro", webotsPath, projectPath);
        guardiaController.start();
        ladroController.start();
    }
}