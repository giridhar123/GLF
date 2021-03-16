package General;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import Map.Mappa;
import Map.Point;
import Robot.GenericRobot;

public class AStarSearcher {
	
	private Mappa mappa;
	
	public AStarSearcher(Mappa mappa)
	{
		this.mappa = mappa;
	}

	public ArrayList<Point> getPath(Point from, Point goal)
	{
		if (mappa == null)
			return null;
		
		Point start = new Point(from);
		Set<Point> closedset = new HashSet<>();			//The empty set % The set of nodes already evaluated.     
		Set<Point> openset = new HashSet<>();			//Set containing the initial node % The set of tentative nodes to be evaluated.
		openset.add(start);
		
		Map<Point, Float> g_score = new HashMap<>();	//Distance from start along optimal path.
		g_score.put(start, (float) 0);
		
		Map<Point, Point> came_from = new HashMap<>(); 	//The empty map % The map of navigated nodes.
		Map<Point, Float> h_score = new HashMap<>();
		float value = heuristic_estimate_of_distance(start, goal);
		h_score.put(start, value);
		
		Map<Point, Float> f_score = new HashMap<>();
		f_score.put(start, value);						//Estimated total distance from start to goal through y.
				
		while (!openset.isEmpty())
		{
			Point x = getHelp(openset, f_score); 		//The node in openset having the lowest f_score[] value
						
			if (x.equals(goal))
			{
				ArrayList<Point> path = reconstruct_path(came_from, goal);
				path.add(0, new Point (start));
				return path;
			}
			
         	openset.remove(x);
         	closedset.add(new Point(x));
         	
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
         			openset.add(new Point(y));
         			tentative_is_better = true;
         		}
         		else if (tentative_g_score < g_score.get(y))
         			tentative_is_better = true;

		        if (tentative_is_better)
		        {
		        	came_from.put(y, x);
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
		Point south = x + 1 < mappa.getXSize() && mappa.get(x + 1, y) == 0 ? new Point(x + 1, y) : null;
		Point east = y + 1 < mappa.getYSize() && mappa.get(x, y + 1) == 0 ? new Point(x, y + 1) : null;
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
		if (came_from.get(current_node) != null)
		{
			ArrayList<Point> p = reconstruct_path(came_from, came_from.get(current_node));
			p.add(current_node);
        	return p;
		}	

		return (new ArrayList<Point>());
	}
	
	private float heuristic_estimate_of_distance(Point point, Point otherPoint)
	{
		double xDiff = point.getX() - otherPoint.getX();
		double yDiff = point.getY() - otherPoint.getY();
		
		xDiff = Math.pow(xDiff, 2);
		yDiff = Math.pow(yDiff, 2);
		
		return (float) (Math.sqrt(xDiff + yDiff));
	}
	
	public static ArrayList<Integer> pathToRobotDirections(ArrayList<Point> path)
	{
		ArrayList<Integer> newPath = new ArrayList<>();
		
		Point pos1 = path.get(0);
		Point pos2;
		for (int i = 1; i < path.size(); ++i)
		{
			pos2 = path.get(i);
			
			if (pos2.getX() < pos1.getX())
				newPath.add(GenericRobot.NORD);
			else if (pos2.getX() > pos1.getX())
				newPath.add(GenericRobot.SUD);
			else if (pos2.getY() > pos1.getY())
				newPath.add(GenericRobot.EST);
			else if (pos2.getY() < pos1.getY())
				newPath.add(GenericRobot.OVEST);
			
			pos1 = pos2;
		}
		
		return newPath;
	}
}
