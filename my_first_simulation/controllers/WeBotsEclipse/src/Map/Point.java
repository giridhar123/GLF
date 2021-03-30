package Map;

public class Point {

	private int x;
	private int y;
	
	public Point()
	{
		this.x = 0;
		this.y = 0;
	}
	
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
		if (o == null)
			return false;
		
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
	
	@Override
	public int hashCode() {
	    StringBuilder hash = new StringBuilder();
	    hash.append(x).append(y);
	    return Integer.valueOf(hash.toString());
	}
	
	public void print() 
	{
		System.out.println("x = "+ x +"; y = "+ y);
	}
}
