package Robot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

import com.cyberbotics.webots.controller.DistanceSensor;
import com.cyberbotics.webots.controller.Motor;
import com.cyberbotics.webots.controller.PositionSensor;
import com.cyberbotics.webots.controller.Robot;
import com.sun.jdi.Value;

import Map.Mappa;
import Map.Point;
import Network.ClientConnectionHandler;
import Network.Client.CTS_PEER_INFO;
import java.util.HashSet;

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
	
	protected int direction;
	protected Point robotPosition;
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
	
	/*
	 * GUAI A CHI TOCCA QUESTO ALGORITMO!!!!
	 */
	public ArrayList<Point> AStar(Point goal)
	{
		Point start = new Point(robotPosition);
		Set<Point> closedset = new HashSet<>();			//the empty set % The set of nodes already evaluated.     
		Set<Point> openset = new HashSet<>();			//set containing the initial node % The set of tentative nodes to be evaluated.
		openset.add(start);
		
		Map<Point, Float> g_score = new HashMap<>();	// Distance from start along optimal path.
		g_score.put(start, (float) 0);
		
		Map<Point, Point> came_from = new HashMap<>(); 	// the empty map % The map of navigated nodes.
		Map<Point, Float> h_score = new HashMap<>();
		float value = heuristic_estimate_of_distance(start, goal);
		h_score.put(start, value);
		
		Map<Point, Float> f_score = new HashMap<>();
		f_score.put(start, value);						//% Estimated total distance from start to goal through y.
				
		while (!openset.isEmpty())
		{
			Point x = getHelp(openset, f_score); //:= the node in openset having the lowest f_score[] value
						
			if (x.equals(goal))
				return reconstruct_path(came_from, goal);
			
         	openset.remove(x);
         	closedset.add(x);
         	
         	Set<Point> neighbors = getNeighbors(x);
         	Iterator<Point> iterator = neighbors.iterator();
         	
         	while (iterator.hasNext())
         	{
         		Point y = iterator.next();
         		if (closedset.contains(y))
         			continue;

         		float tentative_g_score = g_score.get(x) + 1;
             
         		boolean tentative_is_better = false;
         		if (!openset.contains(y))
         		{
         			openset.add(y);
         			tentative_is_better = true;
         		}
         		else if (tentative_g_score < g_score.get(y))
         			tentative_is_better = true;

		        if (tentative_is_better)
		        {
		        	came_from.put(y, x);
		        	
		        	//RIGHE MAGICHE SENZA SENSO CHE FANNO FUNZIONARE LA RICOSTRUZIONE DEL PATH
		        	if (y.equals(goal))
		        		goal = y;

		            g_score.put(y, tentative_g_score);
		            h_score.put(y, heuristic_estimate_of_distance(y, goal));
		            f_score.put(y, g_score.get(y) + h_score.get(y));
		        }
         	}
		}
		return null;
	}
	
	private Point getHelp(Set<Point> openset, Map<Point, Float> f_score)
	{
		Point point = null;		
		float min = Float.MAX_VALUE;
		
		Iterator<Point> iterator = openset.iterator();
		while (iterator.hasNext())
		{
			Point testPoint = iterator.next();
			if (f_score.get(testPoint) < min)
			{
				point = testPoint;
				min = f_score.get(testPoint);
			}
		}
		
		return point;
	}
	
	private Set<Point> getNeighbors(Point point)
	{
		Set<Point> set = new HashSet<>();
		
		int x = point.getX();
		int y = point.getY();
		
		Point north = x - 1 >= 0 && mappa.get(x - 1, y) == 0 ? new Point(x - 1, y) : null;
		Point south = x + 1 <= mappa.getXSize() && mappa.get(x + 1, y) == 0 ? new Point(x + 1, y) : null;
		Point east = y + 1 <= mappa.getYSize() && mappa.get(x, y + 1) == 0 ? new Point(x, y + 1) : null;
		Point west = y - 1 >= 0 && mappa.get(x, y - 1) == 0 ? new Point(x, y - 1) : null;
		
		if (north != null)
			set.add(north);
		if (south != null)
			set.add(south);
		if (east != null)
			set.add(east);
		if (west != null)
			set.add(west);
		
		return set;
	}

	private ArrayList<Point> reconstruct_path(Map<Point, Point> came_from, Point current_node)
	{
		ArrayList<Point> path = new ArrayList<>();
		
		if (came_from.get(current_node) != null)
		{
			ArrayList<Point> p = reconstruct_path(came_from, came_from.get(current_node));
			System.out.println(current_node);
        	p.addAll(path);
        	return p;
		}

		return path;
	}
	
	private float heuristic_estimate_of_distance(Point point, Point otherPoint)
	{
		double xDiff = point.getX() - otherPoint.getX();
		double yDiff = point.getY() - otherPoint.getY();
		
		xDiff = Math.pow(xDiff, 2);
		yDiff = Math.pow(yDiff, 2);
		
		return (float) (Math.sqrt(xDiff + yDiff));
	}
	
	
	public ArrayList<Integer> getPath(int xDestination, int yDestination)
	{
	    ArrayList<Point> viewedCells = new ArrayList<>();
	    ArrayList<Point> pathList = new ArrayList<>();

	    Point nextPosition = new Point(-1, -1);
	    Point currentPosition = new Point(robotPosition);
	    pathList.add(robotPosition);
	    
	    int count = 0;
	    int times = 0;
	    
	    while (nextPosition.getX() != xDestination || nextPosition.getY() != yDestination)
	    {
	        nextPosition = findAdiacentEmptyCell(currentPosition, viewedCells);
	        
	        if (nextPosition.getX() != -1 && nextPosition.getY() != -1)
	        {
	        	pathList.add(nextPosition);
	        	viewedCells.add(nextPosition);
	        	currentPosition = new Point(nextPosition);
	        	count = 0;
	        	times = 0;
	        }
	        else
	        {
	            if (++count == 4)
	            {
	                pathList.remove(pathList.size() - 1);
	                currentPosition = new Point(pathList.get(pathList.size() - 1));
	                count = 0;
	            }
	        }
	        
	        if (++times == 100)
	        	break;
	    }
	    
	    ArrayList<Integer> path = new ArrayList<>();

	    Point pos1 = pathList.get(0);
	    for (int i = 1; i < pathList.size(); ++i)
	    {
	        Point pos2 = pathList.get(i);

	        if (pos2.getX() == (pos1.getX() + 1))
	            path.add(SUD);
	        else if (pos2.getX() == (pos1.getX() - 1))
	        	path.add(NORD);
	        else if (pos2.getY() == (pos1.getY() - 1))
	        	path.add(OVEST);
	        else if (pos2.getY() == (pos1.getY() + 1))
	        	path.add(EST);
	        
	        pos1 = new Point(pos2);
	    }

	    path.add(-1);

	    return path;
	}
	
	public Point findAdiacentEmptyCell(Point currentPosition, ArrayList<Point> viewedCells)
	{
	    Point nextPosition = new Point(currentPosition);
	    nextPosition.setX(nextPosition.getX() + 1);

	    if (nextPosition.getX() < mappa.getXSize() &&
	        mappa.get(nextPosition.getX(), nextPosition.getY()) == 0 &&
	        !contains(nextPosition, viewedCells))
	        return nextPosition;
	    
	    nextPosition.setX(nextPosition.getX() - 2);
	    if (nextPosition.getX() >= 0 &&
	        mappa.get(nextPosition.getX(), nextPosition.getY()) == 0 &&
	        !contains(nextPosition, viewedCells))
	        return nextPosition;

	    nextPosition.setX(nextPosition.getX() + 1);
	    nextPosition.setY(nextPosition.getY() + 1);
	    if (nextPosition.getY() < mappa.getYSize() &&
	    	mappa.get(nextPosition.getX(), nextPosition.getY()) == 0 &&
	    	!contains(nextPosition, viewedCells))
	        return nextPosition;

	    nextPosition.setY(nextPosition.getY() - 2);
	    if (nextPosition.getY() >= 0 &&
    		mappa.get(nextPosition.getX(), nextPosition.getY()) == 0 &&
    		!contains(nextPosition, viewedCells))
	        return nextPosition;

	    nextPosition = new Point(-1, -1);

	    return nextPosition;
	}
	
	private boolean contains(Point element, ArrayList<Point>list)
	{
		for (int i = 0; i < list.size(); ++i)
			if (list.get(i).getX() == element.getX() && list.get(i).getY() == element.getY())
				return true;
		
		return false;
	}
}
