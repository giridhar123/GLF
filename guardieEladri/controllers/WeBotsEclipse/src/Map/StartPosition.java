package Map;

import Robot.GenericRobot;

public class StartPosition extends Point
{
	public StartPosition(String robotName, Mappa mappa)
	{
		super();
		
		if(robotName.contains("Guardia"))
			this.setX(1);		
		else if(robotName.contains("Ladro"))
			this.setX(mappa.getXSize() - 2);
		
		int number = Integer.valueOf(String.valueOf(robotName.charAt(robotName.length() - 1)));
		int halfWidht = mappa.getYSize()/2;
		if(number % 2 == 0)
			this.setY(halfWidht + number);
		else
			this.setY(halfWidht - number - 1);
	}
	
	public StartPosition(GenericRobot robot)
	{
		this(robot.getName(), robot.getMappa());
	}
}