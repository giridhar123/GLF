package Map;

import java.util.ArrayList;
import java.util.Random;

@SuppressWarnings("all")
public class MapGenerator {
	
		String Difficolta ;
		int NumeroBlocchiPercorso;
		private static int x ; //Dim Matrice
		private static int y;
		private static int LimX,LimY = 1000;

		
		//ArrayList<Object> TipologiaBlocchi = new ArrayList<>();

		
		public MapGenerator(String Difficolta, int NBP, int x, int y)
		{
			this.Difficolta = Difficolta;
			this.NumeroBlocchiPercorso = NBP;
			this.x = x;
			this.y = y;

		}
		// Fondamentalmente questa classe mi restituisce a SupervisorController una matrice pseudo-randomica valida.
		public int[][] Start() 
		{
			int[][] Mappa = new int[x][y];

			// Solo per testing
			Random rand = new Random();
			  System.out.println(x);
			  System.out.println(y);
			  
			  for(int i=0; i<x ; i++)
			  {
				 for(int j=0; j<y ; j++)
				 {
					 if( (rand.nextInt(10)+1) <= 5)
					 { 
						 Mappa[i][j] = 0 ;
					 }
					 else
					 {
						 Mappa[i][j] = 1 ;
					 }
				}
			  }
			  PrintMap(Mappa);

		return Mappa;
		}
	
	private static void PrintMap(int[][] Mappa)
	{

		  for(int i=0; i<Mappa[0].length ; i++)
		  {
			 for(int j=0; j<Mappa[1].length ; j++)
			 {
				System.out.print(Mappa[i][j]);
		  	 }
		  }
	}
}
