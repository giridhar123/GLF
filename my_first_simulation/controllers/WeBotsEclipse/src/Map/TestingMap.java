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
    
	private static  ArrayList<int[]>  QuattroAdiacenza(int[] index,int dim, int[][] map) 
	{	
		//System.out.println(dim);
		// Dalla matrice mi prendo i valori:
		//System.out.println(index[0] +""+ index[1]);
		ArrayList<int[]> ArrayList = new ArrayList<int[]>();

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
			   ControlloMatrice(ArrayList,map,dim);
		}
		else
		{
			//System.out.println("Il valore è al limiteIl valore è al limiteIl valore è al limiteIl valore è al limite\n");
		}
		}
		else
		{
			//System.out.println("Il valore è al limiteIl valore è al limiteIl valore è al limiteIl valore è al limite\n");
		}
		return ArrayList;
	}

	private static void ControlloMatrice (ArrayList<int[]> arrayList,int[][] map,int dim)
	{		
		//System.out.println("Sto valutando la posizione :" + arrayList.get(0)[0] + "" + arrayList.get(0)[1]);
		
			if(map[arrayList.get(1)[0]][arrayList.get(1)[1]] != -1 )
			{
				//System.out.println(" E' 1 SOPRA alla posizione "+arrayList.get(1)[0] + ""+ arrayList.get(1)[1]);
				if(map[arrayList.get(2)[0]][arrayList.get(2)[1]] != -1 )
				{
					//System.out.println(" E' 1 SOTTO alla posizione "+arrayList.get(2)[0] + ""+ arrayList.get(2)[1]);			
					if(map[arrayList.get(3)[0]][arrayList.get(3)[1]] != -1 )
					{
						//System.out.println(" E' 1 SINISTRA alla posizione "+arrayList.get(3)[0] + ""+ arrayList.get(3)[1]);	
						if(map[arrayList.get(4)[0]][arrayList.get(4)[1]] != -1  )
						{
							//System.out.println(" E' 1 DESTRA alla posizione "+arrayList.get(4)[0] + ""+ arrayList.get(4)[1]);
							map[arrayList.get(0)[0]][arrayList.get(0)[1]] = -1;
						}
					}
				}
				//printMap(map);
			}
						
			
	
		// ho la matrice, valuto i punti presi se c'è 1 o 0. se sono tutti 0 posso inserirla.
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
