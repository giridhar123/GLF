package General;
import java.lang.Thread;
import java.io.*;
import java.net.*;
import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousServerSocketChannel;
import java.nio.channels.AsynchronousSocketChannel;
import java.util.ArrayList;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

public class Server extends Thread
{
    private final int TCP_PORT = 6868;
    AsynchronousServerSocketChannel server;
    
    ArrayList<AsynchronousSocketChannel> guardie;
    ArrayList<AsynchronousSocketChannel> ladri;

    public Server()
    {
    	guardie = new ArrayList<>();
    	ladri = new ArrayList<>();
        try
        {
        	server = AsynchronousServerSocketChannel.open();
        	server.bind(new InetSocketAddress("127.0.0.1", TCP_PORT));
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
            	Future<AsynchronousSocketChannel> future = server.accept();
            	ConnectionHandler connectionHandler = new ConnectionHandler(this, future.get());
            	connectionHandler.start();
            }
            catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (ExecutionException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
        }
    }
    
    public void addGuardia(AsynchronousSocketChannel guardia)
    {
    	System.out.println("aggiungo una guardia");
    	guardie.add(guardia);
    }
    
    public void addLadro(AsynchronousSocketChannel ladro)
    {
    	System.out.println("Aggiungo un ladro...");
    	ladri.add(ladro);
    }
}