package Network;

import java.lang.Thread;
import java.io.*;
import java.net.*;
import java.nio.channels.AsynchronousServerSocketChannel;
import java.nio.channels.AsynchronousSocketChannel;
import java.util.ArrayList;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import General.SharedVariables;
import Map.Mappa;

public class Server extends Thread {
	private Mappa mappa;
	AsynchronousServerSocketChannel server;

	ArrayList<AsynchronousSocketChannel> guardie;
	ArrayList<AsynchronousSocketChannel> ladri;

	public Server() {
		guardie = new ArrayList<>();
		ladri = new ArrayList<>();
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
		int DimMapX = 20; // Dimensione Matrice in x
		int DimMapY = 20; // Dimensione Matrice in y
		double WeBotsTile = 6.95; // Dimensione della singola cella di WeBots
		double[] WeBotsXYMap = { 8.0, 8.0 }; // Dimensione della mappa(il campo) WebBots

		mappa = new Mappa("difficile", DimMapX, DimMapY, WeBotsTile, WeBotsXYMap);

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
	}

	public void addGuardia(AsynchronousSocketChannel guardia)
	{
		System.out.println("aggiungo una guardia");
		guardie.add(guardia);
	}

	public void addLadro(AsynchronousSocketChannel ladro)
	{
		System.out.println("Aggiungo un ladro");
		ladri.add(ladro);
	}

	public Mappa getMappa() { return mappa; }
	public ArrayList<AsynchronousSocketChannel> getLadri() { return ladri; }
	public ArrayList<AsynchronousSocketChannel> getGuardie() { return guardie; }
}