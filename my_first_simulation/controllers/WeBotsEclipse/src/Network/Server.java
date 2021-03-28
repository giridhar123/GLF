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
import Network.Packets.ServerToClient.STC_SEND_MAP;

public class Server extends Thread
{
	private Mappa mappa;
	AsynchronousServerSocketChannel server;

	ArrayList<AsynchronousSocketChannel> guardie;
	ArrayList<AsynchronousSocketChannel> ladri;
	
	private ArrayList<ControllerExecutor> controllers;

	public Server() {
		guardie = new ArrayList<>();
		ladri = new ArrayList<>();
		
		controllers = new ArrayList<>();
		
		try
		{
			server = AsynchronousServerSocketChannel.open();
			server.bind(new InetSocketAddress("127.0.0.1", SharedVariables.getTcpServerPort()));
		}
		catch (IOException exc)
		{
			exc.printStackTrace();
		}
	}

	public void run() {
		// Creazione dell'oggetto che mi genera la mappa
		// Tutti questi valori sono da mettere in un file config.txt, sarebbe molto piu comodo.
		int DimMapX = 10; // Dimensione Matrice in x
		int DimMapY = 10; // Dimensione Matrice in y
		double WeBotsTile = 1.0; // Dimensione della singola cella di WeBots
		double[] WeBotsXYMap = { 8.0, 8.0 }; // Dimensione della mappa(il campo) WebBots
		int xDimSpawn = 3; // Dimensione dello spawn nella dimensione x. 
		int SpawnPort = 3; // grandezza della porta
		
		mappa = new Mappa("meow", DimMapX, DimMapY, WeBotsTile, WeBotsXYMap,xDimSpawn,SpawnPort);

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
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		/*
		for (int i = 0; i < executors.length; ++i)
		{
			if (executors[i] == null)
				continue;
			
			executors[i].join();
		}
		*/
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

	public Mappa getMappa() { return mappa; }
	public ArrayList<AsynchronousSocketChannel> getLadri() { return ladri; }
	public ArrayList<AsynchronousSocketChannel> getGuardie() { return guardie; }
}