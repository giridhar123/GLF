package General;

import Map.Point;

/** Elemento.java */
public enum StartPositions
{
	Guardia0(new Point(1, 1)),
	Guardia1(new Point(1, 2)),
	Guardia2(new Point(1, 3)),
	Guardia3(new Point(1, 4)),
	Guardia4(new Point(1, 5)),
	Guardia5(new Point(1, 6)),
	Guardia6(new Point(1, 7)),
	Guardia7(new Point(1, 8)),
	Guardia8(new Point(1, 9)),
	Guardia9(new Point(1, 10)),
	
	Ladro0(new Point(18, 18)),
	Ladro1(new Point(18, 17)),
	Ladro2(new Point(18, 16)),
	Ladro3(new Point(18, 15)),
	Ladro4(new Point(18, 14)),
	Ladro5(new Point(18, 13)),
	Ladro6(new Point(18, 12)),
	Ladro7(new Point(18, 11)),
	Ladro8(new Point(18, 10)),
	Ladro9(new Point(18, 9));
	
	private Point position;
	
	public Point getPosition()
	{
		return position;
	}
	
	private StartPositions(Point position)
	{
		this.position = position;
	}
}