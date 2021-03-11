package Robot;

import com.cyberbotics.webots.controller.DistanceSensor;
import com.cyberbotics.webots.controller.Motor;
import com.cyberbotics.webots.controller.PositionSensor;
import com.cyberbotics.webots.controller.Robot;

import General.SharedVariables;
import Map.Mappa;
import Map.Point;

public abstract class GenericRobot extends Robot 
{
	//Valori di supporto
	private final double PI = Math.PI;
	private final double PI2 = 2 * PI;
	
	//Velocità massima delrobot
	private final double MAX_SPEED = 6.28;
	
	//Valori di supporto per il calcolo della rotazione effettuata in base ai valori ricevuti dall'encoder
	private final double WHEEL_RADIUS = 0.0205; // in m
	private final double AXLE_LENGTH = 0.052; 	// in m
	private final double STEPS_ROT = 1000;		// 1000 steps per rotatation
	
	//Valore di soglia per i sensori affinchè venga rilevato un ostacolo
	private final int OBSTACLE_TRESHOLD = 100;
	
	//Valori di supporto per la direzione del robot
	public static final int NORD = 0;
	public static final int OVEST = 1;
	public static final int SUD = 2;
	public static final int EST = 3;	
	
	//Motori e sensori
	private Motor leftMotor, rightMotor;
	private PositionSensor leftMotorSensor, rightMotorSensor;
	private DistanceSensor sensors[];
	private double sensorValue[], pose[];
	
	//Variabili per memorizzare la posizione e la direzione del robot
	protected int direction;
	protected Point robotPosition;
	
	//Mappa in cui si trova il robot
	protected Mappa mappa;
	
	//Flag per la gestione tra thread
	private boolean stepFlag;
	
	public GenericRobot(int direction)
	{
		super();
		
		this.stepFlag = false;
		this.mappa = null;
		
		this.direction = direction;
		pose = new double[3];
		pose[0] = pose[1] = pose[2] = 0;
		
		leftMotor = getMotor("left wheel motor");
	    rightMotor = getMotor("right wheel motor");
	    leftMotor.setPosition(Double.POSITIVE_INFINITY);
	    rightMotor.setPosition(Double.POSITIVE_INFINITY);

	    leftMotorSensor = getPositionSensor("left wheel sensor");
	    rightMotorSensor = getPositionSensor("right wheel sensor");

	    leftMotorSensor.enable(SharedVariables.TIME_STEP);
	    rightMotorSensor.enable(SharedVariables.TIME_STEP);
	    
	    sensors = new DistanceSensor[2];
	    sensors[0] = getDistanceSensor("ps0");
	    sensors[0].enable(SharedVariables.TIME_STEP);

	    sensors[1] = getDistanceSensor("ps7");
	    sensors[1].enable(SharedVariables.TIME_STEP);
	    
	    sensorValue = new double[2];
	    
	    stop();
	}
	
	public synchronized boolean goStraightOn(Thread caller)
	{
		stepFlag = true;
		
	    leftMotor.setVelocity(1 * MAX_SPEED);
	    rightMotor.setVelocity(1 * MAX_SPEED);
	    
	    for (int i = 0; i < 50 ; ++i)
	    {
	        if (checkObstaclesInFront())
	        {
	            stop();
				return false;
	        }
	        myStep(SharedVariables.TIME_STEP, caller);
	    }
	    
	    stop();
	    
	    stepFlag = false;
	    notifyAll();
	    
	    switch(direction)
	    {
	    case NORD:
	    	robotPosition.setX(robotPosition.getX() + 1);
	    	break;
	    case EST:
	    	robotPosition.setY(robotPosition.getY() + 1);
	    	break;
	    case SUD:
	    	robotPosition.setX(robotPosition.getX() - 1);
	    	break;
	    case OVEST:
	    	robotPosition.setY(robotPosition.getY() - 1);
	    	break;
	    }
	    
	    return true;
	}
	
	public synchronized void goBack(Thread caller)
	{
		goBack(50, caller); //Va indietro di una casella
	}
	
	public synchronized void goBack(int times, Thread caller)
	{
	    leftMotor.setVelocity(-1 * MAX_SPEED);
	    rightMotor.setVelocity(-1 * MAX_SPEED);

	    for (int i = 0; i < times; ++i)
	    	myStep(SharedVariables.TIME_STEP, caller);
	    
	    stop();
	}
	
	public synchronized void turnLeft(Thread caller)
	{
		stepFlag = true;
		
	    stop();
	    myStep(SharedVariables.TIME_STEP, caller);
	    sensorValue[0] = leftMotorSensor.getValue();
	    sensorValue[1] = rightMotorSensor.getValue();

	    double goalTheta = (pose[2] + PI/2.00) % PI2;

	    leftMotor.setVelocity(-0.5);
        rightMotor.setVelocity(0.5);
        
	    while(Math.pow(pose[2] - goalTheta, 2) > 0.0005)
	    {	    	
	        myStep(SharedVariables.TIME_STEP, caller);
	        updatePose(OVEST);
	    }

	    stop();
	    direction = (direction + 1) % 4;
	    
	    stepFlag = false;
	    notifyAll();
	}
	
	public synchronized void turnRight(Thread caller)
	{
		stepFlag = true;
		
	    stop();
	    myStep(SharedVariables.TIME_STEP, caller);
	    sensorValue[0] = leftMotorSensor.getValue();
	    sensorValue[1] = rightMotorSensor.getValue();

	    double goalTheta = (pose[2] + PI/2.00) % PI2;

	    while(Math.pow(pose[2] - goalTheta, 2) > 0.0005)
	    {	    	
	        leftMotor.setVelocity(0.5);
	        rightMotor.setVelocity(-0.5);
	        myStep(SharedVariables.TIME_STEP, caller);
	        updatePose(EST);
	    }
	    
	    if(direction == 0) direction = 3;
	    else --direction;
	    
	    stop();
	    
	    stepFlag = false;
	    notifyAll();
	}
	
	public void stop()
	{
	    leftMotor.setVelocity(0 * MAX_SPEED);
	    rightMotor.setVelocity(0 * MAX_SPEED);
	}
	
	private void updatePose(int direction)
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
	    if (direction == EST)
	    	pose[2] += (values[0] - values[1])/AXLE_LENGTH; // orientation
	    else if (direction == OVEST)
	    	pose[2] += (values[1] - values[0])/AXLE_LENGTH; // orientation
	    
	    pose[2] = pose[2] % PI2;
	}
	
	private double[] getWheelDisplacements(double del_enLeftW, double del_enRightW)
	{
		double values[] = new double[2];
		// compute displacement of left wheel in meters
		values[0] = del_enLeftW / STEPS_ROT * 2 * PI * WHEEL_RADIUS; 
		// compute displacement of right wheel in meters
		values[1] = del_enRightW / STEPS_ROT * 2 * PI * WHEEL_RADIUS;
		  
		return values;
	}
	
	private boolean checkObstaclesInFront()
	{
	    return (sensors[0].getValue() > OBSTACLE_TRESHOLD && sensors[1].getValue() > OBSTACLE_TRESHOLD);
	}
	
	public synchronized int myStep(int time, Thread caller)
	{
		if (caller == null)
		{
			while(stepFlag) 
			{
				try 
				{
					wait();
				} 
				catch (InterruptedException e) 
				{
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
		return step(time);
	}
	
	public Mappa getMappa()
	{
		return mappa;
	}
}
