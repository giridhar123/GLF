import java.lang.Thread;
import java.io.*;
import java.net.*;
import java.util.Date;

public class Server extends Thread
{
    private final int TCP_PORT = 6868;
    private ServerSocket serverSocket;

    public Server()
    {
        try
        {
            serverSocket = new ServerSocket(TCP_PORT);
        }
        catch (IOException exc)
        {
            exc.printStackTrace();
        }
    }

    public void run()
    {            
        while (true)
        {
            try
            {
                Socket socket = serverSocket.accept();
                System.out.println("New client connected");
                InputStream input = socket.getInputStream();
                BufferedReader reader = new BufferedReader(new InputStreamReader(input));
                String line = reader.readLine();    // reads a line of text
                System.out.println(line);
                socket.close();
            }
            catch (IOException exc)
            {
                exc.printStackTrace();
            }
        }
    }
}