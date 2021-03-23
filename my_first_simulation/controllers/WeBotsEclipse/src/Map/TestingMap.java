package Map;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class TestingMap {
	public static List<Integer> AL = new ArrayList<>();

	 // Da prendere dal server in base alla difficoltà
	
		public int[][] CreateMap(Mappa mappa)
	    {
			// 1 sopra 2 sotto 3 sinistra 4 destra  come codici usati dopo.
			int[][] map = mappa.getMap();
			String Difficolta = mappa.getDifficolta() ;
			int DimMatrix = map[0].length*map[1].length; // Dimensione matrice
			int dim = map[0].length ; // Dimensione della colonna ( poi devo modificare x e y )
			int RandomIndex = 0 ; // Indice Random preso dal pool all'interno della lista AL
			int[] MatrixIndexPoint = {0,0}; // Equivalente della posizione {x,y} in map[RandomIndex] Indice Della Matrice
			int NumeroCubi = 0; // Da aggiornare con i dati sperimentali in base alla dimensione della mappa
			
			switch (Difficolta)
			{
				case "facile" :
				{
				NumeroCubi = 55; 
				break;
				}
				case "normale" :
				{
				NumeroCubi = 250;
				break;
				}
				case "difficile" :
				{
				NumeroCubi = 400;
				break;
				}
				case "meow" :
				{
				NumeroCubi = 1000;
				break;
				}
				default :
				{
					NumeroCubi = 100;
					break;
				}
			
			}
		
		// Metto all'interno di AL, tutti i valori della matrice
    	for(int i = 0 ; i < DimMatrix ; i++)
    		AL.add(i); // Qui ci sono tutti i valori della matrice, e.s 1600 dim ho i valori { 0,1599}

    	for( int i = 0 ; i < NumeroCubi ; i++ )
    	{
    		// Prendo un valore random 
	    	RandomIndex = (int) Math.floor(Math.random() * (AL.size()-1) ); // Valore random preso tra 0 e AL.size-1 || 0 _ 1599 alla prima iterata , 0_1598 alla seconda etc..
	    	map = TakePattern(MatrixIndexPoint,dim, map, RandomIndex);
    	}
    	
    	map[15][15] = map[4][4] = 0;
    	
		return map;
    }
 

	private int[][] TakePattern(int[] MatrixIndexPoint,int dim, int[][] map,int RandomIndex) 
	{
		MatrixIndexPoint = PointToIndex(AL.get( RandomIndex ),dim); // Prendo l'equivalente di quel valore come [x],[y]
		map = ControlloMatrice(QuattroAdiacenza(MatrixIndexPoint,map), map); // primo punto ovunque
		int RandomIndex2 = 0;
		int RandomIndex3 = 0;

		// Scelgo randomicamente il pattern
		Random rand = new Random();
		int random = rand.nextInt(4)+1;

		switch(random) 
		{
		  case 1: // 3dx 
		  { 
			  //L'elemento a destra è quello a RandomIndex attuale in quanto la lista 
			  RandomIndex2 = (RandomIndex+1)%(AL.size()-1);
			  MatrixIndexPoint = PointToIndex(AL.get(RandomIndex2),dim); //secondo punto solo dx,giu,su
			  map = ControlloMatricePrimoPattern(QuattroAdiacenza(MatrixIndexPoint,map), map);
			  
			  RandomIndex3 = (RandomIndex+2)%(AL.size()-1);
			  MatrixIndexPoint = PointToIndex(AL.get(RandomIndex3),dim); // terzo punto solo dx,giu,su
			  map = ControlloMatricePrimoPattern(QuattroAdiacenza(MatrixIndexPoint,map), map);

			  // Il ragionamento ed il problema sta nella cancellazione dell'elemento.
			  // Se io cancello l'elemento 90, il prossimo elemento alla posizione 90 sarà 91.
			  // Questo significa che una volta evalutato l'elemento alla posizione 91 dovrò eliminare quello alla posizione precedente del 91, ovvero 90 per cancellare l'elemento 91.
			  // Ho dei problemi quando ho meno di 3 elementi all'interno dell'array, pertanto ho preferito mettere penultimo elemento ed una condizione aggiuntiva che sarebbe quella che AL deve avere + di tre elementi.
			  
			  // Devo levare tre elementi, quelli alla posizione x,x+1,x+2
			  if (  AL.size()-1 > 3 )
			  {
				  if(RandomIndex != AL.size()-2)
				  {
				  AL.remove(RandomIndex);
				  
				  if(RandomIndex2 != AL.size()-2)
				  	{
					  AL.remove(RandomIndex); 
					  
					  if(RandomIndex3 != AL.size()-2)
					  {
						  AL.remove(RandomIndex);
					  }
				  	}
				  }
			  }
		   break;		    
		  }
		  
		  case 2: // 3sx
		  {
			  RandomIndex2 = Math.abs((RandomIndex-1)%(AL.size()-1));
			  MatrixIndexPoint = PointToIndex(AL.get(RandomIndex),dim); //secondo punto solo sx,giu,su
			  map = ControlloMatriceSecondoPattern(QuattroAdiacenza(MatrixIndexPoint,map), map);
			  
			  RandomIndex3 = Math.abs(RandomIndex-2)%(AL.size()-1);
			  MatrixIndexPoint = PointToIndex(AL.get(RandomIndex),dim); // terzo punto solo sx,giu,su
			  map = ControlloMatriceSecondoPattern(QuattroAdiacenza(MatrixIndexPoint,map), map);
			  
			  // Devo levare 3 elementi, quelli alla posizione x, x-1 , x-2 
			  if (  AL.size()-1 > 4 )
			  {
				  if(RandomIndex3 != AL.get(0) )
				  {
					 AL.remove(RandomIndex3);
				  
				  if(RandomIndex2 != AL.get(0) )
				  	{
					  AL.remove(RandomIndex3);
					  
					  if(RandomIndex != AL.get(0) )
					  {
						 AL.remove(RandomIndex3);
					  }
				  	}
				  }
			  }
			break;		  
		  }
		  
		  case 3: // 3dw
		  { 
			  RandomIndex2 = (RandomIndex+map[0].length)%(AL.size()-1);
			  MatrixIndexPoint = PointToIndex(AL.get(RandomIndex),dim); // terzo punto solo sx,giu,su
			  map = ControlloMatriceTerzoPattern(QuattroAdiacenza(MatrixIndexPoint,map), map);
		  
			  RandomIndex3 = (RandomIndex2+map[0].length)%(AL.size()-1);
			  MatrixIndexPoint = PointToIndex(AL.get(RandomIndex),dim); // terzo punto solo sx,giu,su
			  map = ControlloMatriceTerzoPattern(QuattroAdiacenza(MatrixIndexPoint,map), map);
			  
			  // Verifico che ci sono piu di quattro elementi ( per sicurezza ), se ci sono controllo che il valore non è all'inizio o alla fine del pool in modo da non creare problemi i ncaso di eliminazione.
			  if (  AL.size()-1 > 4 )
			  {
				  if(RandomIndex3 < (AL.size() - 2) && RandomIndex3 != AL.get(0) )
				  { AL.remove(RandomIndex3); }
				  if(RandomIndex2 < (AL.size() - 2) && RandomIndex2 != AL.get(0) )
				  { AL.remove(RandomIndex2 );}
				  if(RandomIndex < (AL.size() - 2) && RandomIndex != AL.get(0) )
				  { AL.remove(RandomIndex); }
			  }
		    break;
		  }
		  
		  case 4: // 3up
		  { 
			  RandomIndex2 = Math.abs(RandomIndex+map[0].length)%(AL.size()-1);
			  MatrixIndexPoint = PointToIndex(AL.get(RandomIndex),dim); // terzo punto solo sx,giu,su
			  map = ControlloMatriceTerzoPattern(QuattroAdiacenza(MatrixIndexPoint,map), map);
		  
			  RandomIndex3 = Math.abs(RandomIndex2+map[0].length)%(AL.size()-1);
			  MatrixIndexPoint = PointToIndex(AL.get(RandomIndex),dim); // terzo punto solo sx,giu,su
			  map = ControlloMatriceTerzoPattern(QuattroAdiacenza(MatrixIndexPoint,map), map);
			  
			  // Verifico che ci sono piu di quattro elementi ( per sicurezza ), se ci sono controllo che il valore non è all'inizio o alla fine del pool in modo da non creare problemi i ncaso di eliminazione.
			  if (  AL.size()-1 > 4 )
			  {
				  if( RandomIndex3 < (AL.size() - 2 ) && RandomIndex3 != AL.get(0) )
				  { AL.remove(RandomIndex3); }
				  
				  if( RandomIndex2 < (AL.size() - 2 ) && RandomIndex2 != AL.get(0) )
				  { AL.remove(RandomIndex2 ); }
				  
				  if( RandomIndex < (AL.size() - 2 ) && RandomIndex != AL.get(0) )
				  { AL.remove(RandomIndex); }
			  }
		    break;
		  }
		  
		  default:
		  {
			  System.out.println("default");
			  break;
		  }
		}
		return map;
}


 	private int[] PointToIndex(int RandomIndex,int dim)
 	{
 		int[] IndexArray = new int[2];
 		
 		IndexArray[0] = ((int) RandomIndex/(dim)); // x
 		IndexArray[1] =  (RandomIndex%(dim)); // y

 		return IndexArray;
 	}
  
	
 	private ArrayList<int[]>  QuattroAdiacenza(int[] index, int[][] map) 
	{	
		ArrayList<int[]> ArrayList = new ArrayList<int[]>();
		int[] error = { -1 , -1 };

		if( (index[0] > 0 && index[0] < map[0].length-1 )) // se non sono quelli vicini al bordo
		{
			if(  (index[1] > 0 && index[1] < map[1].length-1  ) )
			{
				//Da verificare che i valori abbiamo gli intorni, altrimenti vediamo chje fare
				int[] sopra = { index[0]-1  , index[1] };
				int[] sotto = { index[0]+1  , index[1] };
				int[] sinistra = { index[0]  , index[1]-1 };
				int[] destra = { index[0]  , index[1]+1 };

				   ArrayList.add(index);
				   ArrayList.add(sopra);
				   ArrayList.add(sotto);
				   ArrayList.add(sinistra);
				   ArrayList.add(destra);
				return ArrayList;
			}else
				{	
				   //System. out.println("Il valore è al limite per y, ovvero " + index[1] +" è "+ (dim-1) + " oppure " + " 0 ");
				   ArrayList.add(error);
				   return ArrayList;
				}
		}else
			{
				//System.out.println("Il valore è al limite per x, ovvero  " + index[0] +" è "+ (dim-1) + " oppure " + " 0 ");
				   ArrayList.add(error);

				return ArrayList;

		}
	}

	private static int[][] ControlloMatrice (ArrayList<int[]> arrayList,int[][] map)
	{
		// 1 sopra 2 sotto 3 sinistra 4 destra  come codici usati.
		  if( arrayList.get(0)[0] != -1) // Se la posizione è valida
		  {
			if(map[arrayList.get(1)[0]][arrayList.get(1)[1]] == 0 )
			{
				if(map[arrayList.get(2)[0]][arrayList.get(2)[1]] == 0 )
				{
					if(map[arrayList.get(3)[0]][arrayList.get(3)[1]] == 0 )
					{
						if(map[arrayList.get(4)[0]][arrayList.get(4)[1]] == 0  )
						{
							map[arrayList.get(0)[0]][arrayList.get(0)[1]] = 1;
						}
					}
				}
			}
			// Se la posizione non è valida per qualche motivo, allora non fare nulla e ritorna solo la mappa com'era in ingresso.
		  }  return map;
	}

	private static int[][] ControlloMatricePrimoPattern(ArrayList<int[]> arrayList,int[][] map)
	{
		// 1 sopra 2 sotto 3 sinistra 4 destra  come codici usati.
		  if( arrayList.get(0)[0] != -1) // Se la posizione è valida
		  {
			if(map[arrayList.get(1)[0]][arrayList.get(1)[1]] == 0 )
			 {
				if(map[arrayList.get(2)[0]][arrayList.get(2)[1]] == 0 )
				{
					map[arrayList.get(0)[0]][arrayList.get(0)[1]] = 1;
				}
			 }
			 return map;
		  }
		  //Nel caso in cui la posizione non sia valida per qualche motivo
		  	return map;
	}
	
	private static int[][] ControlloMatriceSecondoPattern(ArrayList<int[]> arrayList,int[][] map)
	{
		 if( arrayList.get(0)[0] != -1) // Se la posizione è valida
			{
				if(map[arrayList.get(1)[0]][arrayList.get(1)[1]] == 0 )
				{
					if(map[arrayList.get(2)[0]][arrayList.get(2)[1]] == 0 )
					{
						if(map[arrayList.get(3)[0]][arrayList.get(3)[1]] == 0 )
						{
							map[arrayList.get(0)[0]][arrayList.get(0)[1]] = 1;
						}
					}
				}
			}
		// Nel caso in cui la posizione non sia valida non fare nulla e ritorna la mappa in ingresso.	  
		 return map;
	}
	
	private static int[][] ControlloMatriceTerzoPattern (ArrayList<int[]> arrayList,int[][] map)
	{	
		if( arrayList.get(0)[0] != -1) // Se la posizione è valida
		{
		 if(map[arrayList.get(3)[0]][arrayList.get(3)[1]] == 0 )
		 {						
			 map[arrayList.get(0)[0]][arrayList.get(0)[1]] = 1;
		 }
		}
	   return map;
	}
	
	private static void printMap(int[][] map)
	{
		for(int i=0; i < map[0].length ; i++)
		{
			for(int j=0; j < map[1].length; j++)
			{
				System.out.print(" " + map[i][j] + " ");
			}
			System.out.println("");
		}
	}
}
