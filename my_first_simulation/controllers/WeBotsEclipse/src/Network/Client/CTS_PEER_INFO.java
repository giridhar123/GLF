package Network.Client;
import java.nio.ByteBuffer;

import Network.Packet;

public class CTS_PEER_INFO extends Packet 
{
	private static final int size = 1;
	
    private byte type;
    
    public static final byte GUARDIA = 1;
    public static final byte LADRO = 2;
    public static final byte SUPERVISOR = 3;
    
    public CTS_PEER_INFO(Packet packet, ByteBuffer buf) {
    	super(packet);
    	this.type = buf.get();
    }

    public CTS_PEER_INFO(byte type) {
        super(size, Packet.CTS_PEER_INFO);
        this.type = type;
    }

    public byte getType() {
        return type;
    }

    public void setType(byte type) {
        this.type = type;
    }

    public String toString() {
        String toString = "toString";
        
        return toString;
    }
        
    @Override
    public ByteBuffer encode()
    {
    	ByteBuffer buf = ByteBuffer.allocate(getSize());
    	buf.putInt(getSize());
    	buf.putShort(this.getOpcode());
    	buf.put(this.type);
        buf.position(0);
        return buf;
    }
}