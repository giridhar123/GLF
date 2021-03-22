package Robot;

import java.util.Random;

import com.cyberbotics.webots.controller.Robot;

import General.SharedVariables;
import General.StartPositions;
import Map.Mappa;
import Map.Point;

public abstract class GenericRobot extends Robot 
{
	//Valori di supporto
	private final double PI = Math.PI;
	private final double PI2 = 2 * PI;

	//Mappa in cui si trova il robot
	protected Mappa mappa;
	
	//Valore di soglia per i sensori affinchè venga rilevato un ostacolo
	protected int FRONTAL_OBSTACLE_TRESHOLD = 80;
	protected int LATERAL_OBSTACLE_TRESHOLD = 100;
	
	//Motori e sensori
	private Motors motors;
	protected FrontalSensors frontalSensors;
	protected LateralSensors lateralSensors;
	
	private int motorAdjustment;
	private final int MORE = 1;
	private final int NOTHING = 0;
	private final int LESS = -1;
	
	//Variabili per la rotazionedi 90°
	private double pose, goalTheta;
	private final double WHEEL_RADIUS = 0.0205; // in m
	private final double AXLE_LENGTH = 0.052; 	// in m
	private final double STEPS_ROT = 1000;		// 1000 steps per rotatation
	
	//Variabili per memorizzare la posizione e la direzione del robot
	protected int direction;
	protected Point robotPosition;
	
	//Valori di supporto per la direzione del robot
	public static final int NORD = 0;
	public static final int OVEST = 1;
	public static final int SUD = 2;
	public static final int EST = 3;	
	
	//Flag per la gestione tra thread
	//private boolean stepFlag;
	
	public GenericRobot(int direction)
	{
		super();
		this.direction = direction;		
		
		//this.stepFlag = false;
		this.mappa = null;
		this.robotPosition = StartPositions.valueOf(getName()).getPosition();
		System.out.println("Sono " + getName() + " - la mia position è: " + robotPosition);
		
		goalTheta = 0;
		pose = 0;
		
		motors = new Motors(this,
							"left wheel motor",
							"right wheel motor",
							"left wheel sensor",
							"right wheel sensor");
		motorAdjustment = NOTHING;
		stop();
	    
	    frontalSensors = new FrontalSensors(this,
								    		"ps7",
								    		"ps0",
								    		FRONTAL_OBSTACLE_TRESHOLD);
	    
	    lateralSensors = new LateralSensors(this, "ps5", "ps2", LATERAL_OBSTACLE_TRESHOLD);
	}	
	
	public boolean goStraightOn(int times)
	{		    
		if (times == 0)
			return true;
		
	    int initialValue = 50;	   
	    int Nvolte = (lateralSensors.getMax() / 100) + 2;
	    //System.out.println("Correggo di " + Nvolte);
	    
	    int count = initialValue;
	    
	    if (motorAdjustment == MORE)
	    	count += Nvolte;
	    else if (motorAdjustment == LESS)
	    	count -= Nvolte;
	    
	    motorAdjustment = NOTHING;
	    lateralSensors.resetMax();
	    
	    boolean obstacle = false;
	    
	    motors.setVelocityMS(1);
	    
	    outerLoop: for(int i = 0; i < times; ++i) 
	    {	    	
	    	for (int j = 0; j < count; ++j)
	    	{	    		
	    		lateralSensors.checkObstaclesLateral();
	    		
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
	    
	    double leftValue = frontalSensors.getLeftValue();
	    double rightValue = frontalSensors.getRightValue();
	    double difference = leftValue - rightValue;
	    
	    if (leftValue > 50 && rightValue > 50)
	    {
		    //if(Math.abs(difference) >= 8)
		    while(Math.abs(difference) >= 8)
	    	{
		    	if (this instanceof GuardiaRobot)
		    		System.out.println("Difference is: " + difference);
		    	
		    	double velocity = 0.7;
		    	if(difference > 0)
		    	{
		    		motors.setVelocity(-velocity, velocity);
		    		
		    		if (this instanceof GuardiaRobot)
		    			System.out.println("Correggo a SX");
		    	}
		    	else 
		    	{
		    		difference *= -1;
		    		motors.setVelocity(velocity, -velocity);
		    		
		    		if (this instanceof GuardiaRobot)
		    			System.out.println("Correggo a DX");
		    	}
		    	
		    	step();
		    	difference = frontalSensors.getLeftValue() - frontalSensors.getRightValue();
		    	/*for(int i = 0; i < (difference / 2); ++i)
		    	{
	    			step();
		    	}
				
		    	stop();
		    	step();
		    	 */
		    }
		    stop();
	    }

	    if (obstacle)
			goBack(4);
	    
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
	    motors.resetEncoders();
	    goalTheta = (goalTheta + PI/2.00) % PI2;

	    /*
	    if (this instanceof GuardiaRobot)
	    	System.out.println("GoalTheta is: " + goalTheta);
	    */
	    
	    motors.setVelocity(-0.5, 0.5);
        
	    while(Math.pow(pose - goalTheta, 2) > 0.0005)
	    {	    	
	    	step();
	        updatePose(OVEST);
	    }

	    stop();
	    direction = (direction + 1) % 4;
	    
	    if (lateralSensors.isObstacleOnLeft())
	    	motorAdjustment = LESS;
	    if (lateralSensors.isObstacleOnRight())
	    	motorAdjustment = MORE;
	    else if (!lateralSensors.isObstacleOnLeft() && !lateralSensors.isObstacleOnRight())
	    	motorAdjustment = NOTHING;
	    
	    lateralSensors.resetObstacles();
	}
	
	public void turnRight()
	{	
	    stop();
	    step();
	    motors.resetEncoders();

	    goalTheta = (goalTheta + PI/2.00) % PI2;
	    
	    motors.setVelocity(0.5, -0.5);
	    
	    while(Math.pow(pose - goalTheta, 2) > 0.0005)
	    {	    	
	        step();
	        updatePose(EST);
	    }
	    
	    if(direction == 0)
	    	direction = 3;
	    else
	    	--direction;
	    
	    stop();
	    
	    if (lateralSensors.isObstacleOnLeft())
	    	motorAdjustment = MORE;
	    if (lateralSensors.isObstacleOnRight())
	    	motorAdjustment = LESS;
	    else if (!lateralSensors.isObstacleOnLeft() && !lateralSensors.isObstacleOnRight())
	    	motorAdjustment = NOTHING;
	    
	    lateralSensors.resetObstacles();
	}
	
	private void updatePose(int direction)
	{
	    // compute current encoder positions
	    double del_enLeftW = motors.getLeftEncoderValue();
	    double del_enRightW = motors.getRightEncoderValue();
	    
	    // compute wheel displacements
	    double values[] = getWheelDisplacements(del_enLeftW, del_enRightW);

	    // Update in orientation
	    if (direction == EST)
	    	pose += (values[0] - values[1])/AXLE_LENGTH; // orientation
	    else if (direction == OVEST)
	    	pose += (values[1] - values[0])/AXLE_LENGTH; // orientation
	    
	    pose = pose % PI2;
	    
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
	
	//Va avanti di una casella
	public boolean goStraightOn() { return goStraightOn(1); }
	
	//Va indietro di una casella
	public void goBack() { goBack(50); }
	
	//Ferma il robot
	public void stop() { motors.setVelocityMS(0); }
	
	public int step() { return step(SharedVariables.getTimeStep()); }
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
				/* 
				 * Per non farlo girare SEMPRE e SOLO
				 * due volte a sinistra/destra
				 * quando deve girare di 180
				 */
				Random r = new Random();
				if (r.nextInt(2) == 0)
				{
					turnLeft();
					stop();
					turnLeft();
				}
				else
				{
					turnRight();
					stop();
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
