package Map;

import java.util.ArrayList;
import java.util.Random;

@SuppressWarnings("all")
public class Mappa
{	
	String Difficolta ;
	int NumeroBlocchiPercorso;
	private static int x ; //Dim Matrice
	private static int y;
	private static int LimX,LimY = 1000;
	private int[][] mappa;
	double[] WeBotsXYMap ; // Quanto deve essere la mappa di WeBots
	private static double WeBotsTile ; // Grandezza di una singola cella di WeBots
	
	public Mappa(String Difficolta, int x, int y, double WeBotsTile , double[] WeBotsXYMap)
	{
		this.Difficolta = Difficolta;
		this.x = x;
		this.y = y;
		this.WeBotsTile = WeBotsTile;
		this.WeBotsXYMap = WeBotsXYMap;
		this.mappa = new int[x][y];
		CreateValidMapTest();
	}
	
	public Mappa(int[][] mappa, int xDim, int yDim, double[] arrayXY)
	{
		this.WeBotsXYMap = arrayXY;
		this.x = xDim;
		this.y = yDim;
		this.mappa = mappa;
		
	}
	
	public int getXSize()
	{
		return x;
	}
	
	public int getYSize()
	{
		return y;
	}
	
	public double getWeBotsTile()
	{
		return WeBotsTile;
	}
	
	public double[] getWeBotsXYMap()
	{
		return WeBotsXYMap;
	}
	
	public int get(int i, int j)
	{
		return mappa[i][j];
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

	public int[][] CreateValidMapTest()
	{
		// Solo per testing
				Random rand = new Random();
				  
				for(int i=0; i<x ; i++)
				{
					for(int j=0; j<y ; j++)
					{
						if( (rand.nextInt(10)+1) <= 13)
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
	
	
	public int[][] CreateValidMap()
	{
		// Solo per testing
				Random rand = new Random();
				  
				for(int i=0; i<=x ; i++)
				{
					for(int j=0; j<y ; j++)
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
}
