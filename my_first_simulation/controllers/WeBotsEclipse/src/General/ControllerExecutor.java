package General;


import java.io.IOException;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.lang.Thread;

public class ControllerExecutor extends Thread
{
    private String controllerName;
    private String robotName;
    private String webotsPath;
    private String projectPath;
    public boolean ready;

    public ControllerExecutor(String controllerName, String robotName, String webotsPath, String projectPath)
    {
        this.controllerName = controllerName;
        this.robotName = robotName;
        this.webotsPath = webotsPath;
        this.projectPath = projectPath;
        ready = false;
    }

    public void run()
    {
        ProcessBuilder processBuilder = new ProcessBuilder();

        //in windows
        String OS = System.getProperty("os.name");

        if (OS.equals(new String("Mac OS X")))
            processBuilder.command("/bin/bash", "-c", "export WEBOTS_ROBOT_NAME=\"" + robotName + "\"");
        else
            processBuilder.command("cmd.exe", "/c", "set WEBOTS_ROBOT_NAME=" + robotName);

        try {
            Process process = processBuilder.start();
            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));

            String line;
            while ((line = reader.readLine()) != null) {
                System.out.println(line);
            }

            int exitCode = process.waitFor();
            if(exitCode == 0)
            {
            	System.out.println(robotName + " : Variabile d'ambiente settata correttamente " );
            }
            
            reader.close();
            
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        
        //System.out.println("javaw.exe -Djava.library.path=" + webotsPath + "/lib/controller/java -Dfile.encoding=Cp1252 -classpath \"" + projectPath + "/my_first_simulation/controllers/WeBotsEclipse/bin;" + webotsPath + "/lib/controller/java/Controller.jar\" -XX:+ShowCodeDetailsInExceptionMessages " + controllerName + "." + controllerName);        

        if (OS.equals(new String("Mac OS X")))
            processBuilder.command("/bin/bash", "-c", "java -Djava.library.path=" + webotsPath + "/lib/controller/java -Dfile.encoding=UTF-8 -classpath " + projectPath + "/my_first_simulation/controllers/WeBotsEclipse/bin:" + webotsPath + "/lib/controller/java/Controller.jar Controllers." + controllerName);
        else
        	processBuilder.command("cmd.exe", "/c", "javaw.exe -Djava.library.path=" + webotsPath + "/lib/controller/java -Dfile.encoding=Cp1252 -classpath \"" + projectPath + "/my_first_simulation/controllers/WeBotsEclipse/bin;" + webotsPath + "/lib/controller/java/Controller.jar\" -XX:+ShowCodeDetailsInExceptionMessages Controllers." + controllerName);        
        try {
            Process process = processBuilder.start();
            ready = true;
            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));

            String line;
            while ((line = reader.readLine()) != null) {
                System.out.println(line);
            }

            int exitCode = process.waitFor();
            System.out.println("\n" + controllerName + ": Exited with error code : " + exitCode);
      
            reader.close();
            
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        
    }
}