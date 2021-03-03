package General;

import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousSocketChannel;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import Network.Packet;
import Network.Client.CTS_PEER_INFO;

public class ConnectionHandler extends Thread {
	
	private AsynchronousSocketChannel clientChannel;
	private Server server;
	
	public ConnectionHandler(Server server, AsynchronousSocketChannel clientChannel)
	{
		this.server = server;
		this.clientChannel = clientChannel;
		System.out.println("New client connected");

	}
	
	public void run()
	{
		while (true)
		{
			try
			{
		        if ((clientChannel != null) && (clientChannel.isOpen()))
		        {
	                ByteBuffer buffer = ByteBuffer.allocate(4);
	                Future<Integer> readResult = clientChannel.read(buffer);
	                readResult.get();
	                buffer.position(0);
	                
	                int sizeToAllocate = buffer.getInt();
	                buffer = ByteBuffer.allocate(sizeToAllocate);
	                readResult = clientChannel.read(buffer);
	                readResult.get();	                
	                buffer.position(0);
	               
	                parse(buffer);
	                
	                /*
	                Future<Integer> writeResult = clientChannel.write(buffer);
	     
	                // perform other computations
	     
	                writeResult.get();
	                buffer.clear();
	                */
		        }
			} catch (InterruptedException | ExecutionException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	
	private void parse(ByteBuffer buf)
	{
		Packet packet = new Packet(buf);
		
		switch (packet.getOpcode())
		{
			case Packet.CTS_PEER_INFO:
				{
					CTS_PEER_INFO cts_peer_info = new CTS_PEER_INFO(packet, buf);
					
					if (cts_peer_info.getType() == CTS_PEER_INFO.GUARDIA)
						server.addGuardia(clientChannel);
					else if (cts_peer_info.getType() == CTS_PEER_INFO.LADRO)
						server.addLadro(clientChannel);
				}
				break;
			default:
				System.out.println("Pacchetto sconosciuto ricevuto " + packet.getOpcode());
				break;
		}
	}

}
