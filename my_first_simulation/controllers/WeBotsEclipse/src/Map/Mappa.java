package Map;

import java.util.Random;

public class Mappa
{	
	String Difficolta ;
	int NumeroBlocchiPercorso;
	private int xDim; //Dim Matrice
	private int yDim;
	private int[][] mappa;
	private double[] WeBotsXYMap ; // Quanto deve essere la mappa di WeBots
	private double WeBotsTile ; // Grandezza di una singola cella di WeBots
	
	public Mappa(String Difficolta, int xDim, int yDim, double WeBotsTile , double[] WeBotsXYMap)
	{
		this.Difficolta = Difficolta;
		this.xDim = xDim;
		this.yDim = yDim;
		this.WeBotsTile = WeBotsTile;
		this.WeBotsXYMap = WeBotsXYMap;
		this.mappa = new int[xDim][yDim];
		CreateValidMapTest();
	}
	
	public Mappa(int[][] mappa, int xDim, int yDim, double[] arrayXY)
	{
		this.WeBotsXYMap = arrayXY;
		this.xDim = xDim;
		this.yDim = yDim;
		this.mappa = mappa;
		
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
		Random rand = new Random();
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
						
					}
				}
		return mappa ;
	}
	
	
	public int[][] CreateValidMap()
	{
		Random rand = new Random();
				  
				for(int i=0; i <= xDim; i++)
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
}
