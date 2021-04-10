package Robot.Sensors;

import com.cyberbotics.webots.controller.DistanceSensor;

import General.SharedVariables;
import Robot.GenericRobot;

/*
 * Classe che "implementa" i due sensori frontali dei robot
 */

public class FrontalSensors {
	
	private DistanceSensor leftSensor, rightSensor;
	private double obstaclesTreshold;
	
	public FrontalSensors(GenericRobot robot, String leftSensorName, String rightSensorName, double obstaclesTreshold)
	{
		leftSensor = robot.getDistanceSensor(leftSensorName);
		leftSensor.enable(SharedVariables.getTimeStep());
		
		rightSensor = robot.getDistanceSensor(rightSensorName);
		rightSensor.enable(SharedVariables.getTimeStep());
		
		this.obstaclesTreshold = obstaclesTreshold;
	}
	
	public void setTreshold(double obstaclesTreshold)
	{
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
