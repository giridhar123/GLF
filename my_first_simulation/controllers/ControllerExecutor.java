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

        processBuilder.command("/bin/bash", "-c", "export WEBOTS_ROBOT_NAME=\"" + robotName + "\"");

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

        processBuilder.command("/bin/bash", "-c", "java -XstartOnFirstThread -classpath /Applications/Webots.app/lib/controller/java/Controller.jar:/Users/davide/Desktop/Università/Robotica/Progetto/Locale/my_first_simulation/controllers/" + controllerName + "/ -Djava.library.path=/Applications/Webots.app/lib/controller/java " + controllerName);

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