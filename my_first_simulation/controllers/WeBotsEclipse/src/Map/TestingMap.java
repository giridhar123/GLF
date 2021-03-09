package Map;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

public class TestingMap {
	
	public int[][] Go(int[][] map)
    {
		 // Prendo da dimensione della matrice passata dalla classe Mappa richiamata da Server.
		// Tutto sto delirio lo faccio perché se prendo elementi random non da un pool avrei un problema di efficenza.
		int dim = map[0].length; // dim matrix
		int randomElement = 0;
		int index = 0 ;
		int[] ArrayIndex = {0,0};
		
    	int[] arr = new int[dim*dim]; // In questo array ci vanno tutti i valori possibili dalla matrice, quindi deve essere dim*dim
    	for(int i = 0 ; i< arr.length ; i++)
    	{ arr[i] = i ;}	
    	
    	//La funzione mi mette all'interno di un'arraylist i valori randomici dell'array sopra in modo da decidere "randomicamente" la posizione dentro la quale inserire il pattern di oggetti ( attualmente il cubo )
    	List<Integer> shuffled = new ArrayList<>();
    	 for (Integer i : arr)
    	    shuffled.addAll (Collections.nCopies(1,i)); 
    			Collections.shuffle(shuffled); 

    	// Ripeto queste operazioni fino al numero di valori all'interno dell'array. da ottimizzare levando quando becco il valore 1 durante i controlli
    	for( int i = 0 ; i < arr.length; i++ )
    	{
	    	index =  (int) Math.floor(Math.random() * shuffled.size()); // valore random dentro l'arrayList
	    	randomElement = shuffled.get( index ); // prendo l'elemento
		    shuffled.remove(index); // rimuovo l'elemento (altrimenti potrei riprendere gli stessi valori)
		    
		    ArrayIndex[0] = (int) randomElement/dim;
		    ArrayIndex[1] =  randomElement%dim;
	    	QuattroAdiacenza(ArrayIndex,dim,map); // Vedere le singole funzioni
    	}    	
		 return map;
		
    }
	
	public int[][] GoPattern(int[][] map)
    {
		 // Prendo da dimensione della matrice passata dalla classe Mappa richiamata da Server.
		// Tutto sto delirio lo faccio perch� se prendo elementi random non da un pool avrei un problema di efficenza.
		

		int dim = map.length*map.length; // dim matrix
		System.out.println(dim);
		int[] MatrixIndexPoint = {0,0};
		int RandomIndex = 0 ;
		int index = 0 ;
    	List<Integer> AL = new ArrayList<>();


		//La funzione mi mette all'interno di un'arraylist i valori randomici dell'array sopra in modo da decidere "randomicamente" la posizione dentro la quale inserire il pattern di oggetti ( attualmente il cubo )
		int[] arr = new int[dim]; // In questo array ci vanno tutti i valori possibili dalla matrice, quindi deve essere dim*dim
    	for(int i = 0 ; i< arr.length ; i++)
    	{	
    		AL.add(i);
    	}
    	
    	// Ripeto queste operazioni fino al numero di valori all'interno dell'array. da ottimizzare levando quando becco il valore 1 durante i controlli
    	for( int i = 0 ; i < 333; i++ )
    	{
	    	RandomIndex = (int) Math.floor(Math.random() * AL.size()); // valore random dentro l'arrayList
	    	MatrixIndexPoint = PointToIndex(AL.get( RandomIndex ),map[0].length);
	    	map = TakePattern(MatrixIndexPoint,AL,dim, map, RandomIndex);
		   // AL.remove(RandomIndex); // rimuovo l'elemento (altrimenti potrei riprendere gli stessi valori)

    	}
    	printMap(map);
		 return map;
		
    }
    
	private int[][] TakePattern(int[] MatrixIndexPoint, List<Integer> AL,int dim, int[][] map,int RandomIndex) 
	{
		Random rand = new Random();
		int random = rand.nextInt(3)+1;

		switch(random) 
		{
		  case 1: // 3dx
		  {
			  map = ControlloMatrice(QuattroAdiacenza(MatrixIndexPoint,dim,map), map,dim); // primo punto ovunque
			  
			  MatrixIndexPoint = PointToIndex(AL.get(RandomIndex+1)%dim-1,map[0].length); //secondo punto solo dx,giu,su
			  map = ControlloMatricePrimoPattern(QuattroAdiacenza(MatrixIndexPoint,dim,map), map,dim);
			  
			  MatrixIndexPoint = PointToIndex(AL.get(RandomIndex+2)%dim-1,map[0].length); // terzo punto solo dx,giu,su
			  map = ControlloMatricePrimoPattern(QuattroAdiacenza(MatrixIndexPoint,dim,map), map,dim);
			 break;		  
		  }
		  case 2: // 3sx
		  {
			  map = ControlloMatrice(QuattroAdiacenza(MatrixIndexPoint,dim,map), map,dim); // primo punto ovunque
			  
			  MatrixIndexPoint = PointToIndex(AL.get(RandomIndex-1)%dim,map[0].length); //secondo punto solo sx,giu,su
			  map = ControlloMatriceSecondoPattern(QuattroAdiacenza(MatrixIndexPoint,dim,map), map,dim);
			  
			  MatrixIndexPoint = PointToIndex(AL.get(RandomIndex-2)%dim,map[0].length); // terzo punto solo sx,giu,su
			  map = ControlloMatriceSecondoPattern(QuattroAdiacenza(MatrixIndexPoint,dim,map), map,dim);
			 break;		  
		  }
		  case 3: // 3dw
		  { 
			  map = ControlloMatrice(QuattroAdiacenza(MatrixIndexPoint,dim,map), map,dim); // primo punto ovunque
		  
			  MatrixIndexPoint = PointToIndex(AL.get( ((RandomIndex+10)%dim)),map[0].length); //secondo punto solo sx,giu,su
			  map = ControlloMatriceTerzoPattern(QuattroAdiacenza(MatrixIndexPoint,dim,map), map,dim);
		  
			  MatrixIndexPoint = PointToIndex(AL.get( ((RandomIndex+20)%dim) ),map[0].length); // terzo punto solo sx,giu,su
			  map = ControlloMatriceTerzoPattern(QuattroAdiacenza(MatrixIndexPoint,dim,map), map,dim);

		    break;
		  }
		  case 4: // 3up
		  {
			  System.out.println("4");
		    break;
		  }
		  default:
		  {
			  System.out.println("altro");
		  break;
		  }
	}
		return map;
}


 	private int[] PointToIndex(int RandomIndex,int dim)
 	{
 		
 		int[] IndexArray = new int[2];
 		
 		IndexArray[0] = ((int) RandomIndex/dim);
 		IndexArray[1] =  (RandomIndex%dim)+1;
	    
 		return IndexArray;
 		
 	} 
	private ArrayList<int[]>  QuattroAdiacenza(int[] index,int dim, int[][] map) 
	{	
		//System.out.println(dim);
		// Dalla matrice mi prendo i valori:
		//System.out.println(index[0] +""+ index[1]);
		ArrayList<int[]> ArrayList = new ArrayList<int[]>();

		System.out.println(" index x " + index[0]);
		System.out.println(" index y " + index[1]);


		if( (index[0] > 0 && index[0] < map[0].length-1 )) // se non sono quelli vicini al bordo
		{
			if(  (index[1]>0 && index[1] < map[1].length-1  ) )
			{
				//Da verificare che i valori abbiamo gli intorni, altrimenti vediamo chje fare
				int[] sopra = { index[0]-1  , index[1] };
				int[] sotto = { index[0]+1  , index[1] };
				int[] sinistra = { index[0]  , index[1]-1 };
				int[] destra = { index[0]  , index[1]+1 };
			
			//System.out.print("sopra "+ sopra[0] ); 	//System.out.println(" sopra "+ sopra[1] );
			//System.out.print("sotto "+  sotto[0]);		//System.out.println(" sotto "+  sotto[1]);
			//System.out.print("sinistra "+  sinistra[0]);	//System.out.println(" sinistra "+  sinistra[1]);
			//System.out.print("destra "+  destra[0]);	//System.out.println(" destra "+  destra[1]);

			   ArrayList.add(index);
			   ArrayList.add(sopra);
			   ArrayList.add(sotto);
			   ArrayList.add(sinistra);
			   ArrayList.add(destra);
				return ArrayList;
			}else
				{	
			System.out.println("Il valore è al limite per y, OVVERO è UGUALE A 0 O A 20 \n");
			   ArrayList.add(index);
			   ArrayList.add(index);
			   ArrayList.add(index);
			   ArrayList.add(index);
			   ArrayList.add(index);
				return ArrayList;
				}
		}else
			{
				System.out.println("Il valore è al limite per x, OVVERO E' UGUALE A 0 O A 20");
				ArrayList.add(index);
			    ArrayList.add(index);
			    ArrayList.add(index);
			    ArrayList.add(index);
			    ArrayList.add(index);
				return ArrayList;

		}
	}

	private static int[][] ControlloMatrice (ArrayList<int[]> arrayList,int[][] map,int dim)
	{	
		try {
		System.out.println("Sto valutando la posizione :X" + arrayList.get(0)[0] + " Y " + arrayList.get(0)[1]);
		
			if(map[arrayList.get(1)[0]][arrayList.get(1)[1]] == 0 )
			{
				//System.out.println(" E' 1 SOPRA alla posizione "+arrayList.get(1)[0] + ""+ arrayList.get(1)[1]);
				if(map[arrayList.get(2)[0]][arrayList.get(2)[1]] == 0 )
				{
					//System.out.println(" E' 1 SOTTO alla posizione "+arrayList.get(2)[0] + ""+ arrayList.get(2)[1]);			
					if(map[arrayList.get(3)[0]][arrayList.get(3)[1]] == 0 )
					{
						//System.out.println(" E' 1 SINISTRA alla posizione "+arrayList.get(3)[0] + ""+ arrayList.get(3)[1]);	
						if(map[arrayList.get(4)[0]][arrayList.get(4)[1]] == 0  )
						{
							//System.out.println(" E' 1 DESTRA alla posizione "+arrayList.get(4)[0] + ""+ arrayList.get(4)[1]);
							map[arrayList.get(0)[0]][arrayList.get(0)[1]] = 1;
							return map;
						}
					}
				}
				//printMap(map);
			}
			return map;
		} catch (ArrayIndexOutOfBoundsException e)
		{
			return map;
		}
	}

	private static int[][] ControlloMatricePrimoPattern(ArrayList<int[]> arrayList,int[][] map,int dim)
	{
		try {
			if(map[arrayList.get(1)[0]][arrayList.get(1)[1]] == 0 )
			{
				//System.out.println(" E' 1 SOPRA alla posizione "+arrayList.get(1)[0] + ""+ arrayList.get(1)[1]);
				if(map[arrayList.get(2)[0]][arrayList.get(2)[1]] == 0 )
				{
					//System.out.println(" E' 1 SOTTO alla posizione "+arrayList.get(2)[0] + ""+ arrayList.get(2)[1]);			
					//if(map[arrayList.get(3)[0]][arrayList.get(3)[1]] == 0 )
					{
							map[arrayList.get(0)[0]][arrayList.get(0)[1]] = 1;
							return map;
					}
				}
				//printMap(map);
			}
			return map;
			
	} catch (ArrayIndexOutOfBoundsException e)
	{
		return map;
	}
	}
	
	private static int[][] ControlloMatriceSecondoPattern(ArrayList<int[]> arrayList,int[][] map,int dim)
	{		
		try {
				if(map[arrayList.get(1)[0]][arrayList.get(1)[1]] == 0 )
				{
					//System.out.println(" E' 1 SOPRA alla posizione "+arrayList.get(1)[0] + ""+ arrayList.get(1)[1]);
					if(map[arrayList.get(2)[0]][arrayList.get(2)[1]] == 0 )
					{
						//System.out.println(" E' 1 SOTTO alla posizione "+arrayList.get(2)[0] + ""+ arrayList.get(2)[1]);			
						if(map[arrayList.get(3)[0]][arrayList.get(3)[1]] == 0 )
						{
							//System.out.println(" E' 1 SINISTRA alla posizione "+arrayList.get(3)[0] + ""+ arrayList.get(3)[1]);	
								map[arrayList.get(0)[0]][arrayList.get(0)[1]] = 1;
								return map;
						}
					}
				}
		return map;
	} catch (ArrayIndexOutOfBoundsException e)
	{
		return map;
	}
	}
	
	private static int[][] ControlloMatriceTerzoPattern (ArrayList<int[]> arrayList,int[][] map,int dim)
	{	
		try {
		if(map[arrayList.get(3)[0]][arrayList.get(3)[1]] == 0 )
		{						
							map[arrayList.get(0)[0]][arrayList.get(0)[1]] = 1;
							return map;
		}
		} catch (ArrayIndexOutOfBoundsException e)
	{
		return map;
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
