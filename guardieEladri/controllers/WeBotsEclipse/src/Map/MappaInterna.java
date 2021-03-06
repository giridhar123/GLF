package Map;

import java.util.ArrayList;
import java.util.Random;

public class MappaInterna {
	
	private int[][] mappaInterna; // Mappa di gioco
	private int xDimInterna, yDimInterna, dimSpawnGate; 
	private String difficolta;	
	private Point MatrixIndexPoint; // Equivalente della posizione {x,y} in map[RandomIndex] Indice Della Matrice
	private ArrayList<Integer> AL;
	
	// Inizializzazione della mappa di gioco.
	public MappaInterna(Mappa mappa)
	{
		this.xDimInterna = mappa.getXDimInterna();
		this.yDimInterna = mappa.getYDimInterna();
		this.dimSpawnGate = mappa.getDimSpawnGate();
		this.difficolta = mappa.getDifficolta();
		
		AL = new ArrayList<>();
		
		this.mappaInterna = new int[xDimInterna][yDimInterna];

		initMappaInterna(); // crea la mappa chiusa agli estremmi
		fillMap();
	}
	
	// In caso di fallimento della creazione della mappa di gioco, rieffettuo solo il riempimento della mappa interna
	public MappaInterna(int xDimInterna, int yDimInterna, int dimSpawnGate)
	{
		this.dimSpawnGate = dimSpawnGate;
		this.xDimInterna = xDimInterna;
		this.yDimInterna = yDimInterna;
		this.mappaInterna = new int[xDimInterna][yDimInterna];
		initMappaInterna();
	}

	public int[][] getMappaInterna()
	{
		return this.mappaInterna;
	}

	
	public int get(Point punto)
	{
		return mappaInterna[punto.getX()][punto.getY()];
	}
	
	public int get(int i, int j)
	{
		return mappaInterna[i][j];
	}
	
	public void setValue(Point punto, int value)
	{
		mappaInterna[punto.getX()][punto.getY()] = value;
	}
	
	public void setValue(int i, int j, int value)
	{
		mappaInterna[i][j] = value;
	}
	
	// Creazione dei bordi
	private void initMappaInterna()
	{
		int min = 0;
		int max = yDimInterna-1;
		
		for(int i = 0; i < xDimInterna; i++)
		{ 
			for(int j = 0; j < yDimInterna; j++)
			{		
				if(i == min || i == max || j == min || j == max )
					mappaInterna[i][j] = 1;
				else
					mappaInterna[i][j] = 0;
				
				if((i == 0 && (j > (yDimInterna/2)-dimSpawnGate && j < (yDimInterna/2)+dimSpawnGate)) ||
					(i == max && (j > yDimInterna/2-dimSpawnGate && j < yDimInterna/2+dimSpawnGate)))
				{
					mappaInterna[i][j] = 0 ;
				}				
			}
		}
	}

	// Riempimento della mappa utilizzando l'algoritmo pseudcasuale-
	public void fillMap()
    {
		int NumeroCubi = 0; // Da aggiornare con i dati sperimentali in base alla dimensione della mappa
		
		switch (difficolta)
		{
			case "facile" :
				NumeroCubi = 55; 
			break;
			case "normale" :
				NumeroCubi = 250;
			break;
			case "difficile" :
				NumeroCubi = 400;
			break;
			case "meow":
				NumeroCubi = 1000;
			break;
			default:
				NumeroCubi = 100;
			break;		
		}
	
		int DimMatrix = xDimInterna * yDimInterna; // Dimensione matrice
		int RandomIndex; // Indice Random preso dal pool all'interno della lista AL
		
		// Metto all'interno di AL, tutti i valori della matrice
		for(int i = 0 ; i < DimMatrix ; i++)
			AL.add(i); // Qui ci sono tutti i valori della matrice, e.s 1600 dim ho i valori { 0,1599}
	
		Random r = new Random();
		for( int i = 0 ; i < NumeroCubi ; i++ )
		{
			// Prendo un valore random
			try
			{
				RandomIndex = r.nextInt(AL.size() - 1); // Valore random preso tra 0 e AL.size-1 || 0 _ 1599 alla prima iterata , 0_1598 alla seconda etc..
			}
			catch (IllegalArgumentException e)
			{
				break;
			}
	    	updatePattern(RandomIndex);
		}		
    }
	
	private void updatePattern(int RandomIndex) 
	{
		MatrixIndexPoint = PointToIndex(xDimInterna); // Prendo l'equivalente di quel valore come [x],[y]
		ControlloMatrice(QuattroAdiacenza()); // primo punto ovunque
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
				MatrixIndexPoint = PointToIndex(AL.get(RandomIndex2)); //secondo punto solo dx,giu,su
				ControlloMatricePrimoPattern(QuattroAdiacenza());
			  
				RandomIndex3 = (RandomIndex+2)%(AL.size()-1);
				MatrixIndexPoint = PointToIndex(AL.get(RandomIndex3)); // terzo punto solo dx,giu,su
				ControlloMatricePrimoPattern(QuattroAdiacenza());

				// Devo rimuovere due elementi, quelli alla posizione x e x+1
				for (int i = 0; i < 2; ++i)
				{
					try {
						AL.remove(RandomIndex);
					} catch (IndexOutOfBoundsException e)
					{
						break;
					}
				}
			}
			break;
			case 2: // 3sx
			{
				RandomIndex2 = Math.abs((RandomIndex-1)%(AL.size()-1));
				MatrixIndexPoint = PointToIndex(AL.get(RandomIndex)); //secondo punto solo sx,giu,su
				ControlloMatriceSecondoPattern(QuattroAdiacenza());
			  
				RandomIndex3 = Math.abs(RandomIndex-2)%(AL.size()-1);
				MatrixIndexPoint = PointToIndex(AL.get(RandomIndex)); // terzo punto solo sx,giu,su
				ControlloMatriceSecondoPattern(QuattroAdiacenza());
			  
				// Devo rimuovere due elementi, quelli alla posizione x e x+1
				for (int i = 0; i < 2; ++i)
				{
					try {
						AL.remove(RandomIndex3);
					} catch (IndexOutOfBoundsException e)
					{
						break;
					}
				}
			}
			break;
			case 3: // 3dw
			{ 
				RandomIndex2 = (RandomIndex+xDimInterna)%(AL.size()-1);
				MatrixIndexPoint = PointToIndex(AL.get(RandomIndex)); // terzo punto solo sx,giu,su
				ControlloMatriceTerzoPattern(QuattroAdiacenza());
		  
				RandomIndex3 = (RandomIndex2+xDimInterna)%(AL.size()-1);
				MatrixIndexPoint = PointToIndex(AL.get(RandomIndex)); // terzo punto solo sx,giu,su
				ControlloMatriceTerzoPattern(QuattroAdiacenza());
			  
				// Devo rimuovere due elementi, quelli alla posizione x e x+1
				
				try {
					AL.remove(RandomIndex2);
					AL.remove(RandomIndex);
				} catch (IndexOutOfBoundsException e)
				{
				}
			}
			break;
			case 4: // 3up
			{ 
				RandomIndex2 = Math.abs(RandomIndex+xDimInterna)%(AL.size()-1);
				MatrixIndexPoint = PointToIndex(AL.get(RandomIndex)); // terzo punto solo sx,giu,su
				ControlloMatriceTerzoPattern(QuattroAdiacenza());
		  
				RandomIndex3 = Math.abs(RandomIndex2+xDimInterna)%(AL.size()-1);
				MatrixIndexPoint = PointToIndex(AL.get(RandomIndex)); // terzo punto solo sx,giu,su
				ControlloMatriceTerzoPattern(QuattroAdiacenza());
			  
				// Devo rimuovere due elementi, quelli alla posizione x e x+1
				try {
					AL.remove(RandomIndex2);
					AL.remove(RandomIndex);
				} catch (IndexOutOfBoundsException e)
				{
				}
			}
			break;
			default:
			break;
		}
	}
	
	private Point PointToIndex(int RandomIndex)
 	{
 		return new Point(RandomIndex / xDimInterna, RandomIndex % xDimInterna);
 	}
	
	private ArrayList<Point> QuattroAdiacenza() 
	{	
		ArrayList<Point> ArrayList = new ArrayList<>();
		int x = MatrixIndexPoint.getX();
		int y = MatrixIndexPoint.getY();

		if(x > 0 && x < xDimInterna - 1) // se non sono quelli vicini al bordo
		{
			if(y > 0 && y < yDimInterna - 1)
			{
				//Da verificare che i valori abbiamo gli intorni, altrimenti vediamo chje fare
				Point sopra = new Point(x -1, y);
				Point sotto = new Point(x + 1, y);
				Point sinistra = new Point(x, y - 1);
				Point destra = new Point(x, y + 1);

				ArrayList.add(MatrixIndexPoint);
				ArrayList.add(sopra);
				ArrayList.add(sotto);
				ArrayList.add(sinistra);
				ArrayList.add(destra);
			}
		}
		
		return ArrayList;
	}

	private void ControlloMatrice(ArrayList<Point> arrayList)
	{
		if (arrayList.size() != 5)
			return;
		
		// 1 sopra 2 sotto 3 sinistra 4 destra  come codici usati.
		if(mappaInterna[arrayList.get(1).getX()][arrayList.get(1).getY()] == 0 &&
			mappaInterna[arrayList.get(2).getX()][arrayList.get(2).getY()] == 0 &&
			mappaInterna[arrayList.get(3).getX()][arrayList.get(3).getY()] == 0 &&
			mappaInterna[arrayList.get(4).getX()][arrayList.get(4).getY()] == 0)
		{
			mappaInterna[arrayList.get(0).getX()][arrayList.get(0).getY()] = 1;
		}
	}
	
	// In base al pattern utilizzato si effettuano delle codnizioni di quattro-adiacenza diverse. 
	// Il punto da controllare � quello successivo. Al termine della funzione vengono eliminati i parametri non utilizzati.
	// In seguito le varie caratteristiche.

	private void ControlloMatricePrimoPattern(ArrayList<Point> arrayList)
	{
		if (arrayList.size() != 5)
			return;
		
		// 1 sopra 2 sotto 3 sinistra 4 destra  come codici usati.
		if(mappaInterna[arrayList.get(1).getX()][arrayList.get(1).getY()] == 0 &&
				mappaInterna[arrayList.get(2).getX()][arrayList.get(2).getY()] == 0 )
		{
			mappaInterna[arrayList.get(0).getX()][arrayList.get(0).getY()] = 1;
		}
	}
	
	private void ControlloMatriceSecondoPattern(ArrayList<Point> arrayList)
	{
		if (arrayList.size() != 5)
			return;
		
		 if(mappaInterna[arrayList.get(1).getX()][arrayList.get(1).getY()] == 0 &&
				 mappaInterna[arrayList.get(2).getX()][arrayList.get(2).getY()] == 0 &&
				 mappaInterna[arrayList.get(3).getX()][arrayList.get(3).getY()] == 0 )
		{
			mappaInterna[arrayList.get(0).getX()][arrayList.get(0).getY()] = 1;
		}
	}
	
	private void ControlloMatriceTerzoPattern (ArrayList<Point> arrayList)
	{	
		if (arrayList.size() != 5)
			return;
		
		if(mappaInterna[arrayList.get(3).getX()][arrayList.get(3).getY()] == 0 )
			 mappaInterna[arrayList.get(0).getX()][arrayList.get(0).getY()] = 1;
	}
}
