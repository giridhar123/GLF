package Robot;

import com.cyberbotics.webots.controller.Motor;

public class Motors {
	
	//Velocità massima delrobot
	private final double MAX_SPEED = 6.28;
	private Motor leftMotor;
	private Motor rightMotor;

	public Motors(GenericRobot robot, String leftMotorName, String rightMotorName)
	{
		leftMotor = robot.getMotor(leftMotorName);
		leftMotor.setPosition(Double.POSITIVE_INFINITY);

		rightMotor = robot.getMotor(rightMotorName);
		rightMotor.setPosition(Double.POSITIVE_INFINITY);
	}
	
	public void setVelocityMS(double velocity)
	{
		leftMotor.setVelocity(velocity * MAX_SPEED);
		rightMotor.setVelocity(velocity * MAX_SPEED);
	}
	
	public void setVelocityMS(double leftVelocity, double rightVelocity)
	{
		leftMotor.setVelocity(leftVelocity * MAX_SPEED);
		rightMotor.setVelocity(rightVelocity * MAX_SPEED);
	}	
	
	public void setVelocity(double velocity)
	{
		leftMotor.setVelocity(velocity);
		rightMotor.setVelocity(velocity);
	}
	
	public void setVelocity(double leftVelocity, double rightVelocity)
	{
		leftMotor.setVelocity(leftVelocity);
		rightMotor.setVelocity(rightVelocity);
	}
}
