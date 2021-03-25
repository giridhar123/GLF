package Network.Packets.ClientToServer;

import java.nio.ByteBuffer;

import Map.Point;
import Network.Packet;

public class CTS_GOAL_CHANGED extends Packet
{
	private Point oldG, newG;
	
    public CTS_GOAL_CHANGED(Packet packet, ByteBuffer buf) {
    	super(packet);
    	//System.out.println("ZZZ: " + getSize());
    	int x1 = buf.getInt();
    	int y1 = buf.getInt();
    	int x2 = buf.getInt();
    	int y2 = buf.getInt();
    	
    	this.oldG = new Point(x1, y1);
    	this.newG = new Point(x2, y2);
    }
    
    public CTS_GOAL_CHANGED(Point oldG, Point newG)
    {
    	super(4 + 4 + 4 + 4, Packet.CTS_GOAL_CHANGED);
    	this.oldG = new Point(oldG);
    	this.newG = new Point(newG);
    }

    public CTS_GOAL_CHANGED(int xb, int yb, int xa, int ya) {
    	//sizeof 2 * int (4 byte)
        super(4 + 4, Packet.CTS_GOAL_CHANGED);
        this.oldG = new Point(xb, yb);
        this.newG = new Point(xa, ya);
    }

    public Point getOld()
    {
        return oldG;
    }
    
    public Point getNew()
    {
    	return newG;
    }
    
    @Override
    public ByteBuffer encode()
    {
    	ByteBuffer buf = ByteBuffer.allocate(getSize());
    	buf.putInt(getSize());
    	buf.putShort(this.getOpcode());
    	buf.putInt(oldG.getX());
    	buf.putInt(oldG.getY());
    	buf.putInt(newG.getX());
    	buf.putInt(newG.getY());
        buf.position(0);
        return buf;
    }
}
