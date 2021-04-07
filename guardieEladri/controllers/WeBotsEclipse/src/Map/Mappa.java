package Map;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

import General.AStarSearcher;

public class Mappa
{	
	public static final int EMPTY = 0;
	public static final int FULL = 1;
	public static final int GUARDIA = 2;
	public static final int LADRO = 3;

	private String Difficolta ;
	private int xDimInterna; 		//Dim Matrice
	private int yDimInterna;
	private double[] floorSize; 	// Quanto deve essere la mappa di WeBots
	private double spiazzamentoX, spiazzamentoY; 	// Grandezza di una singola cella di WeBots
	private int dimSpawnGate; 		// grandezza delle porte dello spawn
	private int xAmpiezzaSpawn;
	private int[][] mappaSuperiore, mappaInferiore;
	private MappaInterna mappaInterna;
	
	public Mappa(String Difficolta, int xDimInterna, int yDimInterna, int xAmpiezzaSpawn, int dimSpawnGate)
	{
		// Questo costruttore � richiamato nella classe SERVER
		this.Difficolta = Difficolta;
		this.xDimInterna = xDimInterna;
		this.yDimInterna = yDimInterna;
		this.xAmpiezzaSpawn = xAmpiezzaSpawn; 
        
        this.floorSize = new double[2];
        this.floorSize[0] = (3 + getYSize()) / 8;
        this.floorSize[1] = (1 + getXSize()) / 8;
        
		this.spiazzamentoX = 0.10 * ((float)getYSize() - 1) / 2;
		this.spiazzamentoY = 0.10 * ((float)getXSize() - 1) / 2;
		
		this.dimSpawnGate = dimSpawnGate;
		
		this.mappaSuperiore = new int[xAmpiezzaSpawn][yDimInterna];
		this.mappaInferiore = new int[xAmpiezzaSpawn][yDimInterna];
		
		for (int i = 0; i < xAmpiezzaSpawn; ++i)
			mappaSuperiore[i][0] = mappaSuperiore[i][yDimInterna - 1] = mappaInferiore[i][0] = mappaInferiore[i][yDimInterna - 1] = 1;
			
		for (int j = 0; j < yDimInterna; ++j)
			mappaSuperiore[0][j] = mappaInferiore[xAmpiezzaSpawn - 1][j] = 1;
		
		do
		{
			mappaInterna = new MappaInterna(this);
		} while(!isValid());
	}
	
	public Mappa(int xAmpiezzaSpawn, int xDimInterna, int yDimInterna, int dimSpawnGate)
	{
		this.xAmpiezzaSpawn = xAmpiezzaSpawn;
		this.xDimInterna = xDimInterna;
		this.yDimInterna = yDimInterna;
		this.dimSpawnGate = dimSpawnGate;
		this.mappaSuperiore = new int[xAmpiezzaSpawn][yDimInterna];
		this.mappaInferiore = new int[xAmpiezzaSpawn][yDimInterna];
		this.mappaInterna = new MappaInterna(xDimInterna, yDimInterna, dimSpawnGate);
		
		for (int i = 0; i < xAmpiezzaSpawn; ++i)
			mappaSuperiore[i][0] = mappaSuperiore[i][yDimInterna - 1] = mappaInferiore[i][0] = mappaInferiore[i][yDimInterna - 1] = 1;
			
		for (int j = 0; j < yDimInterna; ++j)
			mappaSuperiore[0][j] = mappaInferiore[xAmpiezzaSpawn - 1][j] = 1;
	}
	
	public Mappa(int[][] mappa, int xAmpiezzaSpawn, int xDimInterna, int yDimInterna, int dimSpawnGate, double[] floorSize, double spiazzamentoX, double spiazzamentoY)
	{
		// Questo costruttore � richiamato enlla classe STC_SEND_MAP
		// A questo costruttore passo gi� i dati ed i calcoli fatti dal server.
		this.floorSize = floorSize;
		this.xDimInterna = xDimInterna;
		this.yDimInterna = yDimInterna;
		this.dimSpawnGate = dimSpawnGate;
		this.xAmpiezzaSpawn = xAmpiezzaSpawn;
		this.spiazzamentoX = spiazzamentoX;
		this.spiazzamentoY = spiazzamentoY;
		
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
		
		mappaInterna = new MappaInterna(xDimInterna, yDimInterna, xAmpiezzaSpawn);
		
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

	private boolean isValid() 
	{
		Point lastPoint = new Point(((2*xAmpiezzaSpawn) + xDimInterna)-2, (yDimInterna-2) ) ;
		
		AStarSearcher aStarSearcher = new AStarSearcher(this);		
		ArrayList<Point> path = aStarSearcher.getPath(new Point(1,1),lastPoint);
		
		return (path != null);
	}
	
	public int getXSize()
	{
		return (xDimInterna + 2*xAmpiezzaSpawn);
	}
	
	public int getYSize()
	{
		return yDimInterna;
	}
	
	public double getSpiazzamentoX()
	{
		return spiazzamentoX;
	}
	
	public double getSpiazzamentoY()
	{
		return spiazzamentoY;
	}
	
	public double[] getFloorSize()
	{
		return floorSize;
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
	
	public void setValue(Point punto, int value)
	{
		if (punto.getX() < xAmpiezzaSpawn)
			mappaSuperiore[punto.getX()][punto.getY()] = value;
		else if (punto.getX() >= (xDimInterna + xAmpiezzaSpawn))
			mappaInferiore[punto.getX() - xDimInterna - xAmpiezzaSpawn][punto.getY()] = value;
		else
			mappaInterna.setValue(punto.getX() - xAmpiezzaSpawn, punto.getY(), value);
	}
	
	public Set<Point> getNeighbors(Point point)
	{
		Set<Point> set = new HashSet<>();
		
		int x = point.getX();
		int y = point.getY();
		
		Point north = x - 1 >= 0 && get(new Point(x - 1, y)) != FULL && get(new Point(x - 1, y)) != LADRO ? new Point(x - 1, y) : null;
		Point south = x + 1 < getXSize() && get(new Point(x + 1, y)) != FULL && get(new Point(x + 1, y)) != LADRO ? new Point(x + 1, y) : null;
		Point east = y + 1 < getYSize() && get(new Point(x, y + 1)) != FULL && get(new Point(x, y + 1)) != LADRO ? new Point(x, y + 1) : null;
		Point west = y - 1 >= 0 && get(new Point(x, y - 1)) != FULL && get(new Point(x, y - 1)) != LADRO ? new Point(x, y - 1) : null;
		
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
	
	public int getXDimInterna()
	{
		return xDimInterna;
	}
	
	public int getYDimInterna()
	{
		return yDimInterna;
	}

	
	public int getXAmpiezzaSpawn()
	{
		return xAmpiezzaSpawn;
	}

	public int getDimSpawnGate()
	{
		return dimSpawnGate;
	}

	public void setDimSpawnGate(int dimSpawnGate)
	{
		this.dimSpawnGate = dimSpawnGate;
	}

	public int getxAmpiezzaSpawn()
	{
		return xAmpiezzaSpawn;
	}

	public void setxAmpiezzaSpawn(int xAmpiezzaSpawn) {
		this.xAmpiezzaSpawn = xAmpiezzaSpawn;
	}

	public void setDifficolta(String difficolta) {
		Difficolta = difficolta;
	}	
}
