package Map;

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
	private int xDim; //Dim Matrice
	private int yDim;
	private int[][] mappa;
	private double[] WeBotsXYMap ; // Quanto deve essere la mappa di WeBots
	private double WeBotsTile ; // Grandezza di una singola cella di WeBots
	private int SpawnPort; // grandezza delle porte dello spawn
	private int xDimSpawn;

	public Mappa(String Difficolta, int xDim, int yDim, double WeBotsTile , double[] WeBotsXYMap,int xDimSpawn,int SpawnPort)
	{
		// Questo costruttore � richiamato enlla classe SERVER
		this.Difficolta = Difficolta;
		this.xDim = xDim;
		this.yDim = yDim;
		this.WeBotsTile = WeBotsTile;
		this.WeBotsXYMap = WeBotsXYMap;
		this.mappa = new int[xDim][yDim];
		this.xDimSpawn = xDimSpawn;
		this.SpawnPort = SpawnPort;
		this.mappa= CreateMap(CreateMapClosed()); // crea la mappa chiusa agli estremmi
		
	}
	
	
	public Mappa(int[][] mappa, int xDim, int yDim, double[] arrayXY)
	{
		// Questo costruttore � richiamato enlla classe STC_SEND_MAP
		// A questo costruttore passo gi� i dati ed i calcoli fatti dal server.
		this.WeBotsXYMap = arrayXY;
		this.xDim = xDim;
		this.yDim = yDim;
		this.mappa = mappa;	
	}
	
	public Mappa(int xDim, int yDim)
	{
		this.xDim = xDim;
		this.yDim = yDim;
		this.mappa = new int[xDim][yDim];
	}
	
	public int getXSize()
	{
		return xDim;
	}
	
	public int getYSize()
	{
		return yDim;
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
		return mappa[punto.getX()][punto.getY()];
	}
	
	public int[][] getMap()
	{
		return this.mappa;
		
	}
	
	@Override
	public String toString()
	{
		StringBuilder sb = new StringBuilder();
		
		for(int i=0; i < getXSize() ; i++)
		{
			for(int j=0; j < getYSize(); j++)
			{
				sb.append(" " + mappa[i][j] + " ");
			}
			sb.append("\n");
		}
		
		return sb.toString();
	}

	public int[][] CreateMapClosed()
	{
			int min = 0;
			int max = mappa[1].length-1;
			
			for(int i=0; i < xDim; i++)
				{ 
					for(int j=0; j < yDim; j++)
					{		
						if(i==min || i==max || j==min || j==max )
						{
							this.mappa[i][j] = 1 ;
						}
						else
						{
							this.mappa[i][j] = 0 ;
						}
						
						if( (i == 0 && (j > (yDim/2)-SpawnPort && j < (yDim/2)+SpawnPort) ) || (i==max && (j > yDim/2-SpawnPort && j < yDim/2+SpawnPort) ) )
						{
							this.mappa[i][j] = 0 ;
						}
						
					}
				}
		//System.out.print(" x " + mappa[0].length + " y " + mappa[1].length + " x " + " " + xDim + " y " + yDim);
		return mappa ;
	}
	
	public int[][] CreateValidMap()
	{
		Random rand = new Random();
				  
				for(int i=0; i < xDim; i++)
				{
					for(int j=0; j < yDim; j++)
					{
						if( (rand.nextInt(10)+1) <= 5)
						{ 
							this.mappa[i][j] = 0 ;
						}
						else
						{
							this.mappa[i][j] = 1 ;
						}
					}
				}
		return mappa ;
	}
	
	public int[][] CreateMap(int[][] map)
	{
		TestingMap TM = new TestingMap();
		return TM.CreateMap(this);
		
	}

	public String getDifficolta() {
		return Difficolta;
	}
	
	public void setValue(Point punto, int value)
	{
		this.mappa[punto.getX()][punto.getY()] = value;
	}
	
	public Set<Point> getNeighbors(Point point)
	{
		Set<Point> set = new HashSet<>();
		
		int x = point.getX();
		int y = point.getY();
		
		Point north = x - 1 >= 0 && mappa[x - 1][y] == 0 ? new Point(x - 1, y) : null;
		Point south = x + 1 < getXSize() && mappa[x + 1][y] == 0 ? new Point(x + 1, y) : null;
		Point east = y + 1 < getYSize() && mappa[x][y + 1] == 0 ? new Point(x, y + 1) : null;
		Point west = y - 1 >= 0 && mappa[x][y - 1] == 0 ? new Point(x, y - 1) : null;
		
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


	public int getxDimSpawn() {
		return xDimSpawn;
	}
	
}
