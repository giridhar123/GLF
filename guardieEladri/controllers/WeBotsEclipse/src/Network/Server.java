package Network;

import java.lang.Thread;
import java.io.*;
import java.net.*;
import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousServerSocketChannel;
import java.nio.channels.AsynchronousSocketChannel;
import java.util.ArrayList;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import General.ControllerExecutor;
import General.SharedVariables;
import Map.Mappa;
import Network.Packets.Packet;
import Network.Packets.ServerToClient.STC_SEND_MAP;

/*
 * Classe che implementa il comportamento lato server in un thread separato
 */

public class Server extends Thread
{
	private Mappa mappa;
	AsynchronousServerSocketChannel server;

	private int ladriHidden;
	ArrayList<AsynchronousSocketChannel> guardie;
	ArrayList<AsynchronousSocketChannel> ladri;
	
	private ArrayList<ControllerExecutor> controllers;

	public Server()
	{
		guardie = new ArrayList<>();
		ladri = new ArrayList<>();
		
		ladriHidden = 0;
		controllers = new ArrayList<>();
		
		try
		{
			server = AsynchronousServerSocketChannel.open();
			server.bind(new InetSocketAddress("127.0.0.1", SharedVariables.getTcpServerPort()));
		}
		catch (IOException exc)
		{
			System.out.println("Server: binding error");
		}
		
		int dimMappaInternaX = SharedVariables.getDimMappaInternaX();
        int dimMappaInternaY = SharedVariables.getDimMappaInternaY();        		
        int dimSpawnX = SharedVariables.getDimSpawnX();
        int dimSpawnGate = SharedVariables.getDimSpawnGate();
        String difficolta = SharedVariables.getDifficolta();
        
		mappa = new Mappa(difficolta, dimMappaInternaX, dimMappaInternaY, dimSpawnX, dimSpawnGate);
	}

	public void run()
	{
		while (true)
		{
			try
			{
				Future<AsynchronousSocketChannel> future = server.accept();
				ServerConnectionHandler connectionHandler = new ServerConnectionHandler(this, future.get());
				connectionHandler.start();
			}
			catch (InterruptedException | ExecutionException e)
			{
				System.out.println("Server: Errore durante la connessione con un client");
			}
		}
	}

	public void addGuardia(AsynchronousSocketChannel guardia)
	{
		guardie.add(guardia);
		STC_SEND_MAP stc_send_map = new STC_SEND_MAP(mappa);
		ByteBuffer buffer = stc_send_map.encode();
		guardia.write(buffer);
	}

	public void addLadro(AsynchronousSocketChannel ladro)
	{
		ladri.add(ladro);
		STC_SEND_MAP stc_send_map = new STC_SEND_MAP(mappa);
		ByteBuffer buffer = stc_send_map.encode();
		ladro.write(buffer);
	}
	
	public void startControllers()
	{
		startControllers("Guardia");
		startControllers("Ladro");
	}
	
	private void startControllers(String robotName)
	{
		String currentName = null;
		int n = robotName.compareTo(new String("Guardia")) == 0 ? SharedVariables.getNumeroGuardie() : SharedVariables.getNumeroLadri();
		
		for (int i = 0; i < n; ++i)
		{
			currentName = robotName.concat(Integer.toString(i));
			ControllerExecutor controller = new ControllerExecutor(robotName.concat("Controller"), currentName);
			controllers.add(controller);
			controller.start();
		}
	}

	public void incrementLadriHiddenReceived()
	{
		ladriHidden += 1;
	}
	
	public int getLadriHidden()
	{
		return ladriHidden;
	}
	
	public Mappa getMappa() { return mappa; }
	public ArrayList<AsynchronousSocketChannel> getLadri() { return ladri; }
	public ArrayList<AsynchronousSocketChannel> getGuardie() { return guardie; }
	
	/*
	 * Metodo per inviare un pacchetto ricevuto a tutte le guardie
	 * trane a quella che ha inviato il pacchetto
	 */
	public synchronized void sendToGuardie(Packet packet)
	{
		ByteBuffer buffer = packet.encode();
		for (int i = 0; i < guardie.size(); ++i)
		{
			if (guardie.get(i) == packet.getSender())
				continue;
		
			buffer = buffer.position(0);
			Future<Integer> pendingWrite = guardie.get(i).write(buffer);
			
			try {
				pendingWrite.get();
			} catch (InterruptedException | ExecutionException e) {
				System.out.println(packet.getOpcode() + ": errore durante la trasmissione del pacchetto " + packet.getOpcode());
			}
		}
	}
	
	/*
	 * Metodo per inviare un pacchetto ricevuto a tutte i ladri
	 * trane a quello che ha inviato il pacchetto
	 */
	public synchronized void sendToLadri(Packet packet) 
	{
		ByteBuffer buffer = packet.encode();
		for (int i = 0; i < ladri.size(); ++i)
		{			
			if (ladri.get(i) == packet.getSender())
				continue;
		
			buffer = buffer.position(0);
			Future<Integer> pendingWrite = ladri.get(i).write(buffer);
			
			try {
				pendingWrite.get();
			} catch (InterruptedException | ExecutionException e) {
				System.out.println(packet.getOpcode() + ": errore durante la trasmissione del pacchetto " + packet.getOpcode());
			}
		}
	}
}