package Robot;

import com.cyberbotics.webots.controller.DistanceSensor;

import General.SharedVariables;

public class FrontalSensors {
	
	private DistanceSensor leftSensor, rightSensor;
	private double obstaclesTreshold;
	
	public FrontalSensors(GenericRobot robot, String leftSensorName, String rightSensorName, double obstaclesTreshold)
	{
		leftSensor = robot.getDistanceSensor(leftSensorName);
		leftSensor.enable(SharedVariables.TIME_STEP);
		
		rightSensor = robot.getDistanceSensor(rightSensorName);
		rightSensor.enable(SharedVariables.TIME_STEP);
		
		this.obstaclesTreshold = obstaclesTreshold;
	}
	
	public double getLeftValue()
	{
		return leftSensor.getValue();
	}
	
	public double getRightValue()
	{
		return rightSensor.getValue();
	}
	
	public boolean thereAreObstacles() 
	{
		return (getLeftValue() > obstaclesTreshold && getRightValue() > obstaclesTreshold);
	}

}
