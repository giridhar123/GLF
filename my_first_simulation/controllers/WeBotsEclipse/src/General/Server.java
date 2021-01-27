package General;
import java.lang.Thread;
import java.io.*;
import java.net.*;
import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousServerSocketChannel;
import java.nio.channels.AsynchronousSocketChannel;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

public class Server extends Thread
{
    private final int TCP_PORT = 6868;
    AsynchronousServerSocketChannel server;

    public Server()
    {
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
            	AsynchronousSocketChannel clientChannel = future.get();
                System.out.println("New client connected");
                if ((clientChannel != null) && (clientChannel.isOpen())) {
                    /*while (true) {*/
                        ByteBuffer buffer = ByteBuffer.allocate(45);
                        Future<Integer> readResult  = clientChannel.read(buffer);
                        
                        // perform other computations
                        
                        System.out.println("Ho ricevuto " + readResult.get().shortValue());
                        
                        buffer.flip();
                        Future<Integer> writeResult = clientChannel.write(buffer);
             
                        // perform other computations
             
                        writeResult.get();
                        buffer.clear();
                    //}
                }
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
}