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
	private final int FRONTAL_OBSTACLE_TRESHOLD = 95;
	private final int LATERAL_OBSTACLE_TRESHOLD = 100;
	
	//Valori di supporto per la direzione del robot
	public static final int NORD = 0;
	public static final int OVEST = 1;
	public static final int SUD = 2;
	public static final int EST = 3;	
	
	//Motori e sensori
	private Motor leftMotor, rightMotor;
	private PositionSensor leftMotorSensor, rightMotorSensor;
	private DistanceSensor sensors[];
	private DistanceSensor leftSensor, rightSensor;
	private int maxProx = 0;
	private boolean leftObstacle, rightObstacle;
	
	private int myFlag;
	
	private final int MORE = 1;
	private final int NOTHING = 0;
	private final int LESS = -1;
	
	private double sensorValue[], pose[];
	
	//Variabili per memorizzare la posizione e la direzione del robot
	protected int direction;
	protected Point robotPosition;
	
	//Mappa in cui si trova il robot
	protected Mappa mappa;
	
	//Flag per la gestione tra thread
	//private boolean stepFlag;
	
	public GenericRobot(int direction)
	{
		super();
		
		//this.stepFlag = false;
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
	    
	    leftSensor = getDistanceSensor("ps5");
	    leftSensor.enable(SharedVariables.TIME_STEP);
	    
	    rightSensor = getDistanceSensor("ps2");
	    rightSensor.enable(SharedVariables.TIME_STEP);
	    
	    leftObstacle = rightObstacle = false;
	    
	    myFlag = NOTHING;
	    
	    sensorValue = new double[2];
	    
	    stop();
	}
	
	
	public boolean goStraightOn(Thread caller)
	{
		return goStraightOn(caller, 1);
	}
	
	public boolean goStraightOn(Thread caller, int times)
	{		
	    leftMotor.setVelocity(1 * MAX_SPEED);
	    rightMotor.setVelocity(1 * MAX_SPEED);
	    
	    int initialValue = 50;
	   
	    int Nvolte = (maxProx / 100) + 2;
	    //System.out.println("Correggo di " + Nvolte);
	    
	    int count = initialValue;
	    
	    if (myFlag == MORE)
	    	count += Nvolte;
	    else if (myFlag == LESS)
	    	count -= Nvolte;
	    
	    myFlag = NOTHING;
	    maxProx= 0;
	    
	    for(int i=0; i < times; ++i) 
	    {	
	    	for (int j = 0; j < count; ++j)
	    	{	    		
	    		checkObstaclesLateral();
	    		
	    		if (checkObstaclesInFront())
	    		{
	    			stop();
	    			return false;
	    		}
	    		step(SharedVariables.TIME_STEP);
	    	}
	    	
	    	count = initialValue;
	    	
	    	switch(direction)
		    {
		    case NORD:
		    	robotPosition.setX(robotPosition.getX() - 1);
		    	break;
		    case EST:
		    	robotPosition.setY(robotPosition.getY() + 1);
		    	break;
		    case SUD:
		    	robotPosition.setX(robotPosition.getX() + 1);
		    	break;
		    case OVEST:
		    	robotPosition.setY(robotPosition.getY() - 1);
		    	break;
		    }
	    }
	    
	    stop();
	    
	    step(SharedVariables.TIME_STEP);
	    double difference = sensors[1].getValue() - sensors[0].getValue();
	    
	    if(Math.abs(difference) >= 10)
	    {
	    	if(difference > 0)
	    	{
	    		leftMotor.setVelocity(-0.3*MAX_SPEED);
	    		rightMotor.setVelocity(0.3*MAX_SPEED);
	    		//System.out.println("Correggo a SX");
	    	}
	    	else 
	    	{
	    		leftMotor.setVelocity(0.3*MAX_SPEED);
	    		rightMotor.setVelocity(-0.3*MAX_SPEED);
	    		//System.out.println("Correggo a DX");
	    	}
	    	
	    	for(int i = 0; i < difference/5; ++i)
    		{
    			step(SharedVariables.TIME_STEP);
    		}
	    	stop();
	    }
	    
	    return true;
	}
	
	public void goBack(Thread caller)
	{
		goBack(50, caller); //Va indietro di una casella
	}
	
	public void goBack(int times, Thread caller)
	{
	    leftMotor.setVelocity(-1 * MAX_SPEED);
	    rightMotor.setVelocity(-1 * MAX_SPEED);

	    for (int i = 0; i < times; ++i)
	    	step(SharedVariables.TIME_STEP);
	    
	    stop();
	}
	
	public void turnLeft(Thread caller)
	{
	    stop();
	    step(SharedVariables.TIME_STEP);
	    sensorValue[0] = leftMotorSensor.getValue();
	    sensorValue[1] = rightMotorSensor.getValue();

	    double goalTheta = (pose[2] + PI/2.00) % PI2;

	    leftMotor.setVelocity(-0.5);
        rightMotor.setVelocity(0.5);
        
	    while(Math.pow(pose[2] - goalTheta, 2) > 0.0003)
	    {	    	
	    	step(SharedVariables.TIME_STEP);
	        updatePose(OVEST);
	    }

	    stop();
	    direction = (direction + 1) % 4;
	    
	    if (leftObstacle)
	    	myFlag = LESS;
	    if (rightObstacle)
	    	myFlag = MORE;
	    else if (!leftObstacle && !rightObstacle)
	    	myFlag = NOTHING;
	    
	    leftObstacle = rightObstacle = false;
	}
	
	public void turnRight(Thread caller)
	{	
	    stop();
	    step(SharedVariables.TIME_STEP);
	    sensorValue[0] = leftMotorSensor.getValue();
	    sensorValue[1] = rightMotorSensor.getValue();

	    double goalTheta = (pose[2] + PI/2.00) % PI2;

	    leftMotor.setVelocity(0.5);
        rightMotor.setVelocity(-0.5);
	    
	    while(Math.pow(pose[2] - goalTheta, 2) > 0.0003)
	    {	    	
	        step(SharedVariables.TIME_STEP);
	        updatePose(EST);
	    }
	    
	    if(direction == 0) direction = 3;
	    else --direction;
	    
	    stop();
	    
	    if (leftObstacle)
	    	myFlag = MORE;
	    if (rightObstacle)
	    	myFlag = LESS;
	    else if (!leftObstacle && !rightObstacle)
	    	myFlag = NOTHING;
	    
	    leftObstacle = rightObstacle = false;
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
	    return (sensors[0].getValue() > FRONTAL_OBSTACLE_TRESHOLD && sensors[1].getValue() > FRONTAL_OBSTACLE_TRESHOLD);
	}
	
	private void checkObstaclesLateral()
	{
		int leftVal= (int) leftSensor.getValue();
		int rightVal=  (int) rightSensor.getValue();
		
		if (leftVal > LATERAL_OBSTACLE_TRESHOLD)
		{
			leftObstacle = true;
			if(leftVal > maxProx) maxProx = leftVal;
		}
		if (rightVal > LATERAL_OBSTACLE_TRESHOLD)
		{
			rightObstacle = true;
			if(rightVal > maxProx) maxProx = rightVal;
		}
	}
	
	/*
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
	*/
	public void checkAngle () 
	{
		
	}
	public Mappa getMappa()
	{
		return mappa;
	}
}
