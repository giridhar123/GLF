package Network;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousSocketChannel;
import java.util.concurrent.Future;

import com.cyberbotics.webots.controller.Robot;

import General.SharedVariables;
import Map.Mappa;
import Network.Client.CTS_PEER_INFO;
import Network.Server.STC_SEND_MAP;
import Robot.GenericRobot;

public class ClientConnectionHandler extends Thread{
	
	private AsynchronousSocketChannel channel;
	private InetSocketAddress hostAddress;
	private byte clientType;
	private GenericRobot robotClient;
	
	public ClientConnectionHandler(byte clientType, GenericRobot robotClient)
	{
		this.clientType = clientType;
		this.robotClient = robotClient;
				
		try {
			channel = AsynchronousSocketChannel.open();
			hostAddress = new InetSocketAddress("localhost", SharedVariables.TCP_PORT);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}        
	}
	
	public void run()
	{
		try {
			Future<Void> future = channel.connect(hostAddress);
			future.get();
			
			CTS_PEER_INFO packet = new CTS_PEER_INFO(clientType);
	        ByteBuffer buffer = packet.encode();
	        channel.write(buffer);
			
			while (true)
			{
				future.get();
				
				buffer = ByteBuffer.allocate(4);
                Future<Integer> readResult = channel.read(buffer);
                readResult.get();
                buffer.position(0);
                
                int sizeToAllocate = buffer.getInt();
                buffer = ByteBuffer.allocate(sizeToAllocate);
                readResult = channel.read(buffer);
                readResult.get();	                
                buffer.position(0);
               
                parse(buffer);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	private void parse(ByteBuffer buf)
	{
		Packet packet = new Packet(buf);
		
		switch (packet.getOpcode())
		{
			case Packet.STC_SEND_MAP:
			{
				STC_SEND_MAP stc_send_map = new STC_SEND_MAP(packet, buf);
				Mappa mappa = stc_send_map.getMappa();
				robotClient.onStcSendMapReceived(mappa);
			}
			break;
			default:
				System.out.println("Pacchetto sconosciuto ricevuto " + packet.getOpcode());
				break;
		}
	}

}
