import java.io.IOException;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.lang.Thread;

public class ControllerExecutor extends Thread
{
    private String controllerName;
    private String robotName;

    public ControllerExecutor(String controllerName, String robotName)
    {
        this.controllerName = controllerName;
        this.robotName = robotName;
    }

    public void run()
    {
        ProcessBuilder processBuilder = new ProcessBuilder();

        //in windows
        processBuilder.command("cmd.exe", "/c", "set WEBOTS_ROBOT_NAME="+robotName);

        try {

            Process process = processBuilder.start();

            BufferedReader reader =
                    new BufferedReader(new InputStreamReader(process.getInputStream()));

            String line;
            while ((line = reader.readLine()) != null) {
                System.out.println(line);
            }

            int exitCode = process.waitFor();
            System.out.println("\nExited with error code : " + exitCode);

        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

         processBuilder.command("cmd.exe", "/c", "java -classpath C:/Users/biril/AppData/Local/Programs/Webots/lib/controller/java/Controller.jar;C:/Users/biril/Documents/GLF_Personal/my_first_simulation/controllers/"+ controllerName +" -Djava.library.path=C:/Users/biril/AppData/Local/Programs/Webots/lib/controller/java " + controllerName);
        try {

            Process process = processBuilder.start();

            BufferedReader reader =
                    new BufferedReader(new InputStreamReader(process.getInputStream()));

            String line;
            while ((line = reader.readLine()) != null) {
                System.out.println(line);
            }

            int exitCode = process.waitFor();
            System.out.println("\nExited with error code : " + exitCode);

        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}