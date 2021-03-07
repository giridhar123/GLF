package Robot;

import Map.Mappa;

public class LadroRobot extends GenericRobot implements Client {
	
	public LadroRobot(int direction)
	{
		super(direction);
	}

	@Override
	public void onStcSendMapReceived(Mappa mappa) {
		this.mappa = mappa;
		
		for (int i = 0; i < mappa.getXSize(); ++i)
		{
			for (int j = 0; j < mappa.getYSize(); ++j)
			{
				if (i == 10 && j == 10)
					System.out.print(" b" + mappa.get(i, j) + "b");
				else if (i == 13 && j == 33)
					System.out.print(" a" + mappa.get(i, j) + "a");
				else
					System.out.print(" " + mappa.get(i, j) + " ");
			}
			
			System.out.println("");
		}
		//funzione cerca punto buono
		this.xPosition = 10;
		this.yPosition = 10;
		goTo(13, 13);
	}
}
