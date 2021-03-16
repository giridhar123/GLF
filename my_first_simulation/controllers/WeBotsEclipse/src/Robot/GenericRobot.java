package Robot;

import java.util.Random;

import com.cyberbotics.webots.controller.DistanceSensor;
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
	
	//Valori di supporto per il calcolo della rotazione effettuata in base ai valori ricevuti dall'encoder
	private final double WHEEL_RADIUS = 0.0205; // in m
	private final double AXLE_LENGTH = 0.052; 	// in m
	private final double STEPS_ROT = 1000;		// 1000 steps per rotatation
	
	//Valore di soglia per i sensori affinchè venga rilevato un ostacolo
	protected final int FRONTAL_OBSTACLE_TRESHOLD = 80;
	private final int LATERAL_OBSTACLE_TRESHOLD = 100;
	
	//Valori di supporto per la direzione del robot
	public static final int NORD = 0;
	public static final int OVEST = 1;
	public static final int SUD = 2;
	public static final int EST = 3;	
	
	//Motori e sensori
	private Motors motors;
	private PositionSensor leftMotorSensor, rightMotorSensor;
	protected FrontalSensors frontalSensors;
	protected DistanceSensor leftSensor, rightSensor;
	private int maxProx = 0;
	private boolean leftObstacle, rightObstacle;
	
	private int motorAdjustment;
	private final int MORE = 1;
	private final int NOTHING = 0;
	private final int LESS = -1;
	
	private double encodersValue[], pose[];
	
	//Variabili per memorizzare la posizione e la direzione del robot
	protected int direction;
	protected Point robotPosition;
	
	//Mappa in cui si trova il robot
	protected Mappa mappa;
	
	//Flag per la gestione tra thread
	//private boolean stepFlag;
	
	private double goalTheta;
	
	public GenericRobot(int direction)
	{
		super();
		this.direction = direction;		
		
		//this.stepFlag = false;
		this.mappa = null;		
		
		pose = new double[3];
		pose[0] = pose[1] = pose[2] = 0;
		goalTheta = 0;
		
		motors = new Motors(this, "left wheel motor", "right wheel motor");
		stop();

		encodersValue = new double[2];
	    leftMotorSensor = getPositionSensor("left wheel sensor");
	    rightMotorSensor = getPositionSensor("right wheel sensor");

	    leftMotorSensor.enable(SharedVariables.TIME_STEP);
	    rightMotorSensor.enable(SharedVariables.TIME_STEP);
	    
	    frontalSensors = new FrontalSensors(this, "ps7", "ps0", FRONTAL_OBSTACLE_TRESHOLD);
	    
	    leftSensor = getDistanceSensor("ps5");
	    leftSensor.enable(SharedVariables.TIME_STEP);
	    
	    rightSensor = getDistanceSensor("ps2");
	    rightSensor.enable(SharedVariables.TIME_STEP);
	    
	    leftObstacle = rightObstacle = false;
	    motorAdjustment = NOTHING;
	}	
	
	public boolean goStraightOn(int times)
	{		    
		if (times == 0)
			return true;
		
	    int initialValue = 50;	   
	    int Nvolte = (maxProx / 100) + 2;
	    //System.out.println("Correggo di " + Nvolte);
	    
	    int count = initialValue;
	    
	    if (motorAdjustment == MORE)
	    	count += Nvolte;
	    else if (motorAdjustment == LESS)
	    	count -= Nvolte;
	    
	    motorAdjustment = NOTHING;
	    maxProx= 0;
	    
	    boolean obstacle = false;
	    
	    motors.setVelocityMS(1);
	    
	    outerLoop: for(int i = 0; i < times; ++i) 
	    {	    	
	    	for (int j = 0; j < count; ++j)
	    	{	    		
	    		checkObstaclesLateral();
	    		
	    		if (frontalSensors.thereAreObstacles())
	    		{
	    			stop();
	    			obstacle = true;
	    			//System.out.println(j);
	    			
	    			if (j > 35)
	    				incrementaPosizione();
	    			
	    			break outerLoop;
	    		}
	    		step();
	    	}
	    	
	    	incrementaPosizione();
	    	
	    	count = initialValue;
	    }
	    
	    stop();
	    step();
	    
	    double difference = frontalSensors.getLeftValue() - frontalSensors.getRightValue();
	    
	    if (obstacle)
	    {
			goBack(2);
	    }
	    
	    if(Math.abs(difference) >= 8)
	    {
	    	if (this instanceof GuardiaRobot)
	    		System.out.println("Difference is: " + difference);
	    	
	    	if(difference > 0)
	    	{
	    		motors.setVelocity(-0.30, 0.30);
	    		
	    		if (this instanceof GuardiaRobot)
	    			System.out.println("Correggo a SX");
	    	}
	    	else 
	    	{
	    		difference *= -1;
	    		motors.setVelocity(0.30, -0.30);
	    		
	    		if (this instanceof GuardiaRobot)
	    			System.out.println("Correggo a DX");
	    	}
	    	
	    	for(int i = 0; i < (difference / 2); ++i)
	    	{
    			step();
	    	}

	    	stop();
	    	step();
	    }
	    
	    return !obstacle;
	}
	
	public void goBack(int times)
	{
		motors.setVelocityMS(-1);

	    for (int i = 0; i < times; ++i)
	    	step();
	    
	    stop();
	}
	
	public void turnLeft()
	{
	    stop();
	    step();
	    encodersValue[0] = leftMotorSensor.getValue();
	    encodersValue[1] = rightMotorSensor.getValue();

	    goalTheta = (goalTheta + PI/2.00) % PI2;

	    /*
	    if (this instanceof GuardiaRobot)
	    	System.out.println("GoalTheta is: " + goalTheta);
	    */
	    
	    motors.setVelocity(-0.5, 0.5);
        
	    while(Math.pow(pose[2] - goalTheta, 2) > 0.0003)
	    {	    	
	    	step();
	        updatePose(OVEST);
	    }

	    stop();
	    direction = (direction + 1) % 4;
	    
	    if (leftObstacle)
	    	motorAdjustment = LESS;
	    if (rightObstacle)
	    	motorAdjustment = MORE;
	    else if (!leftObstacle && !rightObstacle)
	    	motorAdjustment = NOTHING;
	    
	    leftObstacle = rightObstacle = false;
	}
	
	public void turnRight()
	{	
	    stop();
	    step();
	    encodersValue[0] = leftMotorSensor.getValue();
	    encodersValue[1] = rightMotorSensor.getValue();

	    goalTheta = (goalTheta + PI/2.00) % PI2;
	    
	    motors.setVelocity(0.5, -0.5);
	    
	    while(Math.pow(pose[2] - goalTheta, 2) > 0.0003)
	    {	    	
	        step();
	        updatePose(EST);
	    }
	    
	    if(direction == 0)
	    	direction = 3;
	    else
	    	--direction;
	    
	    stop();
	    
	    if (leftObstacle)
	    	motorAdjustment = MORE;
	    if (rightObstacle)
	    	motorAdjustment = LESS;
	    else if (!leftObstacle && !rightObstacle)
	    	motorAdjustment = NOTHING;
	    
	    leftObstacle = rightObstacle = false;
	}
	
	private void updatePose(int direction)
	{
	    // compute current encoder positions
	    double del_enLeftW = leftMotorSensor.getValue() - encodersValue[0];
	    double del_enRightW = rightMotorSensor.getValue() - encodersValue[1];
	    
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
	    
	    /*
	    if (this instanceof GuardiaRobot)
	    	System.out.println(pose[2] * 360 / PI2);
	    	*/
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
	
	private void checkObstaclesLateral()
	{
		int leftVal= (int) leftSensor.getValue();
		int rightVal=  (int) rightSensor.getValue();
		
		if (leftVal > LATERAL_OBSTACLE_TRESHOLD)
		{
			leftObstacle = true;
			
			if(leftVal > maxProx)
				maxProx = leftVal;
		}
		if (rightVal > LATERAL_OBSTACLE_TRESHOLD)
		{
			rightObstacle = true;
			if(rightVal > maxProx)
				maxProx = rightVal;
		}
	}
	
	//Va avanti di una casella
	public boolean goStraightOn() { return goStraightOn(1); }
	
	//Va indietro di una casella
	public void goBack() { goBack(50); }
	
	//Ferma il robot
	public void stop() { motors.setVelocityMS(0); }
	
	private int step() { return step(SharedVariables.TIME_STEP); }
	public Mappa getMappa() { return mappa; }
	
	private void incrementaPosizione()
	{
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
	
	public void changeDirectionTo(int direction)
	{
		int sig = this.direction - direction;
		
		if (sig < 0)
			sig += 4;
		
		switch (sig)
		{
		case 1:
			turnRight();
			break;
		case 2:
			{
				Random r = new Random();
				//Giusto per non farlo girare SEMPRE e SOLO due volte a sinistra quando deve girare di 180�
				if (r.nextInt(2) == 0)
				{
					turnLeft();
					turnLeft();
				}
				else
				{
					turnRight();
					turnRight();
				}
				motorAdjustment = NOTHING;
			}
			break;
		case 3:
			turnLeft();
			break;
		default:
			break;
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
}
