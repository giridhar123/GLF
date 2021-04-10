package Robot;

import com.cyberbotics.webots.controller.Motor;
import com.cyberbotics.webots.controller.PositionSensor;
import General.SharedVariables;

/*
 * Classe che "implementa" i due motori dei robot insieme ai relativi sensori odometrici
 */

public class Motors {
	
	//Velocità massima dei motori del robot
	private final double MAX_SPEED = 6.28;
	private Motor leftMotor;
	private Motor rightMotor;
	
	//Sensori Encoders
	private PositionSensor leftEncoder, rightEncoder;
	private double encodersValue[];

	public Motors(GenericRobot robot, String leftMotorName, String rightMotorName, String leftEncoderName, String rightEncoderName)
	{
		leftMotor = robot.getMotor(leftMotorName);
		leftMotor.setPosition(Double.POSITIVE_INFINITY);

		rightMotor = robot.getMotor(rightMotorName);
		rightMotor.setPosition(Double.POSITIVE_INFINITY);
		
		leftEncoder = robot.getPositionSensor(leftEncoderName);
		rightEncoder = robot.getPositionSensor(rightEncoderName);

	    leftEncoder.enable(SharedVariables.getTimeStep());
	    rightEncoder.enable(SharedVariables.getTimeStep());
	    
	    encodersValue = new double[2];
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
	
	public void resetEncoders()
	{
		encodersValue[0] = leftEncoder.getValue();
		encodersValue[1] = rightEncoder.getValue();
	}
	
	public double getLeftEncoderValue()
	{
		return (leftEncoder.getValue() - encodersValue[0]);
	}
	
	public double getRightEncoderValue()
	{
		return (rightEncoder.getValue() - encodersValue[1]);
	}
}
