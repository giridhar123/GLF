package Network;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousSocketChannel;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import General.SharedVariables;
import Map.Mappa;
import Network.Packets.ClientToServer.CTS_PEER_INFO;
import Network.Packets.Packet;
import Network.Packets.ClientToServer.CTS_GOAL_CHANGED;
import Network.Packets.ClientToServer.CTS_GOING_TO;
import Network.Packets.ClientToServer.CTS_LADRO_FOUND;
import Network.Packets.ClientToServer.CTS_NEW_GUARDIA_POS;
import Network.Packets.ClientToServer.CTS_OBSTACLE_IN_MAP;
import Network.Packets.ServerToClient.STC_SEND_MAP;

/*
 * Classe che gestisce la connessione client-to-server in un thread separato
 * si occupa della ricezione dei pacchetti e della chiamata ai metodi
 * dell'interfaccia che ne implementa il comportamento alla ricezione di un determinato pacchetto
 */

public class ClientConnectionHandler extends Thread{
	
	private AsynchronousSocketChannel channel;
	private InetSocketAddress hostAddress;
	private byte clientType;
	private Client client;
	
	public ClientConnectionHandler(byte clientType, Client client)
	{
		this.clientType = clientType;
		this.client = client;
				
		try
		{
			channel = AsynchronousSocketChannel.open();
			hostAddress = new InetSocketAddress("localhost", SharedVariables.getTcpServerPort());
		} catch (IOException e) {
			System.out.println("Client: Errore durante l'apertura del canale di comunicazione con il server");
		}        
	}
	
	public void run()
	{
		Future<Void> future;
		try
		{
			future = channel.connect(hostAddress);
			future.get();
		}
		catch (InterruptedException | ExecutionException e)
		{
			System.out.println("Client: errore durante la connessione con il server");
			return;
		}
		
			
		/*
		 * Primo pacchetto da inviare al server
		 * per identificare il client che si sta connettendo
		 */
		
		CTS_PEER_INFO cts_peer_info = new CTS_PEER_INFO(clientType);
		sendPacket(cts_peer_info);
			
		ByteBuffer buffer;
		while (true)
		{
			try {
				future.get();
				
				buffer = ByteBuffer.allocate(4);
                Future<Integer> readResult = channel.read(buffer);
                if (readResult.get() == -1)
                	continue;
                
                buffer.position(0);
                
                int packetSize = buffer.getInt();
                
                /*
                 * 4 byte li ho letti prima quindi alloco un buffer
                 * di dimensione pari al pacchetto in arrivo meno - 4
                 */
                
                buffer = ByteBuffer.allocate(packetSize - 4);
                readResult = channel.read(buffer);
                if (readResult.get() == -1)
                	continue;
                
                buffer.position(0);
               
                parse(packetSize, buffer, channel);
			}
			catch (InterruptedException | ExecutionException e)
			{
				System.out.println("Client terminato: Errore durante la lettura del buffer");
				return;
			}
		}
	}
	
	/*
	 * Metodo per identificare il tipo di pacchetto che è stato appena ricevuto
	 */
	
	private void parse(int packetSize, ByteBuffer buf, AsynchronousSocketChannel sender)
	{
		Packet packet = new Packet(packetSize, buf, sender);
		
		switch (packet.getOpcode())
		{
			case Packet.STC_SEND_MAP:
			{
				STC_SEND_MAP stc_send_map = new STC_SEND_MAP(packet, buf);
				Mappa mappa = stc_send_map.getMappa();
				client.onStcSendMapReceived(mappa);
			}
			break;
			case Packet.CTS_OBSTACLE_IN_MAP:
			{
				CTS_OBSTACLE_IN_MAP cts_update_map_point = new CTS_OBSTACLE_IN_MAP(packet, buf);
				client.onCtsObstacleInMapReceived(cts_update_map_point.getPoint());
			}
			break;
			case Packet.CTS_GOING_TO:
			{
				CTS_GOING_TO cts_going_to = new CTS_GOING_TO(packet, buf);
				client.onCtsGoingToReceived(cts_going_to.getPoint());
			}
			break;
			case Packet.CTS_NEW_GUARDIA_POS:
			{
				CTS_NEW_GUARDIA_POS cts_new_guardia_pos = new CTS_NEW_GUARDIA_POS(packet, buf);
				client.onCtsNewGuardiaPosReceived(cts_new_guardia_pos.getBefore(), cts_new_guardia_pos.getAfter());
			}
			break;
			case Packet.CTS_GOAL_CHANGED:
			{
				CTS_GOAL_CHANGED cts_goal_changed = new CTS_GOAL_CHANGED(packet, buf);
				client.onCtsGoalChangedReceived(cts_goal_changed.getOld(), cts_goal_changed.getNew());
			}
			break;
			case Packet.CTS_LADRO_FOUND:
			{
				CTS_LADRO_FOUND cts_ladro_found = new CTS_LADRO_FOUND(packet, buf);
				client.onCtsLadroFound(cts_ladro_found.getPoint());
			}
			break;
			case Packet.STC_START_GUARDIE:
			{
				client.onStcStartGuardie();
			}
			break;
			default:
				System.out.println("Client: Pacchetto sconosciuto ricevuto " + packet.getOpcode());
			break;
		}
	}
	
	/*
	 * Metodo per inviare un pacchetto al server
	 */
	public void sendPacket(Packet packet)
	{
		ByteBuffer buf = packet.encode();
		Future<Integer> writeResult = channel.write(buf);
		try
		{
			writeResult.get();
		}
		catch (InterruptedException | ExecutionException e)
		{
			System.out.println("Client - Errore durante l'invio del pacchetto: " + packet.getOpcode());
		}
	}
}
