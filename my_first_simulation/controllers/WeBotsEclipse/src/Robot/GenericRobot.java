package Robot;

import java.util.ArrayList;

import com.cyberbotics.webots.controller.DistanceSensor;
import com.cyberbotics.webots.controller.Motor;
import com.cyberbotics.webots.controller.PositionSensor;
import com.cyberbotics.webots.controller.Robot;

import Map.Mappa;
import Network.ClientConnectionHandler;
import Network.Client.CTS_PEER_INFO;

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
	
	public static final int NORD = 0;
	public static final int SUD = 1;
	public static final int EST = 2;
	public static final int OVEST = 3;
	
	private Motor leftMotor, rightMotor;
	private PositionSensor leftMotorSensor, rightMotorSensor;
	private DistanceSensor sensors[];
	private double sensorValue[], pose[];
	
	protected int direction, xPosition, yPosition;
	protected Mappa mappa;
	
	public GenericRobot(int direction)
	{
		super();
		
		this.direction = direction;
		
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
	    
	    stop();
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
	
	public void goTo(int xDestination, int yDestination)
	{
		ArrayList<Integer> path = getPath(xDestination, yDestination);
		System.out.println("SIZE IS. " + path.size());
		
		for (int i = 0; i < path.size(); ++i)
		{
			int value = path.get(i);
			
			switch (value)
			{
			case NORD:
				System.out.println("NORD");
				break;
			case SUD:
				System.out.println("SUD");
				break;
			case EST:
				System.out.println("EST");
				break;
			case OVEST:
				System.out.println("OVEST");
				break;
			}
		}
			
	}
	
	public ArrayList<Integer> getPath(int xDestination, int yDestination)
	{
	    ArrayList<int[]> viewedCells = new ArrayList<>();
	    ArrayList<int[]> pathList = new ArrayList<>();

	    int nextPosition[] = new int[2];
	    int currentPosition[] = new int[2];
	    currentPosition[0] = this.xPosition;
	    currentPosition[1] = this.yPosition;
	    
	    nextPosition[0] = nextPosition[1] = -1;
	    
	    int count = 0;
	    int times = 0;
	    
	    while (nextPosition[0] != xDestination || nextPosition[1] != yDestination)
	    {
	        nextPosition = findAdiacentEmptyCell(currentPosition, viewedCells);
	        
	        if (nextPosition[0] != -1 && nextPosition[1] != -1)
	        {
	        	pathList.add(nextPosition);
	        	viewedCells.add(nextPosition);
	        	currentPosition[0] = nextPosition[0];
	        	currentPosition[1] = nextPosition[1];
	        	count = 0;
	        	times = 0;
	        }
	        else
	        {
	            if (++count == 4)
	            {
	                pathList.remove(pathList.size() - 1);
	                currentPosition[0] = pathList.get(pathList.size() - 1)[0];
	                currentPosition[1] = pathList.get(pathList.size() - 1)[1];
	                count = 0;
	            }
	        }
	        
	        if (++times == 100)
	        	break;
	    }

	    System.out.println("path trovato DIM: " + pathList.size());
	    for (int i = 0; i < pathList.size(); ++i)
	    	System.out.println(pathList.get(i)[0] + " " + pathList.get(i)[1]);
	    
	    ArrayList<Integer> path = new ArrayList<>();

	    int pos1[] = pathList.get(0);
	    for (int i = 1; i < pathList.size(); ++i)
	    {
	        int pos2[] = pathList.get(i);

	        if (pos2[0] == (pos1[0] + 1))
	            path.add(SUD);
	        else if (pos2[0] == (pos1[0] - 1))
	        	path.add(NORD);
	        else if (pos2[1] == (pos1[1] - 1))
	        	path.add(OVEST);
	        else if (pos2[1] == (pos1[1] + 1))
	        	path.add(EST);
	        
	        pos1[0] = pos2[0];
	        pos1[1] = pos2[1];
	    }

	    path.add(-1);

	    return path;
	}
	
	public int[] findAdiacentEmptyCell(int currentPosition[], ArrayList<int[]> viewedCells)
	{
	    int nextPosition[] = new int[2];
	    nextPosition[0] = currentPosition[0];
	    nextPosition[1] = currentPosition[1];

	    nextPosition[0] += 1;

	    if (nextPosition[0] < 21 &&
	        mappa.get(nextPosition[0], nextPosition[1]) == 0 &&
	        !contains(nextPosition, viewedCells))
	        return nextPosition;
	    
	    nextPosition[0] -= 2;
	    if (nextPosition[0] >= 0 &&
	        mappa.get(nextPosition[0], nextPosition[1]) == 0 &&
	        !contains(nextPosition, viewedCells))
	        return nextPosition;

	    nextPosition[0] += 1;
	    nextPosition[1] += 1;
	    if (nextPosition[1] < 21 &&
	    	mappa.get(nextPosition[0], nextPosition[1]) == 0 &&
	    	!contains(nextPosition, viewedCells))
	        return nextPosition;

	    nextPosition[1] -= 2;
	    if (nextPosition[1] >= 0 &&
    		mappa.get(nextPosition[0], nextPosition[1]) == 0 &&
    		!contains(nextPosition, viewedCells))
	        return nextPosition;

	    nextPosition[0] = -1;
	    nextPosition[1] = -1;

	    return nextPosition;
	}
	
	private boolean contains(int element[], ArrayList<int[]>list)
	{
		for (int i = 0; i < list.size(); ++i)
			if (list.get(i)[0] == element[0] && list.get(i)[1] == element[1])
				return true;
		
		return false;
	}
}
