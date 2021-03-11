package Controllers;

import java.util.ArrayList;

import General.SharedVariables;
import Map.Point;
import Network.ClientConnectionHandler;
import Network.Packets.ClientToServer.CTS_PEER_INFO;
import Robot.GenericRobot;
import Robot.LadroRobot;

public class LadroController
{
	
    public static void main(String[] args) throws Exception
    {
        LadroRobot robot = new LadroRobot(GenericRobot.SUD);
        
        //ArrayList<Point> pts = potentialShelters(map);  pts contiene i possibili nascondigli
        //estrai un punto da pts
        

        while (true)
        {
        	robot.myStep(SharedVariables.TIME_STEP, null);
        }
    }
    
    public static ArrayList<Point> potentialShelters(int[][] map)
	{
		ArrayList<Point> pts= new ArrayList<Point>();
		
		for(int x=0; x<20; ++x)
		{
			for(int y=0; y<20; ++y)
			{
				if(map[x][y]==0) 
				{
					if(check(map, x, y)) pts.add(new Point(x,y));
				}
			}
		}
		
		return pts;
	}
    
    public static Boolean check(int[][] map,int x,int y) 
	{
		int north, south, east, west;
		
		int mapDim = map[0].length;
		
		north = x - 1 >= 0 ? map[x-1][y] : 0;
		south = x + 1 < mapDim ? map[x+1][y] : 0;
		east = y + 1 < mapDim ? map[x][y+1] : 0;
		west = y - 1 >= 0 ? map[x][y-1] : 0;
		
		if((north + south + east + west) == 3) return true;
		else return false;
	}
}
