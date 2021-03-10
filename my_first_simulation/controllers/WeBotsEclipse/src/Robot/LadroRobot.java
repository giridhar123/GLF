package Robot;

import java.util.ArrayList;

import Map.Mappa;
import Map.Point;

public class LadroRobot extends GenericRobot implements Client {
	
	public LadroRobot(int direction)
	{
		super(direction);
	}

	@Override
	public void onStcSendMapReceived(Mappa mappa) {
		System.out.println("LADRO MAPPA RICEVUTA");
		this.mappa = mappa;
		
		for (int i = 0; i < mappa.getXSize(); ++i)
		{
			for (int j = 0; j < mappa.getYSize(); ++j)
			{
				if (i == 10 && j == 10)
					System.out.print(" b" + mappa.get(i, j) + "b");
				else if (i == 20 && j == 20)
					System.out.print(" a" + mappa.get(i, j) + "a");
				else
					System.out.print(" " + mappa.get(i, j) + " ");
			}
			
			System.out.println("");
		}
		//funzione cerca punto buono
		this.robotPosition = new Point(10, 10);
		ArrayList<Point> path = this.AStar(new Point(20, 20));
		
		if (path == null)
			System.out.println("Path not found");
		else
		{	
			for (int i = 0; i < path.size(); ++i)
				System.out.println(path.get(i));
		}
	}
}
