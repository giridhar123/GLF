package General;

/*
 * Classe utilizzata per avviare un controllore in un Thread separato
 */

import java.io.IOException;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.lang.Thread;

public class ControllerExecutor extends Thread
{
    private String controllerName;
    private String robotName;
    public boolean ready;

    public ControllerExecutor(String controllerName, String robotName)
    {
        this.controllerName = controllerName;
        this.robotName = robotName;
        ready = false;
    }

    public void run()
    {
        ProcessBuilder processBuilder = new ProcessBuilder();

        String webotsPath = SharedVariables.getWebotsPath();
        String projectPath = SharedVariables.getProjectPath();
        int tcp_port = SharedVariables.getTcpServerPort();
        int time_step = SharedVariables.getTimeStep();
        int numeroGuardie = SharedVariables.getNumeroGuardie();
        int numeroLadri = SharedVariables.getNumeroLadri();
        
        String OS = System.getProperty("os.name");
        if (OS.equals(new String("Mac OS X")))
            processBuilder.command("/bin/bash", "-c", "export WEBOTS_ROBOT_NAME=" + robotName + "\n java -Djava.library.path=" + webotsPath + "/lib/controller/java -Dfile.encoding=UTF-8 -classpath " + projectPath + "/guardieEladri/controllers/WeBotsEclipse/bin:" + webotsPath + "/lib/controller/java/Controller.jar Controllers." + controllerName + " " + webotsPath + " " + projectPath + " " + tcp_port + " " + time_step + " " + numeroGuardie + " " + numeroLadri);
        else
        	processBuilder.command("cmd.exe", "/c", "set WEBOTS_ROBOT_NAME=" + robotName + "&javaw.exe -Djava.library.path=" + webotsPath + "/lib/controller/java -Dfile.encoding=Cp1252 -classpath \"" + projectPath + "/guardieEladri/controllers/WeBotsEclipse/bin;" + webotsPath + "/lib/controller/java/Controller.jar\" -XX:+ShowCodeDetailsInExceptionMessages Controllers." + controllerName + " " + webotsPath + " " + projectPath + " " + tcp_port + " " + time_step + " " + numeroGuardie + " " + numeroLadri);
        try {
            Process process = processBuilder.start();
            BufferedReader outputStream = new BufferedReader(new InputStreamReader(process.getInputStream()));
            BufferedReader errorStream = new BufferedReader(new InputStreamReader(process.getErrorStream()));
            
            StreamPrinter outputPrinter = new StreamPrinter(outputStream);
            StreamPrinter errorPrinter = new StreamPrinter(errorStream);
            
            outputPrinter.start();
            errorPrinter.start();

            int exitCode = process.waitFor();
            System.out.println("\n" + controllerName + ": terminato con codice: " + exitCode);
            
            outputPrinter.join();
            errorPrinter.join();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}