package Map;


import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class TestingMap {
	
    private static int x;

	public static void main(String args[])
    {
		int dim = 150 ; // dim matrix
    	int[] arr = new int[dim];

    	for(int i = 0 ; i< dim ; i++)
    	{
    		arr[i] = i ;
    	}
    
    	List<Integer> shuffled = new ArrayList<>();
    	for (Integer i : arr)
    	    shuffled.addAll (Collections.nCopies(1,i)); 
    			Collections.shuffle(shuffled); 
    			
    			int randomElement = 0;
    			int index = 0 ;
    	
	    		index =  (int) Math.floor(Math.random() * shuffled.size());
	    		randomElement = shuffled.get( index )  ;
		    	shuffled.remove(index);		    	
	
		    	QuattroAdiacenza(ValoreIndex(randomElement),dim);
		    	
		    	index =  (int) Math.floor(Math.random() * shuffled.size());
	    		randomElement = shuffled.get( index )  ;
	    		shuffled.remove(index);

		    	QuattroAdiacenza(ValoreIndex(randomElement),dim);

		    	index =  (int) Math.floor(Math.random() * shuffled.size());
	    		randomElement = shuffled.get( index )  ;
	    		shuffled.remove(index);
		    	
		    	QuattroAdiacenza(ValoreIndex(randomElement),dim);

		    	index =  (int) Math.floor(Math.random() * shuffled.size());
	    		randomElement = shuffled.get( index )  ;
	    		shuffled.remove(index);
		    	//System.out.println(randomElement);
		    	
		    	QuattroAdiacenza(ValoreIndex(randomElement),dim);

		    	// Devo prendere l'elemento 4 e cancellarlo.
    }

	private static int[] ValoreIndex(int randomElement) 
	{	
		// Ho il numero della matrice, lo estraggo.
		String index=randomElement+"";
		int x = 0 ;
		int y = 0 ;
		if(randomElement>10)
		{
		 x = Integer.parseInt(index.charAt(0)+"");
		 y = Integer.parseInt(index.charAt(1)+"");
		}
		if( randomElement < 10)
		{	
			 x = Integer.parseInt(index.charAt(0)+"");
			 y = 0 ;	
		}
    		//System.out.println("x "+ x + " y "+ y);
    		int ArrayIndex[] = {x,y};
    		
    		return ArrayIndex;
		
	}
    
	private static void QuattroAdiacenza(int[] index,int dim) 
	{	
		// Dalla matrice mi prendo i valori:
		System.out.println(index[0] +""+ index[1]);


		if( (index[0] > 0 && index[0] <= dim )) // se non sono quelli vicini al bordo
		{
			if(  (index[1]>0 && index[1] <= dim) )
		{
		//Da verificare che i valori abbiamo gli intorni, altrimenti vediamo chje fare
			int[] sopra = { index[0]-1  , index[1]-1 };
			int[] sotto = { index[0]+1  , index[1]+1 };
			int[] sinistra = { index[0]-1  , index[1] };
			int[] destra = { index[0]  , index[1]+1 };
			
			System.out.println("sopra "+ sopra[0]+ sopra[1] );
			System.out.println("sotto "+  sotto[0]+ sotto[1] );
			System.out.println("sinistra "+  sinistra[0]+ sinistra[1] );
			System.out.println("destra "+  destra[0]+ destra[1] +"\n");
			}
			else
			{
				System.out.println("Il valore è al limiteIl valore è al limiteIl valore è al limiteIl valore è al limite\n");
			}
		}
		else
		{
			System.out.println("Il valore è al limiteIl valore è al limiteIl valore è al limiteIl valore è al limite\n");
		}
		
		





		
	}
    
}
