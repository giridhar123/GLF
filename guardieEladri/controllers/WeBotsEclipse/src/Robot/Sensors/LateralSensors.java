package Robot.Sensors;

import com.cyberbotics.webots.controller.DistanceSensor;
import General.SharedVariables;
import Robot.GenericRobot;

/*
 * Classe che "implementa" i due sensori laterali dei robot
 */

public class LateralSensors {

	private DistanceSensor leftSensor, rightSensor;
	private int treshold;
	private boolean leftObstacle, rightObstacle;
	private int maxValue;
	
	
	public LateralSensors(GenericRobot robot, String leftSensorName, String rightSensorName, int treshold)
	{
	    leftSensor = robot.getDistanceSensor(leftSensorName);
	    leftSensor.enable(SharedVariables.getTimeStep());
	    
	    rightSensor = robot.getDistanceSensor(rightSensorName);
	    rightSensor.enable(SharedVariables.getTimeStep());
	    
	    this.treshold = treshold;
	    
	    leftObstacle = rightObstacle = false;
	    maxValue = 0;
	}
	
	public double getLeftValue()
	{
		return leftSensor.getValue();
	}
	
	public double getRightValue()
	{
		return rightSensor.getValue();
	}
	
	public boolean isObstacleOnLeft()
	{
		return leftObstacle;
	}
	
	public boolean isObstacleOnRight()
	{
		return rightObstacle;
	}
	
	public int getMax()
	{
		return maxValue;
	}
	
	public void resetMax()
	{
		maxValue = 0;
	}
	
	public void resetObstacles()
	{
		leftObstacle = rightObstacle = false;		
	}
	
	public void checkObstaclesLateral()
	{
		int leftVal = (int) getLeftValue();
		int rightVal = (int) getRightValue();
		
		if ((leftVal > treshold) || (rightVal > treshold))
		{
			if (leftVal > rightVal)
			{
				rightObstacle = false;
				leftObstacle = true;
				maxValue = leftVal > maxValue ? leftVal : maxValue;
			}
			else if (leftVal < rightVal)
			{
				leftObstacle = false;
				rightObstacle = true;
				maxValue = rightVal > maxValue ? rightVal : maxValue;
			}
		}
	}
}
