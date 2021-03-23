package Network.Packets.ClientToServer;

import java.nio.ByteBuffer;

import Map.Point;
import Network.Packet;

public class CTS_MAP_POINT extends Packet
{    
	private int x, y;
	
    public CTS_MAP_POINT(Packet packet, ByteBuffer buf) {
    	super(packet);
    	//System.out.println("ZZZ: " + getSize());
    	this.x = buf.getInt();
    	this.y = buf.getInt();
    }
    
    public CTS_MAP_POINT(Point point)
    {
    	super(4 + 4, Packet.CTS_MAP_POINT);
    	this.x = point.getX();
    	this.y = point.getY();
    }

    public CTS_MAP_POINT(int x, int y) {
    	//sizeof 2 * int (4 byte)
        super(4 + 4, Packet.CTS_MAP_POINT);
        this.x = x;
        this.y = y;
    }

    public int getX() {
        return x;
    }
    
    public int getY()
    {
    	return y;
    }
        
    @Override
    public ByteBuffer encode()
    {
    	ByteBuffer buf = ByteBuffer.allocate(getSize());
    	buf.putInt(getSize());
    	buf.putShort(this.getOpcode());
    	buf.putInt(x);
    	buf.putInt(y);
        buf.position(0);
        return buf;
    }
}
