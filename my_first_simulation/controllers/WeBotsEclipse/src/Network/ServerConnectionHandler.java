package Network;

import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousSocketChannel;
import java.util.ArrayList;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import Map.Mappa;
import Network.Packet;
import Network.Packets.ClientToServer.CTS_PEER_INFO;
import Network.Packets.ClientToServer.CTS_UPDATE_MAP_POINT;
import Network.Packets.ServerToClient.STC_SEND_MAP;

public class ServerConnectionHandler extends Thread {
	
	private AsynchronousSocketChannel clientChannel;
	private Server server;
	
	public ServerConnectionHandler(Server server, AsynchronousSocketChannel clientChannel)
	{
		this.server = server;
		this.clientChannel = clientChannel;
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
	               
	                parse(sizeToAllocate, buffer);
	                
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
				return ; // Altrimenti va in loop quando si presenta IOException
			}
		}
	}
	
	private void parse(int sizeToAllocate, ByteBuffer buf)
	{
		Packet packet = new Packet(sizeToAllocate, buf);
		
		switch (packet.getOpcode())
		{
			case Packet.CTS_PEER_INFO:
			{
				CTS_PEER_INFO cts_peer_info = new CTS_PEER_INFO(packet, buf);
				
				if (cts_peer_info.getType() == CTS_PEER_INFO.GUARDIA)
					server.addGuardia(clientChannel);
				else if (cts_peer_info.getType() == CTS_PEER_INFO.LADRO)
					server.addLadro(clientChannel);
				else if (cts_peer_info.getType() == CTS_PEER_INFO.SUPERVISOR)
				{
					Mappa mappa = server.getMappa();
					STC_SEND_MAP stc_send_map = new STC_SEND_MAP(mappa);
					ByteBuffer buffer = stc_send_map.encode();
			        clientChannel.write(buffer);
				}
			}
			break;
			case Packet.CTS_WORLD_READY:
			{
				STC_SEND_MAP stc_send_map = new STC_SEND_MAP(server.getMappa());
				
				ByteBuffer buffer = null;
				
				ArrayList<AsynchronousSocketChannel> clients = server.getLadri();
				for (int i = 0; i < clients.size(); ++i)
				{
					buffer = stc_send_map.encode();
					clients.get(i).write(buffer);
				}
				
				clients = server.getGuardie();
				for (int i = 0; i < clients.size(); ++i)
				{
					buffer = stc_send_map.encode();
					clients.get(i).write(buffer);
				}
			}
			break;
			case Packet.CTS_UPDATE_MAP_POINT:
			{
				CTS_UPDATE_MAP_POINT cts_update_map_point = new CTS_UPDATE_MAP_POINT(packet, buf);
				
				System.out.println("Server: Ho ricevuto ostacolo in " + cts_update_map_point.getX() + " " + cts_update_map_point.getY());
				
				ByteBuffer buffer = null;
				
				ArrayList<AsynchronousSocketChannel> guardie = server.getGuardie();
				for (int i = 0; i < guardie.size(); ++i)
				{
					buffer = cts_update_map_point.encode();
					guardie.get(i).write(buffer);
				}
			}
			break;
			default:
				System.out.println("Pacchetto sconosciuto ricevuto " + packet.getOpcode());
			break;
		}
	}
}
