package SupervisorController;

import com.cyberbotics.webots.controller.Field;
import com.cyberbotics.webots.controller.Node;
import com.cyberbotics.webots.controller.Supervisor;

import Map.Mappa;

public class MySupervisor extends Supervisor {
	
	public void spawnaMappa(Mappa mappa)
	{
		float TempX, TempY;
		
		Node RootNode = getRoot();
        Field RootChildenField = RootNode.getField("children");
        
        System.out.println("Server - X: " + mappa.getXSize() + " Y: " + mappa.getYSize());
        
		for(int i=0; i < mappa.getXSize() ; i++)
		  {
			 for(int j=0; j< mappa.getYSize(); j++)
			 {
				 //System.out.println("I: " + i + " J: " + j);
				 // Sto scorrendo l'array, se all'interno di questo valore c'� 1 allora faccio lo spawn su quel punto di posizione x,y
				 	TempX = MatrixToWorldX((float) i);
				 	TempY = MatrixToWorldY((float) j);

				 if(mappa.get(i, j) == 1 )
				 {
					 String SpawnBox = "DEF L1 Proto1 {translation "+TempX+",0.05,"+TempY+" size 0.099,0.099,0.099 mass 2} " ;
		         	RootChildenField.importMFNodeFromString(4,SpawnBox);
				 }
			}
			 
		  }
	}
	
	private static float MatrixToWorldY(float point) {
		
		point = (float) (4.95 - ( 0.10 * point)); //4,95 � la posizione 0 vedendola ad occhio, posso modificarla liberamente per un'implementazione futura in modo da rendere la mappa come schifo la voglio io
		return point;
	}
	private static float MatrixToWorldX(float point) {
		
		point = (float) (4.95 - ( 0.10 * point)); //4,95 � la posizione 0 vedendola ad occhio, posso modificarla liberamente per un'implementazione futura in modo da rendere la mappa come schifo la voglio io
		return point;
	}

}
