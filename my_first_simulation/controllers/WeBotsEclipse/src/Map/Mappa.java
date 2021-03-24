package Map;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Random;
import java.util.Set;

public class Mappa
{	
	public static final int EMPTY = 0;
	public static final int FULL = 1;
	public static final int GUARDIA = 2;
	public static final int LADRO = 3;

	private String Difficolta ;
	private int xDimInterna; 		//Dim Matrice
	private int yDimInterna;
	private double[] WeBotsXYMap ; 	// Quanto deve essere la mappa di WeBots
	private double WeBotsTile ; 	// Grandezza di una singola cella di WeBots
	private int dimSpawnGate; 		// grandezza delle porte dello spawn
	private int xAmpiezzaSpawn;
	private int[][] mappaSuperiore, mappaInferiore;
	private MappaInterna mappaInterna;
	
	public Mappa(String Difficolta, int xDimInterna, int yDimInterna, double WeBotsTile , double[] WeBotsXYMap, int xAmpiezzaSpawn, int dimSpawnGate)
	{
		// Questo costruttore � richiamato enlla classe SERVER
		this.Difficolta = Difficolta;
		this.xDimInterna = xDimInterna;
		this.yDimInterna = yDimInterna;
		this.WeBotsTile = WeBotsTile;
		this.WeBotsXYMap = WeBotsXYMap;
		this.xAmpiezzaSpawn = xAmpiezzaSpawn;
		this.dimSpawnGate = dimSpawnGate;
		
		this.mappaInterna = new MappaInterna(xDimInterna, yDimInterna, xAmpiezzaSpawn, dimSpawnGate, Difficolta);
		this.mappaSuperiore = new int[xAmpiezzaSpawn][yDimInterna];
		this.mappaInferiore = new int[xAmpiezzaSpawn][yDimInterna];
		
		for (int i = 0; i < xAmpiezzaSpawn; ++i)
			mappaSuperiore[i][0] = mappaSuperiore[i][yDimInterna - 1] = mappaInferiore[i][0] = mappaInferiore[i][yDimInterna - 1] = 1;
			
		for (int j = 0; j < yDimInterna; ++j)
			mappaSuperiore[0][j] = mappaInferiore[xAmpiezzaSpawn - 1][j] = 1;
	}
	
	public Mappa(int[][] mappa, int xAmpiezzaSpawn, int xDimInterna, int yDimInterna, double[] arrayXY)
	{
		// Questo costruttore � richiamato enlla classe STC_SEND_MAP
		// A questo costruttore passo gi� i dati ed i calcoli fatti dal server.
		this.WeBotsXYMap = arrayXY;
		this.xDimInterna = xDimInterna;
		this.yDimInterna = yDimInterna; 
		this.xAmpiezzaSpawn = xAmpiezzaSpawn;
		
		int col, row;
		col = row = 0;
		
		this.mappaSuperiore = new int[xAmpiezzaSpawn][yDimInterna];
		this.mappaInferiore = new int[xAmpiezzaSpawn][yDimInterna];
		
		
		for (int i = 0; i < xAmpiezzaSpawn; ++i, ++row)
		{
			col = 0;
			for (int j = 0; j < yDimInterna; ++j, ++col)
				mappaSuperiore[i][j] = mappa[row][col];
		}
		
		mappaInterna = new MappaInterna(xDimInterna, yDimInterna);
		
		for (int i = 0; i < xDimInterna; ++i, ++row)
		{
			col = 0;
			for (int j = 0; j < yDimInterna; ++j, ++col)
				mappaInterna.setValue(i,  j, mappa[row][col]);
		}
		
		for (int i = 0; i < xAmpiezzaSpawn; ++i, ++row)
		{
			col = 0;
			for (int j = 0; j < yDimInterna; ++j, ++col)
				mappaInferiore[i][j] = mappa[row][col];
		}
	}
	
	public Mappa(int xDim, int yDim)
	{
		/*
		this.xDim = xDim;
		this.yDim = yDim;
		this.mappa = new int[xDim][yDim];
		*/
	}
	
	public int getXSize()
	{
		return (xDimInterna + 2*xAmpiezzaSpawn);
	}
	
	public int getYSize()
	{
		return yDimInterna;
	}
	
	public double getWeBotsTile()
	{
		return WeBotsTile;
	}
	
	public double[] getWeBotsXYMap()
	{
		return WeBotsXYMap;
	}
	
	public int get(Point punto)
	{
		if (punto.getX() < xAmpiezzaSpawn)
			return mappaSuperiore[punto.getX()][punto.getY()];
		else if (punto.getX() >= (xDimInterna + xAmpiezzaSpawn))
			return mappaInferiore[punto.getX() - xDimInterna - xAmpiezzaSpawn][punto.getY()];
		else
			return mappaInterna.get(punto.getX() - xAmpiezzaSpawn, punto.getY());
	}
	
	@Override
	public String toString()
	{
		StringBuilder sb = new StringBuilder();
		
		for(int i=0; i < getXSize() ; i++)
		{
			for(int j=0; j < getYSize(); j++)
			{
				sb.append(" " + get(new Point(i, j)) + " ");
			}
			sb.append("\n");
		}
		
		return sb.toString();
	}

	

	public String getDifficolta() {
		return Difficolta;
	}
	
	//DA RIVEDERE
	public void setValue(Point punto, int value)
	{
		if (punto.getX() < xAmpiezzaSpawn)
			mappaSuperiore[punto.getX()][punto.getY()] = value;
		else if (punto.getX() > (xDimInterna + xAmpiezzaSpawn))
			mappaInferiore[punto.getX()][punto.getY()] = value;
		else
			mappaInterna.setValue(punto, value);
	}
	
	public Set<Point> getNeighbors(Point point)
	{
		Set<Point> set = new HashSet<>();
		
		int x = point.getX();
		int y = point.getY();
		
		Point north = x - 1 >= 0 && get(new Point(x - 1, y)) == 0 ? new Point(x - 1, y) : null;
		Point south = x + 1 < getXSize() && get(new Point(x + 1, y)) == 0 ? new Point(x + 1, y) : null;
		Point east = y + 1 < getYSize() && get(new Point(x, y + 1)) == 0 ? new Point(x, y + 1) : null;
		Point west = y - 1 >= 0 && get(new Point(x, y - 1)) == 0 ? new Point(x, y - 1) : null;
		
		if (north != null)
			set.add(north);
		if (south != null)
			set.add(south);
		if (east != null)
			set.add(east);
		if (west != null)
			set.add(west);
		
		return set;
	}

	public void GetSuperMap(int[][] SuperMappa)
	{
		for(int i=0; i < SuperMappa[0].length ; i++)
		{
			for(int j=0; j <  SuperMappa[1].length; j++)
			{
					System.out.print(" "+ SuperMappa[i][j] + " ");
			}
			System.out.println("\n");
		}
	}
	
	public int getXDimInterna()
	{
		return xDimInterna;
	}
	
	public int getYDimInterna()
	{
		return yDimInterna;
	}

	
	public int getXAmpiezzaSpawn() {
		return xAmpiezzaSpawn;
	}	
}
