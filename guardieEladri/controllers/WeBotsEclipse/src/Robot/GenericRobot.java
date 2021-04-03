package Robot;

import java.util.Random;

import com.cyberbotics.webots.controller.Robot;

import General.SharedVariables;
import Map.Mappa;
import Map.Point;
import Robot.Sensors.FrontalSensors;
import Robot.Sensors.LateralSensors;

public abstract class GenericRobot extends Robot 
{
	//Valori di supporto
	private final double PI = Math.PI;
	private final double PI2 = 2 * PI;

	//Mappa in cui si trova il robot
	protected Mappa mappa;
	
	//Valore di soglia per i sensori affinchè venga rilevato un ostacolo
	protected int FRONTAL_OBSTACLE_TRESHOLD = 80;
	protected int LATERAL_OBSTACLE_TRESHOLD = 115;
	
	//Motori e sensori
	private Motors motors;
	protected FrontalSensors frontalSensors;
	protected LateralSensors lateralSensors;
	
	private int motorAdjustment;
	private final int MORE = 1;
	private final int NOTHING = 0;
	private final int LESS = -1;
	
	//Variabili per la rotazionedi 90°
	private double pose;
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

	public GenericRobot(int direction)
	{
		super();
		this.direction = direction;
		this.mappa = null;
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
	    
	    lateralSensors = new LateralSensors(this,
	    									"ps5",
	    									"ps2",
	    									LATERAL_OBSTACLE_TRESHOLD);
	}	
	
	public boolean goStraightOn(int times)
	{		    
		if (times <= 0)
			return true;
		
	    int initialValue = 50;	   
	    int Nvolte = (lateralSensors.getMax() / 100) + 2;
	    
	    int count = initialValue + 1;
	    
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
	    doAngleCorrection();

	    if (obstacle)
			goBack(4);
	    
	    return !obstacle;
	}
	
	protected void doAngleCorrection()
	{
		step();
	    
	    double leftValue = frontalSensors.getLeftValue();
	    double rightValue = frontalSensors.getRightValue();
	    double difference = leftValue - rightValue;
	    
	    if (leftValue > 65 && rightValue > 65)
	    {
	    	double velocity = 0.7;
		    while (Math.abs(difference) >= 8)
	    	{	
		    	if(difference > 0)
		    		motors.setVelocity(-velocity, velocity);
		    	else
		    		motors.setVelocity(velocity, -velocity);
		    	
		    	step();
		    	difference = frontalSensors.getLeftValue() - frontalSensors.getRightValue();
		    	
		    }
		    stop();
	    }
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
	    double goalTheta = (pose + PI/2.00) % PI2;
	    
	    motors.setVelocity(-0.5, 0.5);
        
	    double error;
	    
	    while((error = Math.pow(pose - goalTheta, 2)) > 0.0001)
	    {	    
	    	System.out.println(error);
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

	    double goalTheta = (pose + PI/2.00) % PI2;
	    
	    motors.setVelocity(0.5, -0.5);
	    
	    double error;
	    while((error = Math.pow(pose - goalTheta, 2)) > 0.0001)
	    {	   
	    	System.out.println(error);
	        step();
	        updatePose(EST);
	    }
	    
	    stop();
	    
	    direction = direction == 0 ? 3 : direction - 1;
	    
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
	    // Calcola la posizione corrente dell'encoder
	    double del_enLeftW = motors.getLeftEncoderValue();
	    double del_enRightW = motors.getRightEncoderValue();
	    
	    // Cancola di quanto si sono mosse le ruote
	    double values[] = getWheelDisplacements(del_enLeftW, del_enRightW);

	    // Aggiorna l'orientamento
	    if (direction == EST)
	    	pose += (values[0] - values[1])/AXLE_LENGTH;
	    else if (direction == OVEST)
	    	pose += (values[1] - values[0])/AXLE_LENGTH;
	    
	    pose = pose % PI2;
	}
	
	/*
	 * Restituisce un array
	 * Posizione 0: spiazzamento ruota sinistra
	 * Posizione 1: spiazzamento ruota destra
	 */
	
	private double[] getWheelDisplacements(double del_enLeftW, double del_enRightW)
	{
		double values[] = new double[2];
		// Cancola di quanto si è mossa la ruota sinistra
		values[0] = del_enLeftW / STEPS_ROT * 2 * PI * WHEEL_RADIUS; 
		// Cancola di quanto si è mossa la ruota destra
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
		
		onPosizioneIncrementata();
	}
	
	public abstract void onPosizioneIncrementata();
	
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
	
	public abstract void work();	
}
