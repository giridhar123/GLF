package Map;

public class Point {

	private int x;
	private int y;
	
	public Point(int x, int y) 
	{
		this.x = x;
		this.y = y;
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
	
	public void print() 
	{
		System.out.println("x = "+ x +"; y = "+ y);
	}
}
