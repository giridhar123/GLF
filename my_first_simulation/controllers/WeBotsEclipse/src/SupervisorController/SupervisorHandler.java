package SupervisorController;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousSocketChannel;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import Map.Mappa;
import Network.Packet;
import Network.Client.CTS_PEER_INFO;
import Network.Server.STC_SEND_MAP;

public class SupervisorHandler extends Thread
{

	private final int TCP_PORT = 6868;
	private MySupervisor supervisor;
	private AsynchronousSocketChannel channel;
	private InetSocketAddress hostAddress;
	
	public SupervisorHandler(MySupervisor supervisor) {
		this.supervisor = supervisor;
		try {
			channel = AsynchronousSocketChannel.open();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        this.hostAddress = new InetSocketAddress("localhost", TCP_PORT);
	}	
	
	public void run() 
	{
		try {
			Future<Void> future = channel.connect(hostAddress);
			future.get();
		
			CTS_PEER_INFO packet = new CTS_PEER_INFO(CTS_PEER_INFO.SUPERVISOR);
	        ByteBuffer buffer = packet.encode();
	        Future<Integer> writeResult = channel.write(buffer);
        
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
		} catch (InterruptedException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (ExecutionException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
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
				supervisor.spawnaMappa(mappa);
			}
			break;
			default:
				System.out.println("Pacchetto sconosciuto ricevuto " + packet.getOpcode());
				break;
		}
	}
}
