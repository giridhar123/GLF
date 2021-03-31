package Map;

/** Elemento.java */
public enum StartPositions
{
	Guardia0(new Point(1, 9)),
	Guardia1(new Point(1, 13)),
	Guardia2(new Point(1, 6)),
	Guardia3(new Point(1, 4)),
	Guardia4(new Point(1, 5)),
	Guardia5(new Point(1, 6)),
	Guardia6(new Point(1, 7)),
	Guardia7(new Point(1, 8)),
	Guardia8(new Point(1, 9)),
	Guardia9(new Point(1, 10)),
	
	Ladro0(new Point(26, 9)),
	Ladro1(new Point(26, 13)),
	Ladro2(new Point(26, 6)),
	Ladro3(new Point(26, 4)),
	Ladro4(new Point(27, 5)),
	Ladro5(new Point(27, 6)),
	Ladro6(new Point(27, 7)),
	Ladro7(new Point(27, 8)),
	Ladro8(new Point(27, 9)),
	Ladro9(new Point(27, 10));
	
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