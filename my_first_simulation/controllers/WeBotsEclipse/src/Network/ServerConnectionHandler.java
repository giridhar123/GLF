package Network;

import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousSocketChannel;
import java.util.ArrayList;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import Map.Mappa;
import Network.Packet;
import Network.Packets.ClientToServer.CTS_PEER_INFO;
import Network.Packets.ClientToServer.CTS_GOING_TO;
import Network.Packets.ClientToServer.CTS_NEW_GUARDIA_POS;
import Network.Packets.ClientToServer.CTS_OBSTACLE_IN_MAP;
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
		ByteBuffer buffer;
		Future<Integer> readResult;
		
		while (true)
		{
			try
			{
		        if ((clientChannel != null) && (clientChannel.isOpen()))
		        {
	                buffer = ByteBuffer.allocate(4);
	                readResult = clientChannel.read(buffer);
	                readResult.get();
	                buffer.position(0);
	                int packetSize = buffer.getInt();
	                buffer = ByteBuffer.allocate(packetSize - 4);
	                readResult = clientChannel.read(buffer);
	                readResult.get();
	                buffer.position(0);
	               
	                parse(clientChannel, packetSize, buffer);
	                
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
	
	private void parse(AsynchronousSocketChannel sender, int packetSize, ByteBuffer buf)
	{
		Packet packet = new Packet(packetSize, buf, sender);
		ByteBuffer buffer = null;
		
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
					buffer = stc_send_map.encode();
			        clientChannel.write(buffer);
				}
			}
			break;
			case Packet.CTS_WORLD_READY:
				server.startControllers();
			break;
			case Packet.CTS_OBSTACLE_IN_MAP:
			{
				CTS_OBSTACLE_IN_MAP cts_update_map_point = new CTS_OBSTACLE_IN_MAP(packet, buf);				
				if(server.getGuardie().contains(packet.getSender()))
				{
					//System.out.println("Server: Ho ricevuto ostacolo in " + cts_update_map_point.getPoint());
					ArrayList<AsynchronousSocketChannel> guardie = server.getGuardie();
					for (int i = 0; i < guardie.size(); ++i)
					{
						if (guardie.get(i) == packet.getSender())
							continue;
					
						buffer = cts_update_map_point.encode();
						guardie.get(i).write(buffer);
					}
				}
			}
			break;
			case Packet.CTS_GOING_TO:
			{
				CTS_GOING_TO cts_going_to = new CTS_GOING_TO(packet, buf);
				if (server.getGuardie().contains(packet.getSender()))
				{
					//System.out.println("Server: La guardia vuole andare in " + cts_going_to.getPoint());
					ArrayList<AsynchronousSocketChannel> guardie = server.getGuardie();
					for (int i = 0; i < guardie.size(); ++i)
					{
						if (guardie.get(i) == packet.getSender())
							continue;
					
						buffer = cts_going_to.encode();
						guardie.get(i).write(buffer);
					}
				}
				else
				{
					//System.out.println("Server: Il ladro vuole andare in " + cts_going_to.getPoint());
					ArrayList<AsynchronousSocketChannel> ladri = server.getLadri();
					for (int i = 0; i < ladri.size(); ++i)
					{
						if (ladri.get(i) == packet.getSender())
							continue;
					
						buffer = cts_going_to.encode();
						ladri.get(i).write(buffer);
					}
				}
				
			}
			break;
			case Packet.CTS_NEW_GUARDIA_POS:
			{
				CTS_NEW_GUARDIA_POS cts_new_guardia_pos = new CTS_NEW_GUARDIA_POS(packet, buf);
				if (server.getGuardie().contains(packet.getSender()))
				{
					//System.out.println("Server: una guardia si è mossa da " + cts_new_guardia_pos.getBefore() + " in " + cts_new_guardia_pos.getAfter());
					ArrayList<AsynchronousSocketChannel> guardie = server.getGuardie();
					for (int i = 0; i < guardie.size(); ++i)
					{
						if (guardie.get(i) == packet.getSender())
							continue;
					
						buffer = cts_new_guardia_pos.encode();
						guardie.get(i).write(buffer);
					}
				}
			}
			break;
			default:
				System.out.println("Server: Pacchetto sconosciuto ricevuto " + packet.getOpcode());
			break;
		}
	}
}
