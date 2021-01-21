import java.io.IOException;
import java.io.BufferedReader;
import java.io.InputStreamReader;

public class General
{
    public static void main(String args[]) throws IOException
    {
        ProcessBuilder processBuilder = new ProcessBuilder();
        processBuilder.command("/bin/bash", "-c", "cd ./GuardiaController/");

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

        ControllerExecutor guardiaExecutor = new ControllerExecutor("GuardiaController", "guardia");
        guardiaExecutor.start();

        ControllerExecutor ladroExecutor = new ControllerExecutor("LadroController", "ladro");
        processBuilder.command("/bin/bash", "-c", "cd ../LadroController/");

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

        ladroExecutor.start();
        

        //Runtime.getRuntime().exec("/bin/bash -c export WEBOTS_ROBOT_NAME=\"guardia\"");

        /*
        Runtime.getRuntime().exec("/bin/bash -c cd ./GuardiaController/");
        Runtime.getRuntime().exec("/bin/bash -c java -XstartOnFirstThread -classpath /Applications/Webots.app/lib/controller/java/Controller.jar:/Users/davide/Desktop/Università/Robotica/Progetto/Locale/my_first_simulation/controllers/GuardiaController/ -Djava.library.path=/Applications/Webots.app/lib/controller/java GuardiaController");
        System.out.println("Avviato guardia controller");

        Runtime.getRuntime().exec("/bin/bash -c export WEBOTS_ROBOT_NAME=\"ladro\"");
        Runtime.getRuntime().exec("/bin/bash -c cd ../LadroController/");
        Runtime.getRuntime().exec("/bin/bash -c java -XstartOnFirstThread -classpath /Applications/Webots.app/lib/controller/java/Controller.jar:/Users/davide/Desktop/Università/Robotica/Progetto/Locale/my_first_simulation/controllers/LadroController/ -Djava.library.path=/Applications/Webots.app/lib/controller/java LadroController");
        System.out.println("Avviato ladro controller");
        */
    }
}