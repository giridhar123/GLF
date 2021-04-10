package Network.Packets.ClientToServer;

import java.nio.ByteBuffer;

import Map.Point;
import Network.Packets.Packet;

/*
 * Pacchetto inviato da una guardia quando trova un nuovo ostacolo
 */

public class CTS_OBSTACLE_IN_MAP extends Packet
{    
	private Point point;
	
    public CTS_OBSTACLE_IN_MAP(Packet packet, ByteBuffer buf) {
    	super(packet);
    	int x = buf.getInt();
    	int y = buf.getInt();
    	
    	this.point = new Point(x, y);
    }
    
    public CTS_OBSTACLE_IN_MAP(Point point)
    {
    	super(4 + 4, Packet.CTS_OBSTACLE_IN_MAP);
    	this.point = new Point(point);
    }

    public CTS_OBSTACLE_IN_MAP(int x, int y) {
    	//sizeof 2 * int (4 byte)
        super(4 + 4, Packet.CTS_OBSTACLE_IN_MAP);
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
