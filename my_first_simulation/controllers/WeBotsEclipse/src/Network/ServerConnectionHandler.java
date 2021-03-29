package Network;

import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousSocketChannel;
import java.util.ArrayList;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import General.SharedVariables;
import Map.Mappa;
import Network.Packet;
import Network.Packets.ClientToServer.CTS_PEER_INFO;
import Network.Packets.ClientToServer.CTS_GOAL_CHANGED;
import Network.Packets.ClientToServer.CTS_GOING_TO;
import Network.Packets.ClientToServer.CTS_LADRO_FOUND;
import Network.Packets.ClientToServer.CTS_NEW_GUARDIA_POS;
import Network.Packets.ClientToServer.CTS_OBSTACLE_IN_MAP;
import Network.Packets.ServerToClient.STC_SEND_MAP;
import Network.Packets.ServerToClient.STC_START_GUARDIE;

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
				CTS_OBSTACLE_IN_MAP cts_obstacle_in_map = new CTS_OBSTACLE_IN_MAP(packet, buf);				
				if(server.getGuardie().contains(packet.getSender()))
					sendToGuardie(cts_obstacle_in_map);
			}
			break;
			case Packet.CTS_GOING_TO:
			{
				CTS_GOING_TO cts_going_to = new CTS_GOING_TO(packet, buf);
				if (server.getGuardie().contains(packet.getSender()))
					sendToGuardie(cts_going_to);
				else if (server.getLadri().contains(packet.getSender()))
					sendToLadri(cts_going_to);
			}
			break;
			case Packet.CTS_NEW_GUARDIA_POS:
			{
				CTS_NEW_GUARDIA_POS cts_new_guardia_pos = new CTS_NEW_GUARDIA_POS(packet, buf);
				if (server.getGuardie().contains(packet.getSender()))
					sendToGuardie(cts_new_guardia_pos);
			}
			break;
			case Packet.CTS_GOAL_CHANGED:
			{
				CTS_GOAL_CHANGED cts_goal_changed = new CTS_GOAL_CHANGED(packet, buf);
				if (server.getGuardie().contains(packet.getSender()))
					sendToGuardie(cts_goal_changed);
			}
			break;
			case Packet.CTS_LADRO_FOUND:
			{
				CTS_LADRO_FOUND cts_ladro_found = new CTS_LADRO_FOUND(packet, buf);
				if (server.getGuardie().contains(packet.getSender()))
					sendToGuardie(cts_ladro_found);
			}
			break;
			case Packet.CTS_LADRO_HIDDEN:
			{
				server.incrementLadriHiddenReceived();
				if(server.getLadriHidden() == SharedVariables.getNumeroLadri())
					sendToGuardie(new STC_START_GUARDIE());
				
			}
			default:
				System.out.println("Server: Pacchetto sconosciuto ricevuto " + packet.getOpcode());
			break;
		}
	}
	
	private void sendToGuardie(Packet packet)
	{
		//System.out.println("Server: una guardia si è mossa da " + cts_new_guardia_pos.getBefore() + " in " + cts_new_guardia_pos.getAfter());
		ArrayList<AsynchronousSocketChannel> guardie = server.getGuardie();
		ByteBuffer buffer = packet.encode();
		for (int i = 0; i < guardie.size(); ++i)
		{
			if (guardie.get(i) == packet.getSender())
				continue;
		
			buffer = buffer.position(0);
			guardie.get(i).write(buffer);
		}
	}
	
	private void sendToLadri(Packet packet)
	{
		//System.out.println("Server: una guardia si è mossa da " + cts_new_guardia_pos.getBefore() + " in " + cts_new_guardia_pos.getAfter());
		ArrayList<AsynchronousSocketChannel> ladri = server.getLadri();
		ByteBuffer buffer = packet.encode();
		for (int i = 0; i < ladri.size(); ++i)
		{			
			if (ladri.get(i) == packet.getSender())
				continue;
		
			buffer = buffer.position(0);
			ladri.get(i).write(buffer);
		}
	}
}
