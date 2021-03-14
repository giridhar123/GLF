package Network.Packets.ClientToServer;

import java.nio.ByteBuffer;

import Network.Packet;

public class CTS_WORLD_READY extends Packet {
	
	private static final int size = 0;
	
	public CTS_WORLD_READY(Packet packet, ByteBuffer buf) {
    	super(packet);
    }

    public CTS_WORLD_READY() {
        super(size, Packet.CTS_WORLD_READY);
    }
    
    @Override
    public ByteBuffer encode()
    {
    	ByteBuffer buf = ByteBuffer.allocate(getSize());
    	buf.putInt(getSize());
    	buf.putShort(this.getOpcode());
        buf.position(0);
        return buf;
    }

}