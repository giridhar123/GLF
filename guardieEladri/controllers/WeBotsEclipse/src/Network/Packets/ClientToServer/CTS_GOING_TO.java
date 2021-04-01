package Network.Packets.ClientToServer;

import java.nio.ByteBuffer;

import Map.Point;
import Network.Packets.Packet;

public class CTS_GOING_TO extends Packet
{    
	private Point point;
	
    public CTS_GOING_TO(Packet packet, ByteBuffer buf) {
    	super(packet);
    	//System.out.println("ZZZ: " + getSize());
    	int x = buf.getInt();
    	int y = buf.getInt();
    	
    	this.point = new Point(x, y);
    }
    
    public CTS_GOING_TO(Point point)
    {
    	super(4 + 4, Packet.CTS_GOING_TO);
    	this.point = new Point(point);
    }

    public CTS_GOING_TO(int x, int y) {
    	//sizeof 2 * int (4 byte)
        super(4 + 4, Packet.CTS_GOING_TO);
        this.point = new Point(x, y);
    }

    public Point getPoint()
    {
        return point;
    }
    
    @Override
    public ByteBuffer encode()
    {
    	ByteBuffer buf = ByteBuffer.allocate(getSize());
    	buf.putInt(getSize());
    	buf.putShort(this.getOpcode());
    	buf.putInt(point.getX());
    	buf.putInt(point.getY());
        buf.position(0);
        return buf;
    }
}
