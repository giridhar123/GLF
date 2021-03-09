package Map;

public class Point {

	private int x;
	private int y;
	
	public Point(int x, int y) 
	{
		this.x = x;
		this.y = y;
	}
	
	public Point(Point otherPoint)
	{
		this.x = otherPoint.x;
		this.y = otherPoint.y;
	}
	
	@Override
	public boolean equals(Object o)
	{		
		Point otherPoint = (Point) o;
		
		return (this.x == otherPoint.x && this.y == otherPoint.y);
	}
	
	public int getX()
	{
		return x;
	}
	
	public int getY()
	{
		return y;
	}
	
	public void setX(int n)
	{
		this.x = n;
	}
	
	public void setY(int n)
	{
		this.y = n;
	}
	
	@Override
	public String toString()
	{		
		return new String(this.x + " " + this.y);
	}
	
	public void print() 
	{
		System.out.println("x = "+ x +"; y = "+ y);
	}
}
