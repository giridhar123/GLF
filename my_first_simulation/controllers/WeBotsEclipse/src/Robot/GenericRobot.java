package Robot;

import com.cyberbotics.webots.controller.DistanceSensor;
import com.cyberbotics.webots.controller.Motor;
import com.cyberbotics.webots.controller.PositionSensor;
import com.cyberbotics.webots.controller.Robot;

public abstract class GenericRobot extends Robot 
{
	private final int TIME_STEP = 16;
	private final double MAX_SPEED = 6.28;
	private final double WHEEL_RADIUS = 0.0205; // in m
	private final double AXLE_LENGTH = 0.052; 	// in m
	private final double STEPS_ROT = 1000;		// 1000 steps per rotatation
	private final double PI = 3.141592654;
	private final double PI2 = 6.283185307;
	private final int OBSTACLE_TRESHOLD = 100;
	
	private Motor leftMotor, rightMotor;
	private PositionSensor leftMotorSensor, rightMotorSensor;
	private DistanceSensor sensors[];
	private double sensorValue[], pose[];
	
	public GenericRobot()
	{
		super();
		
		sensorValue = new double[2];
		pose = new double[3];
		pose[0] = pose[1] = pose[2] = 0;
		
		leftMotor = getMotor("left wheel motor");
	    rightMotor = getMotor("right wheel motor");
	    leftMotor.setPosition(Double.POSITIVE_INFINITY);
	    rightMotor.setPosition(Double.POSITIVE_INFINITY);

	    leftMotorSensor = getPositionSensor("left wheel sensor");
	    rightMotorSensor = getPositionSensor("right wheel sensor");

	    leftMotorSensor.enable(TIME_STEP);
	    rightMotorSensor.enable(TIME_STEP);
	    
	    sensors = new DistanceSensor[2];
	    sensors[0] = getDistanceSensor("ps0");
	    sensors[0].enable(TIME_STEP);

	    sensors[1] = getDistanceSensor("ps7");
	    sensors[1].enable(TIME_STEP);
	}
	
	public void goStraightOn()
	{
	    leftMotor.setVelocity(1 * MAX_SPEED);
	    rightMotor.setVelocity(1 * MAX_SPEED);

	    leftMotor.setVelocity(1 * MAX_SPEED);
        rightMotor.setVelocity(1 * MAX_SPEED);
	    
	    for (int i = 0; i < 50; ++i)
	    {
	        if (checkObstaclesInFront())
	        {
	            goBack(i);
	            break;
	        }    
	        step(TIME_STEP);
	    }
	    
	    stop();
	}
	
	public void goBack()
	{
		goBack(50); //Va indietro di una casella
	}
	
	public void goBack(int times)
	{
	    leftMotor.setVelocity(-1 * MAX_SPEED);
	    rightMotor.setVelocity(-1 * MAX_SPEED);

	    for (int i = 0; i < times; ++i)
	    	step(TIME_STEP);
	    
	    stop();
	}
	
	public void turnLeft()
	{
	    stop();
	    step(TIME_STEP);
	    sensorValue[0] = leftMotorSensor.getValue();
	    sensorValue[1] = rightMotorSensor.getValue();

	    double goalTheta = pose[2] + PI/2.00 % PI2;

	    while(Math.pow(pose[2] - goalTheta, 2) > 0.001)
	    {	    	
	        leftMotor.setVelocity(-0.5);
	        rightMotor.setVelocity(0.5);
	        step(TIME_STEP);
	        updatePose(0);
	    }

	    stop();
	}
	
	public void turnRight()
	{
	    stop();
	    step(TIME_STEP);
	    sensorValue[0] = leftMotorSensor.getValue();
	    sensorValue[1] = rightMotorSensor.getValue();

	    double goalTheta = pose[2] + PI/2.00 % PI2;

	    while(Math.pow(pose[2] - goalTheta, 2) > 0.001)
	    {	    	
	        leftMotor.setVelocity(0.5);
	        rightMotor.setVelocity(-0.5);
	        step(TIME_STEP);
	        updatePose(1);
	    }

	    stop();
	}
	
	public void stop()
	{
	    leftMotor.setVelocity(0 * MAX_SPEED);
	    rightMotor.setVelocity(0 * MAX_SPEED);
	}
	
	public void updatePose(int n)
	{
	    // compute current encoder positions
	    double del_enLeftW = leftMotorSensor.getValue() - sensorValue[0];
	    double del_enRightW = rightMotorSensor.getValue() - sensorValue[1];
	    
	    // compute wheel displacements
	    double values[] = getWheelDisplacements(del_enLeftW, del_enRightW);

	    // displacement of robot
	    double dispRobot = (values[0] + values[1])/2.0; 

	    // Update position vector:
	    // Update in position along X direction
	    pose[0] +=  dispRobot * Math.cos(pose[2]); 
	    // Update in position along Y direction
	    pose[1] +=  dispRobot * Math.sin(pose[2]); // robot position w.r.to Y direction
	    // Update in orientation
	    if(n==1) pose[2] += (values[0] - values[1])/AXLE_LENGTH; // orientation
	    
	    else pose[2] += (values[1] - values[0])/AXLE_LENGTH; // orientation
	    
	    pose[2] = pose[2] % PI2;
	}
	
	public double[] getWheelDisplacements(double del_enLeftW, double del_enRightW)
	{
		double values[] = new double[2];
	  // compute displacement of left wheel in meters
	  values[0] = del_enLeftW / STEPS_ROT * 2 * PI * WHEEL_RADIUS; 
	  // compute displacement of right wheel in meters
	  values[1] = del_enRightW / STEPS_ROT * 2 * PI * WHEEL_RADIUS;
	  
	  return values;
	}
	
	public boolean checkObstaclesInFront()
	{
	    return (sensors[0].getValue() > OBSTACLE_TRESHOLD && sensors[1].getValue() > OBSTACLE_TRESHOLD);
	}
}
