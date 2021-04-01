package Network.Packets.ClientToServer;

import java.nio.ByteBuffer;

import Map.Point;
import Network.Packets.Packet;

public class CTS_NEW_GUARDIA_POS extends Packet
{
	private Point before, after;
	
    public CTS_NEW_GUARDIA_POS(Packet packet, ByteBuffer buf) {
    	super(packet);
    	//System.out.println("ZZZ: " + getSize());
    	int x1 = buf.getInt();
    	int y1 = buf.getInt();
    	int x2 = buf.getInt();
    	int y2 = buf.getInt();
    	
    	this.before = new Point(x1, y1);
    	this.after = new Point(x2, y2);
    }
    
    public CTS_NEW_GUARDIA_POS(Point before, Point after)
    {
    	super(4 + 4 + 4 + 4, Packet.CTS_NEW_GUARDIA_POS);
    	this.before = new Point(before);
    	this.after = new Point(after);
    }

    public CTS_NEW_GUARDIA_POS(int xb, int yb, int xa, int ya) {
    	//sizeof 2 * int (4 byte)
        super(4 + 4, Packet.CTS_NEW_GUARDIA_POS);
        this.before = new Point(xb, yb);
        this.after = new Point(xa, ya);
    }

    public Point getBefore()
    {
        return before;
    }
    
    public Point getAfter()
    {
    	return after;
    }
    
    @Override
    public ByteBuffer encode()
    {
    	ByteBuffer buf = ByteBuffer.allocate(getSize());
    	buf.putInt(getSize());
    	buf.putShort(this.getOpcode());
    	buf.putInt(before.getX());
    	buf.putInt(before.getY());
    	buf.putInt(after.getX());
    	buf.putInt(after.getY());
        buf.position(0);
        return buf;
    }
}
